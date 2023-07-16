package com.styl.pa.modules.kioskactivation.view

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.styl.pa.R
import com.styl.pa.entities.BaseResponse
import com.styl.pa.modules.base.BaseDialogFragment
import com.styl.pa.modules.dialog.LoadingFragment
import com.styl.pa.modules.dialog.MessageDialogFragment
import com.styl.pa.modules.kioskactivation.IKioskActivationContract
import com.styl.pa.modules.kioskactivation.presenter.KioskActivationPresenter
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.LogManager
import com.styl.pa.utils.MySharedPref
import kotlinx.android.synthetic.main.fragment_kiosk_activation.view.*
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.*

/**
 * Created by trangpham on 9/25/2018
 */
class KioskActivationFragment : BaseDialogFragment(), IKioskActivationContract.IView,
        View.OnClickListener {

    companion object {
        private val TAG = KioskActivationFragment::class.java.simpleName
    }

    private var presenter: KioskActivationPresenter? = KioskActivationPresenter(this)

    private var etCode: EditText? = null

    private val loadingFragment = LoadingFragment()
    private var dialog: MessageDialogFragment? = null
    private var v: View? = null
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            // Do nothing
            return
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            if ((s?.trim()?.length ?: 0) == 0) {
                enableErrMessage(null)
            }
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Do nothing
            return
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        v = activity?.layoutInflater?.inflate(R.layout.fragment_kiosk_activation, null)

        etCode = v?.findViewById(R.id.etCode)
        etCode?.addTextChangedListener(textWatcher)

        isCancelable = false

        v?.findViewById<Button>(R.id.btnSubmit)?.setOnClickListener(this)

        val dialog = AlertDialog.Builder(activity)
                .setView(v)
                .create()

        // clear background
        if (dialog.window != null) {
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        return dialog
    }

    override fun onClick(v: View?) {
        // hide keyboard
        GeneralUtils.hideSoftKeyboard(v)

        when (v?.id) {
            R.id.btnSubmit -> {
                var id = getEthernetMacAddress()
                val code = etCode?.text.toString()

                enableSubmitButton(false)
                presenter?.activateKiosk(id, code)
            }
        }
    }

    private fun enableSubmitButton(isEnable: Boolean) {
        v?.btnSubmit?.isEnabled = isEnable
        if (!isEnable) {
            etCode?.text?.clear()
        }
    }

    private fun enableErrMessage(errMessage: String?) {
        if (errMessage != null) {
            v?.txt_error_message?.visibility = View.VISIBLE
            v?.txt_error_message?.text = errMessage
        } else {
            v?.txt_error_message?.text = ""
            v?.txt_error_message?.visibility = View.GONE
        }
    }

    override fun <T> showErrMessage(baseResponse: BaseResponse<T>) {
        (activity as? MainActivity)?.stopTimer()
        val errMessage = baseResponse.getMessage(activity)
        enableErrMessage(errMessage ?: getString(R.string.unknown_error))
    }

    override fun showLoading() {
        if (!loadingFragment.isAdded && fragmentManager != null) {
            loadingFragment.show(fragmentManager!!, LoadingFragment::class.java.simpleName)
        }
    }

    override fun dismissLoading() {
        enableSubmitButton(true)
        if (loadingFragment.isAdded) {
            loadingFragment.dismiss()
        }
    }

    override fun <T> showErrorMessage(response: BaseResponse<T>) {
        if (dialog != null && dialog!!.isAdded) {
            dialog?.dismiss()
        }
        if (response.errorCode < 0) {
            if (response.messageResId != null && response.messageResId!!.compareTo(0) > 0) {
                dialog = MessageDialogFragment.newInstance(0, response.messageResId!!)
            } else {
                dialog = MessageDialogFragment.newInstance("", response.errorMessage!!)
            }
        } else {
            dialog = MessageDialogFragment.newInstance(
                    response.errorCode,
                    "",
                    response.errorMessage!!
            )
        }
        if (fragmentManager != null) {
            dialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun showErrorMessage(messageResId: Int) {
        LogManager.d(TAG, "showErrorMessage")
    }

    override fun onActivationSuccess(kioskInfo: String?) {
        dismiss()
        if (!kioskInfo.isNullOrEmpty()) {
            MySharedPref(activity).kioskInfo = kioskInfo

            (activity as? MainActivity)?.kioskInfoSubject?.onNext(kioskInfo!!)
        }
    }

    override fun storeCode(code: String) {
        MySharedPref(activity).activationCode = code
    }

    /**
     * Load file content to String
     */
    @Throws(java.io.IOException::class)
    private fun loadFileAsString(filePath: String): String {
        val fileData = StringBuffer(1000)
        val reader = BufferedReader(FileReader(filePath))
        val buf = CharArray(1024)
        var numRead = reader.read(buf)
        while (numRead != -1) {
            val readData = String(buf, 0, numRead)
            fileData.append(readData)
            numRead = reader.read(buf)
        }
        reader.close()
        return fileData.toString()
    }

    /**
     * Get the STB MacAddress
     */
    private fun getEthernetMacAddress(): String? {
        try {
            return loadFileAsString("/sys/class/net/eth0/address")
                .uppercase(Locale.ENGLISH).substring(0, 17)
        } catch (e: IOException) {
            return null
        }
    }
}