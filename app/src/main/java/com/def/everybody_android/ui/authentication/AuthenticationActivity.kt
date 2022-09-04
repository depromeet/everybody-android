package com.def.everybody_android.ui.authentication

import android.os.Bundle
import androidx.activity.viewModels
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.def.everybody_android.R
import com.def.everybody_android.base.BaseActivity
import com.def.everybody_android.databinding.ActivityAuthenticationBinding
import com.def.everybody_android.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.concurrent.Executor

@AndroidEntryPoint
class AuthenticationActivity : BaseActivity<ActivityAuthenticationBinding, AuthenticationViewModel>() {
    override val layoutId: Int = R.layout.activity_authentication
    override val viewModel: AuthenticationViewModel by viewModels()
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        executor = ContextCompat.getMainExecutor(this@AuthenticationActivity)
        biometricPrompt = BiometricPrompt(this@AuthenticationActivity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    finish()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    setResult(RESULT_OK)
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
    }

    override fun init() {
        super.init()
        repeatOnStarted {
            viewModel.clearEvent.collect {
                biometricPrompt.authenticate(promptInfo)
            }
        }
    }

}