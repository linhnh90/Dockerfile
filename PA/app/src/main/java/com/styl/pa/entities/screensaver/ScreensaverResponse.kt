package com.styl.pa.entities.screensaver

import com.google.gson.annotations.SerializedName

class ScreensaverResponse {
    @SerializedName("totalRecord")
    var totalRecord: Int? = null
    @SerializedName("items")
    var items: MutableList<Screensaver>? = null
}