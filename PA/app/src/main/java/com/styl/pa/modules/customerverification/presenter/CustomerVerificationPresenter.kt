package com.styl.pa.modules.customerverification.presenter

import androidx.fragment.app.DialogFragment
import android.text.Spanned
import android.text.TextUtils
import androidx.annotation.VisibleForTesting
import com.styl.pa.BuildConfig
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.R
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.api.API
import com.styl.pa.entities.cart.Cart
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.proxy.EmptyRequest
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.entities.proxy.ProxyRequestHeader
import com.styl.pa.entities.scanner.Barcode
import com.styl.pa.modules.base.BaseInteractor
import com.styl.pa.modules.cart.presenter.CartPresenter
import com.styl.pa.modules.customerverification.CustomerVerificationContract
import com.styl.pa.modules.customerverification.interactor.CustomerVerificationInteractor
import com.styl.pa.modules.customerverification.router.CustomerVerificationRouter
import com.styl.pa.modules.scanner.zebraScannerService.DcssdkListener
import com.styl.pa.utils.GeneralTextUtil
import com.styl.pa.utils.GeneralUtils.*
import com.styl.pa.utils.LogManager
import java.util.*
import kotlin.collections.ArrayList

class CustomerVerificationPresenter(var view: CustomerVerificationContract.View?) :
        CustomerVerificationContract.Presenter,
        CustomerVerificationContract.CustomerVerificationOutput, DcssdkListener.DcssdkConfig {

    companion object {

        private val TAG = CustomerVerificationPresenter::class.java.simpleName

        const val ATTENDEE_REQUEST_CODE = 100
        const val PAYER_REQUEST_CODE = 101

        const val ARG_SELECTED_PRODUCT = BuildConfig.APPLICATION_ID + ".args.ARG_SELECTED_PRODUCT"
        const val ARG_CUSTOMER_INFO = BuildConfig.APPLICATION_ID + "args.ARG_CUSTOMER_INFO"
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    constructor(view: CustomerVerificationContract.View?, interactor: CustomerVerificationInteractor?): this(view) {
        this.interactor = interactor
    }

    private var interactor: CustomerVerificationInteractor? = CustomerVerificationInteractor(this)

    private var router: CustomerVerificationRouter? = CustomerVerificationRouter(view as? DialogFragment)

    private var cart: Cart? = null
    private var availableItems: MutableList<CartItem> = ArrayList()

    private var selectedProducts: MutableList<String?> = ArrayList()

    private var customerInfo: CustomerInfo? = null

    var adapterView: CustomerVerificationContract.CourseAdapterView? = null
    var isStop: Boolean = false
    var isPull: Boolean = false

    var isVerifying = false

    private var eventTicketPosition: Int? = null
    private var ticketEntityPosition: Int? = null
    private var participantPosition: Int? = null
    private var cartItemPosition: Int? = null

    @ExcludeFromJacocoGeneratedReport
    override fun setupScannerConfigEvent() {
        view?.setupScannerConfigEvent(this)
        view?.resetScanner()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun dcssdkBarCodeResultEvent(barcode: Barcode?) {
        LogManager.i("Finish scan NRIC/FIN")
        view?.closeScanner()
        if (!TextUtils.isEmpty(barcode?.typeBarcode)) {
            view?.updateNRICCode(barcode?.barcodeDataFormat ?: "")
        } else {
            view?.showMessageRescan(R.string.only_scan_passioncard_or_nric)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    override fun dcssdkPullTriggerEvent(result: Boolean) {
        if (!result && isPull) {
            view?.showErrorMessage(R.string.connect_scanner)
        } else if (result && !isPull) {
            view?.isClickScanQRCode()
        }
    }

    @ExcludeFromJacocoGeneratedReport
    override fun dcssdkConfigAIM(result: Boolean) {
        LogManager.d(TAG, "DcssdkConfigAIM")
    }

    override fun verifyCustomer(token: String?, idNo: String?) {
        if (idNo.isNullOrBlank()) {
            view?.showInvalidId()
            return
        }

        val idNumber = idNo.uppercase(Locale.ENGLISH)
        val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
        val request = ProxyRequest(
                header,
                EmptyRequest(),
                ProxyRequest.GET_METHOD,
                "",
                API.getCustomerDetail(idNumber)
        )

        isVerifying = true

        view?.showLoading()
        nricVerification = idNumber
        interactor?.verifyCustomer(token, request)
    }

    @ExcludeFromJacocoGeneratedReport
    fun navigationCartPage(customerInfo: CustomerInfo?) {
        view?.updatePayerInfoToMain(customerInfo)
        router?.navigateCartPage(
                PAYER_REQUEST_CODE,
                customerInfo,
                selectedProducts.toTypedArray(),
                view?.isCartUpdate() ?: false,
                cartItemPosition
        )
    }

    private var nricVerification: String? = null

    @ExcludeFromJacocoGeneratedReport
    override fun onSuccess(data: CustomerInfo?) {
        isVerifying = false
        customerInfo = data
        view?.dismissLoading()

        if (customerInfo == null) {
            var message = ""
            if (!nricVerification.isNullOrEmpty() && nricVerification!!.length >= 4) {
                message = GeneralTextUtil.maskText(nricVerification, nricVerification!!.length - 3, true)

                message = String.format("%s (%s)", view?.getContext()?.getString(R.string.valid_for_members),
                        message)
            } else {
                message = view?.getContext()?.getString(R.string.valid_for_members) ?: ""
            }
            view?.showMessageRescan(R.string.non_member_title, message, true)
            return
        }

        view?.closeScanner()
        if (view?.isPayer() == true) {
            navigationCartPage(customerInfo)
            return
        }
        if (canAddAttendee()) {
            LogManager.d("CustomerVerificationPresenter: onSuccess canAddAttendee = true, availableItems size = ${availableItems.size}")
            availableItems.forEach item@{ item ->
                item.attendees?.forEach attendee@{ attendee ->
                    if (true == customerInfo?.mCustomerId?.equals(attendee.customerInfo?.mCustomerId)) {
                        val productId = item.getItemId()
                        selectedProducts.add(productId)
                    }
                }
            }
            if (availableItems.size == 1 && cart?.items?.size == 1) {
                if (selectedProducts.isEmpty()) {
                    val item = availableItems[0]
                    val productId = item.getItemId()
                    selectedProducts.add(productId)
                }
                doContinue()
            } else {
                val p = cartItemPosition ?: -1
                val evenInfo = if (p >= 0) cart?.items?.get(p)?.event else null
                if (evenInfo != null){
                    doContinue()
                } else {
                    view?.showCoursePanel(true, customerInfo?.mCustomerId)
                }
            }
        } else {
            view?.updatePayerInfoToMain(customerInfo)
            router?.navigateCartPage(
                    ATTENDEE_REQUEST_CODE,
                    customerInfo,
                    selectedProducts.toTypedArray()
            )
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun canAddAttendee(): Boolean {
        availableItems.forEach item@{ item ->
            if (item.classInfo != null || (item.event != null) || (item.igInfo != null)) {
                return true
            }
        }
        return false
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onError(data: BaseResponse<CustomerInfo>) {
        isVerifying = false
        view?.clearNric()
        view?.dismissLoading()
        if (data.errorCode == BaseInteractor.CONVERT_ERROR) {
            val message = view?.getContext()?.getString(R.string.valid_for_members) ?: ""
            view?.showMessageRescan(R.string.non_member_title, message, true)
        } else {
            view?.showErrorMessage(data)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    override fun setSelected(position: Int, isSelected: Boolean) {
        if (position == availableItems.size) {
            // none option
            selectedProducts.clear()
        } else if (position >= 0 && position < availableItems.size) {
            val item = availableItems.get(position)
            val productId = item.getItemId()

            if (isSelected && !selectedProducts.contains(productId)) {
                selectedProducts.add(productId)
            } else if (!isSelected && selectedProducts.contains(productId)) {
                selectedProducts.remove(productId)
            }
        }
        adapterView?.updateList()
    }

    override fun getItemCount(): Int {
        return availableItems.size
    }

    @ExcludeFromJacocoGeneratedReport
    override fun bindViewHolder(position: Int) {
        if (position == availableItems.size) {
            adapterView?.setNoneOption()

//            var isNoneShown = true
//            cart?.items?.forEach item@{ item ->
//                item.attendees?.forEach attendee@{ attendee ->
//                    if (true == customerInfo?.mCustomerId?.equals(attendee.customerInfo?.mCustomerId)) {
//                        isNoneShown = false
//                        return@item
//                    }
//                }
//            }

            adapterView?.setShowOption(cart?.payer == null)
//            adapterView?.setEnabled(isNoneShown)
            adapterView?.setEnabled(true)

            if (selectedProducts.isNotEmpty()) {
                adapterView?.setChecked(false)
            } else {
                adapterView?.setChecked(true)
            }
        } else if (position >= 0 || position < availableItems.size) {
            val item = availableItems.get(position)

            var isExisting = false
            item.attendees?.forEach { attendee ->
                if (true == customerInfo?.mCustomerId?.equals(attendee.customerInfo?.mCustomerId)) {
                    isExisting = true
                    return@forEach
                }
            }

            adapterView?.setShowOption(true)

            var name: Spanned? = null
            if (item.classInfo != null) {
                // class
                name = item.classInfo?.getDecodedTitle()

                if (CartPresenter.MAX_ATTENDEE <= (item.attendees?.size ?: 0) && !isExisting) {
                    adapterView?.setEnabled(false)
                } else {
                    adapterView?.setEnabled(true)
                }

                val isChecked = selectedProducts.contains(item.classInfo?.getClassId())
                adapterView?.setChecked(isChecked)
            } else if (item.facility != null) {
                // facility
                name = item.facility?.getDecodedName()

                adapterView?.setEnabled(false)
                adapterView?.setChecked(false)
            } else if (item.event != null) {
                // event
                name = item.event?.getDecodedTitle()
                val isChecked = selectedProducts.contains(item.event?.eventId)
                adapterView?.setChecked(isChecked)

                if (CartPresenter.MAX_ATTENDEE <= (item.attendees?.size ?: 0) && !isExisting) {
                    adapterView?.setEnabled(false)
                } else {
                    adapterView?.setEnabled(true)
                }

                /*if (item.event?.registerForMyself == false && item.event?.registerRequired == true) {
                    // group registration with ID required for all participant

                    if (CartPresenter.MAX_ATTENDEE <= (item.attendees?.size ?: 0) && !isExisting) {
                        adapterView?.setEnabled(false)
                    } else {
                        adapterView?.setEnabled(true)
                    }
                } else if (item.event?.registerForMyself == false && item.event?.registerRequired == false) {
                    // group registration without id required
                    adapterView?.setEnabled(false)
                } else if (item.event?.registerForMyself == true && item.event?.registerRequired == true) {
                    // single registration only
                    if (1 <= (item.attendees?.size ?: 0) && !isExisting) {
                        adapterView?.setEnabled(false)
                    } else {
                        adapterView?.setEnabled(true)
                    }
                } else {
                    adapterView?.setEnabled(false)
                }*/
            } else if(item.igInfo != null){
                // class
                name = item.igInfo?.getDecodedTitle()

                if (CartPresenter.MAX_ATTENDEE <= (item.attendees?.size ?: 0) && !isExisting) {
                    adapterView?.setEnabled(false)
                } else {
                    adapterView?.setEnabled(true)
                }

                val isChecked = selectedProducts.contains(item.igInfo?.igId)
                adapterView?.setChecked(isChecked)
            } else {
                adapterView?.setChecked(false)
                adapterView?.setEnabled(false)
            }

            adapterView?.setCourseName(name)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun setCart(cart: Cart?) {
        this.cart = cart
        cart?.items?.forEach item@{ item ->
            if ((item.classInfo != null && (item.attendees?.size
                    ?: 0) < CartPresenter.MAX_ATTENDEE) ||
                (item.event != null) ||
                (item.igInfo != null && (item.attendees?.size
                    ?: 0) < CartPresenter.MAX_ATTENDEE)
            ) {
                availableItems.add(item)
            }
        }
    }

    override fun isPayer(): Boolean {
        return cart?.payer == null
    }

    @ExcludeFromJacocoGeneratedReport
    override fun setAvailableItems(customerId: String?) {
        availableItems.clear()
        cart?.items?.forEach item@{ item ->
            var isExist = false
            item.attendees?.forEach attendee@{ attendee ->

                if (attendee.customerInfo?.mCustomerId.equals(customerId)) {
                    isExist = true
                    return@attendee
                }
            }
            if ((item.classInfo != null && ((item.attendees?.size
                    ?: 0) < CartPresenter.MAX_ATTENDEE || isExist)) ||
                (item.igInfo != null && ((item.attendees?.size
                    ?: 0) < CartPresenter.MAX_ATTENDEE || isExist))
            ) {
                availableItems.add(item)
            }
        }

        selectedProducts.clear()
        availableItems.forEach item@{ item ->
            item.attendees?.forEach attendee@{ attendee ->
                if (true == customerInfo?.mCustomerId?.equals(attendee.customerInfo?.mCustomerId)) {
                    val productId = item.getItemId()
                    selectedProducts.add(productId)
                }
            }
        }
    }

    @ExcludeFromJacocoGeneratedReport
    override fun setParamsForEvent(
        eventTicketPosition: Int?,
        ticketEntityPosition: Int?,
        participantPosition: Int?,
        cartItemPosition: Int?
    ) {
        this.eventTicketPosition = eventTicketPosition
        this.ticketEntityPosition = ticketEntityPosition
        this.participantPosition = participantPosition
        this.cartItemPosition = cartItemPosition
    }

    @ExcludeFromJacocoGeneratedReport
    override fun doContinue() {
        LogManager.d("CustomerVerificationPresenter: doContinue")
        router?.navigateIndemnityView(
                ATTENDEE_REQUEST_CODE,
                customerInfo,
                selectedProducts.toTypedArray(),
            eventTicketPosition,
            ticketEntityPosition,
            participantPosition,
            cartItemPosition
        )
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        interactor?.onDestroy()
        router = null
        interactor = null
    }
}
