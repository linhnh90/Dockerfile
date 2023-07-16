package com.styl.pa.modules.payment.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.styl.castle_terminal_upt1000_api.define.FieldData
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.TerminalError
import com.styl.pa.entities.pacesRequest.PaymentRefRequest
import com.styl.pa.entities.payment.PaymentStatus
import com.styl.pa.entities.wirecard.TransactionResponse
import com.styl.pa.enums.TrackingName
import com.styl.pa.interfaces.TimeOutDialogEvent
import com.styl.pa.modules.base.CustomBaseFragment
import com.styl.pa.modules.checkout.view.CheckoutFragment.Companion.REQUEST_PAYMENT
import com.styl.pa.modules.dialog.LoadingFragment
import com.styl.pa.modules.dialog.MessageDialogFragment
import com.styl.pa.modules.dialog.WaitingResultFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.payment.IPaymentContract
import com.styl.pa.modules.payment.presenter.PaymentPresenter
import com.styl.pa.modules.peripheralsManager.terminalManager.IPaymentResultListener
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.LogManager
import com.styl.pa.utils.MySharedPref
import com.styl.pa.utils.RandomStringGenerator
import kotlinx.android.synthetic.main.fragment_payment.view.*
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by trangpham on 9/7/2018
 */
class PaymentFragment : CustomBaseFragment(), IPaymentContract.IView, View.OnClickListener, TimeOutDialogEvent {
    override fun setCurrentName() {

        if (activity != null) {
            (activity as MainActivity).setCurrentViewName(TrackingName.PaymentFragment.value)
        }
    }

    companion object {
        private val TAG = PaymentFragment::class.java.simpleName

        const val CARD_CEPAS = 1
        const val CARD_CREDIT_CONTACTLESS = 2
        const val CARD_CREDIT = 3
        const val CARD_CUP = 4
        const val CARD_NETS_DEBIT = 5
        const val CARD_NETS_FLASH = 6

        private const val MAX_NUMBER_PAYMENT_TRY = 3
        private const val MAX_INTERVAL = 10 * 1000

        val PAYMENT_RESPONE_TIME_OUT = 4 * 60
        val PAYMENT_RETRY_EXTEND = 60

        const val PAYMENT_PROCESS = 0
        const val DEVICE_STATUS_PROCESS = 1
        const val LAST_TXN_PROCESS = 2

        const val TRANSACTION_RESPONSE = "transactionResponse"
        const val EXTRA_TRANSACTION_RAW = BuildConfig.APPLICATION_ID + ".extras.EXTRA_TRANSACTION_RAW"
        const val EXTRA_PAYMENT_TYPE = BuildConfig.APPLICATION_ID + ".extras.EXTRA_PAYMENT_TYPE"
        const val EXTRA_RESPONSE_CODE = BuildConfig.APPLICATION_ID + ".extras.EXTRA_RESPONSE_CODE"
        const val EXTRA_PAYMENT_STATE = BuildConfig.APPLICATION_ID + ".extras.EXTRA_PAYMENT_STATE"
        const val EXTRA_REF_ID = BuildConfig.APPLICATION_ID + ".extras.EXTRA_PAYMENT_REF_ID"
        const val AMOUNT = "amount"

        fun newInstance(amount: Float): PaymentFragment {
            val f = PaymentFragment()
            val args = Bundle()
            args.putFloat(AMOUNT, amount)
            f.arguments = args
            return f
        }
    }

    var waitingResultFragment: WaitingResultFragment? = null

    private val loadingFragment = LoadingFragment()

    private var dialogFragment: MessageDialogFragment? = null

    private var presenter: PaymentPresenter? = PaymentPresenter(this)
    private var mainActivity: MainActivity? = null

    private var mLastErrorTime: Long = 0

    private var amount: Float = 0f

    private var numberPayment = 0

    private var paymentType = 0
    private var paymentMode: String? = null

    private var paymentRefId : String? = null

    override fun onDestroy() {
        presenter?.onDestroy()
        presenter = null
        super.onDestroy()
    }

