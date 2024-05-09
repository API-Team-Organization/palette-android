package com.example.palette.ui.service.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.palette.viewmodel.WorkAdapter
import com.example.palette.viewmodel.WorkItem
import com.example.palette.R
import com.example.palette.databinding.FragmentCreateMediaBinding

class CreateMediaFragment : Fragment() {
    private lateinit var binding : FragmentCreateMediaBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateMediaBinding.inflate(inflater, container, false)

        val work_recycler_view = binding.root.findViewById<RecyclerView>(R.id.work_recycler_view)
        val itemList = ArrayList<WorkItem>()

        // 임시로 넣어놓은 itemList
        itemList.add(WorkItem(R.drawable.logo, "오렌지 주스 포스터", "나 : (대충 오렌지 주스 홍보 설.."))
        itemList.add(WorkItem(R.drawable.logo, "오렌지 주스 포스터", "나 : (대충 오렌지 주스 홍보 설.."))
        itemList.add(WorkItem(R.drawable.logo, "오렌지 주스 포스터", "나 : (대충 오렌지 주스 홍보 설.."))
        itemList.add(WorkItem(R.drawable.logo, "오렌지 주스 포스터", "나 : (대충 오렌지 주스 홍보 설.."))
        itemList.add(WorkItem(R.drawable.logo, "오렌지 주스 포스터", "나 : (대충 오렌지 주스 홍보 설.."))
        itemList.add(WorkItem(R.drawable.logo, "오렌지 주스 포스터", "나 : (대충 오렌지 주스 홍보 설.."))
        itemList.add(WorkItem(R.drawable.logo, "오렌지 주스 포스터", "나 : (대충 오렌지 주스 홍보 설.."))
        itemList.add(WorkItem(R.drawable.logo, "오렌지 주스 포스터", "나 : (대충 오렌지 주스 홍보 설.."))
        itemList.add(WorkItem(R.drawable.logo, "오렌지 주스 포스터", "나 : (대충 오렌지 주스 홍보 설.."))

        val workAdapter = WorkAdapter(itemList)

        work_recycler_view.adapter = workAdapter
        work_recycler_view.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        workAdapter.itemClickListener = object : WorkAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val item = itemList[position]
                Toast.makeText(context, "${item.title} 클릭함", Toast.LENGTH_SHORT).show()
            }
        }

        workAdapter.notifyDataSetChanged()

        return binding.root
    }
}