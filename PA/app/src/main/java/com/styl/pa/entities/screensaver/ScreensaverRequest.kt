package com.styl.pa.entities.screensaver

import com.google.gson.annotations.SerializedName

class ScreensaverRequest(
    @SerializedName("Ts")
    var timestamp: Long,
    @SerializedName("Ids")
    var listId: ArrayList<Int?>? = null
)