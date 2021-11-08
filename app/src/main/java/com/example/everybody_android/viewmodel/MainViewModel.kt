package com.example.everybody_android.viewmodel

import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.viewModelScope
import com.example.everybody_android.R
import com.example.everybody_android.adapter.RecyclerItem
import com.example.everybody_android.adapter.RecyclerViewAdapter
import com.example.everybody_android.base.BaseViewModel
import com.example.everybody_android.di.ApiModule
import com.example.everybody_android.dto.MainFeedData
import com.example.everybody_android.dto.MainFeedPictureData
import com.example.everybody_android.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModel() {

    private var feedStatus = true
    private var recyclerStatus = true

    private var testFeedData = ArrayList<MainFeedData>()

    val halfFeedAdapter = RecyclerViewAdapter()
    val fullFeedAdapter = RecyclerViewAdapter()

    fun sortFeed(){
        recyclerStatus = !recyclerStatus
        if (testFeedData.size == 0) feedStatus = false
        else {
            if (recyclerStatus) {
                halfFeedAdapter.changeData(testFeedData.map { it.toHalfRecyclerItem() })
                fullFeedAdapter.clearData()
            }
            else {
                fullFeedAdapter.changeData(testFeedData.map { it.toFullRecyclerItem() })
                halfFeedAdapter.clearData()
            }
        }
    }

    fun testFeed() {
        testFeedData = arrayListOf(
            MainFeedData(
                1, "피드이름", "YY.MM-YY.MM",
                MainFeedPictureData(1, 1, "얼굴", "YY.MM-YY.MM", "", "", ""),
            ),
            MainFeedData(
                1, "피드이름", "YY.MM-YY.MM",
                MainFeedPictureData(1, 1, "얼굴", "YY.MM-YY.MM", "", "", "")
            ),
            MainFeedData(
                1, "피드이름", "YY.MM-YY.MM",
                MainFeedPictureData(1, 1, "얼굴", "YY.MM-YY.MM", "", "", "")
            ),
            MainFeedData(
                1, "피드이름", "YY.MM-YY.MM",
                MainFeedPictureData(1, 1, "얼굴", "YY.MM-YY.MM", "", "", "")
            ),
            MainFeedData(
                1, "피드이름", "YY.MM-YY.MM",
                MainFeedPictureData(1, 1, "얼굴", "YY.MM-YY.MM", "", "", "")
            )
        )
        sortFeed()
    }


    private fun MainFeedData.toFullRecyclerItem() =
        RecyclerItem(
            data = this,
            variableId = BR.repo,
            layoutId = R.layout.item_full_mainfeed
        )

    private fun MainFeedData.toHalfRecyclerItem() =
        RecyclerItem(
            data = this,
            variableId = BR.repo,
            layoutId = R.layout.item_half_mainfeed
        )
}