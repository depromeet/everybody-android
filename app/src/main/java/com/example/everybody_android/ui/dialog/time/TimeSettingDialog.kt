package com.example.everybody_android.ui.dialog.time

import android.content.Intent
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseDialogFragment
import com.example.everybody_android.databinding.DialogSeleteTimeBinding
import com.example.everybody_android.viewmodel.TimeSettingViewModel

class TimeSettingDialog(
    private val dialogListener: DialogListener
) : BaseDialogFragment<DialogSeleteTimeBinding, TimeSettingViewModel>() {

    override val viewModel: TimeSettingViewModel by viewModels()
    override val layoutId = R.layout.dialog_selete_time


    interface DialogListener{
        fun checkListener(time : String)
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
            val time = "$hour : $minute"
            dialogListener.checkListener(time)
            dismiss()
        }
    }

}