package com.example.everybody_android.ui

import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.viewModels
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseActivity
import com.example.everybody_android.databinding.ActivityAlarmBinding
import com.example.everybody_android.ui.dialog.time.TimeSettingDialog
import com.example.everybody_android.viewmodel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmActivity : BaseActivity<ActivityAlarmBinding, AlarmViewModel>() {

    override val layoutId = R.layout.activity_alarm
    override val viewModel: AlarmViewModel by viewModels()

    override fun init() {
        dialog()
        selector(binding.flSunday, binding.tvSunday)
        selector(binding.flMonday, binding.tvMonday)
        selector(binding.flTuesday, binding.tvTuesday)
        selector(binding.flWedesday, binding.tvWednesday)
        selector(binding.flThursday, binding.tvThursday)
        selector(binding.flFriday, binding.tvFriday)
        selector(binding.flSaturday, binding.tvSaturday)
    }

    fun dialog(){
        binding.tvTimeSelect.setOnClickListener{
            val dialogFragment = TimeSettingDialog(
                object : TimeSettingDialog.DialogListener{
                    override fun checkListener(time: String) {
                        binding.tvTimeSelect.text = time
                    }

                    override fun cancelListener() {
                        println("cancel")
                    }

                }
            )
            dialogFragment.show(supportFragmentManager,"dialog")

        }

    }

    fun selector(layout : FrameLayout, textView: TextView){
        layout.setOnClickListener {
            if(layout.isSelected){
                layout.isSelected = false
                textView.isSelected = false
            }
            else {
                layout.isSelected = true
                textView.isSelected = true
            }
        }

    }

}