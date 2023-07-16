package com.styl.pa.modules.checkoutVerify.view


import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import com.google.gson.Gson
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.cart.Cart
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.kioskactivation.KioskInfo
import com.styl.pa.entities.payment.PaymentRequest
import com.styl.pa.entities.reservation.TotalCostsResponse
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.checkout.view.CheckoutFragment
import com.styl.pa.modules.checkoutVerify.ICheckoutVerifyContact
import com.styl.pa.modules.checkoutVerify.presenter.CheckoutVerificationPresenter
import com.styl.pa.modules.dialog.LoadingFragment
import com.styl.pa.modules.dialog.MessageDialogFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.utils.GeneralTextUtil
import com.styl.pa.utils.LogManager
import com.styl.pa.utils.MySharedPref
import kotlinx.android.synthetic.main.fragment_checkout_verification.view.*

class CheckoutVerificationFragment : BaseFragment(), View.OnClickListener, RadioGroup.OnCheckedChangeListener, ICheckoutVerifyContact.IView {

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.rb_no -> {
                getView?.ll_not_register_yourself?.visibility = View.VISIBLE
                if (isRefreshUI) {
//                    checkoutObject?.setVerifyResult(false)
                    getView?.img_verify_success?.visibility = View.GONE
                    getView?.txt_verify_success?.visibility = View.GONE
                    getView?.edt_nric?.setText("")
                }
            }

