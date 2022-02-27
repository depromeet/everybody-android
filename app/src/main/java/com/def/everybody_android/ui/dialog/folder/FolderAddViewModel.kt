package com.def.everybody_android.ui.dialog.folder

import androidx.lifecycle.viewModelScope
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import com.def.everybody_android.db.Album
import com.def.everybody_android.nextAlbumId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FolderAddViewModel @Inject constructor() : BaseViewModel() {

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()
    private var folderName = ""

    fun onClickEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }

    fun createAlbums() {
        if (folderName.isEmpty()) {
            setToast("폴더명을 입력해주세요.")
            return
        }
        realm.executeTransaction {
            with(it.createObject(Album::class.java, it.nextAlbumId())) {
                this.name = folderName
                feedCreated = Date()
            }
        }
    }

    fun onNameChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        folderName = s.toString()
    }


    sealed class Event {
        object Cancel : Event()
        object Complete : Event()
    }

}