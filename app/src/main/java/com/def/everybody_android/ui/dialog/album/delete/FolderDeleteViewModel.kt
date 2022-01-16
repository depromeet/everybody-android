package com.def.everybody_android.ui.dialog.album.delete

import androidx.lifecycle.viewModelScope
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderDeleteViewModel @Inject constructor() : BaseViewModel() {

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