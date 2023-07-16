package com.styl.pa.interfaces

import android.view.View

/**
 * Created by Ngatran on 03/15/2019.
 */
interface OnClickItem {
    interface OnClickSingleIndex {
        fun <T> onClick(view: View, param: T)
    }

    interface OnClickMultiIndex {
        fun <T> onClick(view: View, param1: T, param2: T)
    }

    interface OnChangeNoOfMember {
        fun <T> onClick(view: View, param: T, isNoOfMember: Int)
    }
}