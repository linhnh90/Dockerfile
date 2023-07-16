package com.styl.pa.modules.base

import com.styl.pa.utils.GeneralUtils

/**
 * Created by Ngatran on 09/17/2018.
 */
open class FullScreenDialogFragment: BaseDialogFragment() {
    override fun onResume() {
        super.onResume()

        val window = dialog?.window
        val metrics = GeneralUtils.getScreenSize(activity)
        window?.setLayout(metrics.widthPixels, metrics.heightPixels)

    }
}