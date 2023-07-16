package com.styl.pa.entities.event

import android.os.Parcel
import android.os.Parcelable
import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.pacesRequest.EventTicket
import com.styl.pa.entities.product.Product
import com.styl.pa.services.Config

/**
 * Created by Ngatran on 03/11/2019.
 */
class EventInfo : Parcelable {
    @SerializedName("eventId")
    @Expose
    var eventId: String? = null
    @SerializedName("eventCode")
    @Expose
    var eventCode: String? = null
    @SerializedName(value = "outletID", alternate = ["outletId"])
    @Expose
    var outletID: String? = null
    @SerializedName("outletName")
    @Expose
    var outletName: String? = null
    @SerializedName("outletEmail")
    @Expose
    var outletEmail: String? = null
    @SerializedName("outletPhone")
    @Expose
    var outletPhone: String? = null
    @SerializedName("categoryID")
    @Expose
    var categoryID: String? = null
    @SerializedName("eventTitle")
    @Expose
    var eventTitle: String? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("venue")
    @Expose
    var venue: String? = null
    @SerializedName("venueLongitude")
    @Expose
    var venueLongitude: String? = null
    @SerializedName("venueLatitude")
    @Expose
    var venueLatitude: String? = null
    @SerializedName("dateFrom")
    @Expose
    var dateFrom: String? = null
    @SerializedName("dateTo")
    @Expose
    var dateTo: String? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
    @SerializedName("imageURL")
    @Expose
    var imageURL: String? = null
    @SerializedName("indemnityRequired")
    @Expose
    var indemnityRequired: Boolean? = null
    @SerializedName("indemnityDescription")
    @Expose
    var indemnityDescription: String? = null
    @SerializedName("targetCustomerSegments")
    @Expose
    var targetCustomerSegments: List<String>? = null
    @SerializedName("canRegisterOnline")
    @Expose
    var canRegisterOnline: Boolean? = null
    @SerializedName("isPublic")
    @Expose
    var isPublic: Boolean? = null
    @SerializedName("registerForMyself")
    @Expose
    var registerForMyself: Boolean? = null
    @SerializedName("vacancies")
    @Expose
    var vacancies: Int? = null
    @SerializedName("maxvacancy")
    @Expose
    var maxvacancy: Int? = null
    @SerializedName("registerRequired")
    @Expose
    var registerRequired: Boolean? = null
    @SerializedName(value = "noOfRegisteration", alternate = ["noOfRegistration"])
    @Expose
    var noOfRegisteration: Int? = null
    @SerializedName(value = "closingDate", alternate = ["onlineRegistrationClosingDate"])
    @Expose
    var closingDate: String? = null
    @SerializedName("eventFees")
    @Expose
    var eventFees: List<EventFee>? = null
    @SerializedName("outletTypeName")
    @Expose
    var outletTypeName: String? = null

    @SerializedName(value = "indemnityUrl", alternate = ["indemnityURL", "IndemnityURL", "IndemnityUrl"])
    var indemnityUrl: String? = null

    @SerializedName("sku", alternate = ["Sku"])
    var sku: String? = null

    @SerializedName("minPrice")
    var minPrice: Float? = null

    @SerializedName("maxPrice")
    var maxPrice: Float? = null

    @SerializedName("eventMode")
    var eventMode: String? = null

    @SerializedName("bookingInAdvanced")
    var bookingInAdvanced: Int? = null

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

    var listSelectedTicket: ArrayList<EventTicket> = ArrayList()
    var listEventTicketAvailable: ArrayList<EventTicket> = ArrayList()

    constructor()

