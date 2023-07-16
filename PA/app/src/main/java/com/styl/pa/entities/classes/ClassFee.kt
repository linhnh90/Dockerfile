package com.styl.pa.entities.classes

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class ClassFee() :Parcelable {
    @SerializedName("classFeeId")
//    @Expose
    private var classFeeId: String? = null
    @SerializedName("classId")
//    @Expose
    private var classId: String? = null
    @SerializedName("feeTypeId")
//    @Expose
    private var feeTypeId: Int? = null
    @SerializedName("feeTypeName", alternate = ["feeType"])
//    @Expose
    private var feeTypeName: String? = null
    @SerializedName("feeDescription")
//    @Expose
    private var feeDescription: String? = null
    @SerializedName("classFeeAmount", alternate = ["feeAmount"])
//    @Expose
    private var classFeeAmount: String? = null
    @SerializedName("classFeeName", alternate = ["feeName"])
//    @Expose
    private var classFeeName: String? = null

    @SerializedName("name")
    var name: String? = null

    constructor(parcel: Parcel) : this() {
        classFeeId = parcel.readString()
        classId = parcel.readString()
        feeTypeId = parcel.readValue(Int::class.java.classLoader) as? Int
        feeTypeName = parcel.readString()
        feeDescription = parcel.readString()
        classFeeAmount = parcel.readString()
        classFeeName = parcel.readString()
    }

    fun getClassFeeId(): String? {
        return classFeeId
    }

    fun setClassFeeId(classFeeId: String) {
        this.classFeeId = classFeeId
    }

    fun getClassId(): String? {
        return classId
    }

    fun setClassId(classId: String) {
        this.classId = classId
    }

    fun getFeeTypeId(): Int? {
        return feeTypeId
    }

    fun setFeeTypeId(feeTypeId: Int?) {
        this.feeTypeId = feeTypeId
    }

    fun getFeeTypeName(): String? {
        return feeTypeName
    }

    fun setFeeTypeName(feeTypeName: String) {
        this.feeTypeName = feeTypeName
    }

    fun getFeeDescription(): String? {
        return feeDescription
    }

    fun setFeeDescription(feeDescription: String) {
        this.feeDescription = feeDescription
    }

    fun getClassFeeAmount(): String? {
        return classFeeAmount
    }

    fun setClassFeeAmount(classFeeAmount: String) {
        this.classFeeAmount = classFeeAmount
    }

    fun getClassFeeName(): String? {
        return classFeeName
    }

    fun setClassFeeName(classFeeName: String) {
        this.classFeeName = classFeeName
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(classFeeId)
        parcel.writeString(classId)
        parcel.writeValue(feeTypeId)
        parcel.writeString(feeTypeName)
        parcel.writeString(feeDescription)
        parcel.writeString(classFeeAmount)
        parcel.writeString(classFeeName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClassFee> {
        override fun createFromParcel(parcel: Parcel): ClassFee {
            return ClassFee(parcel)
        }

        override fun newArray(size: Int): Array<ClassFee?> {
            return arrayOfNulls(size)
        }
    }
}