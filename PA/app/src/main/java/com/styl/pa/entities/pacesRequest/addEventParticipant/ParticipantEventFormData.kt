package com.styl.pa.entities.pacesRequest.addEventParticipant

import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.event.EventParticipant

class ParticipantEventFormData {
    @SerializedName("Age", alternate = ["age"])
    var age: String? = null

    @SerializedName("dateOfBirth", alternate = ["birth_date", "dateOfbirth"])
    var dateOfBirth: String? = null

    @SerializedName("citizenship")
    var citizenship: String? = null

    @SerializedName("contactno")
    var contactNo: String? = null

    @SerializedName("dietary_preferences")
    var dietaryPreferences: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("gender")
    var gender: String? = null

    @SerializedName("home_address")
    var homeAddress: String? = null

    @SerializedName("indemnity")
    var indemnity: String? = null

    @SerializedName("firstname")
    var firstname: String? = null

    @SerializedName("lastname")
    var lastname: String? = null

    @SerializedName("next_of_kin_contact_no")
    var nextOfKinContactNo: String? = null

    @SerializedName("next_of_kin_name")
    var nextOfKinName: String? = null

    @SerializedName("no_of_accompanying_guest")
    var noOfAccompanyingGuest: String? = null

    @SerializedName("nric")
    var nric: String? = null

    @SerializedName("nric_no_uen_no")
    var nricNoUenNo: String? = null

    @SerializedName("passport_number")
    var passportNumber: String? = null

    @SerializedName("postalzipcode")
    var postalZipCode: String? = null

    @SerializedName("remarks")
    var remarks: String? = null

    @SerializedName("salutation")
    var salutation: String? = null

    @SerializedName("serial_no")
    var serialNo: String? = null

    @SerializedName("unit_number")
    var unitNumber: String? = null

    constructor()

    constructor(eventParticipant: EventParticipant){
        val field = eventParticipant.participantInfo
        if (field != null){
            this.lastname = "."
        }
    }

}