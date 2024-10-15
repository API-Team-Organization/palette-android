package com.example.palette.ui.main.create.room.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.palette.R
import com.example.palette.data.room.data.RoomData

class CreateMediaAdapter(
    private val itemList: ArrayList<RoomData>,
    private val lastMessageList: List<String>
) : RecyclerView.Adapter<CreateMediaAdapter.WorkViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_work_recycler, parent, false)
        return WorkViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkViewHolder, position: Int) {
        holder.iv_logo.setImageResource(R.drawable.logo)
        holder.tv_poster_title.text = itemList[position].title
        holder.tv_poster_desc.text = lastMessageList[position]
    }

    override fun getItemCount(): Int = itemList.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }

    lateinit var itemClickListener: OnItemClickListener

    inner class WorkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_logo = itemView.findViewById<ImageView>(R.id.iv_logo)
        val tv_poster_title = itemView.findViewById<TextView>(R.id.tv_poster_title)
        val tv_poster_desc = itemView.findViewById<TextView>(R.id.tv_poster_desc)

        init {
            itemView.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition)
            }

            itemView.setOnLongClickListener {
                itemClickListener.onItemLongClick(bindingAdapterPosition)
                true
            }
        }
    }
}