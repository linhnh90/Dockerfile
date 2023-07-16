package com.styl.pa.modules.search

import android.content.Context
import android.view.View
import com.styl.pa.customViews.recyclerviewLoadMore.OnLoadMoreListener
import com.styl.pa.entities.advancedSearch.AdvancedSearchRequest
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.event.EventResponse
import com.styl.pa.entities.generateToken.*
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.entities.interestgroup.PageByInterestGroup
import com.styl.pa.entities.product.ProductListResponse
import com.styl.pa.entities.proximity.ProximityLocationInfo
import com.styl.pa.entities.proximity.ProximityLocationResponse
import com.styl.pa.entities.search.PriceClass
import com.styl.pa.entities.search.SearchProductRequest
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.home.ISelectedLocation
import com.styl.pa.modules.vacancyChecking.IVacancyCheckingContact

/**
 * Created by Ngatran on 09/12/2018.
 */
interface ISearchContact {
    interface IView : IVacancyCheckingContact.IView, OnLoadMoreListener, View.OnClickListener, ISelectedLocation {

        fun onGetClassSuccess(classInfo: PageByClassInfo?, request: AdvancedSearchRequest)

        fun onGetInterestGroupSuccess(igInfo: PageByInterestGroup?, request: AdvancedSearchRequest)

        fun onGetFacilitySuccess(facilities: PageByFacility?)

        fun onGetEventSuccess(response: EventResponse?, request: AdvancedSearchRequest)

        fun onLocationByOutlet(pageByOutletDetail: PageByOutletDetail?)

        fun showTotalRecord(total: Int)

        fun proximityLocationInfoResult(response: ProximityLocationResponse?, request: AdvancedSearchRequest)
        fun searchLocationCourse(proximityLocationInfo: ProximityLocationInfo?, isReset: Boolean)
        fun searchAllOfProximityCourse()
        fun searchProximity()

        fun getNearestProximity(): Outlet?
        fun getDistanceSelection(): String?
        fun getPrice(): PriceClass


        fun getContext(): Context?
        fun onSearchProductSuccess(data: ProductListResponse?)

    }

    interface IPresenter : IBaseContract.IBasePresenter, IVacancyCheckingContact.IPresenter {
        fun navigationCourseDetailPage(classInfo: ClassInfo)

        fun navigationFacilityDetailPage(facility: Facility, outlet: Outlet)

        fun navigationEventDetailPage(eventInfo: EventInfo)
        fun navigationIgDetailPage(igInfo: InterestGroup)

        fun searchProximity(keyword: String?, nearestLocation: String, distance: Int, price: PriceClass?)
        fun searchProximity(keyword: String?, nearestLocation: String, distance: Int, price: PriceClass?, productType: String?)

        fun searchProduct(keywords: String?, type: String?, price: PriceClass, sort: String?, outlets: ArrayList<String>?, longitude: Double? = null, latitude: Double? = null)
    }

    interface IHandleProximity {
        fun initProximityView()
        fun showProximityLocationInfo(distance: Int, currentLocation: Outlet?, response: ProximityLocationResponse?)
        fun destroyProximityView()
    }

    interface IInteractor : IBaseContract.IBaseInteractor {
        fun searchProximityLocations(request: AdvancedSearchRequest, output: IBaseContract.IBaseInteractorOutput<ProximityLocationResponse>)
        fun searchProximityLocationsInterestGroup(request: AdvancedSearchRequest, output: IBaseContract.IBaseInteractorOutput<ProximityLocationResponse>)
        fun searchProximityLocationsEvent(request: AdvancedSearchRequest, output: IBaseContract.IBaseInteractorOutput<ProximityLocationResponse>)

        fun searchProduct(token: String?, request: SearchProductRequest, output: IBaseContract.IBaseInteractorOutput<ProductListResponse>)
    }

    interface IInteractorOutput {

    }

    interface IRouter : IBaseContract.IBaseRouter {
        fun navigationCourseDetailPage(classInfo: ClassInfo)
        fun navigationIgDetailPage(igInfo: InterestGroup)
    }
}