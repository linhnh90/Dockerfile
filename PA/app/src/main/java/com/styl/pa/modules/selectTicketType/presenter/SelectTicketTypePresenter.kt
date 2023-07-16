package com.styl.pa.modules.selectTicketType.presenter

import androidx.fragment.app.DialogFragment
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.R
import com.styl.pa.entities.pacesRequest.EventTicket
import com.styl.pa.modules.selectTicketType.SelectTicketTypeContract
import com.styl.pa.modules.selectTicketType.router.SelectTicketTypeRouter
import com.styl.pa.modules.selectTicketType.view.SelectTicketTypeFragment

class SelectTicketTypePresenter(var view: SelectTicketTypeContract.View?):
SelectTicketTypeContract.Presenter {

    private var router: SelectTicketTypeRouter? = SelectTicketTypeRouter(view as? DialogFragment)

    @ExcludeFromJacocoGeneratedReport
    override fun backToCartPage(
        requestCode: Int,
        eventCode: String,
        listEventTicket: ArrayList<EventTicket>?,
        selectedNumber: Int
    ) {
        //check limit ticket type
        var selectedCount = selectedNumber
        if (!listEventTicket.isNullOrEmpty()){
            for (e in listEventTicket){
                if (e.isSelected){
                    selectedCount++
                }
            }
        }
        if (selectedCount > SelectTicketTypeFragment.MAX_TICKET_TYPE_IN_EVENT){
            //show error
            val context = (view as SelectTicketTypeFragment).context
            val msg = context?.getString(R.string.you_can_select_ticket_types_in_max, SelectTicketTypeFragment.MAX_TICKET_TYPE_IN_EVENT.toString()) ?: "error"
            view?.showErrorMessage(msg)
        } else {
            this.router?.backToCartPage(
                requestCode = requestCode,
                eventCode = eventCode,
                listEventTicket = listEventTicket
            )
        }
    }


    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        router = null
    }
}