package com.def.everybody_android.ui.dialog.folder

import androidx.lifecycle.viewModelScope
import com.def.everybody_android.api.AlbumRepo
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import com.def.everybody_android.data.response.AlbumsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderAddViewModel @Inject constructor() : BaseViewModel() {

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()
    private val _createAlbum = MutableEventFlow<AlbumsResponse.Album>()
    val createAlbum = _createAlbum.asEventFlow()
    private var folderName = ""

    fun onClickEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }

    fun createAlbums() {
        if (folderName.isEmpty()) {
            setToast("폴더명을 입력해주세요.")
            return
        }
        runScope({
            AlbumRepo.createAlbum(mapOf("name" to folderName))
        }) {
            viewModelScope.launch { _event.emit(Event.Album(it)) }
        }
    }

    fun onNameChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        folderName = s.toString()
    }


    sealed class Event {
        object Cancel : Event()
        object Complete : Event()
        data class Album(val album: AlbumsResponse.Album) : Event()
    }

}