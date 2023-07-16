package com.styl.pa.entities.product

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.classes.ClassFee
import com.styl.pa.entities.classes.ClassPrerequisite
import com.styl.pa.entities.classes.ClassSession
import com.styl.pa.entities.classes.ClassTrainer
import com.styl.pa.entities.event.EventClassTicket
import com.styl.pa.entities.generateToken.ResourceFeeList

/**
 * Created by NguyenHang on 12/8/2020.
 */

class Product() : Parcelable {

    @SerializedName("outletId")
    var outletId: String? = null
    @SerializedName("outletName")
    var outletName: String? = null
    @SerializedName("outletEmail")
    var outletEmail: String? = null
    @SerializedName("outletPhone")
    var outletPhone: String? = null
    @SerializedName("categoryId")
    var categoryId: String? = null
    @SerializedName("itemId")
    var itemId: String? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("productCode")
    var productCode: String? = null
    @SerializedName("resourceName")
    var resourceName: String? = null
    @SerializedName("resourceSubTypeId")
    var resourceSubTypeId: String? = null
    @SerializedName("resourceSubTypeName")
    var resourceSubTypeName: String? = null
    @SerializedName("operatingHours")
    var operatingHours: String? = null
    @SerializedName("description")
    var description: String? = null
    @SerializedName("imageUrl")
    var imageUrl: String? = null
    @SerializedName("bookingPolicyID")
    var bookingPolicyID: String? = null
    @SerializedName("bookingInAdvance")
    var bookingInAdvance: Int? = null
    @SerializedName("bookingInAdvanceType")
    var bookingInAdvanceType: String? = null
    @SerializedName("indemnityRequired")
    var indemnityRequired: Boolean? = null
    @SerializedName("indemnityDescription")
    var indemnityDescription: String? = null
    @SerializedName("indemnityUrl")
    var indemnityUrl: String? = null
    @SerializedName("classPrerequisites")
    var classPrerequisites: ArrayList<ClassPrerequisite>? = null
    @SerializedName("facilityFeeList")
    var facilityFeeList: ArrayList<ResourceFeeList>? = null
    @SerializedName("classEventFees")
    var classEventFees: ArrayList<ClassFee>? = null
    @SerializedName("venue")
    var venue: String? = null
    @SerializedName("venueLongitude")
    var venueLongitude: Double? = null
    @SerializedName("venueLatitude")
    var venueLatitude: Double? = null
    @SerializedName("dateFrom")
    var dateFrom: String? = null
    @SerializedName("dateTo")
    var dateTo: String? = null
    @SerializedName("status")
    var status: String? = null
    @SerializedName("registerRequired")
    var registerRequired: Boolean? = null
    @SerializedName("onlineRegistrationClosingDate")
    var onlineRegistrationClosingDate: String? = null
    @SerializedName("counterRegistrationClosingDate")
    var counterRegistrationClosingDate: String? = null
    @SerializedName("keywordtag")
    var keywordtag: ArrayList<String>? = null
    @SerializedName("isSkillsFutureEnabled")
    var isSkillsFutureEnabled: Boolean? = null
    @SerializedName("vacancies")
    var vacancies: Int? = null
    @SerializedName("maxvacancy")
    var maxvacancy: Int? = null
    @SerializedName("language")
    var language: String? = null
    @SerializedName("totalSessions")
    var totalSessions: Int? = null
    @SerializedName("classTrainers")
    var classTrainers: ArrayList<ClassTrainer>? = null
    @SerializedName("classSessions")
    var classSessions: ArrayList<ClassSession>? = null
    @SerializedName("isPublic")
    var isPublic: Boolean? = null
    @SerializedName("canRegisterOnline")
    var canRegisterOnline: Boolean? = null
    @SerializedName("registerForMyself")
    var registerForMyself: Boolean? = null
    @SerializedName("noOfRegistration")
    var noOfRegistration: Int? = null
    @SerializedName("targetCustomerSegments")
    var targetCustomerSegments: ArrayList<String>? = null
    @SerializedName("isBookable")
    var isBookable: Boolean = true
    @SerializedName("CrmresourceId", alternate = ["crmResourceId", "crmResouceId"])
    var crmResourceId: String? = null
    @SerializedName("productType")
    var productType: String? = null
    @SerializedName("sku", alternate = ["Sku"])
    var sku: String? = null
    @SerializedName("minPrice")
    var minPrice: Float? = null
    @SerializedName("maxPrice")
    var maxPrice: Float? = null
    @SerializedName("classRemarks")
    var classRemarks: String? = null
    @SerializedName("materialFees")
    var materialFees: List<ResourceFeeList>? = null

    @SerializedName("eventMode")
    var eventMode: String? = null

    @SerializedName("eventFormData")
    var eventFormData: String? = null

    @SerializedName("classTickets")
    var classTickets: List<EventClassTicket>? = null

    @SerializedName("postCode")
    var postCode: String? = null

    @SerializedName("isAllParticipantRequired")
    var isAllParticipantRequired: Boolean? = null

