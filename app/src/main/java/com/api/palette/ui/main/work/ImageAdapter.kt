package com.api.palette.ui.main.work

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
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
import com.api.palette.ui.util.ContextRetainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageAdapter(private var images: MutableList<String>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: SubsamplingScaleImageView = itemView.findViewById(R.id.imageView)
        private var currentBitmap: Bitmap? = null

        fun bind(imageUrl: String) {
            imageView.recycle()
            currentBitmap?.recycle()
            currentBitmap = null

            Glide.with(itemView.context)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        currentBitmap = resource.copy(resource.config, true)
                        imageView.setImage(ImageSource.bitmap(currentBitmap!!))
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        imageView.recycle()
                        currentBitmap?.recycle()
                        currentBitmap = null
                    }
                })

            imageView.setOnClickListener {
                showZoomedImageDialog(itemView.context, imageUrl)
            }

            imageView.setOnLongClickListener {
                showDownloadDialog(itemView.context, imageUrl)
                true
            }
        }

        fun recycle() {
            imageView.recycle()
            currentBitmap?.recycle()
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

    fun updateImages(newImages: List<String>) {
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
    }

    fun addImages(newImages: List<String>) {
        val previousSize = images.size
        images.addAll(newImages)
        notifyItemRangeInserted(previousSize, newImages.size)
    }

    fun clearImages() {
        val size = images.size
        images.clear()
        notifyItemRangeRemoved(0, size)
    }

    private fun showZoomedImageDialog(context: Context, imageUrl: String) {
        val dialog = Dialog(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.item_zoomed_image_dialog, null)

        val imageView = dialogView.findViewById<SubsamplingScaleImageView>(R.id.imageView)

        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE) // 스케일링 변경

        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    imageView.setImage(ImageSource.bitmap(resource))
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    imageView.recycle()
                }
            })

        dialog.setContentView(dialogView)
        dialog.show()

        dialog.window?.setLayout(
            (context.resources.displayMetrics.widthPixels),
            (context.resources.displayMetrics.heightPixels) // 높이도 전체 화면에 맞추기
        )
    }


    private fun showDownloadDialog(context: Context, imageUrl: String) {
        val dialogBuilder = AlertDialog.Builder(context)

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_download_image, null)
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
                    it.recycle()
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

    private suspend fun downloadBitmap(urlString: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            Glide.with(ContextRetainer.getContext())
                .asBitmap()
                .load(urlString)
                .submit()
                .get()
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
}