package com.styl.pa.entities.courseCategory

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.advancedSearch.MultiSelectionObject


/**
 * Created by Ngatran on 10/11/2018.
 */
class CourseCategory : Parcelable, MultiSelectionObject {
    @SerializedName(value = "categoryId", alternate= ["categoryID"])
    @Expose
    private var categoryId: String? = null
    @SerializedName("categoryName", alternate = ["category"])
    @Expose
    private var categoryName: String? = null
    @SerializedName("categoryDescription", alternate = ["description"])
    @Expose
    private var categoryDescription: String? = null
    @SerializedName("imageUrl", alternate = ["image"])
    @Expose
    private var imageUrl: String? = null

    constructor()

    constructor(cate: CourseCategory) {
        categoryId = cate.categoryId
        categoryName = cate.categoryName
        categoryDescription = cate.categoryDescription
        imageUrl = cate.imageUrl
    }

    constructor(parcel: Parcel) : this() {
        categoryId = parcel.readString()
        categoryName = parcel.readString()
        categoryDescription = parcel.readString()
        imageUrl = parcel.readString()
    }

    fun getCategoryId(): String? {
        return categoryId
    }

    fun setCategoryId(categoryId: String) {
        this.categoryId = categoryId
    }

    fun getCategoryName(): String? {
        return categoryName
    }

    fun setCategoryName(categoryName: String) {
        this.categoryName = categoryName
    }

    fun getCategoryDescription(): String? {
        return categoryDescription
    }

    fun setCategoryDescription(categoryDescription: String) {
        this.categoryDescription = categoryDescription
    }

    fun getImageUrl(): String? {
        return imageUrl
    }

    fun setImageUrl(imageUrl: String) {
        this.imageUrl = imageUrl
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(categoryId)
        parcel.writeString(categoryName)
        parcel.writeString(categoryDescription)
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CourseCategory> {
        override fun createFromParcel(parcel: Parcel): CourseCategory {
            return CourseCategory(parcel)
        }

        override fun newArray(size: Int): Array<CourseCategory?> {
            return arrayOfNulls(size)
        }
    }

}