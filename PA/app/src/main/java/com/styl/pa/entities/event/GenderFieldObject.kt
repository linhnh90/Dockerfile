package com.styl.pa.entities.event

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class GenderFieldObject() : Parcelable {
    @SerializedName("Items")
    var item: ArrayList<Item>? = ArrayList()

    @SerializedName("Value")
    var value: Item? = null

    @SerializedName("ItemName")
    var itemName: String? = null

    @SerializedName("Title")
    var title: String? = null

    @SerializedName("FieldName")
    var fieldName: String? = null

    @SerializedName("PlaceHolder")
    var placeHolder: String? = null

    @SerializedName("Type")
    var type: String? = null

    @SerializedName("Validation")
    var validation: Validation? = null

    @SerializedName("IgnoreOnClear")
    var ignoreOnClear: Boolean? = null

    @SerializedName("IsFullWidth")
    var isFullWidth: Boolean? = null

    @SerializedName("UniqueField")
    var uniqueField: Boolean? = null


    var inputValue: String? = null

    constructor(parcel: Parcel) : this() {
        itemName = parcel.readString()
        title = parcel.readString()
        fieldName = parcel.readString()
        placeHolder = parcel.readString()
        type = parcel.readString()
        ignoreOnClear = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        isFullWidth = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        uniqueField = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        inputValue = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(itemName)
        parcel.writeString(title)
        parcel.writeString(fieldName)
        parcel.writeString(placeHolder)
        parcel.writeString(type)
        parcel.writeValue(ignoreOnClear)
        parcel.writeValue(isFullWidth)
        parcel.writeValue(uniqueField)
        parcel.writeString(inputValue)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GenderFieldObject> {
        override fun createFromParcel(parcel: Parcel): GenderFieldObject {
            return GenderFieldObject(parcel)
        }

        override fun newArray(size: Int): Array<GenderFieldObject?> {
            return arrayOfNulls(size)
        }
    }
}