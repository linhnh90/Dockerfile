package com.styl.pa.entities.interestgroup

import android.os.Parcel
import android.os.Parcelable
import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.classes.ClassFee
import com.styl.pa.entities.classes.ClassPrerequisite
import com.styl.pa.entities.classes.ClassSession
import com.styl.pa.entities.classes.ClassTrainer
import com.styl.pa.entities.product.Product
import com.styl.pa.services.Config

class InterestGroup(): Parcelable {
    @SerializedName("igId")
    var igId: String? = null
    
    @SerializedName("igCode")
    var igCode: String? = null

    @SerializedName("igTitle")
    var igTitle: String? = null

    @SerializedName("igDescription")
    var igDescription: String? = null

    @SerializedName("imageURL")
    var imageURL: String? = null

    @SerializedName("outletTypeName")
    var outletTypeName: String? = null

    @SerializedName("outletId")
    var outletId: String? = null

    @SerializedName("outletName")
    var outletName: String? = null

    @SerializedName("outletEmail")
    var outletEmail: String? = null

    @SerializedName("outletPhone")
    var outletPhone: String? = null

    @SerializedName("language")
    var language: String? = null

    @SerializedName("vacancies")
    var vacancies: String? = null

    @SerializedName("maxvacancy")
    var maxVacancy: Int? = null

    @SerializedName(value = "closingDate")
    var closingDate: String? = null

    @SerializedName("startDate")
    var startDate: String? = null

    @SerializedName("endDate")
    var endDate: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("isPublic")
    var isPublic: Boolean? = null

    @SerializedName("canRegisterOnline")
    var canRegisterOnline: Boolean? = null

    @SerializedName("indemnityRequired")
    var indemnityRequired: Boolean? = null

    @SerializedName("indemnityDescription")
    var indemnityDescription: String? = null

    @SerializedName("targetCustomerSegments")
    var targetCustomerSegments: List<String>? = null

    @SerializedName("igTrainers")
    var igTrainers: List<ClassTrainer>? = null

    @SerializedName("igPrerequisites")
    var igPrerequisites: List<ClassPrerequisite>? = null

    @SerializedName("totalSessions")
    var totalSessions: Int? = null

    @SerializedName("igSessions", alternate = ["classSessions"])
    var igSessions: List<ClassSession>? = null

    @SerializedName("igFees")
    var igFees: List<ClassFee>? = null

    @SerializedName("onlineRegistrationClosingDate")
    var onlineRegistrationClosingDate: String? = null

    @SerializedName("counterRegistrationClosingDate")
    var counterRegistrationClosingDate: String? = null

    @SerializedName("sku")
    var sku: String? = null

    @SerializedName("crmresourceId", alternate = ["crmResourceId", "CrmresourceId"])
    var crmresourceId: String? = null

    @SerializedName("igRemarks")
    var igRemarks: String? = null

    @SerializedName("minPrice")
    var minPrice: Float? = null

    @SerializedName("maxPrice")
    var maxPrice: Float? = null

    fun getPriceRange(): FloatArray {
        val result = FloatArray(2)
        result[0] = this.minPrice ?: 0f
        result[1] = this.maxPrice ?: 0f
        return result
    }

