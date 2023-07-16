package com.styl.pa.modules.paymentSuccessful

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.kioskactivation.KioskInfo
import com.styl.pa.entities.reservation.BookingDetail
import com.styl.pa.entities.reservation.TotalCostsResponse
import com.styl.pa.entities.sendEmail.SendEmailRequest
import com.styl.pa.entities.sendEmail.SendEmailResponse
import com.styl.pa.entities.sendmail.SendMailRequest
import com.styl.pa.entities.sendmail.SendMailResponse
import com.styl.pa.entities.wirecard.TransactionResponse
import com.styl.pa.modules.base.IBaseContract

/**
 * Created by trangpham on 9/30/2018
 */
interface IPaymentSuccessfulContract {

    interface IView : IBaseContract.IBaseView {

        fun showInvalidMail()
        fun onSendSuccess()
        fun goHome()
        fun showWaitingPage()
        fun dismissWaitingPage()
        fun showErrorMessage(titleId: Int, messageId: Int)
        fun navigationRatingView()
        fun getPayerInfo(): CustomerInfo?
    }

    interface IPresenter : IBaseContract.IBasePresenter {

        fun isMailValid(email: String?): Boolean
        fun sendMail(token: String?, receivers: List<String>?, subject: String?, content: String?)
        fun generateReceipt(cartList: ArrayList<CartItem>?, receiptId: String?,
                            kioskInfo: KioskInfo?, totalCost: TotalCostsResponse?,
                            payment: TransactionResponse?, customer: CustomerInfo?,
                            bitmap: Bitmap?, parentFragment: Fragment?, bookingDetail: BookingDetail?)

        fun pdfReceiptResult(): String?
        fun reportHealthDevice(code: Int)
    }

    interface IInteractor : IBaseContract.IBaseInteractor {

        fun sendMail(token: String?, request: SendMailRequest?)

        fun sendEmail(token: String?, request: SendEmailRequest?, callBack: IBaseContract.IBaseInteractorOutput<SendEmailResponse>)
    }

    interface IInteractorOutput : IBaseContract.IBaseInteractorOutput<SendMailResponse> {

    }
}