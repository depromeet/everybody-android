package com.def.everybody_android.ui.panorama.edit

import androidx.lifecycle.viewModelScope
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import com.def.everybody_android.db.Album
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

    fun deletePictures(id: Long, path: String) {
        realm.where(Album::class.java).containsValue("id", id).findFirst()?.apply {
            realm.executeTransaction {
                val index = this.feedPictureDataList.indexOfFirst { picture -> picture.imagePath == path }
                if (index > -1) this.feedPictureDataList.remove(this.feedPictureDataList[index])
                viewModelScope.launch { _event.emit(Event.DeleteComplete) }
            }
        }
    }


    fun getAlbum(id: Long) {
        realm.where(Album::class.java).containsValue("id", id).findFirst()?.apply {
            viewModelScope.launch { _event.emit(Event.Album(this@apply)) }
        }
    }

    sealed class Event {
        object Close : Event()
        object Delete : Event()
        object DeleteComplete : Event()
        data class PoseType(val type: Int) : Event()
        data class Album(val album: com.def.everybody_android.db.Album) : Event()
    }
}