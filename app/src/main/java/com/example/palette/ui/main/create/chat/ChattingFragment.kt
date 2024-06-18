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
import com.example.palette.data.chat.ChatModel
import com.example.palette.data.chat.ChattingRecyclerAdapter
import com.example.palette.data.room.RoomRequestManager
import com.example.palette.data.room.data.TitleData
import com.example.palette.databinding.FragmentChattingBinding
import com.example.palette.ui.base.BaseControllable
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.launch


class ChattingFragment : Fragment() {
    private lateinit var binding: FragmentChattingBinding
    private val recyclerAdapter: ChattingRecyclerAdapter by lazy {
        ChattingRecyclerAdapter()
    }
    private var listDemo = mutableListOf(
        ChatModel("RECEIVE", "안녕하세요 Palette입니다.", ""),
        ChatModel("USER", "쌈뽕하게 하나 내와봐라", ""),
        ChatModel("RECEIVE", "해드림 ㅇㅇ", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/77/10_sharing_book_cover_background.jpg/500px-10_sharing_book_cover_background.jpg"),
        ChatModel("USER", "오 근데 나무위키가 최고지;;", ""),
        ChatModel("RECEIVE", "ㅇㅇ 꺼무위키 나라;;", ""),
        ChatModel("USER", "?? 수듄';", ""),
    )

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
        binding.chattingToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack() // 백 스택에서 프래그먼트 제거
        }

        binding.chattingSubmitButton.setOnClickListener {
            submitText()
        }

        binding.chattingRecycler.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        }

        recyclerAdapter.setData(listDemo)
        binding.chattingRecycler.smoothScrollToPosition(listDemo.size - 1)

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

    private fun submitText() {
        val newMessage = binding.chattingEditText.text.toString()
        if (newMessage.isNotBlank()) {
            scrollToPosition()
            createRoom(newMessage)
            val newChatModel = ChatModel("USER", newMessage, "")
            listDemo.add(newChatModel)
            recyclerAdapter.addChat(newChatModel)
            binding.chattingEditText.text.clear()
            binding.chattingRecycler.smoothScrollToPosition(recyclerAdapter.itemCount - 1)
        }
    }

    private fun createRoom(title: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val roomResponse = RoomRequestManager.roomRequest(PaletteApplication.prefs.token, TitleData(title))

                if (roomResponse.isSuccessful) {
                    shortToast("생성 성공")
                }
            } catch (e: Exception) {
                Log.e(Constant.TAG, "ChattingFragment createRoom error : ",e)
            }
        }
    }

    private fun scrollToPosition() {
        binding.chattingRecycler.let { recyclerView ->
            val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
            layoutManager?.scrollToPositionWithOffset(recyclerView.size-1, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as? BaseControllable)?.bottomVisible(true)
    }
}