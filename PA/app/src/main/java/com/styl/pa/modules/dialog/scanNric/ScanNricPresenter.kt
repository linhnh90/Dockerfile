package com.styl.pa.modules.dialog.scanNric

import android.app.Activity
import android.content.Context
import com.styl.pa.R
import com.styl.pa.entities.scanner.Barcode
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.scanner.zebraScannerService.DcssdkListener
import com.styl.pa.utils.LogManager

class ScanNricPresenter(var view: IScanNricContact.IView?, var context: Context) : IScanNricContact.IPresenter {

    private var interactor: ScanNricInteractor? = ScanNricInteractor(context)

    companion object {
        private val TAG = ScanNricPresenter::class.java.simpleName
    }

    override fun onDestroy() {
        interactor?.onDestroy()
        view = null
        interactor = null
    }

    private var isPull: Boolean = false
    private var isPullResult: Boolean = false

    override fun startScan(activity: Activity) {
        if (activity is MainActivity) {
            isPull = true
            (activity as MainActivity).setConfigEvent(listener)
            (activity as MainActivity).pullTrigger(true)
        }
    }

    override fun stopScan(activity: Activity) {
        if (activity is MainActivity) {
            isPull = false
            (activity as MainActivity).pullTrigger(false)
        }
    }

    private var listener = object : DcssdkListener.DcssdkConfig {
        override fun dcssdkBarCodeResultEvent(barcode: Barcode?) {
            if (barcode != null) {
                val data = barcode.barcodeDataFormat
                view?.onScanNricSuccess(data)
            }
        }

        override fun dcssdkPullTriggerEvent(result: Boolean) {
            isPullResult = result

            if (!isPullResult && isPull) {
                view?.dismissScanDialog()
                view?.showErrorMessage(R.string.connect_scanner)
            }
        }

        override fun dcssdkConfigAIM(result: Boolean) {
            LogManager.d(TAG, "DcssdkConfigAIM")
        }

    }
}