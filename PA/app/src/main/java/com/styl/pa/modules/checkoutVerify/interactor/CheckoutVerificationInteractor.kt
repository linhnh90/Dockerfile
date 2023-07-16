package com.styl.pa.modules.checkoutVerify.interactor

import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.modules.base.BaseInteractor
import com.styl.pa.modules.checkoutVerify.ICheckoutVerifyContact
import com.styl.pa.services.IKioskServices
import com.styl.pa.services.ServiceGenerator
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Ngatran on 09/22/2018.
 */
@ExcludeFromJacocoGeneratedReport
class CheckoutVerificationInteractor : ICheckoutVerifyContact.IInteractor, BaseInteractor() {

    private val compositeDisposable = CompositeDisposable()
    var services = ServiceGenerator.createService(IKioskServices::class.java)

    override fun onDestroy() {
        compositeDisposable.clear()
    }

}