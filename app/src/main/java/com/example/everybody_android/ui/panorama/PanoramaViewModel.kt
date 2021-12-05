package com.example.everybody_android.ui.panorama

import androidx.lifecycle.viewModelScope
import com.example.everybody_android.api.AlbumRepo
import com.example.everybody_android.base.BaseViewModel
import com.example.everybody_android.base.MutableEventFlow
import com.example.everybody_android.base.asEventFlow
import com.example.everybody_android.data.response.AlbumResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PanoramaViewModel @Inject constructor() : BaseViewModel() {

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    fun onClickEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }

    fun onPoseType(type: Int) {
        viewModelScope.launch { _event.emit(Event.PoseType(type)) }
    }

    fun getAlbum(id: String) {
        runScope({
            AlbumRepo.getAlbum(id)
        }) {
            viewModelScope.launch { _event.emit(Event.Album(it)) }
        }
    }

    sealed class Event {
        object Close : Event()
        object Edit : Event()
        object ListType : Event()
        data class PoseType(val type: Int) : Event()
        data class Album(val albumResponse: AlbumResponse) : Event()
    }

}