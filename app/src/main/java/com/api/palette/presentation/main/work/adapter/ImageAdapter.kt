package com.api.palette.presentation.main.work.adapter

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.api.palette.R
import com.api.palette.presentation.util.ContextRetainer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import kotlinx.coroutines.*

class ImageAdapter(
    private var images: MutableList<String>,
    private val onActionCompleted: (() -> Unit)? = null
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private var currentDialog: Dialog? = null

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: SubsamplingScaleImageView = itemView.findViewById(R.id.imageView)
        private var currentBitmap: Bitmap? = null

        fun bind(imageUrl: String) {
            recycle()
            loadImage(imageUrl)
            imageView.setOnClickListener { showZoomedImageDialog(itemView.context, imageUrl) }
            imageView.setOnLongClickListener {
                showDownloadOrShareDialog(itemView.context, imageUrl)
                true
            }
        }

        private fun loadImage(url: String) {
            Glide.with(itemView.context)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .load(url)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        currentBitmap = resource.copy(resource.config ?: Bitmap.Config.ARGB_8888, true)
                        imageView.setImage(ImageSource.bitmap(currentBitmap))
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        recycle()
                    }
                })
        }

        fun recycle() {
            imageView.recycle()
            currentBitmap?.takeIf { !it.isRecycled }?.recycle()
            currentBitmap = null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size

    override fun onViewRecycled(holder: ImageViewHolder) {
        super.onViewRecycled(holder)
        holder.recycle()
    }

    fun setImages(newImages: List<String>) {
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
    }

    fun addImages(newImages: List<String>) {
        val start = images.size
        images.addAll(newImages)
        notifyItemRangeInserted(start, newImages.size)
    }

    fun clearImages() {
        val size = images.size
        images.clear()
        notifyItemRangeRemoved(0, size)
    }

    private fun showZoomedImageDialog(context: Context, imageUrl: String) {
        currentDialog?.dismiss()
        val dialog = Dialog(context)
        currentDialog = dialog

        val view = LayoutInflater.from(context).inflate(R.layout.item_zoomed_image_dialog, null)
        val imageView = view.findViewById<SubsamplingScaleImageView>(R.id.imageView)
        val close = view.findViewById<ImageView>(R.id.btn_close)

        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE)
        close.setOnClickListener { dialog.dismiss() }

        var bitmap: Bitmap? = null

        Glide.with(context)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(false)
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource.copy(resource.config ?: Bitmap.Config.ARGB_8888, true)
                    imageView.setImage(ImageSource.bitmap(bitmap))
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    imageView.recycle()
                    bitmap?.takeIf { !it.isRecycled }?.recycle()
                }
            })

        dialog.setOnDismissListener {
            imageView.recycle()
            bitmap?.takeIf { !it.isRecycled }?.recycle()
            currentDialog = null
        }

        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val height = (context.resources.displayMetrics.heightPixels * 0.9).toInt()
        dialog.window?.setLayout(context.resources.displayMetrics.widthPixels, height)
        dialog.show()
    }

    private fun showDownloadOrShareDialog(context: Context, imageUrl: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_download_image, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()

        dialogView.findViewById<TextView>(R.id.noTextView).setOnClickListener {
            coroutineScope.launch { shareImage(context, imageUrl) }
            dialog.dismiss()
        }

        dialogView.findViewById<TextView>(R.id.yesTextView).setOnClickListener {
            coroutineScope.launch {
                downloadBitmap(imageUrl)?.let {
                    saveImageToGallery(context, it)
                    it.takeIf { !it.isRecycled }?.recycle()
                    Toast.makeText(context, "다운로드되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout((context.resources.displayMetrics.widthPixels * 0.9).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private suspend fun shareImage(context: Context, imageUrl: String) = withContext(Dispatchers.IO) {
        try {
            val bitmap = downloadBitmap(imageUrl) ?: return@withContext
            val uri = saveImageToGallery(context, bitmap) ?: return@withContext
            withContext(Dispatchers.Main) {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/jpeg"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, "이미지 공유"))
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "이미지 공유 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun downloadBitmap(url: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            Glide.with(ContextRetainer.context)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .load(url)
                .submit()
                .get()
        } catch (e: Exception) {
            null
        }
    }

    private fun saveImageToGallery(context: Context, bitmap: Bitmap): Uri? {
        if (bitmap.isRecycled) return null

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "palette_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }

        return uri
    }
}
