package com.styl.pa.modules.payment.presenter

import androidx.annotation.VisibleForTesting
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.api.API
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.pacesRequest.CheckoutRequest
import com.styl.pa.entities.pacesRequest.PaymentRefRequest
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.entities.proxy.ProxyRequestHeader
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.payment.IPaymentContract
import com.styl.pa.modules.payment.interactor.PaymentInteractor
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by trangpham on 9/7/2018
 */
class PaymentPresenter(var view: IPaymentContract.IView?) : IPaymentContract.IPresenter, IPaymentContract.IInteractorOutput {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    constructor(view: IPaymentContract.IView?, interactor: PaymentInteractor?): this(view) {
        this.interactor = interactor
    }

    private var interactor: PaymentInteractor? = PaymentInteractor(this)

    @ExcludeFromJacocoGeneratedReport
    override fun subscribeTerminal(terminalSubject: BehaviorSubject<Boolean>) {
        interactor?.subscribeTerminal(terminalSubject)
    }

    private val getCreatePaymentReferenceResult = object : IBaseContract.IBaseInteractorOutput<String> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: String?) {
            val referenceId = data?.replace(Regex("\""), "")
            view?.onCreatePaymentReferenceSuccess(referenceId)
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<String>) {
            view?.dismissLoading()
            view?.showErrorMessage(data)
        }
    }

    override fun createPaymentReference(token: String?, paymentRefRequest: PaymentRefRequest) {
        view?.showLoading()
        val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
        val request = ProxyRequest(
                header,
                paymentRefRequest,
                ProxyRequest.POST_METHOD,
                "",
                API.uriCreatePaymentReference
        )
        interactor?.createPaymentReference(token, request, getCreatePaymentReferenceResult)
    }

    private val prepareCheckoutResult = object : IBaseContract.IBaseInteractorOutput<String> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: String?) {
            view?.onPrepareCheckoutSuccess()
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<String>) {
            view?.dismissLoading()
            view?.showErrorMessage(data)
        }

    }


    override fun prepareCheckout(token: String, cartId: String, payer: CustomerInfo, paymentRefId: String?, paymentMode: String?) {
        val checkoutRequest = CheckoutRequest(
                cartId,
                payer.mEmail,
                payer.mFullName,
                payer.mMobile,
                paymentRefId,
                paymentMode
        )
        val request = ProxyRequest(
                ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE),
                checkoutRequest,
                ProxyRequest.POST_METHOD,
                "",
                API.uriPrepareCheckout
        )
        view?.showLoading()
        interactor?.prepareCheckout(token, payer.mCustomerId ?: "", request, prepareCheckoutResult)
    }

    override fun onDestroy() {
        interactor?.onDestroy()
        view = null
        interactor = null
    }

    override fun onTerminalChanged(isConnected: Boolean) {
        if (!isConnected) {
            view?.onTerminalDisconnected()
        }
    }
}