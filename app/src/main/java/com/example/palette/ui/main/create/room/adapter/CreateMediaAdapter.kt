package com.example.palette.ui.main.create.room.adapter

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.view.LayoutInflater
import android.view.MotionEvent
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: WorkViewHolder, position: Int) {
        holder.iv_logo.setImageResource(R.drawable.logo)
        holder.tv_poster_title.text = itemList[position].title
        holder.tv_poster_desc.text = lastMessageList[position]

        // 터치 이벤트를 통해 클릭 처리
        holder.tv_poster_desc.setOnTouchListener { v, event ->
            val textView = v as TextView
            val movementMethod: MovementMethod = textView.movementMethod

            // 링크 터치 이벤트 처리
            val touchHandled = movementMethod.onTouchEvent(textView, textView.text as Spannable, event)

            if (!touchHandled && event.action == MotionEvent.ACTION_UP) {
                // 터치가 링크가 아닐 때 클릭 처리
                itemClickListener.onItemClick(holder.bindingAdapterPosition)
            }

            touchHandled
        }

        holder.tv_poster_desc.movementMethod = LinkMovementMethod.getInstance()
        holder.tv_poster_desc.ellipsize = TextUtils.TruncateAt.MARQUEE
        holder.tv_poster_desc.setSelected(true)
        holder.tv_poster_desc.setSingleLine(true)
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
                itemClickListener.onItemClick(bindingAdapterPosition)
            }

            itemView.setOnLongClickListener {
                itemClickListener.onItemLongClick(bindingAdapterPosition)
                true
            }
        }
    }
}