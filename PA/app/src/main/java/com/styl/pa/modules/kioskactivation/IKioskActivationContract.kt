package com.styl.pa.modules.kioskactivation

import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.kioskactivation.KioskActivationRequest
import com.styl.pa.entities.kioskactivation.KioskInfo
import com.styl.pa.modules.base.IBaseContract

/**
 * Created by trangpham on 9/25/2018
 */
interface IKioskActivationContract {

    interface IView : IBaseContract.IBaseView {

        fun onActivationSuccess(kioskInfo: String?)

        fun storeCode(code: String)

        fun <T> showErrMessage(baseResponse: BaseResponse<T>)
    }

    interface IPresenter : IBaseContract.IBasePresenter {

        fun activateKiosk(id: String?, code: String?)
    }

    interface IInteractor : IBaseContract.IBaseInteractor {

        fun activateKiosk(request: KioskActivationRequest?)
    }

    interface IInteractorOutput : IBaseContract.IBaseInteractorOutput<KioskInfo> {

    }
}