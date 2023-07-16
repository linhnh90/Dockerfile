package com.styl.pa.modules.search.presenter

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.R
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.advancedSearch.AdvancedSearchRequest
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.event.EventResponse
import com.styl.pa.entities.generateToken.*
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.entities.interestgroup.PageByInterestGroup
import com.styl.pa.entities.product.Product
import com.styl.pa.entities.product.ProductListResponse
import com.styl.pa.entities.proximity.ProximityLocationResponse
import com.styl.pa.entities.search.PriceClass
import com.styl.pa.entities.search.SearchLocationRequest
import com.styl.pa.entities.search.SearchProductRequest
import com.styl.pa.enums.SearchType
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.home.interactor.HomeInteractor
import com.styl.pa.modules.home.router.HomeRouter
import com.styl.pa.modules.search.ISearchContact
import com.styl.pa.modules.search.interactor.SearchInteractor
import com.styl.pa.modules.vacancyChecking.presenter.VacancyCheckingPresenter
import com.styl.pa.utils.MySharedPref

/**
 * Created by Ngatran on 09/12/2018.
 */
class SearchPresenter(var view: ISearchContact.IView?, var context: Context?) : ISearchContact.IPresenter, ISearchContact.IInteractorOutput,
        VacancyCheckingPresenter() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    constructor(view: ISearchContact.IView?, context: Context?, interactor: SearchInteractor?): this(view, context) {
        this.interactor = interactor
    }

    private var interactor: SearchInteractor? = SearchInteractor(this, context)

    private var homeInteractor: HomeInteractor? = HomeInteractor(null, context)

    @ExcludeFromJacocoGeneratedReport
    private var router: HomeRouter? = HomeRouter(view as? BaseFragment)
    private var pageSizeAdvancedSearch: Int = 20
    private var hashQueryNext: String? = ""

    init {
        initVacancyPresenter(view, context, homeInteractor)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationCourseDetailPage(classInfo: ClassInfo) {
        router?.navigationCourseDetailPage(classInfo)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationFacilityDetailPage(facility: Facility, outlet: Outlet) {
        router?.navigationFacilityDetailPage(facility, outlet)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationEventDetailPage(eventInfo: EventInfo) {
        router?.navigationEventDetailPage(eventInfo)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationIgDetailPage(igInfo: InterestGroup) {
        router?.navigationIgDetailPage(igInfo)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        interactor?.onDestroy()
        view = null
        interactor = null
        router = null
        homeInteractor?.onDestroy()
        homeInteractor = null
    }

    //location
    private var locationCallback = object : IBaseContract.IBaseInteractorOutput<PageByOutletDetail> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: PageByOutletDetail?) {
            view?.onLocationByOutlet(data)
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<PageByOutletDetail>) {
            view?.showErrorMessage(data)
        }

    }

    fun getOutletByOutletType(outletTypeName: String?) {
        val request = SearchLocationRequest("", pageIndexLocation, pageSizeLocation)
        interactor?.searchLocation(request, locationCallback)
    }

    fun getMoreOutlet(outletTypeName: String?) {
        pageIndexLocation = pageIndexLocation.inc()
        getOutletByOutletType(outletTypeName)
    }


    //search
    private var pageIndex: Int = 1
    private var pageSize: Int = 20
    private var pageIndexLocation: Int = 1
    private var pageSizeLocation = 300

    private fun searchClassCallback(advancedSearchRequest: AdvancedSearchRequest): IBaseContract.IBaseInteractorOutput<PageByClassInfo> {
        return object : IBaseContract.IBaseInteractorOutput<PageByClassInfo> {
            @ExcludeFromJacocoGeneratedReport
            override fun onSuccess(data: PageByClassInfo?) {
                view?.onGetClassSuccess(data, advancedSearchRequest)
            }

            @ExcludeFromJacocoGeneratedReport
            override fun onError(data: BaseResponse<PageByClassInfo>) {
                view?.showErrorMessage(data)
            }

        }
    }

    private fun searchIgCallback(request: AdvancedSearchRequest) = object : IBaseContract.IBaseInteractorOutput<PageByInterestGroup>{
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: PageByInterestGroup?) {
            view?.onGetInterestGroupSuccess(data, request)
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<PageByInterestGroup>) {
            view?.showErrorMessage(data)
        }

    }

    private var searchFacilityCallback = object : IBaseContract.IBaseInteractorOutput<PageByFacility> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: PageByFacility?) {
            if ((data?.getFacilityList()?.size?: 0) > 0) {
                view?.onGetFacilitySuccess(data)
            } else {
                view?.dismissLoading()
                view?.showErrorMessage(R.string.pls_try_again, R.string.try_another_cc)
            }
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<PageByFacility>) {
            view?.showErrorMessage(data)
        }

    }

    private fun searchEventOutput(request: AdvancedSearchRequest) = object : IBaseContract.IBaseInteractorOutput<EventResponse> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: EventResponse?) {
            view?.onGetEventSuccess(data, request)
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<EventResponse>) {
            view?.showErrorMessage(data)
        }

    }

    fun searchByClass(keyword: String?, outletIds: ArrayList<String>?, outletNames: ArrayList<String>,
                      price: PriceClass?, days: ArrayList<Int>?, searchType: String?) {
        val request = AdvancedSearchRequest(keyword, outletIds, outletNames, price, days, pageIndex, pageSize)
        when (searchType){
            Product.PRODUCT_EVENT -> {
                interactor?.searchEvent(request, searchEventOutput(request))
            }
            Product.PRODUCT_FACILITY -> {
                request.outletId = null
                interactor?.searchFacility(request, searchFacilityCallback)
            }
            Product.PRODUCT_INTEREST_GROUP -> {
                interactor?.searchInterestGroup(request, searchIgCallback(request))
            }
            else -> {
                interactor?.searchClass(request, searchClassCallback(request))
            }
        }
    }

    fun searchByClass(keyword: String?, nearestLocation: String, distance: Int, price: PriceClass?, days: ArrayList<Int>, searchType: String?) {
        val request = AdvancedSearchRequest(keyword, nearestLocation, distance, price, days, pageIndex, pageSize)
        when(searchType) {
            Product.PRODUCT_EVENT -> {
                interactor?.searchEvent(request, searchEventOutput(request))
            }
            Product.PRODUCT_FACILITY -> {
                request.outletId = null
                interactor?.searchFacility(request, searchFacilityCallback)
            }
            Product.PRODUCT_INTEREST_GROUP -> {
                interactor?.searchInterestGroup(request, searchIgCallback(request))
            }
            else -> {
                interactor?.searchClass(request, searchClassCallback(request))
            }
        }
    }

    fun reloadSearch() {
        pageIndex = 1
    }

    fun loadMore() {
        pageIndex = pageIndex.inc()
    }

    fun loadMore(keyword: String?, outletIds: ArrayList<String>?, outletNames: ArrayList<String>,
                 price: PriceClass?, days: ArrayList<Int>, searchType: String?) {
        pageIndex = pageIndex.inc()
        searchByClass(keyword, outletIds, outletNames, price, days, searchType)
    }

    fun loadMore(keyword: String?, nearestLocation: String, distance: Int,
                 price: PriceClass?, days: ArrayList<Int>, searchType: String?) {
        pageIndex = pageIndex.inc()
        searchByClass(keyword, nearestLocation, distance, price, days, searchType)
    }

    fun loadMore(keywords: String?, type: String?, price: PriceClass, sort: String?, outlets: ArrayList<String>?, longitude: Double? = null, latitude: Double? = null) {
        pageIndex = pageIndex.inc()
        searchProduct(keywords, type, price, sort, outlets, longitude, latitude)
    }

    fun advancedSearch(advancedSearchRequest: AdvancedSearchRequest?, searchType: String?) {
        val request = advancedSearchRequest
        request?.pageSize = pageSize
        request?.pageIndex = pageIndex

        if (request != null) {
            if (true == searchType?.equals(SearchType.EVENTS.toString())) {
                interactor?.searchEvent(request, searchEventOutput(request))
            } else if (searchType?.equals(SearchType.COURSES.toString()) == true) {
                interactor?.searchClass(request, searchClassCallback(request))
            } else if (searchType == SearchType.INTEREST_GROUPS.toString()){
                interactor?.searchInterestGroup(request, searchIgCallback(request))
            }
        }
    }

    fun loadMore(advancedSearchRequest: AdvancedSearchRequest?, searchType: String?) {
        pageIndex = pageIndex.inc()
        advancedSearch(advancedSearchRequest, searchType)
    }

    override fun searchProximity(keyword: String?, nearestLocation: String, distance: Int, price: PriceClass?) {
        view?.showLoading()
        val request = AdvancedSearchRequest(keyword, nearestLocation, distance, price)
        interactor?.searchProximityLocations(request, object : IBaseContract.IBaseInteractorOutput<ProximityLocationResponse> {
            @ExcludeFromJacocoGeneratedReport
            override fun onSuccess(data: ProximityLocationResponse?) {
                view?.dismissLoading()
                if (data != null) {
                    view?.proximityLocationInfoResult(data, request)
                } else {
                    view?.showErrorMessage(R.string.proximity_empty)
                }
            }

            @ExcludeFromJacocoGeneratedReport
            override fun onError(data: BaseResponse<ProximityLocationResponse>) {
                view?.dismissLoading()
                view?.showErrorMessage(data)
            }

        })
    }

    override fun searchProximity(
        keyword: String?,
        nearestLocation: String,
        distance: Int,
        price: PriceClass?,
        productType: String?
    ) {
        view?.showLoading()
        val request = AdvancedSearchRequest(keyword, nearestLocation, distance, price)
        if (productType?.equals(Product.PRODUCT_INTEREST_GROUP) == true){
            interactor?.searchProximityLocationsInterestGroup(request, object : IBaseContract.IBaseInteractorOutput<ProximityLocationResponse> {
                @ExcludeFromJacocoGeneratedReport
                override fun onSuccess(data: ProximityLocationResponse?) {
                    view?.dismissLoading()
                    if (data != null) {
                        view?.proximityLocationInfoResult(data, request)
                    } else {
                        view?.showErrorMessage(R.string.proximity_empty)
                    }
                }

                @ExcludeFromJacocoGeneratedReport
                override fun onError(data: BaseResponse<ProximityLocationResponse>) {
                    view?.dismissLoading()
                    view?.showErrorMessage(data)
                }

            })
        }else if (productType?.equals(Product.PRODUCT_EVENT) == true){
            interactor?.searchProximityLocationsEvent(request, object : IBaseContract.IBaseInteractorOutput<ProximityLocationResponse> {
                @ExcludeFromJacocoGeneratedReport
                override fun onSuccess(data: ProximityLocationResponse?) {
                    view?.dismissLoading()
                    if (data != null) {
                        view?.proximityLocationInfoResult(data, request)
                    } else {
                        view?.showErrorMessage(R.string.proximity_empty)
                    }
                }

                @ExcludeFromJacocoGeneratedReport
                override fun onError(data: BaseResponse<ProximityLocationResponse>) {
                    view?.dismissLoading()
                    view?.showErrorMessage(data)
                }

            })
        } else {
            interactor?.searchProximityLocations(request, object : IBaseContract.IBaseInteractorOutput<ProximityLocationResponse> {
                @ExcludeFromJacocoGeneratedReport
                override fun onSuccess(data: ProximityLocationResponse?) {
                    view?.dismissLoading()
                    if (data != null) {
                        view?.proximityLocationInfoResult(data, request)
                    } else {
                        view?.showErrorMessage(R.string.proximity_empty)
                    }
                }

                @ExcludeFromJacocoGeneratedReport
                override fun onError(data: BaseResponse<ProximityLocationResponse>) {
                    view?.dismissLoading()
                    view?.showErrorMessage(data)
                }

            })
        }
    }


    private val searchProductResult = object : IBaseContract.IBaseInteractorOutput<ProductListResponse> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: ProductListResponse?) {
            view?.dismissLoading()
            view?.onSearchProductSuccess(data)
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<ProductListResponse>) {
            view?.dismissLoading()
            view?.showErrorMessage(data)
        }

    }

    override fun searchProduct(keywords: String?, type: String?, price: PriceClass, sort: String?, outlets: ArrayList<String>?, longitude: Double?, latitude: Double?) {

        val request = SearchProductRequest(
                keywords,
                type,
                pageIndex,
                pageSize,
                longitude,
                latitude,
                price,
                sort,
                outlets
        )

        val token = MySharedPref(context).eKioskHeader

        interactor?.searchProduct(token, request, searchProductResult)

    }
}