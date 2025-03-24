package com.api.palette.ui.main.create.chat

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.api.palette.databinding.FragmentChattingBinding
import com.api.palette.data.chat.qna.ChatAnswer
import com.api.palette.ui.main.create.chat.adapter.ChattingRecyclerAdapter
import com.api.palette.ui.util.shortToast
import com.api.palette.viewmodel.ChatViewModel

class ChattingFragment(
    private val roomId: Int,
    private val title: String,
    private val isFirst: Boolean = false,
) : Fragment() {

    private lateinit var binding: FragmentChattingBinding
    private val chatViewModel: ChatViewModel by viewModels()
    private val recyclerAdapter: ChattingRecyclerAdapter by lazy { ChattingRecyclerAdapter() }
    private var currentDialog: Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentChattingBinding.inflate(inflater, container, false)
        binding.chattingToolbar.title = title

        setupRecyclerView()
        setupListeners()

        chatViewModel.currentRoomId = roomId
        chatViewModel.loadChatList()
        chatViewModel.loadQnAList(roomId)

        observeViewModel()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.chattingRecycler.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setupListeners() {
        binding.chattingSubmitButton.setOnClickListener {
            if (binding.chattingEditText.text.isEmpty()) return@setOnClickListener
            val input = binding.chattingEditText.text.toString()
            val chat = ChatAnswer.UserInputAnswer(input = input, type = "USER_INPUT")
            chatViewModel.sendChatMessage(roomId, chat)
            binding.chattingEditText.text.clear()
        }

        binding.chattingRecycler.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(-1)) {
                    val currentList = chatViewModel.chatList.value
                    if (!currentList.isNullOrEmpty()) {
                        val firstMessageTime = currentList.first().datetime.toString()
                        chatViewModel.loadMoreChats(firstMessageTime)
                    }
                }
            }
        })
    }

    private fun observeViewModel() {
        chatViewModel.chatList.observe(viewLifecycleOwner, Observer { chats ->
            recyclerAdapter.setData(chats)
            binding.chattingRecycler.smoothScrollToPosition(recyclerAdapter.itemCount)
        })

        chatViewModel.qnaList.observe(viewLifecycleOwner, Observer { qna ->
            recyclerAdapter.setQnAList(qna)
        })

        chatViewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            if (!error.isNullOrEmpty()) {
                shortToast(error)
            }
        })
    }
}
