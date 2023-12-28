package com.habitude.habit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.habitude.habit.databinding.QuoteImageItemLayoutBinding
import com.habitude.habit.ui.util.DpPxUtils

class QuoteScrollerAdapter(val images: List<String>, val margin: Float, val imageCornerRadius: Float) : RecyclerView.Adapter<QuoteScrollerAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val binding: QuoteImageItemLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            QuoteImageItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val binding = holder.binding
        binding.imageItem.cornerRadius = imageCornerRadius
        Glide.with(binding.imageItem).load(images.get(holder.absoluteAdapterPosition)).into(binding.imageItem)
    }
}