package com.example.everybody_android.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.provider.Settings
import android.view.View
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseActivity
import com.example.everybody_android.databinding.ActivityMainBinding
import com.example.everybody_android.dto.UserData
import com.example.everybody_android.dto.request.SignInRequest
import com.example.everybody_android.pref.LocalStorage
import com.example.everybody_android.repeatOnStarted
import com.example.everybody_android.ui.camera.CameraActivity
import com.example.everybody_android.viewmodel.MainViewModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override val layoutId = R.layout.activity_main
    override val viewModel: MainViewModel by viewModels()
    private var feedStatus = true
    @Inject lateinit var localStorage: LocalStorage

    override fun init() {
        fcmToken()
        deviceToken()
        //viewModel.signIn(SignInRequest(localStorage.getDeviceToken(),"1234"))
        liveEvent()
        viewModel.getAlbum()
        viewModel.getUserData()
        intentCreateFolder()
        feedSort()
        onClickCamara()
        onClickMyPage()

    }

    private fun onClickMyPage(){
        binding.ibProfile.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun intentCreateFolder() {
        binding.ibAdd.setOnClickListener {
            val intent = Intent(this, CreateFolderActivity::class.java)
            startActivity(intent)
        }
    }

    private fun liveEvent() {
        repeatOnStarted {
            viewModel.clickEvent.collect {
                when (it) {
                    is MainViewModel.ClickEvent.SortFeed -> feedSort()
                    is MainViewModel.ClickEvent.GetUserData -> user(it.data)
                }
            }
        }
    }

    private fun user(data : UserData){
        binding.tvNickname.text = data.nickName
        binding.tvGoal.text = data.motto
        Glide.with(this).load(data.profileImage).into(binding.ibProfile)
    }

    private fun onClickCamara(){
        binding.ibCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }

    private fun feedSort() {
        if(viewModel.fullFeedAdapter.itemCount != 0) binding.clNoFeed.visibility = View.GONE

        if (!feedStatus) {
            binding.rvFullSort.visibility = View.VISIBLE
            binding.rvHalfSort.visibility = View.GONE
        } else {
            binding.rvFullSort.visibility = View.GONE
            binding.rvHalfSort.visibility = View.VISIBLE
        }
        feedStatus = !feedStatus
    }

    private fun fcmToken(){
        FirebaseMessaging.getInstance().token.addOnSuccessListener(OnSuccessListener {
            localStorage.saveFcmToken(it)
            println("Token $it")
        })
    }
    @SuppressLint("HardwareIds")
    fun deviceToken(){
        val token = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        localStorage.saveDeviceToken(token)

    }
}