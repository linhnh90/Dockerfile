package com.styl.pa.modules.scanQrCode

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.styl.pa.R
import com.styl.pa.entities.BaseResponse
import com.styl.pa.enums.TrackingName
import com.styl.pa.interfaces.TimeOutDialogEvent
import com.styl.pa.modules.base.CustomBaseFragment
import com.styl.pa.modules.dialog.scanNric.IScanNricContact
import com.styl.pa.modules.dialog.scanNric.ScanNricPresenter
import com.styl.pa.modules.main.IMainContract
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.utils.LogManager
import kotlinx.android.synthetic.main.fragment_scan_qr_code.view.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ScanQrCodeFragment : CustomBaseFragment(), IScanNricContact.IView, View.OnClickListener, TimeOutDialogEvent {
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ScanQrCodeFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }

        private val TAG = ScanQrCodeFragment::class.java.simpleName
    }

    private var getView: View? = null
    private var presenter: ScanNricPresenter? = null
    private var listener: IMainContract.IView? = null

    private var countDownTimer: CountDownTimer? = null
    private val TIME_COUNT_DOWN = 9

    fun setScanQRListener(listener: IMainContract.IView?) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        getView = inflater.inflate(R.layout.fragment_scan_qr_code, container, false)
        init()
        return getView
    }

    private fun init() {
        (activity as? MainActivity)?.setBackgroundLine(R.drawable.bg_gradient_orange)
        (activity as? MainActivity)?.setTitle("")

        getView?.txt_scan?.text = getString(R.string.scan_qr_code)
        playVideo(R.drawable.animation_scanner)

        presenter = ScanNricPresenter(this, activity!!)
        presenter?.startScan(activity!!)

        startCountDownTimer(TIME_COUNT_DOWN, true)
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBackgroundBottomBar(MainActivity.MODE_SCAN_COURSES_EVENTS)
    }

    private fun playVideo(video: Int) {
        activity?.let {
            getView?.img_scan?.let { imageView ->
                Glide.with(it)
                        .load(video)
                        .into(imageView)
            }
        }
    }

    override fun onScanNricSuccess(data: String) {
        listener?.onScanQrSuccess(data)
        stopCountDownTimer()
        fragmentManager?.popBackStack()
    }

    override fun dismissScanDialog() {
        fragmentManager?.popBackStack()
    }

    override fun showErrorMessageAndTitle(message: Int, title: Int) {
        LogManager.d(TAG, "showErrorMessageAndTitle")
    }

    override fun showLoading() {
        super.showLoading()
    }

    override fun dismissLoading() {
        super.dismissLoading()
    }

    override fun <T> showErrorMessage(response: BaseResponse<T>) {
        super.showErrorMessage(response)
    }

    override fun showErrorMessage(messageResId: Int) {
        super.showErrorMessage(messageResId)
    }

    override fun onClick(p0: View?) {
        // Do nothing
        return
    }

    override fun setCurrentName() {
        if (activity != null) {
            (activity as MainActivity).setCurrentViewName(TrackingName.ScanQrCodeFragment.value)
        }
    }

    private fun initCountDownTimer(time: Int) {
        countDownTimer = object : CountDownTimer((time * 1000).toLong(), 1000) {

            override fun onTick(l: Long) {
                // Do nothing
                return
            }

            override fun onFinish() {
                fragmentManager?.popBackStack()
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

    override fun onDestroy() {
        presenter?.stopScan(activity!!)
        presenter?.onDestroy()
        super.onDestroy()
    }
}