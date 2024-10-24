package com.api.palette.ui.main.create.room

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.common.Constant
import com.api.palette.data.base.DataResponse
import com.api.palette.data.info.InfoRequestManager.profileInfoRequest
import com.api.palette.data.room.RoomRequestManager
import com.api.palette.data.room.data.RoomData
import com.api.palette.databinding.FragmentCreateMediaBinding
import com.api.palette.ui.base.BaseControllable
import com.api.palette.ui.main.create.room.adapter.CreateMediaAdapter
import com.api.palette.ui.main.create.chat.ChattingFragment
import com.api.palette.ui.util.changeFragment
import com.api.palette.ui.util.log
import com.api.palette.ui.util.shortToast
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CreateMediaFragment : Fragment() {
    private lateinit var binding: FragmentCreateMediaBinding
    private val itemList = ArrayList<RoomData>()
    private lateinit var workAdapter: CreateMediaAdapter
    private lateinit var roomList: DataResponse<List<RoomData>>
    private var isDeleting = false

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateMediaBinding.inflate(inflater, container, false)

        loadProfileInfo()

        showSampleData(isLoading = true)

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
            workRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        workAdapter.itemClickListener = object : CreateMediaAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                if (isDeleting) return // item 삭제 중 삭호작용 막음 -> block index error
                startChatting(itemList[position].id, itemList[position].title.toString())
            }

            override fun onItemLongClick(position: Int) {
                if (isDeleting) return // item 삭제 중 삭호작용 막음 -> block index error
                deleteChatDialog(requireActivity(), position)
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

                    showSampleData(isLoading = false)
                } else {
                    log("roomList.code <= 400 else{}에서 서버 오류가 발생했습니다: ${roomList.message}")
                }
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    shortToast("인증 오류: 다시 로그인해주세요.")
                    (requireActivity() as? BaseControllable)?.sessionDialog(requireActivity())
                } else {
                    log(
                        "HttpException & !401 에서 서버 오류가 발생했습니다: ${e.message()} \n서버응답: ${
                            e.response()?.errorBody()?.string()
                        }"
                    )
                }
            } catch (e: Exception) {
                log("CreateMediaFragment loadData 알 수 없는 오류가 발생했습니다: ${e.message}")
            }
        }
    }

    private fun startChatting(position: Int, title: String, isFirst: Boolean = false) {
        changeFragment(ChattingFragment(roomId = position, title = title, isFirst = isFirst))
    }

    private fun deleteChatDialog(context: Context, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        val titleTextView = dialogView.findViewById<TextView>(R.id.confirmTextView)
        val noButton = dialogView.findViewById<TextView>(R.id.noTextView)
        val yesButton = dialogView.findViewById<TextView>(R.id.yesTextView)

        titleTextView.text = "정말 \"${itemList[position].title}\"를(을) 삭제하시겠습니까?"

        noButton.setOnClickListener {
            dialog.dismiss()
        }

        yesButton.setOnClickListener {
            isDeleting = true
            deleteRoom(position)
            dialog.dismiss()
        }

        dialog.show()

        dialog.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
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

    private fun createRoom() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val roomResponse = RoomRequestManager.roomRequest(PaletteApplication.prefs.token)

                if (roomResponse.isSuccessful) {
                    startChatting(
                        roomResponse.body()!!.data.id,
                        title = roomResponse.body()!!.data.title.toString(),
                        isFirst = true,
                    )
                    log("생성된 roomId == ${roomResponse.body()!!.data.id}")
                } else {
                    shortToast("생성 실패 errorCode: 34533")
                }
            } catch (e: Exception) {
                Log.e(Constant.TAG, "ChattingFragment createRoom error : ", e)
            }
        }
    }

    private fun deleteRoom(position: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RoomRequestManager.deleteRoom(
                    PaletteApplication.prefs.token,
                    itemList[position].id
                )
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
                Log.e(
                    Constant.TAG,
                    "CreateMediaFragment deleteRoom Http error: ${
                        e.response()?.errorBody()?.string()
                    }"
                )
                shortToast("Failed to delete item: ${e.message()}")
            } catch (e: Exception) {
                Log.e(Constant.TAG, "CreateMediaFragment deleteRoom Exception error: ${e.message}")
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
                    val profileResponse = profileInfoRequest(PaletteApplication.prefs.token)
                    if (profileResponse != null && profileResponse.code <= 400) {
                        PaletteApplication.prefs.username = profileResponse.data.name
                        binding.userName.text = profileResponse.data.name
                        binding.today.text = "환영합니다!"
                    } else {
                        log("프로필 정보를 가져오는 데 실패했습니다: ${profileResponse?.message}")
                    }
                } catch (e: Exception) {
                    log("프로필 정보를 가져오는 도중 오류가 발생했습니다: ${e.message}")
                }
            } else {
                binding.userName.text = username
                binding.today.text = "환영합니다!"
            }
        }
    }
}