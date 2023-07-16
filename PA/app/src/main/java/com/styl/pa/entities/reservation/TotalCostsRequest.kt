package com.styl.pa.entities.reservation

import com.google.gson.annotations.SerializedName

/**
 * Created by Ngatran on 09/22/2018.
 */
class TotalCostsRequest {
    @SerializedName("sessionCode")
    private var sessionCode: String? = ""
    @SerializedName("contactId")
    private var contactId: String? = ""

    constructor(){}

    constructor(sessionCode : String, contactId:String){
        this.sessionCode = sessionCode
        this.contactId = contactId
    }

}