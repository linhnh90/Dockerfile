package com.styl.pa.entities.event

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.generateToken.Page


/**
 * Created by Ngatran on 03/11/2019.
 */
class EventResponse : Page(), Parcelable {
    @SerializedName("eventList", alternate = ["productList"])
    @Expose
    var eventList: ArrayList<EventInfo>? = ArrayList()
}