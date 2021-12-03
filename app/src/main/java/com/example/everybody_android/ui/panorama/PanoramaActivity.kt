package com.example.everybody_android.ui.panorama

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.everybody_android.*
import com.example.everybody_android.adapter.RecyclerItem
import com.example.everybody_android.adapter.RecyclerViewAdapter
import com.example.everybody_android.base.BaseActivity
import com.example.everybody_android.data.response.base.Picture
import com.example.everybody_android.databinding.ActivityPanoramaBinding
import com.example.everybody_android.databinding.ItemPanoramaTabBinding
import com.example.everybody_android.ui.panorama.edit.PanoramaEditActivity
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PanoramaActivity : BaseActivity<ActivityPanoramaBinding, PanoramaViewModel>() {
    override val layoutId: Int = R.layout.activity_panorama
    override val viewModel: PanoramaViewModel by viewModels()
    private val whole = mutableListOf<Picture>()
    private val upper = mutableListOf<Picture>()
    private val lower = mutableListOf<Picture>()
    private lateinit var gridAdapter: RecyclerViewAdapter

    override fun onResume() {
        super.onResume()
        viewModel.getAlbum("10")
    }

    override fun init() {
        super.init()
        gridAdapter = RecyclerViewAdapter { }
        binding.recyclerGrid.addItemDecoration(PanoramaItemDecoration())
        binding.recyclerGrid.adapter = gridAdapter
        viewModel.onClickEvent(PanoramaViewModel.Event.PoseType(1))
        repeatOnStarted {
            viewModel.event.collect {
                when (it) {
                    is PanoramaViewModel.Event.Album -> {
                        val data = it.albumResponse
                        data.pictures?.also { list ->
                            whole.clear()
                            upper.clear()
                            lower.clear()
                            whole.addAll(list.whole.orEmpty())
                            upper.addAll(list.upper.orEmpty())
                            lower.addAll(list.lower.orEmpty())
                        }
                        when {
                            binding.twWhole.isSelected -> {
                                viewPagerSetting(whole)
                                recyclerSetting(whole)
                            }
                            binding.twUpper.isSelected -> {
                                viewPagerSetting(upper)
                                recyclerSetting(upper)
                            }
                            binding.twLower.isSelected -> {
                                viewPagerSetting(lower)
                                recyclerSetting(lower)
                            }
                        }
                        binding.twTitle.text = data.name
                    }
                    PanoramaViewModel.Event.Close -> finish()
                    PanoramaViewModel.Event.Edit -> {
                        startActivity(
                            Intent(
                                this@PanoramaActivity,
                                PanoramaEditActivity::class.java
                            )
                        )
                    }
                    PanoramaViewModel.Event.ListType -> {
                        binding.imgListType.isSelected = !binding.imgListType.isSelected
                        binding.recyclerGrid.isVisible = binding.imgListType.isSelected
                        binding.groupPanorama.isVisible = !binding.imgListType.isSelected
                    }
                    is PanoramaViewModel.Event.PoseType -> {
                        binding.twWhole.isSelected = it.type == 1
                        binding.twUpper.isSelected = it.type == 2
                        binding.twLower.isSelected = it.type == 3
                        binding.twWhole.typeface =
                            typeFace(if (it.type == 1) R.font.pretendard_bold else R.font.pretendard_regular)
                        binding.twUpper.typeface =
                            typeFace(if (it.type == 2) R.font.pretendard_bold else R.font.pretendard_regular)
                        binding.twLower.typeface =
                            typeFace(if (it.type == 3) R.font.pretendard_bold else R.font.pretendard_regular)
                        val list = when (it.type) {
                            1 -> whole
                            2 -> upper
                            else -> lower
                        }
                        viewPagerSetting(list)
                        recyclerSetting(list)
                    }
                }
            }
        }
    }

    private fun recyclerSetting(data: List<Picture>) {
        gridAdapter.setItems(data.map {
            RecyclerItem(
                PanoramaViewPagerAdapter.Item(
                    it.imageUrl ?: "",
                    this.getDrawable(R.drawable.test_feed)!!
                ), R.layout.item_panorama_grid, BR.data
            )
        })
    }

    private fun viewPagerSetting(data: List<Picture>) {
        binding.vpPanorama.adapter = PanoramaViewPagerAdapter().apply { setItems(data) }
        binding.vpPanorama.offscreenPageLimit = data.size
        binding.tabPanorama.setViewPager(binding.vpPanorama)
    }

}