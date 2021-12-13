package com.def.everybody_android.viewmodel

import androidx.lifecycle.viewModelScope
import com.def.everybody_android.api.AlbumRepo
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFolderViewModel @Inject constructor() : BaseViewModel() {

    private val _clickEvent = MutableEventFlow<MainViewModel.ClickEvent>()
    val clickEvent = _clickEvent.asEventFlow()

    fun onClickEvent(event: MainViewModel.ClickEvent) {
        viewModelScope.launch { _clickEvent.emit(event) }
    }

    fun createFolder(name : String){
        runScope({
            AlbumRepo.createAlbums(mapOf("name" to name))
        }){
            data -> println(data)
        }
    }
}