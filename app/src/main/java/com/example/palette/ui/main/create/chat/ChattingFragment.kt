package com.example.palette.ui.main.create.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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


class ChattingFragment(private var roomId: Int) : Fragment() {
    private lateinit var binding: FragmentChattingBinding
    private val recyclerAdapter: ChattingRecyclerAdapter by lazy {
        ChattingRecyclerAdapter()
    }
    private lateinit var listDemo: MutableList<Received>

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
                log("ChattingFragment 리스트는 비어있지 않습니다.")
                recyclerAdapter.setData(listDemo)
                binding.chattingRecycler.smoothScrollToPosition(listDemo.size - 1)
            }
            else {
                log("ChattingFragment 리스트는 비어있습니다.")

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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트가 변경되기 전 호출됩니다. 여기서는 사용하지 않습니다.
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경되는 동안 호출됩니다. 여기서는 사용하지 않습니다.
            }
        })
    }

    private fun submitText(chat: ChatData) {
        viewLifecycleOwner.lifecycleScope.launch {
            showSampleData(true)
            val response = ChatRequestManager.createChat(PaletteApplication.prefs.token, chat)
            showSampleData(false)
            if (listDemo.size == 1) {
                log("ChattingFragment 첫 메세지를 제목으로 설정합니다 ${chat}")
                RoomRequestManager.setRoomTitle(PaletteApplication.prefs.token, RoomData(roomId, chat.message))
                log("response는 ${response.body()}")
            }
            if (response.isSuccessful) {
                with(response.body()!!.data.received[0]) {
                    log("ChattingFragment submitText response.body()!!.data.received[0] : ${response.body()!!.data.received[0]}")
                    val newReceived1 = Received(id = id, isAi = isAi, message = message, datetime = datetime, roomId = roomId, userId = userId, resource = resource)
                    listDemo.add(newReceived1)
                    recyclerAdapter.addChat(newReceived1)
                }

                with(response.body()!!.data.received[1]) {
                    log("ChattingFragment submitText response.body()!!.data.received[1] : ${response.body()!!.data.received[1]}")
                    val newReceived2 = Received(id = id, isAi = isAi, message = message, datetime = datetime, roomId = roomId, userId = userId, resource = resource)
                    listDemo.add(newReceived2)
                    recyclerAdapter.addChat(newReceived2)
                }
            } else {
                shortToast("이미지를 제작하는데 실패했습니다.")
                return@launch
            }

            listDemo = ChatRequestManager.getChatList(PaletteApplication.prefs.token, roomId)!!.data
            log("/chat/{roomId}에서 어떤 값을 주는지 확인: ${listDemo}")
            scrollToPosition()
        }

        scrollToPosition()
        val newReceived = Received(id = -100, isAi = false, message = chat.message, datetime = "대충 날짜", roomId = roomId, userId = 0, resource = "Chat")
        listDemo.add(newReceived)
        recyclerAdapter.addChat(newReceived)

        binding.chattingEditText.text.clear()
        binding.chattingRecycler.smoothScrollToPosition(recyclerAdapter.itemCount - 1)
    }

    private fun scrollToPosition() {
        binding.chattingRecycler.let { recyclerView ->
            val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
            layoutManager?.scrollToPositionWithOffset(recyclerView.size-1, 0)
        }
    }

    private fun showSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.sflSample.startShimmer()
            binding.sflSample.visibility = View.VISIBLE
            binding.chattingRecycler.visibility = View.GONE
        } else {
            binding.sflSample.stopShimmer()
            binding.sflSample.visibility = View.GONE
            binding.chattingRecycler.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (listDemo.isEmpty()) {
            (requireActivity() as? BaseControllable)?.deleteRoom(roomId = roomId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as? BaseControllable)?.bottomVisible(true)
    }
}