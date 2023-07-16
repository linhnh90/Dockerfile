package com.styl.pa.modules.main.interactor


import android.content.Context
import android.os.Handler
import com.google.gson.Gson
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.entities.InitData
import com.styl.pa.entities.api.API
import com.styl.pa.entities.courseCategory.CourseCategoryList
import com.styl.pa.entities.courseCategory.EventCategoryList
import com.styl.pa.entities.generateToken.BookingResponse
import com.styl.pa.entities.generateToken.PageByOutletDetail
import com.styl.pa.entities.healthDevice.HealthDeviceRequest
import com.styl.pa.entities.journey.JourneyRequest
import com.styl.pa.entities.keepAlive.KeepAliveResponse
import com.styl.pa.entities.kioskactivation.KioskInfo
import com.styl.pa.entities.log.LastScreenLogRequest
import com.styl.pa.entities.main.KioskAuthenticationRequest
import com.styl.pa.entities.main.KioskUpdateRequest
import com.styl.pa.entities.ota.OtaCredentials
import com.styl.pa.entities.ota.OtaStatusRequest
import com.styl.pa.entities.ota.OtaStatusResponse
import com.styl.pa.entities.pacesRequest.ProductRequest
import com.styl.pa.entities.product.Product
import com.styl.pa.entities.product.ProductListResponse
import com.styl.pa.entities.proxy.EmptyRequest
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.entities.proxy.ProxyRequestHeader
import com.styl.pa.entities.screensaver.ScreensaverRequest
import com.styl.pa.entities.screensaver.ScreensaverResponse
import com.styl.pa.entities.search.SearchLocationRequest
import com.styl.pa.entities.search.SearchProductRequest
import com.styl.pa.entities.sendmail.SendMailResponse
import com.styl.pa.modules.base.BaseInteractor
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.main.IMainContract
import com.styl.pa.modules.main.presenter.MainPresenter
import com.styl.pa.services.IKioskServices
import com.styl.pa.services.ServiceGenerator
import com.styl.pa.utils.MySharedPref
import com.styl.pa.utils.StoreLogsUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.jetbrains.annotations.TestOnly
import java.util.concurrent.TimeUnit


/**
 * Created by trangpham on 9/3/2018
 */
class MainInteractor : IMainContract.IInteractor, BaseInteractor {

    var output2: IMainContract.IInteractorOutput2? = null
    var output3: IMainContract.IInteractorOutput3? = null
    var output4: IMainContract.IInteractorOutput4? = null

    private var kioskService = ServiceGenerator.createService(IKioskServices::class.java)

    constructor()

    @TestOnly
    fun setKioskService(services: IKioskServices) {
        kioskService = services
    }

    private val compositeDisposable = CompositeDisposable()
    private var refreshTokenInterVal: Disposable? = null

