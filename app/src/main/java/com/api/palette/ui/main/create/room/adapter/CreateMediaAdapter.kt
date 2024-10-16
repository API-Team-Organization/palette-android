package com.api.palette.ui.main.create.room.adapter

import android.annotation.SuppressLint
import android.os.Handler
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
import com.api.palette.R
import com.api.palette.data.room.data.RoomData

class CreateMediaAdapter(
    private val itemList: ArrayList<RoomData>
) : RecyclerView.Adapter<CreateMediaAdapter.WorkViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_work_recycler, parent, false)
        return WorkViewHolder(view)
    }

    private val handler = Handler()
    private var isLongClick = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: WorkViewHolder, position: Int) {
        holder.iv_logo.setImageResource(R.drawable.logo)
        holder.tv_poster_title.text = itemList[position].title
        holder.tv_poster_desc.text = itemList[position].message

        // 터치 이벤트를 통해 클릭 처리
        holder.tv_poster_desc.setOnTouchListener { v, event ->
            val textView = v as TextView
            val movementMethod: MovementMethod = textView.movementMethod

            // 링크 터치 이벤트 처리
            val touchHandled =
                movementMethod.onTouchEvent(textView, textView.text as Spannable, event)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 터치 시작 시 Handler를 사용하여 1.5초 후에 롱 클릭 이벤트 발생
                    isLongClick = false // 초기화
                    handler.postDelayed({
                        isLongClick = true // 롱 클릭 상태로 변경
                        itemClickListener.onItemLongClick(position)
                    }, 600)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // 터치 종료 시 Handler에서 호출된 Runnable을 제거
                    handler.removeCallbacksAndMessages(null)

                    // 롱 클릭 상태가 아닐 경우에만 클릭 이벤트 처리
                    if (!touchHandled && !isLongClick) {
                        itemClickListener.onItemClick(position)
                    }
                }
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