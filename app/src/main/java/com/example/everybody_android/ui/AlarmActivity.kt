package com.example.everybody_android.ui

import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.viewModels
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseActivity
import com.example.everybody_android.databinding.ActivityAlarmBinding
import com.example.everybody_android.dto.AlarmData
import com.example.everybody_android.repeatOnStarted
import com.example.everybody_android.ui.dialog.time.TimeSettingDialog
import com.example.everybody_android.viewmodel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AlarmActivity : BaseActivity<ActivityAlarmBinding, AlarmViewModel>() {

    override val layoutId = R.layout.activity_alarm
    override val viewModel: AlarmViewModel by viewModels()

    override fun init() {
        liveEvent()
        dialog()
        viewModel.getAlarmTime()
        selector(binding.flSunday, binding.tvSunday, null)
        selector(binding.flMonday, binding.tvMonday, null)
        selector(binding.flTuesday, binding.tvTuesday, null)
        selector(binding.flWedesday, binding.tvWednesday, null)
        selector(binding.flThursday, binding.tvThursday, null)
        selector(binding.flFriday, binding.tvFriday, null)
        selector(binding.flSaturday, binding.tvSaturday, null)
    }

    private fun liveEvent() {
        repeatOnStarted {
            viewModel.clickEvent.collect {
                when (it) {
                    is AlarmViewModel.Event.AlarmTime -> setTime(it.data)
                }
            }
        }
    }

    private fun setTime(data: AlarmData) {

        binding.tvTimeSelect.text = "${data.preferredTimeHour} : ${data.preferredTimeMinute}"

        selector(binding.flSunday, binding.tvSunday, data.sunday)
        selector(binding.flMonday, binding.tvMonday, data.monday)
        selector(binding.flTuesday, binding.tvTuesday, data.tuesday)
        selector(binding.flWedesday, binding.tvWednesday, data.wednesday)
        selector(binding.flThursday, binding.tvThursday, data.thursday)
        selector(binding.flFriday, binding.tvFriday, data.friday)
        selector(binding.flSaturday, binding.tvSaturday, data.saturday)
    }

    fun dialog() {
        binding.tvTimeSelect.setOnClickListener {
            val dialogFragment = TimeSettingDialog(
                object : TimeSettingDialog.DialogListener {
                    override fun checkListener(time: String) {
                        binding.tvTimeSelect.text = time
                        storageButton()
                    }

                    override fun cancelListener() {
                        println("cancel")
                    }

                }
            )
            dialogFragment.show(supportFragmentManager, "dialog")

        }

    }

    fun selector(layout: FrameLayout, textView: TextView, before: Boolean?) {

        if (before == false) {
            layout.isSelected = false
            textView.isSelected = false
        } else {
            layout.isSelected = true
            textView.isSelected = true
        }
        layout.setOnClickListener {
            if (layout.isSelected) {
                layout.isSelected = false
                textView.isSelected = false
                storageButton()
            } else {
                layout.isSelected = true
                textView.isSelected = true
                storageButton()
            }
        }
    }

    private fun storageButton() {
        binding.ibSave.setImageDrawable(resources.getDrawable(R.drawable.ic_blue_storage))
        binding.ibSave.setOnClickListener {
            
        }
    }

}