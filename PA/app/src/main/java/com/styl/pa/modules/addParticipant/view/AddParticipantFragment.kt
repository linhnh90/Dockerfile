package com.styl.pa.modules.addParticipant.view

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.styl.pa.R
import com.styl.pa.modules.addParticipant.AddParticipantContract
import com.styl.pa.modules.addParticipant.presenter.AddParticipantPresenter
import com.styl.pa.modules.base.CustomBaseDialogFragment
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.fragment_add_participant.view.*


class AddParticipantFragment : CustomBaseDialogFragment(), AddParticipantContract.View,
    View.OnClickListener {
    private var getView: View? = null
    private var presenter: AddParticipantContract.Presenter? = AddParticipantPresenter(this)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        getView = activity?.layoutInflater?.inflate(R.layout.fragment_add_participant, null)
        onSetEventDismissDialog(this)

        this.init()

        val dialog = AlertDialog.Builder(activity)
            .setView(getView)
            .create()
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    private fun init() {
        getView?.btn_add?.setOnClickListener(this)
        getView?.btn_cancel?.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        GeneralUtils.hideSoftKeyboard(p0)

        when (p0?.id) {
            R.id.btn_add -> {
                val fullName = getView?.edt_full_name?.text?.toString() ?: ""
                val phone = getView?.edt_mobile_number?.text?.toString() ?: ""
                val email = getView?.edt_email?.text?.toString() ?: ""
                this.presenter?.validateInput(
                    fullName = fullName,
                    phone = phone,
                    email = email
                )
            }
            R.id.btn_cancel -> {
                dismiss()
            }
        }
    }

    override fun showErrorInput(errorFullName: String, errorPhone: String, errorEmail: String) {
        if (errorFullName.isNotEmpty()){
            getView?.tl_full_name?.error = errorFullName
        } else {
            getView?.tl_full_name?.error = null
        }

        if (errorPhone.isNotEmpty()){
            getView?.tl_phone_number?.error = errorPhone
        } else {
            getView?.tl_phone_number?.error = null
        }

        if (errorEmail.isNotEmpty()){
            getView?.tl_email?.error = errorEmail
        } else {
            getView?.tl_email?.error = null
        }
    }

}