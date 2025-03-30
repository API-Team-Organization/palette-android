package com.api.palette.presentation.main.create.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.api.palette.data.chat.data.PromptData
import com.api.palette.data.socket.data.MessageResponse
import com.api.palette.databinding.ItemChattingMeBoxBinding
import com.api.palette.databinding.ItemChattingPaletteBoxBinding

class ChattingRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val chatList = mutableListOf<MessageResponse>()
    private val qnaList = mutableListOf<PromptData>()

    companion object {
        private const val VIEW_TYPE_LEFT = 1
        private const val VIEW_TYPE_RIGHT = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].isAi) VIEW_TYPE_LEFT else VIEW_TYPE_RIGHT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_LEFT -> {
                val binding = ItemChattingPaletteBoxBinding.inflate(inflater, parent, false)
                LeftViewHolder(binding)
            }
            else -> {
                val binding = ItemChattingMeBoxBinding.inflate(inflater, parent, false)
                RightViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = chatList[position]
        when (holder) {
            is LeftViewHolder -> holder.bind(message)
            is RightViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = chatList.size

    fun setQnAList(list: List<PromptData>) {
        qnaList.clear()
        qnaList.addAll(list)
        notifyDataSetChanged()
    }

    fun setData(data: List<MessageResponse>) {
        chatList.clear()
        chatList.addAll(data)
        notifyDataSetChanged()
    }

    inner class LeftViewHolder(private val binding: ItemChattingPaletteBoxBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageResponse) {
            binding.textGchatMessagePalette.text = message.message
        }
    }

    inner class RightViewHolder(private val binding: ItemChattingMeBoxBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageResponse) {
            binding.textGchatMessageMe.text = message.message
        }
    }
}
