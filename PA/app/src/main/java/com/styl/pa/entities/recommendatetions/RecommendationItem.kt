package com.styl.pa.entities.recommendatetions

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.enums.SearchType

/**
 * Created by Ngatran on 03/12/2019.
 */
class RecommendationItem : Parcelable {
    @SerializedName("typeItem")
    @Expose
    var typeItem: String? = null

    @SerializedName("infoItem")
    @Expose
    var infoItem: Any? = null

    constructor()

    constructor(infoItem: ClassInfo) {
        typeItem = SearchType.COURSES.toString()
        this.infoItem = infoItem
    }

    constructor(infoItem: EventInfo) {
        typeItem = SearchType.EVENTS.toString()
        this.infoItem = infoItem
    }

    constructor(parcel: Parcel) : this() {
        typeItem = parcel.readString()
        if (SearchType.COURSES.toString().equals(typeItem)) {
            infoItem = parcel.readValue(ClassInfo::class.java.classLoader)
        } else if (SearchType.EVENTS.toString().equals(typeItem)) {
            infoItem = parcel.readValue(EventInfo::class.java.classLoader)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(typeItem)
        if (SearchType.COURSES.toString().equals(typeItem)) {
            parcel.writeParcelable(infoItem as ClassInfo, flags)
        } else if (SearchType.EVENTS.toString().equals(typeItem)) {
            parcel.writeParcelable(infoItem as EventInfo, flags)
        }

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecommendationItem> {
        override fun createFromParcel(parcel: Parcel): RecommendationItem {
            return RecommendationItem(parcel)
        }

        override fun newArray(size: Int): Array<RecommendationItem?> {
            return arrayOfNulls(size)
        }
    }

}