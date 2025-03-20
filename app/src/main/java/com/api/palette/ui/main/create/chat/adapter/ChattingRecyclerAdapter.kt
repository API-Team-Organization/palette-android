package com.api.palette.ui.main.create.chat.adapter

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
    private var currentDialog: Dialog? = null

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
        val isLast = position == listOfChat.size - 1
        if (!chat.isAi) (holder as RightViewHolder).bind(chat)
        else (holder as LeftViewHolder).bind(chat, isLast)
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

    private fun showCopyDialog(context: Context, text: CharSequence, sourceView: View) {
        val builder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_copy, null)

        builder.setView(view)

        val dialog = builder.create()

        view.findViewById<TextView>(R.id.tv_copy).setOnClickListener {
            val clipboardManager = sourceView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            val clipData = ClipData.newPlainText("Palette", text)
            clipboardManager?.setPrimaryClip(clipData)
            Toast.makeText(context, "복사되었습니다.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showZoomedImageDialog(context: Context, imageUrl: String) {
        currentDialog?.dismiss()
        val dialog = Dialog(context)
        currentDialog = dialog

        val dialogView = LayoutInflater.from(context).inflate(R.layout.item_zoomed_image_dialog, null)
        val imageView = dialogView.findViewById<SubsamplingScaleImageView>(R.id.imageView)
        val closeButton = dialogView.findViewById<ImageView>(R.id.btn_close)
        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE)
        closeButton.setOnClickListener { dialog.dismiss() }

        var dialogBitmap: Bitmap? = null

        Glide.with(context)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(false)
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    dialogBitmap = resource.copy(resource.config, true)
                    imageView.setImage(ImageSource.bitmap(dialogBitmap))
                }
                override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {
                    dialogBitmap?.let { if (!it.isRecycled) it.recycle() }
                    imageView.recycle()
                }
            })

        dialog.setOnDismissListener {
            dialogBitmap?.let { if (!it.isRecycled) it.recycle() }
            imageView.recycle()
            currentDialog = null
        }

        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val screenHeight = context.resources.displayMetrics.heightPixels
        val dialogHeight = (screenHeight * 0.9).toInt()
        dialog.window?.setLayout(context.resources.displayMetrics.widthPixels, dialogHeight)
        dialog.show()
    }

    inner class LeftViewHolder(private val binding: ItemChattingPaletteBoxBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: MessageResponse, isLast: Boolean) {
            binding.apply {
                chattingCreatedImage.setImageDrawable(null)
                textGchatMessagePalette.text = null
                textGchatTimePalette.text = null

                if (chat.resource == ChatResource.INTERNAL_IMAGE_LOADING) {
                    chattingLoadImage.visibility = View.VISIBLE
                    textGchatMessagePalette.visibility = View.GONE
                    return
                }

                chattingLoadImage.visibility = View.GONE
                textGchatMessagePalette.visibility = View.VISIBLE
                textGchatMessagePalette.text = chat.message

                if (chat.resource == ChatResource.IMAGE) {
                    Glide.with(itemView)
                        .load(chat.message)
                        .override(600, 900)
                        .into(chattingCreatedImage)
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
                    textGchatMessagePalette.text = chat.message
                    textGchatTimePalette.text = formatChatTime(chat.datetime)
                    textGchatMessagePalette.setOnLongClickListener {
                        showCopyDialog(itemView.context, textGchatMessagePalette.text, binding.cardGchatMessagePalette)
                        true
                    }
                }

                val lastBorderDrawable = GradientDrawable().apply {
                    setColor(ContextCompat.getColor(root.context, R.color.darkGray))
                    cornerRadius = 16f
                }
                val borderDrawable = GradientDrawable().apply {
                    cornerRadius = 16f
                    setColor(ContextCompat.getColor(root.context, R.color.lightGray))
                }

                if (isLast) {
                    textGchatMessagePalette.setTextColor(ContextCompat.getColor(root.context, R.color.white))
                    textGchatMessagePalette.background = lastBorderDrawable
                    root.requestLayout()
                } else {
                    textGchatMessagePalette.setTextColor(ContextCompat.getColor(root.context, R.color.black))
                    textGchatMessagePalette.background = borderDrawable
                    root.requestLayout()
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
            val builder = AlertDialog.Builder(context)
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_download_image, null)
            builder.setView(view)

            val dialog = builder.create()
            val noButton: TextView = view.findViewById(R.id.noTextView)
            val yesButton: TextView = view.findViewById(R.id.yesTextView)
            noButton.setOnClickListener { dialog.dismiss() }
            yesButton.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    val bitmap = downloadBitmap(imageUrl)
                    bitmap?.let { saveImageToGallery(context, it) }
                    Toast.makeText(context, "다운로드되었습니다.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            dialog.show()
            dialog.window?.setLayout((context.resources.displayMetrics.widthPixels * 0.9).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    inner class RightViewHolder(private val binding: ItemChattingMeBoxBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: MessageResponse) {
            binding.apply {
                textGchatMessageMe.text = chat.message
                textGchatTimeMe.text = formatChatTime(chat.datetime)
                layoutGchatContainerMe.setOnLongClickListener {
                    showCopyDialog(itemView.context, textGchatMessageMe.text, binding.cardGchatMessageMe)
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
