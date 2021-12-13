package com.def.everybody_android.ui

import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.viewModels
import com.def.everybody_android.R
import com.def.everybody_android.base.BaseActivity
import com.def.everybody_android.databinding.ActivityAlarmBinding
import com.def.everybody_android.dto.AlarmData
import com.def.everybody_android.dto.UserData
import com.def.everybody_android.repeatOnStarted
import com.def.everybody_android.toast
import com.def.everybody_android.ui.dialog.time.TimeSettingDialog
import com.def.everybody_android.viewmodel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AlarmActivity : BaseActivity<ActivityAlarmBinding, AlarmViewModel>() {

    override val layoutId = R.layout.activity_alarm
    override val viewModel: AlarmViewModel by viewModels()

    lateinit var beforeData: AlarmData
    lateinit var afterData: AlarmData

    lateinit var userData: UserData


    override fun init() {
        userData = intent.getSerializableExtra("userData") as UserData
        liveEvent()
        dialog()
        back(userData)
        viewModel.getAlarmTime()
    }

    private fun liveEvent() {
        repeatOnStarted {
            viewModel.clickEvent.collect {
                when (it) {
                    is AlarmViewModel.Event.AlarmTime -> {
                        beforeData = it.data.copy()
                        afterData = it.data.copy()
                        setTime(it.data)
                    }
                }
            }
        }
    }

    private fun setTime(data: AlarmData) {
        val h =
            if (data.preferredTimeHour.toString().length > 1) data.preferredTimeHour.toString() else "0${data.preferredTimeHour}"
        val m =
            if (data.preferredTimeMinute.toString().length > 1) data.preferredTimeMinute.toString() else "0${data.preferredTimeMinute}"
        binding.tvTimeSelect.text = "$h : $m"

        alarmButton()

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
                    override fun checkListener(hour: Int, minute: Int) {
                        val h = if (hour.toString().length > 1) hour.toString() else "0$hour"
                        val m = if (minute.toString().length > 1) minute.toString() else "0$minute"
                        binding.tvTimeSelect.text = "$h : $m"
                        afterData.preferredTimeHour = hour
                        afterData.preferredTimeMinute = minute
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

            when (it) {
                binding.flSunday -> afterData.sunday = !afterData.sunday
                binding.flMonday -> afterData.monday = !afterData.monday
                binding.flTuesday -> afterData.tuesday = !afterData.tuesday
                binding.flWedesday -> afterData.wednesday = !afterData.wednesday
                binding.flThursday -> afterData.thursday = !afterData.thursday
                binding.flFriday -> afterData.friday = !afterData.friday
                binding.flSaturday -> afterData.saturday = !afterData.saturday
            }
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

    private fun alarmButton() {
        if (afterData.isActivated) binding.ibAlarm.setImageDrawable(resources.getDrawable(R.drawable.ic_alarm_blue))
        else binding.ibAlarm.setImageDrawable(resources.getDrawable(R.drawable.ic_alarm_gray))

        binding.ibAlarm.setOnClickListener {
            if (!afterData.isActivated) {
                binding.ibAlarm.setImageDrawable(resources.getDrawable(R.drawable.ic_alarm_blue))
                afterData.isActivated = true
                storageButton()
            } else {
                binding.ibAlarm.setImageDrawable(resources.getDrawable(R.drawable.ic_alarm_gray))
                afterData.isActivated = false
                storageButton()
            }


        }
    }

    private fun storageButton() {
        println(afterData)
        if (beforeData == afterData) {
            binding.ibSave.setImageDrawable(resources.getDrawable(R.drawable.ic_gray_storage))
        } else {
            binding.ibSave.setImageDrawable(resources.getDrawable(R.drawable.ic_blue_storage))
            binding.ibSave.setOnClickListener {
                viewModel.setAlarmTime(afterData)
                toast(R.drawable.ic_toast_alarm)
                finish()
            }
        }

    }

    private fun back(userData: UserData) {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }


}