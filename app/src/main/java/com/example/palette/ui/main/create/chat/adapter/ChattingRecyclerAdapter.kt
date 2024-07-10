package com.example.palette.ui.main.create.chat.adapter

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.palette.data.chat.Received
import com.example.palette.databinding.ItemChattingMeBoxBinding
import com.example.palette.databinding.ItemChattingPaletteBoxBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale

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
                textGchatTimePalette.text = null // 텍스트 초기화

                if (chat.resource == "IMAGE") {
                    Glide.with(itemView)
                        .load(chat.message) // 이미지 URL
                        .override(600, 900) // 최대 너비 600, 최대 높이 900으로 제한 (원하는 크기로 조정)
                        .into(chattingCreatedImage) // ImageView 설정

                    chattingCreatedImage.setOnLongClickListener {
                        showDownloadDialog(itemView.context, chat.message)
                        true
                    }
                } else {
                    textGchatMessagePalette.text = chat.message // 텍스트 설정
                    textGchatTimePalette.text = formatChatTime(chat.datetime) // 텍스트 설정
                }
            }
        }

        private suspend fun downloadBitmap(urlString: String): Bitmap? {
            return withContext(Dispatchers.IO) {
                try {
                    val url = URL(urlString)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val inputStream = connection.inputStream
                    BitmapFactory.decodeStream(inputStream)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

        private fun saveImageToGallery(context: Context, bitmap: Bitmap) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "downloaded_image.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                resolver.openOutputStream(it).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream!!)
                }

                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
        }

        private fun showDownloadDialog(context: Context, imageUrl: String) {
            AlertDialog.Builder(context).apply {
                setTitle("이미지 다운로드")
                setMessage("이미지를 다운로드하시겠습니까?")
                setPositiveButton("예") { _, _ ->
                    CoroutineScope(Dispatchers.Main).launch {
                        val bitmap = downloadBitmap(imageUrl)
                        bitmap?.let {
                            saveImageToGallery(context, it)
                        }
                        Toast.makeText(context, "다운로드되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                setNegativeButton("아니오", null)
                show()
            }
        }
    }

    inner class RightViewHolder(private val binding: ItemChattingMeBoxBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Received) {
            binding.textGchatMessageMe.text = chat.message // 텍스트 설정
            binding.textGchatTimeMe.text = formatChatTime(chat.datetime) // 텍스트 초기화
        }
    }

    fun formatChatTime(datetime: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormat.parse(datetime)
        return outputFormat.format(date!!)
    }
}