package com.example.everybody_android.ui

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseActivity
import com.example.everybody_android.databinding.ActivityMainBinding
import com.example.everybody_android.repeatOnStarted
import com.example.everybody_android.ui.camera.CameraActivity
import com.example.everybody_android.ui.panorama.PanoramaActivity
import com.example.everybody_android.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override val layoutId = R.layout.activity_main
    override val viewModel: MainViewModel by viewModels()

    private var feedStatus = true

    override fun init() {
        liveEvent()
        viewModel.token()
        viewModel.testFeed()
        intentCreateFolder()
        feedSort()
        onClickCamara()
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
                    MainViewModel.ClickEvent.PanoramaActivity -> startActivity(
                        Intent(
                            this@MainActivity,
                            PanoramaActivity::class.java
                        )
                    )
                }
            }
        }
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
}