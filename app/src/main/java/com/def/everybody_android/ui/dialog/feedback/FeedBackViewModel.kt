package com.def.everybody_android.ui.dialog.feedback

import androidx.lifecycle.viewModelScope
import com.def.everybody_android.api.UserRepo
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedBackViewModel @Inject constructor() : BaseViewModel() {
    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()
    private var content = ""
    private var number = 1
    fun onClickEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }

    fun onFeedBack() {
        runScope({
            UserRepo.feedBack(hashMapOf("content" to content, "star_rate" to number))
        }) {
            viewModelScope.launch { _event.emit(Event.FeedBack) }
        }
    }

    fun onContentChange(s: CharSequence?, start: Int, before: Int, count: Int) {
        content = s.toString()
    }

    fun onNumberClick(number: Int) {
        this.number = number
        viewModelScope.launch { _event.emit(Event.Number(number)) }
    }


    sealed class Event {
        object Cancel : Event()
        object FeedBack : Event()
        data class Number(val number: Int) : Event()
    }
}