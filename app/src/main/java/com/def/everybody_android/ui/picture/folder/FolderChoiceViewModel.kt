package com.def.everybody_android.ui.picture.folder

import androidx.lifecycle.viewModelScope
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import com.def.everybody_android.db.Album
import com.def.everybody_android.dto.Feed
import com.def.everybody_android.toFeeds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderChoiceViewModel @Inject constructor() : BaseViewModel() {

    private val _feedsResponse = MutableEventFlow<List<Feed>>()
    val feedsResponse = _feedsResponse.asEventFlow()
    var valueMap = hashMapOf<String, String>()
    fun getFeeds() {
        val results = realm.where(Album::class.java).findAll()
        viewModelScope.launch {
            _feedsResponse.emit(results.toFeeds())
        }
    }

    data class Item(
        val id: Long,
        val imageUrl: String,
        val name: String,
        val holder: Int,
        val isCheck: Boolean,
        val description: String
    )

}