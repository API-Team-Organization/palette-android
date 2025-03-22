package com.api.palette.ui.main.create.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.api.palette.data.room.data.RoomData
import com.api.palette.databinding.FragmentCreateMediaBinding
import com.api.palette.ui.main.create.room.adapter.CreateMediaAdapter
import com.api.palette.ui.main.create.chat.ChattingFragment
import com.api.palette.ui.util.changeFragment
import com.api.palette.ui.util.shortToast
import com.api.palette.viewmodel.RoomViewModel

class CreateMediaFragment : Fragment() {

    private lateinit var binding: FragmentCreateMediaBinding
    private val roomViewModel: RoomViewModel by viewModels()
    private lateinit var workAdapter: CreateMediaAdapter
    private var itemList = ArrayList<RoomData>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCreateMediaBinding.inflate(inflater, container, false)
        loadProfileInfo()
        showSampleData(true)
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
                changeFragment(ChattingFragment(roomId = it.id, title = it.title ?: "New Chat", isFirst = true))
            } ?: shortToast("룸 생성 오류")
        }
        roomViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
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
                startChatting(itemList[position].id, itemList[position].title ?: "Chat")
            }
            override fun onItemLongClick(position: Int) {
                // 삭제 로직 구현 (필요 시)
            }
        }
    }

    private fun startChatting(roomId: Int, title: String, isFirst: Boolean = false) {
        changeFragment(ChattingFragment(roomId = roomId, title = title, isFirst = isFirst))
    }

    private fun showSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.sflSample.startShimmer()
            binding.sflSample.visibility = View.VISIBLE
            binding.workRecyclerView.visibility = View.GONE
        } else {
            binding.sflSample.stopShimmer()
            binding.sflSample.visibility = View.GONE
            binding.workRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun loadProfileInfo() {
        // 프로필 정보 로드 (필요 시 InfoViewModel 사용)
    }
}
