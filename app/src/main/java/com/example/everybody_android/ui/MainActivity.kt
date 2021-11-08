package com.example.everybody_android.ui

import android.content.Intent
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseActivity
import com.example.everybody_android.databinding.ActivityMainBinding
import com.example.everybody_android.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding,MainViewModel>() {
    override val layoutId = R.layout.activity_main
    override val viewModel: MainViewModel by viewModels()

    var feedStatus = true

    override fun init() {
        viewModel.testFeed()
        intentCreateFolder()
        feedSort()

    }

    private fun intentCreateFolder(){
        binding.ibAdd.setOnClickListener {
            val intent = Intent(this,CreateFolderActivity::class.java)
            startActivity(intent)
        }
    }

    private fun feedSort(){
        binding.ibSort.setOnClickListener {

            if(feedStatus){
                binding.rvFullSort.visibility = View.VISIBLE
                binding.rvHalfSort.visibility = View.GONE
            }
            else{
                binding.rvFullSort.visibility = View.GONE
                binding.rvHalfSort.visibility = View.VISIBLE
            }
            feedStatus = !feedStatus
        }
    }



}