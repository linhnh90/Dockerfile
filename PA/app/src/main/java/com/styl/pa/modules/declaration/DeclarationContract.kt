package com.styl.pa.modules.declaration

import com.styl.pa.modules.base.IBaseContract

interface DeclarationContract {
    interface View: IBaseContract.IBaseView{

    }

    interface Presenter: IBaseContract.IBasePresenter{
        fun navigateToCheckout(isAllowCheckout: Boolean)
    }

    interface Router: IBaseContract.IBaseRouter{
        fun navigateToCheckout(requestCode: Int, isAllowCheckout: Boolean)
    }
}