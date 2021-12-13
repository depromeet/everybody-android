package com.def.everybody_android.ui

import android.content.Intent
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.def.everybody_android.R
import com.def.everybody_android.base.BaseActivity
import com.def.everybody_android.databinding.ActivityMyPageBinding
import com.def.everybody_android.di.HiltApplication.Companion.userData
import com.def.everybody_android.dto.UserData
import com.def.everybody_android.pref.LocalStorage
import com.def.everybody_android.repeatOnStarted
import com.def.everybody_android.viewmodel.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class MyPageActivity : BaseActivity<ActivityMyPageBinding, MyPageViewModel>() {

    override val layoutId = R.layout.activity_my_page
    override val viewModel: MyPageViewModel by viewModels()

    @Inject
    lateinit var localStorage: LocalStorage

    override fun init() {
        userData?.apply {
            onClickAlarm(this)
            profile(this)
        }
        back()
        appStorage()
        binding.ibStorage.isSelected = localStorage.isAppStorage()
        repeatOnStarted {
            viewModel.complete.collect {
                finish()
            }
        }
    }

    private fun appStorage() {
        binding.ibStorage.setOnClickListener {
            binding.ibStorage.isSelected = !binding.ibStorage.isSelected
            localStorage.setAppStorage(binding.ibStorage.isSelected)
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