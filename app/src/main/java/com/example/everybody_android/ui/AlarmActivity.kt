package com.example.everybody_android.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseActivity
import com.example.everybody_android.databinding.ActivityAlarmBinding
import com.example.everybody_android.dto.AlarmData
import com.example.everybody_android.dto.UserData
import com.example.everybody_android.repeatOnStarted
import com.example.everybody_android.ui.dialog.time.TimeSettingDialog
import com.example.everybody_android.viewmodel.AlarmViewModel
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

        binding.tvTimeSelect.text = "${data.preferredTimeHour} : ${data.preferredTimeMinute}"

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
                        binding.tvTimeSelect.text = "$hour : $minute"
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
        if(afterData.isActivated) binding.ibAlarm.setImageDrawable(resources.getDrawable(R.drawable.ic_alarm_blue))
        else binding.ibAlarm.setImageDrawable(resources.getDrawable(R.drawable.ic_alarm_gray))

        binding.ibAlarm.setOnClickListener {
            if (!afterData.isActivated ){
                binding.ibAlarm.setImageDrawable(resources.getDrawable(R.drawable.ic_alarm_blue))
                afterData.isActivated = true
                storageButton()
            }
            else {
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
                toast()
                val intent = Intent(this, MyPageActivity::class.java)
                intent.putExtra("userData", userData)
                startActivity(intent)
            }
        }

    }

    private fun back(userData : UserData){
        binding.ibBack.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            intent.putExtra("userData",userData)
            startActivity(intent)
        }
    }

    private fun toast(){
        val image = ImageView(applicationContext)
        image.setImageResource(R.drawable.ic_toast_alarm)
        val toast = Toast.makeText(applicationContext,"",Toast.LENGTH_LONG)
        toast.setGravity(Gravity.TOP, Gravity.CENTER,Gravity.TOP)
        toast.view = image
        toast.show()
    }

}