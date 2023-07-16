package com.styl.pa.modules.kioskactivation.presenter

import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.kioskactivation.KioskActivationRequest
import com.styl.pa.entities.kioskactivation.KioskInfo
import com.styl.pa.modules.kioskactivation.IKioskActivationContract
import com.styl.pa.modules.kioskactivation.interactor.KioskActivationInteractor


/**
 * Created by trangpham on 9/26/2018
 */
class KioskActivationPresenter(var view: IKioskActivationContract.IView?) : IKioskActivationContract.IPresenter, IKioskActivationContract.IInteractorOutput {

    private var interactor: KioskActivationInteractor? = KioskActivationInteractor(this)

    private var code: String? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    constructor(view: IKioskActivationContract.IView?, interactor: KioskActivationInteractor?): this(view) {
        this.interactor = interactor
    }

    override fun activateKiosk(id: String?, code: String?) {
        view?.showLoading()
        val request = KioskActivationRequest(id, code)

        this.code = code

        interactor?.activateKiosk(request)
    }

    override fun onSuccess(data: KioskInfo?) {
        view?.dismissLoading()

        val gson = Gson()
        val info = gson.toJson(data, KioskInfo::class.java)

        if (!this.code.isNullOrEmpty()) {
            view?.storeCode(this.code!!)
        }

        view?.onActivationSuccess(info)
        this.code = null
    }

    override fun onError(data: BaseResponse<KioskInfo>) {
        view?.dismissLoading()
        view?.showErrMessage(data)
        this.code = null
    }

    override fun onDestroy() {
        view = null
        interactor?.onDestroy()
        interactor = null
    }
}