package com.styl.pa.entities.reservation

import com.google.gson.annotations.SerializedName

/**
 * Created by Ngatran on 09/21/2018.
 */
class ProgrammeBookingAuthRequest {
    companion object {
        val PRODUCT_TYPE_EVENT = 1
        val PRODUCT_TYPE_CLASS = 2
        val PRODUCT_TYPE_IG = 3
    }

    @SerializedName("producttype")
    private var producttype: Int? = 0
    @SerializedName("productid")
    var productid: String? = ""
    @SerializedName("agreeToIndemnity")
    private var agreeToIndemnity: Boolean? = false
    @SerializedName("idno")
    private var idno: String? = null
    var mIdNo: String?
        get() {
            return idno
        }
        set(value) {
            this.idno = value
        }
    @SerializedName("participantIdNo")
    private var participantIdNo: String? = null
    public var mParticipantIdNo: String?
        get() {
            return participantIdNo
        }
        set(value) {
            this.participantIdNo = value
        }
    @SerializedName("quantityForEvent")
    private var quantityForEvent: Int? = null
    @SerializedName("sessioncode")
    private var sessioncode: String? = ""
    var mSessionCode: String?
        get() {
            return sessioncode
        }
        set(value) {
            this.sessioncode = value
        }

    constructor(producttype: Int, productid: String, agreeToIndemnity: Boolean, idno: String, participantIdNo: String?, sessioncode: String) {
        this.producttype = producttype
        this.productid = productid
        this.agreeToIndemnity = agreeToIndemnity
        this.idno = idno
        this.participantIdNo = participantIdNo
//        this.sessioncode = UUID.randomUUID().toString()
        this.sessioncode = sessioncode
    }

    constructor(producttype: Int, productid: String, agreeToIndemnity: Boolean, idno: String, quantityForEvent: Int?, sessioncode: String) {
        this.producttype = producttype
        this.productid = productid
        this.agreeToIndemnity = agreeToIndemnity
        this.idno = idno
        this.quantityForEvent = quantityForEvent
        this.sessioncode = sessioncode
    }

    constructor(producttype: Int, productid: String, agreeToIndemnity: Boolean, idno: String, participantIdNo: String?) {
        this.producttype = producttype
        this.productid = productid
        this.agreeToIndemnity = agreeToIndemnity
        this.idno = idno
        this.participantIdNo = participantIdNo
    }
}