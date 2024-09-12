package com.example.palette.ui.main.work

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.example.palette.R

class ImageAdapter(private var images: List<String>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: SubsamplingScaleImageView = itemView.findViewById(R.id.imageView)

        fun bind(imageUrl: String) {
            Glide.with(itemView.context)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        imageView.setImage(ImageSource.bitmap(resource))
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })

            imageView.setOnClickListener {
                showZoomedImageDialog(itemView.context, imageUrl)
            }
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

    fun updateImages(newImages: List<String>) {
        images = newImages
        notifyDataSetChanged()
    }

    private fun showZoomedImageDialog(context: Context, imageUrl: String) {
        val dialog = Dialog(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.item_image, null)

        val imageView = dialogView.findViewById<SubsamplingScaleImageView>(R.id.imageView)
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    imageView.setImage(ImageSource.bitmap(resource))
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })

        dialog.setContentView(dialogView)
        dialog.show()
    }
}
