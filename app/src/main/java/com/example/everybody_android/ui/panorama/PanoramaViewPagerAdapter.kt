package com.example.everybody_android.ui.panorama

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.astuetz.PagerSlidingTabStrip
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.everybody_android.R
import com.example.everybody_android.convertDpToPx
import com.example.everybody_android.data.response.base.Picture
import com.example.everybody_android.databinding.ItemPanoramaListBinding
import com.example.everybody_android.databinding.ItemPanoramaTabBinding
import com.example.everybody_android.imageLoad

class PanoramaViewPagerAdapter : PagerAdapter(), PagerSlidingTabStrip.CustomTabProvider {

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

    override fun getCustomTabView(parent: ViewGroup?, position: Int): View {
        return parent?.let {
            val bind = ItemPanoramaTabBinding.inflate(LayoutInflater.from(it.context))
            val picture = items[position]
            bind.imgTab.imageLoad(
                picture.imageUrl ?: "",
                RequestOptions().transform(
                    CenterCrop(),
                    RoundedCorners(it.context.convertDpToPx(4))
                ).diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            bind.twCount.text = (position + 1).toString()
            bind.imgTab.isSelected = position == 0
            bind.twCount.isSelected = position == 0
            bind.root
        } ?: return View(parent?.context)
    }

    override fun tabSelected(tab: View?) {
        val imgTab = tab?.findViewById<ImageView>(R.id.img_tab)
        val twCount = tab?.findViewById<TextView>(R.id.tw_count)
        imgTab?.isSelected = true
        twCount?.isSelected = true
    }

    override fun tabUnselected(tab: View?) {
        val imgTab = tab?.findViewById<ImageView>(R.id.img_tab)
        val twCount = tab?.findViewById<TextView>(R.id.tw_count)
        imgTab?.isSelected = false
        twCount?.isSelected = false
    }

}