package com.styl.pa.modules.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.styl.pa.R
import com.styl.pa.enums.TrackingName
import com.styl.pa.modules.base.BaseDialogFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.utils.MySharedPref
import kotlinx.android.synthetic.main.fragment_admin_check.view.*


/**
 * Created by trangpham on 9/20/2018
 */
class AdminCheckFragment : BaseDialogFragment(), View.OnClickListener {

    override fun setCurrentName() {
        super.setCurrentName()

        if (activity != null) {
            (activity as MainActivity).setCurrentViewName(TrackingName.AdminCheckFragment.value)
        }
    }

    private var etPassword: EditText? = null
    private var getView: View? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        getView = activity?.layoutInflater?.inflate(R.layout.fragment_admin_check, null, false)

        onSetEventDismissDialog(this)

        etPassword = getView?.findViewById(R.id.et_password) as? EditText

        getView?.btn_ok?.setOnClickListener(this)
        getView?.btn_cancel?.setOnClickListener(this)
        getView?.ll_container?.setOnClickListener(this)

        return AlertDialog.Builder(activity)
                .setView(getView)
                .create()
    }

    override fun onResume() {
        super.onResume()
        isCancelable = false

        etPassword?.post(Runnable {
            etPassword?.requestFocus()
            val imm = etPassword?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(etPassword, InputMethodManager.SHOW_FORCED)
        })
    }

    override fun onClick(v: View?) {
        touchListener()
        // hide keyboard
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v?.getWindowToken(), 0)

        when (v?.id) {
            R.id.btn_cancel -> {
                dismiss()
            }
            R.id.btn_ok -> {
                val text = etPassword?.text.toString()
                val code = MySharedPref(activity).activationCode
                if (text.equals(code)) {
                    dismiss()
                    (activity as? MainActivity)?.navigateSettingsView()
                }
                etPassword?.text?.clear()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideSoftKeyboard()
    }

    private fun hideSoftKeyboard() {
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(etPassword?.windowToken, 0)
    }
}