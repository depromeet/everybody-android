package com.def.everybody_android.base

import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.def.everybody_android.BR
import com.def.everybody_android.repeatOnStarted
import com.def.everybody_android.toast
import io.realm.Realm
import kotlinx.coroutines.flow.collect

abstract class BaseActivity<T : ViewDataBinding, R : BaseViewModel> : AppCompatActivity() {
    lateinit var binding: T

    abstract val layoutId: Int
    abstract val viewModel: R
    val realm = Realm.getDefaultInstance()
    private var permissionAction = {}
    private var dataPermissionAction = {}
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

    open fun init() {
        repeatOnStarted {
            viewModel.toast.collect { toast(it) }
        }
    }

    fun setPermissionCallback(permission: Array<String>, action: () -> (Unit)) {
        permissionAction = action
        permissionLauncher.launch(permission)
    }
}