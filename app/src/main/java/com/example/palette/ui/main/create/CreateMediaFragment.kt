package com.example.palette.ui.main.create

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.palette.ui.main.create.adapter.CreateMediaAdapter
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.data.room.RoomData
import com.example.palette.data.room.RoomRequestManager
import com.example.palette.databinding.FragmentCreateMediaBinding
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
                val item = itemList[position]
                shortToast("${item.title} 클릭함")
                startChatting()
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
                    // 로그인 페이지로 이동 또는 로그인 상태 재설정
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
}
