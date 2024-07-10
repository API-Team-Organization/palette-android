package com.example.palette.ui.main.create.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.palette.data.chat.Received
import com.example.palette.databinding.ItemChattingMeBoxBinding
import com.example.palette.databinding.ItemChattingPaletteBoxBinding

class ChattingRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listOfChat = mutableListOf<Received>()

    companion object {
        const val VIEW_TYPE_LEFT = 1
        const val VIEW_TYPE_RIGHT = 2
    }

    override fun getItemViewType(position: Int): Int {
//        return super.getItemViewType(position)
        return if (listOfChat[position].isAi) VIEW_TYPE_LEFT else VIEW_TYPE_RIGHT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LEFT -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemChattingPaletteBoxBinding.inflate(inflater, parent, false)
                LeftViewHolder(binding)
            }
            else -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemChattingMeBoxBinding.inflate(inflater, parent, false)
                RightViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int = listOfChat.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = listOfChat[position]
        if (!chat.isAi) {
            (holder as RightViewHolder).bind(chat)
        } else {
            (holder as LeftViewHolder).bind(chat)
        }
    }

    fun setData(list: MutableList<Received>) {
        listOfChat.clear()
        listOfChat.addAll(list)
        notifyDataSetChanged() // 전체 데이터가 변경되었음을 알림
    }

    fun addChat(chat: Received) {
        listOfChat.add(chat)
        notifyItemInserted(listOfChat.size - 1)
    }

    inner class LeftViewHolder(private val binding: ItemChattingPaletteBoxBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Received) {
            binding.apply {
                // 초기화
                chattingCreatedImage.setImageDrawable(null) // 이미지 초기화
                textGchatMessagePalette.text = null // 텍스트 초기화

                if (chat.resource == "IMAGE") {
                    Glide.with(itemView)
                        .load(chat.message) // 이미지 URL
                        .override(600, 900) // 최대 너비 600, 최대 높이 900으로 제한 (원하는 크기로 조정)
                        .into(chattingCreatedImage) // ImageView 설정
                } else {
                    textGchatMessagePalette.text = chat.message // 텍스트 설정
                }
            }
        }
    }

    inner class RightViewHolder(private val binding: ItemChattingMeBoxBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Received) {
            binding.textGchatMessageMe.text = chat.message // 텍스트 설정
        }
    }
}