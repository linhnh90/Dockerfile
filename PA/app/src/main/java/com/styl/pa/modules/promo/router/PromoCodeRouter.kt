package com.styl.pa.modules.promo.router

import com.styl.pa.modules.promo.IPromoCodeContact
import com.styl.pa.modules.promo.view.PromoCodeFragment
import com.styl.pa.modules.promo.view.PromoCodeTnCFragment

class PromoCodeRouter(val fragment: PromoCodeFragment?): IPromoCodeContact.IRouter {
    override fun navigateToTermAndCondition(url: String?) {
        if (fragment?.fragmentManager != null) {
            val f = PromoCodeTnCFragment.newInstance(url ?: "")
            f.show(fragment.fragmentManager!!, PromoCodeTnCFragment::class.java.simpleName)
        }
    }
}