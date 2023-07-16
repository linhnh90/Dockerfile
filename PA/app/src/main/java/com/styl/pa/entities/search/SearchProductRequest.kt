package com.styl.pa.entities.search

import com.google.gson.annotations.SerializedName

class SearchProductRequest {
    @SerializedName("Keyword")
    var keyword: String? = null

    @SerializedName("Type")
    var type: String? = null

    @SerializedName("PageIndex")
    var pageIndex: Int? = null

    @SerializedName("PageSize")
    var pageSize: Int? = null

    @SerializedName("Longitude")
    var longitude: Double? = null

    @SerializedName("Latitude")
    var latitude: Double? = null

    @SerializedName("Price")
    var price: PriceClass? = null

    @SerializedName("Sort")
    var sort: String? = null

    @SerializedName("OutletNames")
    var outlets: ArrayList<String>? = null

    @SerializedName("ProductCode")
    var productCode: String? = null

    constructor()

    constructor(keyword: String?, type: String?, pageIndex: Int?, pageSize: Int?, longitude: Double?, latitude: Double?, price: PriceClass?, sort: String?, outlets: ArrayList<String>?, productCode: String? = null) {
        this.keyword = keyword
        this.type = type
        this.pageIndex = pageIndex
        this.pageSize = pageSize
        this.longitude = longitude
        this.latitude = latitude
        this.price = price
        this.sort = sort
        this.outlets = outlets
        this.productCode = productCode
    }

    companion object {
        const val DESC = "desc"
        const val ASC = "asc"

        const val START_DATE = "startDate"
        const val PRICE = "pricesPrice"
        const val TITLE = "title"
    }
}