    fun getDecodedTitle(): Spanned {
        return HtmlCompat.fromHtml(igTitle ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun getDecodedDescription(): Spanned {
        return HtmlCompat.fromHtml(igDescription ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun getImageURLBase(): String? {
        if (true == imageURL?.contains(Config.BASE_IMAGE_URL)) {
            return imageURL
        }
        return Config.BASE_IMAGE_URL + imageURL
    }

    constructor(parcel: Parcel) : this() {
        igId = parcel.readString()
        igCode = parcel.readString()
        igTitle = parcel.readString()
        igDescription = parcel.readString()
        imageURL = parcel.readString()
        outletTypeName = parcel.readString()
        outletId = parcel.readString()
        outletName = parcel.readString()
        outletEmail = parcel.readString()
        outletPhone = parcel.readString()
        language = parcel.readString()
        vacancies = parcel.readString()
        maxVacancy = parcel.readValue(Int::class.java.classLoader) as? Int
        closingDate = parcel.readString()
        startDate = parcel.readString()
        endDate = parcel.readString()
        status = parcel.readString()
        isPublic = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        canRegisterOnline = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        indemnityRequired = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        indemnityDescription = parcel.readString()
        targetCustomerSegments = parcel.createStringArrayList()
        igTrainers = parcel.createTypedArrayList(ClassTrainer)
        igPrerequisites = parcel.createTypedArrayList(ClassPrerequisite)
        totalSessions = parcel.readValue(Int::class.java.classLoader) as? Int
        igSessions = parcel.createTypedArrayList(ClassSession)
        igFees = parcel.createTypedArrayList(ClassFee)
        onlineRegistrationClosingDate = parcel.readString()
        counterRegistrationClosingDate = parcel.readString()
        sku = parcel.readString()
        crmresourceId = parcel.readString()
        igRemarks = parcel.readString()
        minPrice = parcel.readValue(Float::class.java.classLoader) as? Float
        maxPrice = parcel.readValue(Float::class.java.classLoader) as? Float
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(igId)
        parcel.writeString(igCode)
        parcel.writeString(igTitle)
        parcel.writeString(igDescription)
        parcel.writeString(imageURL)
        parcel.writeString(outletTypeName)
        parcel.writeString(outletId)
        parcel.writeString(outletName)
        parcel.writeString(outletEmail)
        parcel.writeString(outletPhone)
        parcel.writeString(language)
        parcel.writeString(vacancies)
        parcel.writeValue(maxVacancy)
        parcel.writeString(closingDate)
        parcel.writeString(startDate)
        parcel.writeString(endDate)
        parcel.writeString(status)
        parcel.writeValue(isPublic)
        parcel.writeValue(canRegisterOnline)
        parcel.writeValue(indemnityRequired)
        parcel.writeString(indemnityDescription)
        parcel.writeStringList(targetCustomerSegments)
        parcel.writeTypedList(igTrainers)
        parcel.writeTypedList(igPrerequisites)
        parcel.writeValue(totalSessions)
        parcel.writeTypedList(igSessions)
        parcel.writeTypedList(igFees)
        parcel.writeString(onlineRegistrationClosingDate)
        parcel.writeString(counterRegistrationClosingDate)
        parcel.writeString(sku)
        parcel.writeString(crmresourceId)
        parcel.writeString(igRemarks)
        parcel.writeValue(minPrice)
        parcel.writeValue(maxPrice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InterestGroup> {
        override fun createFromParcel(parcel: Parcel): InterestGroup {
            return InterestGroup(parcel)
        }

        override fun newArray(size: Int): Array<InterestGroup?> {
            return arrayOfNulls(size)
        }
    }

    constructor(productInfo: Product) : this() {
        this.igId = productInfo.itemId
        this.igCode = productInfo.productCode
        this.igFees = productInfo.classEventFees
        this.outletId = productInfo.outletId
        this.outletName = productInfo.outletName
        this.outletEmail = productInfo.outletEmail
        this.outletPhone = productInfo.outletPhone
        this.igTitle = productInfo.title
        this.igDescription = productInfo.description
        this.imageURL = productInfo.imageUrl
        this.indemnityRequired = productInfo.indemnityRequired
        this.indemnityDescription = productInfo.indemnityDescription
//        this.indemnityUrl = productInfo.indemnityUrl
        this.igPrerequisites = productInfo.classPrerequisites
        this.startDate = productInfo.dateFrom
        this.endDate = productInfo.dateTo
        this.closingDate = productInfo.onlineRegistrationClosingDate
        this.vacancies = productInfo.vacancies.toString()
        this.status = productInfo.status
        this.maxVacancy = productInfo.maxvacancy
        this.language = productInfo.language
        this.totalSessions = productInfo.totalSessions
        this.igTrainers = productInfo.classTrainers
        this.igSessions = productInfo.classSessions
        this.isPublic = productInfo.isPublic
        this.canRegisterOnline = productInfo.canRegisterOnline
        this.targetCustomerSegments = productInfo.targetCustomerSegments
//        this.isSkillsFutureEnabled = productInfo.isSkillsFutureEnabled
        this.sku = productInfo.sku
        this.crmresourceId = productInfo.crmResourceId
        this.minPrice = productInfo.minPrice
        this.maxPrice = productInfo.maxPrice
        this.igRemarks = productInfo.classRemarks
    }
}