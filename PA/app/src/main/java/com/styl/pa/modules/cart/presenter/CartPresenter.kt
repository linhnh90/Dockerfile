package com.styl.pa.modules.cart.presenter

import android.text.TextUtils
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.DialogFragment
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.R
import com.styl.pa.adapters.ParticipantAdapter
import com.styl.pa.adapters.ParticipantCartEventAdapter
import com.styl.pa.adapters.TicketAdapter
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.api.API
import com.styl.pa.entities.cart.Attendee
import com.styl.pa.entities.cart.Cart
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.checkResidency.CheckResidencyResponse
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.event.TicketEntity
import com.styl.pa.entities.generateToken.BookingResponse
import com.styl.pa.entities.pacesRequest.*
import com.styl.pa.entities.participant.ParticipantResponse
import com.styl.pa.entities.proxy.EmptyRequest
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.entities.proxy.ProxyRequestHeader
import com.styl.pa.entities.scanner.Barcode
import com.styl.pa.entities.vacancy.VacancyResponse
import com.styl.pa.modules.addParticipantEvent.view.AddParticipantEventFragment
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.cart.ICartContact
import com.styl.pa.modules.cart.interactor.CartInteractor
import com.styl.pa.modules.cart.router.CartRouter
import com.styl.pa.modules.cart.view.CartFragment
import com.styl.pa.modules.customerverification.presenter.CustomerVerificationPresenter
import com.styl.pa.modules.dialog.MessageDialogFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.scanner.zebraScannerService.DcssdkListener
import com.styl.pa.modules.selectTicketType.view.SelectTicketTypeFragment
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.LogManager
import org.jetbrains.annotations.TestOnly
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Ngatran on 09/14/2018.
 */
