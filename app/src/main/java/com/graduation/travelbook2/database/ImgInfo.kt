package com.graduation.travelbook2.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "ImgInfo")
data class ImgInfo(
    val path: String?,
    val latting: Double?,
    // 테이블의 자료형은 이미 선언된 키워드는 사용하면 안된다!
    val longtitude: Double?,
    val locality: String?,
    val date: String?,
    val isChecked: Boolean = false,
): Parcelable {
    @PrimaryKey(autoGenerate = true) var id: Int = 0

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
        id = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(path)
        parcel.writeValue(latting)
        parcel.writeValue(longtitude)
        parcel.writeString(locality)
        parcel.writeString(date)
        parcel.writeByte(if (isChecked) 1 else 0)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImgInfo> {
        override fun createFromParcel(parcel: Parcel): ImgInfo {
            return ImgInfo(parcel)
        }

        override fun newArray(size: Int): Array<ImgInfo?> {
            return arrayOfNulls(size)
        }
    }
}

