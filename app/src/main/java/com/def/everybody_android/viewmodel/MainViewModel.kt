package com.def.everybody_android.viewmodel

import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.def.everybody_android.R
import com.def.everybody_android.adapter.RecyclerItem
import com.def.everybody_android.adapter.RecyclerViewAdapter
import com.def.everybody_android.api.AlbumRepo
import com.def.everybody_android.api.SignRepo
import com.def.everybody_android.api.UserRepo
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import com.def.everybody_android.di.HiltApplication
import com.def.everybody_android.dto.UserData
import com.def.everybody_android.dto.request.SignInRequest
import com.def.everybody_android.dto.request.SignUpRequest
import com.def.everybody_android.dto.response.MainFeedResponse
import com.def.everybody_android.pref.LocalStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModel() {

    private var feedStatus = true
    private var recyclerStatus = true

    private var feedData = ArrayList<MainFeedResponse>()

    @Inject
    lateinit var localStorage: LocalStorage

    val halfFeedAdapter = RecyclerViewAdapter {
        if (it is MainFeedResponse) onClickEvent(ClickEvent.PanoramaActivity(it))
        else onClickEvent(ClickEvent.FeedBack)
    }
    val fullFeedAdapter = RecyclerViewAdapter {
        if (it is MainFeedResponse) onClickEvent(ClickEvent.PanoramaActivity(it))
        else onClickEvent(ClickEvent.FeedBack)
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
            localStorage.saveUserId(data.userId)
            signIn(SignInRequest(data.userId, "1234"))
        }
    }

    fun login(map:Map<String,String>){
        runScope({
            SignRepo.oauthLogin(map)
        }) { data->
            HiltApplication.token = data.accessToken
            onClickEvent(ClickEvent.Sign)
        }
    }

    fun signIn(signInRequest: SignInRequest) {
        runScope({
            SignRepo.signIn(signInRequest)
        }) { data ->
            HiltApplication.token = data.accessToken
            onClickEvent(ClickEvent.Sign)
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
            val halfList = data.map { it.toHalfRecyclerItem() }.toMutableList()
            halfList.add(getBottomRecyclerItem())
            val fullList = data.map { it.toFullRecyclerItem() }.toMutableList()
            fullList.add(getBottomRecyclerItem())
            halfFeedAdapter.changeData(halfList)
            fullFeedAdapter.changeData(fullList)
            if (data.isNotEmpty()) noFeed.value = false
        }
    }

    private fun getBottomRecyclerItem() = RecyclerItem(
        data = "",
        variableId = BR.repo,
        layoutId = R.layout.item_bottom_feedback
    )

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
        object Sign : ClickEvent()
        object FeedBack : ClickEvent()
        data class GetFeedData(val data: ArrayList<MainFeedResponse>) : ClickEvent()
        data class GetUserData(val data: UserData) : ClickEvent()
        data class PanoramaActivity(val data: MainFeedResponse) : ClickEvent()
    }

}