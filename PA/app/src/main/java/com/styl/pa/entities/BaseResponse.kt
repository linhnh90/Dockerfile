package com.styl.pa.entities

import android.content.Context
import androidx.annotation.StringRes
import com.styl.pa.R
import com.styl.pa.modules.dialog.MessageDialogFragment
import com.styl.pa.utils.LogManager
import okhttp3.Headers

/**
 * Created by trangpham on 7/23/2018
 */
class BaseResponse<T> {

    companion object {

        const val UNAUTHORIZED_CODE = 401
    }

    var errorCode: Int = 0
    var errorMessage: String? = null
    var messageResId: Int? = null
    var data: T? = null

    constructor(errorCode: Int, errorMessage: String?, data: T?) {
        this.errorCode = errorCode
        this.errorMessage = errorMessage
        this.data = data
    }

    constructor(errorCode: Int, errorMessage: String?, messageResId: Int?) {
        this.errorCode = errorCode
        this.errorMessage = errorMessage
        this.messageResId = messageResId
    }

    fun formatBaseResponse(@StringRes title: Int, isTitle: Boolean): MessageDialogFragment? {
        var dialogFragment: MessageDialogFragment? = null
        if (this.errorCode < 0) {
            if (this.messageResId != null && this.messageResId!!.compareTo(0) > 0) {
                dialogFragment = MessageDialogFragment.newInstance(0, this.messageResId!!, isTitle)
            } else {
                dialogFragment = MessageDialogFragment.newInstance(title, this.errorMessage
                        ?: "", isTitle)
            }
        } else {
            var errMessage: String? = "Unknown Error. Please retry again"
            if (!errorMessage.isNullOrEmpty()) {
                errMessage = errorMessage
            }

            dialogFragment = MessageDialogFragment.newInstance(
                    this.errorCode,
                    "",
                    errMessage ?: ""
            )
        }
        return dialogFragment
    }

    fun getMessage(context: Context?): String? {
        if (this.errorCode < 0) {
            if (this.messageResId != null && this.messageResId!!.compareTo(0) > 0) {
                return context?.getString(messageResId!!)
            } else {
                return context?.getString(R.string.unknown_error)
            }
        } else {
            if (!errorMessage.isNullOrEmpty()) {
                return errorMessage
            } else {
                return "Unknown Error. Please retry again"
            }
        }
    }

    constructor(headers: Headers) {
        try {
            this.errorCode = Integer.parseInt(headers.get("errorCode"))
            this.errorMessage = headers.get("errorMessage")
        } catch (e: NumberFormatException) {
            LogManager.i("Error when formatting number from string")
        }
    }
}