package com.styl.pa.modules.eventDetails

import android.content.Context
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.vacancyChecking.IVacancyCheckingContact

/**
 * Created by Ngatran on 03/11/2019.
 */
interface IEventDetailsContact {
    interface IEventDetailsView : IVacancyCheckingContact.IView {
        fun getContext(): Context?
    }

    interface IEventDetailsPresenter : IBaseContract.IBasePresenter {
        fun navigationCardPage(eventInfo: EventInfo?)
    }

    interface IEventDetailsRouter : IBaseContract.IBaseRouter {
        fun navigationCardPage(eventInfo: EventInfo?)
    }
}