package com.styl.pa.entities.main

import com.google.gson.annotations.SerializedName

/**
 * Created by trangpham on 9/26/2018
 */
class KioskUpdateResponse(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("message")
    var message: String?
) {
}