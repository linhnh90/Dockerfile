package com.styl.pa.entities.screensaver

import com.google.gson.annotations.SerializedName

open class ScreensaverBase(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("updatedTime")
    var updatedTime: Long? = null
)