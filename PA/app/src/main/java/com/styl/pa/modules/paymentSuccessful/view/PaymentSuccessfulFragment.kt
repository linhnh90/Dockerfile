package com.styl.pa.modules.paymentSuccessful.view


import android.graphics.Bitmap
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.styl.pa.R
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.GeneralException
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.kioskactivation.KioskInfo
import com.styl.pa.entities.product.Product
import com.styl.pa.entities.reservation.BookingDetail
import com.styl.pa.entities.reservation.TotalCostsResponse
import com.styl.pa.entities.wirecard.TransactionResponse
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.checkout.view.CheckoutFragment
import com.styl.pa.modules.dialog.LoadingFragment
import com.styl.pa.modules.dialog.MessageDialogFragment
import com.styl.pa.modules.dialog.WaitingResultFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.paymentSuccessful.IPaymentSuccessfulContract
import com.styl.pa.modules.paymentSuccessful.presenter.PaymentSuccessfulPresenter
import com.styl.pa.modules.printer.customPrinterService.HandlePrintStatus
import com.styl.pa.modules.printer.customPrinterService.PrinterErrorType
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.LogManager
import com.styl.pa.utils.MySharedPref
import kotlinx.android.synthetic.main.fragment_payment_successful.*
import kotlinx.android.synthetic.main.fragment_payment_successful.view.*

/**
 * A simple [Fragment] subclass.
 *
 */
class PaymentSuccessfulFragment : BaseFragment(), View.OnClickListener, HandlePrintStatus, IPaymentSuccessfulContract.IView {

    companion object {

        private val TAG = PaymentSuccessfulFragment::class.java.simpleName

        private const val TIME_DELAY = 1500

        var TRANSACTION_RESPONSE = "transactionResponse"
        var RECEIPT_ID = "receiptID"
        var TOTAL_COST = "tostCost"
        var BITMAP = "bitmap"
        var BOOKING_DETAIL = "bookingDetail"
        var PRODUCT_TYPE = "productType"

        fun newInstance(transactionResponse: TransactionResponse?, receiptId: String?, totalCost: TotalCostsResponse?,
                        bitmap: Bitmap?, bookingDetail: BookingDetail?, productType: String? = null): PaymentSuccessfulFragment {
            val f = PaymentSuccessfulFragment()
            val args = Bundle()
            args.putParcelable(BITMAP, bitmap)
            args.putParcelable(TRANSACTION_RESPONSE, transactionResponse)
            args.putParcelable(TOTAL_COST, totalCost)
            args.putString(RECEIPT_ID, receiptId)
            args.putParcelable(BOOKING_DETAIL, bookingDetail)
            args.putString(PRODUCT_TYPE, productType)
            f.arguments = args
            return f
        }
    }

    private val loadingFragment = LoadingFragment()

    private var dialog: MessageDialogFragment? = null

    private var presenter: PaymentSuccessfulPresenter? = null

    var transactionResponse: TransactionResponse? = null
    var customer: CustomerInfo? = null
    var receiptId: String? = ""
    var totalCost: TotalCostsResponse? = null
    var bitmap: Bitmap? = null
    var productType: String? = null

    var bookingDetail: BookingDetail? = null

    var cartItems: ArrayList<CartItem>? = null

    private var mLastClickTime = 0L

    fun getBudle() {
        if (arguments != null) {
            bitmap = arguments?.getParcelable(BITMAP)
            transactionResponse = arguments?.getParcelable(TRANSACTION_RESPONSE)
            totalCost = arguments?.getParcelable(TOTAL_COST)
            receiptId = arguments?.getString(RECEIPT_ID)
            bookingDetail = arguments?.getParcelable(BOOKING_DETAIL)
            productType = arguments?.getString(PRODUCT_TYPE)
        }
    }

    override fun getStatus(result: GeneralException) {
        var message = PrinterErrorType.errorHash.get(result?.code)
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun endCheckStatus() {
        LogManager.d(TAG, "endCheckStatus")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_send_email -> {
                val email = etMail.text.toString()
                if (false.equals(presenter?.isMailValid(email))) {
                    return
                }

                presenter?.sendEmail(cartItems, receiptId, kioskInfo, totalCost, transactionResponse, customer, email, bookingDetail)
            }

            R.id.btn_end_session,
            R.id.btn_submit -> {
                LogManager.i("Submit/End session pressed")
                checkoutObject?.navigationRatingView()
                (activity as? MainActivity)?.showProgressPayment(false)
            }
        }
    }

    private var getView: View? = null
    private var checkoutObject: CheckoutFragment? = null
    private var kioskInfo: KioskInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getBudle()

        if (parentFragment != null && parentFragment is CheckoutFragment) {
            checkoutObject = parentFragment as CheckoutFragment
        }
        customer = checkoutObject?.getPayer()
        cartItems = checkoutObject?.getCartSummaryList()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        getView = inflater.inflate(R.layout.fragment_payment_successful, container, false)
        (activity as? MainActivity)?.showProgressConfirmation()
        presenter = PaymentSuccessfulPresenter(this, activity)
        kioskInfo = GeneralUtils.convertStringToObject<KioskInfo>(MySharedPref(activity).kioskInfo)
        init()

