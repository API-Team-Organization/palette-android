package com.example.palette.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.palette.R

class WorkAdapter(val itemList: ArrayList<WorkItem>): RecyclerView.Adapter<WorkAdapter.WorkViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.work_recycler_view, parent, false)
        return WorkViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkViewHolder, position: Int) {
        holder.iv_logo.setImageResource(R.drawable.logo)
        holder.tv_poster_title.text = itemList[position].title
        holder.tv_poster_desc.text = itemList[position].desc
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    inner class WorkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_logo = itemView.findViewById<ImageView>(R.id.iv_logo)
        val tv_poster_title = itemView.findViewById<TextView>(R.id.tv_poster_title)
        val tv_poster_desc = itemView.findViewById<TextView>(R.id.tv_poster_desc)

        init {
            itemView.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)
            }
        }
    }
}