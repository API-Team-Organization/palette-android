package com.example.palette.ui.main.create

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.palette.R
import com.example.palette.common.Constant
import com.example.palette.data.chat.ChatModel
import com.example.palette.data.chat.ChattingRecyclerAdapter
import com.example.palette.databinding.FragmentChattingBinding
import com.example.palette.ui.main.ServiceActivity


class ChattingFragment : Fragment() {
    private lateinit var binding: FragmentChattingBinding
    private val recyclerAdapter: ChattingRecyclerAdapter by lazy {
        ChattingRecyclerAdapter()
    }
    private val listDemo = listOf(
        ChatModel("RECEIVE", "안녕하세요 Palette입니다.", ""),
        ChatModel("USER", "쌈뽕하게 하나 내와봐라", ""),
        ChatModel("RECEIVE", "해드림 ㅇㅇ", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/77/10_sharing_book_cover_background.jpg/500px-10_sharing_book_cover_background.jpg"),
        ChatModel("USER", "오 근데 나 군대 갔다왔는데?", ""),
        ChatModel("RECEIVE", "ㅇㅇ 알빠노?", ""),
        ChatModel("USER", "?", ""),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChattingBinding.inflate(inflater, container, false)

        initView()
        setEditText()
        return binding.root
    }

    private fun initView() {
        binding.chattingToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack() // 백 스택에서 프래그먼트 제거
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

        (activity as? ServiceActivity)?.bottomVisible(false)
    }


    private fun setEditText() {
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

    override fun onDestroy() {
        super.onDestroy()
        (activity as? ServiceActivity)?.bottomVisible(true)
    }


}