package com.def.everybody_android.ui.picture.folder

import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.def.everybody_android.BR
import com.def.everybody_android.R
import com.def.everybody_android.adapter.RecyclerItem
import com.def.everybody_android.adapter.RecyclerViewAdapter
import com.def.everybody_android.base.BaseFragment
import com.def.everybody_android.databinding.FragmentFolderChoiceBinding
import com.def.everybody_android.repeatOnStarted
import com.def.everybody_android.toast
import com.def.everybody_android.ui.dialog.folder.FolderAddDialog
import com.def.everybody_android.ui.picture.PictureActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FolderChoiceFragment : BaseFragment<FragmentFolderChoiceBinding, FolderChoiceViewModel>() {
    override val viewModel: FolderChoiceViewModel by viewModels()
    override val layoutId: Int = R.layout.fragment_folder_choice
    private lateinit var adapter: RecyclerViewAdapter

    override fun init() {
        viewModel.getAlbums()
        adapter = RecyclerViewAdapter {
            if (it is Unit) {
                FolderAddDialog { data ->
                    adapter.addItem(
                        RecyclerItem(
                            FolderChoiceViewModel.Item(
                                "",
                                data.name ?: "",
                                ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.test_feed,
                                    null
                                )!!,
                                false,
                                data.id,
                                data.description ?: ""
                            ),
                            R.layout.item_folder_choice,
                            BR.data
                        )
                    )
                }.show(childFragmentManager, "")
            } else if (it is FolderChoiceViewModel.Item) {
                val checkIndex = adapter.getItems()
                    .indexOfFirst { data ->
                        if (data.data is FolderChoiceViewModel.Item) data.data.isCheck
                        else false
                    }
                if (checkIndex > -1) {
                    val checkData =
                        adapter.getItems()[checkIndex].data as FolderChoiceViewModel.Item
                    adapter.changeItem(
                        adapter.getItems()[checkIndex].copy(
                            data = checkData.copy(
                                isCheck = !checkData.isCheck
                            )
                        ), checkIndex
                    )
                    viewModel.valueMap["album_id"] = ""
                }
                val index = adapter.getItems().indexOfFirst { data -> data.data == it }
                if (index > -1) {
                    val data = adapter.getItems()[index]
                    viewModel.valueMap["album_id"] = it.id.toString()
                    adapter.changeItem(data.copy(data = it.copy(isCheck = !it.isCheck)), index)
                }
            }
        }
        binding.recyclerFolder.addItemDecoration(FolderItemDecoration())
        binding.recyclerFolder.adapter = adapter
        repeatOnStarted {
            viewModel.albumsResponse.collect {
                val item = it.map { data ->
                    val image = data.thumbnailUrl
                    val recyclerData = FolderChoiceViewModel.Item(
                        image ?: "",
                        data.name ?: "",
                        ResourcesCompat.getDrawable(resources, R.drawable.test_feed, null)!!,
                        false,
                        data.id,
                        data.description ?: ""
                    )
                    RecyclerItem(
                        recyclerData,
                        R.layout.item_folder_choice,
                        BR.data
                    )
                }.toMutableList()
                item.add(0, RecyclerItem(Unit, R.layout.item_folder_add, BR.data))
                adapter.setItems(item)
            }
        }
    }

    fun getValue() {
        val map = viewModel.valueMap
        if (map.containsKey("album_id")) {
            activity?.apply {
                if (this is PictureActivity) {
                    photoUpload(map)
                }
            }
        } else context?.toast("앨범을 선택해주세요.")
    }

    fun setValue(map: HashMap<String, String>) {
        viewModel.valueMap = map
    }

}