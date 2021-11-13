package com.example.everybody_android.ui.picture

import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.example.everybody_android.api.PictureRepo
import com.example.everybody_android.base.BaseViewModel
import com.example.everybody_android.base.MutableEventFlow
import com.example.everybody_android.base.asEventFlow
import com.example.everybody_android.prepareFilePart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class PictureViewModel @Inject constructor() : BaseViewModel() {
    private val _clickEvent = MutableEventFlow<ClickEvent>()
    val clickEvent = _clickEvent.asEventFlow()

    fun onClickEvent(event: ClickEvent) {
        viewModelScope.launch { _clickEvent.emit(event) }
    }

    fun photoUpload(valueMap: Map<String, String>) {
        val map = hashMapOf<String,String>()
        map.putAll(valueMap)
        val image = map.remove("image") ?: return
        val partMap = valueMap.mapValues { it.value.toRequestBody() }
        val filePart = prepareFilePart("image", image.toUri())
        runScope({
            PictureRepo.uploadPicture(partMap, filePart)
        }) {
            viewModelScope.launch { _clickEvent.emit(ClickEvent.Complete) }
        }
    }

    sealed class ClickEvent {
        object Back : ClickEvent()
        object Next : ClickEvent()
        object Complete : ClickEvent()
    }

}