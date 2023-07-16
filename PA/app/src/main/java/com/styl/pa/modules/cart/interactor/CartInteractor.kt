package com.styl.pa.modules.cart.interactor

import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.entities.api.API
import com.styl.pa.entities.checkResidency.CheckResidencyResponse
import com.styl.pa.entities.generateToken.BookingResponse
import com.styl.pa.entities.pacesRequest.*
import com.styl.pa.entities.participant.ParticipantResponse
import com.styl.pa.entities.proxy.EmptyRequest
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.entities.proxy.ProxyRequestHeader
import com.styl.pa.entities.vacancy.VacancyResponse
import com.styl.pa.modules.base.BaseInteractor
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.cart.ICartContact
import com.styl.pa.services.IKioskServices
import com.styl.pa.services.ServiceGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.annotations.TestOnly

/**
 * Created by Ngatran on 09/14/2018.
 */
class CartInteractor() : ICartContact.IInteractor,
        BaseInteractor() {

    @TestOnly
    fun setKioskService(services: IKioskServices) {
        this.services = services
    }

    private val compositeDisposable = CompositeDisposable()
    var services = ServiceGenerator.createService(IKioskServices::class.java)

    override fun removeItemFromCart(token: String,
                                    userId: String,
                                    request: ProxyRequest<ProductRequest>,
                                    output: IBaseContract.IBaseInteractorOutput<BookingResponse>) {
        val  disposable = services.removeItemFromCart(request, token, userId)
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
    override fun removeEventItemFromCart(
        token: String,
        userId: String,
        request: ProxyRequest<ProductRequest>,
        output: IBaseContract.IBaseInteractorOutput<BookingResponse>
    ) {
        val  disposable = services.removeEventItemFromCart(request, token, userId)
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
    override fun removeItemIgFromCart(
        token: String,
        userId: String,
        request: ProxyRequest<ProductRequest>,
        output: IBaseContract.IBaseInteractorOutput<BookingResponse>
    ) {
        val  disposable = services.removeItemIgFromCart(request, token, userId)
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
    override fun addParticipant(token: String,
                                userId: String,
                                request: ProxyRequest<ParticipantRequest<ParticipantItem>>,
                                output: IBaseContract.IBaseInteractorOutput<ParticipantResponse>) {
        val disposable = services.addParticipant(request, token, userId)
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

    override fun deleteParticipant(token: String,
                                   userId: String,
                                   request: ProxyRequest<ParticipantRequest<ParticipantUserId>>,
                                   output: IBaseContract.IBaseInteractorOutput<ParticipantResponse>?) {
        val disposable = services.deleteParticipant(request, token, userId)
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
    override fun deleteParticipantIg(
        token: String,
        userId: String,
        request: ProxyRequest<ParticipantRequest<ParticipantUserId>>,
        output: IBaseContract.IBaseInteractorOutput<ParticipantResponse>?
    ) {
        val disposable = services.deleteParticipantIg(request, token, userId)
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
    override fun checkResidency(
        token: String,
        userId: String,
        request: ProxyRequest<EmptyRequest>,
        output: IBaseContract.IBaseInteractorOutput<CheckResidencyResponse>
    ) {
        val disposable = services.checkResidency(
            token = token,
            userId = userId,
            request = request
        )
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
    override fun checkEventVacancy(
        token: String,
        eventId: String,
        userId: String,
        isMember: Boolean,
        isResident: Boolean,
        callback: IBaseContract.IBaseInteractorOutput<VacancyResponse>
    ) {
        val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
        val request = ProxyRequest(
            header,
            EventAvailabilityRequest(eventId, isMember, isResident),
            ProxyRequest.POST_METHOD,
            "",
            API.uriGetEventAvailability
        )
        val disposable = services.getEventAvailability(request, token, userId)
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

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        compositeDisposable.clear()
    }

}