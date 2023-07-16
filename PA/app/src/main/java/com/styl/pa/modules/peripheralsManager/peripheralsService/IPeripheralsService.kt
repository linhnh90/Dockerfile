package com.styl.pa.modules.peripheralsManager.peripheralsService

import android.content.Context

/**
 * Created by NgaTran on 9/15/2020.
 */
interface IPeripheralsService {
    fun detectUsbPeripherals(context: Context?)
}