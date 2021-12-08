package com.example.everybody_android.viewmodel

import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.everybody_android.R
import com.example.everybody_android.adapter.RecyclerItem
import com.example.everybody_android.adapter.RecyclerViewAdapter
import com.example.everybody_android.api.AlbumRepo
import com.example.everybody_android.api.SignRepo
import com.example.everybody_android.api.UserRepo
import com.example.everybody_android.base.BaseViewModel
import com.example.everybody_android.base.MutableEventFlow
import com.example.everybody_android.base.asEventFlow
import com.example.everybody_android.dto.UserData
import com.example.everybody_android.dto.request.SignInRequest
import com.example.everybody_android.dto.request.SignUpRequest
import com.example.everybody_android.dto.response.MainFeedResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModel() {

    private var feedStatus = true
    private var recyclerStatus = true

    private var feedData = ArrayList<MainFeedResponse>()

    val halfFeedAdapter = RecyclerViewAdapter {
        onClickEvent(ClickEvent.PanoramaActivity(it as MainFeedResponse))
    }
    val fullFeedAdapter = RecyclerViewAdapter {
        onClickEvent(ClickEvent.PanoramaActivity(it as MainFeedResponse))
    }

    var noFeed = MutableLiveData(true)

    private val _clickEvent = MutableEventFlow<ClickEvent>()
    val clickEvent = _clickEvent.asEventFlow()

    fun onClickEvent(event: ClickEvent) {
        viewModelScope.launch { _clickEvent.emit(event) }
    }

    fun sortFeed() {
        onClickEvent(ClickEvent.SortFeed)
        recyclerStatus = !recyclerStatus
        if (feedData.size == 0) feedStatus = false
        else {
            if (recyclerStatus) {
                halfFeedAdapter.changeData(feedData.map { it.toHalfRecyclerItem() })
            } else {
                fullFeedAdapter.changeData(feedData.map { it.toFullRecyclerItem() })
            }
        }
    }

    fun signUp(signUpRequest: SignUpRequest) {
        runScope({
            SignRepo.signUp(signUpRequest)
        }) { data ->
            println("signup $data")
        }
    }

    fun signIn(signInRequest: SignInRequest) {
        runScope({
            SignRepo.signIn(signInRequest)
        }) { data ->
            println("signIn $data")
        }
    }

    fun getUserData() {
        runScope({
            UserRepo.getUserData()
        }) { data ->
            onClickEvent(ClickEvent.GetUserData(data))
        }
    }

    fun getAlbum() {
        runScope({
            AlbumRepo.getMainFeed()
        }) { data ->
            halfFeedAdapter.changeData(data.map { it.toHalfRecyclerItem() })
            fullFeedAdapter.changeData(data.map { it.toFullRecyclerItem() })
            if (data.isNotEmpty()) noFeed.value = false
        }
    }

    private fun MainFeedResponse.toFullRecyclerItem() =
        RecyclerItem(
            data = this,
            variableId = BR.repo,
            layoutId = R.layout.item_full_mainfeed
        )

    private fun MainFeedResponse.toHalfRecyclerItem() =
        RecyclerItem(
            data = this,
            variableId = BR.repo,
            layoutId = R.layout.item_half_mainfeed
        )

    sealed class ClickEvent {
        object SortFeed : ClickEvent()
        data class GetFeedData(val data: ArrayList<MainFeedResponse>) : ClickEvent()
        data class GetUserData(val data: UserData) : ClickEvent()
        data class PanoramaActivity(val data: MainFeedResponse) : ClickEvent()
    }

}