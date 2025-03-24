package com.api.palette.ui.main.create.room.adapter

import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.api.palette.R
import com.api.palette.data.room.RoomData
import com.api.palette.databinding.ItemWorkRecyclerBinding

class CreateMediaAdapter(
    private val itemList: ArrayList<RoomData>
) : RecyclerView.Adapter<CreateMediaAdapter.WorkViewHolder>() {

    private val handler = Handler()
    private var isLongClick = false

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }

    lateinit var itemClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkViewHolder {
        val binding = ItemWorkRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkViewHolder, position: Int) {
        val item = itemList[position]
        holder.binding.ivLogo.setImageResource(R.drawable.logo)
        holder.binding.tvPosterTitle.text = item.title
        holder.binding.tvPosterDesc.text = item.message

        holder.binding.tvPosterDesc.setOnTouchListener { v, event ->
            val textView = v as TextView
            val spannableText: Spannable = if (textView.text is Spannable) {
                textView.text as Spannable
            } else {
                SpannableString(textView.text)
            }

            val touchHandled = textView.movementMethod.onTouchEvent(textView, spannableText, event)
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
                    if (!touchHandled && !isLongClick) {
                        itemClickListener.onItemClick(holder.bindingAdapterPosition)
                        textView.performClick()
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacksAndMessages(null)
                }
            }
            touchHandled
        }
    }

    override fun getItemCount(): Int = itemList.size

    inner class WorkViewHolder(val binding: ItemWorkRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                itemClickListener.onItemClick(bindingAdapterPosition)
            }
            binding.root.setOnLongClickListener {
                itemClickListener.onItemLongClick(bindingAdapterPosition)
                true
            }
        }
    }
}
