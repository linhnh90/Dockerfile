package com.styl.pa.modules.checkout.interactor

import com.styl.pa.entities.api.API
import com.styl.pa.entities.cart.CartData
import com.styl.pa.entities.generateToken.BookingResponse
import com.styl.pa.entities.generateToken.Data
import com.styl.pa.entities.participant.ParticipantResponse
import com.styl.pa.entities.pacesRequest.*
import com.styl.pa.entities.pacesRequest.addEventParticipant.AddEventParticipantRequest
import com.styl.pa.entities.payment.PaymentRequest
import com.styl.pa.entities.promocode.AvailablePromoCodeRequest
import com.styl.pa.entities.promocode.ListPromoCodeResponse
import com.styl.pa.entities.promocode.PromoCodeRequest
import com.styl.pa.entities.proxy.EmptyRequest
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.entities.proxy.ProxyRequestHeader
import com.styl.pa.modules.base.BaseInteractor
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.checkout.ICheckoutContact
import com.styl.pa.services.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.jetbrains.annotations.TestOnly


/**
 * Created by Ngatran on 09/14/2018.
 */
class CheckoutInteractor : ICheckoutContact.IInteractor, BaseInteractor() {

    @TestOnly
    fun setKioskService(services: IKioskServices) {
        this.services = services
    }

    override fun loadCart(token: String,
                          userId: String, cartId: String,
                          output: IBaseContract.IBaseInteractorOutput<Data<CartData>>) {
        val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
        val proxyRequestLoadCart = ProxyRequest(
                header,
                EmptyRequest(),
                ProxyRequest.GET_METHOD,
                "",
                API.loadCart(cartId)
        )
        val disposable = services.loadCart(proxyRequestLoadCart, token, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            handleLoadCartResponse(it, output)
                        },
                        {
                            onError(it, output)
                        }
                )
        compositeDisposable.add(disposable)
    }

    var output2: ICheckoutContact.IInteractorOutput2? = null
    var output3: ICheckoutContact.IInteractorOutput3? = null

    override fun removeItemFromCart(token: String,
                                    userId: String,
                                    request: ProxyRequest<ProductRequest>,
                                    output: IBaseContract.IBaseInteractorOutput<BookingResponse>) {
        val disposable = services.removeItemFromCart(request, token, userId)
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

    override fun removeEventItemFromCart(
        token: String,
        userId: String,
        request: ProxyRequest<ProductRequest>,
        output: IBaseContract.IBaseInteractorOutput<BookingResponse>
    ) {
        val disposable = services.removeEventItemFromCart(request, token, userId)
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

    override fun removeItemIgFromCart(
        token: String,
        userId: String,
        request: ProxyRequest<ProductRequest>,
        output: IBaseContract.IBaseInteractorOutput<BookingResponse>
    ) {
        val disposable = services.removeItemIgFromCart(request, token, userId)
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

    override fun prepareCheckout(token: String,
                                 userId: String,
                                 request: ProxyRequest<CheckoutRequest>,
                                 output: IBaseContract.IBaseInteractorOutput<String>) {
        val disposable = services.prepareCheckout(request, token, userId)
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

    override fun submitPayment(token: String?, request: PaymentRequest?) {
        val disposable = services.submitPayment(token, request)
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

    override fun updatePayment(token: String?, request: PaymentRequest?) {
        val disposable = services.updatePayment(token, request)
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

    private val compositeDisposable = CompositeDisposable()
    var services = ServiceGenerator.createService(IKioskServices::class.java)

    override fun onDestroy() {
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }

    override fun addCourseToCart(token: String,
                                 userId: String,
                                 request: ProxyRequest<ProductRequest>,
                                 output: IBaseContract.IBaseInteractorOutput<BookingResponse>) {
        val disposable = services.addCourseToCart(request, token, userId)
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

    override fun addIgCart(
        token: String,
        userId: String,
        request: ProxyRequest<ProductRequest>,
        output: IBaseContract.IBaseInteractorOutput<BookingResponse>
    ) {
        val disposable = services.addIgToCart(request, token, userId)
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

    override fun addEventToCart(
        token: String,
        userId: String,
        request: ProxyRequest<ProductRequest>,
        output: IBaseContract.IBaseInteractorOutput<BookingResponse>
    ) {
        val disposable = services.addEventToCart(request, token, userId)
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

    override fun quickBookCourse(token: String, userId: String,
                                 request: ProxyRequest<ProductRequest>,
                                 output: IBaseContract.IBaseInteractorOutput<BookingResponse>) {
        val disposable = services.quickBookCourse(request, token, userId)
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

    override fun quickBookIg(
        token: String,
        userId: String,
        request: ProxyRequest<ProductRequest>,
        output: IBaseContract.IBaseInteractorOutput<BookingResponse>
    ) {
        val disposable = services.quickBookIg(request, token, userId)
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

    override fun quickBookFacility(token: String,
                                   userId: String,
                                   request: ProxyRequest<FacilityRequest>,
                                   output: IBaseContract.IBaseInteractorOutput<BookingResponse>) {
        val disposable = services.quickBookFacility(token = token, userId = userId, request= request)
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

    override fun quickBookEvent(
        token: String,
        userId: String,
        request: ProxyRequest<ProductRequest>,
        output: IBaseContract.IBaseInteractorOutput<BookingResponse>
    ) {
        val disposable = services.quickBookEvent(token = token, userId = userId, request= request)
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

    override fun addEventParticipant(
        token: String,
        userId: String,
        request: ProxyRequest<AddEventParticipantRequest>,
        output: IBaseContract.IBaseInteractorOutput<ParticipantResponse>
    ) {
        val disposable = services.addEventParticipant(request, token, userId)
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

    override fun applyPromoCode(
        token: String,
        userId: String,
        request: ProxyRequest<PromoCodeRequest>,
        output: IBaseContract.IBaseInteractorOutput<Data<CartData>>
    ) {
        val disposable = services.applyPromoCode(token, request, userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    handleLoadCartResponse(it, output)
                },
                {
                    onError(it, output)
                }
            )
        compositeDisposable.add(disposable)
    }

    override fun removePromoCode(
        token: String,
        userId: String,
        request: ProxyRequest<EmptyRequest>,
        output: IBaseContract.IBaseInteractorOutput<Data<CartData>>
    ) {
        val disposable = services.removePromoCode(token, request, userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    handleLoadCartResponse(it, output)
                },
                {
                    onError(it, output)
                }
            )
        compositeDisposable.add(disposable)
    }

    override fun getAllAvailablePromoCode(
        token: String,
        request: AvailablePromoCodeRequest,
        output: IBaseContract.IBaseInteractorOutput<ListPromoCodeResponse>
    ) {
        val disposable = services.getAllAvailablePromoCodes(token, request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    onResponse(it, output)
                },
                {
                    onError(it, output)
                }
            )
        compositeDisposable.add(disposable)
    }

}