package com.example.everybody_android.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.everybody_android.R
import com.example.everybody_android.base.BaseActivity
import com.example.everybody_android.databinding.ActivityMyPageBinding
import com.example.everybody_android.viewmodel.MyPageViewModel

class MyPageActivity : BaseActivity<ActivityMyPageBinding, MyPageViewModel>(){

    override val layoutId = R.layout.activity_my_page
    override val viewModel : MyPageViewModel by viewModels()


}