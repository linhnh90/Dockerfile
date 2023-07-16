package com.styl.pa.entities.search

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

open class SearchRequest : Parcelable {
    @SerializedName("Keyword")
    var keyword: String? = null
    @SerializedName("OutletIds")
    var outletId: ArrayList<String>? = null
    @SerializedName("OutletNames")
    var outletNames: ArrayList<String>? = null
    @SerializedName("Price")
    var price: PriceClass? = null
    @SerializedName("Day")
    var day: ArrayList<Int>? = null
    @SerializedName("NearestLocation")
    var nearestLocation: String? = null
    @SerializedName("Distance")
    var distance: Int? = null
    @SerializedName("PageIndex")
    var pageIndex: Int? = null
    @SerializedName("PageSize")
    var pageSize: Int? = null


    constructor()

    constructor(keyword: String?, outletId: ArrayList<String>?, outletNames: ArrayList<String>?, price: PriceClass?, day: ArrayList<Int>?, pageIndex: Int?, pageSize: Int?) {
        this.keyword = keyword
        this.outletId = outletId
        this.outletNames = outletNames
        this.price = price
        this.day = day
        this.pageIndex = pageIndex
        this.pageSize = pageSize
    }

    constructor(keyword: String?, nearestLocation: String, distance: Int, price: PriceClass?, day: ArrayList<Int>?, pageIndex: Int?, pageSize: Int?) {
        this.keyword = keyword
        this.nearestLocation = nearestLocation
        this.distance = distance
        this.price = price
        this.day = day
        this.pageIndex = pageIndex
        this.pageSize = pageSize
    }

    constructor(keyword: String?, nearestLocation: String, distance: Int, price: PriceClass?) {
        this.keyword = keyword
        this.nearestLocation = nearestLocation
        this.distance = distance
        this.price = price
    }

    constructor(parcel: Parcel) : this() {
        keyword = parcel.readString()
        pageIndex = parcel.readValue(Int::class.java.classLoader) as? Int
        pageSize = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(keyword)
        parcel.writeValue(pageIndex)
        parcel.writeValue(pageSize)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchRequest> {
        override fun createFromParcel(parcel: Parcel): SearchRequest {
            return SearchRequest(parcel)
        }

        override fun newArray(size: Int): Array<SearchRequest?> {
            return arrayOfNulls(size)
        }
    }
}