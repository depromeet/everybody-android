package com.def.everybody_android.ui.dialog.folder

import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.def.everybody_android.R
import com.def.everybody_android.base.BaseDialogFragment
import com.def.everybody_android.databinding.DialogFolderAddBinding
import com.def.everybody_android.repeatOnStarted
import kotlinx.coroutines.flow.collect

class FolderAddDialog(private val callBack: () -> Unit) : BaseDialogFragment<DialogFolderAddBinding, FolderAddViewModel>() {
    override val viewModel: FolderAddViewModel by viewModels()
    override val layoutId: Int = R.layout.dialog_folder_add
    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }

    override fun init() {
        repeatOnStarted {
            viewModel.event.collect {
                when (it) {
                    FolderAddViewModel.Event.Cancel -> {
                        dismiss()
                        viewModel.sendingClickEvents("albumCreateModal/btn/cancle")
                    }
                    FolderAddViewModel.Event.Complete -> {
                        viewModel.createAlbums()
                        viewModel.sendingClickEvents("albumCreateModal/btn/complete")
                        callBack.invoke()
                        dismiss()
                    }
                }
            }
        }
    }


}