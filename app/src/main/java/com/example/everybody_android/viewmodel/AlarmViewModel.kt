package com.example.everybody_android.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.everybody_android.api.AlarmRepo
import com.example.everybody_android.base.BaseViewModel
import com.example.everybody_android.base.MutableEventFlow
import com.example.everybody_android.base.asEventFlow
import com.example.everybody_android.dto.AlarmData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(): BaseViewModel() {

    private val _clickEvent = MutableEventFlow<AlarmViewModel.Event>()
    val clickEvent = _clickEvent.asEventFlow()

    fun onClickEvent(event: AlarmViewModel.Event) {
        viewModelScope.launch { _clickEvent.emit(event) }
    }

    fun getAlarmTime(){
        runScope({
            AlarmRepo.getAlarm()
        }){
            onClickEvent(Event.AlarmTime(it))
        }
    }

    fun setAlarmTime(alarmData : AlarmData){
        runScope({
            AlarmRepo.setAlarm(alarmData)
        }){
            println(it)
        }
    }

    sealed class Event{
        data class AlarmTime(val data : AlarmData) : Event()
    }
}