package com.def.everybody_android.ui.panorama

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.*
import androidx.viewpager.widget.ViewPager
import com.def.everybody_android.*
import com.def.everybody_android.adapter.RecyclerItem
import com.def.everybody_android.adapter.RecyclerViewAdapter
import com.def.everybody_android.base.BaseActivity
import com.def.everybody_android.databinding.ActivityPanoramaBinding
import com.def.everybody_android.db.MainFeedPictureData
import com.def.everybody_android.dto.Feed
import com.def.everybody_android.ui.camera.CameraActivity
import com.def.everybody_android.ui.dialog.album.delete.FolderDeleteDialog
import com.def.everybody_android.ui.dialog.album.edit.FolderEditDialog
import com.def.everybody_android.ui.panorama.download.DownloadActivity
import com.def.everybody_android.ui.panorama.edit.PanoramaEditActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class PanoramaActivity : BaseActivity<ActivityPanoramaBinding, PanoramaViewModel>() {
    override val layoutId: Int = R.layout.activity_panorama
    override val viewModel: PanoramaViewModel by viewModels()
    private val whole = mutableListOf<MainFeedPictureData>()
    private val upper = mutableListOf<MainFeedPictureData>()
    private val lower = mutableListOf<MainFeedPictureData>()
    private var id: Long = -1
    private var albumData: Feed? = null
    private lateinit var gridAdapter: RecyclerViewAdapter
    private lateinit var panoramaAdapter: RecyclerViewAdapter

    override fun onResume() {
        super.onResume()
        if (!intent.hasExtra("id")) return finish()
        id = intent.getLongExtra("id", -1)
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
                        val data = it.album.toFeed()
                        albumData = data
                        whole.clear()
                        upper.clear()
                        lower.clear()
                        whole.addAll(data.feedPicture.filter { picture -> picture.bodyPart == "whole" })
                        upper.addAll(data.feedPicture.filter { picture -> picture.bodyPart == "upper" })
                        lower.addAll(data.feedPicture.filter { picture -> picture.bodyPart == "lower" })
                        val latestPart = when {
                            whole.isNotEmpty() -> "whole"
                            upper.isNotEmpty() -> "upper"
                            lower.isNotEmpty() -> "lower"
                            else -> "whole"
                        }
                        when (latestPart) {
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
                    PanoramaViewModel.Event.Close -> {
                        viewModel.sendingClickEvents("viewAlbum/btn/back")
                        finish()
                    }
                    PanoramaViewModel.Event.Edit -> {
                        viewModel.sendingClickEvents("viewAlbum/dropDown/selectPhoto")
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
                        viewModel.sendingClickEvents("selectPhoto/btn/addPhoto")
                        startActivity(
                            Intent(
                                this@PanoramaActivity,
                                CameraActivity::class.java
                            ).apply {
                                putExtra("id", id.toString())
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
                            1 -> {
                                viewModel.sendingClickEvents("selectPhoto/tab/whole")
                                whole
                            }
                            2 -> {
                                viewModel.sendingClickEvents("selectPhoto/tab/upper")
                                upper
                            }
                            else -> {
                                viewModel.sendingClickEvents("selectPhoto/tab/lower")
                                lower
                            }
                        }
                        viewPagerSetting(list)
                        recyclerSetting(list)
                    }
                    PanoramaViewModel.Event.More -> {
                        binding.clMore.isVisible = !binding.clMore.isVisible
                        viewModel.sendingClickEvents("viewAlbum/btn/setting")
                    }
                    PanoramaViewModel.Event.AlbumDelete -> FolderDeleteDialog {
                        viewModel.deleteAlbum(id)
                    }.show(supportFragmentManager, "")
                    PanoramaViewModel.Event.AlbumEdit -> {
                        albumData?.apply {
                            FolderEditDialog(this) {
                                viewModel.sendingClickEvents("viewAlbum/dropDown/editAlbumName")
                                binding.twTitle.text = it
                                albumData = albumData?.copy(name = it)
                                topToast("앨범이름이 수정되었습니다.")
                            }.show(supportFragmentManager, "")
                        }
                    }
                    PanoramaViewModel.Event.Share -> {
                        val img = when {
                            binding.twWhole.isSelected -> whole
                            binding.twUpper.isSelected -> upper
                            else -> lower
                        }
                        if (img.isEmpty()) {
                            toast("비어있는 사진첩입니다.")
                            return@collect
                        }
                        if (img.size < 2) return@collect topToast("사진이 최소 2장이상 필요해요.")
                        startActivity(Intent(this@PanoramaActivity, DownloadActivity::class.java).apply {
                            putExtra("image", img.toTypedArray())
                            putExtra("title", binding.twTitle.text.toString())
                        })
                        viewModel.sendingClickEvents("viewAlbum/dropDown/saveVideo")
                    }
                    PanoramaViewModel.Event.DeleteComplete -> {
                        viewModel.sendingClickEvents("viewAlbum/dropDown/deleteAlbum")
                        topToast("앨범이 삭제되었습니다.")
                        finish()
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

    private val screenWidth: Int
        get() {
            val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size.x
        }

    private fun recyclerSetting(data: List<MainFeedPictureData>) {
        binding.groupEmpty.isVisible = data.isEmpty()
        gridAdapter.setItems(data.map {
            RecyclerItem(
                PanoramaViewPagerAdapter.Item(
                    it.imagePath,
                    R.drawable.test_feed
                ), R.layout.item_panorama_grid, BR.data
            )
        })
    }

    private fun viewPagerSetting(data: List<MainFeedPictureData>) {
        binding.vpPanorama.adapter = PanoramaViewPagerAdapter().apply { setItems(data) }
        binding.vpPanorama.offscreenPageLimit = data.size
        panoramaAdapter.setItems(data.mapIndexed { index, picture ->
            RecyclerItem(
                Item(
                    picture.imagePath,
                    R.drawable.test_feed,
                    index == 0,
                    (index + 1).toString()
                ), R.layout.item_panorama_tab, BR.data
            )
        })
    }

    data class Item(
        val imageUrl: String,
        @DrawableRes val holder: Int,
        val isEnable: Boolean,
        val count: String
    )
}