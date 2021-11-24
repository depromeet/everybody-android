package com.example.everybody_android.ui

import androidx.activity.viewModels
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseActivity
import com.example.everybody_android.databinding.ActivityAlarmBinding
import com.example.everybody_android.viewmodel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmActivity : BaseActivity<ActivityAlarmBinding,AlarmViewModel>(){

    override val layoutId = R.layout.activity_alarm
    override val viewModel : AlarmViewModel by viewModels()

    override fun init() {

    }
}