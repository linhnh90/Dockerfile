package com.styl.pa.entities.generateToken

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PageByOutletType : Page() {
    @SerializedName("outletTypeList")
    @Expose
    private var outletTypeList: ArrayList<OutletType> = ArrayList()

    fun getOutletTypeList(): ArrayList<OutletType> {
        return outletTypeList
    }

    fun setOutletTypeList(outletTypeList: ArrayList<OutletType>) {
        this.outletTypeList = outletTypeList
    }

}