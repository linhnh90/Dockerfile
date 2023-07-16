package com.styl.pa.modules.addParticipantEvent.router

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.DialogFragment
import com.styl.pa.entities.event.Fields
import com.styl.pa.modules.addParticipantEvent.AddParticipantEventContract
import com.styl.pa.modules.addParticipantEvent.view.AddParticipantEventFragment

class AddParticipantEventRouter(var fragment: DialogFragment?) :
    AddParticipantEventContract.Router {
    override fun navigateToCart(
        requestCode: Int,
        cartItemPosition: Int,
        eventTicketPosition: Int,
        ticketEntityPosition: Int,
        participantPosition: Int,
        participantInfo: Fields?
    ) {
        val intent = Intent()
        intent.putExtra(AddParticipantEventFragment.ARG_CART_ITEM_POSITION, cartItemPosition)
        intent.putExtra(AddParticipantEventFragment.ARG_EVENT_TICKET_POSITION, eventTicketPosition)
        intent.putExtra(
            AddParticipantEventFragment.ARG_TICKET_ENTITY_POSITION,
            ticketEntityPosition
        )
        intent.putExtra(AddParticipantEventFragment.ARG_PARTICIPANT_POSITION, participantPosition)
        intent.putExtra(AddParticipantEventFragment.ARG_PARTICIPANT_INFO, participantInfo)
        fragment?.targetFragment?.onActivityResult(requestCode, Activity.RESULT_OK, intent)
        fragment?.dismiss()
    }
}