package com.styl.pa.modules.payment

import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.pacesRequest.CheckoutRequest
import com.styl.pa.entities.pacesRequest.PaymentRefRequest
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.modules.base.IBaseContract
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by trangpham on 9/7/2018
 */
interface IPaymentContract {

    interface IView : IBaseContract.IBaseView {

        fun getToken(): String?

        fun onTerminalDisconnected()
        fun onCreatePaymentReferenceSuccess(data: String?)
        fun onPrepareCheckoutSuccess()
    }

    interface IPresenter : IBaseContract.IBasePresenter {

        fun subscribeTerminal(terminalSubject: BehaviorSubject<Boolean>)

        fun prepareCheckout(token: String, cartId: String, payer: CustomerInfo, paymentRefId: String?, paymentMode: String?)
        fun createPaymentReference(token: String?, paymentRefRequest: PaymentRefRequest)

    }

    interface IInteractor : IBaseContract.IBaseInteractor {

        fun subscribeTerminal(terminalSubject: BehaviorSubject<Boolean>)

        fun createPaymentReference(token: String?,
                                            request: ProxyRequest<PaymentRefRequest>,
                                            output: IBaseContract.IBaseInteractorOutput<String>)

        fun prepareCheckout(token: String,
                            userId: String,
                            request: ProxyRequest<CheckoutRequest>,
                            output: IBaseContract.IBaseInteractorOutput<String>)

    }

    interface IInteractorOutput {

        fun onTerminalChanged(isConnected: Boolean)
    }

    interface IRouter : IBaseContract.IBaseRouter {

    }
}