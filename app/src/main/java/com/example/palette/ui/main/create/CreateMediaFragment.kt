package com.example.palette.ui.main.create

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.palette.ui.main.create.adapter.CreateMediaAdapter
import com.example.palette.ui.main.create.adapter.CreateMediaItem
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.common.Constant
import com.example.palette.data.info.InfoRequestManager
import com.example.palette.data.room.RoomRequestManager
import com.example.palette.databinding.FragmentCreateMediaBinding
import com.example.palette.ui.main.create.chat.ChattingFragment
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.launch

class CreateMediaFragment : Fragment() {
    private lateinit var binding : FragmentCreateMediaBinding
    @SuppressLint("NotifyDataSetChanged")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCreateMediaBinding.inflate(inflater, container, false)

        val work_recycler_view = binding.root.findViewById<RecyclerView>(R.id.work_recycler_view)
        val itemList = ArrayList<CreateMediaItem>()

        // 임시로 넣어놓은 itemList
        itemList.add(CreateMediaItem(R.drawable.logo, "오렌지 주스 포스터", "나 : (대충 오렌지 주스 홍보 설.."))
        itemList.add(CreateMediaItem(R.drawable.logo, "오렌지 주스 포스터", "나 : (대충 오렌지 주스 홍보 설.."))
        itemList.add(CreateMediaItem(R.drawable.logo, "오렌지 주스 포스터", "나 : (대충 오렌지 주스 홍보 설.."))
        itemList.add(CreateMediaItem(R.drawable.logo, "오렌지 주스 포스터", "나 : (대충 오렌지 주스 홍보 설.."))
        itemList.add(CreateMediaItem(R.drawable.logo, "오렌지 주스 포스터", "나 : (대충 오렌지 주스 홍보 설.."))
        itemList.add(CreateMediaItem(R.drawable.logo, "오렌지 주스 포스터", "나 : (대충 오렌지 주스 홍보 설.."))
        itemList.add(CreateMediaItem(R.drawable.logo, "오렌지 주스 포스터", "나 : (대충 오렌지 주스 홍보 설.."))
        itemList.add(CreateMediaItem(R.drawable.logo, "오렌지 주스 포스터", "나 : (대충 오렌지 주스 홍보 설.."))
        itemList.add(CreateMediaItem(R.drawable.logo, "오렌지 주스 포스터", "나 : (대충 오렌지 주스 홍보 설.."))

        val workAdapter = CreateMediaAdapter(itemList)

        work_recycler_view.adapter = workAdapter
        work_recycler_view.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        workAdapter.itemClickListener = object : CreateMediaAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val item = itemList[position]
                shortToast("${item.title} 클릭함")
                startChatting()
            }
        }

        binding.llStartNewWork.setOnClickListener {
            createChat()
        }

        workAdapter.notifyDataSetChanged()

        return binding.root
    }

    private fun startChatting() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainContent, ChattingFragment())
            .addToBackStack(null) // 백 스택에 프래그먼트 추가
            .commitAllowingStateLoss()
    }

    private fun createChat() {
        startChatting()
    }
}