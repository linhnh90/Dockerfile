package com.styl.pa.entities.facility

import com.google.gson.annotations.SerializedName

/**
 * Created by Ngatran on 09/29/2018.
 */
class FacilitySessionResponse {
    @SerializedName("date")
    private var date: String? = null
    @SerializedName("resourceList")
    private var resourceList: ArrayList<FacilitySessionInfo>? = null
    @SerializedName("resourceId")
    var resourceId: String? = null

    var mDate: String?
        get() {
            return date
        }
        set(value) {
            this.date = value
        }

    var mResourceList: ArrayList<FacilitySessionInfo>?
        get() {
            return resourceList
        }
        set(value) {
            this.resourceList = value
        }
}