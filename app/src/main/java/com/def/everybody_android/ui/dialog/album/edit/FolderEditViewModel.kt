package com.def.everybody_android.ui.dialog.album.edit

import androidx.lifecycle.viewModelScope
import com.def.everybody_android.api.AlbumRepo
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import com.def.everybody_android.data.response.AlbumsResponse
import com.def.everybody_android.db.Album
import com.def.everybody_android.db.MainFeedPictureData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FolderEditViewModel @Inject constructor() : BaseViewModel() {

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()
    private var folderName = ""

    fun onClickEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }

    fun editAlbums(id: Long) {
        if (folderName.isEmpty()) {
            setToast("폴더명을 입력해주세요.")
            return
        }
        val results = realm.where(Album::class.java).containsValue("id", id).findFirst()
        results?.apply {
            realm.executeTransaction {
                this.name = folderName
                realm.copyToRealm(this)
                viewModelScope.launch { _event.emit(Event.Album(folderName)) }
            }
        }
    }

    fun onNameChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        folderName = s.toString()
    }


    sealed class Event {
        object Cancel : Event()
        object Complete : Event()
        data class Album(val name: String) : Event()
    }

}