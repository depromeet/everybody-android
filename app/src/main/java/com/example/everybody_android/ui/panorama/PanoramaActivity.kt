package com.example.everybody_android.ui.panorama

import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseActivity
import com.example.everybody_android.data.response.base.Picture
import com.example.everybody_android.databinding.ActivityPanoramaBinding
import com.example.everybody_android.repeatOnStarted
import com.example.everybody_android.toast
import com.example.everybody_android.typeFace
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

    private fun viewPagerSetting() {
        binding.vpPanorama.adapter
        binding.tabPanorama.setupWithViewPager(binding.vpPanorama)
        for(i in 0..binding.tabPanorama.tabCount){
            val tab = binding.tabPanorama.getTabAt(i)

        }
        binding.tabPanorama.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                TODO("Not yet implemented")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                TODO("Not yet implemented")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                TODO("Not yet implemented")
            }
        })
    }

}