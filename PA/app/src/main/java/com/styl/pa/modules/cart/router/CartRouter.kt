package com.styl.pa.modules.cart.router

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.styl.pa.R
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.pacesRequest.EventTicket
import com.styl.pa.enums.TagName
import com.styl.pa.modules.addParticipant.view.AddParticipantFragment
import com.styl.pa.modules.addParticipantEvent.view.AddParticipantEventFragment
import com.styl.pa.modules.cart.ICartContact
import com.styl.pa.modules.cart.view.CartFragment
import com.styl.pa.modules.checkout.view.CheckoutFragment
import com.styl.pa.modules.customerverification.view.CustomerVerificationFragment
import com.styl.pa.modules.indemnity.view.IndemnityFragment
import com.styl.pa.modules.selectTicketType.view.SelectTicketTypeFragment

/**
 * Created by Ngatran on 09/14/2018.
 */
class CartRouter(var view: Fragment?) : ICartContact.IRouter {

    override fun navigationCheckoutView(
        isCartUpdate: Boolean,
        isBookingMyself: Boolean,
        facilityParticipant: CustomerInfo?
    ) {
        val f = CheckoutFragment()
        val args = Bundle()
        args.putBoolean(CustomerVerificationFragment.ARG_IS_CART_UPDATE, isCartUpdate)
        args.putBoolean(CheckoutFragment.ARG_IS_BOOKING_MYSELF, isBookingMyself)
        args.putParcelable(CheckoutFragment.ARG_FACILITY_PARTICIPANT, facilityParticipant)
        f.arguments = args
        f.setTargetFragment(view, CartFragment.LOAD_CART_CODE)
        val ft = view?.fragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.CheckoutFragment.value)
        ft?.addToBackStack(TagName.CheckoutFragment.value)
        ft?.commit()
        if (view != null) {
            ft?.hide(view!!)
        }
    }

    override fun navigateIndemnityView() {
        val fragment = IndemnityFragment()
        fragment.setTargetFragment(view, CartFragment.LOAD_CART_CODE)
        if (view?.fragmentManager != null) {
            fragment.show(view?.fragmentManager!!, IndemnityFragment::class.java.simpleName)
        }
    }

    override fun navigateCustomerVerificationView(
        requestCode: Int,
        customerList: ArrayList<CustomerInfo>,
        isPayer: Boolean,
        eventTicketPosition: Int?,
        ticketEntityPosition: Int?,
        participantPosition: Int?,
        cartItemPosition: Int?
    ) {
        val fragment = CustomerVerificationFragment()
        val args = Bundle()
        args.putParcelableArrayList(CustomerVerificationFragment.ARG_CUSTOMER_LIST, customerList)
        args.putBoolean(CustomerVerificationFragment.ARG_IS_PAYER, isPayer)
        args.putInt(CustomerVerificationFragment.ARG_EVENT_TICKET_POSITION, eventTicketPosition ?: -1)
        args.putInt(CustomerVerificationFragment.ARG_TICKET_ENTITY_POSITION, ticketEntityPosition ?: -1)
        args.putInt(CustomerVerificationFragment.ARG_PARTICIPANT_POSITION, participantPosition ?: -1)
        args.putInt(CustomerVerificationFragment.ARG_CART_ITEM_POSITION, cartItemPosition ?: -1)
        fragment.arguments = args
        fragment.setTargetFragment(view, requestCode)
        if (view?.fragmentManager != null) {
            fragment.show(view?.fragmentManager!!, CustomerVerificationFragment::class.java.simpleName)
        }
    }

    override fun navigateHomeView() {
        view?.fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun navigateAddParticipantViewForFacility(requestCode: Int) {
        val fragment = AddParticipantFragment()
        fragment.setTargetFragment(view, requestCode)
        if (view?.fragmentManager != null){
            fragment.show(view?.fragmentManager!!, AddParticipantFragment::class.java.simpleName)
        }
    }

    override fun navigateSelectTicketTypeView(
        requestCode: Int,
        eventCode: String?,
        listEventTicket: ArrayList<EventTicket>?,
        selectedNumber: Int?
    ) {
        val fragment = SelectTicketTypeFragment.newInstance(
            eventCode = eventCode,
            listEventTicket = listEventTicket,
            selectedNumber = selectedNumber
        )
        fragment.setTargetFragment(view, requestCode)
        if (view?.fragmentManager != null){
            fragment.show(view?.fragmentManager!!, SelectTicketTypeFragment::class.java.simpleName)
        }
    }

    override fun navigateAddParticipantEvent(
        requestCode: Int,
        eventInfo: EventInfo?,
        cartItemPosition: Int,
        eventTicketPosition: Int,
        ticketEntityPosition: Int,
        participantPosition: Int
    ) {
        val fragment = AddParticipantEventFragment.newInstance(
            eventInfo = eventInfo,
            cartItemPosition = cartItemPosition,
            eventTicketPosition = eventTicketPosition,
            ticketEntityPosition = ticketEntityPosition,
            participantPosition = participantPosition
        )
        fragment.setTargetFragment(view, requestCode)
        if (view?.fragmentManager != null){
            fragment.show(view?.fragmentManager!!, AddParticipantEventFragment::class.java.simpleName)
        }
    }
}