package com.styl.pa.entities.proximity

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.generateToken.Outlet

/**
 * Created by Ngatran on 11/22/2019.
 */
class ProximityLocationInfo {
    @SerializedName("outlet")
    var outlet: Outlet? = null

    @Keep
    @SerializedName("productQuantities")
    var productQuantities: Int = 0

    constructor()

    constructor(outlet: Outlet?, productQuantities: Int) {
        this.outlet = outlet
        this.productQuantities = productQuantities
    }
}