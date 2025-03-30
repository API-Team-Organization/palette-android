package com.api.palette.presentation.main.create.room.adapter

import android.annotation.SuppressLint
import android.os.Handler
import android.text.Spannable
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.api.palette.R
import com.api.palette.data.room.data.RoomData

class CreateMediaAdapter(
    private val itemList: ArrayList<RoomData>
) : RecyclerView.Adapter<CreateMediaAdapter.WorkViewHolder>() {

    private val handler = Handler()
    private var isLongClick = false

    lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_work_recycler, parent, false)
        return WorkViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: WorkViewHolder, position: Int) {
        val item = itemList[position]

        holder.ivLogo.setImageResource(R.drawable.logo)
        holder.tvTitle.text = item.title
        holder.tvDesc.text = item.message

        setupDescriptionTextView(holder)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDescriptionTextView(holder: WorkViewHolder) {
        holder.tvDesc.apply {
            setOnTouchListener { v, event ->
                val textView = v as TextView
                val movementMethod: MovementMethod = textView.movementMethod
                val handled = movementMethod.onTouchEvent(textView, textView.text as Spannable, event)

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isLongClick = false
                        handler.postDelayed({
                            isLongClick = true
                            itemClickListener.onItemLongClick(holder.bindingAdapterPosition)
                        }, 600)
                    }
                    MotionEvent.ACTION_UP -> {
                        handler.removeCallbacksAndMessages(null)
                        if (!handled && !isLongClick) {
                            itemClickListener.onItemClick(holder.bindingAdapterPosition)
                        }
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        handler.removeCallbacksAndMessages(null)
                    }
                }

                handled
            }

            movementMethod = LinkMovementMethod.getInstance()
            ellipsize = TextUtils.TruncateAt.MARQUEE
            isSelected = true
            isSingleLine = true
        }
    }

    inner class WorkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivLogo: ImageView = itemView.findViewById(R.id.iv_logo)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_poster_title)
        val tvDesc: TextView = itemView.findViewById(R.id.tv_poster_desc)

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
