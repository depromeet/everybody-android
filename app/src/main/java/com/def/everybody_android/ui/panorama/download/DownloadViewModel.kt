package com.def.everybody_android.ui.panorama.download

import androidx.lifecycle.viewModelScope
import com.def.everybody_android.api.AlbumRepo
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor() : BaseViewModel() {

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    fun onClickEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }

    fun onDeleteClick(item: DownloadActivity.Item) {
        viewModelScope.launch { _event.emit(Event.Delete(item)) }
    }

    fun onDownloadClick(keys: List<String>) {
        runScope({
            AlbumRepo.downloadAlbums(hashMapOf("keys" to keys, "duration" to 0.25))
        }) {
            if (it.isSuccessful) {
                viewModelScope.launch { _event.emit(Event.DownloadFile(it.body() ?: return@launch)) }
            }
        }
    }

    sealed class Event {
        object Close : Event()
        object Download : Event()
        object Refresh : Event()
        data class DownloadFile(val body: ResponseBody) : Event()
        data class Delete(val item: DownloadActivity.Item) : Event()
    }

}