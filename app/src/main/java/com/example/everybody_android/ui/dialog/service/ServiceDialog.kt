package com.example.everybody_android.ui.dialog.service

import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseDialogFragment
import com.example.everybody_android.databinding.DialogServiceNotBinding

class ServiceDialog() : BaseDialogFragment<DialogServiceNotBinding, ServiceViewModel>() {
    override val viewModel: ServiceViewModel by viewModels()
    override val layoutId: Int = R.layout.dialog_service_not
    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }

    override fun init() {
        super.init()
        binding.twOk.setOnClickListener { dismiss() }
    }
}