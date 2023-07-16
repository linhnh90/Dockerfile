package com.styl.pa.modules.addParticipantEvent

import com.styl.pa.entities.event.Fields
import com.styl.pa.modules.base.IBaseContract

interface AddParticipantEventContract {
    interface View: IBaseContract.IBaseView{

    }

    interface Presenter: IBaseContract.IBasePresenter{
        fun navigateToCart(
            requestCode: Int,
            cartItemPosition: Int,
            eventTicketPosition: Int,
            ticketEntityPosition: Int,
            participantPosition: Int,
            participantInfo: Fields?
        )
    }

    interface Router: IBaseContract.IBaseRouter{
        fun navigateToCart(
            requestCode: Int,
            cartItemPosition: Int,
            eventTicketPosition: Int,
            ticketEntityPosition: Int,
            participantPosition: Int,
            participantInfo: Fields?
        )
    }
}