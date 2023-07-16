package com.styl.pa.modules.signature.view

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.styl.pa.R
import com.styl.pa.entities.BaseResponse
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.dialog.LoadingFragment
import com.styl.pa.modules.dialog.MessageDialogFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.signature.ISignatureContract
import com.styl.pa.modules.signature.SketchSheetView
import kotlinx.android.synthetic.main.fragment_signature.view.*


class SignatureFragment : BaseFragment(), ISignatureContract.IView {
    private val loadingFragment = LoadingFragment()
    private var dialogMessage: MessageDialogFragment? = null
    private var getView: View? = null
    private var drawView: SketchSheetView? = null

    companion object {
        val BITMAP_SIGNATURE = "bitmapSignature"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getView = activity?.layoutInflater?.inflate(R.layout.fragment_signature, null)

        init()
        setTitle("")

        return getView
    }

    fun init() {
        if (activity != null && activity is MainActivity) {
            (activity as MainActivity).enableOutsideView(false)
        }

        drawView = SketchSheetView(activity)

        getView?.ll_draw_signature?.addView(drawView, LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT))

        getView?.btn_clear?.setOnClickListener {
            clearDraw()
        }

        getView?.btn_ok?.setOnClickListener {
            handleButtonOk()
        }

        getView?.isFocusableInTouchMode = true
        getView?.requestFocus()
        getView?.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                return@OnKeyListener true
            }
            false
        })
    }

    private fun handleButtonOk() {
        // new terminal not required signature
    }

    override fun onDestroy() {
        super.onDestroy()

        if (activity != null && activity is MainActivity) {
            (activity as MainActivity).enableOutsideView(true)
        }
    }

    private fun clearDraw() {
        drawView?.clear()
    }

    override fun showLoading() {
        if (!loadingFragment.isVisible && fragmentManager != null) {
            loadingFragment.show(fragmentManager!!, LoadingFragment::class.java.simpleName)
        }
    }

    override fun dismissLoading() {
        if (loadingFragment.isAdded) {
            loadingFragment.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        setTitle("")
    }

    override fun <T> showErrorMessage(response: BaseResponse<T>) {
        if (dialogMessage != null && dialogMessage!!.isAdded) {
            dialogMessage?.dismiss()
        }
        if (response.errorCode < 0) {
            if (response.messageResId != null && response.messageResId!!.compareTo(0) > 0) {
                dialogMessage = MessageDialogFragment.newInstance(0, response.messageResId!!)
            } else {
                dialogMessage = MessageDialogFragment.newInstance("", response.errorMessage!!)
            }
        } else {
            dialogMessage = MessageDialogFragment.newInstance(
                    response.errorCode,
                    "",
                    response.errorMessage!!
            )
        }
        if (fragmentManager != null) {
            dialogMessage?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun showErrorMessage(messageResId: Int) {
        if (dialogMessage != null && dialogMessage!!.isAdded) {
            dialogMessage?.dismiss()
        }
        dialogMessage = MessageDialogFragment.newInstance(0, messageResId)
        if (fragmentManager != null) {
            dialogMessage?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }
}