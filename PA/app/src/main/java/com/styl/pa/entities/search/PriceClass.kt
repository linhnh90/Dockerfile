package com.styl.pa.entities.search

import com.google.gson.annotations.SerializedName

class PriceClass(
        @SerializedName("Min")
        var min: Int?,
        @SerializedName("Max")
        var max: Int?) {
}