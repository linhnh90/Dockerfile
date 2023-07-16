package com.styl.pa.entities.promocode

import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.generateToken.Page

class ListPromoCodeResponse : Page() {
    @SerializedName("promotionList")
    var promoCodeList: ArrayList<PromoCode>? = null
}