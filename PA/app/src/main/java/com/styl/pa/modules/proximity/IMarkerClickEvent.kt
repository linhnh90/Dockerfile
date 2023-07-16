package com.styl.pa.modules.proximity

import com.styl.pa.entities.proximity.ProximityLocationInfo

/**
 * Created by Ngatran on 11/26/2019.
 */
interface IMarkerClickEvent {
    fun markerClick(proximityLocationInfo: ProximityLocationInfo)
}