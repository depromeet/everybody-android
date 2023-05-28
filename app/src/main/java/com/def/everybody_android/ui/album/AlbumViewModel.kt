package com.def.everybody_android.ui.album

import androidx.lifecycle.viewModelScope
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import com.def.everybody_android.db.Album
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor() : BaseViewModel() {

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    fun onClickEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }

    fun onPoseType(type: Int) {
        viewModelScope.launch { _event.emit(Event.PoseType(type)) }
    }

    fun getAlbum(id: Long) {
        realm.where(Album::class.java).containsValue("id", id).findFirst()?.apply {
            viewModelScope.launch { _event.emit(Event.Album(this@apply)) }
        }
    }

    fun deleteAlbum(id: Long) {
        viewModelScope.launch { _event.emit(Event.DeleteComplete) }
    }

    sealed class Event {
        object Close : Event()
        object Edit : Event()
        object Camera : Event()
        object More : Event()
        object AlbumDelete : Event()
        object AlbumEdit : Event()
        object DeleteComplete : Event()
        object Share : Event()
        data class PoseType(val type: Int) : Event()
        data class Album(val album: com.def.everybody_android.db.Album) : Event()
    }

}