package com.example.everybody_android.ui.dialog.loading

import androidx.fragment.app.viewModels
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseDialogFragment
import com.example.everybody_android.databinding.DialogLoadingBinding

class LoadingDialog :
    BaseDialogFragment<DialogLoadingBinding, LoadingViewModel>() {
    override val viewModel: LoadingViewModel by viewModels()
    override val layoutId: Int = R.layout.dialog_loading

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

}