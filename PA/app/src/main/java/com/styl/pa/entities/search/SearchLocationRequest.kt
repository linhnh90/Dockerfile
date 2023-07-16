package com.styl.pa.entities.search

import com.google.gson.annotations.SerializedName

class SearchLocationRequest(
        @SerializedName("Keyword")
        var keyword: String?,
        @SerializedName("PageIndex")
        var pageIndex: Int?,
        @SerializedName("PageSize")
        var pageSize: Int?,
        @SerializedName("OutletTypeNames")
        var outletTypeName: ArrayList<String> = arrayListOf("CC", "CCMC")
) {
}