package com.styl.pa.modules.indemnity.view


import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.SystemClock
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.styl.pa.R
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.modules.base.BaseDialogFragment
import com.styl.pa.modules.cart.view.CartFragment
import com.styl.pa.modules.customerverification.presenter.CustomerVerificationPresenter
import com.styl.pa.modules.customerverification.view.CustomerVerificationFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.utils.GeneralTextUtil
import kotlinx.android.synthetic.main.fragment_indemnity.view.*

class IndemnityFragment : BaseDialogFragment(), View.OnClickListener {

    private var getView: View? = null
    private var cartItemList: ArrayList<CartItem> = ArrayList()
    private var couter: Int = 0
    private var total: Int = 0
    private var hasChanged = false

    private var customerInfo: CustomerInfo? = null
    private var selectedProducts : Array<String>? = null
    private var productsWithIndemnity = LinkedHashMap<String?, Boolean>()
    private var eventTicketPosition: Int? = null
    private var ticketEntityPosition: Int? = null
    private var participantPosition: Int? = null
    private var cartItemPosition: Int? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        getView = activity?.layoutInflater?.inflate(R.layout.fragment_indemnity, null)
        onSetEventDismissDialog(this)
        getBundle()

        if (selectedProducts == null || customerInfo == null) {
            getCartItemIndemnityList()

            getTotalIndemnity()

            init()

            handleAcceptanceProcess()
        } else {
            init()
            handleIndemnityWithAddAttendee()
        }

        val dialog = AlertDialog.Builder(activity)
                .setView(getView)
                .create()
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun getBundle() {
        if (arguments != null) {
            customerInfo = arguments?.getParcelable(CustomerVerificationFragment.ARG_CUSTOMER)
            selectedProducts = arguments?.getStringArray(CustomerVerificationFragment.ARG_SELECTED_PRODUCTS)
            eventTicketPosition = arguments?.getInt(CustomerVerificationFragment.ARG_EVENT_TICKET_POSITION)
            ticketEntityPosition = arguments?.getInt(CustomerVerificationFragment.ARG_TICKET_ENTITY_POSITION)
            participantPosition = arguments?.getInt(CustomerVerificationFragment.ARG_PARTICIPANT_POSITION)
            cartItemPosition = arguments?.getInt(CustomerVerificationFragment.ARG_CART_ITEM_POSITION)
        }
    }

