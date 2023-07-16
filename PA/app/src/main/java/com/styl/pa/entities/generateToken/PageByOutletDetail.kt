package com.styl.pa.entities.generateToken

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class PageByOutletDetail : Page() {
    @SerializedName("listOutlets")
    @Expose
    private var outletList: ArrayList<Outlet> = ArrayList()

    @SerializedName("neighbourhoods")
    private var neighbourhoods: ArrayList<Neighbourhood> = ArrayList()

    fun getOutletDetailList(): ArrayList<Outlet> {
        return outletList
    }

    fun setOutletDetailList(outletList: ArrayList<Outlet>) {
        this.outletList = outletList
    }

    fun getNeighbourhood(): ArrayList<Neighbourhood> {
        return neighbourhoods
    }
}