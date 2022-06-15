package com.def.everybody_android.ui.panorama.edit

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import com.def.everybody_android.*
import com.def.everybody_android.adapter.RecyclerItem
import com.def.everybody_android.adapter.RecyclerViewAdapter
import com.def.everybody_android.base.BaseActivity
import com.def.everybody_android.data.response.base.Picture
import com.def.everybody_android.databinding.ActivityPanoramaEditBinding
import com.def.everybody_android.db.MainFeedPictureData
import com.def.everybody_android.ui.camera.CameraActivity
import com.def.everybody_android.ui.dialog.delete.DeleteDialog
import com.def.everybody_android.ui.dialog.service.ServiceDialog
import com.def.everybody_android.ui.panorama.PanoramaItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PanoramaEditActivity : BaseActivity<ActivityPanoramaEditBinding, PanoramaEditViewModel>() {
    override val layoutId: Int = R.layout.activity_panorama_edit
    override val viewModel: PanoramaEditViewModel by viewModels()
    private val whole = mutableListOf<MainFeedPictureData>()
    private val upper = mutableListOf<MainFeedPictureData>()
    private val lower = mutableListOf<MainFeedPictureData>()
    private lateinit var gridAdapter: RecyclerViewAdapter
    private var id: Long = -1
    private var deleteCount = 0

    override fun onResume() {
        super.onResume()
        if (!intent.hasExtra("id")) return finish()
        id = intent.getLongExtra("id", -1)
        viewModel.getAlbum(id)
    }

    override fun init() {
        super.init()
        viewModel.onClickEvent(PanoramaEditViewModel.Event.PoseType(1))
        gridAdapter = RecyclerViewAdapter {
            if (it is Unit) {
                startActivity(Intent(this, CameraActivity::class.java).apply {
                    putExtra("id", id.toString())
                })
            } else if (it is Item) {
                val index = gridAdapter.getItems().indexOfFirst { data -> data.data == it }
                gridAdapter.changeItem(
                    gridAdapter.getItems()[index].copy(data = it.copy(isCheck = !it.isCheck)),
                    index
                )
                val checkCount =
                    gridAdapter.getItems()
                        .filter { data -> (data.data as? Item)?.isCheck ?: false }.size
                if (checkCount == 0) binding.twTitle.text = ""
                else binding.twTitle.text = "${checkCount}장"
                deleteCount = checkCount
            }
        }
        binding.recyclerGrid.addItemDecoration(PanoramaItemDecoration())
        binding.recyclerGrid.adapter = gridAdapter
        repeatOnStarted {
            viewModel.event.collect {
                when (it) {
                    is PanoramaEditViewModel.Event.Album -> {
                        val data = it.album.toFeed()
                        whole.clear()
                        upper.clear()
                        lower.clear()
                        whole.addAll(data.feedPicture.filter { picture -> picture.bodyPart == "whole" })
                        upper.addAll(data.feedPicture.filter { picture -> picture.bodyPart == "upper" })
                        lower.addAll(data.feedPicture.filter { picture -> picture.bodyPart == "lower" })
                        when {
                            binding.twWhole.isSelected -> recyclerSetting(whole)
                            binding.twUpper.isSelected -> recyclerSetting(upper)
                            binding.twLower.isSelected -> recyclerSetting(lower)
                        }
                    }
                    PanoramaEditViewModel.Event.Close -> finish()
                    PanoramaEditViewModel.Event.Delete -> {
                        DeleteDialog(deleteCount) {
                            val deleteItem = gridAdapter.getItems()
                                .filter { data -> (data.data as? Item)?.isCheck ?: false }
                                .map { (it.data as? Item)?.imageUrl ?: "" }
                            deleteItem.forEach { path ->
                                if (path.isNotEmpty()) viewModel.deletePictures(id, path)
                            }
                        }.show(supportFragmentManager, "")
                    }
                    is PanoramaEditViewModel.Event.PoseType -> {
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
                        recyclerSetting(list)
                    }
                    PanoramaEditViewModel.Event.DeleteComplete -> {
                        deleteCount--
                        if (deleteCount == 0) {
                            toast(R.drawable.ic_toast_delete)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun recyclerSetting(data: List<MainFeedPictureData>) {
        gridAdapter.setItems(data.map {
            RecyclerItem(
                Item(
                    it.imagePath,
                    R.drawable.test_feed,
                    false
                ), R.layout.item_panorama_grid_edit, BR.data
            )
        })
        gridAdapter.addItem(0, RecyclerItem(Unit, R.layout.item_panorama_camera, BR.data))
    }

    data class Item(
        val imageUrl: String,
        @DrawableRes val holder: Int,
        val isCheck: Boolean
    )

}