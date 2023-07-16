package com.styl.pa.entities.pacesRequest

import com.google.gson.annotations.SerializedName

class ProductRequest(
        @SerializedName("cartId")
        var cartId: String?,
        @SerializedName("items")
        var items: MutableList<ProductRequestItem>
)