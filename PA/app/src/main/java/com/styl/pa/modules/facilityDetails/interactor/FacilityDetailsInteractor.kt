package com.styl.pa.modules.facilityDetails.interactor

import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.entities.api.API
import com.styl.pa.entities.facility.BookingByEmailRequest
import com.styl.pa.entities.facility.FacilitySessionResponse
import com.styl.pa.entities.proxy.EmptyRequest
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.entities.proxy.ProxyRequestHeader
import com.styl.pa.entities.rulesAndRegulations.RulesAndRegulationsResponse
import com.styl.pa.entities.sendmail.SendMailResponse
import com.styl.pa.modules.base.BaseInteractor
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.facilityDetails.IFacilityDetailsContact
import com.styl.pa.services.IKioskServices
import com.styl.pa.services.ServiceGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class FacilityDetailsInteractor : IFacilityDetailsContact.IInteractor, BaseInteractor() {

    private val compositeDisposable = CompositeDisposable()
    var services = ServiceGenerator.createService(IKioskServices::class.java)

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        compositeDisposable.clear()
    }

    override fun getFacilityAvailability(token: String, facilityId: String, selectedDate: String,
                                         output: IBaseContract.IBaseInteractorOutput<FacilitySessionResponse>) {
        val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
        val request = ProxyRequest(
                header,
                EmptyRequest(),
                ProxyRequest.GET_METHOD,
                "",
                API.getFacilityAvailability(facilityId, selectedDate)
        )
        val disposable = services.getFacilityAvailability(request, token)
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

    @ExcludeFromJacocoGeneratedReport
    override fun sendEmail(token: String, request: BookingByEmailRequest, event: IBaseContract.IBaseInteractorOutput<SendMailResponse>) {
        services.bookingRequestEmail(token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = {
                            onError(it, event)
                        },
                        onNext = {
                            onResponse(it, event)
                        }
                )
    }

    override fun getRuleAndRegulations(token: String, event: IBaseContract.IBaseInteractorOutput<RulesAndRegulationsResponse>) {
        services.getRulesAndRegulations(token)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = {
                            onError(it, event)
                        },
                        onNext = {
                            onResponse(it, event)
                        }
                )
    }
}