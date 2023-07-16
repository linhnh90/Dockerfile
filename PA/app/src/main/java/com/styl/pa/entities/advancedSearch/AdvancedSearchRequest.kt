package com.styl.pa.entities.advancedSearch

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.search.PriceClass
import com.styl.pa.entities.search.SearchRequest

class AdvancedSearchRequest : SearchRequest, Parcelable {

    @SerializedName("StartDate")
    var startDate: String? = null
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("Categories")
    var categories: ArrayList<String>? = null
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("Suitable")
    var suitable: ArrayList<String>? = null
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("HashQueryNext")
    var hashQueryNext: String? = null
        get() = field
        set(value) {
            field = value
        }

    constructor(parcel: Parcel) : this() {
    }


    constructor()

    constructor(keyword: String?, nearestLocation: String, distance: Int, price: PriceClass?)
            : super(keyword, nearestLocation, distance, price) {
    }

    constructor(keyword: String?, outletId: ArrayList<String>?, outletNames: ArrayList<String>?, price: PriceClass?, day: ArrayList<Int>?, pageIndex: Int?, pageSize: Int?)
            : super(keyword, outletId, outletNames, price, day, pageIndex, pageSize) {
    }

    constructor(keyword: String?, nearestLocation: String, distance: Int, price: PriceClass?, day: ArrayList<Int>?, pageIndex: Int?, pageSize: Int?)
            : super(keyword, nearestLocation, distance, price, day, pageIndex, pageSize) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AdvancedSearchRequest> {
        override fun createFromParcel(parcel: Parcel): AdvancedSearchRequest {
            return AdvancedSearchRequest(parcel)
        }

        override fun newArray(size: Int): Array<AdvancedSearchRequest?> {
            return arrayOfNulls(size)
        }
    }


}