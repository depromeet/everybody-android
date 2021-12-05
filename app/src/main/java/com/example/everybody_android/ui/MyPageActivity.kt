package com.example.everybody_android.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseActivity
import com.example.everybody_android.databinding.ActivityMyPageBinding
import com.example.everybody_android.dto.UserData
import com.example.everybody_android.viewmodel.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageActivity : BaseActivity<ActivityMyPageBinding, MyPageViewModel>(){

    override val layoutId = R.layout.activity_my_page
    override val viewModel : MyPageViewModel by viewModels()

    override fun init() {
        val userData = intent.getSerializableExtra("userData") as UserData
        onClickAlarm(userData)
        profile(userData)
        back()
    }

    private fun profile(userData : UserData){
        if(!userData.profileImage.isNullOrEmpty()){
            Glide.with(this)
                .load(userData.profileImage)
                .into(binding.ivProfileImage)
        }
        else binding.ivProfileImage.setImageDrawable(resources.getDrawable(R.drawable.test_icon))

        binding.etNickname.append(userData.nickName)
        binding.etMotto.append(userData.motto)
    }

    fun back(){
        binding.ibBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onClickAlarm(userData: UserData){
        binding.ibSettingAlarm.setOnClickListener {
            val intent = Intent(this, AlarmActivity::class.java)
            intent.putExtra("userData",userData)
            startActivity(intent)
        }
    }


}