package com.def.everybody_android.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
import com.def.everybody_android.data.response.base.Picture
import com.def.everybody_android.db.Album
import com.def.everybody_android.db.MainFeedPictureData
import com.def.everybody_android.di.HiltApplication
import com.def.everybody_android.dto.Feed
import com.def.everybody_android.dto.UserData
import com.def.everybody_android.dto.request.SignInRequest
import com.def.everybody_android.dto.request.SignUpRequest
import com.def.everybody_android.nextAlbumId
import com.def.everybody_android.pref.LocalStorage
import com.def.everybody_android.toFeed
import com.def.everybody_android.toFeeds
import com.def.everybody_android.ui.dialog.loading.LoadingDialog
import com.def.everybody_android.ui.dialog.migrations.MigrationsDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.Realm
import io.realm.RealmList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
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
        return if (results.isEmpty()) { // ????????? ????????? ????????????
            firstAddAlbum()
            val album = realm.where(Album::class.java).findFirst()
            listOf(album?.toFeed() ?: return listOf())
        } else results.toFeeds()
    }

    @SuppressLint("SimpleDateFormat")
    fun getAlbum(activity: AppCompatActivity, loadingDialog: MigrationsDialog) {
        runScope({
            AlbumRepo.getAlbums()
        }) { data ->
            val dataList = data.filter { !it.thumbnailUrl.isNullOrEmpty() }
            if(dataList.isEmpty()) return@runScope
            CoroutineScope(Dispatchers.Main).launch {
                loadingDialog.show(activity.supportFragmentManager, "")
                launch(Dispatchers.IO) {
                    val migrations = dataList.map { album ->
                        val pictures =
                            listOf(album.pictures.whole.orEmpty(), album.pictures.upper.orEmpty(), album.pictures.lower.orEmpty()).flatten()
                        Migration(album.id, album.name, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(album.createdAt), pictures.map { picture ->
                            MainFeedPictureData().apply {
                                bodyPart = picture.bodyPart
                                imagePath = loadImage(activity, picture).path
                                pictureCreated = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(picture.createdAt)
                            }
                        })
                    }
                    migrations.forEach { migration ->
                        Realm.getDefaultInstance().executeTransaction {
                            with(it.createObject(Album::class.java, it.nextAlbumId())) {
                                this.name = migration.name
                                feedCreated = migration.createdAt
                                feedPictureDataList.addAll(migration.pictures)
                            }
                        }
                    }
                    loadingDialog.dismiss()
                    migrationCompleted()
                }
            }
        }
    }

    private fun migrationCompleted(){
        runScope({
            UserRepo.migrationCompleted()
        }){
            onClickEvent(ClickEvent.Migration)
            settingFeedList()
        }
    }

    data class Migration(
        val id: Int, // 10
        val name: String, // ???????????? ??? ?????? ??????!
        val createdAt: Date, // 2021-11-02T13:54:29+09:00
        val pictures: List<MainFeedPictureData>
    )

    private fun loadImage(context: Context, picture: Picture): File {
        val url = URL(picture.imageUrl)
        val stream = url.openStream()
        val nameFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
        val createdAtFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        // ?????? ??????
        val file = File(context.filesDir, nameFormat.format(createdAtFormat.parse(picture.createdAt)) + ".jpg")
        // ????????? ????????? ??????
        val fileOutputStream = FileOutputStream(file)
        // ????????? ????????? ??????
        BitmapFactory.decodeStream(stream).compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        // ????????? ????????? ??????
        fileOutputStream.flush()
        // ????????? ????????? ??????
        fileOutputStream.close()
        return file
    }

    private fun firstAddAlbum() {
        realm.executeTransaction {
            with(it.createObject(Album::class.java, 1)) {
                name = "?????????"
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
        object Migration:ClickEvent()
        data class GetUserData(val data: UserData) : ClickEvent()
        data class PanoramaActivity(val data: Feed) : ClickEvent()
    }

}