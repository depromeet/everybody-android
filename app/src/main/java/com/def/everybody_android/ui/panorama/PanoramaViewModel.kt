package com.def.everybody_android.ui.panorama

import androidx.lifecycle.viewModelScope
import com.def.everybody_android.api.AlbumRepo
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import com.def.everybody_android.data.response.AlbumResponse
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

    fun deleteAlbum(id:String){
        runScope({
            AlbumRepo.deleteAlbum(id)
        }) {
            if(it.code() == 204) viewModelScope.launch { _event.emit(Event.DeleteComplete) }
        }
    }

    sealed class Event {
        object Close : Event()
        object Edit : Event()
        object ListType : Event()
        object Camera : Event()
        object More : Event()
        object AlbumDelete : Event()
        object AlbumEdit : Event()
        object DeleteComplete:Event()
        object Share : Event()
        data class PoseType(val type: Int) : Event()
        data class Album(val albumResponse: AlbumResponse) : Event()
    }

}