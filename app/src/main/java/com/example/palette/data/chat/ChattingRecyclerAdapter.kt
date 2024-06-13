package com.example.palette.data.chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.palette.common.Constant
import com.example.palette.databinding.ItemChattingMeBoxBinding
import com.example.palette.databinding.ItemChattingPaletteBoxBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL


class ChattingRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listOfChat = mutableListOf<ChatModel>()

    companion object {
        const val VIEW_TYPE_LEFT = 1
        const val VIEW_TYPE_RIGHT = 2
    }

    override fun getItemViewType(position: Int): Int {
//        return super.getItemViewType(position)
        return if (listOfChat[position].type == "USER") VIEW_TYPE_RIGHT else VIEW_TYPE_LEFT
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
        if (listOfChat[position].type == "USER")
            (holder as RightViewHolder).bind(listOfChat[position])
        else
            (holder as LeftViewHolder).bind(listOfChat[position])
    }

    fun setData(list: List<ChatModel>) {
        listOfChat.clear()
        listOfChat.addAll(list)
        notifyDataSetChanged() // 전체 데이터가 변경되었음을 알림
    }

    fun addChat(chat: ChatModel) {
        listOfChat.add(chat)
        notifyItemInserted(listOfChat.size - 1)
    }

    inner class LeftViewHolder(private val binding: ItemChattingPaletteBoxBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatModel) {
            binding.apply {
                chat.also {
                    textGchatMessagePalette.text = chat.message
                    CoroutineScope(Dispatchers.Main).launch {
                        chattingCreatedImage.setImageBitmap(stringToUrl(chat.image)) // image
                    }
                }
            }
        }
    }

    inner class RightViewHolder(private val binding: ItemChattingMeBoxBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatModel) {
            binding.apply {
                chat.also {
                    textGchatMessageMe.text = chat.message
                }
            }
        }
    }

    private suspend fun stringToUrl(urlString: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                // 이미지 URL 경로
                val url = URL(urlString)
                // web에서 이미지를 가져와 ImageView에 저장할 Bitmap을 만든다.
                val conn = url.openConnection() as HttpURLConnection
                conn.doInput = true // 서버로부터 응답 수신
                conn.connect() // 연결된 곳에 접속할 때 (connect() 호출해야 실제 통신 가능함)

                val inputStream = conn.inputStream // inputStream 값 가져오기
                val bitmap = BitmapFactory.decodeStream(inputStream) // Bitmap으로 변환
                inputStream.close()
                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}