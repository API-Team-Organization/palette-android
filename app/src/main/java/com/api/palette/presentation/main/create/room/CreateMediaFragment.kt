package com.api.palette.presentation.main.create.room

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.data.room.data.RoomData
import com.api.palette.databinding.FragmentCreateMediaBinding
import com.api.palette.presentation.base.BaseControllable
import com.api.palette.presentation.main.create.chat.ChattingFragment
import com.api.palette.presentation.main.create.room.adapter.CreateMediaAdapter
import com.api.palette.presentation.main.create.room.viewmodel.RoomViewModel
import com.api.palette.presentation.util.changeFragment
import com.api.palette.presentation.util.log
import com.api.palette.presentation.util.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.HttpException

@AndroidEntryPoint
class CreateMediaFragment : Fragment() {
    private lateinit var binding: FragmentCreateMediaBinding
    private val itemList = ArrayList<RoomData>()
    private lateinit var workAdapter: CreateMediaAdapter
    private var isDeleting = false

    private val roomViewModel: RoomViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateMediaBinding.inflate(inflater, container, false)
        loadProfileInfo()
        showSampleData(true)
        loadData()

        binding.llStartNewWork.setOnClickListener {
            createRoom()
        }
        return binding.root
    }

    private fun initWorkAdapter() {
        with(binding) {
            workAdapter = CreateMediaAdapter(itemList)
            workRecyclerView.adapter = workAdapter
            workRecyclerView.layoutManager = LinearLayoutManager(context)
        }
        workAdapter.itemClickListener = object : CreateMediaAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                if (isDeleting) return
                startChatting(itemList[position].id, itemList[position].title.toString())
            }
            override fun onItemLongClick(position: Int) {
                if (isDeleting) return
                deleteChatDialog(requireContext(), position)
            }
        }
    }

    private fun loadData() {
        roomViewModel.loadRoomList(PaletteApplication.prefs.token) {
            if (it is HttpException && it.code() == 401) {
                shortToast("인증 오류: 다시 로그인해주세요.")
                (requireActivity() as? BaseControllable)?.sessionDialog(requireActivity())
            } else {
                log("CreateMediaFragment loadData 오류: ${it.message}")
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            kotlinx.coroutines.delay(300L)
            val rooms = roomViewModel.roomList.value
            if (rooms.isNullOrEmpty()) {
                binding.roomListEmptyText.visibility = View.VISIBLE
            } else {
                binding.roomListEmptyText.visibility = View.GONE
                val list = rooms.reversed()
                itemList.clear()
                itemList.addAll(list)
                initWorkAdapter()
                workAdapter.notifyDataSetChanged()
            }
            showSampleData(false)
        }
    }

    private fun startChatting(roomId: String, title: String, isFirst: Boolean = false) {
        changeFragment(ChattingFragment(roomId = roomId, title = title, isFirst = isFirst))
    }

    private fun deleteChatDialog(context: Context, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        dialog.window?.apply {
            setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
            requestFeature(Window.FEATURE_NO_TITLE)
            setLayout(
                (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        dialogView.findViewById<TextView>(R.id.confirmTextView).text =
            "정말 \"${itemList[position].title}\"를(을) 삭제하시겠습니까?"

        dialogView.findViewById<TextView>(R.id.noTextView).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<TextView>(R.id.yesTextView).setOnClickListener {
            isDeleting = true
            deleteRoom(position)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showSampleData(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                sflSample.startShimmer()
                sflSample.visibility = View.VISIBLE
                workRecyclerView.visibility = View.GONE
            } else {
                sflSample.stopShimmer()
                sflSample.visibility = View.GONE
                workRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun createRoom() {
        roomViewModel.createRoom(PaletteApplication.prefs.token) { result ->
            result.onSuccess {
                startChatting(it.id, it.title.toString(), isFirst = true)
                log("생성된 roomId == ${it.id}")
            }.onFailure { e ->
                shortToast("룸 생성 오류: ${e.message}")
                log("createRoom error: $e")
            }
        }
    }

    private fun deleteRoom(position: Int) {
        val selectedRoom = itemList[position]
        roomViewModel.deleteRoom(PaletteApplication.prefs.token, selectedRoom.id) {
            it.fold(
                onSuccess = {
                    itemList.removeAt(position)
                    workAdapter.notifyItemRemoved(position)
                    isDeleting = false
                    if (itemList.isEmpty()) {
                        binding.roomListEmptyText.visibility = View.VISIBLE
                    }
                },
                onFailure = { e ->
                    shortToast("삭제 실패: ${e.message}")
                    isDeleting = false
                }
            )
        }
    }

    private fun loadProfileInfo() {
        val prefs = PaletteApplication.prefs
        val name = prefs.username
        if (name.isEmpty()) {
            binding.userName.text = "..."
        } else {
            binding.userName.text = name
            binding.today.text = "환영합니다!"
        }
    }
}
