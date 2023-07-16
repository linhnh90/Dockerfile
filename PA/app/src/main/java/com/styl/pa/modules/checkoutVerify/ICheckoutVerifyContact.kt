package com.styl.pa.modules.checkoutVerify

import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.payment.PaymentRequest
import com.styl.pa.entities.reservation.TotalCostsResponse
import com.styl.pa.modules.base.IBaseContract

/**
 * Created by Ngatran on 09/22/2018.
 */
interface ICheckoutVerifyContact {
    interface IView : IBaseContract.IBaseView {
        fun getToken(): String?

        fun verifyResult(isResult: Boolean)

        fun setTotalCost(totalCostsResponse: TotalCostsResponse)

        fun getCartList(): ArrayList<CartItem>?

        fun getPaymentRequest() : PaymentRequest

        fun setParticipantInfo(participantInfo: CustomerInfo?)

        fun updatePaymentRequest(mSessionCode: String?)

        fun <T> showErrorMessageAndBack(response: BaseResponse<T>)

        fun showErrorMessageAndBack(messageResId: Int, title: Int)

        fun showErrorMessageAndTitle(messageResId: Int, title: Int)

        fun showErrorMessageAndTitle(message: String, title: Int)
    }

    interface IPresenter : IBaseContract.IBasePresenter

    interface IInteractor : IBaseContract.IBaseInteractor

    interface IInteractorOutputVerify<T> : IBaseContract.IBaseInteractorOutput<T> {
        fun customerInfo(customerInfo: CustomerInfo)

        fun sessionCode (sessionCode: String)
    }

    interface IRouter : IBaseContract.IBaseRouter {

    }
}