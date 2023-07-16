package com.styl.pa.modules.dialog.scanNric

import android.content.Context
import com.styl.pa.entities.api.API
import com.styl.pa.entities.generateToken.Data
import com.styl.pa.entities.generateToken.PageByClassInfo
import com.styl.pa.entities.proxy.EmptyRequest
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.entities.proxy.ProxyRequestHeader
import com.styl.pa.entities.proxy.ProxyResponse
import com.styl.pa.modules.base.BaseInteractor
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.services.IKioskServices
import com.styl.pa.services.ServiceGenerator
import com.styl.pa.utils.MySharedPref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class ScanNricInteractor : IScanNricContact.IInteractor, BaseInteractor {

    private val compositeDisposable = CompositeDisposable()
    var context: Context? = null
    var servicesKiosk: IKioskServices? = null

    constructor(context: Context) {
        this.context = context
        servicesKiosk = ServiceGenerator.createService(IKioskServices::class.java)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
    }

    override fun getAllClassInfo(data: String, pageIndex: Int?, pageSize: Int?,
                                 allClassInfoCallback: IBaseContract.IBaseInteractorOutput<PageByClassInfo>) {
        var token = MySharedPref(context).eKioskHeader

        val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
        val hostName = ""
        val uri: String = API.getClassCode(data)
        val hasVersion = true
        val request = ProxyRequest(header, EmptyRequest(), ProxyRequest.GET_METHOD, hostName, uri, hasVersion)
        servicesKiosk?.getClassByCode(token, request)?.subscribeOn(Schedulers.newThread())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(
                { result: Response<ProxyResponse<Data<PageByClassInfo>>> ->
                    handleResponseProxy(result, allClassInfoCallback)
                },
                { e: Throwable ->
                    onError(e, allClassInfoCallback)
                },
                {

                }
        )
    }
}