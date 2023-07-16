package com.styl.pa.entities.generateToken

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PageByFacility : Page() {
    @SerializedName("facilityList")
    @Expose
    private var facilityList: ArrayList<Facility>? = ArrayList()

    fun getFacilityList(): ArrayList<Facility>? {
        return facilityList
    }

    fun setFacilityList(facilityList: ArrayList<Facility>?) {
        this.facilityList = facilityList
    }
}