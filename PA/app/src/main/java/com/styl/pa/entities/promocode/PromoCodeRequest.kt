package com.styl.pa.entities.promocode

import com.google.gson.annotations.SerializedName

class PromoCodeRequest(
    @SerializedName("cartId")
    var cartId: String? = null,
    @SerializedName("promoCode")
    var promoCode: String? = null
)