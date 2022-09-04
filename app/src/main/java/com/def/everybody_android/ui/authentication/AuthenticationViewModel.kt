package com.def.everybody_android.ui.authentication

import androidx.lifecycle.viewModelScope
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor()  : BaseViewModel() {

    private val _clearEvent = MutableEventFlow<Unit>()
    val clearEvent = _clearEvent.asEventFlow()

    fun onClickEvent() {
        viewModelScope.launch { _clearEvent.emit(Unit) }
    }

}