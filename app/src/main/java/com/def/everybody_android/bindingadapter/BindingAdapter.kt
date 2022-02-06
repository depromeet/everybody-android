package com.def.everybody_android.bindingadapter

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.def.everybody_android.adapter.RecyclerViewAdapter
import com.def.everybody_android.convertDpToPx
import com.def.everybody_android.dto.response.MainFeedResponse

@BindingAdapter("recyclerView")
fun RecyclerView.setRecyclerAdapter(adapter: RecyclerViewAdapter) {
    this.adapter = adapter
    layoutManager = LinearLayoutManager(context)
}

@BindingAdapter("halfRecyclerView")
fun RecyclerView.setHalfRecyclerAdapter(adapter: RecyclerViewAdapter) {
    this.adapter = adapter
    layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false).apply {
        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (position) {
                    (adapter.itemCount - 1) -> 2
                    else -> 1
                }
            }
        }
    }
}

@BindingAdapter("url", "holder")
fun loadImage(imageView: ImageView, url: MainFeedResponse, placeholder: Drawable) {
    if (url.thumbnailUrl.isNullOrEmpty()) imageView.setImageDrawable(placeholder)
    else {
        Glide.with(imageView.context)
            .load(url.thumbnailUrl)
            .placeholder(placeholder)
            .error(placeholder)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .apply(RequestOptions().fitCenter())
            .transform(CenterCrop(), RoundedCorners(imageView.context.convertDpToPx(4)))
            .into(imageView)
    }
}

@BindingAdapter("feedUrl", "defaultImage")
fun feedImage(imageView: ImageView, url: String, placeholder: Drawable) {
    println("urlurl $url")
    if (url.isEmpty()) imageView.setImageDrawable(placeholder)
    else {
        Glide.with(imageView.context)
            .load(url)
            .placeholder(placeholder)
            .error(placeholder)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .apply(RequestOptions().fitCenter())
            .into(imageView)
    }
}