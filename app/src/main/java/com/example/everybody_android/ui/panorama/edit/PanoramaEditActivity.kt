package com.example.everybody_android.ui.panorama.edit

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.activity.viewModels
import com.example.everybody_android.BR
import com.example.everybody_android.R
import com.example.everybody_android.adapter.RecyclerItem
import com.example.everybody_android.adapter.RecyclerViewAdapter
import com.example.everybody_android.base.BaseActivity
import com.example.everybody_android.data.response.base.Picture
import com.example.everybody_android.databinding.ActivityPanoramaEditBinding
import com.example.everybody_android.repeatOnStarted
import com.example.everybody_android.typeFace
import com.example.everybody_android.ui.camera.CameraActivity
import com.example.everybody_android.ui.dialog.delete.DeleteDialog
import com.example.everybody_android.ui.panorama.PanoramaItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PanoramaEditActivity : BaseActivity<ActivityPanoramaEditBinding, PanoramaEditViewModel>() {
    override val layoutId: Int = R.layout.activity_panorama_edit
    override val viewModel: PanoramaEditViewModel by viewModels()
    private val whole = mutableListOf<Picture>()
    private val upper = mutableListOf<Picture>()
    private val lower = mutableListOf<Picture>()
    private lateinit var gridAdapter: RecyclerViewAdapter
    private var id = ""
    private var deleteCount = 0

    override fun onResume() {
        super.onResume()
        if (!intent.hasExtra("id")) return finish()
        id = intent.getStringExtra("id") ?: ""
        viewModel.getAlbum(id)
    }

    override fun init() {
        super.init()
        viewModel.onClickEvent(PanoramaEditViewModel.Event.PoseType(1))
        gridAdapter = RecyclerViewAdapter {
            if (it is Unit) {
                startActivity(Intent(this, CameraActivity::class.java).apply {
                    putExtra("id", id)
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
                                .map { (it.data as? Item)?.id ?: "" }
                            deleteItem.forEach { id ->
                                if (id.isNotEmpty()) viewModel.deletePictures(id)
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
                        if (deleteCount == 0) finish()
                    }
                }
            }
        }
    }

    private fun recyclerSetting(data: List<Picture>) {
        gridAdapter.setItems(data.map {
            RecyclerItem(
                Item(
                    it.imageUrl ?: "",
                    this.getDrawable(R.drawable.test_feed)!!,
                    false,
                    it.id.toString()
                ), R.layout.item_panorama_grid_edit, BR.data
            )
        })
        gridAdapter.addItem(0, RecyclerItem(Unit, R.layout.item_panorama_camera, BR.data))
    }

    data class Item(
        val imageUrl: String,
        val holder: Drawable,
        val isCheck: Boolean,
        val id: String
    )

}