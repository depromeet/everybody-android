package com.def.everybody_android.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.def.everybody_android.R
import com.def.everybody_android.base.BaseActivity
import com.def.everybody_android.databinding.ActivityMainBinding
import com.def.everybody_android.db.UserData
import com.def.everybody_android.di.HiltApplication.Companion.userData
import com.def.everybody_android.pref.LocalStorage
import com.def.everybody_android.repeatOnStarted
import com.def.everybody_android.ui.camera.CameraActivity
import com.def.everybody_android.ui.dialog.feedback.FeedBackDialog
import com.def.everybody_android.ui.panorama.PanoramaActivity
import com.def.everybody_android.viewmodel.MainViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override val layoutId = R.layout.activity_main
    override val viewModel: MainViewModel by viewModels()
    private var feedStatus = true

    @Inject
    lateinit var localStorage: LocalStorage
    private val createForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.settingFeedList()
        }
    }

    override fun init() {
        liveEvent()
        deviceToken()
        fcmToken()
        feedSort()
        onClickCamara()
        viewModel.settingFeedList()
    }

    private fun onClickMyPage(userData: UserData) {
        binding.ibProfile.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            intent.putExtra("userData", userData)
            startActivity(intent)
        }
    }

    private fun liveEvent() {
        repeatOnStarted {
            viewModel.clickEvent.collect {
                when (it) {
                    is MainViewModel.ClickEvent.SortFeed -> {
                        binding.ibSort.isSelected = !binding.ibSort.isSelected
                        feedSort()
                    }
                    is MainViewModel.ClickEvent.GetUserData -> {
                        userData = it.data
                        user(it.data)
                        onClickMyPage(it.data)
                    }
                    is MainViewModel.ClickEvent.PanoramaActivity -> startActivity(
                        Intent(
                            this@MainActivity,
                            PanoramaActivity::class.java
                        ).apply {
                            putExtra("id", it.data.id.toString())
                        }
                    )
                    MainViewModel.ClickEvent.FeedBack -> FeedBackDialog().show(supportFragmentManager, "")
                    MainViewModel.ClickEvent.Created -> createForResult.launch(Intent(this@MainActivity, CreateFolderActivity::class.java))
                }
            }
        }
    }

    private fun user(data: UserData) {
        binding.tvNickname.text = data.nickName
        binding.tvGoal.text = data.motto
        Glide.with(this).load(data.profileImage).apply(RequestOptions.circleCropTransform())
            .into(binding.ibProfile)
    }

    private fun onClickCamara() {
        binding.ibCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }

    private fun feedSort() {

        if (viewModel.fullFeedAdapter.itemCount != 0) binding.clNoFeed.visibility = View.GONE

        if (!feedStatus) {
            binding.rvFullSort.visibility = View.VISIBLE
            binding.rvHalfSort.visibility = View.GONE
        } else {
            binding.rvFullSort.visibility = View.GONE
            binding.rvHalfSort.visibility = View.VISIBLE
        }
        feedStatus = !feedStatus
    }

    private fun fcmToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            localStorage.saveFcmToken(it)
            println("Token $it")
        }
    }

    @SuppressLint("HardwareIds")
    fun deviceToken() {
        val token = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        localStorage.saveDeviceToken(token)
    }

}