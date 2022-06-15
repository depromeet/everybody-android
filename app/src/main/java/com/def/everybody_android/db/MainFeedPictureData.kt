package com.def.everybody_android.db

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

open class MainFeedPictureData() : RealmObject(), Parcelable {

    var albumId: Long = -1
    lateinit var bodyPart: String
    lateinit var imagePath: String
    lateinit var pictureCreated: Date

    constructor(parcel: Parcel) : this() {
        albumId = parcel.readLong()
        bodyPart = parcel.readString() ?: ""
        imagePath = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(albumId)
        parcel.writeString(bodyPart)
        parcel.writeString(imagePath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainFeedPictureData> {
        override fun createFromParcel(parcel: Parcel): MainFeedPictureData {
            return MainFeedPictureData(parcel)
        }

        override fun newArray(size: Int): Array<MainFeedPictureData?> {
            return arrayOfNulls(size)
        }
    }
}