    private fun getCartItemIndemnityList() {
        val cart = (activity as MainActivity).getBookingCart()
        if (cart != null && !cart.items.isNullOrEmpty()) {
            for (item in cart.items!!) {
                if (item.classInfo != null && item.classInfo?.getIndemnityRequired() == true
                        && !item.attendees.isNullOrEmpty()) {
                    for (attendee in item.attendees!!) {
                        if (attendee.isIndemnity != true) {
                            cartItemList.add(item)
                            break
                        }
                    }
                } else if(item.igInfo != null && item.igInfo?.indemnityRequired == true
                    && !item.attendees.isNullOrEmpty()){
                    for (attendee in item.attendees!!) {
                        if (!attendee.isIndemnity) {
                            cartItemList.add(item)
                            break
                        }
                    }
                } else if (item.event != null && item.event?.indemnityRequired == true) {
                    if (item.noOfEvent != null) {
                        if (item.isIndemnity != true) {
                            cartItemList.add(item)
                        }
                    } else if (!item.attendees.isNullOrEmpty()) {
                        for (attendee in item.attendees!!) {
                            if (attendee.isIndemnity != true) {
                                cartItemList.add(item)
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleIndemnityWithAddAttendee() {
        val cart = (activity as MainActivity).getBookingCart()

        if (selectedProducts != null) {
            for (productId in selectedProducts!!) {
                productsWithIndemnity[productId] = false
                if (cart?.items != null) {
                    loop@ for (cartItem: CartItem in cart.items!!) {
                        var itemId: String? = null
                        if (cartItem.classInfo != null && cartItem.classInfo?.getIndemnityRequired() == true) {
                            itemId = cartItem.classInfo?.getClassId()
                        } else if (cartItem.igInfo != null && cartItem.igInfo?.indemnityRequired == true){
                            itemId = cartItem.igInfo?.igId
                        } else if (cartItem.event != null && cartItem.event?.indemnityRequired == true) {
                            itemId = cartItem.event?.eventId
                        }
                        if (itemId != null && productId.equals(itemId)) {
                            var shouldAdd = true
                            if (cartItem.attendees == null || cartItem.attendees?.size == 0) {
                                cartItemList.add(cartItem)
                            } else {
                                cartItem.attendees?.forEach { attendee ->
                                    if (attendee.customerInfo?.mCustomerId.equals(this.customerInfo?.mCustomerId)) {
                                        productsWithIndemnity[productId] = attendee.isIndemnity
                                        if (!attendee.isIndemnity) {
                                            cartItemList.add(cartItem)
                                        }
                                        shouldAdd = false
                                        return@forEach
                                    }
                                }
                                if (shouldAdd) {
                                    cartItemList.add(cartItem)
                                }
                            }
                            break@loop
                        }
                    }
                }
            }
        }

        total = cartItemList.size
        handleAcceptanceProcess()
    }

    private fun getTotalIndemnity(): Int {
        total = 0
        for (item in cartItemList) {
            if (item.classInfo != null && item.classInfo?.getIndemnityRequired() == true
                    && !item.attendees.isNullOrEmpty()) {
                for (attendee in item.attendees!!) {
                    if (attendee.isIndemnity != true) {
                        total++
                    }
                }
            } else if (item.igInfo != null && item.igInfo?.indemnityRequired == true
                && !item.attendees.isNullOrEmpty()){
                for (attendee in item.attendees!!) {
                    if (attendee.isIndemnity != true) {
                        total++
                    }
                }
            } else if (item.event != null && item.event?.indemnityRequired == true) {
                if (item.noOfEvent != null) {
                    if (item.isIndemnity != true) {
                        total++
                    }
                } else if (!item.attendees.isNullOrEmpty()) {
                    for (attendee in item.attendees!!) {
                        if (attendee.isIndemnity != true) {
                            total++
                        }
                    }
                }
            }
        }

        return total
    }

    private fun init() {
        getView?.txt_indemnity?.movementMethod = ScrollingMovementMethod.getInstance()
        getView?.btn_accept?.setOnClickListener(this)
        getView?.btn_deny?.setOnClickListener(this)
        getView?.iv_close?.setOnClickListener(this)
    }

    private var itemPosition = 0
    private var attendeePosition = 0
    private var isShowIndemnityContent = true
    private fun handleAcceptanceProcess() {
        if (itemPosition >= 0 && itemPosition < cartItemList.size) {
            if (customerInfo == null) {
                showIndemnityContent()

                if (cartItemList[itemPosition].event != null && cartItemList[itemPosition].noOfEvent != null &&
                        cartItemList[itemPosition].isIndemnity != true) {
                    val cart = (activity as MainActivity).getBookingCart()
                    showCustomerInfo(cart?.payer)
                } else {
                    if (attendeePosition >= 0 && attendeePosition < cartItemList[itemPosition].attendees!!.size) {
                        if (cartItemList[itemPosition].attendees?.get(attendeePosition)?.isIndemnity != true) {
                            val attendee = cartItemList[itemPosition].attendees?.get(attendeePosition)?.customerInfo
                            showCustomerInfo(attendee)

                        } else {
                            attendeePosition++
                            handleAcceptanceProcess()
                        }
                    } else {
                        attendeePosition = 0
                        itemPosition++
                        isShowIndemnityContent = true
                        handleAcceptanceProcess()
                    }
                }
            } else {
                showIndemnityContent()
                showCustomerInfo(customerInfo)
                isShowIndemnityContent = true
            }

        } else {
            backCartView()
        }
    }

    private fun showIndemnityContent() {
        if (isShowIndemnityContent) {

            val productName: String?
            var description: String?

            if (cartItemList.get(itemPosition).classInfo != null) {
                productName = cartItemList.get(itemPosition).classInfo?.getClassTitle()
                description = cartItemList.get(itemPosition).classInfo?.getIndemnityDescription()
            }else if (cartItemList.get(itemPosition).igInfo != null){
                productName = cartItemList.get(itemPosition).igInfo?.igTitle
                description = cartItemList.get(itemPosition).igInfo?.indemnityDescription
            } else {
                productName = cartItemList.get(itemPosition).event?.eventTitle
                description = cartItemList.get(itemPosition).event?.indemnityDescription
            }

            getView?.txt_class_name?.text = productName

            if (!description.isNullOrEmpty()) {
                getView?.txt_indemnity?.text = description
            }
            isShowIndemnityContent = false
        }
    }

    private fun showCustomerInfo(customerInfo: CustomerInfo?) {
        getView?.txt_attendee_name?.text = customerInfo?.mFullName
        var nric = customerInfo?.mIdNo
        if (!nric.isNullOrEmpty() && nric.length >= 4) {
            nric = GeneralTextUtil.maskText(nric, nric.length - 4, true)
        }
        getView?.txt_attendee_nric?.text = nric
        couter++
        getView?.txt_counter?.text = getString(R.string.indenmity_couter, couter.toString(), total.toString())
    }

    private fun backCartView() {
        val intent = Intent()
        if (customerInfo == null) {
            intent.putExtra(CartFragment.ARG_IS_LOAD, hasChanged)
            targetFragment?.onActivityResult(CartFragment.LOAD_CART_CODE, Activity.RESULT_OK, intent)
        } else {
            intent.putExtra(CustomerVerificationPresenter.ARG_CUSTOMER_INFO, customerInfo)
            val productsWithIndemnityStr = Gson().toJson(productsWithIndemnity)
            intent.putExtra(CustomerVerificationPresenter.ARG_SELECTED_PRODUCT, productsWithIndemnityStr)
            intent.putExtra(CustomerVerificationFragment.ARG_EVENT_TICKET_POSITION, eventTicketPosition)
            intent.putExtra(CustomerVerificationFragment.ARG_TICKET_ENTITY_POSITION, ticketEntityPosition)
            intent.putExtra(CustomerVerificationFragment.ARG_PARTICIPANT_POSITION, participantPosition)
            intent.putExtra(CustomerVerificationFragment.ARG_CART_ITEM_POSITION, cartItemPosition)
            targetFragment?.onActivityResult(CustomerVerificationPresenter.ATTENDEE_REQUEST_CODE, Activity.RESULT_OK, intent)
        }
//        fragmentManager?.popBackStack()
        dismiss()
    }

    private fun handleClickEvent(isAccept: Boolean) {
        if (itemPosition >= 0 && itemPosition < cartItemList.size) {
            if (customerInfo == null) {
                if (cartItemList[itemPosition].event != null && cartItemList[itemPosition].noOfEvent != null &&
                        cartItemList[itemPosition].isIndemnity != true) {
                    if (isAccept) {
                        hasChanged = true
                    }
                    cartItemList[itemPosition].isIndemnity = isAccept

                    attendeePosition = 0
                    itemPosition++
                    isShowIndemnityContent = true
                } else {
                    if (attendeePosition >= 0 && attendeePosition < cartItemList[itemPosition].attendees!!.size &&
                            cartItemList[itemPosition].attendees?.get(attendeePosition)?.isIndemnity != true) {
                        if (isAccept) {
                            hasChanged = true
                        }
                        cartItemList[itemPosition].attendees?.get(attendeePosition)?.isIndemnity = isAccept
                    }

                    attendeePosition++
                }
            } else {
                val cartItem = cartItemList[itemPosition]
                var itemId: String? = null
                if (cartItem.classInfo != null && cartItem.classInfo?.getIndemnityRequired() == true) {
                    itemId = cartItem.classInfo?.getClassId()
                } else if (cartItem.igInfo != null && cartItem.igInfo?.indemnityRequired == true){
                    itemId = cartItem.igInfo?.igId
                } else if (cartItem.event != null && cartItem.event?.indemnityRequired == true) {
                    itemId = cartItem.event?.eventId
                }
                productsWithIndemnity[itemId] = isAccept
                itemPosition++
            }
        }
        handleAcceptanceProcess()
    }

    private var mLastClickTime = 0L

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < MainActivity.CLICK_TIMER) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        when (v?.id) {
            R.id.btn_accept -> {
                handleClickEvent(true)
            }

            R.id.btn_deny -> {
                handleClickEvent(false)
            }
            R.id.iv_close -> {
                dismiss()
            }
        }
    }
}
