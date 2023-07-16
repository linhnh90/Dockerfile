package com.styl.pa.modules.addParticipantEvent.presenter

import androidx.fragment.app.DialogFragment
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.entities.event.Fields
import com.styl.pa.modules.addParticipantEvent.AddParticipantEventContract
import com.styl.pa.modules.addParticipantEvent.router.AddParticipantEventRouter

class AddParticipantEventPresenter(var view: AddParticipantEventContract.View?) :
    AddParticipantEventContract.Presenter {
    private var router: AddParticipantEventRouter? =
        AddParticipantEventRouter(view as? DialogFragment)

    @ExcludeFromJacocoGeneratedReport
    override fun navigateToCart(
        requestCode: Int,
        cartItemPosition: Int,
        eventTicketPosition: Int,
        ticketEntityPosition: Int,
        participantPosition: Int,
        participantInfo: Fields?
    ) {
        this.router?.navigateToCart(
            requestCode,
            cartItemPosition,
            eventTicketPosition,
            ticketEntityPosition,
            participantPosition,
            participantInfo
        )
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        router = null
    }
}