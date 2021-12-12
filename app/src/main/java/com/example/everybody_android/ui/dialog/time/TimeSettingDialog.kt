package com.example.everybody_android.ui.dialog.time

import androidx.fragment.app.viewModels
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseDialogFragment
import com.example.everybody_android.databinding.DialogSeleteTimeBinding
import com.example.everybody_android.viewmodel.TimeSettingViewModel

class TimeSettingDialog(
    private val dialogListener: DialogListener
) : BaseDialogFragment<DialogSeleteTimeBinding, TimeSettingViewModel>() {

    override val viewModel: TimeSettingViewModel by viewModels()
    override val layoutId = R.layout.dialog_selete_time


    interface DialogListener {
        fun checkListener(hour: Int, minute: Int)
        fun cancelListener()
    }


    var minute = 0
    var hour = 0

    override fun init() {
        check()
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun check() {
        binding.btnCheck.setOnClickListener {

            minute = binding.npMin.value
            hour = binding.npHour.value
            dialogListener.checkListener(hour, minute)
            dismiss()
        }
    }

}