package com.def.everybody_android.ui.splash

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.def.everybody_android.R
import com.def.everybody_android.base.BaseActivity
import com.def.everybody_android.databinding.ActivitySplashBinding
import com.def.everybody_android.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {
    override val layoutId: Int = R.layout.activity_splash
    override val viewModel: SplashViewModel by viewModels()

    override fun init() {
        super.init()
        Handler(Looper.myLooper()!!).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }

}