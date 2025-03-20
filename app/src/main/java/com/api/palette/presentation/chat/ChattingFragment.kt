package com.api.palette.presentation.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentChattingBinding
import com.api.palette.domain.model.ChatAnswer
import com.api.palette.presentation.chat.adapter.ChattingRecyclerAdapter
import com.api.palette.ui.util.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChattingFragment(
    private val roomId: Int,
    private val title: String,
    private val isFirst: Boolean = false,
) : Fragment() {

    private var _binding: FragmentChattingBinding? = null
    private val binding get() = _binding!!
    private val chatViewModel: ChatViewModel by viewModels()
    private val recyclerAdapter: ChattingRecyclerAdapter by lazy { ChattingRecyclerAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChattingBinding.inflate(inflater, container, false)
        initView()
        observeViewModel()
        chatViewModel.loadChatList(PaletteApplication.prefs.token, roomId)
        chatViewModel.loadQnAList(PaletteApplication.prefs.token, roomId)
        return binding.root
    }

    private fun initView() {
        binding.chattingToolbar.title = title
        binding.chattingRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.chattingRecycler.adapter = recyclerAdapter

        binding.chattingSubmitButton.setOnClickListener {
            if (binding.chattingEditText.text.isNotEmpty()) {
                val chat = ChatAnswer.UserInputAnswer(
                    input = binding.chattingEditText.text.toString(),
                    type = "USER_INPUT"
                )
                chatViewModel.createChat(PaletteApplication.prefs.token, chat, roomId)
                binding.chattingEditText.text.clear()
            }
        }
    }

    private fun observeViewModel() {
        chatViewModel.chatList.observe(viewLifecycleOwner, Observer { chats ->
            recyclerAdapter.setData(chats)
            binding.chattingRecycler.smoothScrollToPosition(chats.size)
        })
        chatViewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            shortToast(error)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
