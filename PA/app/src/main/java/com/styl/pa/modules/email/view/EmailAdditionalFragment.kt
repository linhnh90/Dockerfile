package com.styl.pa.modules.email.view

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.styl.pa.R
import com.styl.pa.modules.base.BaseDialogFragment
import com.styl.pa.modules.checkout.view.CheckoutFragment
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.fragment_email_additional.view.*


class EmailAdditionalFragment:  BaseDialogFragment(), View.OnClickListener {

    companion object {
        fun newInstance(): EmailAdditionalFragment {
            return EmailAdditionalFragment()
        }
    }

    private var dialogView: View? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = activity!!.layoutInflater.inflate(R.layout.fragment_email_additional, null)

        onSetEventDismissDialog(this)

        isCancelable = false

        val dialog = AlertDialog.Builder(activity)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView?.btn_skip?.setOnClickListener(this)
        dialogView?.btn_apply_email?.setOnClickListener(this)

        return dialog
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onClick(v: View?) {
        touchListener()
        when (v?.id) {
            R.id.btn_skip -> {
                GeneralUtils.hideSoftKeyboard(dialogView)
                targetFragment?.onActivityResult(CheckoutFragment.EMAIL_REQUEST_CODE, Activity.RESULT_OK, Intent())
                dismiss()
            }
            R.id.btn_apply_email -> {
                val email = dialogView?.edt_email?.text?.toString()
                if (GeneralUtils.isValidEmailAddress(email)) {
                    GeneralUtils.hideSoftKeyboard(dialogView)
                    val intent = Intent()
                    intent.putExtra(CheckoutFragment.EXTRA_EMAIL_ADDRESS, email)
                    targetFragment?.onActivityResult(CheckoutFragment.EMAIL_REQUEST_CODE, Activity.RESULT_OK, intent)
                    dismiss()
                } else {
                    dialogView?.txt_email_error?.visibility = View.VISIBLE
                }
            }
            else -> return
        }
    }
}