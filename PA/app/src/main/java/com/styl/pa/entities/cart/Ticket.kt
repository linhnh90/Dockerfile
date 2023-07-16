package com.styl.pa.entities.cart

import com.google.gson.annotations.SerializedName

class Ticket {
    @SerializedName("ExternalLineId", alternate = ["externalLineId"])
    var externalLineId: String? = null

    @SerializedName("HighRiskChecked", alternate = ["highRiskChecked"])
    var highRiskChecked: Boolean? = null

    @SerializedName("IsHighRiskItem", alternate = ["isHighRiskItem"])
    var isHighRiskItem: Boolean? = null

    @SerializedName("IsOnlineClass", alternate = ["isOnlineClass"])
    var isOnlineClass: Boolean? = null

    @SerializedName("IsPrivateClass", alternate = ["isPrivateClass"])
    var isPrivateClass: Boolean? = null

    @SerializedName("MaxBookingAllowed", alternate = ["maxBookingAllowed"])
    var maxBookingAllowed: Int? = null

    @SerializedName("MaxPrice", alternate = ["maxPrice"])
    var maxPrice: Float? = null

    @SerializedName("MinPrice", alternate = ["minPrice"])
    var minPrice: Float? = null

    @SerializedName("availableVacancies")
    var availableVacancies: Int? = null

    @SerializedName("booking")
    var booking: ArrayList<Booking>? = null

    @SerializedName("date")
    var date: String? = null

    @SerializedName("endDate")
    var endDate: String? = null

    @SerializedName("isRegistrationOpen")
    var isRegistrationOpen: Boolean? = null

    @SerializedName("isSkillsFutureCourse")
    var isSkillsFutureCourse: Boolean? = null

    @SerializedName("itemId")
    var itemId: String? = null

    @SerializedName("itemName")
    var itemName: String? = null

    @SerializedName("itemType")
    var itemType: Int? = null

    @SerializedName("location")
    var location: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("outlet")
    var outlet: String? = null

    @SerializedName("outletId")
    var outletId: String? = null

    @SerializedName("productCode")
    var productCode: String? = null

    @SerializedName("productType")
    var productType: String? = null

    @SerializedName("qty")
    var qty: Int? = null

    @SerializedName("time")
    var time: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("total")
    var total: CartTotalPrice? = null

    @SerializedName("totalVacancies")
    var totalVacancies: Int? = null

}