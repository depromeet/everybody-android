package com.def.everybody_android.ui.picture

import androidx.lifecycle.viewModelScope
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import com.def.everybody_android.db.MainFeedPictureData
import com.def.everybody_android.nextPictureId
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
        realm.executeTransaction {
            with(it.createObject(MainFeedPictureData::class.java, it.nextPictureId())) {
                albumId = valueMap["albumId"]?.toLong() ?: -1
                bodyPart = valueMap["bodyPart"].toString()
                imagePath = valueMap["image"].toString()
                pictureCreated = Date()
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