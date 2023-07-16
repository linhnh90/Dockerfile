package com.styl.pa.modules.cart.view


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.adapters.CartAdapter
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.cart.Cart
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.checkResidency.CheckResidencyResponse
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.event.*
import com.styl.pa.entities.pacesRequest.EventTicket
import com.styl.pa.entities.vacancy.VacancyResponse
import com.styl.pa.modules.addParticipant.presenter.AddParticipantPresenter
import com.styl.pa.modules.addParticipantEvent.view.AddParticipantEventFragment
import com.styl.pa.modules.base.Base2Fragment
import com.styl.pa.modules.cart.ICartContact
import com.styl.pa.modules.cart.presenter.CartPresenter
import com.styl.pa.modules.customerverification.presenter.CustomerVerificationPresenter
import com.styl.pa.modules.customerverification.view.CustomerVerificationFragment
import com.styl.pa.modules.dialog.LoadingFragment
import com.styl.pa.modules.dialog.MessageDialogFragment
import com.styl.pa.modules.dialog.WaitingResultFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.selectTicketType.view.SelectTicketTypeFragment
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.LogManager
import com.styl.pa.utils.MySharedPref
import kotlinx.android.synthetic.main.fragment_cart.view.*
import org.json.JSONObject
import java.lang.reflect.Type

class CartFragment : Base2Fragment(), ICartContact.IView, View.OnClickListener {

    override fun getToken(): String? {
        return MySharedPref(activity).eKioskHeader
    }

    override fun setPullResult(isResult: Boolean) {
        isPullResult = isResult

        if (isPullResult) {
            if (isPull) {
                showScannerDialog()
            }
        } else {
            showErrorMessage(R.string.connect_scanner)
        }
    }

    private fun showScannerDialog() {
        startCountDownTimer(TIME_COUNT_DOWN, true)
        scannerDialog = WaitingResultFragment()
        scannerDialog?.setActionName(WaitingResultFragment.ACTION_NRIC_SCAN)
        if (fragmentManager != null) {
            scannerDialog?.show(fragmentManager!!, WaitingResultFragment::class.java.simpleName)
        }
    }

    companion object {

        val TAG = CartFragment::class.java.simpleName

        const val LOAD_CART_CODE = 1415
        const val ARG_IS_LOAD = BuildConfig.APPLICATION_ID + ".args.ARG_IS_LOAD"
        const val ARG_IS_BOOKING_MYSELF = BuildConfig.APPLICATION_ID + ".args.ARG_IS_BOOKING_MYSELF"

        private const val TIME_COUNT_DOWN = 10

        private const val TIME_DELAY = 1000

        fun newInstance(isBookingMyself: Boolean): CartFragment {
            val args = Bundle()
            args.putBoolean(ARG_IS_BOOKING_MYSELF, isBookingMyself)
            val fragment = CartFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var presenter: CartPresenter? = CartPresenter(this)
    private var getView: View? = null

    private var scannerDialog: WaitingResultFragment? = null
    private var isPull = false
    private var isPullResult = false

    private var countDownTimer: CountDownTimer? = null

    private var loadingDialog: LoadingFragment? = null
    private var messageDialog: MessageDialogFragment? = null

    private var deleteDialogFragment: MessageDialogFragment? = null
    private var mLastClickTime = 0L

    private var isQuickBookFacility = false

    private var isBookingMyself = true
    private var facilityParticipant: CustomerInfo? = null

    private var isQuickBookEvent = false

    private var onCartListener = object : CartAdapter.OnCartListener {

        override fun onItemRemoved(view: View, position: Int) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < TIME_DELAY) {
                return
            }
            mLastClickTime = SystemClock.elapsedRealtime()

            removeItem(position)
        }

        override fun onAddAttendeeClick(view: View, position: Int) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < TIME_DELAY) {
                return
            }
            mLastClickTime = SystemClock.elapsedRealtime()

