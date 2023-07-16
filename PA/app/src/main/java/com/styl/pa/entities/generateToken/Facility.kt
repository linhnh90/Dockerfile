package com.styl.pa.entities.generateToken

import android.os.Parcel
import android.os.Parcelable
import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.product.Product
import com.styl.pa.services.Config

class Facility : Parcelable {
    @SerializedName("resourceID")
    @Expose
    private var resourceID: String? = null
    @SerializedName("resourcetName", alternate = ["resourceName"])
    @Expose
    private var resourcetName: String? = null
    @SerializedName("resourceSubTypeId")
    @Expose
    private var resourceSubTypeId: String? = null
    @SerializedName("resourceSubTypeName")
    @Expose
    private var resourceSubTypeName: String? = null
    @SerializedName("operatingHours")
    @Expose
    private var operatingHours: String? = null
    @SerializedName("resourceDescription")
    @Expose
    private var resourceDescription: String? = null
    @SerializedName("imageUrl")
    @Expose
    private var imageUrl: String? = null
    @SerializedName("isBookable")
    private var isBookable: Boolean = true
    @SerializedName("resourceFeeList")
    @Expose
    private var resourceFeeList: List<ResourceFeeList>? = ArrayList()
    @SerializedName("outletId")
    var outletId: String? = null
    @SerializedName("outletName")
    var outletName: String? = null
    @SerializedName("outletTypeName")
    var outletTypeName: String? = null
    @SerializedName("bookingInAdvance")
    var bookingInAdvance: Int? = null
    @SerializedName("bookingInAdvanceType")
    var bookingInAdvanceType: String? = null
    @SerializedName("sku", alternate = ["Sku"])
    var sku: String? = null
    @SerializedName("CrmresourceId", alternate = ["crmResourceId", "crmresourceId"])
    var crmResourceId: String? = null
    @SerializedName("minPrice")
    var minPrice: Float? = null
    @SerializedName("maxPrice")
    var maxPrice: Float? = null

    fun getPriceRange(): FloatArray {
        val result = FloatArray(2) // result[0] = minPrice, result[1] = maxPrice
        var minPrice = 0f
        var maxPrice = 0f
        if (resourceFeeList?.isNotEmpty() == true) {
            resourceFeeList!!.forEach { resourceFee ->
                val feeNormalAmount = resourceFee.getFeeNormalAmount()?.toFloat() ?: 0f
                val feePeakAmount = resourceFee.getFeePeakAmount()?.toFloat() ?: 0f
                if (minPrice == 0f || minPrice > feeNormalAmount) {
                    minPrice = feeNormalAmount
                }
                if (maxPrice == 0f || maxPrice < feePeakAmount) {
                    maxPrice = feePeakAmount
                }
            }
        }
        result[0] = minPrice
        result[1] = maxPrice
        return result
    }

    constructor(parcel: Parcel) : this() {
        resourceID = parcel.readString()
        resourcetName = parcel.readString()
        resourceSubTypeId = parcel.readString()
        resourceSubTypeName = parcel.readString()
        operatingHours = parcel.readString()
        resourceDescription = parcel.readString()
        imageUrl = parcel.readString()
        isBookable = parcel.readByte() != 0.toByte()
        outletId = parcel.readString()
        outletName = parcel.readString()
        outletTypeName = parcel.readString()
        bookingInAdvance = parcel.readValue(Int::class.java.classLoader) as? Int
        bookingInAdvanceType = parcel.readString()
        sku = parcel.readString()
        crmResourceId = parcel.readString()
        minPrice = parcel.readValue(Float::class.java.classLoader) as? Float
        maxPrice = parcel.readValue(Float::class.java.classLoader) as? Float
    }


    fun copyFacility(): Facility {
        val stringFacility = Gson().toJson(this, Facility::class.java)
        return Gson().fromJson(stringFacility, Facility::class.java)
    }

