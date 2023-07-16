package com.styl.pa.modules.home

import android.content.Context
import android.view.View
import android.widget.RadioGroup
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.generateToken.Facility
import com.styl.pa.entities.generateToken.Outlet
import com.styl.pa.entities.generateToken.PageByOutletDetail
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.entities.recommendatetions.RecommendationResponse
import com.styl.pa.entities.vacancy.VacancyResponse
import com.styl.pa.interfaces.OnClickRecyclerViewItem
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.vacancyChecking.IVacancyCheckingContact

/**
 * Created by Ngatran on 09/10/2018.
 */
interface IHomeContact {
    interface IView : IVacancyCheckingContact.IView, RadioGroup.OnCheckedChangeListener,
            View.OnClickListener, OnClickRecyclerViewItem {
//        fun onGetOutletByNameSuccess(outletDetail: PageByOutletDetail?)

        fun onSearchLocationByOutlet(outletDetail: PageByOutletDetail?)

        fun onGetClassByOutletSuccess(recommendationResponse: RecommendationResponse?)

        fun getContext(): Context?

//        fun onDismissScreenSaver()
    }

    interface IPresenter : IBaseContract.IBasePresenter {
        fun navigationSearchPage(searchType: String?, keyword: String?, location: Outlet?,
                                 classList: ArrayList<ClassInfo>?, isFromSearch: Boolean)

        fun navigationCourseDetailPage(classInfo: ClassInfo)
        fun navigationIgDetailPage(igInfo: InterestGroup)
        fun navigationEventDetailPage(eventInfo: EventInfo)
        fun navigationScreenSaverFragment()
    }

    interface IInteractor : IBaseContract.IBaseInteractor {

        fun getCourseAvailability(token: String?, courses: ArrayList<String?>, callback: IBaseContract.IBaseInteractorOutput<VacancyResponse>)
        fun getIgAvailability(token: String?, igResourceIds: ArrayList<String?>, callback: IBaseContract.IBaseInteractorOutput<VacancyResponse>)
    }

    interface IInteractorOutput {

    }

    interface IRouter : IBaseContract.IBaseRouter {
        fun navigationSearchPage(searchType: String?, keyword: String?, location: Outlet?,
                                 classList: ArrayList<ClassInfo>?, isFromSearch: Boolean)

        fun navigationCourseDetailPage(classInfo: ClassInfo)

        fun navigationFacilityDetailPage(facility: Facility, outlet: Outlet)

        fun navigationEventDetailPage(eventInfo: EventInfo)

        fun navigationIgDetailPage(igInfo: InterestGroup)

        fun navigationScreenSaverFragment()
    }
}