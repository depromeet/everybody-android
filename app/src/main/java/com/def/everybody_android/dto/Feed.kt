package com.def.everybody_android.dto

import com.def.everybody_android.db.MainFeedPictureData
import java.util.*

data class Feed(
    val id: Long,
    val name: String,
    val feedCreated: Date,
    val description: String,
    val image: String,
    val placeHolder: Int,
    val feedPicture: List<MainFeedPictureData>,
    val isBlind: Boolean
)