package com.styl.pa.modules.checkout.view


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.adapters.CartSummaryAdapter
import com.styl.pa.database.AppDatabase
import com.styl.pa.database.DbWorkerThread
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.cart.*
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.event.EventParticipant
import com.styl.pa.entities.event.TicketEntity
import com.styl.pa.entities.generateToken.BookingResponse
import com.styl.pa.entities.kioskactivation.KioskInfo
import com.styl.pa.entities.pacesRequest.EventTicket
import com.styl.pa.entities.pacesRequest.ProductRequestItem
import com.styl.pa.entities.participant.ParticipantResponse
import com.styl.pa.entities.payment.PaymentRequest
import com.styl.pa.entities.product.Product
import com.styl.pa.entities.promocode.ListPromoCodeResponse
import com.styl.pa.entities.reservation.*
import com.styl.pa.entities.wirecard.TransactionResponse
import com.styl.pa.interfaces.OnClickItem
import com.styl.pa.interfaces.OnDeleteRecyclerViewItem
import com.styl.pa.modules.base.CustomBaseFragment
import com.styl.pa.modules.checkout.ICheckoutContact
import com.styl.pa.modules.checkout.presenter.CheckoutPresenter
import com.styl.pa.modules.declaration.presenter.DeclarationPresenter
import com.styl.pa.modules.dialog.MessageDialogFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.payment.view.PaymentFragment
import com.styl.pa.modules.printer.IPrinterFontConfig
import com.styl.pa.modules.promo.view.PromoCodeFragment
import com.styl.pa.utils.*
import kotlinx.android.synthetic.main.fragment_checkout.*
import kotlinx.android.synthetic.main.fragment_checkout.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 *
 */
@SuppressLint("NotifyDataSetChanged")
class CheckoutFragment : CustomBaseFragment(), ICheckoutContact.IView, View.OnClickListener {

