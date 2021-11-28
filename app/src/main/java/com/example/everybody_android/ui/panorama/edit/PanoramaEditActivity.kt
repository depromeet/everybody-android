package com.example.everybody_android.ui.panorama.edit

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
    override fun init() {
        super.init()
        viewModel.getAlbum("10")
        gridAdapter = RecyclerViewAdapter {
            if (it is Unit) {

            } else if (it is Item) {
                val index = gridAdapter.getItems().indexOfFirst { data -> data.data == it }
                gridAdapter.changeItem(
                    gridAdapter.getItems()[index].copy(data = it.copy(isCheck = !it.isCheck)),
                    index
                )
                val checkCount =
                    gridAdapter.getItems().filter { data -> (data.data as? Item)?.isCheck ?: false }.size
                if (checkCount == 0) binding.twTitle.text = ""
                else binding.twTitle.text = "${checkCount}장"
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
                            whole.addAll(list.whole.orEmpty())
                            upper.addAll(list.upper.orEmpty())
                            lower.addAll(list.lower.orEmpty())
                        }
                        recyclerSetting(whole)
                        viewModel.onClickEvent(PanoramaEditViewModel.Event.PoseType(1))
                    }
                    PanoramaEditViewModel.Event.Close -> finish()
                    PanoramaEditViewModel.Event.Delete -> {

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
                            2 -> whole
                            3 -> upper
                            else -> lower
                        }
                        recyclerSetting(list)
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
                    false
                ), R.layout.item_panorama_grid_edit, BR.data
            )
        })
        gridAdapter.addItem(0, RecyclerItem(Unit, R.layout.item_panorama_camera, BR.data))
    }

    data class Item(
        val imageUrl: String,
        val holder: Drawable,
        val isCheck: Boolean
    )

}