package com.styl.pa.modules.search.interactor

import android.content.Context
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.entities.advancedSearch.AdvancedSearchRequest
import com.styl.pa.entities.event.EventResponse
import com.styl.pa.entities.generateToken.PageByClassInfo
import com.styl.pa.entities.generateToken.PageByFacility
import com.styl.pa.entities.generateToken.PageByOutletDetail
import com.styl.pa.entities.interestgroup.PageByInterestGroup
import com.styl.pa.entities.product.ProductListResponse
import com.styl.pa.entities.proximity.ProximityLocationResponse
import com.styl.pa.entities.search.SearchLocationRequest
import com.styl.pa.entities.search.SearchProductRequest
import com.styl.pa.entities.search.SearchRequest
import com.styl.pa.modules.base.BaseInteractor
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.search.ISearchContact
import com.styl.pa.services.IKioskServices
import com.styl.pa.services.ServiceGenerator
import com.styl.pa.utils.MySharedPref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

/**
 * Created by Ngatran on 09/12/2018.
 */
class SearchInteractor : ISearchContact.IInteractor, BaseInteractor {

    private val compositeDisposable = CompositeDisposable()
    var output: ISearchContact.IInteractorOutput? = null
    var context: Context? = null
    var servicesKiosk: IKioskServices = ServiceGenerator.createService(IKioskServices::class.java)

    constructor(output: ISearchContact.IInteractorOutput?, context: Context?) {
        this.output = output
        this.context = context
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        compositeDisposable.clear()
        output = null
    }

    fun searchClass(request: AdvancedSearchRequest,
                    searchClassCallback: IBaseContract.IBaseInteractorOutput<PageByClassInfo>) {
        val token = MySharedPref(context).eKioskHeader
        val d = servicesKiosk.searchClass(token, request).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { result: Response<PageByClassInfo> ->
                    handleResponseSearch(result, searchClassCallback)
                },
                { e: Throwable ->
                    onError(e, searchClassCallback)
                }
        )

        compositeDisposable.add(d)
    }

    fun searchFacility(request: SearchRequest,
                       searchFacilityCallback: IBaseContract.IBaseInteractorOutput<PageByFacility>) {
        val token = MySharedPref(context).eKioskHeader
        val d = servicesKiosk.searchFacility(token, request).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { result: Response<PageByFacility> ->
                    handleResponseSearch(result, searchFacilityCallback)
                },
                { e: Throwable ->
                    onError(e, searchFacilityCallback)
                }
        )

        compositeDisposable.add(d)
    }

    fun searchInterestGroup(
        request: AdvancedSearchRequest,
        output: IBaseContract.IBaseInteractorOutput<PageByInterestGroup>
    ){
        val token = MySharedPref(context).eKioskHeader
        val d = servicesKiosk.searchInterestGroup(token, request).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(
            { result: Response<PageByInterestGroup> ->
                handleResponseSearch(result, output)
            },
            { e: Throwable ->
                onError(e, output)
            }
        )

        compositeDisposable.add(d)
    }

    fun searchLocation(request: SearchLocationRequest,
                       searchLocationCallback: IBaseContract.IBaseInteractorOutput<PageByOutletDetail>) {
        val token = MySharedPref(context).eKioskHeader
        val d = servicesKiosk.searchLocation(token, request).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { result: Response<PageByOutletDetail> ->
                    handleResponseSearch(result, searchLocationCallback)
                },
                { e: Throwable ->
                    onError(e, searchLocationCallback)
                }
        )

        compositeDisposable.add(d)
    }

    fun advancesSearch(token: String?, request: AdvancedSearchRequest?, callback: IBaseContract.IBaseInteractorOutput<PageByClassInfo>) {
        val d = servicesKiosk.advancedSearch(token, request).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { result: Response<PageByClassInfo> ->
                    handleResponseSearch(result, callback)
                },
                { e: Throwable ->
                    onError(e, callback)
                }
        )

        compositeDisposable.add(d)
    }

    fun searchEvent(request: AdvancedSearchRequest, output: IBaseContract.IBaseInteractorOutput<EventResponse>) {
        val token = MySharedPref(context).eKioskHeader
        val d = servicesKiosk.searchEvent(token, request).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    onError(it, output)
                },
                onNext = {
                    handleResponseSearch(it, output)
                }
        )

        compositeDisposable.add(d)
    }

    override fun searchProximityLocations(request: AdvancedSearchRequest, output: IBaseContract.IBaseInteractorOutput<ProximityLocationResponse>) {
        val token = MySharedPref(context).eKioskHeader
        val d = servicesKiosk.searchProximityLocations(token, request).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    onError(it, output)
                },
                onNext = {
                    handleResponseSearch(it, output)
                }
        )

        compositeDisposable.add(d)
    }

    override fun searchProximityLocationsInterestGroup(
        request: AdvancedSearchRequest,
        output: IBaseContract.IBaseInteractorOutput<ProximityLocationResponse>
    ) {
        val token = MySharedPref(context).eKioskHeader
        val d = servicesKiosk.searchProximityLocationsInterestGroup(token, request).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribeBy(
            onError = {
                onError(it, output)
            },
            onNext = {
                handleResponseSearch(it, output)
            }
        )

        compositeDisposable.add(d)
    }

    override fun searchProximityLocationsEvent(
        request: AdvancedSearchRequest,
        output: IBaseContract.IBaseInteractorOutput<ProximityLocationResponse>
    ) {
        val token = MySharedPref(context).eKioskHeader
        val d = servicesKiosk.searchProximityLocationsEvent(token, request).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribeBy(
            onError = {
                onError(it, output)
            },
            onNext = {
                handleResponseSearch(it, output)
            }
        )

        compositeDisposable.add(d)
    }

    override fun searchProduct(token: String?, request: SearchProductRequest, output: IBaseContract.IBaseInteractorOutput<ProductListResponse>) {
        val disposable = servicesKiosk.searchProduct(token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            handleResponseSearch(it, output)
                        },
                        {
                            onError(it, output)
                        }
                )

        compositeDisposable.add(disposable)
    }

}