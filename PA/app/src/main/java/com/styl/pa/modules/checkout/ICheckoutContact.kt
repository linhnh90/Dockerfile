package com.styl.pa.modules.checkout

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.StringRes
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.cart.*
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.generateToken.BookingResponse
import com.styl.pa.entities.generateToken.Data
import com.styl.pa.entities.pacesRequest.*
import com.styl.pa.entities.pacesRequest.addEventParticipant.AddEventParticipantRequest
import com.styl.pa.entities.participant.ParticipantResponse
import com.styl.pa.entities.payment.PaymentRequest
import com.styl.pa.entities.payment.PaymentResponse
import com.styl.pa.entities.promocode.AvailablePromoCodeRequest
import com.styl.pa.entities.promocode.ListPromoCodeResponse
import com.styl.pa.entities.promocode.PromoCode
import com.styl.pa.entities.promocode.PromoCodeRequest
import com.styl.pa.entities.proxy.EmptyRequest
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.entities.reservation.*
import com.styl.pa.entities.wirecard.TransactionResponse
import com.styl.pa.modules.base.IBaseContract

/**
 * Created by Ngatran on 09/14/2018.
 */
interface ICheckoutContact {
    interface IView : IBaseContract.IBaseView {
        fun showMessageAndBack(message: String?)

        fun <T> showMessageAndBack(message: BaseResponse<T>)

        fun showErrorMessage(message: String)

        fun resultTotalCosts(totalCost: TotalCostsResponse)

        fun getToken(): String?

        fun getContext(): Context?

        fun insertLog(item: PaymentRequest)

        fun updateLog(item: PaymentRequest)

        fun updateReceiptNo(txnNo: String, receiptId: String)

        fun updateStatus(txnNo: String, status: Int)

        fun updateSignature(txnNo: String, signatureImage: String)

        fun insertReceipt(item: ReceiptRequest)

        fun deleteReceipt()

        fun updateReceipt(item: ReceiptRequest?)

        fun showErrorMessageTitleAndBack(@StringRes messageResId: Int, @StringRes title: Int)

        fun getCart(): Cart?

        fun reloadCart()

        fun loadCart()

        fun addAttendeeIntoCart(attendee: Attendee, parentIndex: Int)

        fun updateCartTableForCourse(
            request: ProgrammeBookingAuthRequest,
            data: BookingAuthResponse
        )

        fun updateCartTableForFacility(
            request: FacilityBookingAuthRequest,
            data: BookingAuthResponse
        )

        fun updateCartTableForEvent(request: ProgrammeBookingAuthRequest, data: BookingAuthResponse)

        fun clearReservationSuccess(parentIndex: Int, index: Int, callTotalCost: Boolean)

        fun updateSessionCode(sessionCode: String?)
        fun updatePdfFileAndSignature(txnNo: String, pdfFile: String?, signatureImage: String)

        fun onLoadCartSuccess(cartInfo: CartInfo?, isLoadCartForGetExternalLineId: Boolean = false, requestItems: ArrayList<ProductRequestItem>? = null)
        fun onDeleteItemSuccess(index: Int)
        fun onPrepareCheckoutSuccess()
        fun onPaymentDone()

        fun getCartId(): String?
        fun updateCartId(data: BookingResponse?)
        fun getPayer(): CustomerInfo?
        fun bookProduct()
        fun deleteCart()
        fun onDeleteCartFailed()
        fun removeCartLock()
        fun onDeleteFailedReservationResponse(data: ParticipantResponse?, errorMsg: String)

        fun isQuickBook(): Boolean
        fun onBookingFailed()
        fun updateReservation()

        fun onApplyRemovePromoCodeSuccess(cartInfo: CartInfo?, isApply: Boolean)
        fun onApplyPromoCodeError(error: String?)
        fun onRemovePromoCodeError(error: String?)
        fun onGetAvailablePromoCodesSuccess(listPromoCodeResponse: ListPromoCodeResponse?)
        fun showErrorMessageAvailablePromoCode(data: BaseResponse<ListPromoCodeResponse>)
    }

    interface IPresenter : IBaseContract.IBasePresenter {

        fun navigationCheckoutVerificationView(cart: Cart?)

        fun navigationPaymentSuccessfulView(
            transactionResponse: TransactionResponse?,
            receiptId: String?,
            totalCost: TotalCostsResponse?,
            bitmap: Bitmap?,
            bookingDetail: BookingDetail?,
            productType: String? = null
        )

        fun navigationView()
        fun navigationRatingView(payer: CustomerInfo?)

        fun navigationSignature()

        fun submitPayment(
            token: String?,
            kioskId: Int?,
            customerInfo: CustomerInfo?,
            isSubmitPayment: Boolean
        )

        fun updatePayment(token: String?, signatureImage: String?, pdfFile: String?)

        fun updatePayment(
            totalCost: TotalCostsResponse?,
            cartItems: List<CartItem>?,
            txnResponse: TransactionResponse?,
            rawData: String?,
            selectedPaymentType: Int?,
            responseCode: String?,
            referenceId: String?,
            payer: CustomerInfo?
        )

        fun updateFreePayment(totalCost: TotalCostsResponse?, payer: CustomerInfo?)

        fun updatePaymentRequest(mSessionCode: String?)
        fun updateSessionCode(sessionCode: String)
        fun backCartView()

        fun navigationPaymentView(amount: Float)

