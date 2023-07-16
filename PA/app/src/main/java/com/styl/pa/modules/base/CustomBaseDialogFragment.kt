package com.styl.pa.modules.base

import com.styl.pa.R
import com.styl.pa.entities.BaseResponse
import com.styl.pa.modules.dialog.LoadingFragment
import com.styl.pa.modules.dialog.MessageDialogFragment

/**
 * Created by Ngatran on 10/11/2018.
 */
open class CustomBaseDialogFragment() : BaseDialogFragment(), IBaseContract.IBaseView {
    private var loadingDialog: LoadingFragment? = null
    private var messageDialog: MessageDialogFragment? = null

    override fun showLoading() {
        if (loadingDialog == null || loadingDialog?.dialog?.isShowing == false || loadingDialog?.isVisible == false) {
            loadingDialog = LoadingFragment()
            if (fragmentManager != null) {
                loadingDialog?.show(fragmentManager!!, LoadingFragment::class.java.simpleName)
            }
        }

    }

    override fun dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog?.dismiss()
        }
    }

    override fun <T> showErrorMessage(response: BaseResponse<T>) {
        if (messageDialog == null || messageDialog != null && messageDialog?.showsDialog == false) {
            messageDialog = response.formatBaseResponse(0, false)
            if (fragmentManager != null) {
                messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
            }
        }
    }

    override fun showErrorMessage(messageResId: Int) {
        if (messageDialog == null || messageDialog?.dialog?.isShowing == false || loadingDialog?.isVisible == false) {
            messageDialog = MessageDialogFragment.newInstance(R.string.error, messageResId)
            if (fragmentManager != null) {
                messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
            }
        }
    }

    open fun dismissMessageDialog() {
        if (messageDialog != null) {
            messageDialog?.dismiss()
            messageDialog = null
        }
    }

    override fun onPause() {
        super.onPause()
        dismissLoading()
    }
}