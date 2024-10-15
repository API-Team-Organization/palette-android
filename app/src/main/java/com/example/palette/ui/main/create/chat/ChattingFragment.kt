package com.example.palette.ui.main.create.chat

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.data.chat.ChatRequestManager
import com.example.palette.data.chat.qna.ChatAnswer
import com.example.palette.data.chat.qna.ChatQuestion
import com.example.palette.data.chat.qna.PromptData
import com.example.palette.data.error.CustomException
import com.example.palette.data.room.RoomRequestManager
import com.example.palette.data.room.data.RoomData
import com.example.palette.data.socket.BaseResponseMessage
import com.example.palette.data.socket.MessageResponse
import com.example.palette.data.socket.WebSocketManager
import com.example.palette.databinding.FragmentChattingBinding
import com.example.palette.ui.base.BaseControllable
import com.example.palette.ui.main.create.chat.adapter.ChattingRecyclerAdapter
import com.example.palette.ui.util.log
import com.example.palette.ui.util.logE
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChattingFragment(
    private val roomId: Int,
    private val title: String,
    private val isFirst: Boolean = false
) : Fragment() {
    private lateinit var binding: FragmentChattingBinding
    private val recyclerAdapter: ChattingRecyclerAdapter by lazy {
        ChattingRecyclerAdapter()
    }
    private var chatList: MutableList<MessageResponse> = mutableListOf()
    private var qnaList: MutableList<PromptData> = mutableListOf()
    private var isLoading = false
    private lateinit var webSocketManager: WebSocketManager
    private lateinit var sendData: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var firstMsgReceived = false
        binding = FragmentChattingBinding.inflate(inflater, container, false)

        // WebSocket 연결
        try {
            webSocketManager = WebSocketManager(PaletteApplication.prefs.token, roomId)
            if (isFirst) {
                val connection = System.currentTimeMillis()
                webSocketManager.setOnConnect {
                    viewLifecycleOwner.lifecycleScope.launch {
                        val m = async {
                            while (isFirst && !firstMsgReceived && chatList.isEmpty()) {
                                if (System.currentTimeMillis() - connection > 3000) {
                                    delay(500L)
                                    chatList.clear()
                                    loadChatData()
                                }
                            }
                        }
                        m.join()

                        recyclerAdapter.setData(chatList)
                    }
                }
            }
            webSocketManager.setOnMessageReceivedListener { chatMessage ->
                // UI 스레드에서 안전하게 업데이트
                log("ChattingFragment onCreateView handleChatMessage 호출됨")
                firstMsgReceived = true
                viewLifecycleOwner.lifecycleScope.launch {
                    handleChatMessage(chatMessage)
                }
            }
            webSocketManager.start()
        } catch (e: Exception) {
            logE("WebSocketManager 생성 중 오류 발생: ${e.localizedMessage}")
        }

        initView()
        initEditText()
        return binding.root
    }

    private fun initView() {
        binding.chattingToolbar.title = title

        loadQnaData()

        // 백 스택에서 프래그먼트 제거
        binding.chattingToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.chattingToolbar.setOnClickListener { showChangeTitleDialog() }

        binding.chattingSubmitButton.setOnClickListener {
            // TODO: Selectable 만 되게 임시로 박아둠
            val chat = ChatAnswer.SelectableAnswer(
                choiceId = sendData,
                type = "SELECTABLE"
            )

            if (sendData.isEmpty()) return@setOnClickListener

            viewLifecycleOwner.lifecycleScope.launch {
                sendMessage(roomId, chat) // Retrofit으로 채팅 메시지 전송
                binding.chattingEditText.text.clear()
            }
        }

        binding.chattingRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                // 최상단에 도달하고 데이터 로드 중이 아니며, 데이터를 모두 로드하지 않았다면
                if (binding.chattingRecycler.canScrollVertically(-1)) return
                if (isLoading) return
                if (chatList.isEmpty()) return

                isLoading = true // 로딩 시작 플래그 설정

                log("chatList : $chatList")
                val firstMessageTime = chatList[0].datetime
                log("firstMessageTiem : $firstMessageTime")
                loadMoreChats(firstMessageTime.toString())
            }
        })

        binding.chattingRecycler.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        }
        (requireActivity() as? BaseControllable)?.bottomVisible(false)
    }

    private suspend fun loadChatData() {
        try {
            val it = ChatRequestManager.getChatList( // getChatList failed -> return EmptyList
                token = PaletteApplication.prefs.token,
                roomId = roomId
            )?.data ?: emptyList()

            chatList.addAll((it).reversed())
        } catch (e: CustomException) {
            shortToast(e.errorResponse.message)
        }
    }

    private fun loadQnaData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val qnaLoader = async {
                val qna = try {
                    ChatRequestManager.getQnAList(PaletteApplication.prefs.token, roomId)
                } catch (e: CustomException) {
                    shortToast(e.message!!)
                    null
                }
                qnaList.addAll(qna?.data ?: emptyList())
            }

            val chatLoader = async {
                loadChatData()
            }

            listOf(qnaLoader, chatLoader).awaitAll()

            recyclerAdapter.setQnAList(qnaList)
            recyclerAdapter.setData(chatList)
            binding.chattingRecycler.scrollToPosition(chatList.size - 1)

            log("ChattingFragment initView \nqnaList: $qnaList\nchatList: $chatList")

            val qna: PromptData?

            if (chatList.isEmpty()) {
                qna = qnaList[0]
            } else {
                val lastMessage = chatList.last()
                qna = qnaList.find { it.id == lastMessage.promptId }!!
            }

            when (qna) {
                is PromptData.Selectable -> {
                    val selectableQuestion = qna.question as? ChatQuestion.SelectableQuestion
                    with(binding) {
                        chattingSelectLayout.visibility = View.VISIBLE
                        chattingSelectLayout.removeAllViews()
                        selectableQuestion?.choices?.forEach { choice ->
                            val button = Button(context).apply {
                                text = choice.displayName
                                background = ContextCompat.getDrawable(context, R.drawable.bac_auth)
                                elevation = 0f
                                val layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    setMargins(8, 0, 8, 0)
                                }
                                this.layoutParams = layoutParams
                                setOnClickListener {
                                    binding.chattingEditText.setText(choice.displayName)
                                    sendData = choice.id
                                }
                            }
                            chattingSelectLayout.addView(button)
                        }
                    }
                }

                is PromptData.Grid -> {}

                is PromptData.UserInput -> {}
            }
        }
    }

    private fun loadMoreChats(before: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val newChats = ChatRequestManager.getChatList(
                token = PaletteApplication.prefs.token,
                roomId = roomId,
                before = before,
            )?.data

            if (newChats.isNullOrEmpty()) {
                shortToast("더 이상 불러올 채팅이 없습니다.")
            } else {
                newChats.reverse()
                chatList.addAll(0, newChats)
                recyclerAdapter.setData(chatList)
            }

            isLoading = false // 로딩 종료 플래그 설정
        }
    }

    private fun initEditText() {
        binding.chattingEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // EditText 내용이 변경된 후 호출됩니다.
                if (s.isNullOrBlank()) {
                    binding.chattingSubmitButton.setImageResource(R.drawable.ic_arrow_upward_gray)
                } else {
                    binding.chattingSubmitButton.setImageResource(R.drawable.ic_arrow_upward)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private suspend fun sendMessage(roomId: Int, data: ChatAnswer) {
        val chat: ChatAnswer

        when(data) {
            is ChatAnswer.GridAnswer -> {
                chat = ChatAnswer.GridAnswer(
                    choiceId = data.choiceId,
                    type = "GRID"
                )
            }
            is ChatAnswer.SelectableAnswer -> {
                chat = ChatAnswer.SelectableAnswer(
                    choiceId = data.choiceId,
                    type = "SELECTABLE"
                )
            }
            is ChatAnswer.UserInputAnswer -> {
                chat = ChatAnswer.UserInputAnswer(
                    choiceId = data.choiceId,
                    type = "USER_INPUT"
                )
            }
        }

        ChatRequestManager.createChat(
            PaletteApplication.prefs.token,
            roomId = roomId,
            chat = chat
        )
    }

    private fun handleChatMessage(chatMessage: BaseResponseMessage.ChatMessage) {
        chatList.add(
            MessageResponse(
                id = chatMessage.id,
                promptId = chatMessage.promptId,
                message = chatMessage.message,
                roomId = chatMessage.roomId,
                userId = chatMessage.userId,
                datetime = chatMessage.datetime,
                resource = chatMessage.resource,
                isAi = chatMessage.isAi
            )
        )
        recyclerAdapter.setData(chatList)

        if (chatList.isEmpty()) return
        binding.chattingRecycler.smoothScrollToPosition(recyclerAdapter.itemCount - 1)
    }

    private fun showChangeTitleDialog() {
        val builder = AlertDialog.Builder(requireContext())

        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_edit_text, null)

        val input = dialogView.findViewById<EditText>(R.id.etChangeTitle)
        val applyButton = dialogView.findViewById<TextView>(R.id.tv_apply)

        input.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                input.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
            } else {
                input.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.black)
            }
        }

        builder.setView(dialogView)

        val dialog = builder.create()

        applyButton.setOnClickListener {
            val newTitle = input.text.toString()

            if (newTitle.isBlank()) {
                shortToast("제목을 입력해주세요")
                return@setOnClickListener
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    RoomRequestManager.setRoomTitle(
                        PaletteApplication.prefs.token,
                        RoomData(roomId, newTitle)
                    )
                    binding.chattingToolbar.title = newTitle
                }
                dialog.dismiss()
            }

        }
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webSocketManager.stop() // 프래그먼트 종료 시 WebSocket 연결 해제

        if (chatList.isEmpty()) {
            (requireActivity() as? BaseControllable)?.deleteRoom(roomId = roomId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as? BaseControllable)?.bottomVisible(true)
    }
}