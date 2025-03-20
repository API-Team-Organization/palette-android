package com.api.palette.presentation.chat.adapter

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
import com.api.palette.domain.model.ChatResource
import com.api.palette.domain.model.MessageResponse
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
    private var currentDialog: Dialog? = null

    companion object {
        const val VIEW_TYPE_LEFT = 1
        const val VIEW_TYPE_RIGHT = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (listOfChat[position].isAi) VIEW_TYPE_LEFT else VIEW_TYPE_RIGHT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_LEFT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chatting_palette_box, parent, false)
            LeftViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chatting_me_box, parent, false)
            RightViewHolder(view)
        }
    }

    override fun getItemCount(): Int = listOfChat.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = listOfChat[position]
        if (holder.itemViewType == VIEW_TYPE_LEFT) {
            (holder as LeftViewHolder).bind(chat, position == listOfChat.size - 1)
        } else {
            (holder as RightViewHolder).bind(chat)
        }
    }

    fun setData(list: List<MessageResponse>) {
        listOfChat.clear()
        listOfChat.addAll(list)
        notifyDataSetChanged()
    }

    private fun showCopyDialog(context: Context, text: CharSequence, sourceView: View) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_copy, null)
        val dialog = AlertDialog.Builder(context).setView(view).create()
        view.findViewById<TextView>(R.id.tv_copy).setOnClickListener {
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
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
        val closeButton = dialogView.findViewById<TextView>(R.id.btn_close)

        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE)
        closeButton.setOnClickListener { dialog.dismiss() }

        var dialogBitmap: Bitmap? = null
        Glide.with(context)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    dialogBitmap = resource.copy(resource.config ?: Bitmap.Config.ARGB_8888, true)
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

    private fun showDownloadDialog(context: Context, imageUrl: String) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_download_image, null)
        val dialog = AlertDialog.Builder(context).setView(view).create()
        val noButton = view.findViewById<TextView>(R.id.noTextView)
        val yesButton = view.findViewById<TextView>(R.id.yesTextView)
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

    private suspend fun downloadBitmap(urlString: String): Bitmap? = withContext(Dispatchers.IO) {
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

    inner class LeftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessage = itemView.findViewById<TextView>(R.id.text_gchat_message_palette)
        private val textTime = itemView.findViewById<TextView>(R.id.text_gchat_time_palette)
        private val imageMessage = itemView.findViewById<android.widget.ImageView>(R.id.chattingCreatedImage)
        private val imageLoading = itemView.findViewById<android.widget.ImageView>(R.id.chattingLoadImage)
        private val cardView = itemView.findViewById<View>(R.id.card_gchat_message_palette)

        fun bind(chat: MessageResponse, isLast: Boolean) {
            if (chat.resource == ChatResource.INTERNAL_IMAGE_LOADING) {
                imageLoading.visibility = View.VISIBLE
                textMessage.visibility = View.GONE
                return
            }
            imageLoading.visibility = View.GONE
            if (chat.resource == ChatResource.IMAGE) {
                textMessage.visibility = View.GONE
                imageMessage.visibility = View.VISIBLE
                Glide.with(itemView)
                    .load(chat.message)
                    .override(600, 900)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageMessage)
                imageMessage.setOnLongClickListener {
                    showDownloadDialog(itemView.context, chat.message)
                    true
                }
                imageMessage.setOnClickListener {
                    showZoomedImageDialog(itemView.context, chat.message)
                }
            } else {
                imageMessage.visibility = View.GONE
                textMessage.visibility = View.VISIBLE
                textMessage.text = chat.message
                textTime.text = formatChatTime(chat.datetime)
                textMessage.setOnLongClickListener {
                    showCopyDialog(itemView.context, textMessage.text, cardView)
                    true
                }
            }
            val lastBorderDrawable = GradientDrawable().apply {
                setColor(ContextCompat.getColor(itemView.context, R.color.darkGray))
                cornerRadius = 16f
            }
            val borderDrawable = GradientDrawable().apply {
                setColor(ContextCompat.getColor(itemView.context, R.color.lightGray))
                cornerRadius = 16f
            }
            if (isLast) {
                textMessage.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                textMessage.background = lastBorderDrawable
            } else {
                textMessage.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                textMessage.background = borderDrawable
            }
        }
    }

    inner class RightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessage = itemView.findViewById<TextView>(R.id.text_gchat_message_me)
        private val textTime = itemView.findViewById<TextView>(R.id.text_gchat_time_me)
        private val cardView = itemView.findViewById<View>(R.id.card_gchat_message_me)

        fun bind(chat: MessageResponse) {
            textMessage.text = chat.message
            textTime.text = formatChatTime(chat.datetime)
            itemView.setOnLongClickListener {
                showCopyDialog(itemView.context, textMessage.text, cardView)
                true
            }
        }
    }

    private fun formatChatTime(datetime: Instant): String {
        val localDateTime = datetime.toLocalDateTime(TimeZone.currentSystemDefault())
        val period = if (localDateTime.hour < 12) "오전" else "오후"
        val hour12 = if (localDateTime.hour % 12 == 0) 12 else localDateTime.hour % 12
        val minute = localDateTime.minute.toString().padStart(2, '0')
        return "$period $hour12:$minute"
    }
}
