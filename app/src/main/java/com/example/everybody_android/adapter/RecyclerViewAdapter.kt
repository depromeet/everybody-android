package com.example.everybody_android.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.BindingViewHolder>() {

    private val items = arrayListOf<RecyclerItem>()

    private lateinit var recyclerItem: RecyclerItem

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return getItems(position).layoutId
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding = DataBindingUtil.inflate(inflater, viewType, parent, false)
        return BindingViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder,
        position: Int
    ) {
        getItems(position).bind(holder.binding)
        holder.binding.executePendingBindings()
    }

    private fun getItems(position: Int): RecyclerItem {
        return items[position]
    }

    inner class BindingViewHolder(
        val binding: ViewDataBinding,
    ) : RecyclerView.ViewHolder(binding.root)
}