    private fun getBundle() {
        if (arguments != null && arguments?.getFloat(AMOUNT) != null) {
            amount = arguments?.getFloat(AMOUNT)!!
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payment, null)

        getBundle()
        val dialog = AlertDialog.Builder(activity)
                .setView(view)
                .create()

        view?.ll_ez_link_card?.setOnClickListener(this)
        view?.ll_credit_card?.setOnClickListener(this)
        view?.ll_nets_flashpay?.setOnClickListener(this)
        view?.ll_nets?.setOnClickListener(this)
        view?.rl_container?.setOnClickListener(this)

        // clear background
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        val terminalSubject = (activity as? MainActivity)?.hasTerminalSubject
        if (terminalSubject != null) {
            presenter?.subscribeTerminal(terminalSubject)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        if (activity is MainActivity) {
            mainActivity = activity as MainActivity
        }
        mainActivity?.isBookingInProgress = true
        mainActivity?.setPaymentCallbacks(paymentCallback)
    }

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.payment))
        (activity as? MainActivity)?.showProgressPayment()
    }

    fun touchListener() {
        (activity as MainActivity).dispatchTouchEvent()
    }

    override fun onClick(v: View?) {
        touchListener()
        if (SystemClock.elapsedRealtime() - MainActivity.globalLastClickTime < 2000) {
            return
        }
        MainActivity.globalLastClickTime = SystemClock.elapsedRealtime()

        // terminal not connected
        if (true != (activity as? MainActivity)?.presenter?.peripheralsService?.isTerminalConnected()) {
            val dialog = MessageDialogFragment.newInstance(0, R.string.unable_connect_terminal)
            if (fragmentManager != null) {
                dialog.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
            }
            return
        }

        // terminal need to wait 10s between transactions
        if (SystemClock.elapsedRealtime() - mLastErrorTime < MAX_INTERVAL) {
            val dialog = MessageDialogFragment.newInstance(0, R.string.terminal_not_ready)
            if (fragmentManager != null) {
                dialog.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
            }
            return
        }

        when (v?.id) {
            R.id.ll_ez_link_card -> {
                paymentType = CARD_CEPAS
                handlePayOptions(PaymentRefRequest.PAYMENT_MODE_EZLINK)
            }
            R.id.ll_nets_flashpay -> {
                paymentType = CARD_NETS_FLASH
                handlePayOptions(PaymentRefRequest.PAYMENT_MODE_NETS)
            }
            R.id.ll_credit_card -> {
                paymentType = CARD_CREDIT_CONTACTLESS
                handlePayOptions(PaymentRefRequest.PAYMENT_MODE_CARD)
            }
            R.id.ll_nets -> {
                paymentType = CARD_NETS_DEBIT
                handlePayOptions(PaymentRefRequest.PAYMENT_MODE_NETS)
            }
        }

        LogManager.i("User select payment method $paymentType")
    }

    private val listener = object : MessageDialogFragment.MessageDialogListener {

        override fun onNegativeClickListener(dialogFragment: DialogFragment) {
            LogManager.d(TAG, "onNegativeClickListener")
        }

        override fun onPositiveClickListener(dialogFragment: DialogFragment) {
            LogManager.d(TAG, "onPositiveClickListener")
        }

        override fun onNeutralClickListener(dialogFragment: DialogFragment) {
            if (numberPayment >= MAX_NUMBER_PAYMENT_TRY) {
                LogManager.i("Delete cart because payment failed up to 3 times")
                (activity as? MainActivity)?.deleteCart()
                fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }

    var paymentStatus = PaymentStatus.Companion.Status.FAILED


    private fun randomString(): String {
        val randomStringGenerator = RandomStringGenerator(SecureRandom())
        return randomStringGenerator.randomString(15)
    }

    private fun simulateTerminalResp() {
        paymentStatus = PaymentStatus.Companion.Status.COMPLETED
        numberPayment++


        val intent = Intent()
        val txnResp = TransactionResponse()
        txnResp.rrn = randomString()

        if (paymentType == CARD_CEPAS) {
            txnResp.paymentType = Integer.toHexString(FieldData.TxnType.PAYMENT_BY_EZL.toInt())
        } else if (paymentType == CARD_CREDIT_CONTACTLESS) {
            txnResp.paymentType = Integer.toHexString(FieldData.TxnType.PAYMENT_BY_SCHEME_CREDIT.toInt())
        } else if (paymentType == CARD_NETS_FLASH) {
            txnResp.paymentType = Integer.toHexString(FieldData.TxnType.PAYMENT_BY_NETS_NFP.toInt())
        } else {
            txnResp.paymentType = Integer.toHexString(FieldData.TxnType.PAYMENT_BY_NETS_EFT.toInt())
        }

        intent.putExtra(TRANSACTION_RESPONSE, txnResp)
        intent.putExtra(EXTRA_TRANSACTION_RAW, ByteArray(1))
        intent.putExtra(EXTRA_PAYMENT_TYPE, paymentType)
        intent.putExtra(EXTRA_RESPONSE_CODE, "0")
        intent.putExtra(EXTRA_PAYMENT_STATE, paymentStatus.value)
        intent.putExtra(EXTRA_REF_ID, paymentRefId)

        waitingResultFragment?.dismiss()
        targetFragment?.onActivityResult(REQUEST_PAYMENT, Activity.RESULT_OK, intent)

        fragmentManager?.popBackStack()
    }

    private val paymentCallback = object : IPaymentResultListener {
        override fun onPaymentSuccess(msgHeaderError: String, txnResponse: TransactionResponse?, bytes: ByteArray?) {
            paymentStatus = PaymentStatus.Companion.Status.COMPLETED
            numberPayment++

            val intent = Intent()
            intent.putExtra(TRANSACTION_RESPONSE, txnResponse)
            intent.putExtra(EXTRA_TRANSACTION_RAW, bytes)
            intent.putExtra(EXTRA_PAYMENT_TYPE, paymentType)
            intent.putExtra(EXTRA_RESPONSE_CODE, msgHeaderError)
            intent.putExtra(EXTRA_PAYMENT_STATE, paymentStatus.value)
            intent.putExtra(EXTRA_REF_ID, paymentRefId)

            waitingResultFragment?.dismiss()
            targetFragment?.onActivityResult(REQUEST_PAYMENT, Activity.RESULT_OK, intent)

            fragmentManager?.popBackStack()
        }

        override fun onPaymentFail(msgHeaderError: String, txnResponse: TransactionResponse?, bytes: ByteArray?) {
            numberPayment++

            val intent = Intent()
            intent.putExtra(TRANSACTION_RESPONSE, txnResponse)
            intent.putExtra(EXTRA_TRANSACTION_RAW, bytes)
            intent.putExtra(EXTRA_PAYMENT_TYPE, paymentType)
            intent.putExtra(EXTRA_RESPONSE_CODE, msgHeaderError)
            intent.putExtra(EXTRA_PAYMENT_STATE, paymentStatus.value)
            intent.putExtra(EXTRA_REF_ID, paymentRefId)

            waitingResultFragment?.dismiss()
            targetFragment?.onActivityResult(REQUEST_PAYMENT, Activity.RESULT_CANCELED, intent)

            mLastErrorTime = SystemClock.elapsedRealtime()

            val errorCode = msgHeaderError.toInt()
            var errorMessageId = TerminalError.getInstance().terminalErrMap[errorCode]
            if (errorMessageId == null) {
                errorMessageId = R.string.common_error
            }

            val dialog = MessageDialogFragment.newInstance(R.string.payment_unsuccessful, errorMessageId, false)
            dialog.setListener(listener)
            if (fragmentManager != null) {
                dialog.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
            }

        }

        override fun onUnablePayment(libError: Int, msgHeaderError: Int, resErrorMessage: Int?) {

            if (GeneralUtils.isSimulateTerminal()) { // check in build.gradle
                if (paymentType == CARD_NETS_FLASH) { // simulate timeout when payment method is NFP
                    onUnablePayment()
                    return
                }

                // There are no terminal so this call back will be returned
                simulateTerminalResp()
            } else {
                onUnablePayment()
            }
        }

        override fun extendSessionTimeout(extendTime: Int) {
            mainActivity?.startTimer(PAYMENT_RESPONE_TIME_OUT, true)
        }

        override fun changePaymentProgressStatus(status: Int) {
            var content = ""
            when (status) {
                DEVICE_STATUS_PROCESS -> {
                    content = getString(R.string.device_status_process)
                }

                LAST_TXN_PROCESS -> {
                    content = getString(R.string.last_txn_process)
                }
            }
            waitingResultFragment?.setContent(content)
        }
    }

    private fun handlePayOptions(newSelectedPaymentMode: String) {
        paymentMode = newSelectedPaymentMode
        val cartInfo = (activity as MainActivity).getBookingCart()
        if (paymentRefId == null) {
            val expiryDateTime = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
            val paymentRefRequest = PaymentRefRequest(
                    cartInfo?.payer?.mFullName,
                    cartInfo?.sessionCode,
                    amount,
                    expiryDateTime,
                    paymentMode
            )
            presenter?.createPaymentReference(getToken(), paymentRefRequest)
        } else if (cartInfo?.isLocked == false) {
            prepareCheckout()
        } else {
            pay(paymentType, Math.round(amount * 100))
        }
    }

    private fun onUnablePayment() {
        numberPayment++
        waitingResultFragment?.dismiss()

        val dialog = MessageDialogFragment.newInstance(0, R.string.common_error)
        dialog.setListener(listener)
        if (fragmentManager != null) {
            dialog.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    private fun pay(cardType: Int, amount: Int) {
        waitingResultFragment = WaitingResultFragment()
        waitingResultFragment?.setActionName(WaitingResultFragment.ACTION_PAYMENT)
        waitingResultFragment?.isCancelable = false
        if (fragmentManager != null) {
            waitingResultFragment?.show(fragmentManager!!, WaitingResultFragment::class.java.simpleName)
        }
        if (false == mainActivity?.payProduct(cardType, amount)) {
            onUnablePayment()
        }
    }

    override fun <T> showErrorMessage(response: BaseResponse<T>) {
        if (dialogFragment != null && dialogFragment!!.isVisible) {
            dialogFragment?.dismiss()
        }
        if (response.errorCode < 0) {
            if (response.messageResId != null && response.messageResId!!.compareTo(0) > 0) {
                dialogFragment = MessageDialogFragment.newInstance(0, response.messageResId!!)
            } else {
                dialogFragment = MessageDialogFragment.newInstance("", response.errorMessage!!)
            }
        } else {
            dialogFragment = MessageDialogFragment.newInstance(
                    response.errorCode,
                    "",
                    response.errorMessage!!
            )
        }
        if (fragmentManager != null) {
            dialogFragment?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun showErrorMessage(messageResId: Int) {
        if (dialogFragment!!.isVisible) {
            dialogFragment?.dismiss()
        }
        dialogFragment = MessageDialogFragment.newInstance(0, messageResId)
        if (fragmentManager != null) {
            dialogFragment?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun getToken(): String? {
        return MySharedPref(activity).eKioskHeader
    }

    override fun onTerminalDisconnected() {
        if (waitingResultFragment != null) {
            waitingResultFragment?.dismissAllowingStateLoss()
        }
    }

    private fun prepareCheckout() {
        val token = getToken() ?: ""
        val cartId = (activity as MainActivity).getBookingCartId()
        val cartInfo = (activity as MainActivity).getBookingCart()
        if (cartId != null && cartInfo?.payer != null && cartInfo.isLocked == false) {
            presenter?.prepareCheckout(
                    token,
                    cartId,
                    cartInfo.payer!!,
                    cartInfo.sessionCode,
                    paymentMode
            )
        }
    }

    override fun onCreatePaymentReferenceSuccess(data: String?) {
        paymentRefId = data
        prepareCheckout()
    }

    override fun onPrepareCheckoutSuccess() {
        Handler().postDelayed({ dismissLoading() }, 200)
        (activity as MainActivity).setCartLocked()
        pay(paymentType, Math.round(amount * 100))
    }
}
