package com.styl.pa.modules.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.entities.BaseResponse
import com.styl.pa.enums.TrackingName
import com.styl.pa.interfaces.TimeOutDialogEvent
import com.styl.pa.modules.base.CustomBaseDialogFragment
import com.styl.pa.modules.cart.view.CartFragment
import com.styl.pa.modules.customerverification.view.CustomerVerificationFragment
import com.styl.pa.modules.dialog.scanNric.IScanNricContact
import com.styl.pa.modules.dialog.scanNric.ScanNricPresenter
import com.styl.pa.modules.main.IMainContract
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.utils.LogManager
import kotlinx.android.synthetic.main.fragment_waiting_result.view.*

class WaitingResultFragment : CustomBaseDialogFragment(), IScanNricContact.IView, View.OnClickListener, TimeOutDialogEvent {
    override fun setCurrentName() {
        super.setCurrentName()

        if (activity != null) {
            (activity as MainActivity).setCurrentViewName(TrackingName.WaitingResultFragment.value)
        }
    }

    override fun onClick(v: View?) {
        touchListener()
    }

    companion object {
        private val TAG = WaitingResultFragment::class.java.simpleName
        const val EMAIL_EVENT = BuildConfig.APPLICATION_ID + "actions.EMAIL_EVENT"
        const val ACTION_NRIC_SCAN = BuildConfig.APPLICATION_ID + "actions.ACTION_NRIC_SCAN"
        const val ACTION_QR_SCAN = BuildConfig.APPLICATION_ID + "actions.ACTION_QR_SCAN"
        const val ACTION_PAYMENT = BuildConfig.APPLICATION_ID + "actions.ACTION_PAYMENT"
        const val ACTION_INSERT_NETS = BuildConfig.APPLICATION_ID + "actions.ACTION_INSERT_NETS"
    }

    private var actionName: String? = null
    private var getView: View? = null
    private var presenter: ScanNricPresenter? = null
    private var listener: IMainContract.IView? = null

    fun setScanQRListener(listener: IMainContract.IView?) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        getView = activity!!.layoutInflater.inflate(R.layout.fragment_waiting_result, null)

        onSetEventDismissDialog(this)

        presenter = ScanNricPresenter(this, activity!!)

        val dialog = AlertDialog.Builder(activity)
                .setView(getView)
                .create()

        init()

        // clear background
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    override fun onResume() {
        super.onResume()

        val window = dialog?.window
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        when (actionName) {
            ACTION_QR_SCAN -> {
                presenter?.startScan(activity!!)

                startCountDownTimer(TIME_COUNT_DOWN, true)
            }

        }
    }

    override fun showLoading() {
        LogManager.d(TAG, "showLoading")
    }

    override fun dismissLoading() {
        LogManager.d(TAG, "dismissLoading")
    }

    override fun <T> showErrorMessage(response: BaseResponse<T>) {
        dismissScanDialog()
        messageDialog?.dismiss()
        messageDialog = response.formatBaseResponse(0, false)
        if (fragmentManager != null) {
            messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    private var messageDialog: MessageDialogFragment? = null
    override fun showErrorMessage(messageResId: Int) {
        dismissScanDialog()
        messageDialog?.dismiss()
        if (messageDialog == null || messageDialog != null && messageDialog?.showsDialog == false) {
            messageDialog = MessageDialogFragment.newInstance(R.string.error, messageResId)
            if (fragmentManager != null) {
                messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
            }
        }
    }

    override fun showErrorMessageAndTitle(message: Int, title: Int) {
        dismissScanDialog()
        messageDialog?.dismiss()
        messageDialog = MessageDialogFragment.newInstance(title, message, true)
        if (fragmentManager != null) {
            messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    fun setActionName(actionName: String) {
        this.actionName = actionName
    }

    private fun init() {
        var source = R.drawable.animation_machine
        var text = ""
        when (actionName) {
            ACTION_NRIC_SCAN -> {
                source = R.drawable.animation_scan_nric
                text = getString(R.string.scan_nric_card)
            }

            ACTION_QR_SCAN -> {
                source = R.drawable.animation_scanner
                text = getString(R.string.scan_qr_code)
            }

            ACTION_PAYMENT -> {
                source = R.drawable.animation_machine
                text = getString(R.string.tab_card)
            }

            ACTION_INSERT_NETS -> {
                source = R.drawable.animation_insert_nets
                text = getString(R.string.insert_card)
            }

            EMAIL_EVENT -> {
                source = R.drawable.animation_email
                text = ""
            }
        }

        playVideo(source)
        getView?.txt_event?.text = text

        getView?.rl_container?.setOnClickListener(this)
    }

    fun setContent(content: String?) {
        if(!content.isNullOrEmpty()){
            getView?.txt_event?.text = content
        }
    }

    private fun playVideo(video: Int) {
        activity?.let {
            getView?.img_event?.let { imageView ->
                Glide.with(it)
                        .load(video)
                        .into(imageView)
            }
        }
    }

    override fun onScanNricSuccess(data: String) {
        if (actionName.equals(ACTION_QR_SCAN)) {
            dismissScanDialog()
            listener?.onScanQrSuccess(data)
        }
        stopCountDownTimer()
    }

    override fun dismissScanDialog() {
        this@WaitingResultFragment.dismiss()
    }

    override fun onDestroy() {
        when (actionName) {
            ACTION_QR_SCAN -> {
                presenter?.stopScan(activity!!)
                presenter?.onDestroy()

                val f = fragmentManager?.findFragmentById(R.id.container)
                if (f != null) {
                    if (f is CartFragment) {
                        f.setConfigEvent()
                    } else if (f is CustomerVerificationFragment) {
                        f.setupScannerConfigEvent(false)
                    }
                }
            }

            ACTION_NRIC_SCAN -> {
                if (fragmentManager?.findFragmentById(R.id.container) != null && fragmentManager?.findFragmentById(R.id.container) is CartFragment) {
                    (fragmentManager?.findFragmentById(R.id.container) as CartFragment).pullTrigger(false)
                    (fragmentManager?.findFragmentById(R.id.container) as CartFragment).stopCountDownTimer()
                }
            }
        }
        super.onDestroy()
    }

    private var countDownTimer: CountDownTimer? = null
    private val TIME_COUNT_DOWN = 9

    private fun initCountDownTimer(time: Int) {
        countDownTimer = object : CountDownTimer((time * 1000).toLong(), 1000) {

            override fun onTick(l: Long) {
                // Do nothing - no need to handle this function
            }

            override fun onFinish() {
                dismissScanDialog()
            }
        }
    }

    private fun startCountDownTimer(time: Int, isReset: Boolean) {
        if (countDownTimer != null) {
            if (isReset) {
                countDownTimer?.onTick((time * 1000).toLong())
            }
        } else {
            initCountDownTimer(time)
        }

        countDownTimer?.start()
    }

    private fun stopCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
    }

}