        fun loadCart(
            token: String, userId: String, cartId: String,
            courseRequestItems: ArrayList<ProductRequestItem>? = null, isCheckCart: Boolean = false
        )
        fun loadCartEventToGetExternalLineId(
            token: String, userId: String, cartId: String,
            eventRequestItems: ArrayList<ProductRequestItem>? = null, isCheckCart: Boolean = false
        )

        fun prepareCheckout(
            token: String,
            cartId: String,
            payer: CustomerInfo,
            paymentRefId: String?
        )

        fun applyPromoCode(code: String)

        fun removePromoCode()
        fun getAllAvailablePromoCode()
        fun navigationPromoCodeView(listPromoCodeResponse: ListPromoCodeResponse)
        fun navigateToEmailAdditional()
        fun navigateToDeclaration(requestCode: Int, quickBookProductType: String?)
    }

    interface IInteractor : IBaseContract.IBaseInteractor {

        fun submitPayment(token: String?, request: PaymentRequest?)

        fun updatePayment(token: String?, request: PaymentRequest?)

        fun loadCart(
            token: String, userId: String, cartId: String,
            output: IBaseContract.IBaseInteractorOutput<Data<CartData>>
        )

        fun removeItemFromCart(
            token: String,
            userId: String,
            request: ProxyRequest<ProductRequest>,
            output: IBaseContract.IBaseInteractorOutput<BookingResponse>
        )

        fun removeEventItemFromCart(
            token: String,
            userId: String,
            request: ProxyRequest<ProductRequest>,
            output: IBaseContract.IBaseInteractorOutput<BookingResponse>
        )

        fun removeItemIgFromCart(
            token: String,
            userId: String,
            request: ProxyRequest<ProductRequest>,
            output: IBaseContract.IBaseInteractorOutput<BookingResponse>
        )

        fun prepareCheckout(
            token: String,
            userId: String,
            request: ProxyRequest<CheckoutRequest>,
            output: IBaseContract.IBaseInteractorOutput<String>
        )

        fun addCourseToCart(
            token: String,
            userId: String,
            request: ProxyRequest<ProductRequest>,
            output: IBaseContract.IBaseInteractorOutput<BookingResponse>
        )

        fun addIgCart(
            token: String,
            userId: String,
            request: ProxyRequest<ProductRequest>,
            output: IBaseContract.IBaseInteractorOutput<BookingResponse>
        )

        fun addEventToCart(
            token: String,
            userId: String,
            request: ProxyRequest<ProductRequest>,
            output: IBaseContract.IBaseInteractorOutput<BookingResponse>
        )

        fun quickBookCourse(
            token: String,
            userId: String,
            request: ProxyRequest<ProductRequest>,
            output: IBaseContract.IBaseInteractorOutput<BookingResponse>
        )

        fun quickBookIg(
            token: String,
            userId: String,
            request: ProxyRequest<ProductRequest>,
            output: IBaseContract.IBaseInteractorOutput<BookingResponse>
        )

        fun quickBookFacility(
            token: String, userId: String,
            request: ProxyRequest<FacilityRequest>,
            output: IBaseContract.IBaseInteractorOutput<BookingResponse>
        )

        fun quickBookEvent(
            token: String,
            userId: String,
            request: ProxyRequest<ProductRequest>,
            output: IBaseContract.IBaseInteractorOutput<BookingResponse>
        )

        fun addParticipant(
            token: String,
            userId: String,
            request: ProxyRequest<ParticipantRequest<ParticipantItem>>,
            output: IBaseContract.IBaseInteractorOutput<ParticipantResponse>
        )

        fun addEventParticipant(
            token: String,
            userId: String,
            request: ProxyRequest<AddEventParticipantRequest>,
            output: IBaseContract.IBaseInteractorOutput<ParticipantResponse>
        )

        fun applyPromoCode(
            token: String, userId: String, request: ProxyRequest<PromoCodeRequest>,
            output: IBaseContract.IBaseInteractorOutput<Data<CartData>>
        )

        fun removePromoCode(
            token: String, userId: String, request: ProxyRequest<EmptyRequest>,
            output: IBaseContract.IBaseInteractorOutput<Data<CartData>>
        )

        fun getAllAvailablePromoCode(
            token: String, request: AvailablePromoCodeRequest,
            output: IBaseContract.IBaseInteractorOutput<ListPromoCodeResponse>
        )
    }

    interface IInteractorOutputTotalCost<T> : IBaseContract.IBaseInteractorOutput<T> {
        fun onAdditionData(data: String?, sessionCode: String)
    }

    interface IInteractorOutput2 : IBaseContract.IBaseInteractorOutput<PaymentResponse>

    interface IInteractorOutput3 : IBaseContract.IBaseInteractorOutput<PaymentResponse>

    interface IRouter : IBaseContract.IBaseRouter {

        fun navigationCheckoutVerificationView(cart: Cart?)

        fun navigationPaymentSuccessfulView(
            transactionResponse: TransactionResponse?,
            receiptId: String?,
            totalCost: TotalCostsResponse?,
            bitmap: Bitmap?,
            bookingDetail: BookingDetail?,
            productType: String? = null
        )

        fun navigationView()

        fun navigationSignature()
        fun navigationRatingView(payer: CustomerInfo?)
        fun backCartView(isDelete: Boolean)
        fun navigationPaymentView(amout: Float)
        fun navigationPromoCodeView(promoCodeList: ArrayList<PromoCode>)
        fun navigateToEmailAdditional()
        fun navigateToDeclaration(requestCode: Int, quickBookProductType: String?)
    }
}