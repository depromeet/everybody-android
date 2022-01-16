package com.def.everybody_android.ui.dialog.album.delete

import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.def.everybody_android.R
import com.def.everybody_android.base.BaseDialogFragment
import com.def.everybody_android.databinding.DialogDeleteBinding
import com.def.everybody_android.databinding.DialogFolderDeleteBinding
import com.def.everybody_android.repeatOnStarted
import kotlinx.coroutines.flow.collect

class FolderDeleteDialog(private val action: () -> Unit) :
    BaseDialogFragment<DialogFolderDeleteBinding, FolderDeleteViewModel>() {
    override val viewModel: FolderDeleteViewModel by viewModels()
    override val layoutId: Int = R.layout.dialog_folder_delete
    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }

    override fun init() {
        super.init()
        repeatOnStarted {
            viewModel.event.collect {
                when (it) {
                    FolderDeleteViewModel.Event.Cancel -> dismiss()
                    FolderDeleteViewModel.Event.Delete -> {
                        action.invoke()
                        dismiss()
                    }
                }
            }
        }
    }
}