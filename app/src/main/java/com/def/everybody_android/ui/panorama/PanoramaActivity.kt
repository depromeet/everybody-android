package com.def.everybody_android.ui.panorama

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager.widget.ViewPager
import com.def.everybody_android.*
import com.def.everybody_android.adapter.RecyclerItem
import com.def.everybody_android.adapter.RecyclerViewAdapter
import com.def.everybody_android.base.BaseActivity
import com.def.everybody_android.databinding.ActivityPanoramaBinding
import com.def.everybody_android.db.MainFeedPictureData
import com.def.everybody_android.ui.album.PanoramaViewPagerAdapter
import com.def.everybody_android.ui.dialog.message.MessageDialog
import kotlinx.coroutines.flow.collect

class PanoramaActivity : BaseActivity<ActivityPanoramaBinding, PanoramaViewModel>() {
    override val layoutId: Int = R.layout.activity_panorama
    override val viewModel: PanoramaViewModel by viewModels()
    private lateinit var panoramaAdapter: RecyclerViewAdapter
    private val screenWidth: Int
        get() {
            val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                this.display
            } else {
                wm.defaultDisplay
            }
            val size = Point()
            display?.getSize(size)
            return size.x
        }
    private val items = arrayListOf<MainFeedPictureData>()
    private var selectedIndex = 0

    override fun init() {
        super.init()
        panoramaAdapter = RecyclerViewAdapter { }
        selectedIndex = intent.getIntExtra("index", 0)
        items.addAll(intent.getParcelableArrayExtra("items").orEmpty().map { it as MainFeedPictureData })
        binding.twTitle.text = intent.getStringExtra("title").orEmpty()
        settingPanorama()
        viewPagerSetting(items)
        repeatOnStarted {
            viewModel.event.collect {
                when (it) {
                    PanoramaViewModel.Event.Close -> finish()
                    PanoramaViewModel.Event.Delete -> {
                        MessageDialog(true) {
                            val index = panoramaAdapter.getItems().indexOfFirst { item -> (item.data as Item).isEnable }
                            if (index > -1) {
                                val item = items[index]
                                viewModel.deletePictures(item.albumId, item.imagePath)
                            }
                        }.setMessage("사진을 삭제하시겠습니까?", "삭제를 누르면 사진이 완전히 삭제됩니다.").show(supportFragmentManager, "")
                    }
                    PanoramaViewModel.Event.DeleteComplete -> {
                        val index = panoramaAdapter.getItems().indexOfFirst { item -> (item.data as Item).isEnable }
                        if (index > -1) {
                            topToast("사진이 삭제되었습니다.")
                            items.removeAt(index)
                            panoramaAdapter.deleteItem(index)
                            if (selectedIndex > items.lastIndex) selectedIndex = -1
                            viewPagerSetting(items)
                            if (items.size == 0) finish()
                        }
                    }
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
                    val view = snapHelper.findSnapView(recyclerView.layoutManager) ?: return
                    val position = recyclerView.layoutManager!!.getPosition(view)
                    if (currentPosition != position) {
                        binding.vpPanorama.removeOnPageChangeListener(vpListener)
                        currentPosition = position
                        val index =
                            panoramaAdapter.getItems().indexOfFirst { (it.data as Item).isEnable }
                        if (index > -1) {
                            val item = panoramaAdapter.getItems()[index]
                            recyclerView.post {
                                panoramaAdapter.changeItem(
                                    item.copy(
                                        data = (item.data as Item).copy(
                                            isEnable = false
                                        )
                                    ), index
                                )
                            }
                        }
                        val item = panoramaAdapter.getItems()[position]
                        recyclerView.post {
                            panoramaAdapter.changeItem(
                                item.copy(
                                    data = (item.data as Item).copy(
                                        isEnable = true
                                    )
                                ), position
                            )
                        }
                        binding.vpPanorama.setCurrentItem(position, false)
                        binding.vpPanorama.addOnPageChangeListener(vpListener)
                    }
                }
            }
        }
        binding.recyclerPanorama.addOnScrollListener(recyclerListener)
        binding.vpPanorama.addOnPageChangeListener(vpListener)
    }

    private fun viewPagerSetting(data: List<MainFeedPictureData>) {
        binding.vpPanorama.adapter = PanoramaViewPagerAdapter().apply { setItems(data) }
        binding.vpPanorama.offscreenPageLimit = data.size
        panoramaAdapter.setItems(data.mapIndexed { index, picture ->
            RecyclerItem(
                Item(
                    picture.imagePath,
                    R.drawable.test_feed,
                    index == selectedIndex,
                    (index + 1).toString()
                ), R.layout.item_panorama_tab, BR.data
            )
        })
        binding.vpPanorama.postDelayed({ binding.vpPanorama.setCurrentItem(selectedIndex, false) }, 300)
    }

    data class Item(
        val imageUrl: String,
        @DrawableRes val holder: Int,
        val isEnable: Boolean,
        val count: String
    )
}