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
import com.example.palette.data.chat.Received
import com.example.palette.data.room.RoomRequestManager
import com.example.palette.data.room.data.RoomData
import com.example.palette.data.socket.BaseResponseMessage
import com.example.palette.data.socket.WebSocketManager
import com.example.palette.databinding.FragmentChattingBinding
import com.example.palette.ui.base.BaseControllable
import com.example.palette.ui.main.create.chat.adapter.ChattingRecyclerAdapter
import com.example.palette.ui.util.log
import com.example.palette.ui.util.logE
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChattingFragment(
    private var roomId: Int,
    private var title: String
) : Fragment() {
    private lateinit var binding: FragmentChattingBinding
    private val recyclerAdapter: ChattingRecyclerAdapter by lazy {
        ChattingRecyclerAdapter()
    }
    private var chatList: MutableList<Received> = mutableListOf()
    private var isLoading = false
    private var loadPage = 0L
    private lateinit var webSocketManager: WebSocketManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChattingBinding.inflate(inflater, container, false)

        // WebSocket 연결
        webSocketManager = WebSocketManager(token = PaletteApplication.prefs.token, roomId)
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
            chatList = ChatRequestManager.getChatList(
                token = PaletteApplication.prefs.token,
                roomId = roomId
            )!!.data
            if (chatList.size != 0) {
                chatList.reverse()
                recyclerAdapter.setData(chatList)
                binding.chattingRecycler.scrollToPosition(chatList.size - 1)
            } else {
                log("ChattingFragment 리스트가 비어있습니다.")
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

        binding.chattingRecycler.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!binding.chattingRecycler.canScrollVertically(-1) && !isLoading) {
                    isLoading = true // 데이터를 로드 중으로 설정

                    val millis: Long? = stringToMillis(chatList[chatList.size - 1].datetime)
                    loadPage = millis ?: 0
                    viewLifecycleOwner.lifecycleScope.launch {
                        if (millis == null) return@launch
                        val temporaryList = ChatRequestManager.getChatList(
                            PaletteApplication.prefs.token,
                            roomId = roomId,
                            before = millis
                        )?.data

                        if (temporaryList.isNullOrEmpty()) {
                            shortToast("채팅 내역이 더 없습니다")
                        } else {
                            chatList.addAll(0, temporaryList)
                            recyclerAdapter.setData(chatList)
//                            binding.chattingRecycler.scrollToPosition(chatList.size - loadPage*10)
                        }

                        isLoading = false // 데이터 로드가 끝났으므로 플래그 초기화
                    }
                }
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

//
//            // 데이터 요청 및 처리
//            val response = ChatRequestManager.createChat(PaletteApplication.prefs.token, chat, roomId = roomId)
//            if (response.isSuccessful) {
//                val receivedData = response.body()!!.data.received
//                for (i in receivedData.indices) {
//                    chatList[chatList.size - 2 + i] = receivedData[i]
//                }
//                recyclerAdapter.setData(chatList)
//            } else {
//                shortToast("부적절한 단어 혹은 짧은 문장")
//                for (i in 0..< 2) {
//                    chatList.removeAt(chatList.size - 2 + i)
//                }
//                recyclerAdapter.setData(chatList)
//            }
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
            val newReceived = Received(
                id = -100,
                isAi = false,
                message = message,
                datetime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(
                    Date()
                ),
                roomId = roomId,
                userId = 0,
                resource = "Chat"
            )
            chatList.add(newReceived)
            recyclerAdapter.addChat(newReceived)
            binding.chattingEditText.text.clear()

            // 플레이스홀더 아이템 두 개 추가
            val placeholder1 = Received(
                id = -2,
                isAi = true,
                message = "로딩 중...",
                datetime = "",
                roomId = roomId,
                userId = 0,
                resource = ""
            )
            val placeholder2 = placeholder1.copy(id = -1)

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
        val receivedMessage = chatMessage.data?.message
        val newReceived = Received(
            id = receivedMessage?.id,
            isAi = true,
            message = receivedMessage?.message ?: "값이 비어있음",
            datetime = receivedMessage?.timestamp ?: SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss",
                Locale.getDefault()
            ).format(Date()),
            roomId = roomId,
            userId = receivedMessage?.userId ?: -1,
            resource = receivedMessage?.resource ?: "CHAT"
        )
        log("Chatting handleChatMessage chatMessage is $chatMessage")

        val action = chatMessage.data?.action ?: return
        // action에 따라 처리
        when (action) {
            "START" -> {}
            "TEXT" -> {
                log("TEXT 리턴 값입니다!")
                chatList[chatList.size - 2] = (newReceived)
                recyclerAdapter.setData(chatList)

            }

            "IMAGE" -> {
                log("IMAGE 리턴 값입니다!")
                chatList[chatList.size - 1] = (newReceived)
                recyclerAdapter.setData(chatList)
            }

            "END" -> {
                if (chatMessage.data.message == null) return
                shortToast(chatMessage.data.message.message!!)
                for (i in 0..<2) {
                    chatList.removeAt(chatList.size - 2 + i)
                }
                recyclerAdapter.setData(chatList)
            }

            else -> {
                logE("알 수 없는 action: $action")
            }
        }

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

    fun stringToMillis(dateString: String): Long? {
        // 날짜 포맷을 설정합니다. 밀리초 이하의 숫자는 무시하기 위해 .SSS까지만 사용합니다.
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return date?.time // Long 형태의 밀리초 반환
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