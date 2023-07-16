package com.styl.pa.entities.proximity

import com.google.gson.annotations.SerializedName

/**
 * Created by Ngatran on 12/02/2019.
 */
class ProximityLocationResponse {
    @SerializedName("totalProduct")
    var totalProduct: Int? = 0

    @SerializedName("totalRecord")
    var totalRecord: Int? = 0

    @SerializedName("outletList")
    var outletList: ArrayList<ProximityLocationInfo>? = null
}