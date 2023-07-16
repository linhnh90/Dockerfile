package com.styl.pa.modules.promo

interface IPromoCodeContact {
    interface IView {

    }

    interface IPresenter {
        fun navigateToTermAndCondition(url: String?)
    }

    interface IRouter {
        fun navigateToTermAndCondition(url: String?)
    }
}