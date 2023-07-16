package com.styl.pa.entities.event

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Fields() : Parcelable {
    @SerializedName("Age", alternate = ["age"])
    var age: FieldObject? = null

    @SerializedName("dateOfBirth", alternate = ["birth_date", "dateOfbirth"])
    var dateOfBirth: FieldObject? = null

    @SerializedName("citizenship")
    var citizenship: FieldObject? = null

    @SerializedName("contactno")
    var contactNo: FieldObject? = null

    @SerializedName("dietary_preferences")
    var dietaryPreferences: FieldObject? = null

    @SerializedName("email")
    var email: FieldObject? = null

    @SerializedName("gender")
    var gender: FieldObject? = null

    @SerializedName("home_address")
    var homeAddress: FieldObject? = null

    @SerializedName("indemnity")
    var indemnity: FieldObject? = null

    @SerializedName("firstname")
    var firstname: FieldObject? = null

    @SerializedName("lastname")
    var lastname: FieldObject? = null

    @SerializedName("next_of_kin_contact_no")
    var nextOfKinContactNo: FieldObject? = null

    @SerializedName("next_of_kin_name")
    var nextOfKinName: FieldObject? = null

    @SerializedName("no_of_accompanying_guest")
    var noOfAccompanyingGuest: FieldObject? = null

    @SerializedName("nric")
    var nric: FieldObject? = null

    @SerializedName("nric_no_uen_no")
    var nricNoUenNo: FieldObject? = null

    @SerializedName("passport_number")
    var passportNumber: FieldObject? = null

    @SerializedName("postalzipcode")
    var postalZipCode: FieldObject? = null

    @SerializedName("remarks")
    var remarks: FieldObject? = null

    @SerializedName("salutation")
    var salutation: FieldObject? = null

    @SerializedName("serial_no")
    var serialNo: FieldObject? = null

    @SerializedName("ExternalLineId", alternate = ["externalLineId"])
    var externalLineId: String? = null

    @SerializedName("componentId")
    var componentId: String? = null

    @SerializedName("unit_number", alternate = ["unitNumber"])
    var unitNumber: FieldObject? = null

    var listNotDefined: ArrayList<FieldObject> = ArrayList()

    constructor(parcel: Parcel) : this() {
        age = parcel.readParcelable(FieldObject::class.java.classLoader)
        dateOfBirth = parcel.readParcelable(FieldObject::class.java.classLoader)
        citizenship = parcel.readParcelable(FieldObject::class.java.classLoader)
        contactNo = parcel.readParcelable(FieldObject::class.java.classLoader)
        dietaryPreferences = parcel.readParcelable(FieldObject::class.java.classLoader)
        email = parcel.readParcelable(FieldObject::class.java.classLoader)
        gender = parcel.readParcelable(GenderFieldObject::class.java.classLoader)
        homeAddress = parcel.readParcelable(FieldObject::class.java.classLoader)
        indemnity = parcel.readParcelable(FieldObject::class.java.classLoader)
        firstname = parcel.readParcelable(FieldObject::class.java.classLoader)
        lastname = parcel.readParcelable(FieldObject::class.java.classLoader)
        nextOfKinContactNo = parcel.readParcelable(FieldObject::class.java.classLoader)
        nextOfKinName = parcel.readParcelable(FieldObject::class.java.classLoader)
        noOfAccompanyingGuest = parcel.readParcelable(FieldObject::class.java.classLoader)
        nric = parcel.readParcelable(FieldObject::class.java.classLoader)
        nricNoUenNo = parcel.readParcelable(FieldObject::class.java.classLoader)
        passportNumber = parcel.readParcelable(FieldObject::class.java.classLoader)
        postalZipCode = parcel.readParcelable(FieldObject::class.java.classLoader)
        remarks = parcel.readParcelable(FieldObject::class.java.classLoader)
        salutation = parcel.readParcelable(FieldObject::class.java.classLoader)
        serialNo = parcel.readParcelable(FieldObject::class.java.classLoader)
        externalLineId = parcel.readString()
        componentId = parcel.readString()
        unitNumber = parcel.readParcelable(FieldObject::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(age, flags)
        parcel.writeParcelable(dateOfBirth, flags)
        parcel.writeParcelable(citizenship, flags)
        parcel.writeParcelable(contactNo, flags)
        parcel.writeParcelable(dietaryPreferences, flags)
        parcel.writeParcelable(email, flags)
        parcel.writeParcelable(gender, flags)
        parcel.writeParcelable(homeAddress, flags)
        parcel.writeParcelable(indemnity, flags)
        parcel.writeParcelable(firstname, flags)
        parcel.writeParcelable(lastname, flags)
        parcel.writeParcelable(nextOfKinContactNo, flags)
        parcel.writeParcelable(nextOfKinName, flags)
        parcel.writeParcelable(noOfAccompanyingGuest, flags)
        parcel.writeParcelable(nric, flags)
        parcel.writeParcelable(nricNoUenNo, flags)
        parcel.writeParcelable(passportNumber, flags)
        parcel.writeParcelable(postalZipCode, flags)
        parcel.writeParcelable(remarks, flags)
        parcel.writeParcelable(salutation, flags)
        parcel.writeParcelable(serialNo, flags)
        parcel.writeString(externalLineId)
        parcel.writeString(componentId)
        parcel.writeParcelable(unitNumber, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Fields> {
        override fun createFromParcel(parcel: Parcel): Fields {
            return Fields(parcel)
        }

        override fun newArray(size: Int): Array<Fields?> {
            return arrayOfNulls(size)
        }
    }


}