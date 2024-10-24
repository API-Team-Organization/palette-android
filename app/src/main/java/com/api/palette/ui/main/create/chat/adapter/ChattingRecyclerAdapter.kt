package com.api.palette.ui.main.create.chat.adapter

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.api.palette.R
import com.api.palette.data.chat.qna.PromptData
import com.api.palette.data.socket.ChatResource
import com.api.palette.data.socket.MessageResponse
import com.api.palette.databinding.ItemChattingMeBoxBinding
import com.api.palette.databinding.ItemChattingPaletteBoxBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.net.HttpURLConnection
import java.net.URL

class ChattingRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listOfChat = mutableListOf<MessageResponse>()
    private val qnaList = mutableListOf<PromptData>()

    companion object {
        const val VIEW_TYPE_LEFT = 1
        const val VIEW_TYPE_RIGHT = 2
    }

    override fun getItemViewType(position: Int) =
        if (listOfChat[position].isAi) VIEW_TYPE_LEFT else VIEW_TYPE_RIGHT

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
        notifyDataSetChanged() // 전체 데이터가 변경되었음을 알림
    }

    inner class LeftViewHolder(private val binding: ItemChattingPaletteBoxBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: MessageResponse) {
            binding.apply {
                // 초기화
                chattingCreatedImage.setImageDrawable(null) // 이미지 초기화
                textGchatMessagePalette.text = null // 텍스트 초기화
                textGchatTimePalette.text = null // 텍스트 초기화

                if (chat.resource == ChatResource.INTERNAL_IMAGE_LOADING) {
                    // 로딩 애니메이션을 표시할 뷰
                    chattingLoadImage.visibility = View.VISIBLE
                    textGchatMessagePalette.visibility = View.GONE
                    return
                }

                // 실제 데이터를 표시할 뷰
                chattingLoadImage.visibility = View.GONE
                textGchatMessagePalette.visibility = View.VISIBLE
                textGchatMessagePalette.text = chat.message

                if (chat.resource == ChatResource.IMAGE) {
                    Glide.with(itemView).load(chat.message) // 이미지 URL
                        .override(600, 900) // 최대 너비 600, 최대 높이 900으로 제한 (원하는 크기로 조정)
                        .into(chattingCreatedImage) // ImageView 설정
                    textGchatMessagePalette.visibility = View.GONE
                    chattingCreatedImage.visibility = View.VISIBLE

                    chattingCreatedImage.setOnLongClickListener {
                        showDownloadDialog(itemView.context, chat.message)
                        true
                    }

                    chattingCreatedImage.setOnClickListener {
                        showZoomedImageDialog(itemView.context, chat.message)
                    }
                } else {
                    textGchatMessagePalette.visibility = View.VISIBLE
                    chattingCreatedImage.visibility = View.GONE
                    textGchatMessagePalette.text = chat.message // 텍스트 설정
                    textGchatTimePalette.text = formatChatTime(chat.datetime) // 텍스트 설정

                    textGchatMessagePalette.setOnLongClickListener {
                        showCopyPaletteDialog(itemView.context, binding)
                        true
                    }
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
            val dialogBuilder = AlertDialog.Builder(context)

            val dialogView =
                LayoutInflater.from(context).inflate(R.layout.dialog_download_image, null)
            dialogBuilder.setView(dialogView)

            val dialog = dialogBuilder.create()

            val noButton: TextView = dialogView.findViewById(R.id.noTextView)
            val yesButton: TextView = dialogView.findViewById(R.id.yesTextView)

            noButton.setOnClickListener {
                dialog.dismiss()
            }

            yesButton.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    val bitmap = downloadBitmap(imageUrl)
                    bitmap?.let {
                        saveImageToGallery(context, it)
                    }
                    Toast.makeText(context, "다운로드되었습니다.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }

            dialog.show()

            dialog.window?.setLayout(
                (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun showCopyPaletteDialog(context: Context, binding: ItemChattingPaletteBoxBinding) {
        val dialogBuilder = AlertDialog.Builder(context)

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_copy, null)
        dialogBuilder.setView(dialogView)

        val dialog = dialogBuilder.create()

        val copyTextView = dialogView.findViewById<TextView>(R.id.tv_copy)

        copyTextView.setOnClickListener {
            val clipboardManager =
                binding.cardGchatMessagePalette.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            val clipData =
                ClipData.newPlainText("Palette", binding.textGchatMessagePalette.text)
            clipboardManager?.setPrimaryClip(clipData)

            Toast.makeText(context, "복사되었습니다.", Toast.LENGTH_SHORT).show()

            dialog.dismiss()
        }

        dialog.show()
    }


    private fun showCopyMeDialog(context: Context, binding: ItemChattingMeBoxBinding) {
        val dialogBuilder = AlertDialog.Builder(context)

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_copy, null)
        dialogBuilder.setView(dialogView)

        val dialog = dialogBuilder.create()

        val copyTextView = dialogView.findViewById<TextView>(R.id.tv_copy)

        copyTextView.setOnClickListener {
            val clipboardManager =
                binding.cardGchatMessageMe.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            val clipData =
                ClipData.newPlainText("Palette", binding.textGchatMessageMe.text)
            clipboardManager?.setPrimaryClip(clipData)

            Toast.makeText(context, "복사되었습니다.", Toast.LENGTH_SHORT).show()

            dialog.dismiss()
        }

        dialog.show()
    }


    private fun showZoomedImageDialog(context: Context, imageUrl: String) {
        val dialog = Dialog(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_image, null)

        val imageView = dialogView.findViewById<SubsamplingScaleImageView>(R.id.zoomedImageView)

        Glide.with(context).asBitmap().load(imageUrl).into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    imageView.setImage(ImageSource.bitmap(resource))
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })

        dialog.setContentView(dialogView)
        dialog.show()
    }

    inner class RightViewHolder(private val binding: ItemChattingMeBoxBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: MessageResponse) {
            binding.apply {
                textGchatMessageMe.text = chat.message // 텍스트 설정
                textGchatTimeMe.text = formatChatTime(chat.datetime) // 텍스트 초기화

                layoutGchatContainerMe.setOnLongClickListener {
                    showCopyMeDialog(itemView.context, binding)
                    true
                }
            }
        }
    }

    fun formatChatTime(datetime: Instant): String {
        val timeZone = TimeZone.currentSystemDefault()

        val localDateTime = datetime.toLocalDateTime(timeZone)
        val period = if (localDateTime.hour < 12) "오전" else "오후"
        val hour12 = if (localDateTime.hour % 12 == 0) 12 else localDateTime.hour % 12
        val formattedMinute = localDateTime.minute.toString().padStart(2, '0')

        return "$period $hour12:$formattedMinute"
    }
}
