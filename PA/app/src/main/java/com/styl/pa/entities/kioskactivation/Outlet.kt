package com.styl.pa.entities.kioskactivation

import com.google.gson.annotations.SerializedName

/**
 * Created by trangpham on 9/26/2018
 */
class Outlet1(
    @SerializedName("Id")
    var id: Int?,
    @SerializedName("OutletId")
    var outletId: String?,
    @SerializedName("OutletTypeId")
    var outletTypeId: String?,
    @SerializedName("ImageUrl")
    var imageUrl: String?,
    @SerializedName("Name")
    var name: String?,
    @SerializedName("FriendlyName")
    var friendlyName: String?,
    @SerializedName("Postcode")
    var postalCode: String?,
    @SerializedName("Longitude")
    var lng: Float?,
    @SerializedName("Latitude")
    var lat: Float?,
    @SerializedName("Phone")
    var phone: String?,
    @SerializedName("Email")
    var email: String?,
    @SerializedName("OperatingHours")
    var operatingHours: String?,
    @SerializedName("PaymentHours")
    var paymentHours: String?
) {
}