            R.id.rb_yes -> {
                getView?.ll_not_register_yourself?.visibility = View.GONE
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_verify -> {
                // do nothing
                return
            }
        }
    }

    companion object {

        private val TAG = CheckoutFragment::class.java.simpleName

        private val ARG_CUSTOMER_INFO = BuildConfig.APPLICATION_ID + ".args.ARG_CUSTOMER_INFO"

        fun newInstance(customerInfo: CustomerInfo?): CheckoutVerificationFragment {
            val f = CheckoutVerificationFragment()
            val args = Bundle()
            args.putParcelable(ARG_CUSTOMER_INFO, customerInfo)
            f.arguments = args
            return f
        }
    }

    private val CHARACTER_MASK = 5

    private val MAX_CHARACTER = 6

    private var getView: View? = null
    private var checkoutObject: CheckoutFragment? = null

    private var cart: Cart? = null

    private var isVerifyOtherNRIC = false

    private var isRefreshUI = true

    private var anotherNRICVerifySuccess = ""

    private var presenter: CheckoutVerificationPresenter? = CheckoutVerificationPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (parentFragment != null && parentFragment is CheckoutFragment) {
            checkoutObject = parentFragment as CheckoutFragment
        }

        cart = checkoutObject?.getCart()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        getView = inflater.inflate(R.layout.fragment_checkout_verification, container, false)

        init()

        return getView
    }

    private fun init() {
        if ((activity as MainActivity).isQuickBook) {
            setTitle(getString(R.string.payment_summary_title))
        } else {
            setTitle(getString(R.string.cart_summary_title))
        }

        getView?.txt_payer_name?.text = cart?.payer?.mFullName

        getView?.txt_name?.text = cart?.payer?.mFullName
        getView?.txt_ic?.text = GeneralTextUtil.maskText(cart?.payer?.mIdNo, CHARACTER_MASK, true)
        getView?.rg_option?.check(R.id.rb_yes)
        getView?.rg_option?.setOnCheckedChangeListener(this)

        getView?.btn_verify?.setOnClickListener(this)
        getView?.btn_verify?.visibility = View.GONE

        getView?.edt_nric?.inputType = InputType.TYPE_NULL
        getView?.edt_nric?.setOnClickListener(this)

//        checkoutObject?.setVerifyResult(true)
        getView?.ll_not_register_yourself?.visibility = View.GONE
    }

    override fun verifyResult(isResult: Boolean) {
        isRefreshUI = isResult
        if (isResult) {
            when (view?.rg_option?.checkedRadioButtonId) {
                R.id.rb_yes -> {
                    isVerifyOtherNRIC = false
                    anotherNRICVerifySuccess = ""
                }
                R.id.rb_no -> {
                    isVerifyOtherNRIC = true
                    anotherNRICVerifySuccess = if (!getView?.edt_nric?.text?.toString().isNullOrEmpty()) getView?.edt_nric?.text?.toString()!! else ""
                }
            }

            getView?.img_verify_success?.visibility = View.VISIBLE
            getView?.txt_verify_success?.visibility = View.VISIBLE
            getView?.btn_verify?.visibility = View.GONE

//            checkoutObject?.setVerifyResult(true)

        } else {
            when (view?.rg_option?.checkedRadioButtonId) {
                R.id.rb_yes -> {
                    getView?.rb_no?.isChecked = true
                }
                R.id.rb_no -> {
//                    checkoutObject?.setVerifyResult(false)
                }
            }
        }

    }

    fun getCurrentOption(): Int? {
        return getView?.rg_option?.checkedRadioButtonId
    }

    override fun setTotalCost(totalCostsResponse: TotalCostsResponse) {
        checkoutObject?.resultTotalCosts(totalCostsResponse)
    }

    override fun getCartList(): ArrayList<CartItem>? {
        return cart?.items as ArrayList<CartItem>?
    }

    override fun getPaymentRequest(): PaymentRequest {
        val createdAt = System.currentTimeMillis() / 1000
        val kiosk =
                Gson().fromJson<KioskInfo>(MySharedPref(activity).kioskInfo, KioskInfo::class.java)
        return PaymentRequest(kiosk.id, createdAt, "", "")
    }

    override fun setParticipantInfo(participantInfo: CustomerInfo?) {
//        checkoutObject?.setParticipantInfo(participantInfo)
    }

    override fun updatePaymentRequest(mSessionCode: String?) {
        checkoutObject?.updatePaymentRequest(mSessionCode)
    }

    override fun onPause() {
        super.onPause()
        dismissLoading()
        dismissMessageDialog()
    }

    private var loadingDialog: LoadingFragment? = null
    private var messageDialog: MessageDialogFragment? = null

    override fun showLoading() {
        if (loadingDialog == null || loadingDialog?.dialog?.isShowing == false) {
            loadingDialog = LoadingFragment()
            if (fragmentManager != null) {
                loadingDialog?.show(fragmentManager!!, LoadingFragment::class.java.simpleName)
            }
        }

    }

    override fun dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog?.dismiss()
            loadingDialog = null
        }
    }

    override fun <T> showErrorMessage(response: BaseResponse<T>) {
        messageDialog?.dismiss()
        messageDialog = response.formatBaseResponse(0, false)
        if (fragmentManager != null) {
            messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun showErrorMessage(messageResId: Int) {
        if (messageDialog == null || messageDialog?.dialog?.isShowing == false) {
            messageDialog = MessageDialogFragment.newInstance(R.string.error, messageResId)
            if (fragmentManager != null) {
                messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
            }
        }
    }

    private var listener = object : MessageDialogFragment.MessageDialogListener {
        override fun onNegativeClickListener(dialogFragment: DialogFragment) {
            LogManager.d(TAG, "onNegativeClickListener")
        }

        override fun onPositiveClickListener(dialogFragment: DialogFragment) {
            LogManager.d(TAG, "onPositiveClickListener")
        }

        override fun onNeutralClickListener(dialogFragment: DialogFragment) {
            fragmentManager?.popBackStack()
        }

    }

    override fun <T> showErrorMessageAndBack(response: BaseResponse<T>) {
        messageDialog?.dismiss()
        messageDialog = response.formatBaseResponse(0, false)
        messageDialog?.setListener(listener)
        if (fragmentManager != null) {
            messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun showErrorMessageAndBack(messageResId: Int, title: Int) {
        messageDialog?.dismiss()
        messageDialog = MessageDialogFragment.newInstance(title, messageResId, true)
        messageDialog?.setListener(listener)
        if (fragmentManager != null) {
            messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun showErrorMessageAndTitle(messageResId: Int, title: Int) {
        messageDialog?.dismiss()
        messageDialog = MessageDialogFragment.newInstance(title, messageResId, true)
        if (fragmentManager != null) {
            messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun showErrorMessageAndTitle(message: String, title: Int) {
        messageDialog?.dismiss()
        messageDialog = MessageDialogFragment.newInstance(title, message, true)
        if (fragmentManager != null) {
            messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    private fun dismissMessageDialog() {
        if (messageDialog != null) {
            messageDialog?.dismiss()
            messageDialog = null
        }
    }

    override fun getToken(): String? {
        return (activity as MainActivity).getToken()
    }
}
