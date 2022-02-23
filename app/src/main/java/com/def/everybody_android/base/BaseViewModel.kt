package com.def.everybody_android.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


open class BaseViewModel : ViewModel() {

    private val _toast = MutableEventFlow<String>()
    val toast = _toast.asEventFlow()
    val realm = Realm.getDefaultInstance()
    protected fun setToast(msg: String) {
        viewModelScope.launch { _toast.emit(msg) }
    }

    protected fun <T> runScope(req: suspend CoroutineScope.() -> T, res: ((T) -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                req().also { value ->
                    launch(Dispatchers.Main) { res?.invoke(value) }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }
}