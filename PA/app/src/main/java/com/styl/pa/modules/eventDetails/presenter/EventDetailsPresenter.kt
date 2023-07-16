package com.styl.pa.modules.eventDetails.presenter

import android.content.Context
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.eventDetails.IEventDetailsContact
import com.styl.pa.modules.eventDetails.router.EventDetailsRouter
import com.styl.pa.modules.home.interactor.HomeInteractor
import com.styl.pa.modules.vacancyChecking.presenter.VacancyCheckingPresenter

/**
 * Created by Ngatran on 03/11/2019.
 */
@ExcludeFromJacocoGeneratedReport
class EventDetailsPresenter(var view: IEventDetailsContact.IEventDetailsView?, var context: Context?) : IEventDetailsContact.IEventDetailsPresenter,
        VacancyCheckingPresenter() {
    private var router: EventDetailsRouter? = EventDetailsRouter(view as BaseFragment)
    private var homeInteractor: HomeInteractor? = HomeInteractor(null, context)

    init {
        initVacancyPresenter(view, context, homeInteractor)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationCardPage(eventInfo: EventInfo?) {
        router?.navigationCardPage(eventInfo)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        view = null
        router = null
        homeInteractor?.onDestroy()
        homeInteractor = null
    }
}