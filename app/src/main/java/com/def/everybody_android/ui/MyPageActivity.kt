package com.def.everybody_android.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.def.everybody_android.R
import com.def.everybody_android.base.BaseActivity
import com.def.everybody_android.databinding.ActivityMyPageBinding
import com.def.everybody_android.di.HiltApplication.Companion.userData
import com.def.everybody_android.dto.UserData
import com.def.everybody_android.pref.LocalStorage
import com.def.everybody_android.repeatOnStarted
import com.def.everybody_android.toast
import com.def.everybody_android.ui.authentication.AuthenticationActivity
import com.def.everybody_android.viewmodel.MyPageViewModel
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class MyPageActivity : BaseActivity<ActivityMyPageBinding, MyPageViewModel>() {

    override val layoutId = R.layout.activity_my_page
    override val viewModel: MyPageViewModel by viewModels()

    @Inject
    lateinit var localStorage: LocalStorage

    private val clearLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.ibAuthentication.isSelected = !binding.ibAuthentication.isSelected
                localStorage.setAuthentication(binding.ibAuthentication.isSelected)
            }
        }


    override fun init() {

        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> binding.groupAuthentication.isVisible = true
            else -> binding.groupAuthentication.isVisible = false
        }

        userData?.apply {
            profile(this)
        }
        binding.ibStorage.isSelected = localStorage.isAppStorage()
        binding.ibThumbnail.isSelected = localStorage.isThumbnailBlind()
        binding.ibAuthentication.isSelected = localStorage.isAuthentication()
        repeatOnStarted {
            viewModel.clickEvent.collect {
                when (it) {
                    MyPageViewModel.Event.Alarm -> {
                        val intent = Intent(this@MyPageActivity, AlarmActivity::class.java)
                        intent.putExtra("userData", userData)
                        startActivity(intent)
                    }
                    MyPageViewModel.Event.Complete, MyPageViewModel.Event.Finish -> {
                        viewModel.sendingClickEvents("setting/btn/back")
                        finish()
                    }
                    MyPageViewModel.Event.Google -> TODO()
                    MyPageViewModel.Event.Kakao -> {
                        UserApiClient.instance.loginWithKakaoTalk(this@MyPageActivity) { token, error ->
                            if (error != null) {
                                toast("로그인 실패")
                            } else if (token != null) {
                                viewModel.login(
                                    hashMapOf(
                                        "user_id" to localStorage.getUserId(),
                                        "password" to "1234",
                                        "kind" to "KAKAO",
                                        "token" to token.accessToken
                                    )
                                )
                            }
                        }
                    }
                    MyPageViewModel.Event.Storage -> {
                        binding.ibStorage.isSelected = !binding.ibStorage.isSelected
                        localStorage.setAppStorage(binding.ibStorage.isSelected)
                        if(binding.ibStorage.isSelected) viewModel.sendingClickEvents("setting/saveApp/toggle/on")
                        else viewModel.sendingClickEvents("setting/saveApp/toggle/off")
                    }
                    MyPageViewModel.Event.Thumbnail -> {
                        binding.ibThumbnail.isSelected = !binding.ibThumbnail.isSelected
                        localStorage.setThumbnailBlind(binding.ibThumbnail.isSelected)
                        if(binding.ibStorage.isSelected) viewModel.sendingClickEvents("setting/hiddenThumb/toggle/on")
                        else viewModel.sendingClickEvents("setting/hiddenThumb/toggle/off")
                    }
                    MyPageViewModel.Event.Authentication -> {
                        if (binding.ibAuthentication.isSelected) {
                            clearLauncher.launch(Intent(this@MyPageActivity, AuthenticationActivity::class.java))
                            return@collect
                        }
                        binding.ibAuthentication.isSelected = !binding.ibAuthentication.isSelected
                        localStorage.setAuthentication(binding.ibAuthentication.isSelected)
                        if(binding.ibStorage.isSelected) viewModel.sendingClickEvents("setting/faceId/toggle/on")
                        else viewModel.sendingClickEvents("setting/faceId/toggle/off")
                    }
                }
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
}