package com.example.everybody_android.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


open class BaseViewModel : ViewModel(){

    private val compositeDisposable = CompositeDisposable()

    private fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
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