package com.styl.pa.modules.checkoutVerify.presenter

import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.checkoutVerify.ICheckoutVerifyContact
import com.styl.pa.modules.checkoutVerify.interactor.CheckoutVerificationInteractor
import com.styl.pa.modules.checkoutVerify.router.CheckoutVerificationRouter
/**
 * Created by Ngatran on 09/22/2018.
 */
@ExcludeFromJacocoGeneratedReport
class CheckoutVerificationPresenter(var view: ICheckoutVerifyContact.IView?) : ICheckoutVerifyContact.IPresenter {

    private var interactor: CheckoutVerificationInteractor? = CheckoutVerificationInteractor()

    private var router: CheckoutVerificationRouter? = CheckoutVerificationRouter(view as BaseFragment)

    override fun onDestroy() {
        interactor?.onDestroy()
        view = null
        interactor = null
        router = null
    }

}