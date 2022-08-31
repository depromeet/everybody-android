package com.def.everybody_android.viewmodel

import androidx.lifecycle.viewModelScope
import com.def.everybody_android.api.SignRepo
import com.def.everybody_android.api.UserRepo
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import com.def.everybody_android.di.HiltApplication
import com.def.everybody_android.di.HiltApplication.Companion.userData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor() : BaseViewModel() {

    private val _clickEvent = MutableEventFlow<Event>()
    val clickEvent = _clickEvent.asEventFlow()

    fun onClickEvent(event: Event) {
        viewModelScope.launch { _clickEvent.emit(event) }
    }

    fun complete(mapOf: Map<String, String>) {
        runScope({
            UserRepo.putUserData(mapOf)
        }) {

            userData = userData?.copy(
                nickName = mapOf["nickname"].toString(),
                motto = mapOf["motto"].toString()
            )
            viewModelScope.launch { _clickEvent.emit(Event.Complete) }
        }
    }

    fun login(map: HashMap<String, Any>) {
        runScope({
            SignRepo.oauthLogin(map)
        }) { data ->
            HiltApplication.token = data.accessToken
        }
    }

    sealed class Event {
        object Complete : Event()
        object Kakao : Event()
        object Google : Event()
        object Alarm : Event()
        object Finish : Event()
        object Storage : Event()
        object Thumbnail : Event()
    }

}