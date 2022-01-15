package com.def.everybody_android.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(private val clickCallBack: (Any) -> Unit) :
    RecyclerView.Adapter<RecyclerViewAdapter.BindingViewHolder>() {

    private val items = arrayListOf<RecyclerItem>()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).layoutId
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
        holder.itemView.setOnClickListener { clickCallBack.invoke(getItem(position).data) }
        getItem(position).bind(holder.binding)
        holder.binding.executePendingBindings()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        this.items.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeData(newItems: List<RecyclerItem>) {
        this.items.clear()
        this.items.addAll(newItems)
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): RecyclerItem {
        return items[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(item: List<RecyclerItem>) {
        items.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    fun deleteItem(item: RecyclerItem) {
        val index = items.indexOf(item)
        if (index > -1) {
            items[index] = item
            notifyItemChanged(index)
        }
    }

    fun changeItem(item: RecyclerItem, position: Int) {
        items[position] = item
        notifyItemChanged(position)
    }

    fun addItem(item: RecyclerItem) {
        items.add(item)
        notifyItemInserted(items.size)
    }

    fun addItem(position: Int, item: RecyclerItem) {
        items.add(position, item)
        notifyItemInserted(items.size)
    }

    fun getItems() = items

    inner class BindingViewHolder(
        val binding: ViewDataBinding,
    ) : RecyclerView.ViewHolder(binding.root)
}