    override fun deleteCart(token: String?, userId: String?, cartId: String,
                            output: IBaseContract.IBaseInteractorOutput<String>?) {
        val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
        val proxyRequestDeleteCart = ProxyRequest(
                header,
                EmptyRequest(),
                ProxyRequest.GET_METHOD,
                "",
                API.deleteCart(cartId)
        )
        val disposable = kioskService.deleteCart(proxyRequestDeleteCart, token, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            handleResponseProxy(it, output)
                        },
                        {
                            onError(it, output)
                        }
                )
        compositeDisposable.add(disposable)
    }

    override fun addCourseToCart(token: String,
                                 userId: String,
                                 request: ProxyRequest<ProductRequest>,
                                 output: IBaseContract.IBaseInteractorOutput<BookingResponse>) {
        val disposable = kioskService.addCourseToCart(request, token, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            handleResponseProxy(it, output)
                        },
                        {
                            onError(it, output)
                        }
                )
        compositeDisposable.add(disposable)
    }

    override fun subscribeKioskInfo(subject: BehaviorSubject<String>) {
        val disposable = subject
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = {
                            StoreLogsUtils.logErrorMessage(-1, "Subscribe kiosk info error.")
                        },
                        onNext = {
                            val gson = Gson()
                            val kioskInfo = gson.fromJson(it, KioskInfo::class.java)
                            output2?.onKioskInfoUpdated(kioskInfo)
                        }
                )
        compositeDisposable.add(disposable)
    }

    override fun authenticateKiosk(request: KioskAuthenticationRequest?) {
        val disposable = kioskService.authenticateKiosk(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = {
                            onError(it, output2)
                        },
                        onNext = {
                            onResponse(it, output2)
                        }
                )
        compositeDisposable.add(disposable)
    }

    override fun updateKioskInfo(token: String?, request: KioskUpdateRequest?) {
        val disposable = kioskService.updateKioskInfo(token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = {
                            StoreLogsUtils.logErrorMessage(-1, "Update kiosk info failed!")
                        },
                        onNext = {

                        }
                )
        compositeDisposable.add(disposable)
    }

    override fun getKioskInfo(token: String?, kioskId: Int?) {
        val disposable = kioskService.getKioskInfo(token, kioskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = {
                            onError(it, output3)
                        },
                        onNext = {
                            onResponse(it, output3)
                        }
                )
        compositeDisposable.add(disposable)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun refreshToken(context: Context?, count: Long, request: KioskAuthenticationRequest?) {
        val timeUnit = TimeUnit.SECONDS
        val period : Long
        val delay : Long
        if (count == MainPresenter.DEFAULT_EXPIRY) {
            period = 10L
            delay = 0
        } else {
            refreshTokenInterVal?.dispose()
            refreshTokenInterVal = null
            period = if (count > 10) {
                count - 10
            } else {
                count
            }
            delay = 200
        }
        if (refreshTokenInterVal == null) {
            Handler().postDelayed(@ExcludeFromJacocoGeneratedReport {
                refreshTokenInterVal = Observable.interval(period, timeUnit)
                        .timeInterval()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe @ExcludeFromJacocoGeneratedReport {
                            authenticateKiosk(request)
                        }

            }, delay)
        }
    }

    override fun help(token: String?) {
        val disposable = kioskService.help(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = @ExcludeFromJacocoGeneratedReport {
                            onError(it, output4)
                        },
                        onNext = {
                            onResponse(it, output4)
                        }
                )
        compositeDisposable.add(disposable)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        refreshTokenInterVal?.dispose()
        compositeDisposable.clear()
    }

    override fun aliveTracking(token: String?, request: EmptyRequest, callback: IBaseContract.IBaseInteractorOutput<KeepAliveResponse>?) {
        val disposable = kioskService.aliveTracking(token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = @ExcludeFromJacocoGeneratedReport {
                            onError(it, callback)
                        },
                        onNext = {
                            onResponse(it, callback)
                        }
                )
        compositeDisposable.add(disposable)
    }

    override fun lastScreenLog(token: String?, request: LastScreenLogRequest) {
        val disposable = kioskService.trackingAbortAction(token, request)
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                        //nothing
                )
        compositeDisposable.add(disposable)
    }

    override fun deviceHealthUpdate(token: String?, request: HealthDeviceRequest) {
        val disposable = kioskService.healthUpdate(token, request)
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                        onError = {
                            StoreLogsUtils.logErrorMessage(-1, "Error sending health status")
                        }
                )
        compositeDisposable.add(disposable)
    }

    fun getLocationAndCategories(context: Context, token: String?, locationRequest: SearchLocationRequest, courseCategoryRequest: ProxyRequest<EmptyRequest>,
                                 eventCategoryRequest: ProxyRequest<EmptyRequest>,
                                 initCallback: IBaseContract.IBaseInteractorOutput<InitData>) {
        val disposable = kioskService.searchLocation(token, locationRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = {
                            StoreLogsUtils.logErrorMessage(-1, "Get Location failed!")
                            initCallback.onSuccess(null)
                        },
                        onNext = {
                            var pageByOutletDetail: PageByOutletDetail? = null
                            if (it.isSuccessful) {
                                pageByOutletDetail = it.body()
                            } else {
                                StoreLogsUtils.logErrorMessage(it.code()
                                        , it.message())
                            }

                            // no need categories, so set it to empty
                            val courseCategoryList = CourseCategoryList()
                            courseCategoryList.setCourseCategoryList(ArrayList())
                            val eventCategoryLis = EventCategoryList()
                            eventCategoryLis.eventCategoryList = ArrayList()

                            val response = InitData(pageByOutletDetail, courseCategoryList, eventCategoryLis)
                            initCallback.onSuccess(response)
                        }
                )

        compositeDisposable.add(disposable)
    }

    @ExcludeFromJacocoGeneratedReport
    fun getAllClassInfo(context: Context, data: String, pageIndex: Int?, pageSize: Int?,
                        allClassInfoCallback: IBaseContract.IBaseInteractorOutput<ProductListResponse>) {
        val token = MySharedPref(context).eKioskHeader

        val productSearchRequest = SearchProductRequest(
                "",
                Product.PRODUCT_COURSE,
                pageIndex,
                pageSize,
                null,
                null,
                null,
                null,
                null,
                data
        )

        val d = kioskService.searchProduct(token, productSearchRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        @ExcludeFromJacocoGeneratedReport
                        {
                            handleResponseSearch(it, allClassInfoCallback)
                        },
                        @ExcludeFromJacocoGeneratedReport
                        {
                            onError(it , allClassInfoCallback)
                        }
                )

        compositeDisposable.add(d)
    }

    @ExcludeFromJacocoGeneratedReport
    fun getAllEventInfo(context: Context, data: String, pageIndex: Int?, pageSize: Int?,
                        allEventInfoCallback: IBaseContract.IBaseInteractorOutput<ProductListResponse>) {
        val token = MySharedPref(context).eKioskHeader

        val productSearchRequest = SearchProductRequest(
            "",
            Product.PRODUCT_EVENT,
            pageIndex,
            pageSize,
            null,
            null,
            null,
            null,
            null,
            data
        )

        val d = kioskService.searchProduct(token, productSearchRequest)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                @ExcludeFromJacocoGeneratedReport
                {
                    handleResponseSearch(it, allEventInfoCallback)
                },
                @ExcludeFromJacocoGeneratedReport
                {
                    onError(it , allEventInfoCallback)
                }
            )

        compositeDisposable.add(d)
    }

    @ExcludeFromJacocoGeneratedReport
    fun getAllIG(context: Context, data: String, pageIndex: Int?, pageSize: Int?,
                        allIGCallback: IBaseContract.IBaseInteractorOutput<ProductListResponse>) {
        val token = MySharedPref(context).eKioskHeader

        val productSearchRequest = SearchProductRequest(
            "",
            Product.PRODUCT_INTEREST_GROUP,
            pageIndex,
            pageSize,
            null,
            null,
            null,
            null,
            null,
            data
        )

        val d = kioskService.searchProduct(token, productSearchRequest)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                @ExcludeFromJacocoGeneratedReport
                {
                    handleResponseSearch(it, allIGCallback)
                },
                @ExcludeFromJacocoGeneratedReport
                {
                    onError(it , allIGCallback)
                }
            )

        compositeDisposable.add(d)
    }

    override fun trackUserJourney(token: String, request: JourneyRequest?, output: IBaseContract.IBaseInteractorOutput<SendMailResponse>?) {
        val d = kioskService.trackUserJourney(token, request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = {
                            StoreLogsUtils.logErrorMessage(-1, "Track user journey error.")
                        }
                )
        compositeDisposable.add(d)
    }

    override fun reportOtaStatus(token: String?, request: OtaStatusRequest, callback: IBaseContract.IBaseInteractorOutput<OtaStatusResponse>?) {
        val disposable = kioskService.reportOtaStatus(token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = {
                            onError(it, callback)
                        },
                        onNext = {
                            onResponse(it, callback)
                        }
                )
        compositeDisposable.add(disposable)
    }

    override fun getListScreensaver(
        token: String?,
        request: ScreensaverRequest?,
        output: IBaseContract.IBaseInteractorOutput<ScreensaverResponse>
    ) {
        val disposable = kioskService.getListScreensaver(token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        @ExcludeFromJacocoGeneratedReport
                        {
                            onResponse(it, output)
                        },
                        {
                            onError(it, output)
                        }
                )
        compositeDisposable.add(disposable)
    }

    override fun getOtaCredentials(token: String?, request: EmptyRequest, callback: IBaseContract.IBaseInteractorOutput<OtaCredentials>) {
        val disposable = kioskService.getOtaCredentials(token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            onResponse(it, callback)
                        },
                        @ExcludeFromJacocoGeneratedReport {
                            onError(it, callback)
                        }
                )
        compositeDisposable.add(disposable)
    }

}

