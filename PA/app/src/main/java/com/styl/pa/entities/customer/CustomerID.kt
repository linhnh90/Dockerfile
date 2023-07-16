package com.styl.pa.entities.customer

import com.google.gson.annotations.SerializedName

/**
 * Created by Ngatran on 09/25/2018.
 */
class CustomerID {
    @SerializedName("contactid")
    private var contactid: String? = ""
    var mContactID:String?
    get() {return contactid}
    set(value) {this.contactid = value}
}