package com.styl.pa.entities.rulesAndRegulations

import com.google.gson.annotations.SerializedName

/**
 * Created by Ngatran on 10/11/2018.
 */
class RulesAndRegulationsResponse {
    @SerializedName("content")
    private var rulesUrl: String? = null
    var mRulesUrl: String?
        get() {
            return rulesUrl
        }
        set(value) {
            this.rulesUrl = value
        }
}