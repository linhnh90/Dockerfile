package com.styl.pa.modules.interestGroupDetails

import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.vacancyChecking.IVacancyCheckingContact

interface InterestGroupDetailsContract {
    interface View: IVacancyCheckingContact.IView {

    }

    interface Presenter: IBaseContract.IBasePresenter

    interface Interactor: IBaseContract.IBaseInteractor

    interface InteractorOutput: IBaseContract.IBaseInteractorOutput<InterestGroup>

    interface Router: IBaseContract.IBaseRouter
}