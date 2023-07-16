package com.styl.pa.entities.pacesRequest

import com.google.gson.annotations.SerializedName

open class ProductRequestItem (
    @SerializedName("itemId")
    var itemId: String?,
    @SerializedName("qty")
    var qty: String? = "1",
    @SerializedName("tickets")
    var tickets: ArrayList<ProductRequestItemTicket>? = null
)