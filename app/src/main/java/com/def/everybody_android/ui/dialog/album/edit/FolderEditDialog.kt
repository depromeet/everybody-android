package com.def.everybody_android.ui.dialog.album.edit

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.def.everybody_android.R
import com.def.everybody_android.base.BaseDialogFragment
import com.def.everybody_android.databinding.DialogFolderEditBinding
import com.def.everybody_android.dto.Feed
import com.def.everybody_android.repeatOnStarted
import kotlinx.coroutines.flow.collect

class FolderEditDialog(private val album: Feed, private val callBack: (String) -> Unit) :
    BaseDialogFragment<DialogFolderEditBinding, FolderEditViewModel>() {
    override val viewModel: FolderEditViewModel by viewModels()
    override val layoutId: Int = R.layout.dialog_folder_edit
    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etName.setText(album.name)
    }

    override fun init() {
        repeatOnStarted {
            viewModel.event.collect {
                when (it) {
                    FolderEditViewModel.Event.Cancel -> dismiss()
                    FolderEditViewModel.Event.Complete -> viewModel.editAlbums(album.id)
                    is FolderEditViewModel.Event.Album -> {
                        callBack.invoke(it.name)
                        dismiss()
                    }
                }
            }
        }
    }


}