        return getView
    }

    private fun init() {
        setTitle(getString(R.string.payment_successful_title))
        if ((bookingDetail?.totalPayment ?: (totalCost?.getTotalPaymentAmount()?: 0f)) > 0){
            getView?.txt_suggest_receipt?.text = getString(R.string.receive_receipt_question)
        } else {
            if (productType.equals(Product.PRODUCT_INTEREST_GROUP)){
                getView?.txt_suggest_receipt?.text = getString(R.string.receive_receipt_question_free_ig)
            }else{
                getView?.txt_suggest_receipt?.text = getString(R.string.receive_receipt_question_free)
            }
        }

        (activity as MainActivity).setHandlePrinterStatus(this)

        getView?.print_receipt?.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < TIME_DELAY) {
                return@setOnClickListener
            }
            LogManager.i("Print receipt button pressed")
            mLastClickTime = SystemClock.elapsedRealtime()
            printReceipt()
        }

        getView?.txt_receipt?.text = receiptId ?: getString(R.string.na)

        getView?.btn_send_email?.setOnClickListener(this)
        getView?.btn_end_session?.setOnClickListener(this)
        getView?.btn_submit?.setOnClickListener(this)

        getView?.isFocusableInTouchMode = true
        getView?.requestFocus()
        getView?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                LogManager.i("User press back in keyboard")
                checkoutObject?.backCartView()
                return@OnKeyListener true
            }
            false
        })

        checkPaperStatus()
    }

    private fun checkPaperStatus() {
        if (activity is MainActivity &&
                ((activity as MainActivity).checkStatusPrinter())?.code == PrinterErrorType.ERROR_NO_PAPER) {
            getView?.rl_printing_container?.visibility = View.GONE
            getView?.rl_none_paper_container?.visibility = View.VISIBLE

            presenter?.reportHealthDevice(PrinterErrorType.ERROR_NO_PAPER)
            return
        }

        getView?.rl_printing_container?.visibility = View.VISIBLE
        getView?.rl_none_paper_container?.visibility = View.GONE
    }

    override fun getPayerInfo(): CustomerInfo? {
        return customer
    }

    fun printReceipt() {
        try {
            if ((activity as MainActivity).getConnectPrinterResult()) {
                if (activity is MainActivity) {
                    if (parentFragment is CheckoutFragment) {
                        this.bitmap = (parentFragment as CheckoutFragment).getBitmap()
                    }
                    presenter?.context = activity
                    presenter?.receiptWithSignature(cartItems, receiptId
                            ?: getString(R.string.na), kioskInfo, totalCost,
                            transactionResponse, customer, bitmap, parentFragment, bookingDetail)
                }

            } else {
                LogManager.i("Printer connection error")
                showErrorMessage(R.string.booking_success, R.string.problem_to_printer)
            }
        } catch (e: Exception) {
            LogManager.i("Printer connection error")
            showErrorMessage(R.string.booking_success, R.string.problem_to_printer)
        }
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

    override fun onSendSuccess() {
        goHome()
    }

    override fun showErrorMessage(messageResId: Int) {
        if (dialog != null && dialog!!.isAdded) {
            dialog?.dismiss()
        }

        if (fragmentManager != null) {
            dialog = MessageDialogFragment.newInstance(0, messageResId)
            dialog?.setListener(object : MessageDialogFragment.MessageDialogListener {
                override fun onNegativeClickListener(dialogFragment: DialogFragment) {
                    LogManager.d(TAG, "onNegativeClickListener")
                }

                override fun onPositiveClickListener(dialogFragment: DialogFragment) {
                    LogManager.d(TAG, "onPositiveClickListener")
                }

                override fun onNeutralClickListener(dialogFragment: DialogFragment) {
                    navigationRatingView()
                }

            })
            if (fragmentManager != null) {
                dialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
            }
        }
    }

    override fun showInvalidMail() {
        if (dialog != null && dialog!!.isAdded) {
            dialog?.dismiss()
        }
        dialog = MessageDialogFragment.newInstance(0, R.string.pls_enter_valid_email)
        if (fragmentManager != null) {
            dialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun goHome() {
        if (dialog != null && dialog!!.isAdded) {
            dialog?.dismiss()
        }
        val listener = object : MessageDialogFragment.MessageDialogListener {

            override fun onNegativeClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onNegativeClickListener")
            }

            override fun onPositiveClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onPositiveClickListener")
            }

            override fun onNeutralClickListener(dialogFragment: DialogFragment) {
                (activity as? MainActivity)?.showProgressPayment(false)
                parentFragment?.fragmentManager?.popBackStack(
                        null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }
        }
        dialog = MessageDialogFragment.newInstance(R.string.transaction_completed, R.string.thank_you, true)
        dialog?.setListener(listener)
        if (fragmentManager != null) {
            dialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun navigationRatingView() {
        activity?.runOnUiThread {
            (activity as? MainActivity)?.showProgressPayment(false)
        }
        checkoutObject?.navigationRatingView()
    }

    override fun showErrorMessage(titleId: Int, messageId: Int) {
        if (dialog != null && dialog!!.isAdded) {
            dialog?.dismiss()
        }
        dialog = MessageDialogFragment.newInstance(titleId, messageId, true)
        dialog?.setListener(object : MessageDialogFragment.MessageDialogListener {
            override fun onNegativeClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onNegativeClickListener")
            }

            override fun onPositiveClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onPositiveClickListener")
            }

            override fun onNeutralClickListener(dialogFragment: DialogFragment) {
                navigationRatingView()
            }

        })
        if (fragmentManager != null) {
            dialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    private var waitingSendEmailDialog: WaitingResultFragment? = null

    override fun showWaitingPage() {
        if (waitingSendEmailDialog == null) {
            waitingSendEmailDialog = WaitingResultFragment()
            waitingSendEmailDialog?.setActionName(WaitingResultFragment.EMAIL_EVENT)
            if (fragmentManager != null) {
                waitingSendEmailDialog?.show(
                        fragmentManager!!,
                        WaitingResultFragment::class.java.simpleName
                )
            }
        }
    }

    override fun dismissWaitingPage() {
        waitingSendEmailDialog?.dismiss()
        waitingSendEmailDialog = null
    }
}
