package com.example.everybody_android.base

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.everybody_android.BR

abstract class BaseActivity<T : ViewDataBinding, R : BaseViewModel> : AppCompatActivity() {
    lateinit var binding: T

    abstract val layoutId: Int
    abstract val viewModel: R
    private var permissionAction = {}
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            it.entries.forEach { data ->
                if (!data.value) return@registerForActivityResult Toast.makeText(
                    this,
                    "권한설정에 동의해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            permissionAction.invoke()
            permissionAction = {}
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this@BaseActivity
        binding.setVariable(BR.vm, viewModel)
        init()
    }

    abstract fun init()

    fun setPermissionCallback(permission: Array<String>, action: () -> (Unit)) {
        permissionAction = action
        permissionLauncher.launch(permission)
    }
}