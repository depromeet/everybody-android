package com.example.everybody_android.ui.panorama

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.example.everybody_android.R
import com.example.everybody_android.data.response.base.Picture
import com.example.everybody_android.databinding.ItemPanoramaListBinding

class PanoramaViewPagerAdapter : PagerAdapter() {

    private val items = mutableListOf<Picture>()
    private val layout = mutableListOf<ItemPanoramaListBinding>()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val bind =
            if (layout.size > position) {
                layout[position]
            } else {
                layout.add(ItemPanoramaListBinding.inflate(LayoutInflater.from(container.context)))
                layout[position]
            }
        bind.data = Item(
            items[position].imageUrl ?: "",
            container.context.getDrawable(R.drawable.test_feed)!!
        )
        container.addView(bind.root)
        return bind.root
    }

    data class Item(
        val imageUrl: String,
        val holder: Drawable
    )

    override fun getCount() = items.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

    }

    fun setItems(item: List<Picture>) {
        items.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

}