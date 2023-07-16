package com.styl.pa.entities.generateToken

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.advancedSearch.MultiSelectionObject

class Outlet : Parcelable, MultiSelectionObject {

    @SerializedName("outletId")
    @Expose
    private var outletId: String? = null
    @SerializedName("outletTypeId")
    @Expose
    private var outletTypeId: String? = null
    @SerializedName("imageUrl")
    @Expose
    private var imageUrl: String? = null
    @SerializedName("name")
    @Expose
    private var name: String? = null
    @SerializedName("friendlyName")
    @Expose
    private var friendlyName: String? = null
    @SerializedName("address")
    @Expose
    private var address: String? = null
    @SerializedName("postcode")
    @Expose
    private var postcode: String? = null
    @SerializedName("longitude")
    @Expose
    private var longitude: Double? = null
    @SerializedName("latitude")
    @Expose
    private var latitude: Double? = null
    @SerializedName("phone")
    @Expose
    private var phone: String? = null
    @SerializedName("fax")
    @Expose
    private var fax: String? = null
    @SerializedName("email")
    @Expose
    private var email: String? = null
    @SerializedName("operatingHours")
    @Expose
    private var operatingHours: String? = null
    @SerializedName("paymentHours")
    @Expose
    private var paymentHours: String? = null
    @SerializedName("rebootTime")
    @Expose
    var rebootTime: String? = null
    @SerializedName("outletTypeName")
    var outletTypeName: String? = null
    @SerializedName("createdAt")
    var createdAt: Long? = 0
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("updatedAt")
    var updatedAt: Long? = 0
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("gstNo")
    var gstNo: String? = ""
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("officialWebsite")
    var officialWebsite: String? = ""
        get() = field
        set(value) {
            field = value
        }

    constructor()

    constructor(out: Outlet) {
        outletId = out.outletId
        outletTypeId = out.outletTypeId
        imageUrl = out.imageUrl
        name = out.name
        friendlyName = out.friendlyName
        address = out.address
        postcode = out.postcode
        longitude = out.longitude
        latitude = out.latitude
        phone = out.phone
        fax = out.fax
        email = out.email
        operatingHours = out.operatingHours
        paymentHours = out.paymentHours
        IsCheck = out.IsCheck
    }

    constructor(parcel: Parcel) : this() {
        outletId = parcel.readString()
        outletTypeId = parcel.readString()
        imageUrl = parcel.readString()
        name = parcel.readString()
        friendlyName = parcel.readString()
        address = parcel.readString()
        postcode = parcel.readString()
        longitude = parcel.readValue(Double::class.java.classLoader) as? Double
        latitude = parcel.readValue(Double::class.java.classLoader) as? Double
        phone = parcel.readString()
        fax = parcel.readString()
        email = parcel.readString()
        operatingHours = parcel.readString()
        paymentHours = parcel.readString()
        outletTypeName = parcel.readString()
    }

    fun getOutletId(): String? {
        return outletId
    }

    fun setOutletId(outletId: String) {
        this.outletId = outletId
    }

    fun getOutletTypeId(): String? {
        return outletTypeId
    }

    fun setOutletTypeId(outletTypeId: String) {
        this.outletTypeId = outletTypeId
    }

    fun getImageUrl(): String? {
        return imageUrl
    }

    fun setImageUrl(imageUrl: String) {
        this.imageUrl = imageUrl
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getFriendlyName(): String? {
        return friendlyName
    }

    fun setFriendlyName(friendlyName: String) {
        this.friendlyName = friendlyName
    }

    fun getAddress(): String? {
        return address
    }

    fun setAddress(address: String) {
        this.address = address
    }

    fun getPostcode(): String? {
        return postcode
    }

    fun setPostcode(postcode: String) {
        this.postcode = postcode
    }

    fun getLongitude(): Double? {
        return longitude
    }

    fun setLongitude(longitude: Double?) {
        this.longitude = longitude
    }

    fun getLatitude(): Double? {
        return latitude
    }

    fun setLatitude(latitude: Double?) {
        this.latitude = latitude
    }

    fun getPhone(): String? {
        return phone
    }

    fun setPhone(phone: String) {
        this.phone = phone
    }

    fun getFax(): String? {
        return fax
    }

    fun setFax(fax: String) {
        this.fax = fax
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun getOperatingHours(): String? {
        return operatingHours
    }

    fun setOperatingHours(operatingHours: String) {
        this.operatingHours = operatingHours
    }

    fun getPaymentHours(): String? {
        return paymentHours
    }

    fun setPaymentHours(paymentHours: String) {
        this.paymentHours = paymentHours
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(outletId)
        parcel.writeString(outletTypeId)
        parcel.writeString(imageUrl)
        parcel.writeString(name)
        parcel.writeString(friendlyName)
        parcel.writeString(address)
        parcel.writeString(postcode)
        parcel.writeValue(longitude)
        parcel.writeValue(latitude)
        parcel.writeString(phone)
        parcel.writeString(fax)
        parcel.writeString(email)
        parcel.writeString(operatingHours)
        parcel.writeString(paymentHours)
        parcel.writeString(outletTypeName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Outlet> {
        override fun createFromParcel(parcel: Parcel): Outlet {
            return Outlet(parcel)
        }

        override fun newArray(size: Int): Array<Outlet?> {
            return arrayOfNulls(size)
        }
    }
}