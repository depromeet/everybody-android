package com.example.everybody_android.ui.picture.folder

import android.graphics.drawable.Drawable
import androidx.lifecycle.viewModelScope
import com.example.everybody_android.api.AlbumRepo
import com.example.everybody_android.base.BaseViewModel
import com.example.everybody_android.base.MutableEventFlow
import com.example.everybody_android.base.asEventFlow
import com.example.everybody_android.data.response.AlbumsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderChoiceViewModel @Inject constructor() : BaseViewModel() {

    private val _albumsResponse = MutableEventFlow<AlbumsResponse>()
    val albumsResponse = _albumsResponse.asEventFlow()

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
        val hash: Int,
        val description: String
    )

}