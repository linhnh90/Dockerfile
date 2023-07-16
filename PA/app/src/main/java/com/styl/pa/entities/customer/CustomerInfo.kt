package com.styl.pa.entities.customer

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Ngatran on 09/21/2018.
 */
class CustomerInfo() : Parcelable {
    @SerializedName("customerId")
    private var customerId: String? = null
    var mCustomerId: String?
        get() {
            return customerId
        }
        set(value) {
            this.customerId = value
        }
    @SerializedName("idno")
    private var idno: String? = null
    var mIdNo: String?
        get() {
            return idno
        }
        set(value) {
            this.idno = value
        }

    @SerializedName("idtype")
    private var idtype: String? = null
    var mIdType: String?
        get() {
            return idtype
        }
        set(value) {
            idtype = value
        }

    @SerializedName("phone")
    private var phone: String? = null
    @SerializedName("email")
    private var email: String? = null
    var mEmail: String?
        get() {
            return email
        }
        set(value) {
            email = value
        }

    @SerializedName("block", alternate = ["blockno"])
    private var block: String? = null
    @SerializedName("dob")
    var dob: String? = null
    var mDoB: String?
        get() {
            return dob
        }
        set(value) {
            dob = value
        }
    @SerializedName("floor")
    private var floor: String? = null
    @SerializedName("fullName")
    private var fullName: String? = null
    var mFullName: String?
        get() {
            return fullName
        }
        set(value) {
            this.fullName = value
        }
    @SerializedName("gender")
    var gender: String? = null
    var mGender: String?
        get() {
            return gender
        }
        set(value) {
            gender = value
        }
    @SerializedName("mobile", alternate = ["mobileno"])
    private var mobile: String? = null
    var mMobile: String?
        get() {
            return mobile
        }
        set(value) {
            mobile = value
        }

    @SerializedName("nationality")
    private var nationality: String? = null
    var mNationality: String?
        get() {
            return nationality
        }
        set(value) {
            nationality = value
        }

    @SerializedName("postalcode")
    private var postalcode: String? = null
    @SerializedName("race")
    private var race: String? = null
    var mRace: String?
        get() {
            return race
        }
        set(value) {
            race = value
        }
    @SerializedName("status")
    private var status: String? = null
    @SerializedName("streetname")
    private var streetname: String? = null
    @SerializedName("unitno")
    private var unitno: String? = null
    @SerializedName("membershipCardList")
    var membershipCardList: ArrayList<MembershipCard>? = null

    @SerializedName("contactID")
    private var contactID: String? = null
    var mContactID: String?
        get() {
            return contactID
        }
        set(value) {
            this.contactID = value
        }

    fun getPostalCode(): String?{
        return postalcode
    }

    fun isMember(): Boolean {
        var isMember = false
        membershipCardList?.forEach { membershipCard ->
            try {
                if (membershipCard.expiryDate != null) {
                    val today = Date()
                    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)
                    val expiryDate = simpleDateFormat.parse(membershipCard.expiryDate)
                    if (today.before(expiryDate)) {
                        isMember = true
                        return@forEach
                    }
                }
            } catch (e: Exception) {
                isMember = false
            }
        }
        return isMember
    }

    constructor(parcel: Parcel) : this() {
        customerId = parcel.readString()
        idno = parcel.readString()
        idtype = parcel.readString()
        phone = parcel.readString()
        email = parcel.readString()
        block = parcel.readString()
        dob = parcel.readString()
        floor = parcel.readString()
        fullName = parcel.readString()
        gender = parcel.readString()
        mobile = parcel.readString()
        nationality = parcel.readString()
        postalcode = parcel.readString()
        race = parcel.readString()
        status = parcel.readString()
        streetname = parcel.readString()
        unitno = parcel.readString()
        contactID = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(customerId)
        parcel.writeString(idno)
        parcel.writeString(idtype)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeString(block)
        parcel.writeString(dob)
        parcel.writeString(floor)
        parcel.writeString(fullName)
        parcel.writeString(gender)
        parcel.writeString(mobile)
        parcel.writeString(nationality)
        parcel.writeString(postalcode)
        parcel.writeString(race)
        parcel.writeString(status)
        parcel.writeString(streetname)
        parcel.writeString(unitno)
        parcel.writeString(contactID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomerInfo> {
        override fun createFromParcel(parcel: Parcel): CustomerInfo {
            return CustomerInfo(parcel)
        }

        override fun newArray(size: Int): Array<CustomerInfo?> {
            return arrayOfNulls(size)
        }
    }

}