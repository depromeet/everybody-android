package com.def.everybody_android.viewmodel

import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.def.everybody_android.R
import com.def.everybody_android.adapter.RecyclerItem
import com.def.everybody_android.adapter.RecyclerViewAdapter
import com.def.everybody_android.api.SignRepo
import com.def.everybody_android.api.UserRepo
import com.def.everybody_android.base.BaseViewModel
import com.def.everybody_android.base.MutableEventFlow
import com.def.everybody_android.base.asEventFlow
import com.def.everybody_android.db.Album
import com.def.everybody_android.db.MainFeedPictureData
import com.def.everybody_android.di.HiltApplication
import com.def.everybody_android.dto.Feed
import com.def.everybody_android.dto.UserData
import com.def.everybody_android.dto.request.SignInRequest
import com.def.everybody_android.dto.request.SignUpRequest
import com.def.everybody_android.pref.LocalStorage
import com.def.everybody_android.toFeed
import com.def.everybody_android.toFeeds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModel() {

    private var feedStatus = true
    private var recyclerStatus = true

    private var feedData = mutableListOf<Feed>()

    @Inject
    lateinit var localStorage: LocalStorage

    val halfFeedAdapter = RecyclerViewAdapter {
        if (it is Feed) onClickEvent(ClickEvent.PanoramaActivity(it))
        else onClickEvent(ClickEvent.FeedBack)
    }
    val fullFeedAdapter = RecyclerViewAdapter {
        if (it is Feed) onClickEvent(ClickEvent.PanoramaActivity(it))
        else onClickEvent(ClickEvent.FeedBack)
    }

    var noFeed = MutableLiveData(true)

    private val _clickEvent = MutableEventFlow<ClickEvent>()
    val clickEvent = _clickEvent.asEventFlow()

    fun onClickEvent(event: ClickEvent) {
        viewModelScope.launch { _clickEvent.emit(event) }
    }

    fun signUp(signUpRequest: SignUpRequest) {
        runScope({
            SignRepo.signUp(signUpRequest)
        }) { data ->
            localStorage.saveUserId(data.userId)
            signIn(SignInRequest(data.userId, "1234"))
        }
    }

    fun login(map: HashMap<String, Any>) {
        runScope({
            SignRepo.oauthLogin(map)
        }) { data ->
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

    fun onSortFeedClick() {
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

    fun settingFeedList() {
        feedData.clear()
        feedData.addAll(getFeeds())
        val halfList = feedData.map { it.toHalfRecyclerItem() }.toMutableList()
        halfList.add(getBottomRecyclerItem())
        val fullList = feedData.map { it.toFullRecyclerItem() }.toMutableList()
        fullList.add(getBottomRecyclerItem())
        halfFeedAdapter.changeData(halfList)
        fullFeedAdapter.changeData(fullList)
        if (feedData.isNotEmpty()) noFeed.value = false
    }

    private fun getFeeds(): List<Feed> {
        val results = realm.where(Album::class.java).findAll()
        return if (results.isEmpty()) { // 앨범이 하나도 없을경우
            firstAddAlbum()
            val album = realm.where(Album::class.java).findFirst()
            listOf(album?.toFeed() ?: return listOf())
        } else results.toFeeds()
    }

    private fun firstAddAlbum() {
        realm.executeTransaction {
            with(it.createObject(Album::class.java, 1)) {
                name = "눈바디"
                feedCreated = Date()
            }
        }
    }

    private fun getBottomRecyclerItem() = RecyclerItem(
        data = "",
        variableId = BR.repo,
        layoutId = R.layout.item_bottom_feedback
    )

    private fun Feed.toFullRecyclerItem() =
        RecyclerItem(
            data = this,
            variableId = BR.repo,
            layoutId = R.layout.item_full_mainfeed
        )

    private fun Feed.toHalfRecyclerItem() =
        RecyclerItem(
            data = this,
            variableId = BR.repo,
            layoutId = R.layout.item_half_mainfeed
        )

    sealed class ClickEvent {
        object SortFeed : ClickEvent()
        object FeedBack : ClickEvent()
        object Created : ClickEvent()
        object Sign : ClickEvent()
        data class GetUserData(val data: UserData) : ClickEvent()
        data class PanoramaActivity(val data: Feed) : ClickEvent()
    }

}