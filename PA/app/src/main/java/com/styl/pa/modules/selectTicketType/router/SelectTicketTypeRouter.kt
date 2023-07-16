package com.styl.pa.modules.selectTicketType.router

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.DialogFragment
import com.styl.pa.entities.pacesRequest.EventTicket
import com.styl.pa.modules.selectTicketType.SelectTicketTypeContract
import com.styl.pa.modules.selectTicketType.view.SelectTicketTypeFragment

class SelectTicketTypeRouter(var fragment: DialogFragment?): SelectTicketTypeContract.Router {
    override fun backToCartPage(
        requestCode: Int,
        eventCode: String,
        listEventTicket: ArrayList<EventTicket>?
    ) {
        val intent = Intent()
        intent.putExtra(SelectTicketTypeFragment.ARG_EVENT_CODE, eventCode)
        intent.putExtra(SelectTicketTypeFragment.ARG_LIST_EVENT_TICKET, listEventTicket)
        fragment?.targetFragment?.onActivityResult(requestCode, Activity.RESULT_OK, intent)
        fragment?.dismiss()
    }
}