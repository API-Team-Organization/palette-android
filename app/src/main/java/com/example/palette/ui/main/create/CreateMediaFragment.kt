package com.example.palette.ui.main.create

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.palette.MainActivity
import com.example.palette.ui.main.create.adapter.CreateMediaAdapter
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.common.Constant
import com.example.palette.data.room.data.RoomData
import com.example.palette.data.room.RoomRequestManager
import com.example.palette.data.room.data.IdData
import com.example.palette.databinding.FragmentCreateMediaBinding
import com.example.palette.ui.base.BaseControllable
import com.example.palette.ui.main.create.chat.ChattingFragment
import com.example.palette.ui.util.log
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CreateMediaFragment : Fragment() {
    private lateinit var binding: FragmentCreateMediaBinding
    private val itemList = ArrayList<RoomData>()
    private lateinit var workAdapter: CreateMediaAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCreateMediaBinding.inflate(inflater, container, false)

        val workRecyclerView = binding.workRecyclerView

        // 어댑터 초기화
        workAdapter = CreateMediaAdapter(itemList)
        workRecyclerView.adapter = workAdapter
        workRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // 데이터 로드
        loadData()

        // 아이템 클릭 리스너 설정
        workAdapter.itemClickListener = object : CreateMediaAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // TODO: 여기서 id 값 서버에 보내서 방마다 다른 채팅 보이게 할 것.
                startChatting()
            }

            override fun onItemLongClick(position: Int) {
                log("onItemLongClick itemPosition is ${itemList[position]}")
                deleteChatDialog(requireActivity(), position)
            }
        }

        binding.llStartNewWork.setOnClickListener {
            startChatting()
        }

        return binding.root
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val roomList = RoomRequestManager.roomList(PaletteApplication.prefs.token)
                if (roomList.code <= 400) {
                    log("CreateMediaFragment <= 400 check data ${roomList.data}")
                    itemList.clear()
                    itemList.addAll(roomList.data)
                    workAdapter.notifyDataSetChanged() // 데이터 변경 통지
                } else {
                    log("roomList.code <= 400 else{}에서 서버 오류가 발생했습니다: ${roomList.message}")
                }
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    shortToast("인증 오류: 다시 로그인해주세요.")
                    // 로그인 페이지로 이동 또는 로그인 상태 재설정증
                    (requireActivity() as? BaseControllable)?.sessionDialog(requireActivity())

                } else {
                    log("HttpException & !401 에서 서버 오류가 발생했습니다: ${e.message()}")
                }
            } catch (e: Exception) {
                log("알 수 없는 오류가 발생했습니다: ${e.message}")
            }
        }
    }

    private fun startChatting() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainContent, ChattingFragment())
            .addToBackStack(null) // 백 스택에 프래그먼트 추가
            .commitAllowingStateLoss()
    }

    private fun deleteChatDialog(context: Context, position: Int) {
        val builder = AlertDialog.Builder(context)

        builder.setTitle("채팅방 삭제")
        builder.setMessage("정말 \"${itemList[position].title}\"를(을) 삭제하시겠습니까?")

        // "예" 버튼 추가
        builder.setPositiveButton("삭제") { dialog, _ ->
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val response = RoomRequestManager.deleteRoom(PaletteApplication.prefs.token, itemList[position].id)
                    if (response.isSuccessful) {
                        itemList.removeAt(position)
                        workAdapter.notifyItemRemoved(position)
                        shortToast("삭제되었습니다")
                    } else {
                        shortToast("삭제 실패: ${response.message()}")
                    }
                } catch (e: HttpException) {
                    Log.e(Constant.TAG, "CreateMediaFragment deleteRoom Http error: ${e.response()?.errorBody()?.string()}")
                    shortToast("Failed to delete item: ${e.message()}")
                } catch (e: Exception) {
                    Log.e(Constant.TAG, "CreateMediaFragment deleteRoom Exception error: ${e.message}")
                    shortToast("An error occurred: ${e.message}")
                }
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.dismiss()
        }

        // 다이얼로그 외부 클릭이나 뒤로가기 버튼 비활성화
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()
    }
}
