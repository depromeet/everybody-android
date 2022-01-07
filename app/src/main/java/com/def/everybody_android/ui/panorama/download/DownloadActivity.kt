package com.def.everybody_android.ui.panorama.download

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.viewModels
import com.def.everybody_android.BR
import com.def.everybody_android.R
import com.def.everybody_android.adapter.RecyclerItem
import com.def.everybody_android.adapter.RecyclerViewAdapter
import com.def.everybody_android.base.BaseActivity
import com.def.everybody_android.data.response.base.Picture
import com.def.everybody_android.databinding.ActivityDownloadBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DownloadActivity : BaseActivity<ActivityDownloadBinding, DownloadViewModel>() {
    override val layoutId: Int = R.layout.activity_download
    override val viewModel: DownloadViewModel by viewModels()
    private lateinit var adapter: RecyclerViewAdapter
    private val imageList = mutableListOf<Picture>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!intent.hasExtra("image")) return finish()
        imageList.addAll(intent.getParcelableArrayListExtra<Picture>("image").orEmpty())
        if (imageList.isEmpty()) return finish()
        adapter = RecyclerViewAdapter { }
        adapter.setItems(imageList.mapIndexed { index, picture ->
            RecyclerItem(
                Item(
                    picture.imageUrl ?: "",
                    this.getDrawable(R.drawable.test_feed)!!,
                    (index + 1).toString(),
                    viewModel
                ), R.layout.item_panorama_tab, BR.data
            )
        })
    }

    data class Item(
        val imageUrl: String,
        val holder: Drawable,
        val count: String,
        val viewModel: DownloadViewModel
    )
}