package com.styl.pa.entities.classes

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Ngatran on 09/27/2019.
 */
class ClassPrerequisite : Parcelable {
    @SerializedName("courseId")
    var courseId: String? = null
    @SerializedName("courseTitle")
    var courseTitle: String? = null

    constructor(parcel: Parcel) : this() {
        courseId = parcel.readString()
        courseTitle = parcel.readString()
    }

    constructor()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(courseId)
        parcel.writeString(courseTitle)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClassPrerequisite> {
        override fun createFromParcel(parcel: Parcel): ClassPrerequisite {
            return ClassPrerequisite(parcel)
        }

        override fun newArray(size: Int): Array<ClassPrerequisite?> {
            return arrayOfNulls(size)
        }
    }
}