package com.graduation.travelbook2.internalDto

import android.os.Parcel
import android.os.Parcelable
import com.graduation.travelbook2.database.ImgInfo


data class SelectedImgDto(
    var imgIndex: Int,
    val imgInfo: ImgInfo?,
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readParcelable(ImgInfo::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(imgIndex)
        parcel.writeParcelable(imgInfo, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SelectedImgDto> {
        override fun createFromParcel(parcel: Parcel): SelectedImgDto {
            return SelectedImgDto(parcel)
        }

        override fun newArray(size: Int): Array<SelectedImgDto?> {
            return arrayOfNulls(size)
        }
    }
}

