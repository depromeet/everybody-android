package com.example.everybody_android.ui.dialog.delete

import androidx.fragment.app.viewModels
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseDialogFragment
import com.example.everybody_android.databinding.DialogDeleteBinding
import com.example.everybody_android.repeatOnStarted
import kotlinx.coroutines.flow.collect

class DeleteDialog(private val count: Int, private val action: () -> Unit) :
    BaseDialogFragment<DialogDeleteBinding, DeleteViewModel>() {
    override val viewModel: DeleteViewModel by viewModels()
    override val layoutId: Int = R.layout.dialog_delete

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