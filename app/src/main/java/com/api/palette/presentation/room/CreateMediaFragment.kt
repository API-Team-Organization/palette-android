package com.api.palette.presentation.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentCreateMediaBinding
import com.api.palette.domain.model.RoomData
import com.api.palette.ui.main.create.room.adapter.CreateMediaAdapter
import com.api.palette.ui.util.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateMediaFragment : Fragment() {

    private var _binding: FragmentCreateMediaBinding? = null
    private val binding get() = _binding!!
    private val roomViewModel: RoomViewModel by viewModels()
    private lateinit var workAdapter: CreateMediaAdapter
    private val itemList = ArrayList<RoomData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCreateMediaBinding.inflate(inflater, container, false)
        initRecycler()
        observeViewModel()
        roomViewModel.loadRoomList(PaletteApplication.prefs.token)
        binding.llStartNewWork.setOnClickListener {
            roomViewModel.createRoom(PaletteApplication.prefs.token)
        }
        return binding.root
    }

    private fun initRecycler() {
        workAdapter = CreateMediaAdapter(itemList)
        binding.workRecyclerView.apply {
            adapter = workAdapter
            layoutManager = LinearLayoutManager(context)
        }
        workAdapter.itemClickListener = object : CreateMediaAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // 룸 선택 시 ChattingFragment 전환 처리
            }

            override fun onItemLongClick(position: Int) {
                roomViewModel.deleteRoom(PaletteApplication.prefs.token, itemList[position].id)
            }
        }
    }

    private fun observeViewModel() {
        roomViewModel.roomList.observe(viewLifecycleOwner) { rooms ->
            itemList.clear()
            itemList.addAll(rooms.reversed())
            workAdapter.notifyDataSetChanged()
            binding.roomListEmptyText.visibility = if (rooms.isEmpty()) View.VISIBLE else View.GONE
        }
        roomViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            shortToast(error)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
