package com.def.everybody_android.db

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

open class MainFeedPictureData : RealmObject() {

    @Required
    var albumId: Long = -1
    @PrimaryKey
    var pictureId: Long = -1
    lateinit var bodyPart: String
    lateinit var imagePath: String
    lateinit var pictureCreated: Date
}
