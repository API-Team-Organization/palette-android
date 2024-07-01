package com.example.palette.ui.main.create.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.common.Constant
import com.example.palette.data.chat.ChatData
import com.example.palette.data.chat.ChatRequestManager
import com.example.palette.data.chat.ChatModel
import com.example.palette.ui.main.create.chat.adapter.ChattingRecyclerAdapter
import com.example.palette.data.room.RoomRequestManager
import com.example.palette.data.room.data.RoomData
import com.example.palette.data.room.data.TitleData
import com.example.palette.databinding.FragmentChattingBinding
import com.example.palette.ui.base.BaseControllable
import com.example.palette.ui.util.log
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ChattingFragment(private var roomId: Int) : Fragment() {
    private lateinit var binding: FragmentChattingBinding
    private val recyclerAdapter: ChattingRecyclerAdapter by lazy {
        ChattingRecyclerAdapter()
    }
    private lateinit var listDemo: MutableList<ChatModel>

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
        viewLifecycleOwner.lifecycleScope.launch {
            listDemo = ChatRequestManager.getChatList(PaletteApplication.prefs.token, roomId)!!.data
            if (listDemo.size != 0) {
                recyclerAdapter.setData(listDemo)
                binding.chattingRecycler.smoothScrollToPosition(listDemo.size - 1)
            }
        }

        binding.chattingToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack() // 백 스택에서 프래그먼트 제거
        }

        binding.chattingSubmitButton.setOnClickListener {
            val newMessage = binding.chattingEditText.text.toString()

            val chat = ChatData(roomId, newMessage)

            if (newMessage.isNotBlank()) {
                submitText(chat)
            }

        }

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
                    binding.chattingSubmitButton.setImageResource(R.drawable.ic_send)
                } else {
                    binding.chattingSubmitButton.setImageResource(R.drawable.ic_send_ok)

                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트가 변경되기 전 호출됩니다. 여기서는 사용하지 않습니다.
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경되는 동안 호출됩니다. 여기서는 사용하지 않습니다.
            }
        })
    }

    private fun submitText(chat: ChatData) {
        // TODO: 메세지 시, roomId, chat 서버에 보내기.
        viewLifecycleOwner.lifecycleScope.launch {
            ChatRequestManager.createChat(PaletteApplication.prefs.token, chat)
            // TODO: 첫 글 시, 제목 설정
            if (listDemo.size == 1) {
                log("ChattingFragment 첫 메세지를 제목으로 설정합니다 ${chat}")
                RoomRequestManager.setRoomTitle(PaletteApplication.prefs.token, RoomData(roomId, chat.message))
            }
            log("ChattingFragment 메세지를 제목으로 설정하지않습니다. ${chat} && listDemo == $listDemo")

            listDemo = ChatRequestManager.getChatList(PaletteApplication.prefs.token, roomId)!!.data
        }
        scrollToPosition()

        val newChatModel = ChatModel(id = -100, isAi = false, message = chat.message, datetime = "대충 날짜", roomId = roomId, userId = 0)
        listDemo.add(newChatModel)
        recyclerAdapter.addChat(newChatModel)
        binding.chattingEditText.text.clear()
        binding.chattingRecycler.smoothScrollToPosition(recyclerAdapter.itemCount - 1)
    }

    private fun scrollToPosition() {
        binding.chattingRecycler.let { recyclerView ->
            val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
            layoutManager?.scrollToPositionWithOffset(recyclerView.size-1, 0)
        }
    }

    override fun onDestroyView() {
        if (listDemo.isEmpty()) {
            (requireActivity() as? BaseControllable)?.deleteRoom(roomId = roomId)
        }

        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()

        (requireActivity() as? BaseControllable)?.bottomVisible(true)
    }
}