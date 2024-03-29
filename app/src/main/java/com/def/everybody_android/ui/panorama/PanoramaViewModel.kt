package com.def.everybody_android.ui.panorama

import androidx.lifecycle.viewModelScope
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import com.def.everybody_android.db.Album
import kotlinx.coroutines.launch

class PanoramaViewModel : BaseViewModel() {

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    fun onClickEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
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

    sealed class Event {
        object Close : Event()
        object Delete : Event()
        object DeleteComplete : Event()
    }

}