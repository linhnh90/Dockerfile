package com.styl.pa.modules.declaration.router

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.DialogFragment
import com.styl.pa.modules.declaration.DeclarationContract
import com.styl.pa.modules.declaration.presenter.DeclarationPresenter

class DeclarationRouter(var fragment: DialogFragment?): DeclarationContract.Router {
    override fun navigateToCheckout(requestCode: Int, isAllowCheckout: Boolean) {
        val intent = Intent()
        intent.putExtra(DeclarationPresenter.ARG_ALLOW_CHECKOUT, isAllowCheckout)
        fragment?.targetFragment?.onActivityResult(requestCode, Activity.RESULT_OK, intent)
        fragment?.dismiss()
    }
}