class CartPresenter(var view: ICartContact.IView?) : ICartContact.IPresenter,
    DcssdkListener.DcssdkConfig {

    @TestOnly
    constructor(view: ICartContact.IView?, interactor: CartInteractor?) : this(view) {
        this.interactor = interactor
    }

    companion object {
        private val TAG = CartPresenter::class.java.simpleName
        const val MAX_ATTENDEE = 4
        const val MAX_NUMBER_TICKET = 10
    }

    private var interactor: CartInteractor? = CartInteractor()

    private var router: CartRouter? = CartRouter(view as? BaseFragment)

    var cartAdapterView: ICartContact.ICartAdapterView? = null

    private var cart: Cart? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var newCart: Cart? = null

    private var isPullResult: Boolean = false

    private var cartItemPosNeedAddParticipant = -1
    private var shouldShowParticipantErr = false

    val participantErr = StringBuilder()

    @ExcludeFromJacocoGeneratedReport
    override fun navigationCheckoutView(
        isCartUpdate: Boolean,
        isBookingMyself: Boolean,
        facilityParticipant: CustomerInfo?
    ) {
        router?.navigationCheckoutView(isCartUpdate, isBookingMyself, facilityParticipant)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigateIndemnityView() {
        router?.navigateIndemnityView()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun getScannerConfigEvent(): DcssdkListener.DcssdkConfig {
        return this
    }

    @ExcludeFromJacocoGeneratedReport
    override fun dcssdkBarCodeResultEvent(barcode: Barcode?) {
        if (isPullResult) {
            view?.dismissScannerDialog()

            if (TextUtils.isEmpty(barcode?.typeBarcode)) {
                view?.showErrorMessageTitle(
                    R.string.only_scan_passioncard_or_nric,
                    R.string.invalid_bardcode
                )
            }
        }
    }

    @ExcludeFromJacocoGeneratedReport
    override fun dcssdkPullTriggerEvent(result: Boolean) {
        isPullResult = result
        view?.setPullResult(result)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun dcssdkConfigAIM(result: Boolean) {
        LogManager.d(TAG, "DcssdkConfigAIM")
    }

    override fun getCartItemCount(): Int {
        return cart?.items?.size ?: 0
    }

    @ExcludeFromJacocoGeneratedReport
    override fun bindCartViewHolder(position: Int) {
        if (position < 0 || position >= cart?.items?.size ?: 0) {
            return
        }
        val item = cart?.items?.get(position)

        var imageUrl: String? = null
        var name: String? = null
        val info = StringBuilder()
        var outletName = ""
        var date = ""
        var time = ""

        if (item?.classInfo != null) {
            // class
            imageUrl = item.classInfo?.getImageURL()

            name = item.classInfo?.getDecodedTitle()
                .toString() + " (" + item.classInfo?.getClassCode() + ")"

            var startTime = ""
            var endTime = ""
            var weekday = ""
            if (true == item.classInfo?.getClassSessions()?.isNotEmpty()) {
                val classSession = item.classInfo?.getClassSessions()?.get(0)
                startTime = GeneralUtils.formatToTime(classSession?.getStartTime())
                weekday = GeneralUtils.formatShortDay(classSession?.getStartTime())

                endTime = GeneralUtils.formatToTime(classSession?.getEndTime())
            }

            val startDate = GeneralUtils.formatToDate(item.classInfo?.getStartDate())
            val endDate = GeneralUtils.formatToDate(item.classInfo?.getEndDate())
            val outletName = item.classInfo?.getOutletName()

            info.append(startDate)
                .append(" - ")
                .append(endDate)
                .append("\n")
                .append(startTime)
                .append(" - ")
                .append(endTime)
                .append(" (" + weekday + ")")
                .append("\n")
                .append(outletName)

            // set visibility
            cartAdapterView?.showAttendeePanel(true)
            cartAdapterView?.showEventPanel(false)

            cartAdapterView?.showPointerAttendee(item.attendees?.size ?: 0 == 0)
            if (MAX_ATTENDEE <= (item.attendees?.size ?: 0)) {
                cartAdapterView?.showAddButton(false)
            } else {
                cartAdapterView?.showAddButton(true)
            }
            cartAdapterView?.showPointerAttendee(item.attendees?.size ?: 0 == 0)
            cartAdapterView?.setMarginAddAttendeeButton(item.attendees?.size ?: 0 > 0)

            if (item.classInfo?.isSkillsFutureEnabled == true) {
                cartAdapterView?.showSkillFuture(true)
            } else {
                cartAdapterView?.showSkillFuture(false)
            }
            cartAdapterView?.visibleDescription(
                isShowTxtInfo = true,
                isShowLLDesc = false
            )
        } else if (item?.igInfo != null) {

            imageUrl = item.igInfo?.imageURL

            name = item.igInfo?.getDecodedTitle().toString() + " (" + item.igInfo?.igCode + ")"

            outletName = item.igInfo?.outletName ?: ""

            // set visibility
            cartAdapterView?.showAttendeePanel(true)
            cartAdapterView?.showEventPanel(false)

            cartAdapterView?.showPointerAttendee((item.attendees?.size ?: 0) == 0)
            if (MAX_ATTENDEE <= (item.attendees?.size ?: 0)) {
                cartAdapterView?.showAddButton(false)
            } else {
                cartAdapterView?.showAddButton(true)
            }
            cartAdapterView?.showPointerAttendee((item.attendees?.size ?: 0) == 0)
            cartAdapterView?.setMarginAddAttendeeButton((item.attendees?.size ?: 0) > 0)
            cartAdapterView?.showSkillFuture(false)
            cartAdapterView?.visibleDescription(
                isShowTxtInfo = false,
                isShowLLDesc = true,
                isShowTvOutlet = true,
                isShowTvDate = false,
                isShowTvTime = false
            )

        } else if (item?.facility != null) {
            // facility
            imageUrl = item.facility?.getImageUrl()

            name = item.facility?.getDecodedName().toString()

            val startDate = GeneralUtils.formatToDate(item.selectedDate)
            info.append(startDate)
                .append("\n")
            item.slotList?.forEach { slotList ->
                info.append(slotList.mTimeRageName)
                info.append("\n")
            }
            val outletName = item.facility?.outletName
            info.append(outletName)

            // set visibility
            cartAdapterView?.showAttendeePanel(false)
            cartAdapterView?.showEventPanel(false)
            cartAdapterView?.visibleDescription(
                isShowTxtInfo = true,
                isShowLLDesc = false
            )
        } else if (item?.event != null) {
            // event
            imageUrl = item.event?.getImageUrl()

            name = item.event?.getDecodedTitle().toString() + " (" + item.event?.eventCode + ")"

            val startDate = GeneralUtils.formatToDate(item.event?.dateFrom)
            val endDate = GeneralUtils.formatToDate(item.event?.dateTo)
            var startTime = ""
            var endTime = ""
            if (item.event?.classTickets?.isNotEmpty() == true) {
                val ticket = item.event?.classTickets?.get(0)
                startTime = GeneralUtils.formatToTime(ticket?.beginDate)
                endTime = GeneralUtils.formatToTime(ticket?.endDate)
            }
            outletName = item.event?.outletName ?: ""
            date = "$startDate - $endDate"
            time = "$startTime - $endTime"


            cartAdapterView?.visibleDescription(
                isShowTxtInfo = false,
                isShowLLDesc = true,
                isShowTvOutlet = true,
                isShowTvDate = true,
                isShowTvTime = true
            )
            cartAdapterView?.showAttendeePanel(true)
            cartAdapterView?.showEventPanel(false)
            cartAdapterView?.showPointerAttendee((item.event?.listSelectedTicket?.size ?: 0) == 0)
            cartAdapterView?.setPointerText(text = (view as? CartFragment)?.getString(R.string.please_select_ticket_type) ?: "")
            cartAdapterView?.setMarginAddAttendeeButton((item.attendees?.size ?: 0) > 0)
            cartAdapterView?.showAddButton(true)
            val textAddBtn = if ((item.event?.listSelectedTicket?.size ?: 0) == 0)
                    (view as? CartFragment)?.getString(R.string.select_ticket_type_btn)
                    else (view as? CartFragment)?.getString(R.string.add_ticket_type_btn)
            cartAdapterView?.setTextAddButton(textBtn = textAddBtn ?: "")

        }

        cartAdapterView?.loadImage(imageUrl)
        cartAdapterView?.setProductName(name)
        cartAdapterView?.setInfo(info.toString())
        cartAdapterView?.setLLDescInfo(outletName, date, time)
        cartAdapterView?.setNoAttendee((item?.noOfEvent ?: 0).toString())
        if (item?.event != null) {
            cartAdapterView?.setParticipantCartEventAdapter(
                item = item,
                listener = object : ParticipantCartEventAdapter.OnParticipantCartEventListener {
                    @ExcludeFromJacocoGeneratedReport
                    override fun onClickBtnAddTicket(eventTicketPosition: Int) {
                        val eventTicket = item.event?.listSelectedTicket?.get(eventTicketPosition)
                        if (eventTicket != null) {
                            LogManager.d("onClickBtnAddTicket: add 1 ticket for ${eventTicket.name}, selectedQty = ${eventTicket.selectedQty}")
                            //check limit ticket depend on EventTicket
                            val isMaxLimitQty = SelectTicketTypeFragment.isMaxTicketLimit(
                                currentQty = eventTicket.selectedQty ?: 0,
                                maxQty = eventTicket.maxQty ?: 0,
                                minQty = eventTicket.minQty ?: 0,
                                availableQty = eventTicket.availableQty ?: 0
                            )
                            if (isMaxLimitQty) {
                                view?.showErrorMessage(R.string.msg_max_ticket_event)
                            } else {
                                //add new ticketEntity
                                val newParticipantList =
                                    (view as? CartFragment)?.generateListParticipantOfTicketEntity(
                                        isAllParticipantRequired = eventTicket.isAllParticipantRequired,
                                        ticketMapCount = eventTicket.ticketMapCount
                                    )
                                val ticketEntity = TicketEntity()
                                val newSelectedQty =
                                    (eventTicket.listTicketParticipantEntity.size) + 1
                                ticketEntity.listParticipant = newParticipantList ?: ArrayList()

                                eventTicket.selectedQty = newSelectedQty
                                eventTicket.listTicketParticipantEntity.add(ticketEntity)

                                cart?.items?.get(position)
                                    ?.event?.listSelectedTicket?.set(
                                        eventTicketPosition,
                                        eventTicket
                                    )

                                setCart(cart)
                                cartAdapterView?.updateList()
                            }

                        }
                    }

                    @ExcludeFromJacocoGeneratedReport
                    override fun expandContent(isExpand: Boolean, eventTicketPosition: Int) {
                        item.event?.listSelectedTicket?.get(eventTicketPosition)?.isExpandContent =
                            !isExpand
                    }

                },
                ticketAdapterListener = object : TicketAdapter.OnTicketAdapterListener {
                    @ExcludeFromJacocoGeneratedReport
                    override fun onBtnRemoveTicket(
                        eventTicketPosition: Int,
                        ticketEntityPosition: Int
                    ) {
                        val context = (view as? CartFragment)?.activity
                        val eventTicket = item.event?.listSelectedTicket?.get(eventTicketPosition)
                        if (eventTicket != null) {
                            val totalTicketEntity = eventTicket.listTicketParticipantEntity.size
                            if (totalTicketEntity > 0) {
                                //show dialog confirm
                                val dialog = MessageDialogFragment.newInstance(
                                    R.string.error,
                                    R.string.are_you_sure_to_remove_this_ticket
                                )
                                dialog.setMultiChoice(
                                    context?.getString(R.string.no),
                                    context?.getString(R.string.yes)
                                )
                                dialog.setListener(object :
                                    MessageDialogFragment.MessageDialogListener {
                                    @ExcludeFromJacocoGeneratedReport
                                    override fun onPositiveClickListener(dialogFragment: DialogFragment) {
                                        //do remove
                                        LogManager.d("onBtnRemoveTicket: remove Ticket-${ticketEntityPosition + 1} of ticket type: ${eventTicket.name}")
                                        if((context as? MainActivity)?.getBookingCart()?.hasReservation != true){
                                            if (totalTicketEntity == 1) { // last ticket -> remove event ticket
                                                removeEventTicket(
                                                    cartItemPosition = position,
                                                    eventTicketPosition = eventTicketPosition
                                                )
                                            } else {
                                                //remove ticketEntity
                                                removeTicketEntity(
                                                    cartItemPosition = position,
                                                    eventTicketPosition = eventTicketPosition,
                                                    ticketEntityPosition = ticketEntityPosition
                                                )
                                            }

                                        } else {
                                            val token = (view as? CartFragment)?.getToken() ?: ""
                                            val cartId = (view as? CartFragment)?.getCartId() ?: ""
                                            if(totalTicketEntity == 1){ // last ticket -> remove event ticket
                                                removeEventItemEventTicketFromCart(
                                                    token = token,
                                                    cartId = cartId,
                                                    cartItemPosition = position,
                                                    eventTicketPosition = eventTicketPosition
                                                )
                                            } else {
                                                removeEventItemTicketEntityFromCart(
                                                    token = token,
                                                    cartId = cartId,
                                                    cartItemPosition = position,
                                                    eventTicketPosition = eventTicketPosition,
                                                    ticketEntityPosition = ticketEntityPosition
                                                )
                                            }

                                        }

                                    }

                                    @ExcludeFromJacocoGeneratedReport
                                    override fun onNeutralClickListener(dialogFragment: DialogFragment) {
                                        LogManager.d(CartFragment.TAG, "onNeutralClickListener")
                                    }

                                    @ExcludeFromJacocoGeneratedReport
                                    override fun onNegativeClickListener(dialogFragment: DialogFragment) {
                                        LogManager.d(CartFragment.TAG, "onNegativeClickListener")
                                    }
                                })
                                if (context?.supportFragmentManager != null) {
                                    dialog.show(
                                        context.supportFragmentManager,
                                        MessageDialogFragment::class.java.simpleName
                                    )
                                }
                            }
                        }


                    }

                },
                participantAdapterListener = object :
                    ParticipantAdapter.OnParticipantAdapterListener {
                    @ExcludeFromJacocoGeneratedReport
                    override fun onClickBtnEdit(
                        eventTicketPosition: Int,
                        ticketEntityPosition: Int,
                        eventParticipantPosition: Int
                    ) {
                        //event edit EventParticipant
                        val eventTicket = item.event?.listSelectedTicket?.get(eventTicketPosition)
                        val ticketEntity =
                            eventTicket?.listTicketParticipantEntity?.get(ticketEntityPosition)
                        val eventParticipant =
                            ticketEntity?.listParticipant?.get(eventParticipantPosition)
                        LogManager.d(
                            "onClickBtnEdit: edit customer: ${eventParticipant?.nameToShow ?: "position $eventParticipantPosition"}," +
                                    " Ticket-${ticketEntityPosition + 1} of ticket type: ${eventTicket?.name} , ticketEntityPosition = $ticketEntityPosition" +
                                    "eventFormData = ${item.event?.eventFormData}"
                        )
                        //if new -> show screen scan participant
                        if (eventParticipant?.isNew == true) {
                            this@CartPresenter.navigateCustomerVerificationView(
                                requestCode = CustomerVerificationPresenter.ATTENDEE_REQUEST_CODE,
                                isPayer = false,
                                cartItemPosition = position,
                                eventTicketPosition = eventTicketPosition,
                                ticketEntityPosition = ticketEntityPosition,
                                participantPosition = eventParticipantPosition
                            )
                        } else {
                            //if participant has not updated event form -> navigate to AddParticipantEventFragment
                            if (eventParticipant?.participantInfo?.listNotDefined.isNullOrEmpty()){
                                view?.showErrorMessage(R.string.attendee_form_not_available)
                            } else {
                                this@CartPresenter.navigateAddParticipantEvent(
                                    requestCode = AddParticipantEventFragment.ADD_PARTICIPANT_REQUEST_CODE,
                                    eventInfo = item.event,
                                    cartItemPosition = position,
                                    eventTicketPosition = eventTicketPosition,
                                    ticketEntityPosition = ticketEntityPosition,
                                    participantPosition = eventParticipantPosition
                                )
                            }

                        }
                    }
                }

            )
        } else {
            cartAdapterView?.setAttendeeAdapter(item)
        }
        view?.showButtonProceedCheckout(canGoToProceedCheckout())
    }

    @ExcludeFromJacocoGeneratedReport
    private fun removeTicketEntity(
        cartItemPosition: Int,
        eventTicketPosition: Int,
        ticketEntityPosition: Int
    ){
        //remove ticketEntity
        val eventTicket = cart?.items?.get(cartItemPosition)?.event?.listSelectedTicket?.get(eventTicketPosition)
        val currentQty = eventTicket?.selectedQty ?: 0
        if (eventTicket != null) {
            eventTicket.listTicketParticipantEntity.removeAt(
                ticketEntityPosition
            )
            eventTicket.selectedQty = currentQty - 1

            cart?.items?.get(cartItemPosition)
                ?.event?.listSelectedTicket?.set(
                    eventTicketPosition,
                    eventTicket
                )

            setCart(cart)
            cartAdapterView?.updateList()
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun removeEventTicket(
        cartItemPosition: Int,
        eventTicketPosition: Int
    ){
        val eventTicket = cart?.items?.get(cartItemPosition)?.event?.listSelectedTicket?.get(eventTicketPosition)
        if (eventTicket != null) {
            cart?.items?.get(cartItemPosition)
                ?.event?.listSelectedTicket?.removeAt(
                    eventTicketPosition
                )

            if ((cart?.items?.get(cartItemPosition)?.event?.listSelectedTicket?.size ?: 0) == 0){
                //event do not have any ticket type -> clear listEventTicketAvailable
                cart?.items?.get(cartItemPosition)
                    ?.event?.listEventTicketAvailable?.clear()
            }

            setCart(cart)
            cartAdapterView?.updateList()
        }
    }

    @ExcludeFromJacocoGeneratedReport
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldItemPosition < 0 || oldItemPosition >= cart?.items?.size ?: 0 ||
            newItemPosition < 0 || newItemPosition >= newCart?.items?.size ?: 0
        ) {
            return false
        }
        return cart?.items?.get(oldItemPosition)
            ?.id.equals(newCart?.items?.get(newItemPosition)?.id)
    }

    override fun getOldListSize(): Int {
        return cart?.items?.size ?: 0
    }

    override fun getNewListSize(): Int {
        return newCart?.items?.size ?: 0
    }

    @ExcludeFromJacocoGeneratedReport
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldItemPosition < 0 || oldItemPosition >= cart?.items?.size ?: 0 ||
            newItemPosition < 0 || newItemPosition >= newCart?.items?.size ?: 0
        ) {
            return false
        }
        return cart?.items?.get(oldItemPosition) == (newCart?.items?.get(newItemPosition))
    }

    @ExcludeFromJacocoGeneratedReport
    override fun decreaseNoAttendee(position: Int) {
        val currentNoAttendee = cart?.items?.get(position)?.noOfEvent ?: 0
        if (currentNoAttendee > 0) {
            newCart?.items?.get(position)?.noOfEvent = currentNoAttendee - 1

            cartAdapterView?.updateList()

            cart?.items?.get(position)?.noOfEvent = currentNoAttendee - 1
        }
    }

    @ExcludeFromJacocoGeneratedReport
    override fun increaseNoAttendee(position: Int) {
        val currentNoAttendee = cart?.items?.get(position)?.noOfEvent ?: 0
        if (currentNoAttendee < MAX_NUMBER_TICKET) {
            newCart?.items?.get(position)?.noOfEvent = currentNoAttendee + 1

            cartAdapterView?.updateList()

            cart?.items?.get(position)?.noOfEvent = currentNoAttendee + 1
        } else {
            view?.showErrorMessage(R.string.max_ten_attendee)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun updatePayer(customerInfo: CustomerInfo?) {
        newCart?.payer = customerInfo
        cart?.payer = customerInfo
        view?.displayPayer(cart)
    }

    private fun getDeleteParticipantResult(
        deletedProductPosition: Int,
        deletedAttendeePosition: Int
    ): IBaseContract.IBaseInteractorOutput<ParticipantResponse> {
        return object : IBaseContract.IBaseInteractorOutput<ParticipantResponse> {
            @ExcludeFromJacocoGeneratedReport
            override fun onSuccess(data: ParticipantResponse?) {
                view?.dismissLoading()
                deleteParticipant(deletedProductPosition, deletedAttendeePosition)
            }

            @ExcludeFromJacocoGeneratedReport
            override fun onError(data: BaseResponse<ParticipantResponse>) {
                view?.dismissLoading()
                view?.showErrorMessage(data)
            }
        }
    }

    fun deleteParticipant(cartItem: CartItem?, position: Int) {
        val itemPosition = cart?.items?.indexOf(cartItem)
        if (itemPosition == null || itemPosition < 0 ||
            position < 0 || position >= cart?.items?.get(itemPosition)?.attendees?.size ?: 0
        ) {
            return
        }
        deleteParticipant(itemPosition, position)
    }

    @ExcludeFromJacocoGeneratedReport
    private fun deleteParticipant(productPos: Int, attendeePos: Int) {
        newCart?.items?.get(productPos)?.attendees?.removeAt(attendeePos)
        cartAdapterView?.updateList()
        cart?.items?.get(productPos)?.attendees?.removeAt(attendeePos)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getItemId(cartItem: CartItem?): String? {
        return cartItem?.getSku()
    }

    override fun deleteParticipant(token: String, cartItem: CartItem?, position: Int) {
        val itemPosition = cart?.items?.indexOf(cartItem)
        if (itemPosition == null || itemPosition < 0 ||
            position < 0 || position >= cart?.items?.get(itemPosition)?.attendees?.size ?: 0
        ) {
            return
        }
        if (view?.getPayer() == null || cart?.isLocked == true) {
            deleteParticipant(itemPosition, position)
        } else {
            val attendee = newCart?.items?.get(itemPosition)?.attendees?.get(position)
            val participantUserId = ParticipantUserId(attendee?.customerInfo?.mCustomerId)
            val cartId = view?.getCartId()
            val itemId = getItemId(cartItem)
            if (!itemId.isNullOrEmpty()) {
                view?.showLoading()
                val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
                val participantRequest =
                    ParticipantRequest(cartId, itemId, arrayListOf(participantUserId))
                if (cartItem?.igInfo != null) {
                    val request = ProxyRequest(
                        header,
                        participantRequest,
                        ProxyRequest.POST_METHOD,
                        "",
                        API.uriDeleteParicipantIg
                    )
                    interactor?.deleteParticipantIg(
                        token,
                        cart?.payer?.mCustomerId ?: "",
                        request,
                        getDeleteParticipantResult(itemPosition, position)
                    )
                } else {
                    val request = ProxyRequest(
                        header,
                        participantRequest,
                        ProxyRequest.POST_METHOD,
                        "",
                        API.uriDeleteParicipant
                    )
                    interactor?.deleteParticipant(
                        token, cart?.payer?.mCustomerId ?: "", request,
                        getDeleteParticipantResult(itemPosition, position)
                    )
                }
            }
        }
    }

    override fun updateAttendee(
        token: String?,
        customerInfo: CustomerInfo?,
        selectedProducts: LinkedHashMap<String?, Boolean>
    ) {

        val cartId = view?.getCartId()
        updateCartAttendee(cartId, customerInfo, selectedProducts)

    }

    private var itemsUpdatedSuccess = TreeMap<Int, Int?>()

    @ExcludeFromJacocoGeneratedReport
    private fun onFinishUpdateParticipant(
        customerInfo: CustomerInfo?,
        selectedProducts: LinkedHashMap<String?, Boolean>
    ) {
        view?.dismissLoading()
        if (shouldShowParticipantErr) {
            view?.showErrorParticipantExist(
                cart?.items?.get(cartItemPosNeedAddParticipant),
                customerInfo
            )
            shouldShowParticipantErr = false
            cartItemPosNeedAddParticipant = -1
        }
        updateCart(customerInfo, selectedProducts)
    }

    fun getItemName(cartItem: CartItem?): String? {
        return when {
            cartItem?.classInfo != null -> {
                cartItem.classInfo?.getClassTitle()
            }
            cartItem?.igInfo != null -> {
                cartItem.igInfo?.igTitle
            }
            cartItem?.event != null -> {
                cartItem.event?.eventTitle
            }
            else -> ""
        }
    }

    private fun getUpdateParticipantOutput(
        customerInfo: CustomerInfo?,
        selectedProducts: LinkedHashMap<String?, Boolean>,
        position: Int,
        participantRemovedPosition: Int?,
        isAdded: Boolean
    ): IBaseContract.IBaseInteractorOutput<ParticipantResponse> {
        return object : IBaseContract.IBaseInteractorOutput<ParticipantResponse> {
            override fun onSuccess(data: ParticipantResponse?) {
                itemsUpdatedSuccess.put(position, participantRemovedPosition)
                if (!isAdded) {
                    --deletedItemCount
                }
                if (addedItemCount == 0 && deletedItemCount == 0) {
                    onFinishUpdateParticipant(customerInfo, selectedProducts)
                }
            }

            override fun onError(data: BaseResponse<ParticipantResponse>) {
                if (!isAdded) {
                    --deletedItemCount
                }
                if (addedItemCount == 0 && deletedItemCount == 0) {
                    view?.showErrorMessage(data)
                    onFinishUpdateParticipant(customerInfo, selectedProducts)
                }
            }
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun updateCart(
        customerInfo: CustomerInfo?,
        selectedProducts: LinkedHashMap<String?, Boolean>,
        cart: Cart?,
        listToUpdate: TreeMap<Int, Int?>
    ) {
        for (item in listToUpdate) {
            val cartPosition = item.key
            val participantRemovedPosition = item.value
            var attendees = cart?.items?.get(cartPosition)?.attendees
            if (participantRemovedPosition == null) {
                val attendee = Attendee(null, customerInfo, null)
                val cartItem = cart?.items?.get(cartPosition)
                var itemId: String? = null
                if (cartItem?.classInfo != null) {
                    itemId = cartItem.classInfo?.getClassId()
                } else if (cartItem?.igInfo != null) {
                    itemId = cartItem.igInfo?.igId
                } else if (cartItem?.event != null) {
                    itemId = cartItem.event?.eventId
                    if (cartItem.noOfEvent != null && cartItem.isIndemnity != true) {
                        cart.items?.get(cartPosition)?.isIndemnity = true
                    }
                }
                attendee.isIndemnity = selectedProducts[itemId] ?: false

                if (attendees == null) {
                    attendees = ArrayList()
                    attendees.add(attendee)
                    cart?.items?.get(cartPosition)?.attendees = attendees
                } else {
                    attendees.add(attendee)
                }
            } else {
                cart?.items?.get(cartPosition)?.attendees?.removeAt(participantRemovedPosition)
            }
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun updateCart(
        customerInfo: CustomerInfo?,
        selectedProducts: LinkedHashMap<String?, Boolean>
    ) {
        if (itemsUpdatedSuccess.size > 0) {
            updateCart(customerInfo, selectedProducts, newCart, itemsUpdatedSuccess)
            cartAdapterView?.updateList()
            updateCart(customerInfo, selectedProducts, cart, itemsUpdatedSuccess)
            itemsUpdatedSuccess.clear()
        }
        addedItemCount = -1
        deletedItemCount = -1
    }

    private var addedItemCount = -1
    private var deletedItemCount = -1
    private fun updateParticipant(
        customerInfo: CustomerInfo?,
        selectedProducts: LinkedHashMap<String?, Boolean>,
        cartId: String?,
        listToUpdate: MutableList<Int>,
        isAdded: Boolean
    ) {
        val token = view?.getToken() ?: ""
        view?.showLoading()
        listToUpdate.forEach { position ->
            val cartItem = newCart?.items?.get(position)
            val itemId = getItemId(cartItem)
            if (isAdded) {
                if (!itemId.isNullOrEmpty()) {
                    --addedItemCount
                    itemsUpdatedSuccess.put(position, null)
                    if (addedItemCount == 0 && deletedItemCount == 0) {
                        onFinishUpdateParticipant(customerInfo, selectedProducts)
                    }
                }
            } else {
                if (newCart?.items?.get(position)?.attendees != null) {
                    var removedPosition = -1
                    for (i in newCart?.items?.get(position)?.attendees!!.indices) {
                        val attendee = newCart?.items?.get(position)?.attendees?.get(i)
                        if (attendee?.customerInfo?.mCustomerId.equals(customerInfo?.mCustomerId)) {
                            removedPosition = i
                            break
                        }
                    }
                    if (removedPosition >= 0 && !itemId.isNullOrEmpty()) {
                        val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
                        val participantUserId = ParticipantUserId(customerInfo?.mCustomerId)
                        val participantRequest =
                            ParticipantRequest(cartId, itemId, arrayListOf(participantUserId))
                        if (cartItem?.igInfo != null) {
                            val proxyRequestDeleteParticipant = ProxyRequest(
                                header,
                                participantRequest,
                                ProxyRequest.POST_METHOD,
                                "",
                                API.uriDeleteParicipantIg
                            )
                            interactor?.deleteParticipantIg(
                                token,
                                cart?.payer?.mCustomerId ?: "",
                                proxyRequestDeleteParticipant,
                                getUpdateParticipantOutput(
                                    customerInfo,
                                    selectedProducts,
                                    position,
                                    removedPosition,
                                    isAdded
                                )
                            )
                        } else {
                            val proxyRequestDeleteParticipant = ProxyRequest(
                                header,
                                participantRequest,
                                ProxyRequest.POST_METHOD,
                                "",
                                API.uriDeleteParicipant
                            )
                            interactor?.deleteParticipant(
                                token,
                                cart?.payer?.mCustomerId ?: "",
                                proxyRequestDeleteParticipant,
                                getUpdateParticipantOutput(
                                    customerInfo,
                                    selectedProducts,
                                    position,
                                    removedPosition,
                                    isAdded
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun updateCartAttendee(
        cartId: String?,
        customerInfo: CustomerInfo?,
        selectedProducts: LinkedHashMap<String?, Boolean>
    ) {
        val listToAdd = getItems(newCart, true, customerInfo, selectedProducts)
        val listToRemove = getItems(newCart, false, customerInfo, selectedProducts)

        addedItemCount = listToAdd.size
        deletedItemCount = listToRemove.size

        if (newCart?.hasReservation != true) {
            listToAdd.forEach { position ->
                itemsUpdatedSuccess[position] = null
            }
            listToRemove.forEach { position ->
                if (newCart?.items?.get(position)?.attendees != null) {
                    var removedPosition = -1
                    for (i in newCart?.items?.get(position)?.attendees!!.indices) {
                        val attendee = newCart?.items?.get(position)?.attendees?.get(i)
                        if (attendee?.customerInfo?.mCustomerId.equals(customerInfo?.mCustomerId)) {
                            removedPosition = i
                            break
                        }
                    }
                    if (removedPosition >= 0) {
                        itemsUpdatedSuccess[position] = removedPosition
                    }
                }
            }
            onFinishUpdateParticipant(customerInfo, selectedProducts)
        } else {
            if (listToAdd.size == 0) {
                if (shouldShowParticipantErr) {
                    view?.showErrorParticipantExist(
                        cart?.items?.get(cartItemPosNeedAddParticipant),
                        customerInfo
                    )
                }
            } else {
                updateParticipant(customerInfo, selectedProducts, cartId, listToAdd, true)
            }

            if (listToRemove.size > 0) {
                updateParticipant(customerInfo, selectedProducts, cartId, listToRemove, false)
            }
        }
    }

    private fun getItems(
        cart: Cart?,
        isAdded: Boolean,
        customerInfo: CustomerInfo?,
        selectedProducts: LinkedHashMap<String?, Boolean>?
    ): MutableList<Int> {
        val listItems: MutableList<Int> = ArrayList()
        if (cart?.items != null) {
            for (i in cart.items!!.indices) {
                val item = cart.items!!.get(i)
                val productId = item.getItemId()
                if (isAdded == selectedProducts?.containsKey(productId)) {
                    LogManager.d("CArtPresenter: getItems : productID = $productId")
                    var isExisting = false
                    item.attendees?.forEach { attendee ->
                        if (true == attendee.customerInfo?.mCustomerId?.equals(customerInfo?.mCustomerId)) {
                            val isIndemnity = selectedProducts[productId] ?: false
                            if (attendee.isIndemnity != isIndemnity) {
                                val attendeeIndex = item.attendees?.indexOf(attendee) ?: -1
                                updateIndemnity(this.newCart, i, attendeeIndex, isIndemnity)
                                updateIndemnity(this.cart, i, attendeeIndex, isIndemnity)
                            }
                            isExisting = true
                            if (i == cartItemPosNeedAddParticipant) {
                                shouldShowParticipantErr = true
                            }
                            return@forEach
                        }
                    }
                    if ((isAdded && !isExisting) || (!isAdded && isExisting)) {
                        listItems.add(i)
                    }
                }
            }
        }
        if (listItems.size == 0 && isAdded) {
            cartAdapterView?.updateList()
        }
        return listItems
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun updateIndemnity(cart: Cart?, pos: Int, attendeeIndex: Int, isAccept: Boolean) {
        if (attendeeIndex != -1) {
            val cartAttendees = cart?.items?.get(pos)?.attendees
            cartAttendees?.get(attendeeIndex)?.isIndemnity = isAccept
        }
    }

    override fun canProceed(): Boolean {
        if (cart?.payer == null) {
            return false
        }
        cart?.items?.forEach { item ->

            // class, non-ticket event, single event without attendee
            if (((item.classInfo != null || item.igInfo != null) &&
                        ((item.attendees?.size ?: 0) <= 0
                                || (item.attendees?.size ?: 0) > MAX_ATTENDEE)
                        ) ||
                (item.event != null && !canProceedEvent(item))
            ) {
                return false
            }
        }
        return true
    }

    @ExcludeFromJacocoGeneratedReport
    private fun canProceedEvent(cartItem: CartItem): Boolean{
        val eventInfo = cartItem.event
        val listSelectedTicket = eventInfo?.listSelectedTicket ?: ArrayList()
        if (listSelectedTicket.size > 0){
            listSelectedTicket.forEach { eventTicket ->
                val listTicketEntity = eventTicket.listTicketParticipantEntity
                if (listSelectedTicket.size > 0){
                    listTicketEntity.forEach { ticketEntity ->
                        val listParticipant = ticketEntity.listParticipant
                        if (listParticipant.size > 0){
                            if (eventTicket.isAllParticipantRequired && listParticipant.size < eventTicket.ticketMapCount){
                                return false
                            }
                            listParticipant.forEach { participant ->
                                if (participant.participantInfo == null || participant.isNew || !participant.isFullFillInfo){
                                    return false
                                }
                            }
                        }else{
                            return false
                        }
                    }
                }else {
                    return false
                }
            }
        } else {
            return false
        }

        return true
    }

    @ExcludeFromJacocoGeneratedReport
    override fun isTicketEventValid(): Boolean {
        cart?.items?.forEach { item ->

            // ticket event
            if (item.event != null &&
                (item.noOfEvent == null || item.noOfEvent!! <= 0)
            ) {
                return false
            }
        }
        return true
    }

    override fun isCartChange(cart: Cart?): Boolean {
        return cart != newCart
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigateCustomerVerificationView(
        requestCode: Int,
        isPayer: Boolean,
        cartItemPosition: Int,
        eventTicketPosition: Int?,
        ticketEntityPosition: Int?,
        participantPosition: Int?
    ) {
        cartItemPosNeedAddParticipant = cartItemPosition
        router?.navigateCustomerVerificationView(
            requestCode,
            getAllCustomerFromAttendees(),
            isPayer,
            eventTicketPosition,
            ticketEntityPosition,
            participantPosition,
            cartItemPosition
        )
    }

    @ExcludeFromJacocoGeneratedReport
    fun setCart(cart: Cart?) {
        this.cart = cart
        this.newCart = cart?.copyCart()
    }

    @ExcludeFromJacocoGeneratedReport
    fun getCartInPresenter(): Cart? {
        return this.cart
    }

    override fun isExitsIndemnity(): Boolean {
        if (cart != null && !cart!!.items.isNullOrEmpty()) {
            for (item in cart!!.items!!) {
                if (((item.classInfo != null && item.classInfo!!.getIndemnityRequired() == true) || (item.igInfo != null && item.igInfo!!.indemnityRequired == true)) &&
                    !item.attendees.isNullOrEmpty()
                ) {
                    for (attendee in item.attendees!!) {
                        if (attendee.isIndemnity != true) {
                            return true
                        }
                    }
                } else if (item.event != null && item.event!!.indemnityRequired == true) {
                    if (item.noOfEvent != null) {
                        if (item.isIndemnity != true) {
                            return true
                        }
                    } else if (!item.attendees.isNullOrEmpty()) {
                        for (attendee in item.attendees!!) {
                            if (attendee.isIndemnity != true) {
                                return true
                            }
                        }
                    }
                }
            }
        }

        return false
    }

    private fun getRemoveItemFromCartResult(position: Int): IBaseContract.IBaseInteractorOutput<BookingResponse> {
        return object : IBaseContract.IBaseInteractorOutput<BookingResponse> {
            override fun onSuccess(data: BookingResponse?) {
                view?.dismissLoading()
                removeItemFromCart(position)
            }

            override fun onError(data: BaseResponse<BookingResponse>) {
                view?.dismissLoading()
                view?.showErrorMessage(data)
            }

        }
    }

    fun removeItemFromCart(position: Int) {
        newCart?.items?.removeAt(position)
        cartAdapterView?.updateList()
        cart?.items?.removeAt(position)
        view?.updateNoCartItem()

        // check if cart is empty
        if (cart?.items?.size ?: 0 <= 0) {
            router?.navigateHomeView()

            cart?.payer = null
            cart?.sessionCode = null
        }
    }

    fun clearCart() {
        cart?.items?.clear()
        view?.updateNoCartItem()
        router?.navigateHomeView()
    }

    override fun removeItemFromCart(token: String, cartId: String?, position: Int) {
        if (position < 0 || position >= cart?.items?.size ?: 0) {
            return
        }
        val cartItem = cart?.items?.get(position)
        val itemId = getItemId(cartItem)
        view?.showLoading()
        if (cartItem?.igInfo != null) {
            val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
            val productRequest = ProductRequest(
                cartId,
                arrayListOf(ProductRequestItem(itemId, null))
            )
            val proxyRequestRemoveItem = ProxyRequest(
                header,
                productRequest,
                ProxyRequest.POST_METHOD,
                "",
                API.uriRemoveItemIgFromCart
            )
            interactor?.removeItemIgFromCart(
                token, cart?.payer?.mCustomerId ?: "",
                proxyRequestRemoveItem,
                getRemoveItemFromCartResult(position)
            )
        }else if(cartItem?.event != null){
            val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
            val listTickets = null
            /*//remove all ticket type
            cartItem.event?.listSelectedTicket?.forEach { eventTicket ->
                val productRequestItemTicket = ProductRequestItemTicket().apply {
                    ticketId = eventTicket.id
                }
                listTickets.add(productRequestItemTicket)
            }*/
            val productRequestItem = ProductRequestItem(
                itemId = itemId,
                qty = null,
                tickets = listTickets
            )
            val productRequest = ProductRequest(
                cartId = cartId,
                items = arrayListOf(productRequestItem)
            )
            val proxyRequestRemoveItem = ProxyRequest(
                header,
                productRequest,
                ProxyRequest.POST_METHOD,
                "",
                API.uriRemoveEventItemFromCart
            )
            interactor?.removeEventItemFromCart(
                token, cart?.payer?.mCustomerId ?: "",
                proxyRequestRemoveItem, getRemoveItemFromCartResult(position)
            )

        } else {
            val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
            val productRequest = ProductRequest(
                cartId,
                arrayListOf(ProductRequestItem(itemId, null))
            )
            val proxyRequestRemoveItem = ProxyRequest(
                header,
                productRequest,
                ProxyRequest.POST_METHOD,
                "",
                API.uriRemoveItemFromCart
            )
            interactor?.removeItemFromCart(
                token, cart?.payer?.mCustomerId ?: "",
                proxyRequestRemoveItem, getRemoveItemFromCartResult(position)
            )
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun removeEventItemTicketEntityFromCart(
        token: String,
        cartId: String?,
        cartItemPosition: Int,
        eventTicketPosition: Int,
        ticketEntityPosition: Int
    ){
        if (cartItemPosition < 0 || cartItemPosition >= (cart?.items?.size ?: 0)) {
            return
        }
        val cartItem = cart?.items?.get(cartItemPosition)
        val itemId = getItemId(cartItem)
        view?.showLoading()
        if(cartItem?.event != null){
            val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
            val listTickets = ArrayList<ProductRequestItemTicket>()
            val ticketEntity = cartItem.event?.listSelectedTicket?.get(eventTicketPosition)?.listTicketParticipantEntity?.get(ticketEntityPosition)
            if (ticketEntity != null) {
                val productRequestItemTicket = ProductRequestItemTicket().apply {
                    ticketId = cartItem.event?.listSelectedTicket?.get(eventTicketPosition)?.id
                    externalId = arrayListOf(ticketEntity.externalLineId ?: "-1")
                }
                listTickets.add(productRequestItemTicket)
                val productRequestItem = ProductRequestItem(
                    itemId = itemId,
                    qty = null,
                    tickets = listTickets
                )
                val productRequest = ProductRequest(
                    cartId = cartId,
                    items = arrayListOf(productRequestItem)
                )
                val proxyRequestRemoveItem = ProxyRequest(
                    header,
                    productRequest,
                    ProxyRequest.POST_METHOD,
                    "",
                    API.uriRemoveEventItemFromCart
                )
                interactor?.removeEventItemFromCart(
                    token = token,
                    userId = cart?.payer?.mCustomerId ?: "",
                    request = proxyRequestRemoveItem,
                    output = object : IBaseContract.IBaseInteractorOutput<BookingResponse> {
                        @ExcludeFromJacocoGeneratedReport
                        override fun onSuccess(data: BookingResponse?) {
                            view?.dismissLoading()
                            //remove ticketEntity
                            removeTicketEntity(
                                cartItemPosition = cartItemPosition,
                                eventTicketPosition = eventTicketPosition,
                                ticketEntityPosition = ticketEntityPosition
                            )

                        }

                        @ExcludeFromJacocoGeneratedReport
                        override fun onError(data: BaseResponse<BookingResponse>) {
                            view?.dismissLoading()
                            view?.showErrorMessage(data)
                        }

                    }
                )
            }
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun removeEventItemEventTicketFromCart(
        token: String,
        cartId: String?,
        cartItemPosition: Int,
        eventTicketPosition: Int
    ){
        if (cartItemPosition < 0 || cartItemPosition >= (cart?.items?.size ?: 0)) {
            return
        }
        val cartItem = cart?.items?.get(cartItemPosition)
        val itemId = getItemId(cartItem)
        view?.showLoading()
        if(cartItem?.event != null){
            val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
            val listTickets = ArrayList<ProductRequestItemTicket>()
            val eventTicket = cartItem.event?.listSelectedTicket?.get(eventTicketPosition)
            if (eventTicket != null) {
                val productRequestItemTicket = ProductRequestItemTicket().apply {
                    ticketId = cartItem.event?.listSelectedTicket?.get(eventTicketPosition)?.id
                    externalId = null
                }
                listTickets.add(productRequestItemTicket)
                val productRequestItem = ProductRequestItem(
                    itemId = itemId,
                    qty = null,
                    tickets = listTickets
                )
                val productRequest = ProductRequest(
                    cartId = cartId,
                    items = arrayListOf(productRequestItem)
                )
                val proxyRequestRemoveItem = ProxyRequest(
                    header,
                    productRequest,
                    ProxyRequest.POST_METHOD,
                    "",
                    API.uriRemoveEventItemFromCart
                )
                interactor?.removeEventItemFromCart(
                    token = token,
                    userId = cart?.payer?.mCustomerId ?: "",
                    request = proxyRequestRemoveItem,
                    output = object : IBaseContract.IBaseInteractorOutput<BookingResponse> {
                        @ExcludeFromJacocoGeneratedReport
                        override fun onSuccess(data: BookingResponse?) {
                            view?.dismissLoading()
                            removeEventTicket(
                                cartItemPosition = cartItemPosition,
                                eventTicketPosition = eventTicketPosition
                            )

                        }

                        @ExcludeFromJacocoGeneratedReport
                        override fun onError(data: BaseResponse<BookingResponse>) {
                            view?.dismissLoading()
                            view?.showErrorMessage(data)
                        }

                    }
                )
            }
        }
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigateAddParticipantViewForFacility(requestCode: Int) {
        router?.navigateAddParticipantViewForFacility(requestCode)
    }

    override fun checkResidency(token: String, eventInfo: EventInfo?) {
        view?.showLoading()
        val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
        val hostName = ""
        val uri: String = API.getUriCheckResidency(
            addressPostalCode = cart?.payer?.getPostalCode() ?: "",
            committeePostalCode = eventInfo?.postCode ?: ""
        )
        val request = ProxyRequest(header, EmptyRequest(), ProxyRequest.GET_METHOD, hostName, uri)
        interactor?.checkResidency(
            token = token,
            userId = cart?.payer?.mCustomerId ?: "",
            request = request,
            output = object : IBaseContract.IBaseInteractorOutput<CheckResidencyResponse> {
                @ExcludeFromJacocoGeneratedReport
                override fun onSuccess(data: CheckResidencyResponse?) {
                    view?.dismissLoading()
                    view?.checkResidencySuccess(eventInfo, data)
                }

                @ExcludeFromJacocoGeneratedReport
                override fun onError(data: BaseResponse<CheckResidencyResponse>) {
                    view?.dismissLoading()
                    view?.showErrorMessage(data)
                }

            }
        )
    }

    override fun checkEventVacancy(
        token: String,
        eventInfo: EventInfo?,
        isResident: Boolean
    ) {
        view?.showLoading()

        interactor?.checkEventVacancy(
            token = token,
            eventId = eventInfo?.eventCode ?: "",
            userId = cart?.payer?.mCustomerId ?: "",
            isMember = cart?.payer?.isMember() ?: false,
            isResident = isResident,
            callback = object : IBaseContract.IBaseInteractorOutput<VacancyResponse> {
                @ExcludeFromJacocoGeneratedReport
                override fun onSuccess(data: VacancyResponse?) {
                    view?.dismissLoading()
                    view?.checkEventVacancySuccess(eventInfo, data)
                }

                @ExcludeFromJacocoGeneratedReport
                override fun onError(data: BaseResponse<VacancyResponse>) {
                    view?.dismissLoading()
                    view?.showErrorMessage(data)
                }

            }
        )
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigateSelectTicketType(
        requestCode: Int,
        eventCode: String?,
        listEventTicket: ArrayList<EventTicket>?,
        selectedNumber: Int?
    ) {
        this.router?.navigateSelectTicketTypeView(
            requestCode = requestCode,
            eventCode = eventCode,
            listEventTicket = listEventTicket,
            selectedNumber = selectedNumber
        )
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigateAddParticipantEvent(
        requestCode: Int,
        eventInfo: EventInfo?,
        cartItemPosition: Int,
        eventTicketPosition: Int,
        ticketEntityPosition: Int,
        participantPosition: Int
    ) {
        this.router?.navigateAddParticipantEvent(
            requestCode = requestCode,
            eventInfo = eventInfo,
            cartItemPosition = cartItemPosition,
            eventTicketPosition = eventTicketPosition,
            ticketEntityPosition = ticketEntityPosition,
            participantPosition = participantPosition
        )
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        interactor?.onDestroy()
        view = null
        interactor = null
        router = null
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getAllCustomerFromAttendees(): ArrayList<CustomerInfo> {
        val result = ArrayList<CustomerInfo>()
        cart?.items?.forEach { item ->
            item.attendees?.forEach { attendee ->
                if (attendee.customerInfo != null && !result.contains(attendee.customerInfo!!)) {
                    result.add(attendee.customerInfo!!)
                }
            }
        }
        return result
    }

    fun canGoToProceedCheckout(): Boolean {
        if (cart == null || cart!!.items == null) {
            return false
        }
        cart!!.items!!.forEach { item ->
            if (((item.classInfo != null || item.igInfo != null) && (item.attendees?.size ?: 0) <= 0)
                || (item.event != null && !canProceedEvent(item))
            ) {
                return false
            }
        }

        return true
    }

}