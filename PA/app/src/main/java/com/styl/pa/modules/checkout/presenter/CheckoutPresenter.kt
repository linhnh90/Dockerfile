package com.styl.pa.modules.checkout.presenter

import android.graphics.Bitmap
import androidx.annotation.VisibleForTesting
import com.styl.castle_terminal_upt1000_api.define.FieldData
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.R
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.api.API
import com.styl.pa.entities.cart.Cart
import com.styl.pa.entities.cart.CartData
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.generateToken.BookingResponse
import com.styl.pa.entities.generateToken.Data
import com.styl.pa.entities.generateToken.Facility
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.entities.kioskactivation.KioskInfo
import com.styl.pa.entities.pacesRequest.*
import com.styl.pa.entities.pacesRequest.addEventParticipant.AddEventParticipantRequest
import com.styl.pa.entities.pacesRequest.addEventParticipant.EventParticipantItem
import com.styl.pa.entities.participant.ParticipantResponse
import com.styl.pa.entities.payment.PaymentItem
import com.styl.pa.entities.payment.PaymentRequest
import com.styl.pa.entities.payment.PaymentResponse
import com.styl.pa.entities.product.Product
import com.styl.pa.entities.promocode.AvailablePromoCodeRequest
import com.styl.pa.entities.promocode.ListPromoCodeResponse
import com.styl.pa.entities.promocode.PromoCodeRequest
import com.styl.pa.entities.proxy.EmptyRequest
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.entities.proxy.ProxyRequestHeader
import com.styl.pa.entities.reservation.BookingDetail
import com.styl.pa.entities.reservation.ReceiptRequest
import com.styl.pa.entities.reservation.TotalCostsResponse
import com.styl.pa.entities.wirecard.TransactionResponse
import com.styl.pa.entities.wirecard.WireCardMessage
import com.styl.pa.enums.Gender
import com.styl.pa.enums.IdType
import com.styl.pa.enums.Nationality
import com.styl.pa.enums.RaceType
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.checkout.ICheckoutContact
import com.styl.pa.modules.checkout.interactor.CheckoutInteractor
import com.styl.pa.modules.checkout.router.CheckoutRouter
import com.styl.pa.modules.payment.view.PaymentFragment
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.LogManager
import com.styl.pa.utils.MySharedPref
import com.styl.pa.utils.StoreLogsUtils
import org.jetbrains.annotations.TestOnly
import kotlin.math.roundToInt


/**
 * Created by Ngatran on 09/14/2018.
 */
class CheckoutPresenter(var view: ICheckoutContact.IView?) : ICheckoutContact.IPresenter {
    companion object {

        private val TAG = CheckoutPresenter::class.java.simpleName

        val NON_MEMBER_CODE = 45456
        val CLEAR_ALL = -1
    }

    @TestOnly
    constructor(
        view: ICheckoutContact.IView?,
        interactor: CheckoutInteractor?, router: CheckoutRouter?
    ) : this(view) {
        this.interactor = interactor
        this.router = router
    }

    private var interactor: CheckoutInteractor? = CheckoutInteractor()

    private var router: CheckoutRouter? = CheckoutRouter(view as? BaseFragment)

    var sessionCode: String? = null
    var isDelete = false

    private var totalAddParticipantRequest = 0
    private var currentParticipantProgress = 0
    private var hasFailedParticipantRequest = false

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var paymentRequest: PaymentRequest? = null

    private var errMsg = StringBuilder()

    override fun updateSessionCode(sessionCode: String) {
        this.sessionCode = sessionCode
    }