    constructor(parcel: Parcel) : this() {
        eventId = parcel.readString()
        eventCode = parcel.readString()
        outletID = parcel.readString()
        outletName = parcel.readString()
        outletEmail = parcel.readString()
        outletPhone = parcel.readString()
        categoryID = parcel.readString()
        eventTitle = parcel.readString()
        description = parcel.readString()
        venue = parcel.readString()
        venueLongitude = parcel.readString()
        venueLatitude = parcel.readString()
        dateFrom = parcel.readString()
        dateTo = parcel.readString()
        status = parcel.readString()
        imageURL = parcel.readString()
        indemnityRequired = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        indemnityDescription = parcel.readString()
        targetCustomerSegments = parcel.createStringArrayList()
        canRegisterOnline = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        isPublic = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        registerForMyself = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        vacancies = parcel.readInt()
        maxvacancy = parcel.readInt()
        registerRequired = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        noOfRegisteration = parcel.readValue(Int::class.java.classLoader) as? Int
        closingDate = parcel.readString()
        eventFees = parcel.createTypedArrayList(EventFee)
        outletTypeName = parcel.readString()
        indemnityUrl = parcel.readString()
        sku = parcel.readString()
        eventMode = parcel.readString()
        bookingInAdvanced = parcel.readValue(Int::class.java.classLoader) as? Int
        eventFormData = parcel.readString()
        classTickets = parcel.createTypedArrayList(EventClassTicket)
        postCode = parcel.readString()
        isAllParticipantRequired = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        faq = parcel.readString()
        specialInstruction = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(eventId)
        parcel.writeString(eventCode)
        parcel.writeString(outletID)
        parcel.writeString(outletName)
        parcel.writeString(outletEmail)
        parcel.writeString(outletPhone)
        parcel.writeString(categoryID)
        parcel.writeString(eventTitle)
        parcel.writeString(description)
        parcel.writeString(venue)
        parcel.writeString(venueLongitude)
        parcel.writeString(venueLatitude)
        parcel.writeString(dateFrom)
        parcel.writeString(dateTo)
        parcel.writeString(status)
        parcel.writeString(imageURL)
        parcel.writeValue(indemnityRequired)
        parcel.writeString(indemnityDescription)
        parcel.writeStringList(targetCustomerSegments)
        parcel.writeValue(canRegisterOnline)
        parcel.writeValue(isPublic)
        parcel.writeValue(registerForMyself)
        parcel.writeValue(vacancies)
        parcel.writeValue(maxvacancy)
        parcel.writeValue(registerRequired)
        parcel.writeValue(noOfRegisteration)
        parcel.writeString(closingDate)
        parcel.writeTypedList(eventFees)
        parcel.writeString(outletTypeName)
        parcel.writeString(indemnityUrl)
        parcel.writeString(sku)
        parcel.writeString(eventMode)
        parcel.writeValue(bookingInAdvanced)
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

    fun getDecodedTitle(): Spanned {
        return HtmlCompat.fromHtml(eventTitle ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun getFaq(): Spanned {
        return HtmlCompat.fromHtml(faq ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun getDecodedDescription(): Spanned {
        return HtmlCompat.fromHtml(description ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun getImageUrl(): String? {
        if (true == imageURL?.contains(Config.BASE_IMAGE_URL)) {
            return imageURL
        }
        return Config.BASE_IMAGE_URL + imageURL
    }

    fun getEventFormDataFromHTML(): Spanned{
        return HtmlCompat.fromHtml(eventFormData ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    companion object CREATOR : Parcelable.Creator<EventInfo> {
        override fun createFromParcel(parcel: Parcel): EventInfo {
            return EventInfo(parcel)
        }

        override fun newArray(size: Int): Array<EventInfo?> {
            return arrayOfNulls(size)
        }
    }

    constructor(productInfo: Product) {
        this.outletID = productInfo.outletId
        this.outletEmail = productInfo.outletEmail
        this.outletName = productInfo.outletName
        this.outletPhone = productInfo.outletPhone
        this.categoryID = productInfo.categoryId
        this.eventId = productInfo.itemId
        this.eventCode = productInfo.productCode
        this.eventTitle = productInfo.title
        this.imageURL = productInfo.imageUrl
        this.indemnityRequired = productInfo.indemnityRequired
        this.indemnityDescription = productInfo.indemnityDescription
        this.indemnityUrl = productInfo.indemnityUrl
//        this.eventFees = productInfo.classEventFees
        this.venue = productInfo.venue
        this.venueLatitude = productInfo.venueLatitude.toString()
        this.venueLongitude = productInfo.venueLongitude.toString()
        this.dateFrom = productInfo.dateFrom
        this.dateTo = productInfo.dateTo
        this.closingDate = productInfo.onlineRegistrationClosingDate
        this.registerRequired = productInfo.registerRequired
        this.status = productInfo.status
        this.vacancies = productInfo.vacancies
        this.maxvacancy = productInfo.maxvacancy
        this.isPublic = productInfo.isPublic
        this.registerForMyself = productInfo.registerForMyself
        this.noOfRegisteration = productInfo.noOfRegistration
        this.targetCustomerSegments = productInfo.targetCustomerSegments
        this.canRegisterOnline = productInfo.canRegisterOnline
        this.sku = productInfo.sku
        this.eventMode = productInfo.eventMode
        this.bookingInAdvanced = productInfo.bookingInAdvance
        this.eventFormData = productInfo.eventFormData
        this.classTickets = productInfo.classTickets
        this.postCode = productInfo.postCode
        this.isAllParticipantRequired = productInfo.isAllParticipantRequired
        this.minPrice = productInfo.minPrice
        this.maxPrice = productInfo.maxPrice
        this.faq = productInfo.faq
        this.specialInstruction = productInfo.specialInstruction
    }
}

