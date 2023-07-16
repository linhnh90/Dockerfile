package com.styl.pa.modules.selectTicketType

import com.styl.pa.entities.pacesRequest.EventTicket
import com.styl.pa.modules.base.IBaseContract

interface SelectTicketTypeContract {
    interface View: IBaseContract.IBaseView{
        fun showErrorMessage(msg: String)
    }

    interface Presenter: IBaseContract.IBasePresenter{
        fun backToCartPage(
            requestCode: Int,
            eventCode: String,
            listEventTicket: ArrayList<EventTicket>?,
            selectedNumber: Int
        )
    }

    interface Interactor: IBaseContract.IBaseInteractor{

    }

    interface Router: IBaseContract.IBaseRouter{
        fun backToCartPage(
            requestCode: Int,
            eventCode: String,
            listEventTicket: ArrayList<EventTicket>?
        )
    }
}