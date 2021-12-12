package com.example.everybody_android.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.everybody_android.api.UserRepo
import com.example.everybody_android.base.BaseViewModel
import com.example.everybody_android.base.MutableEventFlow
import com.example.everybody_android.base.asEventFlow
import com.example.everybody_android.di.HiltApplication.Companion.userData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor() : BaseViewModel() {

    private val _complete = MutableEventFlow<Unit>()
    val complete = _complete.asEventFlow()

    fun complete(mapOf: Map<String, String>) {
        runScope({
            UserRepo.putUserData(mapOf)
        }) {
            userData = userData?.copy(
                nickName = mapOf["nickname"].toString(),
                motto = mapOf["motto"].toString()
            )
            viewModelScope.launch { _complete.emit(Unit) }
        }
    }

}