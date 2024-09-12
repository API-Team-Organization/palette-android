package com.example.palette.ui.main.work

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.palette.R

class ImageAdapter(private var images: List<String>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(imageUrl: String) {
            Glide.with(itemView.context)
                .load(imageUrl)
                .into(imageView)

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
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_image, null)

        val imageView = dialogView.findViewById<ImageView>(R.id.zoomedImageView)

        Glide.with(context)
            .load(imageUrl)
            .override(900, 1200)
            .into(imageView)

        dialog.setContentView(dialogView)
        dialog.show()
    }
}
