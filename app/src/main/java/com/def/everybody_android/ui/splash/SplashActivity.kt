package com.def.everybody_android.ui.splash

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.def.everybody_android.R
import com.def.everybody_android.base.BaseActivity
import com.def.everybody_android.databinding.ActivitySplashBinding
import com.def.everybody_android.pref.LocalStorage
import com.def.everybody_android.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {
    override val layoutId: Int = R.layout.activity_splash
    override val viewModel: SplashViewModel by viewModels()
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    @Inject
    lateinit var localStorage: LocalStorage
    override fun init() {
        super.init()
        Handler(Looper.myLooper()!!).postDelayed({
            if (localStorage.isAuthentication()) {
                executor = ContextCompat.getMainExecutor(this)
                biometricPrompt = BiometricPrompt(this, executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            super.onAuthenticationError(errorCode, errString)
                            finish()
                        }

                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                            finish()
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                        }
                    })


                promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("생체인증")
                    .setSubtitle("생체정보로 인증해 주세요")
                    .setNegativeButtonText("취소")
                    .build()
                biometricPrompt.authenticate(promptInfo)
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }, 2000)
    }

}