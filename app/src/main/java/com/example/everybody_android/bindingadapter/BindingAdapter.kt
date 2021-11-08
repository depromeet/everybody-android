package com.example.everybody_android.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.everybody_android.adapter.RecyclerViewAdapter

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