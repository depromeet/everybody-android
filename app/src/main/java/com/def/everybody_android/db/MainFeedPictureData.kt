package com.def.everybody_android.db

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

open class MainFeedPictureData : RealmObject() {

    var albumId: Long = -1
    lateinit var bodyPart: String
    lateinit var imagePath: String
    lateinit var pictureCreated: Date
}
