package com.styl.pa.modules.base

import androidx.annotation.StringRes
import com.styl.pa.entities.BaseResponse

/**
 * Created by trangpham on 8/29/2018
 */
interface IBaseContract {

    interface IBaseView {

        fun showLoading()
        fun dismissLoading()
        fun <T> showErrorMessage(response: BaseResponse<T>)
        fun showErrorMessage(@StringRes messageResId: Int)
    }

    interface IBasePresenter {

        fun onDestroy()
    }

    interface IBaseInteractor {

        fun onDestroy()
    }

    interface IBaseInteractorOutput<T> {

        fun onSuccess(data: T?)
        fun onError(data: BaseResponse<T>)
    }

    interface IBaseRouter {

    }
}