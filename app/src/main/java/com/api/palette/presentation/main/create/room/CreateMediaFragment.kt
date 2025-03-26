package com.api.palette.presentation.main.create.room

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.data.base.DataResponse
import com.api.palette.data.info.InfoRequestManager
import com.api.palette.data.room.RoomRequestManager
import com.api.palette.data.room.data.RoomData
import com.api.palette.databinding.FragmentCreateMediaBinding
import com.api.palette.presentation.base.BaseControllable
import com.api.palette.presentation.main.create.chat.ChattingFragment
import com.api.palette.presentation.main.create.room.adapter.CreateMediaAdapter
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
    private lateinit var roomList: DataResponse<List<RoomData>>
    private var isDeleting = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateMediaBinding.inflate(inflater, container, false)
        loadProfileInfo()
        showSampleData(true)
        loadData()

        binding.llStartNewWork.setOnClickListener { createRoom() }

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
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                roomList = RoomRequestManager.roomList(PaletteApplication.prefs.token)
                if (roomList.code <= 400) {
                    if (roomList.data.isEmpty()) {
                        binding.roomListEmptyText.visibility = View.VISIBLE
                    } else {
                        binding.roomListEmptyText.visibility = View.GONE
                        val list = roomList.data.reversed()
                        itemList.clear()
                        itemList.addAll(list)
                        initWorkAdapter()
                        workAdapter.notifyDataSetChanged()
                    }
                    showSampleData(false)
                } else {
                    log("roomList.code <= 400 else{}에서 서버 오류 발생: ${roomList.message}")
                }
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    shortToast("인증 오류: 다시 로그인해주세요.")
                    (requireActivity() as? BaseControllable)?.sessionDialog(requireActivity())
                } else {
                    log("HttpException 서버 오류: ${e.message()} ${e.response()?.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                log("CreateMediaFragment loadData 오류: ${e.message}")
            }
        }
    }

    private fun startChatting(position: String, title: String, isFirst: Boolean = false) {
        changeFragment(ChattingFragment(roomId = position, title = title, isFirst = isFirst))
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
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RoomRequestManager.roomRequest(PaletteApplication.prefs.token)
                response.body()?.let {
                    startChatting(it.data.id, title = it.data.title.toString(), isFirst = true)
                    log("생성된 roomId == ${it.data.id}")
                } ?: shortToast("룸 생성 오류")
            } catch (e: Exception) {
                Log.e("CreateMediaFragment", "createRoom error", e)
            }
        }
    }

    private fun deleteRoom(position: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RoomRequestManager.deleteRoom(PaletteApplication.prefs.token, itemList[position].id)
                if (response.isSuccessful) {
                    itemList.removeAt(position)
                    workAdapter.notifyItemRemoved(position)
                    isDeleting = false
                    if (itemList.isEmpty()) {
                        binding.roomListEmptyText.visibility = View.VISIBLE
                    }
                } else {
                    shortToast("삭제 실패: ${response.message()}")
                }
            } catch (e: HttpException) {
                Log.e("CreateMediaFragment", "deleteRoom Http error: ${e.response()?.errorBody()?.string()}")
                shortToast("삭제 실패: ${e.message}")
            } catch (e: Exception) {
                Log.e("CreateMediaFragment", "deleteRoom error: ${e.message}")
                shortToast("An error occurred: ${e.message}")
            }
        }
    }

    private fun loadProfileInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            val username = PaletteApplication.prefs.username
            if (username.isEmpty()) {
                binding.userName.text = "..."
                try {
                    val profileResponse = InfoRequestManager.profileInfoRequest(PaletteApplication.prefs.token)
                    profileResponse?.let {
                        if (it.code <= 400) {
                            PaletteApplication.prefs.username = it.data.name
                            binding.userName.text = it.data.name
                            binding.today.text = "환영합니다!"
                        } else {
                            log("프로필 정보를 가져오지 못했습니다: ${it.message}")
                        }
                    }
                } catch (e: Exception) {
                    log("프로필 정보 오류: ${e.message}")
                }
            } else {
                binding.userName.text = username
                binding.today.text = "환영합니다!"
            }
        }
    }
}
