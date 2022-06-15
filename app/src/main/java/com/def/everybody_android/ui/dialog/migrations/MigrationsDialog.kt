package com.def.everybody_android.ui.dialog.migrations

import androidx.fragment.app.viewModels
import com.def.everybody_android.R
import com.def.everybody_android.base.BaseDialogFragment
import com.def.everybody_android.databinding.DialogMigrationsBinding

class MigrationsDialog :
    BaseDialogFragment<DialogMigrationsBinding, MigrationsViewModel>() {
    override val viewModel: MigrationsViewModel by viewModels()
    override val layoutId: Int = R.layout.dialog_migrations

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        isCancelable = false
    }

}