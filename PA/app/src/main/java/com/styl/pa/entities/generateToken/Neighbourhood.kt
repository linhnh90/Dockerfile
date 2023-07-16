package com.styl.pa.entities.generateToken

import com.google.gson.annotations.SerializedName

class Neighbourhood {
    @SerializedName("title")
    var title: String? = null
    @SerializedName("outlets")
    var outlets: ArrayList<String> = ArrayList()
}