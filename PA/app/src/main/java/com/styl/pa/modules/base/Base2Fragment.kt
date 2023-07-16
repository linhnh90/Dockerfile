package com.styl.pa.modules.base

import com.styl.pa.modules.main.view.MainActivity

abstract class Base2Fragment: BaseFragment() {
    open fun setCanOrder(isAlive: Boolean) {
        (activity as? MainActivity)?.canOrder = isAlive
    }
}