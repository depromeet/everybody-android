package com.def.everybody_android.ui.panorama.edit

import androidx.lifecycle.viewModelScope
import com.def.everybody_android.api.AlbumRepo
import com.def.everybody_android.api.PictureRepo
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import com.def.everybody_android.data.response.AlbumResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PanoramaEditViewModel @Inject constructor() : BaseViewModel() {

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    fun onClickEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }

    fun onPoseType(type: Int) {
        viewModelScope.launch { _event.emit(Event.PoseType(type)) }
    }

    fun deletePictures(id: String) {
        runScope({
            PictureRepo.deletePicture(id)
        }){
            viewModelScope.launch { _event.emit(Event.DeleteComplete) }
        }
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
        object Delete : Event()
        object DeleteComplete:Event()
        data class PoseType(val type: Int) : Event()
        data class Album(val albumResponse: AlbumResponse) : Event()
    }
}