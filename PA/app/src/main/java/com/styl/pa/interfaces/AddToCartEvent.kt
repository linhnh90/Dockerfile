package com.styl.pa.interfaces

import android.view.View

/**
 * Created by Ngatran on 09/24/2018.
 */
interface AddToCartEvent{
    interface NavigationView{
        fun navigation()
    }

    interface RefreshView{
        fun refresh()
    }

    interface AddToCart{
        fun addItem(view: View, position: Int, isQuickBook: Boolean = false)
    }
}