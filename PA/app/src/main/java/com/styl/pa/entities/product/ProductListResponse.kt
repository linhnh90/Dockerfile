package com.styl.pa.entities.product

import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.generateToken.Page

class ProductListResponse: Page() {
    @SerializedName("productList")
    var productList: ArrayList<Product>? = null
}