package com.example.everybody_android.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseActivity
import com.example.everybody_android.databinding.ActivityMyPageBinding
import com.example.everybody_android.viewmodel.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageActivity : BaseActivity<ActivityMyPageBinding, MyPageViewModel>(){

    override val layoutId = R.layout.activity_my_page
    override val viewModel : MyPageViewModel by viewModels()

    override fun init() {
        onClickAlarm()
    }

    private fun onClickAlarm(){
        binding.ibSettingAlarm.setOnClickListener {
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
        }
    }
}