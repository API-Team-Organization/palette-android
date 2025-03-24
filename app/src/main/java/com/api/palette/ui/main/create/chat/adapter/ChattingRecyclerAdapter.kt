package com.api.palette.ui.main.create.chat.adapter

import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.api.palette.data.chat.qna.PromptData
import com.api.palette.data.socket.MessageResponse
import com.api.palette.databinding.ItemChattingMeBoxBinding
import com.api.palette.databinding.ItemChattingPaletteBoxBinding

class ChattingRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listOfChat = mutableListOf<MessageResponse>()
    private val qnaList = mutableListOf<PromptData>()
    private var currentDialog: Dialog? = null

    companion object {
        const val VIEW_TYPE_LEFT = 1
        const val VIEW_TYPE_RIGHT = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (listOfChat[position].isAi) VIEW_TYPE_LEFT else VIEW_TYPE_RIGHT
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

    override fun getItemCount(): Int = listOfChat.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = listOfChat[position]
        val isLast = position == listOfChat.size - 1
        if (!chat.isAi) {
            (holder as RightViewHolder).bind(chat)
        } else {
            (holder as LeftViewHolder).bind(chat)
        }
    }

    fun setQnAList(list: List<PromptData>) {
        qnaList.clear()
        qnaList.addAll(list)
        notifyDataSetChanged()
    }

    fun setData(list: List<MessageResponse>) {
        listOfChat.clear()
        listOfChat.addAll(list)
        notifyDataSetChanged()
    }

    inner class LeftViewHolder(private val binding: ItemChattingPaletteBoxBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: MessageResponse) {
            binding.textGchatMessagePalette.text = chat.message
        }
    }

    inner class RightViewHolder(private val binding: ItemChattingMeBoxBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: MessageResponse) {
            binding.textGchatMessageMe.text = chat.message
        }
    }
}