    @SerializedName("faq")
    var faq: String? = null

    @SerializedName("specialInstruction")
    var specialInstruction: String? = null

    constructor(parcel: Parcel) : this() {
        outletId = parcel.readString()
        outletName = parcel.readString()
        outletEmail = parcel.readString()
        outletPhone = parcel.readString()
        categoryId = parcel.readString()
        itemId = parcel.readString()
        title = parcel.readString()
        productCode = parcel.readString()
        resourceName = parcel.readString()
        resourceSubTypeId = parcel.readString()
        resourceSubTypeName = parcel.readString()
        operatingHours = parcel.readString()
        description = parcel.readString()
        imageUrl = parcel.readString()
        bookingPolicyID = parcel.readString()
        bookingInAdvance = parcel.readValue(Int::class.java.classLoader) as? Int
        bookingInAdvanceType = parcel.readString()
        indemnityRequired = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        indemnityDescription = parcel.readString()
        indemnityUrl = parcel.readString()
        venue = parcel.readString()
        venueLongitude = parcel.readValue(Double::class.java.classLoader) as? Double
        venueLatitude = parcel.readValue(Double::class.java.classLoader) as? Double
        dateFrom = parcel.readString()
        dateTo = parcel.readString()
        status = parcel.readString()
        registerRequired = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        onlineRegistrationClosingDate = parcel.readString()
        counterRegistrationClosingDate = parcel.readString()
        isSkillsFutureEnabled = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        vacancies = parcel.readValue(Int::class.java.classLoader) as? Int
        maxvacancy = parcel.readValue(Int::class.java.classLoader) as? Int
        language = parcel.readString()
        totalSessions = parcel.readValue(Int::class.java.classLoader) as? Int
        isPublic = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        canRegisterOnline = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        registerForMyself = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        noOfRegistration = parcel.readValue(Int::class.java.classLoader) as? Int
        isBookable = parcel.readByte() != 0.toByte()
        crmResourceId = parcel.readString()
        sku = parcel.readString()
        minPrice = parcel.readValue(Float::class.java.classLoader) as? Float
        maxPrice = parcel.readValue(Float::class.java.classLoader) as? Float
        classRemarks = parcel.readString()
        eventMode = parcel.readString()
        eventFormData = parcel.readString()
        classTickets = parcel.createTypedArrayList(EventClassTicket)
        postCode = parcel.readString()
        isAllParticipantRequired = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        faq = parcel.readString()
        specialInstruction = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(outletId)
        parcel.writeString(outletName)
        parcel.writeString(outletEmail)
        parcel.writeString(outletPhone)
        parcel.writeString(categoryId)
        parcel.writeString(itemId)
        parcel.writeString(title)
        parcel.writeString(productCode)
        parcel.writeString(resourceName)
        parcel.writeString(resourceSubTypeId)
        parcel.writeString(resourceSubTypeName)
        parcel.writeString(operatingHours)
        parcel.writeString(description)
        parcel.writeString(imageUrl)
        parcel.writeString(bookingPolicyID)
        parcel.writeValue(bookingInAdvance)
        parcel.writeString(bookingInAdvanceType)
        parcel.writeValue(indemnityRequired)
        parcel.writeString(indemnityDescription)
        parcel.writeString(indemnityUrl)
        parcel.writeString(venue)
        parcel.writeValue(venueLongitude)
        parcel.writeValue(venueLatitude)
        parcel.writeString(dateFrom)
        parcel.writeString(dateTo)
        parcel.writeString(status)
        parcel.writeValue(registerRequired)
        parcel.writeString(onlineRegistrationClosingDate)
        parcel.writeString(counterRegistrationClosingDate)
        parcel.writeValue(isSkillsFutureEnabled)
        parcel.writeValue(vacancies)
        parcel.writeValue(maxvacancy)
        parcel.writeString(language)
        parcel.writeValue(totalSessions)
        parcel.writeValue(isPublic)
        parcel.writeValue(canRegisterOnline)
        parcel.writeValue(registerForMyself)
        parcel.writeValue(noOfRegistration)
        parcel.writeByte(if (isBookable) 1 else 0)
        parcel.writeString(crmResourceId)
        parcel.writeString(sku)
        parcel.writeValue(minPrice)
        parcel.writeValue(maxPrice)
        parcel.writeString(classRemarks)
        parcel.writeString(eventMode)
        parcel.writeString(eventFormData)
        parcel.writeTypedList(classTickets)
        parcel.writeString(postCode)
        parcel.writeValue(isAllParticipantRequired)
        parcel.writeString(faq)
        parcel.writeString(specialInstruction)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR = object : Creator<Product> {
            override fun createFromParcel(parcel: Parcel): Product {
                return Product(parcel)
            }

            override fun newArray(size: Int): Array<Product?> {
                return arrayOfNulls(size)
            }
        }

        const val PRODUCT_COURSE = "Course"
        const val PRODUCT_EVENT = "Event"
        const val PRODUCT_FACILITY = "Facility"
        const val PRODUCT_INTEREST_GROUP = "InterestGroup"

    }

}