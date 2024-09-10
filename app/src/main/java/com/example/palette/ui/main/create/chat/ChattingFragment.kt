package com.example.palette.ui.main.create.chat

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
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
import com.example.palette.databinding.FragmentChattingBinding
import com.example.palette.ui.base.BaseControllable
import com.example.palette.ui.main.create.chat.adapter.ChattingRecyclerAdapter
import com.example.palette.ui.util.log
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChattingFragment(private var roomId: Int, private var title: String) : Fragment() {
    private lateinit var binding: FragmentChattingBinding
    private val recyclerAdapter: ChattingRecyclerAdapter by lazy {
        ChattingRecyclerAdapter()
    }
    private var chatList: MutableList<Received> = mutableListOf()
    private var isLoading = false
    private var loadPage = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChattingBinding.inflate(inflater, container, false)

        initView()
        initEditText()
        return binding.root
    }

    private fun initView() {
        binding.chattingToolbar.title = title

        viewLifecycleOwner.lifecycleScope.launch {
            chatList = ChatRequestManager.getChatList(PaletteApplication.prefs.token, roomId, loadPage)!!.data
            if (chatList.size != 0) {
                chatList.reverse()
                recyclerAdapter.setData(chatList)
                binding.chattingRecycler.scrollToPosition(chatList.size - 1)
            }
            else {
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

                    viewLifecycleOwner.lifecycleScope.launch {
                        loadPage += 1
                        val temporaryList = ChatRequestManager.getChatList(PaletteApplication.prefs.token, roomId, loadPage)?.data

                        if (temporaryList.isNullOrEmpty()) {
                            shortToast("채팅 내역이 더 없습니다")
                            loadPage -= 1
                        } else {
                            chatList.reverse()
                            chatList += temporaryList
                            chatList.reverse()
                            recyclerAdapter.setData(chatList)
                            binding.chattingRecycler.scrollToPosition(chatList.size - loadPage*10)
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
        showSampleData(false)
        binding.chattingEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // EditText 내용이 변경된 후 호출됩니다.
                if (s.isNullOrBlank()) {
                    binding.chattingSubmitButton.setImageResource(R.drawable.ic_send)
                } else {
                    binding.chattingSubmitButton.setImageResource(R.drawable.ic_send_ok)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun submitText(chat: ChatData) {
        viewLifecycleOwner.lifecycleScope.launch {
            showSampleData(true)
            val response = ChatRequestManager.createChat(PaletteApplication.prefs.token, chat, roomId = roomId)
            if (chatList.size == 1) {
                log("ChattingFragment 첫 메세지를 제목으로 설정합니다 ${chat}")
                RoomRequestManager.setRoomTitle(PaletteApplication.prefs.token, RoomData(roomId, chat.message))
                binding.chattingToolbar.title = chat.message
            }
            if (response.isSuccessful) {
                for(i in 0..1) {
                    with(response.body()!!.data.received[i]) {
                        log("ChattingFragment submitText response.body()!!.data.received[0] : ${response.body()!!.data.received[i]}")
                        val newReceived1 = Received(id = id, isAi = isAi, message = message, datetime = datetime, roomId = roomId, userId = userId, resource = resource)
                        chatList.add(newReceived1)
                        recyclerAdapter.addChat(newReceived1)
                    }
                }
            } else {
                shortToast("부적절한 단어 혹은 짧은 문장")
                showSampleData(false)
                return@launch
            }
            showSampleData(false)

            chatList = ChatRequestManager.getChatList(PaletteApplication.prefs.token, roomId, loadPage)!!.data
            log("/chat/{roomId}에서 어떤 값을 주는지 확인: ${chatList}")
            binding.chattingRecycler.smoothScrollToPosition(chatList.size - 1)
        }

        val newReceived = Received(
            id = -100,
            isAi = false,
            message = chat.message,
            datetime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date()),
            roomId = roomId,
            userId = 0,
            resource = "Chat"
        )
        chatList.add(newReceived)
        recyclerAdapter.addChat(newReceived)
        binding.chattingEditText.text.clear()
        binding.chattingRecycler.smoothScrollToPosition(recyclerAdapter.itemCount - 1)
    }

    private fun showSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.sflSample.startShimmer()
            binding.sflSample.visibility = View.VISIBLE
            binding.chattingRecycler.visibility = View.GONE
            binding.chattingEditText.visibility = View.GONE
            binding.chattingSubmitButton.visibility = View.GONE
        } else {
            binding.sflSample.stopShimmer()
            binding.sflSample.visibility = View.GONE
            binding.chattingRecycler.visibility = View.VISIBLE
            binding.chattingEditText.visibility = View.VISIBLE
            binding.chattingSubmitButton.visibility = View.VISIBLE
        }
    }

    private fun showChangeTitleDialog() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("제목 변경")
        builder.setMessage("제목을 변경해주세요")

        // EditText 생성
        val input = EditText(requireContext())
        input.hint = "새 제목 입력"
        input.inputType = InputType.TYPE_CLASS_TEXT

        // EditText를 다이얼로그에 추가
        builder.setView(input)

        builder.setPositiveButton("확인") { dialog, _ ->
            val newTitle = input.text.toString()

            if (newTitle.isBlank()) {
                shortToast("제목을 입력해주세요")
                return@setPositiveButton
            }

             viewLifecycleOwner.lifecycleScope.launch {
                 RoomRequestManager.setRoomTitle(PaletteApplication.prefs.token, RoomData(roomId, newTitle))
                 binding.chattingToolbar.title = newTitle
             }

            dialog.dismiss()
        }
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (chatList.isEmpty()) {
            (requireActivity() as? BaseControllable)?.deleteRoom(roomId = roomId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as? BaseControllable)?.bottomVisible(true)
    }
}