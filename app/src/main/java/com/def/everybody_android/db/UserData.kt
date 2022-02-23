package com.def.everybody_android.db

import io.realm.RealmObject
import java.io.Serializable

open class UserData : RealmObject(), Serializable {
    var nickName: String = "눈린이"
    var motto: String = "천천히 그리고 꾸준히!"
    var profileImage: String = ""
}
