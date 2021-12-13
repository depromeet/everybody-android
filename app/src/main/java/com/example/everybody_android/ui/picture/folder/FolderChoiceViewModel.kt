package com.def.everybody_android.ui.picture.folder

import android.graphics.drawable.Drawable
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
class FolderChoiceViewModel @Inject constructor() : BaseViewModel() {

    private val _albumsResponse = MutableEventFlow<AlbumsResponse>()
    val albumsResponse = _albumsResponse.asEventFlow()
    var valueMap = hashMapOf<String, String>()
    fun getAlbums() {
        runScope({
            AlbumRepo.getAlbums()
        }) {
            viewModelScope.launch { _albumsResponse.emit(it) }
        }
    }

    data class Item(
        val imageUrl: String,
        val name: String,
        val holder: Drawable,
        val isCheck: Boolean,
        val id: Int,
        val description: String
    )

}