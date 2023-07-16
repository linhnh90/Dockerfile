package com.styl.pa.entities.interestgroup

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.generateToken.Page

class PageByInterestGroup: Page() {
    @SerializedName("igList")
    @Expose
    private var igList: ArrayList<InterestGroup>? = ArrayList()

    fun getIgList(): ArrayList<InterestGroup>? {
        return igList
    }

    fun setIgList(list: ArrayList<InterestGroup>?){
        this.igList = list
    }
}