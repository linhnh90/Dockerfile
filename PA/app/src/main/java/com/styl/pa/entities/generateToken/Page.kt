package com.styl.pa.entities.generateToken

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

open class Page() : Parcelable{
    @SerializedName("pageSize")
    private var pageSize: Int? = -1
    @SerializedName("pageIndex")
    private var pageIndex: Int? = -1
    @SerializedName("totalRecord")
    private var totalRecord: Int? = -1

    constructor(parcel: Parcel) : this() {
        pageSize = parcel.readValue(Int::class.java.classLoader) as? Int
        pageIndex = parcel.readValue(Int::class.java.classLoader) as? Int
        totalRecord = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    fun getPageSize(): Int? {
        return pageSize
    }

    fun setPageSize(pageSize: Int?) {
        this.pageSize = pageSize
    }

    fun getPageIndex(): Int? {
        return pageIndex
    }

    fun setPageIndex(pageIndex: Int?) {
        this.pageIndex = pageIndex
    }

    fun getTotalRecord(): Int? {
        return totalRecord
    }

    fun setTotalRecord(totalRecord: Int?) {
        this.totalRecord = totalRecord
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(pageSize)
        parcel.writeValue(pageIndex)
        parcel.writeValue(totalRecord)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Page> {
        override fun createFromParcel(parcel: Parcel): Page {
            return Page(parcel)
        }

        override fun newArray(size: Int): Array<Page?> {
            return arrayOfNulls(size)
        }
    }

}