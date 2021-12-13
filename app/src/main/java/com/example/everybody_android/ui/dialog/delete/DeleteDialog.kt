package com.def.everybody_android.ui.dialog.delete

import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.def.everybody_android.R
import com.def.everybody_android.base.BaseDialogFragment
import com.def.everybody_android.databinding.DialogDeleteBinding
import com.def.everybody_android.repeatOnStarted
import kotlinx.coroutines.flow.collect

class DeleteDialog(private val count: Int, private val action: () -> Unit) :
    BaseDialogFragment<DialogDeleteBinding, DeleteViewModel>() {
    override val viewModel: DeleteViewModel by viewModels()
    override val layoutId: Int = R.layout.dialog_delete
    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }

    override fun init() {
        super.init()
        binding.twTitle.text = "${count}장의 사진을 삭제하시겠어요?"
        repeatOnStarted {
            viewModel.event.collect {
                when (it) {
                    DeleteViewModel.Event.Cancel -> dismiss()
                    DeleteViewModel.Event.Delete -> {
                        action.invoke()
                        dismiss()
                    }
                }
            }
        }
    }
}