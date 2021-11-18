package com.example.everybody_android.ui.panorama

import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.everybody_android.*
import com.example.everybody_android.base.BaseActivity
import com.example.everybody_android.data.response.base.Picture
import com.example.everybody_android.databinding.ActivityPanoramaBinding
import com.example.everybody_android.databinding.ItemPanoramaTabBinding
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

    override fun init() {
        super.init()
        if (!intent.hasExtra("id")) finish()
        viewModel.getAlbum(intent.getStringExtra("id") ?: "")



        repeatOnStarted {
            viewModel.event.collect {
                when (it) {
                    is PanoramaViewModel.Event.Album -> {
                        val data = it.albumResponse
                        data.pictures?.also { list ->
                            whole.addAll(list.whole.orEmpty())
                            upper.addAll(list.upper.orEmpty())
                            lower.addAll(list.lower.orEmpty())
                        }

                    }
                    PanoramaViewModel.Event.Close -> finish()
                    PanoramaViewModel.Event.Edit -> toast("수정 클릭")
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
                    }
                }
            }
        }
    }

    private fun viewPagerSetting(data: List<Picture>) {
        binding.vpPanorama.adapter
        binding.tabPanorama.setupWithViewPager(binding.vpPanorama)
        for (i in 0..binding.tabPanorama.tabCount) {
            val tab = binding.tabPanorama.getTabAt(i) ?: continue
            val bind = ItemPanoramaTabBinding.inflate(LayoutInflater.from(this), null, false)
            bind.imgTab.imageLoad(
                data[i].imageUrl ?: "",
                RequestOptions().transform(
                    CenterCrop(),
                    RoundedCorners(convertDpToPx(4))
                ).diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            bind.twCount.text = (i + 1).toString()
            tab.customView = bind.root

        }
        binding.tabPanorama.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.customView?.apply {
                    val bind = ItemPanoramaTabBinding.bind(this)
                    bind.imgTab.isSelected = true
                    bind.twCount.isSelected = true
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView?.apply {
                    val bind = ItemPanoramaTabBinding.bind(this)
                    bind.imgTab.isSelected = false
                    bind.twCount.isSelected = false
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.customView?.apply {
                    val bind = ItemPanoramaTabBinding.bind(this)
                    bind.imgTab.isSelected = true
                    bind.twCount.isSelected = true
                }
            }
        })
    }

}