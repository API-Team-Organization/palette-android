package com.api.palette.presentation.work

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.api.palette.databinding.FragmentWorkPosterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkPosterFragment : Fragment() {
    private var _binding: FragmentWorkPosterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWorkPosterBinding.inflate(inflater, container, false)
        binding.rvImageList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvImageList.adapter = PosterAdapter()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class PosterAdapter : RecyclerView.Adapter<PosterAdapter.PosterViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosterViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            return PosterViewHolder(view)
        }
        override fun onBindViewHolder(holder: PosterViewHolder, position: Int) {
            holder.textView.text = "포스터 목록이 없습니다."
        }
        override fun getItemCount(): Int = 1
        class PosterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(android.R.id.text1)
        }
    }
}
