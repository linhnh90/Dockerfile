package com.styl.pa.modules.promo.presenter

import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.modules.promo.IPromoCodeContact
import com.styl.pa.modules.promo.router.PromoCodeRouter
import com.styl.pa.modules.promo.view.PromoCodeFragment

@ExcludeFromJacocoGeneratedReport
class PromoCodePresenter(view: IPromoCodeContact.IView): IPromoCodeContact.IPresenter {

    private var router = PromoCodeRouter(view as? PromoCodeFragment)

    override fun navigateToTermAndCondition(url: String?) {
        router.navigateToTermAndCondition(url)
    }
}