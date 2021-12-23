package com.def.everybody_android.ui.panorama

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.*
import androidx.viewpager.widget.ViewPager
import com.def.everybody_android.*
import com.def.everybody_android.adapter.RecyclerItem
import com.def.everybody_android.adapter.RecyclerViewAdapter
import com.def.everybody_android.base.BaseActivity
import com.def.everybody_android.data.response.base.Picture
import com.def.everybody_android.databinding.ActivityPanoramaBinding
import com.def.everybody_android.ui.camera.CameraActivity
import com.def.everybody_android.ui.dialog.service.ServiceDialog
import com.def.everybody_android.ui.panorama.edit.PanoramaEditActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class PanoramaActivity : BaseActivity<ActivityPanoramaBinding, PanoramaViewModel>() {
    override val layoutId: Int = R.layout.activity_panorama
    override val viewModel: PanoramaViewModel by viewModels()
    private val whole = mutableListOf<Picture>()
    private val upper = mutableListOf<Picture>()
    private val lower = mutableListOf<Picture>()
    private var id: String = ""
    private lateinit var gridAdapter: RecyclerViewAdapter
    private lateinit var panoramaAdapter: RecyclerViewAdapter

    override fun onResume() {
        super.onResume()
        if (!intent.hasExtra("id")) return finish()
        id = intent.getStringExtra("id") ?: ""
        viewModel.getAlbum(id)
    }

    override fun init() {
        super.init()
        gridAdapter = RecyclerViewAdapter { }
        panoramaAdapter = RecyclerViewAdapter { }
        binding.recyclerGrid.addItemDecoration(PanoramaItemDecoration())
        binding.recyclerGrid.adapter = gridAdapter
        viewModel.onClickEvent(PanoramaViewModel.Event.PoseType(1))
        settingPanorama()
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
                        when (data.latestPart) {
                            "whole" -> viewModel.onClickEvent(PanoramaViewModel.Event.PoseType(1))
                            "upper" -> viewModel.onClickEvent(PanoramaViewModel.Event.PoseType(2))
                            "lower" -> viewModel.onClickEvent(PanoramaViewModel.Event.PoseType(3))
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
                            ).apply {
                                putExtra("id", id)
                            }
                        )
                    }
                    PanoramaViewModel.Event.ListType -> {
                        binding.imgListType.isSelected = !binding.imgListType.isSelected
                        binding.recyclerGrid.isVisible = binding.imgListType.isSelected
                        binding.groupPanorama.isVisible = !binding.imgListType.isSelected
                    }
                    PanoramaViewModel.Event.Camera -> {
                        startActivity(
                            Intent(
                                this@PanoramaActivity,
                                CameraActivity::class.java
                            ).apply {
                                putExtra("id", id)
                            }
                        )
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
                    PanoramaViewModel.Event.Share -> ServiceDialog().show(
                        supportFragmentManager,
                        ""
                    )
                }
            }
        }
    }

    private fun settingPanorama() {
        var currentPosition = RecyclerView.NO_POSITION
        binding.recyclerPanorama.adapter = panoramaAdapter
        binding.recyclerPanorama.addItemDecoration(OffsetItemDecoration(this))
        binding.recyclerPanorama.itemAnimator?.apply {
            (this as SimpleItemAnimator).supportsChangeAnimations = false
        }
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerPanorama)
        val vpListener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                (binding.recyclerPanorama.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                    position,
                    (screenWidth / 2) + convertDpToPx(12)
                )
                if (currentPosition != position) {
                    currentPosition = position
                    val index =
                        panoramaAdapter.getItems().indexOfFirst { (it.data as Item).isEnable }
                    if (index > -1) {
                        val item = panoramaAdapter.getItems()[index]
                        panoramaAdapter.changeItem(
                            item.copy(
                                data = (item.data as Item).copy(
                                    isEnable = false
                                )
                            ), index
                        )
                    }
                    val item = panoramaAdapter.getItems()[position]
                    panoramaAdapter.changeItem(
                        item.copy(
                            data = (item.data as Item).copy(
                                isEnable = true
                            )
                        ), position
                    )
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}

        }
        val recyclerListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.layoutManager != null) {
                    val view = snapHelper.findSnapView(recyclerView.layoutManager)!!
                    val position = recyclerView.layoutManager!!.getPosition(view)
                    if (currentPosition != position) {
                        binding.vpPanorama.removeOnPageChangeListener(vpListener)
                        currentPosition = position
                        val index =
                            panoramaAdapter.getItems().indexOfFirst { (it.data as Item).isEnable }
                        if (index > -1) {
                            val item = panoramaAdapter.getItems()[index]
                            panoramaAdapter.changeItem(
                                item.copy(
                                    data = (item.data as Item).copy(
                                        isEnable = false
                                    )
                                ), index
                            )
                        }
                        val item = panoramaAdapter.getItems()[position]
                        panoramaAdapter.changeItem(
                            item.copy(
                                data = (item.data as Item).copy(
                                    isEnable = true
                                )
                            ), position
                        )
                        binding.vpPanorama.setCurrentItem(position, false)
                        binding.vpPanorama.addOnPageChangeListener(vpListener)
                    }
                }
            }
        }
        binding.recyclerPanorama.addOnScrollListener(recyclerListener)
        binding.vpPanorama.addOnPageChangeListener(vpListener)
    }

    private val screenWidth: Int
        get() {
            val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size.x
        }

    private fun recyclerSetting(data: List<Picture>) {
        binding.groupEmpty.isVisible = data.isEmpty()
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
        panoramaAdapter.setItems(data.mapIndexed { index, picture ->
            RecyclerItem(
                Item(
                    picture.imageUrl ?: "",
                    this.getDrawable(R.drawable.test_feed)!!,
                    index == 0,
                    (index + 1).toString()
                ), R.layout.item_panorama_tab, BR.data
            )
        })
    }

    data class Item(
        val imageUrl: String,
        val holder: Drawable,
        val isEnable: Boolean,
        val count: String
    )
}