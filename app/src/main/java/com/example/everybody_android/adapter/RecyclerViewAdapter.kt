package com.example.everybody_android.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(private val clickCallBack: (Any) -> Unit) :
    RecyclerView.Adapter<RecyclerViewAdapter.BindingViewHolder>() {

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
        holder.itemView.setOnClickListener { clickCallBack.invoke(getItems(position).data) }
        getItems(position).bind(holder.binding)
        holder.binding.executePendingBindings()
    }

    private fun getItems(position: Int): RecyclerItem {
        return items[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(item: List<RecyclerItem>) {
        items.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    inner class BindingViewHolder(
        val binding: ViewDataBinding,
    ) : RecyclerView.ViewHolder(binding.root)
}