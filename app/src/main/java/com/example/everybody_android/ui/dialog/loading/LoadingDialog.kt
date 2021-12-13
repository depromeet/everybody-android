package com.def.everybody_android.ui.dialog.loading

import androidx.fragment.app.viewModels
import com.def.everybody_android.R
import com.def.everybody_android.base.BaseDialogFragment
import com.def.everybody_android.databinding.DialogLoadingBinding

class LoadingDialog :
    BaseDialogFragment<DialogLoadingBinding, LoadingViewModel>() {
    override val viewModel: LoadingViewModel by viewModels()
    override val layoutId: Int = R.layout.dialog_loading

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

}