package com.def.everybody_android.ui.picture

import androidx.lifecycle.viewModelScope
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import com.def.everybody_android.db.Album
import com.def.everybody_android.db.MainFeedPictureData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PictureViewModel @Inject constructor() : BaseViewModel() {
    private val _clickEvent = MutableEventFlow<ClickEvent>()
    val clickEvent = _clickEvent.asEventFlow()

    fun onClickEvent(event: ClickEvent) {
        viewModelScope.launch { _clickEvent.emit(event) }
    }

    fun photoUpload(valueMap: Map<String, String>) {
        val albumId = valueMap["albumId"]?.toLong() ?: -1
        val bodyPart = valueMap["bodyPart"].toString()
        val imagePath = valueMap["image"].toString()
        val results = realm.where(Album::class.java).containsValue("id", albumId).findFirst()
        results?.apply {
            realm.executeTransaction {
                feedPictureDataList.add(
                    MainFeedPictureData().apply {
                        this.albumId = albumId
                        this.bodyPart = bodyPart
                        this.imagePath = imagePath
                        this.pictureCreated = Date()
                    }
                )
                realm.copyToRealm(this)
            }
        }
        viewModelScope.launch { _clickEvent.emit(ClickEvent.Complete) }
    }

    sealed class ClickEvent {
        object Back : ClickEvent()
        object Next : ClickEvent()
        object Complete : ClickEvent()
    }

}