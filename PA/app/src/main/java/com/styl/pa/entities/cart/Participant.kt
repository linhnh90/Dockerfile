package com.styl.pa.entities.cart

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Participant() : Parcelable {
    @SerializedName("IdNo")
    var idNo: String? = null

    @SerializedName("IdType")
    var idType: String? = null

    @SerializedName("Email", alternate = ["email"])
    var email: String? = null

    @SerializedName("MobileNumber", alternate = ["mobilenumber", "mobileNumber"])
    var mobileNumber: String? = null

    @SerializedName("FullName", alternate = ["firstName", "firstname"])
    var fullName: String? = null

    @SerializedName("Gender", alternate = ["gender"])
    var gender: String? = null

    @SerializedName("DateOfBirth", alternate = ["birth_date", "dateofbirth", "dateOfBirth"])
    var dateOfBirth: String? = null

    @SerializedName("IsSelf", alternate = ["isSelf", "isself"])
    var isSelf: Boolean? = null

    @SerializedName("Nationality", alternate = ["nationality"])
    var nationality: String? = null

    @SerializedName("Race", alternate = ["race"])
    var race: String? = null

    @SerializedName("nextOfKinName", alternate = ["next_of_kin_name", "nextofkinname"])
    var nextOfKinName: String? = null

    @SerializedName("nextOfKinContactNumber", alternate = ["next_of_kin_contact_no", "nextofkincontactnumber"])
    var nextOfKinContactNumber: String? = null

    @SerializedName("UserId", alternate = ["userId"])
    var userId: String? = null

    @SerializedName("RegisterOnline", alternate = ["registerOnline", "registeronline"])
    var registrationOnline: Boolean? = null

    @SerializedName("itemName") //sku
    var itemName: String? = null

    @SerializedName("externalLineId")
    var externalLineId: String? = null

    @SerializedName("componentId")
    var componentId: String? = null

    @SerializedName("Age", alternate = ["age"])
    var age: String? = null

    @SerializedName("citizenship", alternate = ["citizenship_arr"])
    var citizenship: String? = null

    @SerializedName("contactno")
    var contactNo: String? = null

    @SerializedName("dietary_preferences", alternate = ["dietaryPreferences", "dietarypreferences"])
    var dietaryPreferences: String? = null

    @SerializedName("home_address", alternate = ["homeAddress", "homeaddress"])
    var homeAddress: String? = null

    @SerializedName("indemnity")
    var indemnity: String? = null

    @SerializedName("lastname")
    var lastname: String? = null

    @SerializedName("no_of_accompanying_guest", alternate = ["noOfAccompanyingGuest", "noofaccompanyingguest"])
    var noOfAccompanyingGuest: String? = null

    @SerializedName("nric")
    var nric: String? = null

    @SerializedName("nric_no_uen_no", alternate = ["nricNoUenNo", "nricnouenno"])
    var nricNoUenNo: String? = null

    @SerializedName("passport_number", alternate = ["passportnumber", "passportNumber"])
    var passportNumber: String? = null

    @SerializedName("postalzipcode", alternate = ["postalZipCode"])
    var postalZipCode: String? = null

    @SerializedName("remarks")
    var remarks: String? = null

    @SerializedName("salutation")
    var salutation: String? = null

    @SerializedName("serial_no", alternate = ["serialNo", "serialno"])
    var serialNo: String? = null

    @SerializedName("unit_number", alternate = ["unitNumber"])
    var unitNumber: String? = null

    constructor(parcel: Parcel) : this() {
        idNo = parcel.readString()
        idType = parcel.readString()
        email = parcel.readString()
        mobileNumber = parcel.readString()
        fullName = parcel.readString()
        gender = parcel.readString()
        dateOfBirth = parcel.readString()
        isSelf = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        nationality = parcel.readString()
        race = parcel.readString()
        nextOfKinName = parcel.readString()
        nextOfKinContactNumber = parcel.readString()
        userId = parcel.readString()
        registrationOnline = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        itemName = parcel.readString()
        externalLineId = parcel.readString()
        componentId = parcel.readString()
        age = parcel.readString()
        citizenship = parcel.readString()
        contactNo = parcel.readString()
        dietaryPreferences = parcel.readString()
        homeAddress = parcel.readString()
        indemnity = parcel.readString()
        lastname = parcel.readString()
        noOfAccompanyingGuest = parcel.readString()
        nric = parcel.readString()
        nricNoUenNo = parcel.readString()
        passportNumber = parcel.readString()
        postalZipCode = parcel.readString()
        remarks = parcel.readString()
        salutation = parcel.readString()
        serialNo = parcel.readString()
        unitNumber = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idNo)
        parcel.writeString(idType)
        parcel.writeString(email)
        parcel.writeString(mobileNumber)
        parcel.writeString(fullName)
        parcel.writeString(gender)
        parcel.writeString(dateOfBirth)
        parcel.writeValue(isSelf)
        parcel.writeString(nationality)
        parcel.writeString(race)
        parcel.writeString(nextOfKinName)
        parcel.writeString(nextOfKinContactNumber)
        parcel.writeString(userId)
        parcel.writeValue(registrationOnline)
        parcel.writeString(itemName)
        parcel.writeString(externalLineId)
        parcel.writeString(componentId)
        parcel.writeString(age)
        parcel.writeString(citizenship)
        parcel.writeString(contactNo)
        parcel.writeString(dietaryPreferences)
        parcel.writeString(homeAddress)
        parcel.writeString(indemnity)
        parcel.writeString(lastname)
        parcel.writeString(noOfAccompanyingGuest)
        parcel.writeString(nric)
        parcel.writeString(nricNoUenNo)
        parcel.writeString(passportNumber)
        parcel.writeString(postalZipCode)
        parcel.writeString(remarks)
        parcel.writeString(salutation)
        parcel.writeString(serialNo)
        parcel.writeString(unitNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Participant> {
        override fun createFromParcel(parcel: Parcel): Participant {
            return Participant(parcel)
        }

        override fun newArray(size: Int): Array<Participant?> {
            return arrayOfNulls(size)
        }
    }


}