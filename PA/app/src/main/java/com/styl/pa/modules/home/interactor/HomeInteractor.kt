package com.styl.pa.modules.home.interactor

import android.content.Context
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.entities.api.API
import com.styl.pa.entities.pacesRequest.CourseAvailabilityRequest
import com.styl.pa.entities.pacesRequest.IgAvailabilityRequest
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.entities.proxy.ProxyRequestHeader
import com.styl.pa.entities.recommendatetions.RecommendationResponse
import com.styl.pa.entities.vacancy.VacancyResponse
import com.styl.pa.modules.base.BaseInteractor
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.home.IHomeContact
import com.styl.pa.services.IKioskServices
import com.styl.pa.services.ServiceGenerator
import com.styl.pa.utils.MySharedPref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

/**
 * Created by Ngatran on 09/11/2018.
 */
class HomeInteractor : IHomeContact.IInteractor, BaseInteractor {

    var output: IHomeContact.IInteractorOutput? = null
    var context: Context? = null
    var servicesKiosk: IKioskServices = ServiceGenerator.createService(IKioskServices::class.java)

    constructor(output: IHomeContact.IInteractorOutput?, context: Context?) {
        this.context = context
        this.output = output
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        compositeDisposable.clear()
        output = null
    }

    fun searchClass(searchClassCallback: IBaseContract.IBaseInteractorOutput<RecommendationResponse>) {
        val token = MySharedPref(context).eKioskHeader
        val d = servicesKiosk.searchRecommendation(token).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { result: Response<RecommendationResponse> ->
                    handleResponseSearch(result, searchClassCallback)
                },
                @ExcludeFromJacocoGeneratedReport { e: Throwable ->
                    onError(e, searchClassCallback)
                },
                {

                }
        )
        compositeDisposable.add(d)
    }

    override fun getCourseAvailability(token: String?,
                                       courses: ArrayList<String?>,
                                       callback: IBaseContract.IBaseInteractorOutput<VacancyResponse>) {
        val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
        val request = ProxyRequest(
                header,
                CourseAvailabilityRequest(courses),
                ProxyRequest.POST_METHOD,
                "",
                API.uriGetCourseAvailability
        )
        val disposable = servicesKiosk.getCourseAvailability(request, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            handleResponseProxy(it, callback)
                        },
                        @ExcludeFromJacocoGeneratedReport {
                            onError(it, callback)
                        }
                )
        compositeDisposable.add(disposable)
    }

    override fun getIgAvailability(
        token: String?,
        igResourceIds: ArrayList<String?>,
        callback: IBaseContract.IBaseInteractorOutput<VacancyResponse>
    ) {
        val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
        val request = ProxyRequest(
            header,
            IgAvailabilityRequest(igResourceIds),
            ProxyRequest.POST_METHOD,
            "",
            API.uriGetIgAvailability
        )
        val disposable = servicesKiosk.getIgAvailability(request, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    handleResponseProxy(it, callback)
                },
                @ExcludeFromJacocoGeneratedReport {
                    onError(it, callback)
                }
            )
        compositeDisposable.add(disposable)
    }

}

