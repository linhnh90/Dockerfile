package com.styl.pa.entities.journey

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.utils.GeneralUtils


/**
 * Created by Ngatran on 03/29/2019.
 */
class UserInfo {
    @SerializedName("uid")
    @Expose
    var uID: String? = null
    @SerializedName("age")
    @Expose
    var age: Int? = null
    @SerializedName("gender")
    @Expose
    var gender: String? = null

    constructor()

    constructor(info: CustomerInfo?) {
        this.uID = info?.mCustomerId
        this.gender = info?.gender
        val ageString = GeneralUtils.convertDateToAge(info?.dob)
        if (ageString != null) {
            this.age = ageString.toInt()
        }
    }
}