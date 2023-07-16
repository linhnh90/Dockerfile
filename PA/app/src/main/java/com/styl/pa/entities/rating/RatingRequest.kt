package com.styl.pa.entities.rating

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.feedback.FeedbackRequest
import com.styl.pa.utils.GeneralUtils


/**
 * Created by Ngatran on 03/22/2019.
 */
open class RatingRequest : Parcelable {

    @SerializedName(value = "Level", alternate = ["level"])
    @Expose
    var level: Int? = null
    @SerializedName(value = "Reused", alternate = ["reused"])
    @Expose
    var reused: Boolean? = false
    @SerializedName(value = "Age", alternate = ["age"])
    @Expose
    var age: String? = null
    @SerializedName(value = "Gender", alternate = ["gender"])
    @Expose
    var gender: String? = null
    @SerializedName(value = "PayerId", alternate = ["payerId"])
    @Expose
    var payerId: String? = null

    constructor(parcel: Parcel) : this() {
        level = parcel.readValue(Int::class.java.classLoader) as? Int
        reused = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        age = parcel.readString()
        gender = parcel.readString()
        payerId = parcel.readString()
    }

    constructor()

    constructor(level: Int, birthday: String?, gender: String?, payerId: String?) {
        this.level = level
        if (!birthday.isNullOrEmpty()) {
            this.age = GeneralUtils.convertDateToAge(birthday)
        }
        this.gender = gender
        this.payerId = payerId
    }

    fun convertFeedbackRequest(): FeedbackRequest {
        return FeedbackRequest(this, null);
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(level)
        parcel.writeValue(reused)
        parcel.writeString(age)
        parcel.writeString(gender)
        parcel.writeString(payerId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RatingRequest> {
        override fun createFromParcel(parcel: Parcel): RatingRequest {
            return RatingRequest(parcel)
        }

        override fun newArray(size: Int): Array<RatingRequest?> {
            return arrayOfNulls(size)
        }

        const val VERRY_POOR = 1
        const val POOR = 2
        const val AVERAGE = 3
        const val GOOD = 4
        const val EXCELLENT = 5
    }

}