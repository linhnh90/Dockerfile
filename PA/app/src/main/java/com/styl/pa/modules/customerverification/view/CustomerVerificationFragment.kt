package com.styl.pa.modules.customerverification.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.adapters.PayerAdapter
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.cart.Cart
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.enums.TrackingName
import com.styl.pa.modules.base.CustomBaseDialogFragment
import com.styl.pa.modules.customerverification.CustomerVerificationContract
import com.styl.pa.modules.customerverification.presenter.CustomerVerificationPresenter
import com.styl.pa.modules.dialog.MessageDialogFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.scanner.zebraScannerService.DcssdkListener
import com.styl.pa.utils.BlurProcessor
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.LogManager
import com.styl.pa.utils.MySharedPref
import kotlinx.android.synthetic.main.fragment_customer_verification.view.*

class CustomerVerificationFragment : CustomBaseDialogFragment(), CustomerVerificationContract.View,
        View.OnClickListener {

    companion object {
        private val TAG = CustomerVerificationFragment::class.java.simpleName
        const val ARG_CUSTOMER = BuildConfig.APPLICATION_ID + ".args.ARG_CUSTOMER"
        const val ARG_CUSTOMER_LIST = BuildConfig.APPLICATION_ID + ".args.ARG_CUSTOMER_LIST"
        const val ARG_IS_PAYER = BuildConfig.APPLICATION_ID + ".args.ARG_IS_PAYER"
        const val ARG_IS_CART_UPDATE = BuildConfig.APPLICATION_ID + ".args.ARG_IS_CART_UPDATE"
        const val ARG_SELECTED_PRODUCTS = BuildConfig.APPLICATION_ID + ".args.ARG_SELECTED_PRODUCTS"
        const val ARG_EVENT_TICKET_POSITION = BuildConfig.APPLICATION_ID + ".args.ARG_EVENT_TICKET_POSITION"
        const val ARG_TICKET_ENTITY_POSITION = BuildConfig.APPLICATION_ID + ".args.ARG_TICKET_ENTITY_POSITION"
        const val ARG_PARTICIPANT_POSITION = BuildConfig.APPLICATION_ID + ".args.ARG_PARTICIPANT_POSITION"
        const val ARG_CART_ITEM_POSITION = BuildConfig.APPLICATION_ID + ".args.ARG_CART_ITEM_POSITION"
    }

    private var presenter: CustomerVerificationPresenter? = CustomerVerificationPresenter(this)

    private val onCourseListener = object : CourseAdapter.OnCourseListener {

        override fun onCheckedChange(view: View, position: Int, isChecked: Boolean) {
            presenter?.setSelected(position, isChecked)
        }
    }

    private var isPayer = false
    private var isCartUpdate = false
    private var getView: View? = null
    private var customerList = ArrayList<CustomerInfo> ()
    private var payerAdapter: PayerAdapter? = null
    private var enableSelectPayer = true
    private var blurProcessor: BlurProcessor? = null
    private var eventTicketPosition: Int? = null
    private var ticketEntityPosition: Int? = null
    private var participantPosition: Int? = null
    private var cartItemPosition: Int? = null

    private var textEvent = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (getView?.et_id?.text.toString().isEmpty()) {
                enableListSelectPayer(enableSelectPayer)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            LogManager.d(TAG, "beforeTextChanged")
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            LogManager.d(TAG, "onTextChanged")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customerList = ArrayList(arguments?.getParcelableArrayList(ARG_CUSTOMER_LIST))
        isPayer = arguments?.getBoolean(ARG_IS_PAYER, false)?: false
        isCartUpdate = arguments?.getBoolean(ARG_IS_CART_UPDATE, false)?: false
        eventTicketPosition = arguments?.getInt(ARG_EVENT_TICKET_POSITION)
        ticketEntityPosition = arguments?.getInt(ARG_TICKET_ENTITY_POSITION)
        participantPosition = arguments?.getInt(ARG_PARTICIPANT_POSITION)
        cartItemPosition = arguments?.getInt(ARG_CART_ITEM_POSITION, -1)
        this.presenter?.setParamsForEvent(
            eventTicketPosition, ticketEntityPosition, participantPosition, cartItemPosition
        )
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        getView = activity?.layoutInflater?.inflate(R.layout.fragment_customer_verification, null)
        onSetEventDismissDialog(this)
        getView?.payer_panel?.visibility = View.GONE
        getView?.attendee_panel?.visibility = View.VISIBLE
        playVideo(getView?.img_event, R.drawable.animation_scan_nric)

        getView?.btn_clear?.setOnClickListener(this)
        getView?.btn_ok?.setOnClickListener(this)
        getView?.btn_continue?.setOnClickListener(this)
        getView?.iv_close?.setOnClickListener(this)
        getView?.et_id?.addTextChangedListener(textEvent)
        getView?.btn_continue_payer?.setOnClickListener(this)
        getView?.img_back_list_payer?.setOnClickListener(this)

        LogManager.d("isPayer " + isPayer.toString())

        if (isPayer) {
            getView?.lb_scan?.text = getString(R.string.scan_payer)
            getView?.tv_title?.text = getString(R.string.verify_payer)
        } else {
            getView?.lb_scan?.text = getString(R.string.scan)
            getView?.tv_title?.text = getString(R.string.scan_participant_id)
        }

        presenter?.setCart(getCartFromMain())
//        setupScannerConfigEvent(true)

        if (customerList.size > 0 && isPayer) {
            payerAdapter = PayerAdapter(context, customerList)
            getView?.rcv_payer?.layoutManager = LinearLayoutManager(activity)
            getView?.rcv_payer?.adapter = payerAdapter
            showListSelectPayer(true)
            enableSelectPayer = true
            enableListSelectPayer(enableSelectPayer, 100L)
        } else {
            showListSelectPayer(false)
            enableSelectPayer = false
            enableListSelectPayer(enableSelectPayer, 100L)
        }

        blurProcessor = BlurProcessor(context)

        val dialog = AlertDialog.Builder(activity)
                .setView(getView)
                .create()
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    private fun getCartFromMain(): Cart? {
        return (activity as MainActivity).getBookingCart()
    }

    override fun setCurrentName() {
        super.setCurrentName()
        if (activity != null) {
            (activity as MainActivity).setCurrentViewName(TrackingName.CustomerVerificationFragment.value)
        }
    }

    fun setupScannerConfigEvent(isImmediate: Boolean) {
        var start = 0L

        if (isImmediate) {
            start = 0L
        } else {
            start = 1500L
        }

        Handler().postDelayed({
            presenter?.setupScannerConfigEvent()
        }, start)
    }

    override fun setupScannerConfigEvent(event: DcssdkListener.DcssdkConfig) {
        (activity as? MainActivity)?.setConfigEvent(event)
    }

    override fun pullTrigger(isPull: Boolean) {
        (activity as? MainActivity)?.pullTrigger(isPull)
    }

    override fun showCoursePanel(isShown: Boolean, customerId: String?) {
        presenter?.setAvailableItems(customerId)
        val adapter = CourseAdapter(presenter, getView?.context)
        adapter.onCourseListener = onCourseListener
        getView?.list_course?.layoutManager = GridLayoutManager(activity, 3)
        getView?.list_course?.adapter = adapter

        if (isShown) {
            getView?.img_back_list_payer?.visibility = View.GONE
            getView?.img_payer?.visibility = View.GONE
            getView?.img_event?.visibility = View.GONE

            getView?.payer_panel?.visibility = View.GONE
            getView?.attendee_panel?.visibility = View.GONE

            getView?.course_panel?.visibility = View.VISIBLE
            GeneralUtils.hideSoftKeyboard(getView)

            getView?.btn_ok?.isEnabled = false
            getView?.btn_clear?.isEnabled = false
            getView?.et_id?.isEnabled = false
        } else {
            getView?.payer_panel?.visibility = View.GONE
            getView?.attendee_panel?.visibility = View.VISIBLE

            getView?.course_panel?.visibility = View.GONE
            getView?.btn_ok?.isEnabled = true
            getView?.btn_clear?.isEnabled = true
            getView?.et_id?.isEnabled = true
        }
    }

    override fun onClick(v: View?) {
        GeneralUtils.hideSoftKeyboard(v)

        when (v?.id) {
            R.id.btn_clear -> {
                getView?.et_id?.text?.clear()
            }
            R.id.btn_ok -> {
                closeScanner()
                verifyCustomer()
            }
            R.id.btn_continue -> {
                presenter?.doContinue()
            }
            R.id.iv_close -> {
                dismiss()
            }
            R.id.btn_continue_payer -> {
                // Verify payer
                if (payerAdapter?.selectedPosition != null) {
                    if (payerAdapter?.selectedPosition?: 0 >= (payerAdapter?.itemCount ?: 0) - 1) {
                        enableSelectPayer = false
                        LogManager.d("Start scan payer")
                        enableListSelectPayer(enableSelectPayer)
                    } else {
                        val customerInfo = customerList[payerAdapter!!.selectedPosition!!]
                        presenter?.navigationCartPage(customerInfo)
                    }
                }
            }
            R.id.img_back_list_payer -> {
                enableSelectPayer = true
                enableListSelectPayer(enableSelectPayer)
            }
        }
    }

    private fun verifyCustomer() {
        val id = getView?.et_id?.text.toString()
        val token = getToken() ?: ""
        presenter?.verifyCustomer(token, id)
    }

    fun getToken(): String? {
        return MySharedPref(activity).eKioskHeader
    }

    override fun showInvalidId() {
        clearNric()
        showMessageRescan(R.string.pls_input_valid_nric)
    }

    override fun showMessageRescan(messageId: Int) {
        showMessageRescan(R.string.error, getString(messageId), false)
    }

    override fun showMessageRescan(title: Int, message: String, isShowTitle: Boolean) {
        val dialog =
                MessageDialogFragment.newInstance(title, message, isShowTitle)
        dialog.setListener(object : MessageDialogFragment.MessageDialogListener {

            override fun onPositiveClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onPositiveClickListener")
            }

            override fun onNeutralClickListener(dialogFragment: DialogFragment) {
                enableListSelectPayer(enableSelectPayer)
            }

            override fun onNegativeClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onNegativeClickListener")
            }
        })
        if (fragmentManager != null) {
            dialog.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun <T> showErrorMessage(response: BaseResponse<T>) {
        val dialog = response.formatBaseResponse(0, false)
        dialog?.setListener(object : MessageDialogFragment.MessageDialogListener {

            override fun onPositiveClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onPositiveClickListener")
            }

            override fun onNeutralClickListener(dialogFragment: DialogFragment) {
                enableListSelectPayer(enableSelectPayer)
            }

            override fun onNegativeClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onNegativeClickListener")
            }
        })
        if (fragmentManager != null) {
            dialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun showErrorMessage(messageResId: Int) {
        val dialog =
                MessageDialogFragment.newInstance(R.string.error, messageResId)
        dialog.setListener(object : MessageDialogFragment.MessageDialogListener {

            override fun onPositiveClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onPositiveClickListener")
            }

            override fun onNeutralClickListener(dialogFragment: DialogFragment) {
                this@CustomerVerificationFragment.dismiss()
                fragmentManager?.popBackStack()
            }

            override fun onNegativeClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onNegativeClickListener")
            }
        })
        if (fragmentManager != null) {
            dialog.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun updateNRICCode(nricCode: String) {
        GeneralUtils.hideSoftKeyboard(getView)
        activity?.runOnUiThread {
            if (presenter?.isVerifying == false) {
                if (isPayer) {
                    val token = MySharedPref(activity).eKioskHeader
                    presenter?.verifyCustomer(token, nricCode)
                } else {
                    getView?.et_id?.setText(nricCode)

                    // verify customer immediately once user scan
                    verifyCustomer()
                }
            }
        }
    }

    override fun resetScanner() {
        startScanTimer()
        presenter?.isPull = true
        pullTrigger(true)
    }

    override fun closeScanner() {
        if (true == presenter?.isPull) {
            presenter?.isPull = false
            pullTrigger(false)
        }
        finishScanTimer()
    }

    fun backHome() {
        isBackHome = true
        closeScanner()
    }

    private var isBackHome = false
    override fun isClickScanQRCode() {
        if (isBackHome) {
            isBackHome = false
            if (activity is MainActivity) {
                (activity as MainActivity).navigationScanQRView()
            }
        }
    }

    private val MAX_TIMER = 10000L
    private var handler: Handler? = Handler()
    private var runnable = Runnable {
        resetScanner()
    }

    private fun startScanTimer() {
        if (handler == null) {
            handler = Handler()
        }
        handler?.removeCallbacks(runnable)
        handler?.postDelayed(runnable, MAX_TIMER)
    }

    private fun finishScanTimer() {
        handler?.removeCallbacks(runnable)
        handler = null
    }

    override fun onDestroy() {
        presenter?.onDestroy()
        presenter = null
        super.onDestroy()
    }

    private fun playVideo(imageView: ImageView?, video: Int) {
        activity?.let {
            imageView?.let { imageView ->
                Glide.with(it)
                        .load(video)
                        .into(imageView)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        presenter?.isStop = true
        closeScanner()
    }

    override fun getContext(): Context? {
        return activity
    }

    override fun updatePayerInfoToMain(info: CustomerInfo?) {
        GeneralUtils.hideSoftKeyboard(getView)
        (activity as MainActivity).setPaymentInfo(info)
    }

    override fun clearNric() {
        view?.et_id?.text?.clear()
    }

    private fun showListSelectPayer(isShow: Boolean) {
        if (isShow) {
            getView?.rl_select_payer?.visibility = View.VISIBLE
            getView?.img_back_list_payer?.visibility = View.VISIBLE
        } else {
            getView?.rl_select_payer?.visibility = View.GONE
            getView?.img_back_list_payer?.visibility = View.GONE
        }
    }

    private fun enableListSelectPayer(enable: Boolean, delay: Long = 0L) {
        if (enable) {
            closeScanner()
            getView?.img_opacity_payer?.visibility = View.GONE
            getView?.img_opacity_scan?.visibility = View.VISIBLE
            Handler().postDelayed({
                if(getView?.attendee_panel?.visibility == View.VISIBLE) {
                    val imgRight = getView?.findViewById<RelativeLayout>(R.id.attendee_panel)
                    imgRight?.let {
                        val blurRightBitmap = blurProcessor?.blur(it)
                        getView?.img_opacity_scan?.setImageBitmap(blurRightBitmap)
                    }
                }
            },delay)
        } else {
            setupScannerConfigEvent(true)
            getView?.img_opacity_scan?.visibility = View.GONE
            getView?.img_opacity_payer?.visibility = View.VISIBLE
            Handler().postDelayed({
                if(getView?.rl_select_payer?.visibility == View.VISIBLE) {
                    val imgLeft = getView?.findViewById<RelativeLayout>(R.id.rl_select_payer)
                    imgLeft?.post {
                        imgLeft.let {
                            val blurLeftBitmap = blurProcessor?.blur(it)
                            getView?.img_opacity_payer?.setImageBitmap(blurLeftBitmap)
                        }
                    }
                }
            }, delay)
        }
    }

    override fun isPayer(): Boolean {
        return isPayer
    }

    override fun isCartUpdate(): Boolean {
        return isCartUpdate
    }
}
