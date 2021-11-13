package com.example.everybody_android.ui.picture.folder

import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.example.everybody_android.BR
import com.example.everybody_android.R
import com.example.everybody_android.adapter.RecyclerItem
import com.example.everybody_android.adapter.RecyclerViewAdapter
import com.example.everybody_android.base.BaseFragment
import com.example.everybody_android.databinding.FragmentFolderChoiceBinding
import com.example.everybody_android.repeatOnStarted
import com.example.everybody_android.ui.dialog.folder.FolderAddDialog
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
                                ResourcesCompat.getDrawable(resources, R.drawable.ic_add, null)!!,
                                false,
                                data.hashCode(),
                                data.description ?: ""
                            ),
                            R.layout.item_folder_choice,
                            BR.data
                        )
                    )
                }.show(childFragmentManager, "")
            } else if (it is FolderChoiceViewModel.Item) {
                val index = adapter.getItems().indexOfFirst { data -> data.data == it }
                val data = adapter.getItems()[index]
                adapter.changeItem(data.copy(data = it.copy(isCheck = !it.isCheck)), index)
            }
        }
        binding.recyclerFolder.addItemDecoration(FolderItemDecoration())
        binding.recyclerFolder.adapter = adapter
        repeatOnStarted {
            viewModel.albumsResponse.collect {
                val item = it.map { data ->
                    val image =
                        if (data.pictures.upper.isNullOrEmpty()) "" else data.pictures.upper[0].imageUrl
                    val recyclerData = FolderChoiceViewModel.Item(
                        image ?: "",
                        data.name ?: "",
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_add, null)!!,
                        false,
                        data.hashCode(),
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
}