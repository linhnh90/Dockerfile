package com.styl.pa.modules.declaration.presenter

import androidx.fragment.app.DialogFragment
import com.styl.pa.BuildConfig
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.modules.declaration.DeclarationContract
import com.styl.pa.modules.declaration.router.DeclarationRouter

class DeclarationPresenter(var view: DeclarationContract.View?): DeclarationContract.Presenter {
    private var router: DeclarationRouter? = DeclarationRouter(view as? DialogFragment)

    companion object {
        const val DECLARATION_REQUEST_CODE = 103
        const val ARG_ALLOW_CHECKOUT = BuildConfig.APPLICATION_ID + "args.ARG_ALLOW_CHECKOUT"
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigateToCheckout(isAllowCheckout: Boolean) {
        this.router?.navigateToCheckout(DECLARATION_REQUEST_CODE, isAllowCheckout)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        router = null
        view = null
    }
}