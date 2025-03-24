package com.api.palette.ui.main.create.room

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.api.palette.R
import com.api.palette.data.room.RoomData
import com.api.palette.databinding.FragmentCreateMediaBinding
import com.api.palette.ui.main.create.room.adapter.CreateMediaAdapter
import com.api.palette.ui.main.create.chat.ChattingFragment
import com.api.palette.ui.util.changeFragment
import com.api.palette.ui.util.shortToast
import com.api.palette.viewmodel.InfoViewModel
import com.api.palette.viewmodel.RoomViewModel

@SuppressLint("NotifyDataSetChanged")
class CreateMediaFragment : Fragment() {

    private lateinit var binding: FragmentCreateMediaBinding
    private val roomViewModel: RoomViewModel by viewModels()
    private val infoViewModel: InfoViewModel by viewModels()
    private lateinit var workAdapter: CreateMediaAdapter
    private var itemList = ArrayList<RoomData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateMediaBinding.inflate(inflater, container, false)

        loadProfileInfo()

        roomViewModel.getRoomList()
        observeViewModel()

        binding.llStartNewWork.setOnClickListener {
            roomViewModel.createRoom()
        }

        return binding.root
    }

    private fun observeViewModel() {
        roomViewModel.roomList.observe(viewLifecycleOwner) { roomList ->
            if (roomList.isEmpty()) {
                binding.roomListEmptyText.visibility = View.VISIBLE
            } else {
                binding.roomListEmptyText.visibility = View.GONE
                itemList = ArrayList(roomList.reversed())
                initWorkAdapter()
            }
        }

        roomViewModel.createRoomResponse.observe(viewLifecycleOwner) { roomData ->
            roomData?.let {
                changeFragment(
                    ChattingFragment(
                        roomId = it.id,
                        title = it.title ?: "New Chat",
                        isFirst = true
                    )
                )
            } ?: shortToast("룸 생성 오류")
        }

        roomViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                shortToast(error)
            }
        }

        infoViewModel.profileData.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                binding.userName.text = it.name
            }
        }
        infoViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                shortToast(error)
            }
        }
    }

    private fun initWorkAdapter() {
        workAdapter = CreateMediaAdapter(itemList)
        binding.workRecyclerView.adapter = workAdapter
        binding.workRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        workAdapter.itemClickListener = object : CreateMediaAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                startChatting(itemList[position].id.toString(), itemList[position].title ?: "Chat")
            }
            override fun onItemLongClick(position: Int) {
                // 삭제 로직
            }
        }
    }

    private fun startChatting(roomId: String, title: String, isFirst: Boolean = false) {
        val bundle = Bundle().apply {
            putString("roomId", roomId)
            putString("title", title)
            putBoolean("isFirst", isFirst)
        }
        findNavController().navigate(R.id.action_createMediaFragment_to_chattingFragment, bundle)
    }

    private fun loadProfileInfo() {
        infoViewModel.loadProfileInfo()
    }
}
