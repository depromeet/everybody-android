package com.example.everybody_android.ui.camera

import androidx.lifecycle.viewModelScope
import com.example.everybody_android.base.BaseViewModel
import com.example.everybody_android.base.MutableEventFlow
import com.example.everybody_android.base.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : BaseViewModel() {

    private val _clickEvent = MutableEventFlow<ClickEvent>()
    val clickEvent = _clickEvent.asEventFlow()

    fun onClickEvent(event: ClickEvent) {
        viewModelScope.launch { _clickEvent.emit(event) }
    }

    sealed class ClickEvent {
        object Shutter : ClickEvent()
        object Album : ClickEvent()
        object Pose : ClickEvent()
        object Back : ClickEvent()
        object Switch : ClickEvent()
        object Grid : ClickEvent()
    }

}