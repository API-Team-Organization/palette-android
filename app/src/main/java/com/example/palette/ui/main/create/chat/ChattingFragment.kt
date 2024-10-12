package com.example.palette.ui.main.create.chat

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
            // UI 스레드에서 안전하게 업데이트
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
                        ChatRequestManager.getChatList( // getChatList failed -> return EmptyList
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

            val lastMessage = chatList.last() // no? sad...
            if (lastMessage.promptId != null) {
                val qna = qnaList.find { it.id == lastMessage.promptId }!!

                with(binding) {
                    chattingEditText.visibility = if (qna.type == PromptType.USER_INPUT) View.VISIBLE else View.GONE
                    // selectable, grid 넣기.
                }
            }
        }

        binding.chattingToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack() // 백 스택에서 프래그먼트 제거
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
            sendMessage(roomId, chat.message) // Retrofit으로 채팅 메시지 전송
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

//        val newReceived = chatMessage.message
        // action에 따라 처리
//        when (action) {
//            "START" -> {}
//            "TEXT" -> {
//                log("TEXT 리턴 값입니다!")
//                chatList[chatList.size - 2] = newReceived!! // must provided by server
//                recyclerAdapter.setData(chatList)
//            }
//
//            "IMAGE" -> {
//                log("IMAGE 리턴 값입니다!")
//                chatList[chatList.size - 1] = newReceived!!
//                recyclerAdapter.setData(chatList)
//            }
//
//            "END" -> {
//                if (chatMessage.data.message == null) return
//                shortToast(chatMessage.data.message.message)
//                for (i in 0..<2) {
//                    chatList.removeAt(chatList.size - 2 + i)
//                }
//                recyclerAdapter.setData(chatList)
//            }
//
//            else -> {
//                logE("알 수 없는 action: $action")
//            }
//        }

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