package com.def.everybody_android.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.edit
import com.def.everybody_android.R
import com.def.everybody_android.base.BaseActivity
import com.def.everybody_android.databinding.ActivityMainBinding
import com.def.everybody_android.di.HiltApplication.Companion.userData
import com.def.everybody_android.pref.LocalStorage
import com.def.everybody_android.repeatOnStarted
import com.def.everybody_android.ui.album.AlbumActivity
import com.def.everybody_android.ui.camera.CameraActivity
import com.def.everybody_android.ui.dialog.feedback.FeedBackDialog
import com.def.everybody_android.ui.dialog.message.MessageDialog
import com.def.everybody_android.ui.dialog.migrations.MigrationsDialog
import com.def.everybody_android.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override val layoutId = R.layout.activity_main
    override val viewModel: MainViewModel by viewModels()
    private var feedStatus = true
    private val sharedPreferences by lazy { getSharedPreferences("everybody", MODE_PRIVATE) }

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
        feedSort()
    }

    override fun onResume() {
        super.onResume()
        viewModel.settingFeedList()
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
                        if (!sharedPreferences.getBoolean("isMigration", false)) {
                            viewModel.getAlbum(this@MainActivity, MigrationsDialog())
                        }
                    }
                    is MainViewModel.ClickEvent.PanoramaActivity -> startActivity(
                        Intent(
                            this@MainActivity,
                            AlbumActivity::class.java
                        ).apply {
                            putExtra("id", it.data.id)
                        }
                    )
                    MainViewModel.ClickEvent.FeedBack -> FeedBackDialog().show(supportFragmentManager, "")
                    MainViewModel.ClickEvent.Created -> createForResult.launch(Intent(this@MainActivity, CreateFolderActivity::class.java))
                    MainViewModel.ClickEvent.Sign -> viewModel.getUserData()
                    MainViewModel.ClickEvent.Migration -> {
                        MessageDialog(true) {}.setMessage("완료", "서버 정보를 앱으로 이동해왔습니다.\n이제 안전하게 저장하세요!\n더이상 서버에 사진을 저장하지 않습니다!")
                            .show(supportFragmentManager, "")
                        sharedPreferences.edit {
                            putBoolean("isMigration", true)
                        }
                    }
                    MainViewModel.ClickEvent.SettingProfile -> {
                        val intent = Intent(this@MainActivity, MyPageActivity::class.java)
                        startActivity(intent)
                    }
                    MainViewModel.ClickEvent.Camera -> {
                        val intent = Intent(this@MainActivity, CameraActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun feedSort() {
        if (viewModel.fullFeedAdapter.itemCount != 0) binding.clNoFeed.visibility = View.GONE
        if (!feedStatus) {
            binding.rvFullSort.visibility = View.VISIBLE
            binding.rvHalfSort.visibility = View.INVISIBLE
        } else {
            binding.rvFullSort.visibility = View.INVISIBLE
            binding.rvHalfSort.visibility = View.VISIBLE
        }
        feedStatus = !feedStatus
    }

    @SuppressLint("HardwareIds")
    fun deviceToken() {
        val token = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        localStorage.saveDeviceToken(token)
    }

}