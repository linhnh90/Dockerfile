package com.styl.pa.modules.proximity

import android.content.Context
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.styl.pa.entities.proximity.ProximityLocationInfo

/**
 * Created by Ngatran on 11/21/2019.
 */
class JavaScriptInterface {
    private var markerClickEvent: IMarkerClickEvent? = null
    private var activity: Context? = null

    constructor(markerClickEvent: IMarkerClickEvent, context: Context?) {
        this.markerClickEvent = markerClickEvent
        this.activity = context
    }

    @JavascriptInterface
    public fun getSearchInfo() {
        // Do nothing
        return
    }

    @JavascriptInterface
    public fun setLocationInfo(info: String) {
        val locationInfo = Gson().fromJson<ProximityLocationInfo>(info, ProximityLocationInfo::class.java)
        markerClickEvent?.markerClick(locationInfo)
    }
}