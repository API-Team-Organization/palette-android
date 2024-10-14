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
import com.example.palette.data.chat.ChatData
import com.example.palette.data.chat.ChatRequestManager
import com.example.palette.data.chat.qna.ChatQuestion
import com.example.palette.data.chat.qna.PromptData
import com.example.palette.data.error.CustomException
import com.example.palette.data.room.RoomRequestManager
import com.example.palette.data.room.data.RoomData
import com.example.palette.data.socket.BaseResponseMessage
import com.example.palette.data.socket.ChatResource
import com.example.palette.data.socket.MessageResponse
import com.example.palette.data.socket.PromptType
import com.example.palette.data.socket.WebSocketManager
import com.example.palette.databinding.FragmentChattingBinding
import com.example.palette.ui.base.BaseControllable
import com.example.palette.ui.main.create.chat.adapter.ChattingRecyclerAdapter
import com.example.palette.ui.util.log
import com.example.palette.ui.util.logE
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class ChattingFragment(
    private val roomId: Int,
    private val title: String
) : Fragment() {
    private lateinit var binding: FragmentChattingBinding
    private val recyclerAdapter: ChattingRecyclerAdapter by lazy {
        ChattingRecyclerAdapter()
    }
    private var chatList: MutableList<MessageResponse> = mutableListOf()
    private var qnaList: MutableList<PromptData> = mutableListOf()
    private var isLoading = false
    private lateinit var webSocketManager: WebSocketManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChattingBinding.inflate(inflater, container, false)

        // WebSocket 연결
        try {
            webSocketManager = WebSocketManager(PaletteApplication.prefs.token, roomId)
        } catch (e: Exception) {
            logE("WebSocketManager 생성 중 오류 발생: ${e.localizedMessage}")
        }
        webSocketManager.setOnMessageReceivedListener { chatMessage ->
            viewLifecycleOwner.lifecycleScope.launch {
                log("ChattingFragment onCreateView handleChatMessage 호출됨")
                handleChatMessage(chatMessage)
            }
        }
        webSocketManager.start()

        initView()
        initEditText()
        return binding.root
    }

    private fun initView() {
        binding.chattingToolbar.title = title

        viewLifecycleOwner.lifecycleScope.launch {
            val qnaLoader = async {
                val qna = try {
                    ChatRequestManager.getQnAList(PaletteApplication.prefs.token, roomId)
                } catch (e: CustomException) {
                    shortToast(e.message!!)
                    null
                } ?: return@async
                qnaList.addAll(qna.data)
                recyclerAdapter.setQnAList(qnaList)
            }

            val chatLoader = async {
                try {
                    val chats =
                        ChatRequestManager.getChatList(
                            token = PaletteApplication.prefs.token,
                            roomId = roomId
                        )!!.data

                    chatList.addAll(chats.reversed())
                    recyclerAdapter.setData(chatList)
                    binding.chattingRecycler.scrollToPosition(chatList.size - 1)
                } catch (e: CustomException) {
                    shortToast(e.errorResponse.message)
                }
            }
            listOf(qnaLoader, chatLoader).awaitAll()

            val lastMessage = chatList.lastOrNull()
            if (lastMessage?.promptId != null) {
                val qna = qnaList.find { it.id == lastMessage.promptId }!!

                if (qna is PromptData.Selectable) {
                    val selectableQuestion = qna.question as? ChatQuestion.SelectableQuestion

                    with(binding) {
//                        chattingEditText.visibility = View.GONE // 입력 필드 숨김
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
                                }
                            }
                            chattingSelectLayout.addView(button)
                        }
                    }
                }
            }
        }

        binding.chattingToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.chattingToolbar.setOnClickListener {
            showChangeTitleDialog()
        }

        binding.chattingSubmitButton.setOnClickListener {
            val newMessage = binding.chattingEditText.text.toString()
            val chat = ChatData(newMessage)

            if (newMessage.isNotBlank()) {
                submitText(chat)
            }
        }

        binding.chattingRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (binding.chattingRecycler.canScrollVertically(-1)) return
                if (isLoading) return
                if (chatList.isEmpty()) return

                isLoading = true
                log("chatList : $chatList")
                val firstMessageTime = chatList[0].datetime
                log("firstMessageTime : $firstMessageTime")
                loadMoreChats(firstMessageTime.toString())
            }
        })

        binding.chattingRecycler.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        (requireActivity() as? BaseControllable)?.bottomVisible(false)
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

            isLoading = false
        }
    }

    private fun initEditText() {
        binding.chattingEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
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

    private fun submitText(chat: ChatData) {
        viewLifecycleOwner.lifecycleScope.launch {
            if (chatList.size == 0) {
                log("ChattingFragment 첫 메세지를 제목으로 설정합니다 ${chat}")
                RoomRequestManager.setRoomTitle(
                    PaletteApplication.prefs.token,
                    RoomData(roomId, chat.message)
                )
                binding.chattingToolbar.title = chat.message
            }
            sendMessage(roomId, chat.message)
            binding.chattingEditText.text.clear()
        }
    }

    private suspend fun sendMessage(roomId: Int, message: String) {
        val chatRequest = ChatData(message = message)
        val response = ChatRequestManager.createChat(
            PaletteApplication.prefs.token,
            roomId = roomId,
            chat = chatRequest
        )

        if (response.isSuccessful) {
            val newReceived = MessageResponse(
                id = "",
                isAi = false,
                datetime = Clock.System.now(),
                message = message,
                roomId = roomId,
                userId = 0,
                resource = ChatResource.CHAT,
                promptId = null
            )
            chatList.add(newReceived)
            recyclerAdapter.addChat(newReceived)
            binding.chattingEditText.text.clear()

            // 플레이스홀더 아이템 두 개 추가
            val placeholder1 = MessageResponse(
                id = "",
                isAi = true,
                message = "로딩 중...",
                datetime = Clock.System.now(),
                roomId = roomId,
                userId = 0,
                resource = ChatResource.INTERNAL_CHAT_LOADING,
                promptId = null
            )
            val placeholder2 = placeholder1.copy(resource = ChatResource.INTERNAL_IMAGE_LOADING)

            chatList.add(placeholder1)
            chatList.add(placeholder2)
            recyclerAdapter.addChat(placeholder1)
            recyclerAdapter.addChat(placeholder2)

            binding.chattingRecycler.smoothScrollToPosition(recyclerAdapter.itemCount - 1)
            log("ChattingFragment sendMessage response.isSuccessful $response")
        } else {
            log("ChattingFragment sendMessage response.error $response")
        }
    }

    private fun handleChatMessage(chatMessage: BaseResponseMessage.ChatMessage) {
        log("Chatting handleChatMessage chatMessage is $chatMessage")

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
