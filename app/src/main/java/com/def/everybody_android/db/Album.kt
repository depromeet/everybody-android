package com.def.everybody_android.db

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

open class Album : RealmObject() {
    @PrimaryKey
    var id: Long = -1
    @Required
    lateinit var name: String
    @Required
    lateinit var feedCreated: Date
    var feedPictureDataList: RealmList<MainFeedPictureData> = RealmList()
}