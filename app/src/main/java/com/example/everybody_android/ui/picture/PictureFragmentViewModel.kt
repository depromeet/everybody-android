package com.example.everybody_android.ui.picture

import androidx.lifecycle.viewModelScope
import com.example.everybody_android.base.BaseViewModel
import com.example.everybody_android.base.MutableEventFlow
import com.example.everybody_android.base.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PictureFragmentViewModel @Inject constructor() : BaseViewModel() {
    private val _clickEvent = MutableEventFlow<ClickEvent>()
    val clickEvent = _clickEvent.asEventFlow()

    fun onClickEvent(event: ClickEvent) {
        viewModelScope.launch { _clickEvent.emit(event) }
    }

    sealed class ClickEvent {
        object PartTab : ClickEvent()
        object PartWhole : ClickEvent()
        object PartUpper : ClickEvent()
        object PartLower : ClickEvent()
        object TimeTab : ClickEvent()
        object TimeNow : ClickEvent()
        object TimePerson : ClickEvent()
        object TimePicture : ClickEvent()
    }

}