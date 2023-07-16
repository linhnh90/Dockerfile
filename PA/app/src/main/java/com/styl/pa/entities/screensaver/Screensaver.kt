package com.styl.pa.entities.screensaver

import com.google.gson.annotations.SerializedName

class Screensaver: ScreensaverBase() {
    @SerializedName("fileName")
    var fileName: String? = null
    @SerializedName("imageBase64")
    var imageBase64: String? = null
    @SerializedName("status")
    var status: Int? = null
}