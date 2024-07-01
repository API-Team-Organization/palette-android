package com.example.palette.ui.main.create

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.common.Constant
import com.example.palette.data.base.BaseResponse
import com.example.palette.data.room.RoomRequestManager
import com.example.palette.data.room.data.RoomData
import com.example.palette.databinding.FragmentCreateMediaBinding
import com.example.palette.ui.base.BaseControllable
import com.example.palette.ui.main.create.adapter.CreateMediaAdapter
import com.example.palette.ui.main.create.chat.ChattingFragment
import com.example.palette.ui.util.log
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CreateMediaFragment : Fragment() {
    private lateinit var binding: FragmentCreateMediaBinding
    private val itemList = ArrayList<RoomData>()
    private lateinit var workAdapter: CreateMediaAdapter
    private lateinit var roomList: BaseResponse<List<RoomData>>

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCreateMediaBinding.inflate(inflater, container, false)

        with(binding) {
            // 어댑터 초기화
            workAdapter = CreateMediaAdapter(itemList)
            workRecyclerView.adapter = workAdapter
            workRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        showSampleData(isLoading = true)

        // 데이터 로드
        loadData()

        // 아이템 클릭 리스너 설정
        workAdapter.itemClickListener = object : CreateMediaAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // TODO: 여기서 id 값 서버에 보내서 방마다 다른 채팅 보이게 할 것.
                log("item position is $position")
                // TODO: roomList 가 정의되어있지 않음 해결 ㄱㄱ
                startChatting(roomList.data[position].id)
            }

            override fun onItemLongClick(position: Int) {
                log("onItemLongClick itemPosition is ${itemList[position]}")
                deleteChatDialog(requireActivity(), position)
            }
        }

        binding.llStartNewWork.setOnClickListener {
            createRoom()
            viewLifecycleOwner.lifecycleScope.launch {
                val roomId = RoomRequestManager.roomList(PaletteApplication.prefs.token).data.last().id
                startChatting(roomId)
            }


        }

        return binding.root
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                roomList = RoomRequestManager.roomList(PaletteApplication.prefs.token)
                if (roomList.code <= 400) {
                    log("CreateMediaFragment <= 400 check data ${roomList.data}")

                    itemList.clear()
                    itemList.addAll(roomList.data)
                    workAdapter.notifyDataSetChanged() // 데이터 변경 통지

                    showSampleData(isLoading = false)
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
                log("CreateMediaFragment loadData 알 수 없는 오류가 발생했습니다: ${e.message}")
            }
        }
    }

    private fun startChatting(position: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainContent, ChattingFragment(position))
            .addToBackStack(null) // 백 스택에 프래그먼트 추가
            .commitAllowingStateLoss()
    }

    private fun deleteChatDialog(context: Context, position: Int) {
        val builder = AlertDialog.Builder(context)

        builder.setTitle("채팅방 삭제")
        builder.setMessage("정말 \"${itemList[position].title}\"를(을) 삭제하시겠습니까?")

        // "예" 버튼 추가
        builder.setPositiveButton("삭제") { dialog, _ ->
            deleteRoom(position)
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
                    shortToast("생성 성공") // 생성했으면, room/list해서 받은 뒤에, 가장 마지막에 있는거 id를 roomId에 넣고, createChatting 재실행
                }
            } catch (e: Exception) {
                Log.e(Constant.TAG, "ChattingFragment createRoom error : ",e)
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
    }
}