    private val output2 = object : ICheckoutContact.IInteractorOutput2 {

        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: PaymentResponse?) {
            updatePaymentRequest(data?.txnNo)
            view?.bookProduct()
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<PaymentResponse>) {
            view?.dismissLoading()
            view?.showMessageAndBack(view?.getContext()?.getString(R.string.common_error))
        }
    }

    @ExcludeFromJacocoGeneratedReport
    override fun updatePaymentRequest(mSessionCode: String?) {
        sessionCode = mSessionCode
        view?.updateSessionCode(sessionCode)

        paymentRequest?.txnNo = sessionCode ?: ""
        paymentRequest?.paymentStatus = PaymentRequest.STATUS_CREATED
        // add items here
        val items: MutableList<PaymentItem> = ArrayList()
        val cart = view?.getCart()
        cart?.items?.forEach item@{ item ->
            if (item.classInfo != null) {
                item.attendees?.forEach attendee@{
                    var beforeDiscountAmount = 0
                    item.classInfo?.getClassFees()?.let { fees ->
                        for (fee in fees) {
                            val amount = Math.round(
                                (fee.getClassFeeAmount()?.toFloat()
                                    ?: 0f) * 100
                            )

                            if (amount > beforeDiscountAmount) {
                                beforeDiscountAmount = amount
                            }
                        }
                    }

                    val paymentItem = PaymentItem(
                        item.classInfo?.getClassCode(), item.classInfo?.getClassTitle(),
                        "", "", beforeDiscountAmount, 0, 0, null
                    )
                    paymentItem.resourceType = PaymentItem.TYPE_CLASS
                    paymentItem.outletId = item.classInfo?.getOutletId()
                    paymentItem.outletName = item.classInfo?.getOutletName()
                    paymentItem.outletTypeName = item.classInfo?.outletTypeName

                    paymentItem.txnNo = sessionCode
                    items.add(paymentItem)
                }
            } else if (item.igInfo != null) {
                item.attendees?.forEach attendee@{
                    var beforeDiscountAmount = 0
                    item.igInfo?.igFees?.let { fees ->
                        for (fee in fees) {
                            val amount = Math.round(
                                (fee.getClassFeeAmount()?.toFloat()
                                    ?: 0f) * 100
                            )

                            if (amount > beforeDiscountAmount) {
                                beforeDiscountAmount = amount
                            }
                        }
                    }

                    val paymentItem = PaymentItem(
                        item.igInfo?.igCode, item.igInfo?.igTitle,
                        "", "", beforeDiscountAmount, 0, 0, null
                    )
                    paymentItem.resourceType = PaymentItem.TYPE_INTEREST_GROUP
                    paymentItem.outletId = item.igInfo?.outletId
                    paymentItem.outletName = item.igInfo?.outletName
                    paymentItem.outletTypeName = item.igInfo?.outletTypeName

                    paymentItem.txnNo = sessionCode
                    items.add(paymentItem)
                }
            } else if (item.facility != null) {
                var beforeDiscountAmount = 0
                item.facility?.getResourceFeeList()?.let { fees ->
                    for (fee in fees) {
                        val amount = Math.round((fee.getFeePeakAmount()?.toFloat() ?: 0f) * 100)

                        if (amount > beforeDiscountAmount) {
                            beforeDiscountAmount = amount
                        }
                    }
                }

                val paymentItem = PaymentItem(
                    null, item.facility?.getResourcetName(),
                    "", "", beforeDiscountAmount, 0, 0, null
                )
                paymentItem.resourceType = PaymentItem.TYPE_FACILITY
                paymentItem.outletId = item.facility?.outletId
                paymentItem.outletName = item.facility?.outletName
                paymentItem.outletTypeName = item.facility?.outletTypeName

                paymentItem.txnNo = sessionCode
                items.add(paymentItem)
            } else { //event
                var beforeDiscountAmount = 0
                item.event?.listSelectedTicket?.forEach { eventTicket ->
                    eventTicket.listTicketParticipantEntity.forEach { ticketEntity ->
                        ticketEntity.listParticipant.forEach { participant ->
                            beforeDiscountAmount = ((eventTicket.price ?: 0f) * 100).roundToInt()
                            val paymentItem = PaymentItem(
                                item.event?.eventCode, item.event?.eventTitle,
                                "", "", beforeDiscountAmount, 0, 0, null
                            )
                            paymentItem.resourceType = PaymentItem.TYPE_EVENT
                            paymentItem.outletId = item.event?.outletID
                            paymentItem.outletName = item.event?.outletName
                            paymentItem.outletTypeName = item.event?.outletTypeName

                            paymentItem.txnNo = sessionCode
                            items.add(paymentItem)
                        }
                    }
                }

            }
        }
        paymentRequest?.items = items
        createRecord(paymentRequest)
    }

    private val output3 = object : ICheckoutContact.IInteractorOutput3 {

        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: PaymentResponse?) {
            if (paymentRequest?.txnNo != null &&
                (paymentRequest?.paymentStatus == PaymentRequest.STATUS_UNSUCCESSFUL ||
                        paymentRequest?.paymentStatus == PaymentRequest.STATUS_PAID)
            ) {
                updateStatus(paymentRequest?.txnNo!!, PaymentRequest.STATUS_DONE)
            }
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<PaymentResponse>) {
            LogManager.d(TAG, "Update payment OnError")
        }
    }

    init {
        interactor?.output2 = output2
        interactor?.output3 = output3
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        interactor?.onDestroy()
        view = null
        interactor = null
        router = null
    }

    @ExcludeFromJacocoGeneratedReport
    override fun backCartView() {
        router?.backCartView(isDelete)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationPaymentView(amount: Float) {
        router?.navigationPaymentView(amount)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationCheckoutVerificationView(cart: Cart?) {
        router?.navigationCheckoutVerificationView(cart)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationPaymentSuccessfulView(
        transactionResponse: TransactionResponse?,
        receiptId: String?,
        totalCost: TotalCostsResponse?,
        bitmap: Bitmap?,
        bookingDetail: BookingDetail?,
        productType: String?
    ) {
        router?.navigationPaymentSuccessfulView(
            transactionResponse,
            receiptId,
            totalCost,
            bitmap,
            bookingDetail,
            productType
        )
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationSignature() {
        router?.navigationSignature()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationView() {
        router?.navigationView()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationRatingView(payer: CustomerInfo?) {
        router?.navigationRatingView(payer)
    }

    private fun resetState() {
        currentParticipantProgress = 0
        totalAddParticipantRequest = 0
    }

    @ExcludeFromJacocoGeneratedReport
    private fun checkBookingProcess() {
        if (hasFailedParticipantRequest) {
            resetState()
            errMsg.clear()
            view?.dismissLoading()
            view?.onBookingFailed()
            return
        }
        if (errMsg.isNotEmpty()) {
            view?.dismissLoading()
            view?.showMessageAndBack(errMsg.toString())
            errMsg.clear()
            return
        }
        view?.dismissLoading()
        if (currentParticipantProgress != totalAddParticipantRequest) {
            return
        }
        resetState()
        if (view?.isQuickBook() != true && needToCallAddToCartProcess()){
            addToCartProcess()
            return
        }
        view?.removeCartLock()
        val token = view?.getToken() ?: ""
        val userId = view?.getPayer()?.mCustomerId ?: ""
        loadCart(token, userId, view?.getCartId() ?: "")
    }

    val quickBookFacilityOutput = object : IBaseContract.IBaseInteractorOutput<BookingResponse> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: BookingResponse?) {
            view?.updateCartId(data)
            view?.updateReservation()
            facilityRequestItems.clear()
            checkBookingProcess()
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<BookingResponse>) {
            view?.dismissLoading()
            view?.showMessageAndBack(data)
            facilityRequestItems.clear()
        }

    }

    @ExcludeFromJacocoGeneratedReport
    private fun checkParticipantError(data: ParticipantResponse?) {
        if (data?.errValidation?.hasErr == true) {
            errMsg.clear()
            data.cartError?.forEach { errorItem ->
                val cartItem = view?.getCart()?.items?.find {
                    it.classInfo?.sku == errorItem.itemName
                }
                val itemName = cartItem?.classInfo?.getClassTitle()
                if (errorItem.participantsError?.isNotEmpty() == true) {
                    errorItem.participantsError!!.forEach { error ->
                        var errorStr = error.crmErrorMessage
                        if (errorStr.isNullOrEmpty()) {
                            errorStr = error.errorMessage
                        }
                        errMsg.append("$itemName: $errorStr").append("\n")
                    }
                } else {
                    errMsg.append("$itemName: ${errorItem.message}").append("\n")
                }
            }
            if (errMsg.isEmpty()) {
                hasFailedParticipantRequest = true
            }
        }

        view?.updateReservation()
        checkBookingProcess()
    }

    @ExcludeFromJacocoGeneratedReport
    private fun checkEventParticipantError(data: ParticipantResponse?) {
        if (data?.errValidation?.hasErr == true) {
            errMsg.clear()
            if(!data.cartError.isNullOrEmpty()){
                data.cartError?.forEach { errorItem ->
                    val cartItem = view?.getCart()?.items?.find {
                        it.event?.sku == errorItem.itemName
                    }
                    val itemName = cartItem?.event?.getDecodedTitle()
                    if (errorItem.participantsError?.isNotEmpty() == true) {
                        errorItem.participantsError!!.forEach { error ->
                            var errorStr = error.crmErrorMessage
                            if (errorStr.isNullOrEmpty()) {
                                errorStr = error.errorMessage
                            }
                            val newError = "$itemName: $errorStr"
                            if (!errMsg.contains(newError)) {
                                errMsg.append(newError).append("\n")
                            }
                        }
                    } else {
                        errMsg.append("$itemName: ${errorItem.message}").append("\n")
                    }
                }
            } else {
                if(data.errValidation?.message?.isNotEmpty() == true){
                    errMsg.append(data.errValidation?.message)
                }
            }

            if (errMsg.isEmpty()) {
                hasFailedParticipantRequest = true
            }
        }
        view?.updateReservation()
        checkBookingProcess()
    }

    @ExcludeFromJacocoGeneratedReport
    private fun checkParticipantErrorIg(data: ParticipantResponse?) {
        if (data?.errValidation?.hasErr == true) {
            errMsg.clear()
            data.cartError?.forEach { errorItem ->
                val cartItem = view?.getCart()?.items?.find {
                    it.igInfo?.sku == errorItem.itemName
                }
                val itemName = cartItem?.igInfo?.igTitle
                if (errorItem.participantsError?.isNotEmpty() == true) {
                    errorItem.participantsError!!.forEach { error ->
                        var errorStr = error.crmErrorMessage
                        if (errorStr.isNullOrEmpty()) {
                            errorStr = error.errorMessage
                        }
                        errMsg.append("$itemName: $errorStr").append("\n")
                    }
                } else {
                    errMsg.append("$itemName: ${errorItem.message}").append("\n")
                }
            }
            if (errMsg.isEmpty()) {
                hasFailedParticipantRequest = true
            }
        }
        view?.updateReservation()
        checkBookingProcess()
    }

    fun getAddParticipantOutput(
        courseRequestItems: ArrayList<ProductRequestItem>,
        index: Int
    ): IBaseContract.IBaseInteractorOutput<ParticipantResponse> {
        return object : IBaseContract.IBaseInteractorOutput<ParticipantResponse> {
            @ExcludeFromJacocoGeneratedReport
            override fun onSuccess(data: ParticipantResponse?) {
                if (index < totalAddParticipantRequest - 1) {
                    val nextIndex = index + 1
                    addParticipant(courseRequestItems, nextIndex)
                }
                currentParticipantProgress += 1

                if (currentParticipantProgress < totalAddParticipantRequest) {
                    return
                }

                this@CheckoutPresenter.courseRequestItems.clear()
                checkParticipantError(data)
            }

            @ExcludeFromJacocoGeneratedReport
            override fun onError(data: BaseResponse<ParticipantResponse>) {
                currentParticipantProgress += 1

                this@CheckoutPresenter.courseRequestItems.clear()
                if (data.data != null) {
                    checkParticipantError(data.data)
                    return
                }

                hasFailedParticipantRequest = true
                checkBookingProcess()
            }
        }
    }


    private fun getAddEventParticipantOutput(
        eventRequestItems: ArrayList<ProductRequestItem>,
        index: Int
    ): IBaseContract.IBaseInteractorOutput<ParticipantResponse> {
        return object : IBaseContract.IBaseInteractorOutput<ParticipantResponse> {
            @ExcludeFromJacocoGeneratedReport
            override fun onSuccess(data: ParticipantResponse?) {
                /*if (index < totalAddParticipantRequest - 1) {
                    val nextIndex = index + 1
                    addEventParticipant(eventRequestItems, nextIndex)
                }
                currentParticipantProgress += 1

                if (currentParticipantProgress < totalAddParticipantRequest) {
                    return
                }*/

                this@CheckoutPresenter.eventRequestItems.clear()
                checkEventParticipantError(data)
            }

            @ExcludeFromJacocoGeneratedReport
            override fun onError(data: BaseResponse<ParticipantResponse>) {
//                currentParticipantProgress += 1

                this@CheckoutPresenter.eventRequestItems.clear()
                if (data.data != null) {
                    checkEventParticipantError(data.data)
                    return
                }

                hasFailedParticipantRequest = true
                checkBookingProcess()
            }
        }
    }

    fun getAddParticipantOutputIg(
        igRequestItems: ArrayList<ProductRequestItem>,
        index: Int
    ): IBaseContract.IBaseInteractorOutput<ParticipantResponse> {
        return object : IBaseContract.IBaseInteractorOutput<ParticipantResponse> {
            @ExcludeFromJacocoGeneratedReport
            override fun onSuccess(data: ParticipantResponse?) {
                if (index < totalAddParticipantRequest - 1) {
                    val nextIndex = index + 1
                    addParticipantIg(igRequestItems, nextIndex)
                }
                currentParticipantProgress += 1

                if (currentParticipantProgress < totalAddParticipantRequest) {
                    return
                }

                this@CheckoutPresenter.igRequestItems.clear()
                checkParticipantErrorIg(data)
            }

            @ExcludeFromJacocoGeneratedReport
            override fun onError(data: BaseResponse<ParticipantResponse>) {
                currentParticipantProgress += 1

                this@CheckoutPresenter.igRequestItems.clear()
                if (data.data != null) {
                    checkParticipantErrorIg(data.data)
                    return
                }

                hasFailedParticipantRequest = true
                checkBookingProcess()
            }
        }
    }

    fun quickBookFacility(cartId: String?, facilityRequestItems: ArrayList<FacilityRequestItem>) {
        if (facilityRequestItems.size > 0) {
            val proxyRequest = ProxyRequest(
                ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE),
                FacilityRequest(
                    cartId = cartId,
                    items = facilityRequestItems
                ),
                ProxyRequest.POST_METHOD,
                "",
                API.uriQuickBookFacility
            )
            interactor?.quickBookFacility(
                view?.getToken() ?: "",
                view?.getPayer()?.mCustomerId ?: "",
                proxyRequest,
                quickBookFacilityOutput
            )
        }
    }

    fun quickBookFacilitySomeone(
        cartId: String?,
        facilityRequestItems: ArrayList<FacilityRequestItem>,
        someoneInfo: CustomerInfo
    ) {
        if (facilityRequestItems.size > 0) {
            val proxyRequest = ProxyRequest(
                ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE),
                FacilityRequest(
                    cartId = cartId,
                    items = facilityRequestItems,
                    isSomeoneElse = true,
                    fullName = someoneInfo.mFullName ?: "",
                    mobileNumber = someoneInfo.mMobile ?: "",
                    email = someoneInfo.mEmail ?: ""
                ),
                ProxyRequest.POST_METHOD,
                "",
                API.uriQuickBookFacility
            )
            interactor?.quickBookFacility(
                view?.getToken() ?: "",
                view?.getPayer()?.mCustomerId ?: "",
                proxyRequest,
                quickBookFacilityOutput
            )
        }
    }

    private fun getResultAddCourseToCart(
        courseRequestItems: ArrayList<ProductRequestItem>
    ): IBaseContract.IBaseInteractorOutput<BookingResponse> {
        return object : IBaseContract.IBaseInteractorOutput<BookingResponse> {
            @ExcludeFromJacocoGeneratedReport
            override fun onSuccess(data: BookingResponse?) {
                if (!data?.cartId.isNullOrEmpty()) {
                    view?.updateCartId(data)
                    addParticipant(courseRequestItems, 0)
                } else {
                    view?.dismissLoading()
                    view?.onBookingFailed()
                }
            }

            @ExcludeFromJacocoGeneratedReport
            override fun onError(data: BaseResponse<BookingResponse>) {
                view?.dismissLoading()
                view?.onBookingFailed()
            }

        }
    }

    private fun getResultAddEventToCart(
        eventRequestItems: ArrayList<ProductRequestItem>
    ): IBaseContract.IBaseInteractorOutput<BookingResponse> {
        return object : IBaseContract.IBaseInteractorOutput<BookingResponse> {
            @ExcludeFromJacocoGeneratedReport
            override fun onSuccess(data: BookingResponse?) {
                if (!data?.cartId.isNullOrEmpty()) {
                    view?.updateCartId(data)
                    //call api load cart to get externalId -> call add participant event
                    val token = view?.getToken() ?: ""
                    val userId = view?.getPayer()?.mCustomerId ?: ""
                    val cartId = view?.getCartId() ?: ""
                    loadCartEventToGetExternalLineId(token, userId, cartId, eventRequestItems)
                } else {
                    view?.dismissLoading()
                    view?.onBookingFailed()
                }
            }

            @ExcludeFromJacocoGeneratedReport
            override fun onError(data: BaseResponse<BookingResponse>) {
                view?.dismissLoading()
                view?.onBookingFailed()
            }

        }
    }

    private fun getCallbackAddIgToCart(
        igRequestItems: ArrayList<ProductRequestItem>
    ): IBaseContract.IBaseInteractorOutput<BookingResponse> {
        return object : IBaseContract.IBaseInteractorOutput<BookingResponse> {
            @ExcludeFromJacocoGeneratedReport
            override fun onSuccess(data: BookingResponse?) {
                if (!data?.cartId.isNullOrEmpty()) {
                    view?.updateCartId(data)
                    addParticipantIg(igRequestItems, 0)
                } else {
                    view?.dismissLoading()
                    view?.onBookingFailed()
                }
            }

            @ExcludeFromJacocoGeneratedReport
            override fun onError(data: BaseResponse<BookingResponse>) {
                view?.dismissLoading()
                view?.onBookingFailed()
            }

        }
    }
    private val courseRequestItems = ArrayList<ProductRequestItem>()
    private val igRequestItems = ArrayList<ProductRequestItem>()
    private val facilityRequestItems = ArrayList<FacilityRequestItem>()
    private val eventRequestItems = ArrayList<ProductRequestItem>()
    fun bookingProcess(cartItems: ArrayList<CartItem>?, someoneInfo: CustomerInfo? = null) {
        courseRequestItems.clear()
        igRequestItems.clear()
        facilityRequestItems.clear()
        eventRequestItems.clear()

        cartItems?.forEach { cartItem ->
            if (cartItem.classInfo != null) {
                val courseRequestItem = ProductRequestItem(
                    cartItem.classInfo!!.sku,
                    (cartItem.attendees?.size ?: 1).toString()
                )
                courseRequestItems.add(courseRequestItem)
            } else if (cartItem.igInfo != null) {
                val item = ProductRequestItem(
                    itemId = cartItem.igInfo!!.sku,
                    qty = (cartItem.attendees?.size ?: 1).toString()
                )
                igRequestItems.add(item)
            } else if (cartItem.facility != null) {
                cartItem.slotList?.forEach { slot ->
                    val facilityRequestItem = FacilityRequestItem(
                        cartItem.facility!!.crmResourceId,
                        GeneralUtils.formatDateToNumber(cartItem.selectedDate),
                        slot.mTimeRageName,
                        slot.mTimeRangeId
                    )
                    facilityRequestItems.add(facilityRequestItem)
                }
            } else if (cartItem.event != null) {
                val tickets: ArrayList<ProductRequestItemTicket> = ArrayList()
                cartItem.event?.listSelectedTicket?.forEach { eventTicket ->
                    val itemTicket = ProductRequestItemTicket().apply {
                        ticketId = eventTicket.id ?: ""
                        qty = (eventTicket.selectedQty ?: 0).toString()
                    }
                    tickets.add(itemTicket)
                }

                val eventRequestItem = ProductRequestItem(
                    itemId = cartItem.event?.sku ?: "",
                    tickets = tickets
                )
                eventRequestItems.add(eventRequestItem)
            }
        }


        if (view?.isQuickBook() == true){
            if (courseRequestItems.size > 0) {
                val productRequest = ProductRequest(view?.getCartId(), courseRequestItems)
                val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
                val proxyRequest = ProxyRequest(
                    header,
                    productRequest,
                    ProxyRequest.POST_METHOD,
                    "",
                    API.uriQuickBookCourse
                )
                interactor?.quickBookCourse(
                    view!!.getToken() ?: "",
                    view!!.getPayer()?.mCustomerId ?: "",
                    proxyRequest,
                    getResultAddCourseToCart(courseRequestItems)
                )
            }else if (igRequestItems.size > 0) {
                val productRequest = ProductRequest(view?.getCartId(), igRequestItems)
                val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
                val proxyRequest = ProxyRequest(
                    header,
                    productRequest,
                    ProxyRequest.POST_METHOD,
                    "",
                    API.uriQuickBookIg
                )
                interactor?.quickBookIg(
                    view!!.getToken() ?: "",
                    view!!.getPayer()?.mCustomerId ?: "",
                    proxyRequest,
                    getCallbackAddIgToCart(igRequestItems)
                )

            }else if (eventRequestItems.size > 0) {
                val productRequest = ProductRequest(view?.getCartId(), eventRequestItems)
                val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
                val proxyRequest = ProxyRequest(
                    header,
                    productRequest,
                    ProxyRequest.POST_METHOD,
                    "",
                    API.uriQuickBookEvent
                )
                interactor?.quickBookEvent(
                    view!!.getToken() ?: "",
                    view!!.getPayer()?.mCustomerId ?: "",
                    proxyRequest,
                    getResultAddEventToCart(eventRequestItems)
                )
            }else {
                if (someoneInfo == null) {
                    quickBookFacility(view?.getCartId(), facilityRequestItems)
                } else {
                    quickBookFacilitySomeone(view?.getCartId(), facilityRequestItems, someoneInfo)
                }
            }
        }else { //add to cart
            addToCartProcess()
        }


    }
    private fun needToCallAddToCartProcess(): Boolean{
        return courseRequestItems.size > 0 || igRequestItems.size >0 || eventRequestItems.size > 0
    }
    private fun addToCartProcess(){
        if (courseRequestItems.size > 0) {
            val productRequest = ProductRequest(view?.getCartId(), courseRequestItems)
            val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
            val proxyRequest = ProxyRequest(
                header,
                productRequest,
                ProxyRequest.POST_METHOD,
                "",
                API.uriAddCourseToCart
            )
            interactor?.addCourseToCart(
                view?.getToken() ?: "",
                view?.getPayer()?.mCustomerId ?: "",
                proxyRequest,
                getResultAddCourseToCart(courseRequestItems)
            )
        } else if (igRequestItems.size > 0) {
            val productRequest = ProductRequest(view?.getCartId(), igRequestItems)
            val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
            val proxyRequest = ProxyRequest(
                header,
                productRequest,
                ProxyRequest.POST_METHOD,
                "",
                API.uriAddIgToCart
            )
            interactor?.addIgCart(
                view?.getToken() ?: "",
                view?.getPayer()?.mCustomerId ?: "",
                proxyRequest,
                getCallbackAddIgToCart(igRequestItems)
            )
        } else if (eventRequestItems.size > 0) {
            val productRequest = ProductRequest(view?.getCartId(), eventRequestItems)
            val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
            val proxyRequest = ProxyRequest(
                header,
                productRequest,
                ProxyRequest.POST_METHOD,
                "",
                API.uriAddEventToCart
            )
            interactor?.addIgCart(
                view?.getToken() ?: "",
                view?.getPayer()?.mCustomerId ?: "",
                proxyRequest,
                getResultAddEventToCart(eventRequestItems)
            )
        }
    }

    override fun submitPayment(
        token: String?,
        kioskId: Int?,
        customerInfo: CustomerInfo?,
        isSubmitPayment: Boolean
    ) {
        val createdAt = System.currentTimeMillis() / 1000
        paymentRequest = PaymentRequest(kioskId, createdAt, customerInfo?.mCustomerId, "")
        if (!isSubmitPayment) {
            updatePaymentRequest(sessionCode)
            return
        }
        view?.showLoading()
        interactor?.submitPayment(token, paymentRequest)
    }

    override fun updatePayment(token: String?, signatureImage: String?, pdfFile: String?) {
        paymentRequest?.signatureImage = signatureImage
        paymentRequest?.pdfFile = pdfFile
        if (paymentRequest != null && signatureImage != null) {
            updateSignatureAndPdfFile(paymentRequest!!.txnNo, pdfFile, signatureImage)
        }
        interactor?.updatePayment(token, paymentRequest)
    }

    override fun updatePayment(
        totalCost: TotalCostsResponse?, cartItems: List<CartItem>?,
        txnResponse: TransactionResponse?, rawData: String?,
        selectedPaymentType: Int?, responseCode: String?, referenceId: String?, payer: CustomerInfo?
    ) {
        val items: MutableList<PaymentItem> = ArrayList()
        val paymentAmount = Math.round((totalCost?.getTotalPaymentAmount() ?: 0f) * 100)
        val beforeDiscountAmount = Math.round(
            (totalCost?.getTotalBeforeDiscountAmount()
                ?: 0f) * 100
        )
        val discountAmount = Math.round((totalCost?.getTotalDiscountAmount() ?: 0f) * 100)
        val adjustmentAmount = Math.round((totalCost?.getTotalAdjustmentAmount() ?: 0f) * 100)
        val promoDiscountAmount = Math.round((totalCost?.promoDiscountAmount ?: 0f) * 100)
        val gst = Math.round((totalCost?.getTotalGST()?.toFloat() ?: 0f))
        if (totalCost?.getProducts() != null) {
            for (product in totalCost.getProducts()!!) {
                val resourceId = product.getProductCode()
                val resourceName = product.getProductTitle()
                val resourceType = ""
                val resourceTypeName = ""
                val resourceDateTime = product.getProductDateTime()

                if (product.productType.equals(Product.PRODUCT_EVENT)){
                    product.listSelectedTicket.forEach { eventTicket ->
                        eventTicket.listTicketParticipantEntity.forEach { ticketEntity ->
                            ticketEntity.listParticipant.forEach { participant ->
                                val beforeDiscount =
                                    ((participant.beforeDiscountAmount ?: 0f) * 100).roundToInt()
                                val discount = ((participant.discountAmount
                                    ?: 0f) * 100).roundToInt() + ((participant.promoDiscountAmount ?: 0f) * 100).roundToInt()
                                val payment = ((participant.paymentAmount ?: 0f) * 100).roundToInt()
                                val item = PaymentItem(
                                    resourceId,
                                    resourceName,
                                    resourceType,
                                    resourceTypeName,
                                    beforeDiscount,
                                    discount,
                                    payment,
                                    resourceDateTime
                                )
                                item.txnNo = sessionCode
                                val cartProduct = getProductById(product.productId, Product.PRODUCT_EVENT) as EventInfo
                                item.customerAge = getCustomerAge(product.getCustomerId())
                                item.resourceType = PaymentItem.TYPE_EVENT
                                item.outletId = cartProduct.outletID
                                item.outletName = cartProduct.outletName
                                item.outletTypeName = cartProduct.outletTypeName
                                items.add(item)
                            }
                        }
                    }
                }else {
                    val beforeDiscount = Math.round((product.getBeforeDiscountAmount() ?: 0f) * 100)
                    val discount = Math.round(
                        (product.getDiscountAmount() ?: 0f) * 100
                    ) + Math.round((product.promoDiscountAmount ?: 0f) * 100)
                    val payment = Math.round((product.getPaymentAmount() ?: 0f) * 100)
                    val item = PaymentItem(
                        resourceId,
                        resourceName,
                        resourceType,
                        resourceTypeName,
                        beforeDiscount,
                        discount,
                        payment,
                        resourceDateTime
                    )
                    item.txnNo = sessionCode
                    val cartProduct = getProductById(product.productId, product.productType)
                    item.customerAge = getCustomerAge(product.getCustomerId())
                    if (cartProduct is ClassInfo) {
                        item.resourceType = PaymentItem.TYPE_CLASS
                        item.outletId = cartProduct.getOutletId()
                        item.outletName = cartProduct.getOutletName()
                        item.outletTypeName = cartProduct.outletTypeName
                    } else if (cartProduct is InterestGroup) {
                        item.resourceType = PaymentItem.TYPE_INTEREST_GROUP
                        item.outletId = cartProduct.outletId
                        item.outletName = cartProduct.outletName
                        item.outletTypeName = cartProduct.outletTypeName
                    } else if (cartProduct is Facility) {
                        item.resourceType = PaymentItem.TYPE_FACILITY
                        item.outletId = cartProduct.outletId
                        item.outletName = cartProduct.outletName
                        item.outletTypeName = cartProduct.outletTypeName
                    } else if (cartProduct is EventInfo) {
                        item.resourceType = PaymentItem.TYPE_EVENT
                        item.outletId = cartProduct.outletID
                        item.outletName = cartProduct.outletName
                        item.outletTypeName = cartProduct.outletTypeName
                    }
                    items.add(item)
                }


            }
        }

        paymentRequest?.outletId = getOutletId()
        paymentRequest?.cartId = totalCost?.getShoppingCartID()
        paymentRequest?.email = payer?.mEmail

        paymentRequest?.rawData = rawData
        paymentRequest?.items = items
        paymentRequest?.beforeDiscountAmount = beforeDiscountAmount
        paymentRequest?.discountAmount = discountAmount + promoDiscountAmount
        paymentRequest?.adjustmentAmount = adjustmentAmount
        paymentRequest?.paymentAmount = paymentAmount
        paymentRequest?.gst = gst.toDouble()

        paymentRequest?.rrn = txnResponse?.rrn
        paymentRequest?.tid = txnResponse?.tid
        paymentRequest?.mid = txnResponse?.mid
        paymentRequest?.approvalCode = txnResponse?.approvalCode
        paymentRequest?.errorCode = txnResponse?.errorCode
        paymentRequest?.issuerName = txnResponse?.issuerName
        paymentRequest?.paymentDate = txnResponse?.dateTime
        paymentRequest?.pan = txnResponse?.panNumber
        paymentRequest?.invoiceNo = txnResponse?.invoiceNo
        paymentRequest?.batch = txnResponse?.batchNo
        paymentRequest?.stan = txnResponse?.stanNo
        paymentRequest?.aid = txnResponse?.aid
        paymentRequest?.cardLabel = txnResponse?.cardLabel
        paymentRequest?.signatureRequired = txnResponse?.signatureRequired
        if (WireCardMessage.SUCCESS_RESPONSE_CODE.equals(responseCode)) {
            paymentRequest?.paymentStatus = PaymentRequest.STATUS_PAID
            paymentRequest?.paymentTypeCode = txnResponse?.paymentType
            paymentRequest?.rrn = getRefNumber(txnResponse)
            val paymentMethod: Int
            if (TransactionResponse.CREDIT_PAYMENT_TYPE == txnResponse?.paymentType ||
                Integer.toHexString(FieldData.TxnType.PAYMENT_BY_SCHEME_CREDIT.toInt()) == txnResponse?.paymentType
            ) {
                paymentMethod = ReceiptRequest.DEBIT_CREDIT_METHOD
            } else if (TransactionResponse.CEPAS_PAYMENT_TYPE == txnResponse?.paymentType ||
                Integer.toHexString(FieldData.TxnType.PAYMENT_BY_EZL.toInt())
                    .toString() == txnResponse?.paymentType
            ) {
                paymentMethod = ReceiptRequest.EZ_LINK_METHOD
            } else {
                paymentMethod = ReceiptRequest.NETS_METHOD
            }
            paymentRequest?.paymentCode = paymentMethod
        } else {
            paymentRequest?.paymentStatus = PaymentRequest.STATUS_UNSUCCESSFUL
            var paymentTypeCodeStatus: Int? = null
            when (selectedPaymentType) {
                PaymentFragment.CARD_CEPAS -> paymentTypeCodeStatus =
                    FieldData.TxnType.PAYMENT_BY_EZL.toInt()
                PaymentFragment.CARD_CREDIT_CONTACTLESS -> paymentTypeCodeStatus =
                    FieldData.TxnType.PAYMENT_BY_SCHEME_CREDIT.toInt()
                PaymentFragment.CARD_NETS_FLASH -> paymentTypeCodeStatus =
                    FieldData.TxnType.PAYMENT_BY_NETS_NFP.toInt()
                else -> paymentTypeCodeStatus = FieldData.TxnType.PAYMENT_BY_NETS_EFT.toInt()
            }
            paymentRequest?.paymentTypeCode =
                com.styl.pa.modules.peripheralsManager.GeneralUtils.convertIntToHexString(
                    paymentTypeCodeStatus,
                    2
                )
        }
        paymentRequest?.completedAt = System.currentTimeMillis() / 1000
        paymentRequest?.referenceId = referenceId

        updateRecord(paymentRequest)
    }

    override fun updateFreePayment(totalCost: TotalCostsResponse?, payer: CustomerInfo?) {
        val items: MutableList<PaymentItem> = ArrayList()
        val paymentAmount = Math.round((totalCost?.getTotalPaymentAmount() ?: 0f) * 100)
        val beforeDiscountAmount = Math.round(
            (totalCost?.getTotalBeforeDiscountAmount()
                ?: 0f) * 100
        )
        val discountAmount = Math.round((totalCost?.getTotalDiscountAmount() ?: 0f) * 100)
        val adjustmentAmount = Math.round((totalCost?.getTotalAdjustmentAmount() ?: 0f) * 100)
        val promoDiscountAmount = Math.round(totalCost?.promoDiscountAmount ?: 0f) * 100
        val gst = Math.round((totalCost?.getTotalGST()?.toFloat() ?: 0f))
        if (totalCost?.getProducts() != null) {
            for (product in totalCost.getProducts()!!) {
                val resourceId = product.getProductCode()
                val resourceName = product.getProductTitle()
                val resourceType = ""
                val resourceTypeName = ""
                val resourceDateTime = product.getProductDateTime()

                if (product.productType.equals(Product.PRODUCT_EVENT)) {
                    product.listSelectedTicket.forEach { eventTicket ->
                        eventTicket.listTicketParticipantEntity.forEach { ticketEntity ->
                            ticketEntity.listParticipant.forEach { participant ->
                                val beforeDiscount =
                                    ((participant.beforeDiscountAmount ?: 0f) * 100).roundToInt()
                                val discount = ((participant.discountAmount
                                    ?: 0f) * 100).roundToInt() + ((participant.promoDiscountAmount ?: 0f) * 100).roundToInt()
                                val payment = ((participant.paymentAmount ?: 0f) * 100).roundToInt()
                                val item = PaymentItem(
                                    resourceId,
                                    resourceName,
                                    resourceType,
                                    resourceTypeName,
                                    beforeDiscount,
                                    discount,
                                    payment,
                                    resourceDateTime
                                )
                                item.txnNo = sessionCode
                                val cartProduct = getProductById(product.productId, Product.PRODUCT_EVENT) as EventInfo
                                item.customerAge = getCustomerAge(product.getCustomerId())
                                item.resourceType = PaymentItem.TYPE_EVENT
                                item.outletId = cartProduct.outletID
                                item.outletName = cartProduct.outletName
                                item.outletTypeName = cartProduct.outletTypeName
                                items.add(item)
                            }
                        }
                    }
                } else {
                    val beforeDiscount = Math.round((product.getBeforeDiscountAmount() ?: 0f) * 100)
                    val discount = Math.round(
                        (product.getDiscountAmount() ?: 0f) * 100
                    ) + Math.round((product.promoDiscountAmount ?: 0f) * 100)
                    val payment = Math.round((product.getPaymentAmount() ?: 0f) * 100)
                    val item = PaymentItem(
                        resourceId,
                        resourceName,
                        resourceType,
                        resourceTypeName,
                        beforeDiscount,
                        discount,
                        payment,
                        resourceDateTime
                    )
                    item.txnNo = sessionCode
                    val cartProduct = getProductById(product.productId, product.productType)
                    item.customerAge = getCustomerAge(product.getCustomerId())
                    if (cartProduct is ClassInfo) {
                        item.resourceType = PaymentItem.TYPE_CLASS
                        item.outletId = cartProduct.getOutletId()
                        item.outletName = cartProduct.getOutletName()
                        item.outletTypeName = cartProduct.outletTypeName
                    } else if (cartProduct is InterestGroup) {
                        item.resourceType = PaymentItem.TYPE_INTEREST_GROUP
                        item.outletId = cartProduct.outletId
                        item.outletName = cartProduct.outletName
                        item.outletTypeName = cartProduct.outletTypeName
                    } else if (cartProduct is Facility) {
                        item.resourceType = PaymentItem.TYPE_FACILITY
                        item.outletId = cartProduct.outletId
                        item.outletName = cartProduct.outletName
                        item.outletTypeName = cartProduct.outletTypeName
                    } else if (cartProduct is EventInfo) {
                        item.resourceType = PaymentItem.TYPE_EVENT
                        item.outletId = cartProduct.outletID
                        item.outletName = cartProduct.outletName
                        item.outletTypeName = cartProduct.outletTypeName
                    }
                    items.add(item)
                }

            }
        }

        paymentRequest?.outletId = getOutletId()
        paymentRequest?.cartId = totalCost?.getShoppingCartID()
        paymentRequest?.email = payer?.mEmail
        paymentRequest?.paymentCode = ReceiptRequest.DEBIT_CREDIT_METHOD

        paymentRequest?.rrn = ""
        paymentRequest?.tid = ""
        paymentRequest?.mid = ""
        paymentRequest?.approvalCode = ""
        paymentRequest?.errorCode = ""
        paymentRequest?.issuerName = ""
        paymentRequest?.beforeDiscountAmount = beforeDiscountAmount
        paymentRequest?.discountAmount = discountAmount + promoDiscountAmount
        paymentRequest?.adjustmentAmount = adjustmentAmount
        paymentRequest?.paymentAmount = paymentAmount
        paymentRequest?.gst = gst.toDouble()
        paymentRequest?.paymentTypeCode = ""
        paymentRequest?.paymentDate = ""
        paymentRequest?.pan = ""
        paymentRequest?.invoiceNo = ""
        paymentRequest?.batch = ""
        paymentRequest?.stan = ""
        paymentRequest?.aid = ""
        paymentRequest?.cardLabel = ""
        paymentRequest?.signatureRequired = false
        paymentRequest?.rawData = ""
        paymentRequest?.items = items
        paymentRequest?.paymentStatus = PaymentRequest.STATUS_PAID

        updateRecord(paymentRequest)
    }

    private fun getProductById(productId: String?, productType: String?): Any? {
        val cart = view?.getCart()
        cart?.items?.forEach { item ->
            if (productType.equals(Product.PRODUCT_EVENT)){
                if (item.event?.sku.equals(productId)) {
                    return item.event
                }
            } else {
                if (item.classInfo?.sku.equals(productId)) {
                    return item.classInfo
                } else if (item.igInfo?.sku.equals(productId)) {
                    return item.igInfo
                } else if (item.facility != null) { // have only 1 facility in cart so just return it
                    return item.facility
                }
            }
        }
        return null
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getCustomerAge(customerId: String?): String? {
        val cart = view?.getCart()
        var birthday = cart?.payer?.dob
        if (!customerId.isNullOrEmpty()) {
            cart?.items?.forEach item@{ item ->
                item.attendees?.forEach attendee@{ attendee ->
                    if (attendee.customerInfo?.mCustomerId.equals(customerId)) {
                        birthday = attendee.customerInfo!!.dob
                        return@item
                    }
                }
            }
        }
        return GeneralUtils.convertDateToAge(birthday)
    }

    private fun getCartOutput(
        courseRequestItems: ArrayList<ProductRequestItem>? = null,
        isCheckCart: Boolean = false,
        isLoadCartForGetExternalLineId: Boolean = false
    ): IBaseContract.IBaseInteractorOutput<Data<CartData>> {
        return object : IBaseContract.IBaseInteractorOutput<Data<CartData>> {
            @ExcludeFromJacocoGeneratedReport
            override fun onSuccess(data: Data<CartData>?) {
                handleCartError(
                    data,
                    courseRequestItems,
                    isCheckCart,
                    isLoadCartForGetExternalLineId
                )
            }

            @ExcludeFromJacocoGeneratedReport
            override fun onError(data: BaseResponse<Data<CartData>>) {
                errMsg.clear()
                view?.dismissLoading()
                view?.onBookingFailed()
            }
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun addParticipant(courseRequestItems: ArrayList<ProductRequestItem>, index: Int) {
        val cart = view?.getCart()
        totalAddParticipantRequest = courseRequestItems.size
        val item = courseRequestItems[index]
        val cartItem = cart?.items?.find { cartItem ->
            cartItem.classInfo?.sku == item.itemId
        }
        val attendees = cartItem?.attendees
        val participantItems = ArrayList<ParticipantItem>()
        attendees?.forEach { attendee ->
            val customerInfo = attendee.customerInfo
            val participantItem = ParticipantItem(
                customerInfo?.mFullName,
                customerInfo?.mEmail,
                customerInfo?.mMobile,
                customerInfo?.mIdNo,
                IdType.getIdType(customerInfo?.mIdType),
                Nationality.getNationality(customerInfo?.mNationality),
                RaceType.getRaceType(customerInfo?.mRace),
                Gender.getGender(customerInfo?.mGender),
                GeneralUtils.formatDateTimeToDate(customerInfo?.mDoB)
            )
            participantItems.add(participantItem)
        }
        val proxyRequest = ProxyRequest(
            ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE),
            ParticipantRequest(view?.getCartId(), item.itemId, participantItems),
            ProxyRequest.POST_METHOD,
            "",
            API.uriAddParticipant
        )
        interactor?.addParticipant(
            view?.getToken() ?: "",
            view?.getPayer()?.mCustomerId ?: "",
            proxyRequest,
            getAddParticipantOutput(courseRequestItems, index)
        )
    }

    @ExcludeFromJacocoGeneratedReport
    fun addEventParticipant(eventRequestItems: ArrayList<ProductRequestItem>, index: Int) {
        val cart = view?.getCart()
        totalAddParticipantRequest = eventRequestItems.size
        val listParticipants = ArrayList<EventParticipantItem>()
        val addEventParticipantRequest = AddEventParticipantRequest()
        addEventParticipantRequest.cartId = view?.getCartId()

        view?.showLoading()
        eventRequestItems.forEach { item ->
            //        val item = eventRequestItems[index]
            currentParticipantProgress++
            val cartItem = cart?.items?.find { cartItem ->
                cartItem.event?.sku == item.itemId
            }
            if (cartItem?.event != null) {
                LogManager.d("CheckoutPresenter: AddParticipant: event - ${cartItem.event?.eventCode} = ${cartItem.event?.getDecodedTitle()}")
                cartItem.event?.listSelectedTicket?.forEach { eventTicket ->
                    eventTicket.listTicketParticipantEntity.forEach { ticketEntity ->
                        ticketEntity.listParticipant.forEach { eventParticipant ->
                            if (eventParticipant.participantInfo != null) {
                                val eventParticipantItem = EventParticipantItem()
                                eventParticipantItem.itemName = cartItem.event?.sku ?: ""
                                LogManager.d("CheckoutPresenter: AddParticipant: eventParticipant.externalLineId = ${eventParticipant.externalLineId}")
                                LogManager.d("CheckoutPresenter: AddParticipant: ticketEntity.externalLineId = ${ticketEntity.externalLineId}")
                                eventParticipantItem.externalLineId = eventParticipant.externalLineId ?: ticketEntity.externalLineId
                                LogManager.d("CheckoutPresenter: AddParticipant: eventParticipant.userId = ${eventParticipant.userId}")
                                eventParticipantItem.userId = eventParticipant.userId
                                LogManager.d("CheckoutPresenter: AddParticipant: eventParticipant.componentId = ${eventParticipant.componentId}")
                                eventParticipantItem.componentId = eventParticipant.componentId

                                val map: HashMap<String, String> = HashMap()
                                if ((eventParticipant.participantInfo?.listNotDefined?.size ?: 0) > 0){
                                    for (obj in eventParticipant.participantInfo?.listNotDefined!!){
                                        map[obj.itemName ?: ""] = obj.inputValue ?: ""
                                    }
                                }
                                map["lastname"] = "."

                                eventParticipantItem.formData = map
                                listParticipants.add(eventParticipantItem)
                            }
                        }
                    }
                }
            }
        }

        addEventParticipantRequest.participants = listParticipants
        val proxyRequest = ProxyRequest(
            ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE),
            addEventParticipantRequest,
            ProxyRequest.POST_METHOD,
            "",
            API.uriAddEventParticipant
        )
        interactor?.addEventParticipant(
            view?.getToken() ?: "",
            view?.getPayer()?.mCustomerId ?: "",
            proxyRequest,
            getAddEventParticipantOutput(eventRequestItems, index)
        )
    }

    @ExcludeFromJacocoGeneratedReport
    private fun addParticipantIg(igRequestItems: ArrayList<ProductRequestItem>, index: Int) {
        val cart = view?.getCart()
        totalAddParticipantRequest = igRequestItems.size
        val item = igRequestItems[index]
        val cartItem = cart?.items?.find { cartItem ->
            cartItem.igInfo?.sku == item.itemId
        }
        val attendees = cartItem?.attendees
        val participantItems = ArrayList<ParticipantItem>()
        attendees?.forEach { attendee ->
            val customerInfo = attendee.customerInfo
            val participantItem = ParticipantItem(
                customerInfo?.mFullName,
                customerInfo?.mEmail,
                customerInfo?.mMobile,
                customerInfo?.mIdNo,
                IdType.getIdType(customerInfo?.mIdType),
                Nationality.getNationality(customerInfo?.mNationality),
                RaceType.getRaceType(customerInfo?.mRace),
                Gender.getGender(customerInfo?.mGender),
                GeneralUtils.formatDateTimeToDate(customerInfo?.mDoB)
            )
            participantItems.add(participantItem)
        }
        val proxyRequest = ProxyRequest(
            ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE),
            ParticipantRequest(view?.getCartId(), item.itemId, participantItems),
            ProxyRequest.POST_METHOD,
            "",
            API.uriAddParticipantIg
        )
        interactor?.addParticipant(
            view?.getToken() ?: "",
            view?.getPayer()?.mCustomerId ?: "",
            proxyRequest,
            getAddParticipantOutputIg(igRequestItems, index)
        )
    }

    @ExcludeFromJacocoGeneratedReport
    private fun deleteRedundantItems(
        data: Data<CartData>,
        courseRequestItems: ArrayList<ProductRequestItem>
    ) {
        val listItemNeedRemove: MutableList<ProductRequestItem> = ArrayList()
        val bookingCart = data.getEntity()?.cart
        bookingCart?.items?.forEach { cartItem ->
            val filterItem = courseRequestItems.find { courseRequestItem ->
                cartItem.itemId == courseRequestItem.itemId
            }
            if (filterItem == null) {
                listItemNeedRemove.add(ProductRequestItem(cartItem.itemId, null))
            }
        }
        if (listItemNeedRemove.isNotEmpty()) {
            val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
            val productRequest = ProductRequest(view?.getCartId(), listItemNeedRemove)
            val proxyRequestClearCart = ProxyRequest(
                header,
                productRequest,
                ProxyRequest.POST_METHOD,
                "",
                API.uriRemoveItemFromCart
            )
            val token = view?.getToken() ?: ""
            interactor?.removeItemFromCart(
                token, view?.getPayer()?.mCustomerId ?: "",
                proxyRequestClearCart, getOutputDeleteRedundantItem(courseRequestItems)
            )
        } else {
            addParticipant(courseRequestItems, 0)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun handleCartError(
        data: Data<CartData>?,
        courseRequestItems: ArrayList<ProductRequestItem>?,
        isCheckCart: Boolean,
        isLoadCartForGetExternalLineId: Boolean = false
    ) {
        if (data?.getHasError() == true && !isLoadCartForGetExternalLineId) {
            if (isCheckCart && courseRequestItems?.isNotEmpty() == true) {
                LogManager.d("handleCartError: deleteRedundantItems")
                deleteRedundantItems(data, courseRequestItems)
                return
            }
            view?.dismissLoading()
            StoreLogsUtils.logErrorMessage(data.getResponseStatusCode(), data.getErrorMessages())
            if (errMsg.isEmpty()) {
                val cart = data.getEntity()?.cart
                cart?.items?.forEach { item ->
                    if (item.productType == Product.PRODUCT_EVENT){
                        item.ticketGroups?.forEach { ticketGroup ->
                            ticketGroup.tickets?.forEach { ticket ->
                                ticket.booking?.forEach{ booking ->
                                    val itemName = item.title ?: ""
                                    val ticketName = ticketGroup.ticketName ?: ""
                                    val msg = String.format(
                                        "%s - %s: %s",
                                        itemName,
                                        ticketName,
                                        booking.reservationErrorMessageCRM ?: booking.errorMesssage ?: data.getErrorMessages()
                                    )
                                    errMsg.append(msg).append("\n")
                                }
                            }
                        }
                    } else {
                        item.booking?.forEach { booking ->
                            if (booking.isReservationSuccess == false && !booking.reservationErrorMessageCRM.isNullOrEmpty()) {
                                val itemName =
                                    if (item.productType == Product.PRODUCT_COURSE || item.productType == Product.PRODUCT_INTEREST_GROUP) {
                                        item.title
                                    } else {
                                        booking.facilityName
                                    }
                                val msg = String.format(
                                    "%s: %s",
                                    itemName,
                                    booking.reservationErrorMessageCRM
                                )
                                errMsg.append(msg).append("\n")
                            }
                        }
                    }

                }
            }
            if (errMsg.isEmpty()) {
                var cartErrMsg = data.getErrorMessages()
                if (cartErrMsg.isNullOrEmpty()) {
                    cartErrMsg = view?.getContext()?.getString(R.string.request_error, data.getResponseStatusCode())
                }
                errMsg.append(cartErrMsg).append("\n")
            }
            view?.showMessageAndBack(errMsg.toString())
        } else {
            view?.dismissLoading()
            view?.onLoadCartSuccess(data?.getEntity()?.cart, isLoadCartForGetExternalLineId, courseRequestItems)
        }
        errMsg.clear()
    }

    override fun loadCart(
        token: String, userId: String, cartId: String,
        courseRequestItems: ArrayList<ProductRequestItem>?, isCheckCart: Boolean
    ) {
        view?.showLoading()
        //loadCart always call with courseRequestItems = null?
        interactor?.loadCart(token, userId, cartId, getCartOutput(courseRequestItems, isCheckCart))
    }

    override fun loadCartEventToGetExternalLineId(
        token: String,
        userId: String,
        cartId: String,
        eventRequestItems: ArrayList<ProductRequestItem>?,
        isCheckCart: Boolean
    ) {
        view?.showLoading()
        interactor?.loadCart(
            token,
            userId,
            cartId,
            getCartOutput(eventRequestItems, isCheckCart, true)
        )
    }

    private fun getOutputDeleteRedundantItem(
        courseRequestItems: ArrayList<ProductRequestItem>
    ): IBaseContract.IBaseInteractorOutput<BookingResponse> {
        return object : IBaseContract.IBaseInteractorOutput<BookingResponse> {
            @ExcludeFromJacocoGeneratedReport
            override fun onSuccess(data: BookingResponse?) {
                addParticipant(courseRequestItems, 0)
            }

            @ExcludeFromJacocoGeneratedReport
            override fun onError(data: BaseResponse<BookingResponse>) {
                view?.dismissLoading()
                view?.onBookingFailed()
            }
        }
    }

    private fun getDeleteItemResult(index: Int): IBaseContract.IBaseInteractorOutput<BookingResponse> {
        return object : IBaseContract.IBaseInteractorOutput<BookingResponse> {
            @ExcludeFromJacocoGeneratedReport
            override fun onSuccess(data: BookingResponse?) {
                view?.dismissLoading()
                view?.onDeleteItemSuccess(index)
            }

            @ExcludeFromJacocoGeneratedReport
            override fun onError(data: BaseResponse<BookingResponse>) {
                view?.dismissLoading()
                view?.showErrorMessage(data)
            }

        }
    }

    fun deleteItem(cartItem: CartItem, cartId: String, index: Int) {
        val itemId: String? = cartItem.getSku()
        if (itemId != null) {
            val productItems: MutableList<ProductRequestItem> = ArrayList()
            productItems.add(ProductRequestItem(itemId, null))
            view?.showLoading()
            val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
            val productRequest = ProductRequest(cartId, productItems)
            if (cartItem.igInfo != null) {
                val proxyRequestClearCart = ProxyRequest(
                    header,
                    productRequest,
                    ProxyRequest.POST_METHOD,
                    "",
                    API.uriRemoveItemIgFromCart
                )
                val token = view?.getToken() ?: ""
                interactor?.removeItemIgFromCart(
                    token,
                    view?.getPayer()?.mCustomerId ?: "",
                    proxyRequestClearCart,
                    getDeleteItemResult(index)
                )
            } else if(cartItem.event != null){
                val proxyRequestRemoveItem = ProxyRequest(
                    header,
                    productRequest,
                    ProxyRequest.POST_METHOD,
                    "",
                    API.uriRemoveEventItemFromCart
                )
                val token = view?.getToken() ?: ""
                val userId = view?.getPayer()?.mCustomerId ?: ""
                interactor?.removeEventItemFromCart(
                    token = token,
                    userId = userId,
                    proxyRequestRemoveItem,
                    getDeleteItemResult(index)
                )
            }else {
                val proxyRequestClearCart = ProxyRequest(
                    header,
                    productRequest,
                    ProxyRequest.POST_METHOD,
                    "",
                    API.uriRemoveItemFromCart
                )
                val token = view?.getToken() ?: ""
                interactor?.removeItemFromCart(
                    token,
                    view?.getPayer()?.mCustomerId ?: "",
                    proxyRequestClearCart,
                    getDeleteItemResult(index)
                )
            }
        }
    }

    private val prepareCheckoutResult = object : IBaseContract.IBaseInteractorOutput<String> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: String?) {
            view?.dismissLoading()
            view?.onPrepareCheckoutSuccess()
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<String>) {
            view?.dismissLoading()
            view?.showErrorMessage(data)
        }

    }

    override fun prepareCheckout(
        token: String,
        cartId: String,
        payer: CustomerInfo,
        paymentRefId: String?
    ) {
        val checkoutRequest = CheckoutRequest(
            cartId,
            payer.mEmail,
            payer.mFullName,
            payer.mMobile,
            paymentRefId
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

    @ExcludeFromJacocoGeneratedReport
    private fun createRecord(item: PaymentRequest?) {
        if (item != null) {
            view?.insertLog(item)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun updateRecord(item: PaymentRequest?) {
        if (item != null) {
            view?.updateLog(item)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun updateStatus(txnNo: String, status: Int) {
        view?.updateStatus(txnNo, status)
    }

    @ExcludeFromJacocoGeneratedReport
    private fun updateSignatureAndPdfFile(txnNo: String, pdfFile: String?, signatureImage: String) {
        view?.updatePdfFileAndSignature(txnNo, pdfFile, signatureImage)
    }

    @ExcludeFromJacocoGeneratedReport
    private fun getOutletId(): String? {
        val kioskInfo =
            GeneralUtils.convertStringToObject<KioskInfo>(MySharedPref(view?.getContext()).kioskInfo)
        return kioskInfo?.outlet?.getOutletId()
    }

    @ExcludeFromJacocoGeneratedReport
    fun getRefNumber(txnResponse: TransactionResponse?): String? {
        if (!GeneralUtils.isSimulateTerminal()) {
            // due to RRN of NFP is not available, need to combine datetime + pan as NETS suggestion
            return if (com.styl.pa.modules.peripheralsManager.GeneralUtils.convertIntToHexString(
                    FieldData.TxnType.PAYMENT_BY_NETS_NFP.toInt(),
                    2
                ) == txnResponse?.paymentType
            )
                txnResponse.dateTime?.replace(" ", "") + txnResponse.panNumber
            else txnResponse?.rrn
        }

        /*Simulate for terminal response*/
        /*because rrn already simulated so just return it*/
        return txnResponse?.rrn
    }

    fun getResultApplyRemovePromo(isApply: Boolean): IBaseContract.IBaseInteractorOutput<Data<CartData>> {
        return object : IBaseContract.IBaseInteractorOutput<Data<CartData>> {
            @ExcludeFromJacocoGeneratedReport
            override fun onSuccess(data: Data<CartData>?) {
                view?.dismissLoading()
                if (data != null && data.getHasError() == false) {
                    view?.onApplyRemovePromoCodeSuccess(data.getEntity()?.cart, isApply)
                } else {
                    if (isApply) {
                        val errorValidation = data?.getEntity()?.errorValidation
                        val errorMsg = errorValidation?.message
                        view?.onApplyPromoCodeError(errorMsg)
                    } else {
                        view?.onRemovePromoCodeError(data?.getErrorMessages())
                    }
                }
            }

            @ExcludeFromJacocoGeneratedReport
            override fun onError(data: BaseResponse<Data<CartData>>) {
                view?.dismissLoading()
                view?.showErrorMessage(data)
            }
        }
    }


    var resultsGetAvailablePromoCode =
        object : IBaseContract.IBaseInteractorOutput<ListPromoCodeResponse> {
            @ExcludeFromJacocoGeneratedReport
            override fun onSuccess(data: ListPromoCodeResponse?) {
                view?.dismissLoading()
                view?.onGetAvailablePromoCodesSuccess(data)
            }

            @ExcludeFromJacocoGeneratedReport
            override fun onError(data: BaseResponse<ListPromoCodeResponse>) {
                view?.dismissLoading()
                view?.showErrorMessageAvailablePromoCode(data)
            }

        }


    override fun applyPromoCode(code: String) {
        view?.showLoading()
        val token = view?.getToken() ?: ""
        val proxyRequest = ProxyRequest(
            ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE),
            PromoCodeRequest(view?.getCartId(), code),
            ProxyRequest.POST_METHOD,
            "",
            API.uriApplyPromoCode
        )
        val customerInfo = view?.getPayer()
        val xUserId = customerInfo?.mCustomerId ?: ""
        interactor?.applyPromoCode(token, xUserId, proxyRequest, getResultApplyRemovePromo(true))
    }

    override fun removePromoCode() {
        view?.showLoading()
        val token = view?.getToken() ?: ""
        val cartId = view?.getCartId() ?: ""
        val proxyRequest = ProxyRequest(
            ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE),
            EmptyRequest(),
            ProxyRequest.GET_METHOD,
            "",
            API.getUriRemovePromo(cartId)
        )
        val customerInfo = view?.getPayer()
        val xUserId = customerInfo?.mCustomerId ?: ""
        interactor?.removePromoCode(token, xUserId, proxyRequest, getResultApplyRemovePromo(false))
    }

    override fun getAllAvailablePromoCode() {
        val availablePromoCodeRequest = AvailablePromoCodeRequest()
        val token = view?.getToken() ?: ""
        view?.showLoading()
        interactor?.getAllAvailablePromoCode(
            token,
            availablePromoCodeRequest,
            resultsGetAvailablePromoCode
        )
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationPromoCodeView(listPromoCodeResponse: ListPromoCodeResponse) {
        router?.navigationPromoCodeView(listPromoCodeResponse.promoCodeList!!)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigateToEmailAdditional() {
        router?.navigateToEmailAdditional()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigateToDeclaration(requestCode: Int, quickBookProductType: String?) {
        this.router?.navigateToDeclaration(requestCode, quickBookProductType)
    }
}