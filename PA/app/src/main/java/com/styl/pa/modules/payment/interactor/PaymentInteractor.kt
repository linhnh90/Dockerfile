package com.styl.pa.modules.payment.interactor

import androidx.annotation.VisibleForTesting
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.entities.pacesRequest.CheckoutRequest
import com.styl.pa.entities.pacesRequest.PaymentRefRequest
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.modules.base.BaseInteractor
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.payment.IPaymentContract
import com.styl.pa.services.IKioskServices
import com.styl.pa.services.ServiceGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by trangpham on 10/31/2018
 */
class PaymentInteractor(var output: IPaymentContract.IInteractorOutput?) :
        IPaymentContract.IInteractor, BaseInteractor() {

    private val compositeDisposable = CompositeDisposable()

    private var services = ServiceGenerator.createService(IKioskServices::class.java)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setServices(services: IKioskServices) {
        this.services = services
    }

    @ExcludeFromJacocoGeneratedReport
    override fun subscribeTerminal(terminalSubject: BehaviorSubject<Boolean>) {
        terminalSubject.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = @ExcludeFromJacocoGeneratedReport {
                            output?.onTerminalChanged(false)
                        },
                        onNext = @ExcludeFromJacocoGeneratedReport {
                            output?.onTerminalChanged(it)
                        }
                )
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        output = null
    }

    override fun createPaymentReference(token: String?,
                                        request: ProxyRequest<PaymentRefRequest>,
                                        output: IBaseContract.IBaseInteractorOutput<String>) {
        val disposable = services.createPaymentReference(request, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            handlePaymentResponseProxy(it, output)
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
}