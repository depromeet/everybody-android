package com.example.everybody_android.bindingadapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.everybody_android.R
import com.example.everybody_android.adapter.RecyclerViewAdapter
import com.example.everybody_android.dto.MainFeedPicturePositionData
import com.example.everybody_android.dto.UserData

@BindingAdapter("recyclerView")
fun RecyclerView.setRecyclerAdapter(adapter: RecyclerViewAdapter) {
    this.adapter = adapter
    layoutManager = LinearLayoutManager(context)
}

@BindingAdapter("halfRecyclerView",)
fun RecyclerView.setHalfRecyclerAdapter(adapter : RecyclerViewAdapter){
    this.adapter = adapter
    layoutManager = GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false)
}

@BindingAdapter("url","holder")
fun loadImage(imageView: ImageView, url: MainFeedPicturePositionData, placeholder: Drawable){
    if(url.upper.isNullOrEmpty()) imageView.setImageDrawable(placeholder)

    else{
        Glide.with(imageView.context)
            .load(url.upper[0].imageUrl)
            .placeholder(placeholder)
            .error(placeholder)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .apply(RequestOptions().fitCenter())
            .into(imageView)
    }
}

@BindingAdapter("feedUrl","defaultImage")
fun feedImage(imageView: ImageView, url: String, placeholder: Drawable){
    println("urlurl $url")
    if(url.isEmpty()) imageView.setImageDrawable(placeholder)

    else{
        Glide.with(imageView.context)
            .load(url)
            .placeholder(placeholder)
            .error(placeholder)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .apply(RequestOptions().fitCenter())
            .into(imageView)
    }
}