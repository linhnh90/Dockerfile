package com.styl.pa.modules.addParticipant

import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.modules.base.IBaseContract

interface AddParticipantContract {
    interface View: IBaseContract.IBaseView{
        fun showErrorInput(errorFullName: String, errorPhone: String, errorEmail: String)
    }

    interface Presenter: IBaseContract.IBasePresenter{
        fun navigateToCartPage(participantInfo: CustomerInfo?)
        fun validateInput(fullName: String, phone: String, email: String)
    }

    interface Interactor: IBaseContract.IBaseInteractor{
    }

    interface Router: IBaseContract.IBaseRouter{
        fun navigateToCartPage(requestCode: Int, participantInfo: CustomerInfo?)
    }
}