    fun getResourceID(): String? {
        return resourceID
    }

    fun setResourceID(resourceID: String?) {
        this.resourceID = resourceID
    }

    fun getResourcetName(): String? {
        return resourcetName
    }

    fun getDecodedName(): Spanned {
        return HtmlCompat.fromHtml(resourcetName ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun setResourcetName(resourcetName: String?) {
        this.resourcetName = resourcetName
    }

    fun getResourceSubTypeId(): String? {
        return resourceSubTypeId
    }

    fun setResourceSubTypeId(resourceSubTypeId: String) {
        this.resourceSubTypeId = resourceSubTypeId
    }

    fun getResourceSubTypeName(): String? {
        return resourceSubTypeName ?: resourcetName
    }

    fun setResourceSubTypeName(resourceSubTypeName: String) {
        this.resourceSubTypeName = resourceSubTypeName
    }

    fun getOperatingHours(): String? {
        return operatingHours
    }

    fun setOperatingHours(operatingHours: String) {
        this.operatingHours = operatingHours
    }

    fun getResourceDescription(): String? {
        return resourceDescription
    }

    fun setResourceDescription(resourceDescription: String) {
        this.resourceDescription = resourceDescription
    }

    fun getImageUrl(): String? {
        if (true == imageUrl?.contains(Config.BASE_IMAGE_URL)) {
            return imageUrl
        }
        return Config.BASE_IMAGE_URL + imageUrl
    }

    fun setImageUrl(imageUrl: String) {
        this.imageUrl = imageUrl
    }

    fun getResourceFeeList(): List<ResourceFeeList>? {
        return resourceFeeList
    }

    fun setResourceFeeList(resourceFeeList: List<ResourceFeeList>) {
        this.resourceFeeList = resourceFeeList
    }

    fun isBookable(value: Boolean) {
        this.isBookable = value
    }

    fun getIsBookable(): Boolean {
        return isBookable
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(resourceID)
        parcel.writeString(resourcetName)
        parcel.writeString(resourceSubTypeId)
        parcel.writeString(resourceSubTypeName)
        parcel.writeString(operatingHours)
        parcel.writeString(resourceDescription)
        parcel.writeString(imageUrl)
        parcel.writeByte(if (isBookable) 1 else 0)
        parcel.writeString(outletId)
        parcel.writeString(outletName)
        parcel.writeString(outletTypeName)
        parcel.writeValue(bookingInAdvance)
        parcel.writeString(bookingInAdvanceType)
        parcel.writeString(sku)
        parcel.writeString(crmResourceId)
        parcel.writeValue(minPrice)
        parcel.writeValue(maxPrice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Facility> {
        override fun createFromParcel(parcel: Parcel): Facility {
            return Facility(parcel)
        }

        override fun newArray(size: Int): Array<Facility?> {
            return arrayOfNulls(size)
        }

        val DAY_TYPE = "day"
        val DAYS_TYPE = "days"
        val MONTH_TYPE = "month"
    }

    constructor()

    constructor(productInfo: Product) {
        this.outletId = productInfo.outletId
        this.outletName = productInfo.outletName
        this.resourceID = productInfo.itemId
        this.resourcetName = productInfo.resourceName
        this.resourceSubTypeId = productInfo.resourceSubTypeId
        this.resourceSubTypeName = productInfo.resourceSubTypeName
        this.resourceDescription = productInfo.description
        this.operatingHours = productInfo.operatingHours
        this.imageUrl = productInfo.imageUrl
        this.bookingInAdvance = productInfo.bookingInAdvance
        this.bookingInAdvanceType = productInfo.bookingInAdvanceType
        this.resourceFeeList = productInfo.facilityFeeList
        this.sku = productInfo.sku
        this.isBookable = productInfo.isBookable
        this.crmResourceId = productInfo.crmResourceId
        this.minPrice = productInfo.minPrice
        this.maxPrice = productInfo.maxPrice
    }
}