            val cart = this@CartFragment.presenter?.getCartInPresenter()
            val cartItem = cart?.items?.get(position)
            if (cartItem?.event != null) {
                selectEventTicketType(cartItemPosition = position)
            } else{
                performAddParticipant(position)
            }
        }

        override fun onMinusClick(view: View, position: Int) {
            presenter?.decreaseNoAttendee(position)
        }

        override fun onPlusClick(view: View, position: Int) {
            presenter?.increaseNoAttendee(position)
        }

        override fun onItemClickListener(view: View?, position: Int) {
            adapter.selectItem(position)
        }
    }

    private fun removeItem(position: Int) {
        val dialog = MessageDialogFragment.newInstance(R.string.error, R.string.delete_item_sure)
        dialog.setMultiChoice(getString(R.string.no), getString(R.string.yes))
        dialog.setListener(object : MessageDialogFragment.MessageDialogListener {

            override fun onPositiveClickListener(dialogFragment: DialogFragment) {
                if (getCartId() != null && (activity as MainActivity).getBookingCart()?.items?.size ?: 0 == 1) {
                    LogManager.i("Delete cart because remove the last item")
                    (activity as MainActivity).deleteCart()
                    fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    return
                }
                if ((activity as MainActivity).getBookingCart()?.hasReservation != true) {
                    presenter?.removeItemFromCart(position)
                } else {
                    val token = getToken() ?: ""
                    presenter?.removeItemFromCart(
                        token,
                        getCartId(),
                        position
                    )
                }
            }

            override fun onNeutralClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onNeutralClickListener")
            }

            override fun onNegativeClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onNegativeClickListener")
            }
        })
        if (fragmentManager != null) {
            dialog.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    private val onAttendeeListener = object : AttendeeAdapter.OnAttendeeListener {

        override fun onRemoveAttendee(view: View, cartItem: CartItem?, position: Int) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < TIME_DELAY) {
                return
            }
            mLastClickTime = SystemClock.elapsedRealtime()

            removeAttendee(cartItem, position)
        }

        override fun onItemClickListener(view: View?, position: Int) {
            LogManager.d(TAG, "onItemClickListener")
        }
    }

    private fun removeAttendee(cartItem: CartItem?, position: Int) {
        val dialog = MessageDialogFragment.newInstance(
            R.string.error,
            R.string.are_you_sure_to_remove_this_attendee
        )
        dialog.setMultiChoice(getString(R.string.no), getString(R.string.yes))
        dialog.setListener(object : MessageDialogFragment.MessageDialogListener {

            override fun onPositiveClickListener(dialogFragment: DialogFragment) {
                if ((activity as MainActivity).getBookingCart()?.hasReservation != true) {
                    presenter?.deleteParticipant(cartItem, position)
                    return
                }
                val token = getToken() ?: ""
                presenter?.deleteParticipant(token, cartItem, position)
            }

            override fun onNeutralClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onNeutralClickListener")
            }

            override fun onNegativeClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onNegativeClickListener")
            }
        })
        if (fragmentManager != null) {
            dialog.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun showErrorParticipantExist(cartItem: CartItem?, customerInfo: CustomerInfo?) {
        val error = getString(
            R.string.participant_error,
            presenter?.getItemName(cartItem), customerInfo?.mFullName
        )
        showErrorMessage(error)
    }

    override fun checkResidencySuccess(eventInfo: EventInfo?, data: CheckResidencyResponse?) {
        presenter?.checkEventVacancy(
            token = getToken() ?: "",
            eventInfo = eventInfo,
            isResident = data?.isResident ?: false
        )
    }

    override fun checkEventVacancySuccess(eventInfo: EventInfo?, data: VacancyResponse?) {
        if (data?.tickets != null) {
            val listTicketsAvailable = data.tickets ?: ArrayList()
            eventInfo?.listEventTicketAvailable?.clear()
            eventInfo?.listEventTicketAvailable?.addAll(listTicketsAvailable)
            val cart = this.presenter?.getCartInPresenter()
            val cartItemPosition = getPositionCartItemFromCart(
                eventCode = eventInfo?.eventCode ?: "",
                cart = cart
            )
            LogManager.d("checkEventVacancySuccess: cartItemPosition = $cartItemPosition, eventCode = ${eventInfo?.eventCode}, listTicketsAvailable: ${listTicketsAvailable.size},listEventTicketAvailable = ${eventInfo?.listEventTicketAvailable?.size} ")
            cart?.items?.get(cartItemPosition)?.event = eventInfo
            this.presenter?.setCart(cart)

            if (listTicketsAvailable.size > 0) {
                this.presenter?.navigateSelectTicketType(
                    requestCode = SelectTicketTypeFragment.SELECT_TICKET_TYPE_REQUEST_CODE,
                    eventCode = eventInfo?.eventCode ?: "",
                    listEventTicket = listTicketsAvailable,
                    selectedNumber = eventInfo?.listSelectedTicket?.size ?: 0
                )
            } else {
                showErrorMessage(R.string.event_has_no_available_ticket)
            }


        } else {
            showErrorMessage(getString(R.string.cannot_get_list_ticket))
        }
    }

    private fun performAddParticipant(itemPosition: Int = 0) {
        var hasScanner = false
        if (activity != null) {
            hasScanner = (activity as MainActivity).isConnectScanner()
        }

        if (hasScanner) {
            LogManager.i("Start scan participant")
            presenter?.navigateCustomerVerificationView(
                CustomerVerificationPresenter.ATTENDEE_REQUEST_CODE,
                false,
                itemPosition
            )
        } else {
            showErrorMessage(R.string.connect_scanner)
        }
    }

    private fun selectEventTicketType(cartItemPosition: Int){
        var hasScanner = false
        if (activity != null) {
            hasScanner = (activity as MainActivity).isConnectScanner()
        }
        if (hasScanner) {
            if (getPayer() != null){
                val cart = this.presenter?.getCartInPresenter()
                val eventInfo = cart?.items?.get(cartItemPosition)?.event
                LogManager.d("selectEventTicketType: listEventTicketAvailable size = ${eventInfo?.listEventTicketAvailable?.size}")
                if ((eventInfo?.listSelectedTicket?.size ?: 0) > 0){
                    //listSelectedTicket compare with listEventTicketAvailable to show ticket type
                    val listToShow = getListUnselectedEventTicket(
                        listSelectedTicket = eventInfo?.listSelectedTicket ?: ArrayList(),
                        listEventTicketAvailable = eventInfo?.listEventTicketAvailable ?: ArrayList()
                    )
                    if (listToShow.size > 0) {
                        this.presenter?.navigateSelectTicketType(
                            requestCode = SelectTicketTypeFragment.SELECT_TICKET_TYPE_REQUEST_CODE,
                            eventCode = eventInfo?.eventCode ?: "",
                            listEventTicket = listToShow,
                            selectedNumber = eventInfo?.listSelectedTicket?.size ?: 0
                        )
                    } else {
                        showErrorMessage(R.string.event_has_no_available_ticket)
                    }

                }else {
                    //if do not have selected ticket -> get residency and check vacancy before goto select ticket type
                    presenter?.checkResidency(
                        token = getToken() ?: "",
                        eventInfo = cart?.items?.get(cartItemPosition)?.event
                    )
                }
            } else {
                presenter?.navigateCustomerVerificationView(
                    requestCode = CustomerVerificationPresenter.PAYER_REQUEST_CODE,
                    isPayer = true,
                    cartItemPosition = cartItemPosition
                )
            }
        } else {
            showErrorMessage(R.string.connect_scanner)
        }
    }

    private fun getListUnselectedEventTicket(
        listSelectedTicket: ArrayList<EventTicket>,
        listEventTicketAvailable: ArrayList<EventTicket>
    ): ArrayList<EventTicket> {
        LogManager.d("CartFragment: getListUnselectedEventTicket: listSelectedTicket.size = ${listSelectedTicket.size}, listEventTicketAvailable.size = ${listEventTicketAvailable.size}")
        val result: ArrayList<EventTicket> = ArrayList()
        if (listSelectedTicket.size == 0){
            result.addAll(listEventTicketAvailable)
        } else{
            listEventTicketAvailable.forEach {
                val isTicketInListSelected = isTicketExistInList(
                    id =  it.id ?: "",
                    list = listSelectedTicket
                )
                if (!isTicketInListSelected){
                    result.add(it)
                }

            }
        }
        return  result
    }

    private fun isTicketExistInList(id: String, list: ArrayList<EventTicket>): Boolean{
        list.forEach {
            if (id == it.id){
                return true
            }
        }
        return false
    }

    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        getView = inflater.inflate(R.layout.fragment_cart, container, false)

        this.getBundle()
        init()

        return getView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if ((activity as MainActivity).isQuickBook) {
            if ((activity as MainActivity).getPayer() != null) {
                return
            }
            if (isQuickBookFacility) {
                if (isBookingMyself) {
                    proceedToCheckout()
                } else {
                    presenter?.navigateAddParticipantViewForFacility(AddParticipantPresenter.ADD_PARTICIPANT_FOR_FACILITY_REQUEST_CODE)
                }
            } else if (isCartHaveEvent()) {
                if (isQuickBookEvent) {
                    //event need to add payer before
                    presenter?.navigateCustomerVerificationView(
                        CustomerVerificationPresenter.PAYER_REQUEST_CODE,
                        true
                    )
                }
            } else {
                performAddParticipant()
            }
        }
    }

    private fun getCart(): Cart? {
        return (activity as MainActivity).getBookingCart()
    }

    private fun getBundle() {
        isBookingMyself = arguments?.getBoolean(ARG_IS_BOOKING_MYSELF, true) ?: true
    }

    private fun init() {
        val cart = getCart()
        isQuickBookFacility =
            (activity as MainActivity).isQuickBook && cart?.items?.get(0)?.facility != null
        isQuickBookEvent =
            (activity as MainActivity).isQuickBook && cart?.items?.get(0)?.event != null

        presenter?.setCart(cart)
        adapter = CartAdapter(activity as Context, presenter)
        adapter.selectItem(0)
        adapter.onCartListener = onCartListener
        adapter.onAttendeeListener = onAttendeeListener

        getView?.list_item?.layoutManager = LinearLayoutManager(activity)
        getView?.list_item?.adapter = adapter

        displayPayer(cart)
        showButtonProceedCheckout(presenter?.canGoToProceedCheckout() == true)

        val isAlive = (activity as? MainActivity)?.canOrder ?: true

        getView?.btn_proceed?.isEnabled = isAlive
        if (isAlive) {
            getView?.img_pointer_checkout?.setImageResource(R.drawable.ic_hand)
        } else {
            getView?.img_pointer_checkout?.setImageResource(R.drawable.ic_hand_disable)
        }

        getView?.btn_proceed?.setOnClickListener(this)
        getView?.btn_back?.setOnClickListener(this)
        getView?.btn_clear?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < MainActivity.CLICK_TIMER) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        when (v?.id) {
            R.id.btn_proceed -> {
                LogManager.i("Proceed to Checkout button pressed")
                //check event min ticket
                val errorMinTicket = getErrorEventMinTicket()
                if (errorMinTicket.isEmpty()) {
                    if (isQuickBookFacility && !isBookingMyself) {
                        //check have participant, if yes do proceedToCheckout, else show add participant screen
                        if (facilityParticipant != null) {
                            proceedToCheckout()
                        } else {
                            LogManager.d("Facility book someone but done have participant")
                            presenter?.navigateAddParticipantViewForFacility(AddParticipantPresenter.ADD_PARTICIPANT_FOR_FACILITY_REQUEST_CODE)
                        }
                    } else {
                        proceedToCheckout()
                    }
                } else {
                    showErrorMessage(errorMinTicket)
                }
            }
            R.id.btn_clear -> {
                clearCart()
            }
            R.id.btn_back -> {
                if ((activity as MainActivity).isQuickBook) {
                    (activity as MainActivity).showBookingInProgress()
                } else {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

    private fun getErrorEventMinTicket(): String {
        var errorMsg = ""
        if (isCartHaveEvent()){
            val cart = presenter?.getCartInPresenter()
            cart?.items?.forEach { cartItem ->
                if (cartItem.event != null){
                    cartItem.event?.listSelectedTicket?.forEach { selectedTicket ->
                        val currentQty = selectedTicket.selectedQty ?: 0
                        val minQty = selectedTicket.minQty ?: 0
                        if (currentQty < minQty){
                            errorMsg = getString(R.string.error_min_event_ticket, cartItem.event?.eventTitle, selectedTicket.name, minQty.toString())
                            return errorMsg
                        }
                    }
                }
            }
        }
        return errorMsg
    }

    private fun handleCanProceed() {
        if (presenter?.isExitsIndemnity() == true) {
            presenter?.navigateIndemnityView()
        } else {
            if (getPayer() != null){
                presenter?.navigationCheckoutView(
                    isCartUpdate = false,
                    isBookingMyself = isBookingMyself,
                    facilityParticipant = facilityParticipant
                )
            } else {
                presenter?.navigateCustomerVerificationView(
                    CustomerVerificationPresenter.PAYER_REQUEST_CODE,
                    true
                )
            }
        }
    }

    private fun handleCannotProceed() {
        var hasScanner = false
        if (activity != null) {
            hasScanner = (activity as MainActivity).isConnectScanner()
        }

        if (hasScanner) {
            if (presenter?.isExitsIndemnity() == true) {
                presenter?.navigateIndemnityView()
            } else {
                if (getPayer() == null) {
                    presenter?.navigateCustomerVerificationView(
                        CustomerVerificationPresenter.PAYER_REQUEST_CODE,
                        true
                    )
                } else {
                    showErrorMessage(R.string.please_check_attendees)
                }
            }
        } else {
            showErrorMessage(R.string.connect_scanner)
        }
    }

    private fun proceedToCheckout() {
        if (true == presenter?.canProceed()) {
            handleCanProceed()
        } else {
            handleCannotProceed()
        }
    }

    private fun clearCart() {
        val dialog =
            MessageDialogFragment.newInstance(R.string.error, R.string.are_you_sure_to_clear_cart)
        dialog.setMultiChoice(getString(R.string.no), getString(R.string.yes))
        dialog.setListener(object : MessageDialogFragment.MessageDialogListener {

            override fun onPositiveClickListener(dialogFragment: DialogFragment) {
                if ((activity as MainActivity).getBookingCart()?.hasReservation != true) {
                    presenter?.clearCart()
                } else {
                    LogManager.i("User clear cart")
                    (activity as MainActivity).deleteCart()
                    fragmentManager?.popBackStack()
                }
            }

            override fun onNeutralClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onNeutralClickListener")
            }

            override fun onNegativeClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onNegativeClickListener")
            }
        })
        if (fragmentManager != null) {
            dialog.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun updateNoCartItem() {
        (activity as? MainActivity)?.setItemAddToCart()
    }

    override fun displayPayer(cart: Cart?) {
        if (cart?.payer != null) {
            getView?.payer_panel?.visibility = View.VISIBLE

            getView?.txt_payer_name?.text = cart.payer?.mFullName
        } else {
            getView?.payer_panel?.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.cart_title))
        (activity as? MainActivity)?.setBackgroundLine(R.drawable.bg_gradient_orange)
        (activity as? MainActivity)?.showProgressParticipant()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CustomerVerificationPresenter.PAYER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val customerInfo =
                    data?.getParcelableExtra<CustomerInfo>(CustomerVerificationPresenter.ARG_CUSTOMER_INFO)
                val isCartUpdate =
                    data?.getBooleanExtra(CustomerVerificationFragment.ARG_IS_CART_UPDATE, false)
                        ?: false
                val cartItemPosition = data?.getIntExtra(CustomerVerificationFragment.ARG_CART_ITEM_POSITION, -1) ?: -1
                presenter?.updatePayer(customerInfo)


                if (isCartHaveEvent()) {
                    if (isQuickBookEvent) {
                        val cart = this.presenter?.getCartInPresenter()
                        presenter?.checkResidency(
                            token = getToken() ?: "",
                            eventInfo = cart?.items?.get(0)?.event
                        )
                    } else {
                        if (cartItemPosition >= 0){
                            val cart = this.presenter?.getCartInPresenter()
                            val cartItem = cart?.items?.get(cartItemPosition)
                            if (cartItem?.event != null){
                                presenter?.checkResidency(
                                    token = getToken() ?: "",
                                    eventInfo = cartItem.event
                                )
                            }
                        }
                    }
                } else {
                    presenter?.navigationCheckoutView(
                        isCartUpdate = isCartUpdate,
                        isBookingMyself = isBookingMyself,
                        facilityParticipant = facilityParticipant
                    )
                }
            }
        } else if (requestCode == CustomerVerificationPresenter.ATTENDEE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val customerInfo =
                    data?.getParcelableExtra<CustomerInfo>(CustomerVerificationPresenter.ARG_CUSTOMER_INFO)
                val selectedProductsStr =
                    data?.getStringExtra(CustomerVerificationPresenter.ARG_SELECTED_PRODUCT)
                val eventTicketPosition =
                    data?.getIntExtra(CustomerVerificationFragment.ARG_EVENT_TICKET_POSITION, -1)
                val ticketEntityPosition =
                    data?.getIntExtra(CustomerVerificationFragment.ARG_TICKET_ENTITY_POSITION, -1)
                val participantPosition =
                    data?.getIntExtra(CustomerVerificationFragment.ARG_PARTICIPANT_POSITION, -1)
                val cartItemPosition = data?.getIntExtra(CustomerVerificationFragment.ARG_CART_ITEM_POSITION, -1)

                val entityType: Type =
                    object : TypeToken<LinkedHashMap<String?, Boolean>>() {}.type
                val selectedProducts: LinkedHashMap<String?, Boolean> =
                    Gson().fromJson(selectedProductsStr, entityType)
                val token = MySharedPref(activity).eKioskHeader

                if (isCartHaveEvent()) {
                    if (isQuickBookEvent) {
                        //create participant add to event ticket and set cart the notify
                        LogManager.d("onActivityResult ticketEntityPosition = $ticketEntityPosition")
                        updateCartEvent(
                            customerInfo = customerInfo,
                            selectedProducts = selectedProducts,
                            cartItemPosition = 0,
                            eventTicketPosition = eventTicketPosition ?: -1,
                            ticketEntityPosition = ticketEntityPosition ?: -1,
                            participantPosition = participantPosition ?: -1
                        )
                    } else{
                        val cart = getCart()
                        if ((cartItemPosition ?: -1) >= 0){
                            if (cart?.items?.get(cartItemPosition!!)?.event != null) {
                                updateCartEvent(
                                    customerInfo = customerInfo,
                                    selectedProducts = selectedProducts,
                                    cartItemPosition = cartItemPosition ?: -1,
                                    eventTicketPosition = eventTicketPosition ?: -1,
                                    ticketEntityPosition = ticketEntityPosition ?: -1,
                                    participantPosition = participantPosition ?: -1
                                )
                            } else {
                                presenter?.updateAttendee(token, customerInfo, selectedProducts)
                            }
                        }

                    }
                } else {
                    presenter?.updateAttendee(token, customerInfo, selectedProducts)
                }

            }
        } else if (requestCode == LOAD_CART_CODE && resultCode == Activity.RESULT_OK) {
            val cart = (activity as MainActivity).getBookingCart()

            val isNeedToReload = presenter?.isCartChange(cart)
            if (true == isNeedToReload) {
                if ((cart?.items?.size ?: 0) > 0) {
                    presenter?.setCart(cart)

                    adapter.notifyDataSetChanged()
                } else {
                    LogManager.i("Not item in the cart -> delete cart")
                    (activity as MainActivity).deleteCart()
                    fragmentManager?.popBackStack()
                }
            }
        } else if (requestCode == AddParticipantPresenter.ADD_PARTICIPANT_FOR_FACILITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val facilityParticipant =
                data?.getParcelableExtra<CustomerInfo>(AddParticipantPresenter.ARG_PARTICIPANT_INFO)
            this.facilityParticipant = facilityParticipant
            proceedToCheckout()
        } else if (requestCode == SelectTicketTypeFragment.SELECT_TICKET_TYPE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val eventCode = data?.getStringExtra(SelectTicketTypeFragment.ARG_EVENT_CODE) ?: ""
            val listEventTickets =
                data?.getParcelableArrayListExtra<EventTicket>(SelectTicketTypeFragment.ARG_LIST_EVENT_TICKET)
                    ?: ArrayList()

            LogManager.d("CartFragment: onActivityForResult: eventCode = $eventCode, listEventTickets size = ${listEventTickets.size}")
            val cart = presenter?.getCartInPresenter()
            if (cart != null) {
                val cartItemPosition =
                    getPositionCartItemFromCart(eventCode = eventCode, cart = cart)
                if (cartItemPosition >= 0 && !cart.items.isNullOrEmpty()) {
                    val cartItemEvent = cart.items!![cartItemPosition]
                    if (cartItemEvent.event != null) {
                        //generate listSelectedTicket for event
                        val newListSelectedTicket = getSelectedTicketList(
                            eventInfo = cartItemEvent.event,
                            listTicket = listEventTickets
                        )
                        cartItemEvent.event?.listSelectedTicket?.addAll(newListSelectedTicket)
                        cart.items!![cartItemPosition] = cartItemEvent

                        //setCart and notify
                        presenter?.setCart(cart)
                        adapter.notifyDataSetChanged()
                    }
                }
            } else {
                showErrorMessage(R.string.error_cannot_get_cart)
            }
        } else if (requestCode == AddParticipantEventFragment.ADD_PARTICIPANT_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val cartItemPosition = data?.getIntExtra(AddParticipantEventFragment.ARG_CART_ITEM_POSITION,-1) ?: -1
            val eventTicketPosition = data?.getIntExtra(AddParticipantEventFragment.ARG_EVENT_TICKET_POSITION,-1) ?: -1
            val ticketEntityPosition = data?.getIntExtra(AddParticipantEventFragment.ARG_TICKET_ENTITY_POSITION,-1) ?: -1
            val participantPosition = data?.getIntExtra(AddParticipantEventFragment.ARG_PARTICIPANT_POSITION,-1) ?: -1
            val participantInfo = data?.getParcelableExtra<Fields>(AddParticipantEventFragment.ARG_PARTICIPANT_INFO)

            val cart = (activity as MainActivity).getBookingCart()
            if (cart != null && cartItemPosition >= 0 && eventTicketPosition >= 0
                && ticketEntityPosition >= 0 && participantPosition >= 0
                && participantInfo != null
            ){
                cart.items?.get(cartItemPosition)
                    ?.event?.listSelectedTicket?.get(eventTicketPosition)
                    ?.listTicketParticipantEntity?.get(ticketEntityPosition)
                    ?.listParticipant?.get(participantPosition)?.isFullFillInfo = true

                val firstNameObject = participantInfo.listNotDefined.find {
                    it.itemName.equals("firstname")
                }

                if (firstNameObject != null && !firstNameObject.inputValue.isNullOrEmpty()) {
                    cart.items?.get(cartItemPosition)
                        ?.event?.listSelectedTicket?.get(eventTicketPosition)
                        ?.listTicketParticipantEntity?.get(ticketEntityPosition)
                        ?.listParticipant?.get(participantPosition)?.nameToShow =
                        firstNameObject.inputValue
                }



                cart.items?.get(cartItemPosition)
                    ?.event?.listSelectedTicket?.get(eventTicketPosition)
                    ?.listTicketParticipantEntity?.get(ticketEntityPosition)
                    ?.listParticipant?.get(participantPosition)?.participantInfo = participantInfo
                //setCart and notify
                presenter?.setCart(cart)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun updateCartEvent(
        customerInfo: CustomerInfo?,
        selectedProducts: LinkedHashMap<String?, Boolean>,
        cartItemPosition: Int,
        eventTicketPosition: Int,
        ticketEntityPosition: Int,
        participantPosition: Int
    ) {
        LogManager.d("CartFragment: updateCartEvent: customer: ${customerInfo?.mFullName}, cartItemPosition=${cartItemPosition}, eventTicketPosition=${eventTicketPosition}, ticketEntityPosition=${ticketEntityPosition}, participantPosition=${participantPosition} ")
        val cart = this.presenter?.getCartInPresenter()
        if (cart != null && cartItemPosition >= 0 && eventTicketPosition >= 0 && ticketEntityPosition >= 0 && participantPosition >= 0) {
            val cartItem = cart.items?.get(cartItemPosition)
            if (cartItem != null) {
                val eventTicket = cartItem.event?.listSelectedTicket?.get(eventTicketPosition)
                val ticketEntity =
                    eventTicket?.listTicketParticipantEntity?.get(ticketEntityPosition)
                val participant = ticketEntity?.listParticipant?.get(participantPosition)

                if (participant != null) {
                    ticketEntity.listParticipant.forEach {
                        if (it.id.equals(customerInfo?.mCustomerId)){
                            showErrorMessage(R.string.msg_existed_participant_event)
                            return
                        }
                    }
                    participant.isNew = false
                    participant.nameToShow = customerInfo?.mFullName
                    participant.id = customerInfo?.mCustomerId
                    participant.customerInfo = customerInfo

                    val eventInfo = cart.items?.get(cartItemPosition)?.event

                    val eventFormData: EventFormData? = try {
                        GeneralUtils.convertStringToObject(eventInfo?.getEventFormDataFromHTML().toString())
                    } catch (e: Exception){
                        e.printStackTrace()
                        null
                    }

                    if (eventFormData != null){
                        val fields: Fields? = eventFormData.fieldSections?.page1?.fields
                        if (fields != null){
                            if (fields.firstname != null){
                                fields.firstname?.inputValue = customerInfo?.mFullName
                            }
                            if (fields.lastname != null){
                                fields.lastname?.inputValue = "."
                            }
                            if (fields.email != null){
                                fields.email?.inputValue = customerInfo?.mEmail
                            }
                            if (fields.gender != null){
                                fields.gender?.inputValue = customerInfo?.gender
                            }
                            if (fields.dateOfBirth != null){
                                fields.dateOfBirth?.inputValue = GeneralUtils.formatDateTimeToDate(customerInfo?.dob)
                            }
                            if (fields.postalZipCode != null){
                                fields.postalZipCode?.inputValue = customerInfo?.getPostalCode()
                            }
                            if (fields.contactNo != null){
                                fields.contactNo?.inputValue = customerInfo?.mMobile
                            }
                            if (fields.homeAddress != null){
                                fields.homeAddress?.inputValue = null
                            }
                            if (fields.nextOfKinContactNo != null){
                                fields.nextOfKinContactNo?.inputValue = null
                            }
                            if (fields.nextOfKinName != null){
                                fields.nextOfKinName?.inputValue = null
                            }
                            if (fields.noOfAccompanyingGuest != null){
                                fields.noOfAccompanyingGuest?.inputValue = null
                            }
                            if (fields.nric != null){
                                fields.nric?.inputValue = if (customerInfo?.mIdType?.equals("NRIC") == true){
                                    customerInfo.mIdNo
                                } else {
                                    null
                                }
                            }
                            if (fields.nricNoUenNo != null){
                                fields.nricNoUenNo?.inputValue = null
                            }
                            if (fields.passportNumber != null){
                                fields.passportNumber?.inputValue = null
                            }
                            if (fields.remarks != null){
                                fields.remarks?.inputValue = null
                            }
                            if (fields.serialNo != null){
                                fields.serialNo?.inputValue = null
                            }
                            if (fields.age != null){
                                fields.age?.inputValue = null
                            }
                            if (fields.citizenship != null){
                                fields.citizenship?.inputValue = null
                            }
                            if (fields.dietaryPreferences != null){
                                fields.dietaryPreferences?.inputValue = null
                            }
                            if (fields.salutation != null){
                                fields.salutation?.inputValue = null
                            }
                            if (fields.indemnity != null){
                                fields.indemnity?.inputValue = null
                            }
                        }
                        //Add list field object
                        fields?.listNotDefined = AddParticipantEventFragment.getListFieldObjectEventForm(eventInfo?.getEventFormDataFromHTML().toString())
                        //add init value to list field object
                        val fieldsStr = GeneralUtils.convertObjectToString(fields)
                        val fieldsObject = JSONObject(fieldsStr)
                        fields?.listNotDefined?.forEach {
                            val valueObject = try {
                                fieldsObject.getJSONObject(it.itemName)
                            } catch (e: Exception){
                                null
                            }
                            LogManager.d("CartFragment: valueObject = $valueObject")
                            val inputValue = try {
                                valueObject?.getString("inputValue") ?: ""
                            } catch (e: Exception){
                                ""
                            }
                            it.inputValue = inputValue
                        }


                        participant.participantInfo = fields
                        participant.isFullFillInfo = isFieldsFullFill(fields)

                    } else {
                        participant.isFullFillInfo = false
                    }

                    cart.items?.get(cartItemPosition)
                        ?.event?.listSelectedTicket?.get(eventTicketPosition)
                        ?.listTicketParticipantEntity?.get(ticketEntityPosition)
                        ?.listParticipant?.set(participantPosition, participant)

                    this.presenter?.setCart(cart)
                    adapter.notifyItemChanged(cartItemPosition)

                }

            }
        }
    }

    private fun isFieldsFullFill(fields: Fields?): Boolean{
        if (fields == null){
            return false
        }
        if (fields.listNotDefined.size <= 0){
            return false
        }
        fields.listNotDefined.forEach {
            if (it.validation?.required == true && it.inputValue.isNullOrEmpty()){
                return false
            }
        }

        return true
    }

    private fun getPositionCartItemFromCart(eventCode: String, cart: Cart?): Int {
        cart?.items?.forEach {
            if (it.event?.eventCode == eventCode) {
                return cart.items?.indexOf(it) ?: -1
            }
        }
        return -1
    }

    private fun getSelectedTicketList(
        eventInfo: EventInfo?,
        listTicket: ArrayList<EventTicket>
    ): ArrayList<EventTicket> {
        val result = ArrayList<EventTicket>()
        val isAllParticipantRequired = eventInfo?.isAllParticipantRequired ?: false
        listTicket.forEach { ticket ->
            if (ticket.isSelected) {
                LogManager.d("SelectTicketTypeFragment: getSelectedTicketList: ticket ${ticket.name}, selectQty ${ticket.selectedQty}")
                val ticketMapCount =
                    getTicketMapCount(eventInfo = eventInfo, ticketId = ticket.id ?: "")
                //generate list TicketEntity depend on selectedQty
                ticket.listTicketParticipantEntity = generateListTicketParticipantEntity(
                    isAllParticipantRequired = isAllParticipantRequired,
                    ticketMapCount = ticketMapCount,
                    selectedQty = ticket.selectedQty ?: 0
                )
                ticket.ticketMapCount = ticketMapCount
                ticket.isAllParticipantRequired = isAllParticipantRequired
                result.add(ticket)
            }
        }
        LogManager.d("SelectTicketTypeFragment: getSelectedTicketList: ${result.size}")
        return result
    }

    private fun getTicketMapCount(eventInfo: EventInfo?, ticketId: String): Int {
        var result = 1
        eventInfo?.classTickets?.forEach {
            LogManager.d("getTicketMapCount: event class ticket id = ${it.ticketId}, ticketIdSelected = $ticketId")
            if (it.ticketId == ticketId) {
                result = it.ticketMapCount ?: 1
            }
        }
        return result
    }

    private fun generateListTicketParticipantEntity(
        isAllParticipantRequired: Boolean,
        ticketMapCount: Int,
        selectedQty: Int
    ): ArrayList<TicketEntity> {
        val result: ArrayList<TicketEntity> = ArrayList()
        if (selectedQty > 0) {
            for (i in 1..selectedQty) {
                val ticketEntity = TicketEntity()
                ticketEntity.listParticipant = generateListParticipantOfTicketEntity(
                    isAllParticipantRequired = isAllParticipantRequired,
                    ticketMapCount = ticketMapCount
                )
                result.add(ticketEntity)
            }
        }
        return result
    }

    fun generateListParticipantOfTicketEntity(
        isAllParticipantRequired: Boolean,
        ticketMapCount: Int
    ): ArrayList<EventParticipant> {
        val result: ArrayList<EventParticipant> = ArrayList()
        var count = 1
        if (isAllParticipantRequired) {
            count = ticketMapCount
        }
        for (i in 1..count) {
            val eventParticipant = EventParticipant()
            result.add(eventParticipant)
        }
        return result

    }

    fun setConfigEvent() {
//        if (presenter?.getScannerConfigEvent() != null) {
//            (activity as MainActivity).setConfigEvent(presenter?.getScannerConfigEvent()!!)
//        }
    }

    fun pullTrigger(isPull: Boolean) {
        this.isPull = isPull
        if ((activity as MainActivity).getConnectScannerResult()) {
            isPullResult = false
            (activity as MainActivity).pullTrigger(isPull)
        } else {
            showErrorMessage(R.string.connect_scanner)
        }
    }

    override fun dismissScannerDialog() {
        if (scannerDialog != null) {
            scannerDialog?.dismiss()
            scannerDialog = null
        }
        stopCountDownTimer()
    }

    override fun onPause() {
        super.onPause()
        dismissScannerDialog()
        dismissLoading()
        dismissMessageDialog()
        dismissDialogDeleteItem()
    }

    override fun onStop() {
        super.onStop()
        if (isPull) {
            pullTrigger(false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
        presenter = null
    }

    private fun initCountDownTimer(time: Int) {
        countDownTimer = object : CountDownTimer((time * 1000).toLong(), 1000) {

            override fun onTick(l: Long) {
                LogManager.d(TAG, "onTick")
            }

            override fun onFinish() {
                dismissScannerDialog()
            }
        }
    }

    private fun startCountDownTimer(time: Int, isReset: Boolean) {
        if (countDownTimer != null) {
            if (isReset) {
                countDownTimer?.onTick((time * 1000).toLong())
            }
        } else {
            initCountDownTimer(time)
        }

        countDownTimer?.start()
    }

    fun stopCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
    }

    override fun showLoading() {
        if (loadingDialog == null || loadingDialog?.dialog?.isShowing == false) {
            loadingDialog = LoadingFragment()
            if (fragmentManager != null) {
                loadingDialog?.show(fragmentManager!!, LoadingFragment::class.java.simpleName)
            }
        }

    }

    override fun dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog?.dismiss()
            loadingDialog = null
        }
    }

    override fun getCartId(): String? {
        return (activity as MainActivity).getBookingCartId()
    }

    override fun getPayer(): CustomerInfo? {
        return (activity as MainActivity).getPayer()
    }

    override fun <T> showErrorMessage(response: BaseResponse<T>) {
        messageDialog?.dismiss()
        messageDialog = response.formatBaseResponse(0, false)
        if (fragmentManager != null) {
            messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun showErrorMessage(messageResId: Int) {
        messageDialog?.dismiss()
        messageDialog = MessageDialogFragment.newInstance(R.string.error, messageResId)
        if (fragmentManager != null) {
            messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun showErrorMessage(message: String) {
        messageDialog?.dismiss()
        messageDialog = MessageDialogFragment.newInstance(0, message, false)
        if (fragmentManager != null) {
            messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun showErrorMessageTitle(messageResId: Int, title: Int) {
        messageDialog?.dismiss()

        messageDialog = MessageDialogFragment.newInstance(title, messageResId, true)
        if (fragmentManager != null) {
            messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    private fun dismissMessageDialog() {
        if (messageDialog != null) {
            messageDialog?.dismiss()
            messageDialog = null
        }
    }

    private fun dismissDialogDeleteItem() {
        if (deleteDialogFragment != null) {
            deleteDialogFragment?.dismiss()
        }
    }

    override fun showButtonProceedCheckout(isShown: Boolean) {
        if (isShown) {
            getView?.btn_proceed?.visibility = View.VISIBLE
            getView?.img_pointer_checkout?.visibility = View.VISIBLE
        } else {
            getView?.btn_proceed?.visibility = View.INVISIBLE
            getView?.img_pointer_checkout?.visibility = View.INVISIBLE
        }
    }

    override fun getLockedCart(): Cart? {
        return (activity as MainActivity).lockedCart
    }

    override fun setCanOrder(isAlive: Boolean) {
        getView?.btn_proceed?.isEnabled = isAlive
        if (isAlive) {
            getView?.img_pointer_checkout?.setImageResource(R.drawable.ic_hand)
        } else {
            getView?.img_pointer_checkout?.setImageResource(R.drawable.ic_hand_disable)
        }
    }

    private fun isCartHaveEvent(): Boolean {
        val cart = this.presenter?.getCartInPresenter()
        cart?.items?.forEach {
            if (it.event != null) {
                return true
            }
        }
        return false
    }
}
