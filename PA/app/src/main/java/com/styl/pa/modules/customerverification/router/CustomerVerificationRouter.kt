package com.styl.pa.modules.customerverification.router

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.modules.customerverification.CustomerVerificationContract
import com.styl.pa.modules.customerverification.presenter.CustomerVerificationPresenter
import com.styl.pa.modules.customerverification.view.CustomerVerificationFragment
import com.styl.pa.modules.indemnity.view.IndemnityFragment

class CustomerVerificationRouter(var fragment: DialogFragment?): CustomerVerificationContract.Router {

    override fun navigateCartPage(
        requestCode: Int,
        customerInfo: CustomerInfo?,
        selectedProducts: Array<String?>,
        isCartUpdate: Boolean,
        cartItemPosition: Int?
    ) {
        val intent = Intent()
        intent.putExtra(CustomerVerificationPresenter.ARG_CUSTOMER_INFO, customerInfo)
        intent.putExtra(CustomerVerificationPresenter.ARG_SELECTED_PRODUCT, selectedProducts)
        intent.putExtra(CustomerVerificationFragment.ARG_IS_CART_UPDATE, isCartUpdate)
        intent.putExtra(CustomerVerificationFragment.ARG_CART_ITEM_POSITION, cartItemPosition)
        fragment?.targetFragment?.onActivityResult(requestCode, Activity.RESULT_OK, intent)
        fragment?.dismiss()
    }

    override fun navigateIndemnityView(
        requestCode: Int,
        customerInfo: CustomerInfo?,
        selectedProducts: Array<String?>,
        eventTicketPosition: Int?,
        ticketEntityPosition: Int?,
        participantPosition: Int?,
        cartItemPosition: Int?
    ) {
        val indemnityFragment = IndemnityFragment()
        val arg = Bundle()
        arg.putParcelable(CustomerVerificationFragment.ARG_CUSTOMER, customerInfo)
        arg.putStringArray(CustomerVerificationFragment.ARG_SELECTED_PRODUCTS, selectedProducts)
        arg.putInt(CustomerVerificationFragment.ARG_EVENT_TICKET_POSITION, eventTicketPosition ?: -1)
        arg.putInt(CustomerVerificationFragment.ARG_TICKET_ENTITY_POSITION, ticketEntityPosition ?: -1)
        arg.putInt(CustomerVerificationFragment.ARG_PARTICIPANT_POSITION, participantPosition ?: -1)
        arg.putInt(CustomerVerificationFragment.ARG_CART_ITEM_POSITION, cartItemPosition ?: -1)
        indemnityFragment.arguments = arg
        indemnityFragment.setTargetFragment(fragment?.targetFragment, requestCode)
        if (fragment?.fragmentManager != null) {
            indemnityFragment.show(fragment?.fragmentManager!!, IndemnityFragment::class.java.simpleName)
        }
        fragment?.dismiss()
    }
}