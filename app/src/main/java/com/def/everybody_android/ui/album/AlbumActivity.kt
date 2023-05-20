package com.def.everybody_android.ui.album

import android.content.Intent
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.def.everybody_android.*
import com.def.everybody_android.adapter.RecyclerItem
import com.def.everybody_android.adapter.RecyclerViewAdapter
import com.def.everybody_android.base.BaseActivity
import com.def.everybody_android.databinding.ActivityAlbumBinding
import com.def.everybody_android.db.MainFeedPictureData
import com.def.everybody_android.dto.Feed
import com.def.everybody_android.ui.album.download.DownloadActivity
import com.def.everybody_android.ui.album.edit.PanoramaEditActivity
import com.def.everybody_android.ui.camera.CameraActivity
import com.def.everybody_android.ui.dialog.album.delete.FolderDeleteDialog
import com.def.everybody_android.ui.dialog.album.edit.FolderEditDialog
import com.def.everybody_android.ui.panorama.PanoramaActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class AlbumActivity : BaseActivity<ActivityAlbumBinding, AlbumViewModel>() {
    override val layoutId: Int = R.layout.activity_album
    override val viewModel: AlbumViewModel by viewModels()
    private val whole = mutableListOf<MainFeedPictureData>()
    private val upper = mutableListOf<MainFeedPictureData>()
    private val lower = mutableListOf<MainFeedPictureData>()
    private var id: Long = -1
    private var albumData: Feed? = null
    private lateinit var gridAdapter: RecyclerViewAdapter


    override fun onResume() {
        super.onResume()
        if (!intent.hasExtra("id")) return finish()
        id = intent.getLongExtra("id", -1)
        viewModel.getAlbum(id)
    }

    override fun init() {
        super.init()
        gridAdapter = RecyclerViewAdapter {
            val index = (it as PanoramaViewPagerAdapter.Item).let { item ->
                gridAdapter.getItems().indexOfFirst { recyclerItem ->
                    (recyclerItem.data as PanoramaViewPagerAdapter.Item).imageUrl == item.imageUrl
                }
            }
            val list = when {
                binding.twWhole.isSelected -> whole
                binding.twUpper.isSelected -> upper
                else -> lower
            }
            startActivity(Intent(this, PanoramaActivity::class.java).apply {
                putExtra("items", list.toTypedArray())
                putExtra("index", index)
                putExtra("title", binding.twTitle.text.toString())
            })

        }
        binding.recyclerGrid.addItemDecoration(PanoramaItemDecoration())
        binding.recyclerGrid.adapter = gridAdapter
        viewModel.onClickEvent(AlbumViewModel.Event.PoseType(1))
        repeatOnStarted {
            viewModel.event.collect {
                when (it) {
                    is AlbumViewModel.Event.Album -> {
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
                            "whole" -> viewModel.onClickEvent(AlbumViewModel.Event.PoseType(1))
                            "upper" -> viewModel.onClickEvent(AlbumViewModel.Event.PoseType(2))
                            "lower" -> viewModel.onClickEvent(AlbumViewModel.Event.PoseType(3))
                        }
                        when {
                            binding.twWhole.isSelected -> {
                                recyclerSetting(whole)
                            }
                            binding.twUpper.isSelected -> {
                                recyclerSetting(upper)
                            }
                            binding.twLower.isSelected -> {
                                recyclerSetting(lower)
                            }
                        }
                        binding.twTitle.text = data.name
                    }
                    AlbumViewModel.Event.Close -> {
                        viewModel.sendingClickEvents("viewAlbum/btn/back")
                        finish()
                    }
                    AlbumViewModel.Event.Edit -> {
                        viewModel.sendingClickEvents("viewAlbum/dropDown/selectPhoto")
                        startActivity(
                            Intent(
                                this@AlbumActivity,
                                PanoramaEditActivity::class.java
                            ).apply {
                                putExtra("id", id)
                            }
                        )
                    }
                    AlbumViewModel.Event.ListType -> {
                        binding.imgListType.isSelected = !binding.imgListType.isSelected
                        binding.recyclerGrid.isVisible = binding.imgListType.isSelected
                    }
                    AlbumViewModel.Event.Camera -> {
                        viewModel.sendingClickEvents("selectPhoto/btn/addPhoto")
                        startActivity(
                            Intent(
                                this@AlbumActivity,
                                CameraActivity::class.java
                            ).apply {
                                putExtra("id", id.toString())
                            }
                        )
                    }
                    is AlbumViewModel.Event.PoseType -> {
                        binding.twWhole.isSelected = it.type == 1
                        binding.twUpper.isSelected = it.type == 2
                        binding.twLower.isSelected = it.type == 3
                        binding.twWhole.typeface =
                            typeFace(if (it.type == 1) R.font.lineseed_bold else R.font.lineseed_regular)
                        binding.twUpper.typeface =
                            typeFace(if (it.type == 2) R.font.lineseed_bold else R.font.lineseed_regular)
                        binding.twLower.typeface =
                            typeFace(if (it.type == 3) R.font.lineseed_bold else R.font.lineseed_regular)
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
                        recyclerSetting(list)
                    }
                    AlbumViewModel.Event.More -> {
                        binding.clMore.isVisible = !binding.clMore.isVisible
                        viewModel.sendingClickEvents("viewAlbum/btn/setting")
                    }
                    AlbumViewModel.Event.AlbumDelete -> FolderDeleteDialog {
                        viewModel.deleteAlbum(id)
                    }.show(supportFragmentManager, "")
                    AlbumViewModel.Event.AlbumEdit -> {
                        albumData?.apply {
                            FolderEditDialog(this) {
                                viewModel.sendingClickEvents("viewAlbum/dropDown/editAlbumName")
                                binding.twTitle.text = it
                                albumData = albumData?.copy(name = it)
                                topToast("앨범이름이 수정되었습니다.")
                            }.show(supportFragmentManager, "")
                        }
                    }
                    AlbumViewModel.Event.Share -> {
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
                        startActivity(Intent(this@AlbumActivity, DownloadActivity::class.java).apply {
                            putExtra("image", img.toTypedArray())
                            putExtra("title", binding.twTitle.text.toString())
                        })
                        viewModel.sendingClickEvents("viewAlbum/dropDown/saveVideo")
                    }
                    AlbumViewModel.Event.DeleteComplete -> {
                        viewModel.sendingClickEvents("viewAlbum/dropDown/deleteAlbum")
                        topToast("앨범이 삭제되었습니다.")
                        finish()
                    }
                }
            }
        }
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
}