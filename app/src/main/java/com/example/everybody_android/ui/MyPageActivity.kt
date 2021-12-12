package com.example.everybody_android.ui

import android.content.Intent
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseActivity
import com.example.everybody_android.databinding.ActivityMyPageBinding
import com.example.everybody_android.di.HiltApplication.Companion.userData
import com.example.everybody_android.dto.UserData
import com.example.everybody_android.repeatOnStarted
import com.example.everybody_android.viewmodel.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MyPageActivity : BaseActivity<ActivityMyPageBinding, MyPageViewModel>() {

    override val layoutId = R.layout.activity_my_page
    override val viewModel: MyPageViewModel by viewModels()

    override fun init() {
        userData?.apply {
            onClickAlarm(this)
            profile(this)
        }
        back()
        repeatOnStarted {
            viewModel.complete.collect {
                finish()
            }
        }
    }

    private fun profile(userData: UserData) {
        if (!userData.profileImage.isNullOrEmpty()) {
            Glide.with(this)
                .load(userData.profileImage)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.ivProfileImage)
        } else binding.ivProfileImage.setImageDrawable(resources.getDrawable(R.drawable.test_icon))

        binding.etNickname.append(userData.nickName)
        binding.etMotto.append(userData.motto)
        binding.tvComplete.setOnClickListener {
            if (userData.motto == binding.etMotto.text.toString() && userData.nickName == binding.etNickname.text.toString()) finish()
            else viewModel.complete(
                mapOf(
                    "nickname" to binding.etNickname.text.toString(),
                    "motto" to binding.etMotto.text.toString()
                )
            )
        }
    }

    fun back() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }

    private fun onClickAlarm(userData: UserData) {
        binding.ibSettingAlarm.setOnClickListener {
            val intent = Intent(this, AlarmActivity::class.java)
            intent.putExtra("userData", userData)
            startActivity(intent)
        }
        binding.tvAlarm.setOnClickListener {
            val intent = Intent(this, AlarmActivity::class.java)
            intent.putExtra("userData", userData)
            startActivity(intent)
        }
    }


}