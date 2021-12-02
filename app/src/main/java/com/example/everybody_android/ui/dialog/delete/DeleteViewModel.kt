package com.example.everybody_android.ui.dialog.delete

import androidx.lifecycle.viewModelScope
import com.example.everybody_android.base.BaseViewModel
import com.example.everybody_android.base.MutableEventFlow
import com.example.everybody_android.base.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteViewModel @Inject constructor() : BaseViewModel() {

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    fun onClickEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }

    sealed class Event {
        object Cancel : Event()
        object Delete : Event()
    }

}