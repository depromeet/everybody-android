package com.def.everybody_android.ui.panorama

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.viewpager.widget.PagerAdapter
import com.def.everybody_android.R
import com.def.everybody_android.data.response.base.Picture
import com.def.everybody_android.databinding.ItemPanoramaListBinding
import com.def.everybody_android.db.MainFeedPictureData

class PanoramaViewPagerAdapter : PagerAdapter() {

    private val items = mutableListOf<MainFeedPictureData>()
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
            items[position].imagePath,
            R.drawable.test_feed
        )
        container.addView(bind.root)
        return bind.root
    }

    data class Item(
        val imageUrl: String,
        @DrawableRes val holder: Int
    )

    override fun getCount() = items.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

    }

    fun setItems(item: List<MainFeedPictureData>) {
        items.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

}