    private var mLastClickTime = 0L

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - MainActivity.globalLastClickTime < 1500)
            return
        MainActivity.globalLastClickTime = SystemClock.elapsedRealtime()

        when (v?.id) {
            R.id.rl_pay -> {
                LogManager.i("Pay button pressed")
                if (totalCost != null) {
                    /*if ((isQuickBookFacility || isQuickBookIg || isQuickBookEvent) && !isAllowCheckout) {
                        this.presenter?.navigateToDeclaration(
                            requestCode = DeclarationPresenter.DECLARATION_REQUEST_CODE,
                            quickBookProductType = quickBookProductType
                        )
                    } else {
                        clickPayEvent()
                    }*/
                    this.presenter?.navigateToDeclaration(
                        requestCode = DeclarationPresenter.DECLARATION_REQUEST_CODE,
                        quickBookProductType = quickBookProductType
                    )
                }
            }

            R.id.btn_back_action -> {
                LogManager.i("Back button pressed")
                if ((activity as MainActivity).isQuickBook) {
                    (activity as MainActivity).showBookingInProgress()
                    return
                }
                backCartView()
            }

            R.id.btn_continue_browsing -> {
                if ((activity as MainActivity).isQuickBook) {
                    (activity as MainActivity).showBookingInProgress()
                    return
                }
                fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }

            R.id.txt_available_promo_code -> {
                presenter?.getAllAvailablePromoCode()
            }

            R.id.btn_remove -> {
                presenter?.removePromoCode()
            }
            R.id.img_remove -> {
                if (isInvalidPromoCode) {
                    presenter?.removePromoCode()
                }
            }
        }
    }

    private fun clickPayEvent() {
        try {
            val pattern = "yyyy-MM-dd'T'HH:mm:ss"
            val dateFormat = SimpleDateFormat(pattern, Locale.ENGLISH)
            val expiryTime = dateFormat.parse(cartTotal?.expiry)
            val currentTime = System.currentTimeMillis()
            if (Date(currentTime).after(expiryTime)) {
                showErrorMessageAndDeleteCart(getString(R.string.session_timeout))
                return
            }
            if (cartTotal?.isValidCart == false) {
                showErrorMessageAndDeleteCart(getString(R.string.invalid_cart))
                return
            }
            if (payer?.mEmail.isNullOrEmpty()) {
                presenter?.navigateToEmailAdditional()
            } else {
                proceedToPayment()
            }
        } catch (e: Exception) {
            showErrorMessageAndDeleteCart(getString(R.string.common_error))
        }
    }

    companion object {

        private val TAG = CheckoutFragment::class.java.simpleName

        const val EXTRA_EMAIL_ADDRESS = BuildConfig.APPLICATION_ID + ".extras.EXTRA_EMAIL_ADDRESS"
        const val ARG_IS_BOOKING_MYSELF = BuildConfig.APPLICATION_ID + ".args.ARG_IS_BOOKING_MYSELF"
        const val ARG_FACILITY_PARTICIPANT =
            BuildConfig.APPLICATION_ID + ".args.ARG_FACILITY_PARTICIPANT"

        const val REQUEST_PAYMENT = 2
        private const val TIME_DELAY = 1200

        const val SELECT_PROMO_CODE_REQUEST_CODE = 3
        const val EMAIL_REQUEST_CODE = 4
    }

    private var paymentReferenceId: String? = null
    private var paymentState: String? = null

    private var printerFontConfig: IPrinterFontConfig? = null
    private var presenter: CheckoutPresenter? = CheckoutPresenter(this)
    private var cartInfo: Cart? = null
    private var getView: View? = null
    private var receipt: ReceiptRequest? = null
    private var cartTotal: CartInfo? = null

    private var fragmentChoosePayment: PaymentFragment? = null

    private var cartSummaryList = ArrayList<CartItem>()
    private var cartSummaryAdapter: CartSummaryAdapter? = null

    private var transactionResponse: TransactionResponse? = null
    private var bitmap: Bitmap? = null

    private var payer: CustomerInfo? = null

    private var appDb: AppDatabase? = null
    private lateinit var dbWorkerThread: DbWorkerThread
    private var isFreeCourses = false

    private var isQuickBookFacility = false
    private var isQuickBookIg = false
    private var isQuickBookEvent = false

    private var isInvalidPromoCode = false

    private var isBookingMyself = true
    private var facilityParticipant: CustomerInfo? = null
    private var isAllowCheckout = false
    private var quickBookProductType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbWorkerThread = DbWorkerThread("dbWorkThread")
        dbWorkerThread.start()

        appDb = AppDatabase.getInstance(activity as Context)

        cartInfo = getCartFromMain()
        payer = getPayerFromMain()
        printerFontConfig = (activity as MainActivity).getPrinterFontConfig()

    }

    private fun getCartFromMain(): Cart? {
        return (activity as MainActivity).getBookingCart()
    }

    private fun getPayerFromMain(): CustomerInfo? {
        val cart = getCartFromMain()?.copyCart() as Cart
        return cart.payer
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        getView = inflater.inflate(R.layout.fragment_checkout, container, false)

        this.getBundle()
        init()

        isQuickBookFacility =
            (activity as MainActivity).isQuickBook && cartInfo?.items?.get(0)?.facility != null

        isQuickBookIg =
            (activity as MainActivity).isQuickBook && cartInfo?.items?.get(0)?.igInfo != null

        isQuickBookEvent =
            (activity as MainActivity).isQuickBook && cartInfo?.items?.get(0)?.event != null

        val cart = getCartFromMain()?.copyCart() as Cart

        quickBookProductType = if (isQuickBookFacility) {
            Product.PRODUCT_FACILITY
        } else if (isQuickBookIg) {
            Product.PRODUCT_INTEREST_GROUP
        } else if (isQuickBookEvent) {
            Product.PRODUCT_EVENT
        } else {
            if (cart.items?.size == 1){
                if (cart.items?.get(0)?.event != null){
                    Product.PRODUCT_EVENT
                } else if (cart.items?.get(0)?.igInfo != null){
                    Product.PRODUCT_INTEREST_GROUP
                } else {
                    ""
                }
            } else {
                ""
            }
        }

        LogManager.d("CheckoutFragment: isQuickBookFacility = $isQuickBookFacility, isBookingMyself: $isBookingMyself, participant = ${facilityParticipant?.mFullName}")

        return getView
    }

    private fun getBundle() {
        isBookingMyself = arguments?.getBoolean(ARG_IS_BOOKING_MYSELF, true) ?: true
        facilityParticipant = arguments?.getParcelable(ARG_FACILITY_PARTICIPANT)
    }

    private fun init() {
        cartSummaryAdapter = CartSummaryAdapter(cartSummaryList, activity)
        cartSummaryAdapter?.setIsEdit(true)
        cartSummaryAdapter?.setDeleteCartItem(deleteCartItem)
        cartSummaryAdapter?.setDeleteAttendeeItem(deleteAttendeeItem)
        cartSummaryAdapter?.setIsBookingMyself(isBookingMyself)
        val participantInfo = if (isBookingMyself) {
            getPayerFromMain()
        } else {
            facilityParticipant
        }
        cartSummaryAdapter?.setFacilityParticipant(participantInfo)

        getView?.rcv_particulars_list?.layoutManager = LinearLayoutManager(activity)
        getView?.rcv_particulars_list?.adapter = cartSummaryAdapter

        isFreeCourses = false
        checkSessionCode()

        presenter?.navigationCheckoutVerificationView(null)

        getView?.txt_promo_error?.visibility = View.GONE
        getView?.ll_total_discount?.visibility = View.GONE
        getView?.view_line_2?.visibility = View.GONE

        getView?.rl_pay?.setOnClickListener(this)
        getView?.btn_back_action?.setOnClickListener(this)
        getView?.btn_continue_browsing?.setOnClickListener(this)
        getView?.btn_remove?.setOnClickListener(this)
        getView?.img_remove?.setOnClickListener(this)
        getView?.txt_available_promo_code?.setOnClickListener(this)

        getView?.isFocusableInTouchMode = true
        getView?.requestFocus()
        getView?.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                backCartView()
                return@OnKeyListener true
            }
            false
        })

        showCartSummaryBeforeCheckout()
    }

    fun setDeleteCart(isDelete: Boolean) {
        presenter?.isDelete = isDelete
    }

    fun backCartView() {
        presenter?.backCartView()
    }

    fun refreshRecyclerView() {
        initRecyclerViewHeight(false)
        getView?.rcv_particulars_list?.setHasFixedSize(true)
        getView?.rcv_particulars_list?.isNestedScrollingEnabled = false
    }

    private fun initRecyclerViewHeight(isFixed: Boolean) {
        val param = getView?.rcv_particulars_list?.layoutParams
        if (isFixed) {
            param?.height = resources.getDimensionPixelSize(R.dimen.dp_350)
        } else {
            param?.height = LinearLayout.LayoutParams.MATCH_PARENT
        }
        getView?.rcv_particulars_list?.layoutParams = param
    }

    private val deleteCartItem = object : OnDeleteRecyclerViewItem {
        override fun onClick(view: View, position: Int) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < TIME_DELAY)
                return
            mLastClickTime = SystemClock.elapsedRealtime()

            if (position >= 0 && position < cartSummaryList.size) {
                showDeleteConfirmDialog(position, CheckoutPresenter.CLEAR_ALL)
            }
        }
    }

    private val deleteAttendeeItem = object : OnClickItem.OnClickMultiIndex {
        override fun <T> onClick(view: View, param1: T, param2: T) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < TIME_DELAY)
                return
            mLastClickTime = SystemClock.elapsedRealtime()

            param1 as Int
            param2 as Int
            if (param1 >= 0 && param1 < cartSummaryList.size && param2 >= 0 && param2 < cartSummaryList[param1].attendees?.size ?: 0) {
                showDeleteConfirmDialog(param1, param2)
            }
        }
    }

    override fun getCart(): Cart? {
        return cartInfo
    }

    override fun getPayer(): CustomerInfo? {
        return payer
    }

    fun getCartSummaryList(): ArrayList<CartItem>? {
        return cartSummaryList
    }

    fun navigationRatingView() {
        presenter?.navigationRatingView(payer)
    }

    override fun addAttendeeIntoCart(attendee: Attendee, parentIndex: Int) {
        if (parentIndex >= 0 && parentIndex < cartSummaryList.size) {
            // update to cart in MainActivity
            if (cartSummaryList[parentIndex].attendees != null) {
                (cartSummaryList[parentIndex].attendees as ArrayList<Attendee>).add(attendee)
            } else {
                val attendees = ArrayList<Attendee>()
                attendees.add(attendee)
                cartSummaryList[parentIndex].attendees = attendees
            }

            cartSummaryAdapter?.notifyDataSetChanged()
        }
    }

    override fun reloadCart() {
        if (cartInfo?.items != null && cartInfo?.items!!.size > 0) {
            loadCart()
        } else {
            backCartView()
        }
    }

    override fun updateCartTableForCourse(
        request: ProgrammeBookingAuthRequest,
        data: BookingAuthResponse
    ) {
        val reservationId = data.mReservationID
        if (cartInfo != null && cartInfo?.items != null && !reservationId.isNullOrEmpty()) {
            val cartItems = cartInfo?.items!!
            for (i in 0..cartItems.size) {
                if (cartItems[i].classInfo != null && true == request.productid?.equals(cartItems[i].classInfo?.getClassId())) {
                    updateReservationIdForCard(request.mIdNo, cartItems[i], reservationId)
                    return
                }
            }
        }
    }

    override fun updateCartTableForFacility(
        request: FacilityBookingAuthRequest,
        data: BookingAuthResponse
    ) {
        val reservationId = data.mReservationID
        if (cartInfo != null && cartInfo?.items != null && !reservationId.isNullOrEmpty()) {
            val cartItems = cartInfo?.items!!
            for (i in 0..cartItems.size) {
                if (cartItems[i].facility != null && true == request.facilityid?.equals(cartItems[i].facility?.getResourceID())) {
                    if (!updateReservationIdForCard(
                            request.contactid,
                            cartItems[i],
                            reservationId
                        )
                    ) {
                        cartItems[i].attendees = arrayListOf(Attendee(reservationId, null, null))
                    }
                    return
                }
            }
        }
    }

    override fun updateCartTableForEvent(
        request: ProgrammeBookingAuthRequest,
        data: BookingAuthResponse
    ) {
        val reservationId = data.mReservationID
        if (cartInfo != null && cartInfo?.items != null && !reservationId.isNullOrEmpty()) {
            val cartItems = cartInfo?.items!!
            for (i in 0..cartItems.size) {
                if (cartItems[i].event != null && true == request.productid?.equals(cartItems[i].event?.eventId)) {
                    if (!updateReservationIdForCard(request.mIdNo, cartItems[i], reservationId)) {
                        cartItems[i].attendees = arrayListOf(Attendee(reservationId, null, null))
                    }
                    return
                }
            }
        }
    }

    private fun updateReservationIdForCard(
        idNo: String?,
        cartItem: CartItem,
        reservationId: String
    ): Boolean {
        val attendees = cartItem.attendees
        if (attendees != null) {
            for (j in 0 until attendees.size) {
                if (attendees[j].customerInfo != null && true == idNo.equals(attendees[j].customerInfo?.mCustomerId)) {
                    attendees[j].reservationId = reservationId

                    return true
                }
            }
        }

        return false
    }

    override fun clearReservationSuccess(
        parentIndex: Int,
        index: Int,
        callTotalCost: Boolean
    ) { //no usages found
        if (parentIndex == CheckoutPresenter.CLEAR_ALL && index == CheckoutPresenter.CLEAR_ALL) {
            deleteItemInCart(null, null)
            resetCart()
            cartSummaryList.clear()
            backCartView()
        } else if (parentIndex != CheckoutPresenter.CLEAR_ALL && index == CheckoutPresenter.CLEAR_ALL) {
            if (parentIndex >= 0 && parentIndex < cartSummaryList.size) {
                deleteItemInCart(cartSummaryList[parentIndex], null)
                cartSummaryList.removeAt(parentIndex)
            }
            resetCart()

            if (cartSummaryList.size > 0) {
                isGetTotalCostAgain(callTotalCost)
            } else {
                backCartView()
            }
        } else if (parentIndex != CheckoutPresenter.CLEAR_ALL && index != CheckoutPresenter.CLEAR_ALL) {
            if (parentIndex >= 0 && parentIndex < cartSummaryList.size && index >= 0 && index < cartSummaryList[parentIndex].attendees?.size ?: 0) {
                deleteItemInCart(
                    cartSummaryList[parentIndex],
                    cartSummaryList[parentIndex].attendees!![index]
                )
                (cartSummaryList[parentIndex].attendees as ArrayList<Attendee>).removeAt(index)
            }

            resetCart()

            isGetTotalCostAgain(callTotalCost)
        }
        cartSummaryAdapter?.notifyDataSetChanged()
    }

    private fun isGetTotalCostAgain(callTotalCost: Boolean) {
        val isExist = isExistAttendee()
        if (isExist && callTotalCost) {
//            presenter?.getTotalCost(cartInfo?.payer)
        } else {
            if (!isExist) {
                resetTotalCostView()
            }
            refreshCartSummaryList()
            dismissLoading()
        }
    }

    private fun isExistAttendee(): Boolean {
        cartSummaryList.forEach { item ->
            if (item.attendees != null && item.attendees!!.size > 0 || item.facility != null ||
                item.event != null && item.event?.registerForMyself != true && item.event?.registerRequired != true
            ) {
                return true
            }
        }

        return false
    }

    private fun refreshCartSummaryList() {
        cartSummaryList.forEach { item ->
            if (requireAttendee(item)) {
                removeItemFromCart(item)
                cartSummaryList.remove(item)
            }
        }

        if (cartSummaryList.size >= 0) {
            backCartView()
        }
    }

    private fun deleteItemInCart(item: CartItem?, attendee: Attendee?) {
        if (item == null && attendee == null) {
            cartInfo?.items?.clear()
        } else if (item != null && attendee == null) {
            if (cartInfo?.items != null && cartInfo?.items!!.size > 0) {
                for (i in cartInfo?.items!!) {
                    if ((i.classInfo != null &&
                                true == i.classInfo?.getClassId()
                            ?.equals(item.classInfo?.getClassId())) ||
                        (i.igInfo != null &&
                                true == i.igInfo?.igId
                            ?.equals(item.igInfo?.igId)) ||
                        (i.facility != null &&
                                true == i.facility?.getResourceID()
                            ?.equals(item.facility?.getResourceID())) ||
                        (i.event != null &&
                                true == i.event?.eventId?.equals(item.event?.eventId))
                    ) {
                        cartInfo?.items?.remove(i)
                        break
                    }
                }
            }
        } else if (item != null && attendee != null &&
            cartInfo?.items != null && cartInfo?.items!!.size > 0
        ) {
            for (i in 0 until cartInfo?.items!!.size) {
                if ((cartInfo?.items!![i].classInfo != null &&
                            true == cartInfo?.items!![i].classInfo?.getClassId()
                        ?.equals(item.classInfo?.getClassId())) ||
                    (cartInfo?.items!![i].igInfo != null &&
                            true == cartInfo?.items!![i].igInfo?.igId
                        ?.equals(item.igInfo?.igId)) ||
                    (cartInfo?.items!![i].facility != null &&
                            true == cartInfo?.items!![i].facility?.getResourceID()
                        ?.equals(item.facility?.getResourceID())) ||
                    (cartInfo?.items!![i].event != null &&
                            true == cartInfo?.items!![i].event?.eventId?.equals(item.event?.eventId))
                ) {
                    cartInfo?.items!![i].attendees =
                        deleteAttendee(
                            cartInfo?.items!![i].attendees as ArrayList<Attendee>?,
                            attendee
                        )
                    break
                }
            }
        }
    }

    private fun deleteAttendee(
        attendees: ArrayList<Attendee>?,
        target: Attendee
    ): ArrayList<Attendee>? {
        if (attendees != null && attendees.size > 0) {
            for (i in attendees) {
                if (true == i.customerInfo?.mCustomerId.equals(target.customerInfo?.mCustomerId)) {
                    attendees.remove(i)
                    return attendees
                }
            }
        }

        return attendees
    }

    private fun resetCart() {
        presenter?.isDelete = true
        (activity as MainActivity).setItemAddToCart()
    }

    fun setSendEmailResult(isResult: Boolean) {
        presenter?.navigationView()
    }

    private fun hidePayContainer(isHide: Boolean) {
        if (isHide) {
//            refreshCartView()
            getView?.rl_pay?.visibility = View.GONE
            getView?.btn_back_action?.visibility = View.GONE
            getView?.btn_continue_browsing?.visibility = View.GONE

            //hide table
            getView?.ll_container_label?.visibility = View.GONE
            getView?.v_line?.visibility = View.GONE
            getView?.rcv_particulars_list?.visibility = View.GONE

            //hide total info
            getView?.ll_total_container?.visibility = View.GONE
        } else {
            getView?.rl_pay?.visibility = View.VISIBLE
            getView?.btn_back_action?.visibility = View.VISIBLE
            getView?.btn_continue_browsing?.visibility = View.VISIBLE
        }
    }

    override fun onPause() {
        super.onPause()
        dismissChoosePaymentDialog()
    }

    private fun showPaymentView() {
        if (totalCost?.getTotalPaymentAmount() != null) {
            presenter?.navigationPaymentView(totalCost?.getTotalPaymentAmount()!!)
        }

    }

    private fun dismissChoosePaymentDialog() {
        if (fragmentChoosePayment != null) {
            fragmentManager?.popBackStack()
            fragmentChoosePayment = null
        }
    }

    fun updatePaymentRequest(sessionCode: String?) {
        presenter?.updatePaymentRequest(sessionCode)
    }

    override fun onLoadCartSuccess(
        cartInfo: CartInfo?,
        isLoadCartForGetExternalLineId: Boolean,
        requestItems: ArrayList<ProductRequestItem>?
    ) {
        this.cartTotal = cartInfo
        if (transactionResponse == null && !isFreeCourses) {
            if (isLoadCartForGetExternalLineId) {
                this.totalCost = convertCartTotalCost(cartInfo, true)

                //update externalLineId in CartInfo
                this.cartInfo?.items?.forEach { cartItem ->
                    if (cartItem.event != null){
                        val product = cartInfo?.items?.find{it ->
                            it.productCode?.equals(cartItem.event?.eventCode) == true
                        }
                        if (product != null){
                            cartItem.event?.listSelectedTicket?.forEach { eventTicket ->
                                val productEventTicket = product.ticketGroups?.find { it1 ->
                                    it1.ticketId == eventTicket.id
                                }
                                if (productEventTicket != null){
                                    eventTicket.listTicketParticipantEntity.forEach { ticketEntity ->
                                        productEventTicket.tickets?.forEach { it2 ->
                                            val existedTicket = eventTicket.listTicketParticipantEntity.find { p ->
                                                p.externalLineId == it2.externalLineId
                                            } != null
                                            if (ticketEntity.externalLineId == null && !existedTicket){
                                                ticketEntity.externalLineId = it2.externalLineId
                                            }

                                            ticketEntity.listParticipant.forEach { eventParticipant ->
                                                it2.booking?.forEach { booking ->
                                                    val existedParticipant = isExistedParticipant(
                                                        externalLineId = booking.participant?.externalLineId ?: "",
                                                        componentId = booking.participant?.componentId ?: "",
                                                        userId = booking.participant?.userId ?: "",
                                                        listTicketParticipantEntity = eventTicket.listTicketParticipantEntity
                                                    )
                                                    if(eventParticipant.componentId == null && !existedParticipant) {
                                                        eventParticipant.externalLineId = booking.participant?.externalLineId
                                                        eventParticipant.componentId = booking.participant?.componentId
                                                        eventParticipant.userId = booking.participant?.userId
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //call add participant
                this.presenter?.addEventParticipant(
                    requestItems ?: ArrayList(),
                    0
                )
            } else {
                if (cartTotal?.isValidCart == true) {
                    this.totalCost = convertCartTotalCost(cartInfo, false)
                    if (!initCartSummaryList()) {
                        showErrorMessageAndDeleteCart(getString(R.string.invalid_cart_product_not_match))
                        return
                    }
                    if (totalCost != null) {
                        val totalCost = this.totalCost!!
                        val totalCostTemp =
                            Gson().fromJson(
                                Gson().toJson(totalCost),
                                TotalCostsResponse::class.java
                            )
                        updateCostProductToCart(
                            totalCostTemp.getProducts() as? ArrayList<ProductInfo>?,
                            false
                        )

                        var amount =
                            (if (totalCost.getTotalBeforeDiscountAmount() != null) totalCost.getTotalBeforeDiscountAmount()!! else 0f)
                        getView?.txt_subtotal?.text =
                            GeneralUtils.formatAmountSymbols("$", amount, 2)
                        val totalDiscountAmount: Float = (totalCost.getTotalDiscountAmount()
                            ?: 0F).toFloat() + (totalCost.promoDiscountAmount ?: 0f)
                        if (totalDiscountAmount == 0f) {
                            getView?.ll_total_discount?.visibility = View.GONE
                            getView?.view_line_2?.visibility = View.GONE
                        } else {
                            getView?.ll_total_discount?.visibility = View.VISIBLE
                            getView?.view_line_2?.visibility = View.VISIBLE
                            getView?.txt_adjustment_amount_label?.text =
                                getString(R.string.discount)
                            getView?.txt_adjustment_amount?.text =
                                GeneralUtils.formatAmountSymbols("$", totalDiscountAmount, 2)
                        }
                        amount =
                            (if (totalCost.getTotalPaymentAmount() != null) totalCost.getTotalPaymentAmount()!! else 0f)
                        getView?.txt_total_amount?.text =
                            GeneralUtils.formatAmountSymbols("$", amount, 2)
                    }
                } else {
                    showErrorMessageAndDeleteCart(getString(R.string.invalid_cart))
                }
            }

        }
    }

    private fun isExistedParticipant(
        externalLineId: String,
        componentId: String,
        userId: String,
        listTicketParticipantEntity: ArrayList<TicketEntity>
    ): Boolean{
        listTicketParticipantEntity.forEach { ticketEntity ->
            ticketEntity.listParticipant.forEach { eventParticipant ->
                if (eventParticipant.externalLineId?.equals(externalLineId) == true
                    && eventParticipant.componentId?.equals(componentId) == true
                    && eventParticipant.userId?.equals(userId) == true
                ){
                    return true
                }
            }
        }
        return false
    }

    private fun getItemId(cartItem: CartItem?): String? {
        return if (cartItem?.classInfo != null) {
            cartItem.classInfo?.sku
        } else if (cartItem?.igInfo != null) {
            cartItem.igInfo?.sku
        } else if (cartItem?.event != null) {
            cartItem.event?.sku
        } else if (cartItem?.facility != null) {
            val name =
                "${cartItem.facility?.outletName}_${cartItem.facility?.getResourceSubTypeName()}"
            name.replace(Regex(" "), "")
        } else {
            null
        }
    }

    private fun summaryCourseAttendees(
        summaryItem: CartItem,
        booking: Booking,
        attendeesTmp: ArrayList<Attendee>
    ) {
        val attendees = summaryItem.attendees
        if (attendees != null) {
            for (j in 0 until attendees.size) {
                if (attendees[j].customerInfo != null &&
                    true == booking.participant?.userId?.equals(attendees[j].customerInfo?.mCustomerId)
                ) {
                    attendees[j].isReservationSuccess = true
                    attendeesTmp.add(attendees[j].copyAttendee())
                }
            }
        }
    }

    private fun showCartSummaryBeforeCheckout() {
        cartSummaryList.clear()
        cartInfo?.items?.let {
            cartSummaryList.addAll(it)
            cartSummaryAdapter?.notifyDataSetChanged()
        }
        getView?.txt_subtotal?.text = GeneralUtils.formatAmountSymbols("$", 0f, 2)
        getView?.txt_adjustment_amount_label?.text = getString(R.string.discount)
        getView?.txt_adjustment_amount?.text = GeneralUtils.formatAmountSymbols("$", 0f, 2)
        getView?.txt_total_amount?.text = GeneralUtils.formatAmountSymbols("$", 0f, 2)
    }

    private fun initCartSummaryList(): Boolean {
        val cartItems = cartInfo?.items
        cartSummaryList.clear()
        val listFailedItem = ArrayList<Item>()
        val errorMessage = StringBuilder()
        if (cartItems != null && cartTotal != null) {
            if (cartItems.size != cartTotal!!.items?.size) {
                return false
            }
            cartItems.forEach { cartItem ->
                val cartItemId = getItemId(cartItem)
                cartTotal!!.items?.forEach { item ->
                    if (isQuickBookFacility && item.productType.isNullOrEmpty()) {
                        item.productType = Product.PRODUCT_FACILITY
                    }
                    if (cartItemId != null &&
                        (cartItemId == item.itemId || isQuickBookFacility)
                    ) {
                        val attendeesTmp = ArrayList<Attendee>()
                        val summaryItem = cartItem.copyCartItem() as CartItem
                        var shouldAddToSummaryList = false
                        var hasFailedReservation = false
                        if (item.productType == Product.PRODUCT_EVENT){
                            item.ticketGroups?.forEach { ticketGroup ->
                                val eventTicket = EventTicket()
                                eventTicket.id = ticketGroup.ticketId

                                ticketGroup.tickets?.forEach { ticket ->
                                    val ticketEntity = TicketEntity()
                                    ticketEntity.externalLineId = ticket.externalLineId
                                    ticketEntity.listParticipant = ArrayList()
                                    ticket.booking?.forEach { booking ->
                                        if (booking.isReservationSuccess == true){
                                            val eventParticipant = EventParticipant()
                                            eventParticipant.externalLineId = booking.participant?.externalLineId
                                            eventParticipant.componentId = booking.participant?.componentId
                                            eventParticipant.userId = booking.participant?.userId
                                            eventParticipant.participantAfterLoadCart = booking.participant

                                            ticketEntity.listParticipant.add(eventParticipant)


                                        } else {
                                            hasFailedReservation = true
                                            if (errorMessage.isNotEmpty()) {
                                                errorMessage.append("\n")
                                            }
                                            val reservationErrorMsg = booking.reservationErrorMessageCRM
                                                ?: getString(R.string.unknown_error)
                                            errorMessage.append(
                                                getString(
                                                    R.string.reservation_error,
                                                    getItemName(item), reservationErrorMsg
                                                )
                                            )
                                        }
                                    }
                                    if (ticketEntity.listParticipant.isNotEmpty()) {
                                        eventTicket.listTicketParticipantEntity.add(ticketEntity)
                                    }
                                }
                                if (eventTicket.listTicketParticipantEntity.isEmpty()) {
                                    summaryItem.event?.listSelectedTicket?.add(eventTicket)
                                }
                                if (summaryItem.event?.listSelectedTicket?.isNotEmpty() == true){
                                    shouldAddToSummaryList = true
                                }
                            }
                        } else {
                            item.booking?.forEach { bookingItem ->
                                if (bookingItem.isReservationSuccess == true) {
                                    if (item.productType == Product.PRODUCT_FACILITY) {
                                        shouldAddToSummaryList = true
                                        if (attendeesTmp.size == 0) {
                                            attendeesTmp.add(Attendee(isReservationSuccess = true))
                                            summaryItem.attendees = attendeesTmp
                                        }
                                    } else if (item.productType == Product.PRODUCT_COURSE || item.productType == Product.PRODUCT_INTEREST_GROUP) {
                                        summaryCourseAttendees(
                                            summaryItem,
                                            bookingItem,
                                            attendeesTmp
                                        )
                                        if (attendeesTmp.size > 0) {
                                            shouldAddToSummaryList = true
                                        }
                                    }
                                } else {
                                    hasFailedReservation = true
                                    if (errorMessage.isNotEmpty()) {
                                        errorMessage.append("\n")
                                    }
                                    val reservationErrorMsg = bookingItem.reservationErrorMessageCRM
                                        ?: getString(R.string.unknown_error)
                                    errorMessage.append(
                                        getString(
                                            R.string.reservation_error,
                                            getItemName(item), reservationErrorMsg
                                        )
                                    )
                                }
                            }
                        }

                        if (shouldAddToSummaryList) {
                            cartSummaryList.add(summaryItem)
                        }
                        if (hasFailedReservation) {
                            if (item.productType == Product.PRODUCT_FACILITY) {
                                cartSummaryList.clear()
                            }
                            listFailedItem.add(item)
                        }
                    }
                }
            }
        }
        var retVal = true
        if (errorMessage.isNotEmpty()) {
            retVal = false
            if (isQuickBookFacility) {
                showErrorMessageAndDeleteCart(errorMessage.toString())
            } else {
                showMessageAndBack(errorMessage.toString())
            }
        } else {
            cartSummaryAdapter?.notifyDataSetChanged()
        }
        return retVal

    }

    private fun getItemName(item: Item): String? {
        return if (item.productType == Product.PRODUCT_FACILITY) {
            item.name
        } else {
            item.title
        }
    }

    override fun onDeleteFailedReservationResponse(data: ParticipantResponse?, errorMsg: String) {
        showErrorMessage(errorMsg)
    }

    override fun isQuickBook(): Boolean {
        return (activity as MainActivity).isQuickBook
    }

    private fun convertCartTotalCost(
        cartInfo: CartInfo?,
        isLoadCartForGetExternalLineId: Boolean = false
    ): TotalCostsResponse? {
        val cartId = getCartId()
        if (!cartId.isNullOrEmpty()) {
            val totalCost = TotalCostsResponse()
            totalCost.setShoppingCartID(cartId)
            totalCost.setTotalBeforeDiscountAmount(formatPrice(cartInfo?.total?.subTotal))
            totalCost.setTotalDiscountAmount(formatPrice(cartInfo?.total?.discountTotal))
            totalCost.setTotalPaymentAmount(formatPrice(cartInfo?.total?.grandTotal))
            totalCost.setTotalGST(formatPrice(cartInfo?.total?.taxTotal)?.toDouble())
            totalCost.promoDiscountAmount = formatPrice(cartInfo?.total?.promoDiscountAmount)
            totalCost.promoCode = cartInfo?.promotion?.promoCode
            val productList: MutableList<ProductInfo> = ArrayList()
            cartInfo?.items?.forEach { item ->
                if ((item.booking?.size ?: 0) > 0) {
                    item.booking?.forEach { booking ->
                        if (booking.isReservationSuccess == true) {
                            val productInfo = ProductInfo()
                            productInfo.productId = item.itemId ?: item.productCode
                            productInfo.setProductCode(item.productCode)
                            var productTitle = booking.facilityName
                            if (productTitle.isNullOrEmpty()) {
                                productTitle = booking.title
                            }
                            productInfo.productType = item.productType
                            productInfo.setProductTitle(productTitle)
                            productInfo.setCustomerId(booking.participant?.userId)
                            productInfo.setCustomerName(booking.participant?.fullName)
                            productInfo.setOutletName(item.outlet)
                            productInfo.setBeforeDiscountAmount(formatPrice(booking.price?.subTotal))
                            productInfo.setDiscountAmount(formatPrice(booking.price?.discountTotal))
                            productInfo.setPaymentAmount(formatPrice(booking.price?.grandTotal))
                            productInfo.promoDiscountAmount =
                                formatPrice(booking.price?.promoDiscountAmount)
                            productList.add(productInfo)
                        }
                    }
                } else { //event
                    val productInfo = ProductInfo()
                    productInfo.productId = item.itemId
                    productInfo.productType = Product.PRODUCT_EVENT
                    productInfo.setProductCode(item.productCode)
                    productInfo.setProductTitle(item.title)
                    productInfo.setOutletName(item.outlet)
                    productInfo.setBeforeDiscountAmount(formatPrice(cartInfo.total?.subTotal))
                    productInfo.setDiscountAmount(formatPrice(cartInfo.total?.discountTotal))
                    productInfo.setPaymentAmount(formatPrice(cartInfo.total?.grandTotal))
                    productInfo.promoDiscountAmount =
                        formatPrice(cartInfo.total?.promoDiscountAmount)

                    val listSelectedTicketType = ArrayList<EventTicket>()
                    item.ticketGroups?.forEach { ticketGroup ->
                        val eventTicket = EventTicket()
                        eventTicket.qty = ticketGroup.qty ?: 0
                        eventTicket.id = ticketGroup.ticketId
                        eventTicket.name = ticketGroup.ticketName
                        eventTicket.eventId = item.productCode
                        eventTicket.ticketMapCount = ticketGroup.ticketMapCount ?: 1

                        val listTicketEntity = ArrayList<TicketEntity>()
                        ticketGroup.tickets?.forEach { ticket ->
                            val ticketEntity = TicketEntity()
                            ticketEntity.externalLineId = ticket.externalLineId
                            var discountAmount: Float = 0f
                            var promoDiscountAmount: Float = 0f
                            val listParticipant = ArrayList<EventParticipant>()
                            if (!isLoadCartForGetExternalLineId) {
                                ticket.booking?.forEach { booking ->
                                    if (booking.isReservationSuccess == true && booking.participant != null && !booking.participant?.fullName.isNullOrEmpty()) {
                                        val eventParticipant = EventParticipant()
                                        eventParticipant.isNew = false
                                        eventParticipant.isFullFillInfo = true
                                        eventParticipant.nameToShow = booking.participant?.fullName
                                        eventParticipant.id = booking.participant?.nric
                                        eventParticipant.participantAfterLoadCart =
                                            booking.participant
                                        eventParticipant.beforeDiscountAmount =
                                            formatPrice(booking.price?.subTotal) ?: 0f
                                        eventParticipant.discountAmount =
                                            formatPrice(booking.price?.discountTotal) ?: 0f
                                        eventParticipant.paymentAmount =
                                            formatPrice(booking.price?.grandTotal) ?: 0f
                                        eventParticipant.promoDiscountAmount =
                                            formatPrice(booking.price?.promoDiscountAmount) ?: 0f
                                        eventParticipant.outletName = ticket.outlet

                                        /*beforeDiscountAmount += (formatPrice(booking.price?.subTotal)
                                            ?: 0f)
                                        discountAmount += formatPrice(booking.price?.discountTotal)
                                            ?: 0f
                                        paymentAmount += formatPrice(booking.price?.grandTotal)
                                            ?: 0f
                                        promoDiscountAmount += formatPrice(booking.price?.promoDiscountAmount)
                                            ?: 0f*/

                                        listParticipant.add(eventParticipant)
                                    }
                                }
                            }

                            ticketEntity.beforeDiscountAmount = ticket.total?.totalPrice
                            ticketEntity.discountAmount = discountAmount
                            ticketEntity.paymentAmount = ticket.total?.totalPrice
                            ticketEntity.promoDiscountAmount = promoDiscountAmount
                            ticketEntity.listParticipant = listParticipant
                            listTicketEntity.add(ticketEntity)
                        }

                        eventTicket.listTicketParticipantEntity = listTicketEntity
                        listSelectedTicketType.add(eventTicket)
                    }


                    productInfo.listSelectedTicket = listSelectedTicketType
                    productList.add(productInfo)
                }

            }
            totalCost.setProducts(productList)
            return totalCost
        }
        return null
    }

    private fun formatPrice(priceString: String?): Float? {
        return priceString?.replace("$", "")?.toFloat()
    }

    override fun getCartId(): String? {
        return (activity as MainActivity).getBookingCartId()
    }

    override fun updateCartId(data: BookingResponse?) {
        if (isQuickBook()) {
            (activity as MainActivity).quickCartId = data?.cartId
        } else {
            (activity as MainActivity).cartId = data?.cartId
        }
    }

    override fun onBookingFailed() {
        val msg = getString(R.string.booking_fail, presenter?.sessionCode ?: "")
        showErrorMessageAndDeleteCart(msg)
    }

    override fun updateReservation() {
        if (cartInfo?.hasReservation != true) {
            cartInfo?.hasReservation = true
        }
    }

    override fun onApplyRemovePromoCodeSuccess(cartInfo: CartInfo?, isApply: Boolean) {
        if (isApply) {
            getView?.btn_remove?.visibility = View.VISIBLE
            getView?.rl_promo_code?.setBackgroundResource(R.drawable.bg_green_border_corner_black)
            getView?.txt_promo_code?.setTextColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.green_color_1
                )
            )
            getView?.txt_promo_error?.visibility = View.GONE
            getView?.img_remove?.setImageResource(R.drawable.ic_valid_promo_code)
            getView?.img_remove?.visibility = View.VISIBLE
        } else {
            getView?.btn_remove?.visibility = View.INVISIBLE
            getView?.rl_promo_code?.visibility = View.GONE
            getView?.txt_promo_error?.visibility = View.GONE
            getView?.txt_available_promo_code?.visibility = View.VISIBLE
        }
        isInvalidPromoCode = false

        onLoadCartSuccess(cartInfo)
    }

    override fun onApplyPromoCodeError(error: String?) {
        var errorMsg = error
        if (errorMsg.isNullOrEmpty()) {
            errorMsg = getString(R.string.default_promo_error)
        }
        isInvalidPromoCode = true
        getView?.txt_promo_error?.visibility = View.VISIBLE
        getView?.txt_promo_error?.text =
            HtmlCompat.fromHtml(errorMsg, HtmlCompat.FROM_HTML_MODE_LEGACY)
        getView?.img_remove?.setImageResource(R.drawable.ic_remove_promo_code)
        getView?.img_remove?.visibility = View.VISIBLE
        getView?.txt_promo_code?.setTextColor(
            ContextCompat.getColor(
                activity!!,
                R.color.red_color_3
            )
        )
        getView?.rl_promo_code?.setBackgroundResource(R.drawable.bg_red_border_corner_black)
    }

    override fun onRemovePromoCodeError(error: String?) {
        var errorMsg = error
        if (errorMsg.isNullOrEmpty()) {
            errorMsg = getString(R.string.remove_promo_error)
        }
        showErrorMessage(errorMsg)
    }

    override fun onGetAvailablePromoCodesSuccess(listPromoCodeResponse: ListPromoCodeResponse?) {
        if (listPromoCodeResponse?.promoCodeList == null || listPromoCodeResponse.promoCodeList!!.isEmpty()) {
            showErrorMessage(getString(R.string.no_promo_code_available))
        } else {
            presenter?.navigationPromoCodeView(listPromoCodeResponse)
        }
    }

    override fun showErrorMessageAvailablePromoCode(data: BaseResponse<ListPromoCodeResponse>) {
        val msg = getString(R.string.request_error, data.errorCode)
        showErrorMessage(msg)
    }

    private var totalCost: TotalCostsResponse? = null
    override fun resultTotalCosts(totalCost: TotalCostsResponse) { //dont use
        this.totalCost = totalCost

        val totalCostTemp =
            Gson().fromJson(Gson().toJson(totalCost), TotalCostsResponse::class.java)
        updateCostProductToCart(totalCostTemp.getProducts() as ArrayList<ProductInfo>?)

        var amount =
            (if (totalCost.getTotalBeforeDiscountAmount() != null) totalCost.getTotalBeforeDiscountAmount()!! else 0f)
        getView?.txt_subtotal?.text = GeneralUtils.formatAmountSymbols("$", amount, 2)
        val totalGST: Float = (totalCost.getTotalDiscountAmount() ?: 0F).toFloat()
        getView?.txt_adjustment_amount_label?.text = getString(R.string.discount)
        getView?.txt_adjustment_amount?.text = GeneralUtils.formatAmountSymbols("$", totalGST, 2)
        amount =
            (if (totalCost.getTotalPaymentAmount() != null) totalCost.getTotalPaymentAmount()!! else 0f)
        getView?.txt_total_amount?.text = GeneralUtils.formatAmountSymbols("$", amount, 2)
    }

    private fun resetTotalCostView() {
        totalCost = null
        getView?.txt_subtotal?.text = GeneralUtils.formatAmountSymbols("$", 0f, 2)
        getView?.txt_adjustment_amount?.text = GeneralUtils.formatAmountSymbols("$", 0f, 2)
        getView?.txt_total_amount?.text = GeneralUtils.formatAmountSymbols("$", 0f, 2)
    }

    private fun updateCostProductToCart(
        productList: ArrayList<ProductInfo>?,
        isLoadCartForGetExternalLineId: Boolean = false
    ) {
        refreshTotalCostItem()

        if (productList != null && productList.size > 0) {
            for (i in 0 until productList.size) {
                for (j in 0 until cartSummaryList.size) {
                    if (cartSummaryList[j].classInfo != null && productList[i].productId.equals(
                            cartSummaryList[j].classInfo?.sku
                        )
                    ) {
                        updateCostProductToCart(cartSummaryList[j], productList[i], j)
                        break
                    } else if (cartSummaryList[j].igInfo != null && productList[i].productId.equals(
                            cartSummaryList[j].igInfo?.sku
                        )
                    ) {
                        updateCostProductToCart(cartSummaryList[j], productList[i], j)
                        break
                    } else if (cartSummaryList[j].facility != null) {
                        updateCostProductToCart(cartSummaryList[j], productList[i], j)
                        break
                    } else if (cartSummaryList[j].event != null && productList[i].productId.equals(
                            cartSummaryList[j].event?.sku
                        )
                    ) {
                        updateCostProductToCart(
                            cartSummaryList[j],
                            productList[i],
                            j,
                            isLoadCartForGetExternalLineId
                        )
                        break
                    }
                }
            }
        }

        var i = 0
        var j = 0
        while (i < cartSummaryList.size) {
            j = 0
            if (cartSummaryList[i].event != null) { //case event
                if (isLoadCartForGetExternalLineId) {
                    val eventInfo = cartSummaryList[i].event
                    eventInfo?.listSelectedTicket?.forEach { eventTicket ->
                        val positionEventTicket =
                            cartSummaryList[i].event?.listSelectedTicket?.indexOf(eventTicket)
                        if (positionEventTicket != null) {
                            eventTicket.listTicketParticipantEntity.forEach { ticketEntity ->
                                val positionTicketEntity =
                                    cartSummaryList[i].event?.listSelectedTicket?.get(
                                        positionEventTicket
                                    )
                                        ?.listTicketParticipantEntity?.indexOf(ticketEntity)
                                if (positionTicketEntity != null && ticketEntity.externalLineId.isNullOrEmpty()) {
                                    //remove this ticket
                                    cartSummaryList[i].event?.listSelectedTicket?.get(
                                        positionEventTicket
                                    )
                                        ?.listTicketParticipantEntity?.removeAt(
                                            positionTicketEntity
                                        )
                                }
                            }
                        }

                    }
                } else {
                    cartSummaryList[i].event
                        ?.listSelectedTicket?.forEach { eventTicket ->
                            val positionEventTicket =
                                cartSummaryList[i].event?.listSelectedTicket?.indexOf(eventTicket)
                            if (positionEventTicket != null) {
                                eventTicket.listTicketParticipantEntity.forEach { ticketEntity ->
                                    val positionTicketEntity =
                                        cartSummaryList[i].event?.listSelectedTicket?.get(
                                            positionEventTicket
                                        )
                                            ?.listTicketParticipantEntity?.indexOf(ticketEntity)
                                    if (positionTicketEntity != null) {
                                        ticketEntity.listParticipant.forEach { eventParticipant ->
                                            if (eventParticipant.participantAfterLoadCart == null) {
                                                //remove participant
                                                cartSummaryList[i].event
                                                    ?.listSelectedTicket?.get(positionEventTicket)
                                                    ?.listTicketParticipantEntity?.get(
                                                        positionTicketEntity
                                                    )
                                                    ?.listParticipant?.remove(eventParticipant)
                                            }
                                        }
                                    }
                                }
                            }

                        }
                }
            } else {
                val attendees = cartSummaryList[i].attendees
                if (attendees != null && attendees.size > 0) {
                    while (j < attendees.size) {
                        if (attendees[j].productInfo == null) {
                            (cartSummaryList[i].attendees as ArrayList<Attendee>).removeAt(j)
                            j--
                        }
                        j++
                    }
                }

                if (requireAttendee(cartSummaryList[i])) {
                    removeItemFromCart(cartSummaryList[i])
                    cartSummaryList.removeAt(i)
                    i--
                }
            }


            i++
        }
        cartSummaryAdapter?.notifyDataSetChanged()
    }

    private fun requireAttendee(cartItem: CartItem): Boolean {
        if (cartItem.attendees != null && cartItem.attendees!!.size > 0) {
            return false
        }

        if (cartItem.classInfo != null || cartItem.igInfo != null) {
            return true
        }

        return false
    }

    private fun removeItemFromCart(cartItem: CartItem) {
        if (activity != null && activity is MainActivity) {
            (activity as MainActivity).deleteCartItem(cartItem)
        }
    }

    private fun refreshTotalCostItem() {
        cartSummaryList.forEach { item ->
            item.attendees?.forEach { attendee ->
                attendee.productInfo = null
            }
        }
    }

    private fun updateCostProductToCart(
        item: CartItem,
        productInfo: ProductInfo,
        index: Int,
        isLoadCartForGetExternalLineId: Boolean = false
    ) {
        val attendees = item.attendees
        if (item.event != null) { //case event
            val eventInfo = item.event
            if (productInfo.productId.equals(eventInfo?.sku)) {
                val listEventTicketProductInfo = productInfo.listSelectedTicket

                val listSelectedTicketCartItem = eventInfo?.listSelectedTicket ?: ArrayList()

                if (listEventTicketProductInfo.size > 0) {
                    for (i in 0 until listEventTicketProductInfo.size) {
                        val eventTicketProductInfo = listEventTicketProductInfo[i]
                        val eventTicketCartItem = getEventTicketInListById(
                            id = eventTicketProductInfo.id,
                            listSelectedTicketCartItem
                        )
                        if (eventTicketCartItem != null) {
                            val positionInListSelectedTicketCartItem =
                                listSelectedTicketCartItem.indexOf(eventTicketCartItem)
                            eventTicketProductInfo.listTicketParticipantEntity.forEach { ticketEntityProduct ->
                                if (isLoadCartForGetExternalLineId) { // first time load cart so do not have participant booking
                                    //get ticketEntity has empty externalLineId in listTicketParticipantEntity of eventTicketCartItem
                                    val ticketEntityEmptyExternalLineId =
                                        getTicketEntityEmptyExternalLineId(eventTicketCartItem.listTicketParticipantEntity)
                                    if (ticketEntityEmptyExternalLineId != null) {
                                        val positionTicketEntityCartItem =
                                            eventTicketCartItem.listTicketParticipantEntity.indexOf(
                                                ticketEntityEmptyExternalLineId
                                            )
                                        ticketEntityEmptyExternalLineId.externalLineId =
                                            ticketEntityProduct.externalLineId
                                        eventTicketCartItem.listTicketParticipantEntity[positionTicketEntityCartItem] =
                                            ticketEntityEmptyExternalLineId

                                        cartSummaryList[index].event?.listSelectedTicket?.get(
                                            positionInListSelectedTicketCartItem
                                        )
                                            ?.listTicketParticipantEntity?.set(
                                                positionTicketEntityCartItem,
                                                ticketEntityEmptyExternalLineId
                                            )
                                    }
                                } else { //case after call add participant api
                                    eventTicketCartItem.listTicketParticipantEntity.forEach { ticketEntityCartItem ->
                                        if (ticketEntityCartItem.externalLineId?.equals(
                                                ticketEntityProduct.externalLineId
                                            ) == true
                                        ) {
                                            val positionTicketEntityCartItem =
                                                eventTicketCartItem.listTicketParticipantEntity.indexOf(
                                                    ticketEntityCartItem
                                                )
                                            ticketEntityCartItem.beforeDiscountAmount =
                                                ticketEntityProduct.beforeDiscountAmount
                                            ticketEntityCartItem.discountAmount =
                                                ticketEntityProduct.discountAmount
                                            ticketEntityCartItem.paymentAmount =
                                                ticketEntityProduct.paymentAmount
                                            ticketEntityCartItem.promoDiscountAmount =
                                                ticketEntityProduct.promoDiscountAmount

                                            ticketEntityProduct.listParticipant.forEach { participantProduct ->
                                                ticketEntityCartItem.listParticipant.forEach { participantCartItem ->
                                                    if ( participantCartItem.externalLineId?.equals(participantProduct.participantAfterLoadCart?.externalLineId) == true
                                                        && participantCartItem.componentId?.equals(participantProduct.participantAfterLoadCart?.componentId) == true
                                                    ) {
                                                        val positionParticipantCartItem =
                                                            ticketEntityCartItem.listParticipant.indexOf(
                                                                participantCartItem
                                                            )
                                                        participantCartItem.outletName =
                                                            participantProduct.outletName
                                                        participantCartItem.participantAfterLoadCart =
                                                            participantProduct.participantAfterLoadCart
                                                        participantCartItem.beforeDiscountAmount =
                                                            participantProduct.beforeDiscountAmount
                                                        participantCartItem.discountAmount =
                                                            participantProduct.discountAmount
                                                        participantCartItem.promoDiscountAmount =
                                                            participantProduct.promoDiscountAmount
                                                        participantCartItem.paymentAmount =
                                                            participantProduct.paymentAmount

                                                        ticketEntityCartItem.listParticipant[positionParticipantCartItem] =
                                                            participantCartItem
                                                    }
                                                }
                                            }

                                            cartSummaryList[index].event?.listSelectedTicket?.get(
                                                positionInListSelectedTicketCartItem
                                            )
                                                ?.listTicketParticipantEntity?.set(
                                                    positionTicketEntityCartItem,
                                                    ticketEntityCartItem
                                                )
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        } else {
            if (attendees != null && attendees.size > 0) {
                if (attendees.size == 1) {
                    if (cartSummaryList[index].attendees!![0].productInfo == null) {
                        cartSummaryList[index].attendees!![0].productInfo = productInfo
                    } else {
                        cartSummaryList[index].attendees!![0].productInfo = groupFacilityValue(
                            cartSummaryList[index].attendees!![0].productInfo,
                            productInfo
                        )
                    }
                } else {
                    for (i in 0 until attendees.size) {
                        if (true == attendees[i].customerInfo?.mCustomerId?.equals(productInfo.getCustomerId())) {
                            cartSummaryList[index].attendees!![i].productInfo = productInfo
                            return
                        }
                    }
                }
            }
        }

    }

    private fun getTicketEntityEmptyExternalLineId(list: ArrayList<TicketEntity>): TicketEntity? {
        list.forEach {
            if (it.externalLineId.isNullOrEmpty()) {
                return it
            }
        }
        return null
    }

    private fun getEventTicketInListById(id: String?, list: ArrayList<EventTicket>): EventTicket? {
        list.forEach {
            if (it.id.equals(id)) {
                return it
            }
        }
        return null
    }

    private fun groupFacilityValue(
        beforeProduct: ProductInfo?,
        productInfo: ProductInfo?
    ): ProductInfo? {
        val product = beforeProduct
        val beforeDiscountAmount = (product?.getBeforeDiscountAmount()
            ?: 0f) + (productInfo?.getBeforeDiscountAmount() ?: 0f)
        product?.setBeforeDiscountAmount(beforeDiscountAmount)
        val paymentAmount = (product?.getPaymentAmount()
            ?: 0f) + (productInfo?.getPaymentAmount() ?: 0f)
        product?.setPaymentAmount(paymentAmount)
        val discountAmount = beforeDiscountAmount - paymentAmount
        product?.setDiscountAmount(discountAmount)
        return product
    }

    override fun onDestroy() {
        AppDatabase.destroyInstance()
        dbWorkerThread.quit()

        super.onDestroy()
        presenter?.onDestroy()
    }

    fun getBitmap(): Bitmap? {
        return bitmap
    }

    override fun getToken(): String? {
        return MySharedPref(activity).eKioskHeader
    }

    private var messageDialog: MessageDialogFragment? = null

    override fun <T> showErrorMessage(response: BaseResponse<T>) {
        if (messageDialog == null || messageDialog != null && messageDialog?.isVisible == false) {
            messageDialog = response.formatBaseResponse(0, false)
            if (fragmentManager != null) {
                messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
            }
        }
    }

    override fun showErrorMessage(message: String) {
        messageDialog?.dismiss()

        messageDialog = MessageDialogFragment.newInstance("", message)
        if (fragmentManager != null) {
            messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    private var messageDialogListener = object : MessageDialogFragment.MessageDialogListener {
        override fun onNegativeClickListener(dialogFragment: DialogFragment) {
            LogManager.d(TAG, "onNegativeClickListener")
        }

        override fun onPositiveClickListener(dialogFragment: DialogFragment) {
            LogManager.d(TAG, "onPositiveClickListener")
        }

        override fun onNeutralClickListener(dialogFragment: DialogFragment) {
            LogManager.i("Back cart view due to error")
            backCartView()
        }

    }

    override fun showMessageAndBack(message: String?) {
        if (messageDialog == null || messageDialog != null && messageDialog?.showsDialog == false) {
            messageDialog = MessageDialogFragment.newInstance(
                "",
                if (!TextUtils.isEmpty(message)) message!! else ""
            )
            messageDialog?.setListener(messageDialogListener)
            if (fragmentManager != null) {
                messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
            }
        }
    }

    override fun <T> showMessageAndBack(message: BaseResponse<T>) {
        messageDialog?.dismiss()

        messageDialog = message.formatBaseResponse(0, false)
        messageDialog?.setListener(messageDialogListener)
        if (fragmentManager != null) {
            messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun showErrorMessageTitleAndBack(messageResId: Int, title: Int) {
        messageDialog?.dismiss()

        messageDialog = MessageDialogFragment.newInstance(title, messageResId, true)
        messageDialog?.setListener(messageDialogListener)
        if (fragmentManager != null) {
            messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    val listenerDeleteCart = object : MessageDialogFragment.MessageDialogListener {
        override fun onNegativeClickListener(dialogFragment: DialogFragment) {
            LogManager.d(TAG, "onNegativeClickListener")
        }

        override fun onPositiveClickListener(dialogFragment: DialogFragment) {
            LogManager.d(TAG, "onPositiveClickListener")
        }

        override fun onNeutralClickListener(dialogFragment: DialogFragment) {
            dismissMessageDialog()
            LogManager.i("Close error dialog -> delete cart")
            (activity as? MainActivity)?.deleteCart()
            fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

    }


    fun showErrorMessageAndDeleteCart(message: String) {
        messageDialog?.dismiss()
        messageDialog = MessageDialogFragment.newInstance("", message)
        LogManager.i("Delete cart because error")
        messageDialog?.setListener(listenerDeleteCart)
        if (fragmentManager != null) {
            messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun dismissMessageDialog() {
        if (messageDialog != null) {
            messageDialog?.dismiss()
            messageDialog = null
        }
    }

    private var deleteConfirmDialog: MessageDialogFragment? = null
    private fun deleteConfirmEvent(
        parentIndex: Int,
        index: Int
    ): MessageDialogFragment.MessageDialogListener {
        return object : MessageDialogFragment.MessageDialogListener {
            override fun onNegativeClickListener(dialogFragment: DialogFragment) {
                dismissDeleteConfirmDialog()
            }

            override fun onPositiveClickListener(dialogFragment: DialogFragment) {
                if ((isQuickBookFacility && getCartId() != null) || cartSummaryList.size == 1) {
                    LogManager.i("Delete the last item in cart")
                    cartInfo?.items?.clear()
                    cartSummaryList.clear()
                    backCartView()
                } else if (parentIndex != CheckoutPresenter.CLEAR_ALL && index == CheckoutPresenter.CLEAR_ALL &&
                    getPayer() != null
                ) {
                    presenter?.deleteItem(cartSummaryList[parentIndex], getCartId()!!, parentIndex)
                }
                dismissDeleteConfirmDialog()
            }

            override fun onNeutralClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onNeutralClickListener")
            }

        }
    }

    private fun showDeleteConfirmDialog(parentIndex: Int, index: Int) {
        if (deleteConfirmDialog == null) {
            var message = R.string.delete_item_sure
            var isShowTitle = false
            if (parentIndex == CheckoutPresenter.CLEAR_ALL && index == CheckoutPresenter.CLEAR_ALL) {
                message = R.string.delete_all_item_sure
            } else if (index != CheckoutPresenter.CLEAR_ALL) {
                if (isLastAttendee(parentIndex, index)) {
                    message = R.string.remove_last_attendee
                    isShowTitle = true
                } else {
                    message = R.string.are_you_sure_to_remove_this_attendee
                }
            }
            deleteConfirmDialog = MessageDialogFragment.newInstance(
                R.string.remove_last_attendee_title,
                message,
                isShowTitle
            )
            deleteConfirmDialog?.setMultiChoice(getString(R.string.no), getString(R.string.yes))
            deleteConfirmDialog?.setListener(
                deleteConfirmEvent(
                    parentIndex,
                    index
                )
            )
        }
        if (fragmentManager != null) {
            deleteConfirmDialog?.show(
                fragmentManager!!,
                MessageDialogFragment::class.java.simpleName
            )
        }
    }

    private fun isLastAttendee(parentIndex: Int, index: Int): Boolean {
        if (index == CheckoutPresenter.CLEAR_ALL) {
            return false
        }

        if (cartSummaryList[parentIndex].classInfo != null &&
            (cartSummaryList[parentIndex].attendees?.size ?: 0) == 1
        ) {
            return true
        }

        if (cartSummaryList[parentIndex].igInfo != null &&
            (cartSummaryList[parentIndex].attendees?.size ?: 0) == 1
        ) {
            return true
        }

        if (cartSummaryList[parentIndex].event != null && cartSummaryList[parentIndex].event?.registerForMyself != null &&
            true == cartSummaryList[parentIndex].event?.registerRequired && (cartSummaryList[parentIndex].attendees?.size
                ?: 0) == 1
        ) {
            return true
        }

        return false
    }

    private fun dismissDeleteConfirmDialog() {
        deleteConfirmDialog?.dismiss()
        deleteConfirmDialog = null
    }

    override fun getContext(): Context? {
        return activity
    }

    override fun updateSessionCode(sessionCode: String?) {
        //update sessionCode for MainActivity
        cartInfo?.sessionCode = sessionCode
    }

    fun checkSessionCode() {
        val token = MySharedPref(activity).eKioskHeader
        val kiosk =
            Gson().fromJson<KioskInfo>(MySharedPref(activity).kioskInfo, KioskInfo::class.java)
        if (cartInfo?.sessionCode.isNullOrEmpty()) {
            presenter?.submitPayment(token, kiosk?.id, cartInfo?.payer, true)
        } else {
            presenter?.updateSessionCode(cartInfo?.sessionCode!!)
            presenter?.submitPayment(token, kiosk?.id, cartInfo?.payer, false)
            showLoading()
            bookProduct()
        }
    }

    override fun deleteCart() {
        (activity as MainActivity).cartId = null
    }

    override fun onDeleteCartFailed() {
        showMessageAndBack(getString(R.string.unable_delete_cart_and_recreate))
    }

    override fun removeCartLock() {
        (activity as MainActivity).cart?.isLocked = false
        (activity as MainActivity).lockedCart = null
    }

    override fun onResume() {
        super.onResume()
        getView?.rl_pay?.visibility = View.VISIBLE
        (activity as? MainActivity)?.showProgressSummary()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PAYMENT -> {
                transactionResponse =
                    data?.getParcelableExtra(PaymentFragment.TRANSACTION_RESPONSE)
                val rawBytes = data?.getByteArrayExtra(PaymentFragment.EXTRA_TRANSACTION_RAW)
                val selectedPaymentType = data?.getIntExtra(PaymentFragment.EXTRA_PAYMENT_TYPE, 0)
                val responseCode = data?.getStringExtra(PaymentFragment.EXTRA_RESPONSE_CODE)
                paymentState = data?.getStringExtra(PaymentFragment.EXTRA_PAYMENT_STATE)
                paymentReferenceId = data?.getStringExtra(PaymentFragment.EXTRA_REF_ID)

                // update database
                presenter?.updatePayment(
                    totalCost,
                    cartSummaryList,
                    transactionResponse,
                    HexBytesUtils.bytesToHex(rawBytes ?: ByteArray(0)),
                    selectedPaymentType,
                    responseCode,
                    paymentReferenceId,
                    payer
                )

                val token = getToken()

                // update payment to tks-server
                presenter?.updatePayment(token, null, null)

                if (resultCode == Activity.RESULT_OK) {
                    getView?.rl_pay?.visibility = View.GONE
                    onPaymentDone()
                } else {
                    getView?.rl_pay?.visibility = View.VISIBLE
                }
            }
            SELECT_PROMO_CODE_REQUEST_CODE -> {
                val promoCode = data?.getStringExtra(PromoCodeFragment.ARG_SELECTED_PROMO_CODE)
                if (promoCode != null) {
                    showPromoCode(promoCode)
                    presenter?.applyPromoCode(promoCode)
                }
            }
            EMAIL_REQUEST_CODE -> {
                handleEmailResult(data)
            }
            DeclarationPresenter.DECLARATION_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    isAllowCheckout =
                        data?.getBooleanExtra(DeclarationPresenter.ARG_ALLOW_CHECKOUT, false)
                            ?: false
                    if (isAllowCheckout) {
                        clickPayEvent()
                    } else {
                        showErrorMessage(getString(R.string.need_to_check_declaration))
                    }
                }
            }
            else -> return
        }
    }

    private fun showPromoCode(promoCode: String) {
        getView?.txt_available_promo_code?.visibility = View.GONE
        getView?.rl_promo_code?.visibility = View.VISIBLE
        getView?.txt_promo_code?.text = promoCode
        getView?.btn_remove?.visibility = View.VISIBLE
        getView?.img_remove?.visibility = View.GONE
        getView?.rl_promo_code?.setBackgroundResource(R.drawable.border_corner_black)
        getView?.txt_promo_code?.setTextColor(
            ContextCompat.getColor(
                activity!!,
                R.color.black_color_3
            )
        )
    }

    override fun onPrepareCheckoutSuccess() {
        (activity as MainActivity).cart?.isLocked = true
        (activity as MainActivity).lockedCart = getCart()?.copyCart()
        val token = getToken()
        if (isFreeCourses) {
            presenter?.updateFreePayment(totalCost, cartInfo?.payer)

            //update payment to tks-server
            presenter?.updatePayment(token, null, null)

            onPaymentDone()
        }
    }

    override fun loadCart() {
        val cartId = getCartId()
        if (cartId != null) {
            val token = getToken() ?: ""
            val userId = cartInfo?.payer?.mCustomerId ?: ""
            presenter?.loadCart(token, userId, cartId)
        }
    }

    override fun onPaymentDone() {
        val receiptId = presenter?.sessionCode
        presenter?.navigationPaymentSuccessfulView(
            transactionResponse,
            receiptId,
            totalCost,
            null,
            null,
            quickBookProductType
        )
        hidePayContainer(true)
        (activity as MainActivity).deleteAllCart()
    }

    private fun updatePayerEmail(email: String?) {
        payer?.mEmail = email
        getCartFromMain()?.payer?.mEmail = email
    }

    private fun handleEmailResult(data: Intent?) {
        updatePayerEmail(data?.getStringExtra(EXTRA_EMAIL_ADDRESS))
        proceedToPayment()
    }

    private fun proceedToPayment() {
        if (totalCost?.getTotalPaymentAmount() == null ||
            totalCost?.getTotalPaymentAmount()!! <= 0
        ) {
            handleFreeCourse()
            isFreeCourses = true
        } else {
            showPaymentView()
        }
    }

    private fun handleFreeCourse() {

        val kiosk =
            Gson().fromJson<KioskInfo>(MySharedPref(activity).kioskInfo, KioskInfo::class.java)
        val committeeId = kiosk?.outlet?.getOutletId()
        receipt = ReceiptRequest(
            totalCost?.getShoppingCartID()
                ?: "", "NA", "NA", ReceiptRequest.DEBIT_CREDIT_METHOD, committeeId
        )
        receipt?.txnNo = presenter?.sessionCode
        val token = getToken() ?: ""
        val cartId = (activity as MainActivity).getBookingCartId()
        if (cartId != null && cartInfo?.payer != null) {
            presenter?.prepareCheckout(
                token,
                cartId,
                cartInfo?.payer!!,
                presenter?.sessionCode
            )
        }

    }

    override fun insertLog(item: PaymentRequest) {
        val task = Runnable {
            appDb?.txnLogDao()?.insertLog(item)
            if (item.items != null) {
                appDb?.txnItemDao()?.deleteItems(item.txnNo)
                appDb?.txnItemDao()?.insertItems(item.items!!)
            }
        }
        dbWorkerThread.postTask(task)
    }

    override fun updateLog(item: PaymentRequest) {
        val task = Runnable {
            appDb?.txnLogDao()?.updateLog(item)
            if (item.items != null) {
                appDb?.txnItemDao()?.deleteItems(item.txnNo)
                appDb?.txnItemDao()?.insertItems(item.items!!)
            }
        }
        dbWorkerThread.postTask(task)
    }

    override fun updateReceiptNo(txnNo: String, receiptId: String) {
        val task = Runnable {
            appDb?.txnLogDao()?.updateReceiptId(txnNo, receiptId)
        }
        dbWorkerThread.postTask(task)
    }

    override fun updateStatus(txnNo: String, status: Int) {
        val task = Runnable {
            appDb?.txnLogDao()?.updateStatus(status, txnNo)
        }
        dbWorkerThread.postTask(task)
    }

    override fun updateSignature(txnNo: String, signatureImage: String) {
        val task = Runnable {
            appDb?.txnLogDao()?.updateSignature(txnNo, signatureImage)
        }
        dbWorkerThread.postTask(task)
    }

    override fun updatePdfFileAndSignature(
        txnNo: String,
        pdfFile: String?,
        signatureImage: String
    ) {
        val task = Runnable {
            appDb?.txnLogDao()?.updateSignatureAndPdfFile(txnNo, pdfFile, signatureImage)
        }
        dbWorkerThread.postTask(task)
    }

    override fun deleteReceipt() {
        if (receipt != null) {
            val task = Runnable {
                appDb?.receiptDao()?.delete(receipt!!)
            }
            dbWorkerThread.postTask(task)
        }
    }

    override fun insertReceipt(item: ReceiptRequest) {
        val task = Runnable {
            appDb?.receiptDao()?.insert(item)
        }
        dbWorkerThread.postTask(task)
    }

    override fun updateReceipt(item: ReceiptRequest?) {
        if (item != null) {
            val task = Runnable {
                appDb?.receiptDao()?.updateErrorGenerateReceipt(
                    item.shoppingCartId,
                    item.errorCode,
                    item.errorMessage,
                    item.createdAt,
                    item.errorLatestUpdate
                )
            }
            dbWorkerThread.postTask(task)
        }
    }

    fun groupProductItem(bookingDetail: BookingDetail?): BookingDetail? {
        if (bookingDetail != null && bookingDetail.products != null && bookingDetail.products!!.size > 0) {
            val productList = bookingDetail.products!!
            var i = 0
            var j = 0
            while (i < productList.size) {
                j = i + 1
                while (j < productList.size) {
                    if (true == productList[j].productId?.equals(productList[i].productId) &&
                        true == productList[j].getOutletName()?.trim()
                            ?.uppercase(Locale.ENGLISH)
                            ?.equals(
                                productList[i].getOutletName()?.trim()?.uppercase(Locale.ENGLISH)
                            ) &&
                        true == productList[j].getProductTitle()?.trim()?.uppercase(Locale.ENGLISH)
                            ?.equals(
                                productList[i].getProductTitle()?.trim()?.uppercase(Locale.ENGLISH)
                            ) &&
                        ((true == productList[j].getCustomerId()?.trim()?.uppercase(Locale.ENGLISH)
                            ?.equals(
                                productList[i].getCustomerId()?.trim()?.uppercase(Locale.ENGLISH),
                                false
                            )) ||
                                (productList[j].getCustomerId() == null && productList[i].getCustomerId() == null))
                    ) {

                        productList[i] = groupFacilityValue(productList[i], productList[j])!!

                        productList.removeAt(j)
                        j--
                    }
                    j++
                }
                i++
            }
        }

        return bookingDetail
    }

    override fun onDeleteItemSuccess(index: Int) {
        if (index >= 0 && index < cartSummaryList.size) {
            deleteItemInCart(cartSummaryList[index], null)
            cartSummaryList.removeAt(index)
            cartSummaryAdapter?.notifyDataSetChanged()
        }
        resetCart()
        if (cartSummaryList.size > 0) {
            if (getCartId() != null) {
                val token = getToken() ?: ""
                val userId = cartInfo?.payer?.mCustomerId ?: ""
                presenter?.loadCart(token, userId, getCartId()!!)
            }
        } else {
            LogManager.i("Back cart view due to no item in the cart")
            backCartView()
        }
    }

    override fun bookProduct() {
        if (cartInfo?.items != null && cartInfo?.items!!.size > 0) {
            if (isQuickBookFacility && !isBookingMyself) {
                if (facilityParticipant != null && facilityParticipant?.mFullName?.isNotEmpty() == true) {
                    presenter?.bookingProcess(
                        cartItems = cartInfo?.items as ArrayList<CartItem>?,
                        someoneInfo = facilityParticipant
                    )
                } else {
                    showErrorMessage("No participant info.")
                }
            } else {
                presenter?.bookingProcess(cartInfo?.items as ArrayList<CartItem>?)
            }
        } else {
            backCartView()
        }
    }

}
