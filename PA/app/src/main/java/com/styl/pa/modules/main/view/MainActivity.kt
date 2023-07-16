package com.styl.pa.modules.main.view

import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.*
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.styl.kioskcore.SystemManager
import com.styl.pa.BuildConfig
import com.styl.pa.MyExceptionHandler
import com.styl.pa.R
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.GeneralException
import com.styl.pa.entities.cart.Cart
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.courseCategory.CourseCategory
import com.styl.pa.entities.courseCategory.CourseCategoryList
import com.styl.pa.entities.courseCategory.EventCategoryList
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.generateToken.*
import com.styl.pa.entities.healthDevice.HealthDeviceRequest
import com.styl.pa.entities.healthDevice.InfoHealthDevice
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.entities.journey.JourneyRequest
import com.styl.pa.entities.journey.JourneyScreenInfo
import com.styl.pa.entities.journey.UserInfo
import com.styl.pa.entities.kioskactivation.KioskInfo
import com.styl.pa.entities.log.LastScreenLogRequest
import com.styl.pa.entities.payment.PaymentOption
import com.styl.pa.entities.product.ProductListResponse
import com.styl.pa.enums.BookingType
import com.styl.pa.enums.SearchType
import com.styl.pa.enums.TagName
import com.styl.pa.enums.TrackingName
import com.styl.pa.modules.advancedSearch.view.AdvancedSearchFragment
import com.styl.pa.modules.base.Base2Fragment
import com.styl.pa.modules.base.BaseActivity
import com.styl.pa.modules.cart.view.CartFragment
import com.styl.pa.modules.cart.view.EmptyCartFragment
import com.styl.pa.modules.checkout.view.CheckoutFragment
import com.styl.pa.modules.checkoutVerify.view.CheckoutVerificationFragment
import com.styl.pa.modules.courseDetails.view.CourseDetailsFragment
import com.styl.pa.modules.customerverification.view.CustomerVerificationFragment
import com.styl.pa.modules.dialog.AdminCheckFragment
import com.styl.pa.modules.dialog.LoadingFragment
import com.styl.pa.modules.dialog.MessageDialogFragment
import com.styl.pa.modules.dialog.WaitingResultFragment
import com.styl.pa.modules.eventDetails.view.EventDetailsFragment
import com.styl.pa.modules.facilityDetails.view.FacilityDetailsFragment
import com.styl.pa.modules.feedback.view.FeedbackFragment
import com.styl.pa.modules.help.HelpFragment
import com.styl.pa.modules.home.view.HomeFragment
import com.styl.pa.modules.indemnity.view.IndemnityFragment
import com.styl.pa.modules.kioskactivation.view.KioskActivationFragment
import com.styl.pa.modules.main.IMainContract
import com.styl.pa.modules.main.presenter.MainPresenter
import com.styl.pa.modules.payment.view.PaymentFragment
import com.styl.pa.modules.paymentSuccessful.view.PaymentSuccessfulFragment
import com.styl.pa.modules.peripheralsManager.peripheralsService.PeripheralsService
import com.styl.pa.modules.peripheralsManager.terminalManager.IPaymentResultListener
import com.styl.pa.modules.printer.IPrinterFontConfig
import com.styl.pa.modules.printer.customPrinterService.HandlePrintStatus
import com.styl.pa.modules.rating.view.RatingFragment
import com.styl.pa.modules.ruleAndRegulations.view.RuleAndRegulationsFragment
import com.styl.pa.modules.scanQrCode.ScanQrCodeFragment
import com.styl.pa.modules.scanner.zebraScannerService.DcssdkListener
import com.styl.pa.modules.search.view.SearchPageFragment
import com.styl.pa.modules.setting.SettingsFragment
import com.styl.pa.modules.setting.terminalSetting.view.TerminalSettingsFragment
import com.styl.pa.modules.signature.view.SignatureFragment
import com.styl.pa.serverlocal.WebServerService
import com.styl.pa.services.MyJobService
import com.styl.pa.utils.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_progress_payment.*
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : BaseActivity(), IMainContract.IView, View.OnClickListener {

    var counter = 0

    override fun onClick(v: View?) {
        if (v?.id != R.id.img_logo) {
            if (SystemClock.elapsedRealtime() - globalLastClickTime < CLICK_TIMER) {
                return
            }
            globalLastClickTime = SystemClock.elapsedRealtime()
        }

        if (isBookingInProgress) {
            if (v?.id != R.id.ll_cart) {
                showBookingInProgress()
                return
            } else {
                if (getCurrentObject() is PaymentFragment) {
                    showBookingInProgress()
                    return
                }
            }
        }

        when (v?.id) {
            R.id.ll_help -> {
                if (getCurrentObject() !is HelpFragment) {
                    val token = MySharedPref(this).eKioskHeader
                    presenter?.help(token)
                }
            }

            R.id.ll_cart -> {
                LogManager.i("Cart button pressed")
                if (cart?.items?.size ?: 0 > 0 || quickbookCart.items?.size ?: 0 > 0) {
                    if (getCurrentObject() !is CartFragment) {
                        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        this.isPopHome = true
                        presenter?.navigationCardView()
                    }
                } else {
                    if (getCurrentObject() !is EmptyCartFragment) {
                        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        this.isPopHome = true
                        presenter?.navigationEmptyCardView()
                    }
                }
            }

            R.id.img_logo -> {
                counter++
                if (counter == 3) {
                    val f = AdminCheckFragment()
                    f.show(supportFragmentManager, AdminCheckFragment::class.java.simpleName)
                }

                Handler().postDelayed({ counter = 0 }, DELAY_TIME)
            }

            R.id.rb_home -> {
                setBackgroundBottomBar(MODE_HOME)
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                if (supportFragmentManager.findFragmentByTag(TagName.HomeFragment.value) is HomeFragment) {
                    (supportFragmentManager.findFragmentByTag(TagName.HomeFragment.value) as HomeFragment).setLocation()
                }
            }

            R.id.rb_attend_course -> {
                setBackgroundBottomBar(MODE_COURSES)
                val kioskInfo = GeneralUtils.convertStringToObject<KioskInfo>(MySharedPref(this).kioskInfo)
                val outlet = kioskInfo?.outlet
                if (getCurrentObject() !is SearchPageFragment) {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    this.isPopHome = true
                    presenter?.navigationAttendCoursesView(outlet, classInfoList)
                } else {
                    if (!SearchType.COURSES.toString().equals((getCurrentObject() as SearchPageFragment).getSearchType())) {
                        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        this.isPopHome = true
                        presenter?.navigationAttendCoursesView(outlet, classInfoList)
                    }
                }

            }

            R.id.rb_book_facilities -> {
                setBackgroundBottomBar(MODE_FACILITIES)
                val kioskInfo = GeneralUtils.convertStringToObject<KioskInfo>(MySharedPref(this).kioskInfo)
                val outlet = kioskInfo?.outlet
                if (getCurrentObject() !is SearchPageFragment) {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    this.isPopHome = true
                    presenter?.navigationBookFacilitiesView(outlet, classInfoList)
                } else {
                    if (!SearchType.FACILITIES.toString().equals((getCurrentObject() as SearchPageFragment).getSearchType())) {
                        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        this.isPopHome = true
                        presenter?.navigationBookFacilitiesView(outlet, classInfoList)
                    }
                }
            }

            R.id.rb_participate -> {
                val kioskInfo = GeneralUtils.convertStringToObject<KioskInfo>(MySharedPref(this).kioskInfo)
                val outlet = kioskInfo?.outlet
                val f = getCurrentObject()
                if (f !is SearchPageFragment) {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    this.isPopHome = true
                    presenter?.navigationParticipateEvent(outlet, classInfoList)
                } else {
                    if (!SearchType.EVENTS.toString().equals((getCurrentObject() as SearchPageFragment).getSearchType())) {
                        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        this.isPopHome = true
                        presenter?.navigationParticipateEvent(outlet, classInfoList)
                    }
                }
            }

            R.id.rb_scan_qr -> {
                LogManager.i("Scan courses/events")
                setBackgroundBottomBar(MODE_SCAN_COURSES_EVENTS)
                val f = getCurrentObject()
                if (f !is HomeFragment) {
                    if (f is CustomerVerificationFragment) {
                        f.backHome()
                        return
                    } else {
                        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        this.isPopHome = true
                    }
                }

                if (isConnectScannerSuccess) {
                    presenter?.navigationScanQRView()
                } else {
                    showErrorMessage(R.string.connect_scanner)
                }
            }
            R.id.iv_participant -> {
                if (supportFragmentManager.findFragmentByTag(TagName.CartFragment.value) != null) {
                    supportFragmentManager.popBackStack(TagName.CartFragment.value, 0)
                }
            }
            R.id.iv_summary -> {
                if (supportFragmentManager.findFragmentByTag(TagName.CheckoutFragment.value) != null) {
                    supportFragmentManager.popBackStack(TagName.CheckoutFragment.value, 0)
                }
            }
            R.id.rl_interest_group -> {
                setBackgroundBottomBar(MODE_INTEREST_GROUPS)
                val kioskInfo = GeneralUtils.convertStringToObject<KioskInfo>(MySharedPref(this).kioskInfo)
                val outlet = kioskInfo?.outlet
                if (getCurrentObject() !is SearchPageFragment) {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    this.isPopHome = true
                    presenter?.navigationInterestGroupsView(outlet, classInfoList)
                } else {
                    if (!SearchType.INTEREST_GROUPS.toString().equals((getCurrentObject() as SearchPageFragment).getSearchType())) {
                        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        this.isPopHome = true
                        presenter?.navigationInterestGroupsView(outlet, classInfoList)
                    }
                }
            }
        }
    }

    private fun startLocalServer() {
        val intent = Intent(this, WebServerService::class.java)
        startService(intent)
    }

    fun navigationScanQRView() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        this.isPopHome = true

        if (isConnectScannerSuccess) {
            presenter?.navigationScanQRView()
        } else {
            showErrorMessage(R.string.connect_scanner)
        }
    }

    companion object {

        private val TAG = MainActivity::class.java.simpleName

        const val NUMBER_KIOSK: Int = 100

        const val ACTION_TERMINAL_DAILY = BuildConfig.APPLICATION_ID + ".action.ACTION_TERMINAL_DAILY"
        var globalLastClickTime = 0L

        const val CART_ITEM_MAX = 2

        // 1 min
        const val WORK_DURATION: Long = 60 * 1000
        const val JOB_ID: Int = 200

        // 3 hours
        const val INTERVAL_TIME: Long = 3 * 60 * 60 * 1000

        const val CLICK_TIMER = 1500

        const val HEALTH_REPORT_ID: Int = 300
        const val INTERVAL_HEALTH_TIME = 1L

        const val TERMINAL_DAILY_CODE = 1405

        const val REQUEST_PREVENT_STATUS_BAR_EXPANSION = 2000

        private const val DELAY_TIME = 1500L

        var isConnectScannerSuccess = false
        var isConnectPrinterSuccess = false

        const val MODE_HOME = "HOME"
        const val MODE_COURSES = "COURSES"
        const val MODE_EVENTS = "EVENTS"
        const val MODE_FACILITIES = "FACILITIES"
        const val MODE_SCAN_COURSES_EVENTS = "MODE_SCAN_COURSES_EVENTS"
        const val MODE_INTEREST_GROUPS = "MODE_INTEREST_GROUPS"

        const val PARTICIPANT = 1
        const val SUMMARY = 2
        const val PAYMENT = 3
        const val CONFIRMATION = 4

        private const val ALIVE_TRACKING_TIMER = 20L
        private const val REFRESH_TOKEN_TIMER = 30

        private const val START_TIME_KEEP_ALIVE = 9
        private const val END_TIME_KEEP_ALIVE = 22
    }

    private var terminalFailedTime = 0

    var canOrder = true

    var presenter: MainPresenter? = MainPresenter(this)
    var systemManager: SystemManager? = null

    private val loadingFragment = LoadingFragment()

    private var fragment: MessageDialogFragment? = null

    val hasPrinterSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()
    val hasScannerSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()
    val hasTerminalSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()

    private val printerGrantedSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()
    private val scannerGrantedSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()
    private val terminalGrantedSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()

    val kioskInfoSubject: BehaviorSubject<String> = BehaviorSubject.create()

    private var addToCartSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()
    var mAddToCartSubject: BehaviorSubject<Boolean>
        get() {
            return addToCartSubject
        }
        set(value) {
            this.addToCartSubject = value
        }

    //    private var cartInfoList: ArrayList<CartInfo> = ArrayList()
    var cart: Cart? = Cart(ArrayList(), null, null)
    var quickbookCart = Cart(ArrayList(), null, null)
    var isBookingInProgress = false
    var isQuickBook = false
    var lockedCart: Cart? = null
    var cartId: String? = null
    var quickCartId: String? = null

    /**
     * Broadcast receiver to handle USB disconnect events.
     */
    private var usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    if (presenter?.isPrinter(device) == true) {
                        hasPrinterSubject.onNext(false)
                        isConnectPrinterSuccess = false
                        presenter?.detachPrinterUsb()
                    } else if (presenter?.isScanner(device) == true) {
                        hasScannerSubject.onNext(false)
                        presenter?.detachScannerUsb()
                    } else if (presenter?.isTerminal(device) == true) {
                        hasTerminalSubject.onNext(false)
                        presenter?.detachTerminalUsb()
                        presenter?.initTerminalManagerResult(false)
                    }
                }
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE) ?: return
                    if (presenter?.isPrinter(device) == true) {
                        hasPrinterSubject.onNext(true)
                        presenter?.initPrinterManager(device)
                    } else if (presenter?.isScanner(device) == true) {
                        hasScannerSubject.onNext(true)
                        presenter?.initScannerManager(device)
                    } else if (presenter?.isTerminal(device) == true) {
                        hasTerminalSubject.onNext(true)
                        presenter?.initTerminalManager(device)
                        presenter?.initTerminalManagerResult(true)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        generateTykToken()

        initStatusBarExpansion(this)
        presenter?.registerTerminalDailySchedule(true)

        if (!StoreLogsUtils.checkFolderExternalStorage(this)) {
            initView(false)
            return
        }

        doCreate()
    }

    private fun doCreate() {
        // exception handler
        Thread.setDefaultUncaughtExceptionHandler(MyExceptionHandler(this))

        startLocalServer()

        hasScannerSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    Toast.makeText(this, "hasScanner: " + it.toString(), Toast.LENGTH_SHORT).show()
                    if (it) {
                        LogManager.i("Scanner connected")
                    } else {
                        LogManager.i("Scanner disconnected")
                    }
                }
            )
        hasPrinterSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    Toast.makeText(this, "hasPrinter: " + it.toString(), Toast.LENGTH_SHORT).show()
                    if (it) {
                        LogManager.i("Printer connected")
                    } else {
                        LogManager.i("Printer disconnected")
                    }
                }
            )
        scannerGrantedSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    Toast.makeText(this, "scannerGranted: " + it.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            )
        printerGrantedSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    Toast.makeText(this, "printerGranted: " + it.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            )

        presenter?.subscribeKioskInfo(kioskInfoSubject)
        presenter?.addBackStackChanged(this)
        init()

        createLogDaily()

        val filter = IntentFilter()
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        registerReceiver(usbReceiver, filter, GeneralUtils.COMMON_PERMISSION, null)

        val kioskInfo = MySharedPref(this).kioskInfo
        val iv = MySharedPref(this).iv
        if (kioskInfo.isNullOrBlank() || iv.isNullOrEmpty()) {
            showActivationScreen()
        } else {
            initView(false)

            val ki = Gson().fromJson<KioskInfo>(kioskInfo, KioskInfo::class.java)
            presenter?.authenticateKiosk(ki?.accessKey, ki?.secretKey)
        }
    }

    override fun onStart() {
        super.onStart()
        preventStatusBarExpansion(false)
    }

    // status bar config
    private fun initStatusBarExpansion(context: Context) {
        if (systemManager == null) {
            systemManager = SystemManager.init(context)
        }
    }

    private fun preventStatusBarExpansion(isShow: Boolean) {
        var delayTime = 0L
        if (!isShow) {
            delayTime = 1L
        }
        Handler().postDelayed({
            systemManager?.enableStatusBar(isShow)
            systemManager?.enableNavigationBar(isShow)
        }, delayTime)
    }


    fun destroyStatusBarExpansion() {
        Handler().postDelayed({
            systemManager?.destroy()
            systemManager = null
        }, 1)
    }

    // end status bar config

    class CustomViewGroup(context: Context) : ViewGroup(context) {

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            // Do nothing
            return
        }

        override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
            // Intercepted touch!
            return true
        }
    }

    private fun initView(isShow: Boolean?) {
        if (isShow == null) {
            pausePlayer()
            txt_status?.text = ""
            ll_init_container?.visibility = View.GONE
            ll_bottom_bar?.visibility = View.INVISIBLE
            ll_top_bar?.visibility = View.GONE
            container?.visibility = View.GONE
            return
        }

        if (isShow) {
            pausePlayer()
            img_loading?.visibility = View.GONE
            ll_init_container?.visibility = View.GONE
            setBackgroundBottomBar(MODE_HOME)
            ll_bottom_bar?.visibility = View.VISIBLE
            ll_top_bar?.visibility = View.VISIBLE
            container?.visibility = View.VISIBLE
        } else {
            playVideo()
            ll_bottom_bar?.visibility = View.INVISIBLE
            ll_top_bar?.visibility = View.GONE
            container?.visibility = View.GONE
            ll_init_container?.visibility = View.VISIBLE
        }
        showOnlyPassionMember(true)
        iv_participant?.setOnClickListener(this)
        iv_summary?.setOnClickListener(this)
    }

    override fun updateStatus(resource: Int, isSuccess: Boolean) {
        runOnUiThread {
            txt_status.text = getString(resource)
            if (!isSuccess) {
                pausePlayer()
            } else {
                startPlayer()
            }
        }
    }


    override fun updateStatus(message: String, isSuccess: Boolean) {
        runOnUiThread {
            txt_status.text = message
            if (!isSuccess) {
                pausePlayer()
            } else {
                startPlayer()
            }
        }
    }

    private fun playVideo() {
        runOnUiThread {
            img_loading?.let { imageView ->
                Glide.with(this)
                    .load(R.drawable.animation_initiation_data)
                    .into(imageView)
            }
        }
    }

    private fun pausePlayer() {
        runOnUiThread {
            img_loading?.let { imageView ->
                Glide.with(this)
                    .load(R.drawable.ic_initiation)
                    .into(imageView)
            }
        }
    }

    private fun startPlayer() {
        playVideo()
    }

    private fun startAllOfSchedule() {
        scheduleJob()
        healthReportDaily()
        getScreensaver()
        checkUpgradeFirmware()
        presenter?.rebootKioskDaily()
        presenter?.terminalDailySchedule()
    }

    override fun onStop() {
        super.onStop()
        preventStatusBarExpansion(true)
        cancelAllJobs()
    }

    override fun onDestroy() {
        stopTimer()
        destroyStatusBarExpansion()

        presenter?.disconnectScanner()
        presenter?.disconnectPrinter()
        presenter?.disconnectTerminal()

        presenter?.destroyTerminalDailySchedule()
        presenter?.registerTerminalDailySchedule(false)

        presenter?.onDestroy()
        presenter = null

        try {
            unregisterReceiver(usbReceiver)
        } catch (e: IllegalArgumentException) {
            LogManager.d("Unregister receiver failed")
        }

        aliveTrackingInterVal?.dispose()
        compositeHealthReportDaily.clear()
        compositeHealthReportDaily.dispose()

        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        dismissDialogDelete()
        dismissDialogBookingNotFinished()
    }

    override fun showLoading() {
//        if (!loadingFragment.isVisible) {
//            loadingFragment.show(supportFragmentManager, LoadingFragment::class.java.simpleName)
//        }
    }

    override fun showLoadingDialog() {
        if (!loadingFragment.isVisible) {
            loadingFragment.show(supportFragmentManager, LoadingFragment::class.java.simpleName)
        }
    }

    override fun dismissLoading() {
        if (loadingFragment.isAdded) {
            loadingFragment.dismiss()
        }
    }

    override fun <T> showErrorMessage(response: BaseResponse<T>) {
        dismissLoading()

        if (fragment != null && fragment!!.isVisible) {
            fragment?.dismiss()
        }
        if (response.errorCode < 0) {
            if (response.messageResId != null && response.messageResId!!.compareTo(0) > 0) {
                fragment = MessageDialogFragment.newInstance(0, response.messageResId!!)
            } else {
                fragment = MessageDialogFragment.newInstance("", response.errorMessage!!)
            }
        } else {
            fragment = MessageDialogFragment.newInstance(
                response.errorCode,
                "",
                response.errorMessage!!
            )
        }
        fragment?.show(supportFragmentManager, MessageDialogFragment::class.java.simpleName)
    }

    override fun showErrorMessage(messageResId: Int) {
        dismissLoading()

        fragment?.dismiss()

        fragment = MessageDialogFragment.newInstance(0, messageResId)
        fragment?.show(supportFragmentManager, MessageDialogFragment::class.java.simpleName)
    }

    override fun showErrorMessage(titleResId: Int, messageResId: Int) {
        if (fragment != null && fragment!!.isVisible) {
            fragment?.dismiss()
        }
        fragment = MessageDialogFragment.newInstance(titleResId, messageResId, true)
        fragment?.show(supportFragmentManager, MessageDialogFragment::class.java.simpleName)
    }

    override fun handleCourseEvent(classInfo: ClassInfo, isAddCart: Boolean, isQuickBook: Boolean) {
        // Do nothing because this method is never called
    }

    override fun handleIgEvent(igInfo: InterestGroup, isAddCart: Boolean, isQuickBook: Boolean) {
       presenter?.goToIgDetails(igInfo)
    }

    private var isFirst = false
    private var expiry: Int? = null
    override fun onAuthenticationSuccess(token: String?, expiry: Int?, isFirst: Boolean) {
        MySharedPref(this).eKioskHeader = token
        if (isFirst) {
            initView(false)
            startAllOfSchedule()
            presenter?.getOtaConfig()
            presenter?.initUsbDevices()
        }
        this.expiry = expiry
        this.isFirst = isFirst
    }

    override fun onAuthenticationFailed() {
        val kioskInfo = Gson().fromJson<KioskInfo>(MySharedPref(this).kioskInfo, KioskInfo::class.java)
        presenter?.refreshToken(null, kioskInfo)
    }

    override fun onGetInfoSuccess(info: String) {
        MySharedPref(this).kioskInfo = info

        val token = MySharedPref(this).eKioskHeader
        presenter?.updateKioskInfo(token, getEthernetMacAddress(), Build.ID)
        if (isFirst) {
            presenter?.getLocationAndCategories(this, outlet?.outletTypeName)
            isFirst = false

        }
    }

    override fun onAddToCartSuccess(response: BookingResponse?, itemsToAdd: ArrayList<CartItem>,
                                    errorMessage: String?, addType: BookingType) {
        if (this.cartId.isNullOrEmpty()) {
            this.cartId = response?.cartId
        }
        itemsToAdd.forEach { item ->
            if (item.classInfo != null || item.event != null) {
                cart?.items?.add(item)
            }
        }
        setItemAddToCart()
        if (!errorMessage.isNullOrEmpty()) {
            showErrorMessage(errorMessage)
        }
    }

    override fun onDeleteCartResponse() {
        LogManager.d("onDeleteCartResponse")
    }

    override fun showActivationScreen() {
        val dialog = KioskActivationFragment()
        dialog.show(supportFragmentManager, KioskActivationFragment::class.java.simpleName)
    }

    override fun getContext(): Context {
        return this
    }

    override fun setScannerResult(result: Boolean) {
        isConnectScannerSuccess = result
    }

    //scanner
    fun getConnectScannerResult(): Boolean {
        return isConnectScannerSuccess
    }

    fun setConfigEvent(listener: DcssdkListener.DcssdkConfig) {
        presenter?.setConfigEvent(listener)
    }

    fun pullTrigger(isPull: Boolean) {
        presenter?.pullTrigger(isPull)
    }


    //printer
    fun getPrinterFontConfig(): IPrinterFontConfig? {
        return presenter?.getPrinterFontConfig()
    }

    fun getPrinterStatus(): Int {
        return presenter?.printerStatus ?: InfoHealthDevice.PRINTER_CONNECT
    }

    fun setPrinterStatus(printerStatus: Int) {
        presenter?.printerStatus = printerStatus
    }

    override fun setPrinterResult(result: Boolean) {
        isConnectPrinterSuccess = result
    }

    fun getConnectPrinterResult(): Boolean {
        return isConnectPrinterSuccess
    }

    fun setHandlePrinterStatus(event: HandlePrintStatus) {
        presenter?.setHandlePrinterStatusEvent(event)
    }

    fun printText(text: String, printerFont: Int?, fontSize: Int?,
                  isBold: Boolean, isItalic: Boolean, isUnderline: Boolean): GeneralException? {
        return presenter?.printText(text, printerFont, fontSize, isBold, isItalic, isUnderline)
    }

    fun cutPage() {
        presenter?.cutPage()
    }

    fun printFeed() {
        presenter?.printFeed()
    }

    fun checkStatusPrinter(): GeneralException? {
        return presenter?.checkStatusPrinter()
    }

    fun printImage(bitmap: Bitmap?) {
        presenter?.printImage(bitmap)
    }

    // terminal
    fun setPaymentCallbacks(listener: IPaymentResultListener?) {
        presenter?.setPaymentCallbacks(listener)
    }

    fun payProduct(cardType: Int, amount: Int): Boolean {
        return presenter?.payProduct(cardType, amount) ?: false
    }


    fun init() {
        rb_home.setOnClickListener(this)
        rb_attend_course.setOnClickListener(this)
        rb_book_facilities.setOnClickListener(this)
        rb_participate.setOnClickListener(this)
        rb_scan_qr.setOnClickListener(this)
        rl_interest_group.setOnClickListener(this)

        ll_cart.setOnClickListener(this)
        img_logo.setOnClickListener(this)
        ll_help.setOnClickListener(this)

        setItemAddToCart()

        GeneralUtils.hideBottomBar(window.decorView.rootView, this)
    }

    fun enableOutsideView(isShow: Boolean) {
        for (i in 0 until ll_bottom_bar.childCount) {
            val child = ll_bottom_bar.getChildAt(i)
            if (isShow) {
                child.setOnClickListener(this)
            } else {
                child.setOnClickListener(null)
            }
        }

        for (i in 0 until ll_top_bar.childCount) {
            val child = ll_top_bar.getChildAt(i)
            if (isShow) {
                child.setOnClickListener(this)
            } else {
                child.setOnClickListener(null)
            }
        }
    }

    fun setTitle(title: String?) {
        txt_title?.text = HtmlCompat.fromHtml(title ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun setItemAddToCart() {
        var size = cart?.items?.size ?: 0
        if (isQuickBook) {
            size = quickbookCart.items?.size ?: 0
            if (size == 0) {
                releaseCart(quickbookCart)
                quickCartId = null
                isQuickBook = false
                size = cart?.items?.size ?: 0
            }
        }
        txt_total_item.text = (size ?: 0).toString()
        if (size ?: 0 == 0) {
            deleteAllCart(false)
        }
    }

    private fun navigationCartView(isBookingMyself: Boolean) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        this.isPopHome = true
        if (isBookingMyself) {
            presenter?.navigationCardView()
        } else {
            presenter?.navigationCartView(false)
        }
    }

    fun doQuickBook(cartItems: List<CartItem>) {
        doQuickBook(cartItems = cartItems, isBookingMyself = true)
    }

    fun doQuickBook(cartItems: List<CartItem>, isBookingMyself: Boolean) {
        isBookingInProgress = true
        isQuickBook = true
        quickbookCart.items?.addAll(cartItems)
        setItemAddToCart()
        Handler().post {
            navigationCartView(isBookingMyself)
        }
    }

    fun showCartError() {
        deleteDialog = MessageDialogFragment.newInstance(R.string.sorry, getString(R.string.items_up_to, CART_ITEM_MAX), true)
        deleteDialog?.show(supportFragmentManager, MessageDialogFragment::class.java.simpleName)
    }

    fun showBookingInProgress() {
        LogManager.i("Show order not finished!")
        bookingNotFinishedDialog = MessageDialogFragment.newInstance(
            R.string.order_not_finish_yet_title,
            R.string.order_not_finish_yet_msg,
            true
        )
        bookingNotFinishedDialog!!.setOrderNotFinishedDialog()
        bookingNotFinishedDialog!!.setMultiChoice(getString(R.string.no), getString(R.string.yes))
        bookingNotFinishedDialog!!.setListener(object : MessageDialogFragment.MessageDialogListener {
            override fun onNegativeClickListener(dialogFragment: DialogFragment) {
                LogManager.d("onNegativeClickListener")
            }

            override fun onPositiveClickListener(dialogFragment: DialogFragment) {
                trackAbortTxn()
                LogManager.i("User end transaction and delete cart")
                deleteCart()
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }

            override fun onNeutralClickListener(dialogFragment: DialogFragment) {
                LogManager.d("onNeutralClickListener")
            }

        })
        bookingNotFinishedDialog!!.show(supportFragmentManager, MessageDialogFragment::class.java.simpleName)
    }

    fun addToCart(cartItems: List<CartItem>) {
        if (cart?.items?.size?.plus(cartItems.size) ?: 0 > CART_ITEM_MAX) {
            showCartError()
        } else {
            val errorMessage = StringBuilder()
            cartItems.forEach { cartItem ->
                var isExisting = false
                cart?.items?.forEach { item ->
                    if (true == item.classInfo?.getClassId()?.equals(cartItem.classInfo?.getClassId()) ||
                        true == item.igInfo?.igId?.equals(cartItem.igInfo?.igId) ||
                        true == item.event?.eventId?.equals(cartItem.event?.eventId)) {
                        isExisting = true
                        return@forEach
                    }
                }

                val productName = getProductName(cartItem)
                if (!isExisting) {
                    cart?.items?.add(cartItem)

                    setItemAddToCart()
                    if (errorMessage.isNotEmpty()) {
                        errorMessage.append("\n")
                    }
                    errorMessage.append(getString(R.string.product_added_cart, productName))
                } else {
                    if (errorMessage.isNotEmpty()) {
                        errorMessage.append("\n")
                    }
                    errorMessage.append(getString(R.string.product_already_added_cart, productName))
                }
            }
            showErrorMessage(errorMessage.toString())
        }
    }

    private fun getProductName(cartItem: CartItem): String? {
        var productName = cartItem.classInfo?.getDecodedTitle()
        if (productName == null){
            productName = cartItem.igInfo?.getDecodedTitle()
        }
        if (productName == null) {
            productName = cartItem.event?.getDecodedTitle()
        }
        if (productName == null) {
            productName = cartItem.facility?.getDecodedName()
        }
        return productName.toString()
    }

    fun releaseCart(cart: Cart?) {
        cart?.items?.clear()
        cart?.payer = null
        cart?.sessionCode = null
        cart?.isLocked = false
        cart?.hasReservation = false
    }

    fun deleteAllCart(needUpdateView: Boolean = true) {
        if (isQuickBook) {
            releaseCart(quickbookCart)
            quickCartId = null
            isQuickBook = false
        } else {
            releaseCart(cart)
            cartId = null
        }
        if (needUpdateView) {
            setItemAddToCart()
        }
        isBookingInProgress = false
    }

    fun getBookingCart(): Cart? {
        return if (isQuickBook) {
            quickbookCart
        } else {
            cart
        }
    }

    fun setCartLocked() {
        return if (isQuickBook) {
            quickbookCart.isLocked = true
        } else {
            cart?.isLocked =true
        }
    }

    fun getBookingCartId(): String? {
        return if (isQuickBook) {
            quickCartId
        } else {
            cartId
        }
    }

    private fun getPayerId(): String? {
        return if (isQuickBook) {
            quickbookCart.payer?.mCustomerId
        } else {
            cart?.payer?.mCustomerId
        }
    }

    private fun deleteCart(cartId: String, userId: String?) {
        val token = getToken()
        presenter?.deleteCart(token, userId, cartId)
    }

    fun deleteCart(isTimeoutSession: Boolean = false) {
        LogManager.i("Delete Cart call from View = $currentViewName")
        if (isTimeoutSession) {
            if (quickCartId != null) {
                deleteCart(quickCartId!!, quickbookCart.payer?.mCustomerId)
            }
            if (cartId != null) {
                deleteCart(cartId!!, cart?.payer?.mCustomerId)
            }
        } else {
            val cartId = getBookingCartId()
            val userId = getPayerId()
            if (cartId != null) {
                deleteCart(cartId, userId)
            }
        }
        deleteAllCart()
    }

    private var deleteDialog: MessageDialogFragment? = null
    private var bookingNotFinishedDialog: MessageDialogFragment? = null

    private fun dismissDialogDelete() {
        if (deleteDialog != null) {
            deleteDialog?.dismiss()
        }
    }

    private fun dismissDialogBookingNotFinished() {
        bookingNotFinishedDialog?.dismiss()
    }

    fun deleteCartItem(cartItem: CartItem) {
        var i = 0
        while (i < (cart?.items?.size ?: 0)) {
            val item = cart?.items?.get(i)

            if (item == null) {
                return
            }

            if ((!item.classInfo?.getClassId().isNullOrEmpty()) && (item.classInfo?.getClassId().equals(cartItem.classInfo?.getClassId()))) {
                cart?.items?.remove(item)
                break
            }

            if ((!item.igInfo?.igId.isNullOrEmpty()) && (item.igInfo?.igId.equals(cartItem.igInfo?.igId))) {
                cart?.items?.remove(item)
                break
            }

            if ((!item.facility?.getResourceID().isNullOrEmpty()) && (item.facility?.getResourceID().equals(cartItem.facility?.getResourceID()))) {
                cart?.items?.remove(item)
                break
            }

            if ((!item.event?.eventId.isNullOrEmpty()) && (item.event?.eventId.equals(cartItem.event?.eventId))) {
                cart?.items?.remove(item)
                break
            }

            i++
        }

        setItemAddToCart()
    }

    override fun onBackPressed() {
        val f = supportFragmentManager.findFragmentById(R.id.container)
        if (f is HomeFragment) {
            return
        } else if (f is CheckoutFragment &&
            f.childFragmentManager.findFragmentById(R.id.container_checkout) is PaymentSuccessfulFragment) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            return
        }

        if (f != null && f !is HomeFragment) {
            dispatchTouchEvent()
        }

        super.onBackPressed()
    }

    private fun getCurrentObject(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.container)
    }

    override fun getToken(): String? {
        return MySharedPref(this).eKioskHeader
    }

    override fun onGetOutletTypeSuccess(outletType: PageByOutletType) {
        // Do nothing
        return
    }

    override fun onSearchOutletDetailByName(outletDetail: PageByOutletDetail) {
        // Do nothing
        return
    }

    var classInfoList: ArrayList<ClassInfo>? = ArrayList()

    fun showBottomBar(isShow: Boolean) {
        if (isShow) {
            if (outletDetailResult && courseCategoryResult && eventCategoryResult) {
                ll_bottom_bar.visibility = View.VISIBLE
            } else {
                ll_bottom_bar.visibility = View.GONE
            }
        } else {
            ll_bottom_bar.visibility = View.GONE
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {

        val f = supportFragmentManager?.findFragmentById(R.id.container)
        if (f != null) {
            if (f !is HomeFragment) {
                isMessageCountDown = false
                startTimer(TIME_COUNT_DOWN, true)
            } else {
                f.dispatchTouchEvent(event)
            }
        }

        if (event?.getAction() == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.getRawX().toInt(), event.getRawY().toInt())) {
                    v.clearFocus()
                    GeneralUtils.hideKeyboard(this)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }


    /**
     * Timeout 3m
     */

    var backTimerSubject: PublishSubject<Boolean> = PublishSubject.create()
    val stopCountDownTimerFromHome: PublishSubject<Boolean> = PublishSubject.create()

    fun dispatchTouchEvent() {
        val f = supportFragmentManager?.findFragmentById(R.id.container)
        if (f is HomeFragment) {
            f.dispatchTouchEvent(null)
        }
        isMessageCountDown = false
        startTimer(TIME_COUNT_DOWN, true)
    }

    private var countDownTimer: CountDownTimer? = null
    val TIME_COUNT_DOWN = 2 * 60
    private val TIME_COUNT_DOWN_MESSAGE = 30
    private var extendSessionDialog: MessageDialogFragment? = null
    private var isMessageCountDown = false

    private var currentViewName = ""

    fun setCurrentViewName(currentViewName: String?) {
        if (!currentViewName.isNullOrEmpty()) {
            this.currentViewName = currentViewName!!
        }
    }

    fun setCurrentViewNameFragment() {
        val currentView: String? = getCurrentView()
        if (!currentView.isNullOrEmpty()) {
            this.currentViewName = currentView
        }
    }

    fun getCurrentView(): String? {
        var currentView: String? = null
        if (supportFragmentManager?.findFragmentById(R.id.container) != null) {
            val mainFragment = supportFragmentManager?.findFragmentById(R.id.container)

            if (mainFragment is HomeFragment) {
                currentView = TrackingName.HomeFragment.value

            } else if (mainFragment is CartFragment) {
                currentView = TrackingName.CartFragment.value

            } else if (mainFragment is CourseDetailsFragment) {
                currentView = TrackingName.CourseDetailsFragment.value

            } else if (mainFragment is FacilityDetailsFragment) {
                currentView = TrackingName.FacilityDetailsFragment.value

            } else if (mainFragment is EventDetailsFragment) {
                currentView = TrackingName.EventDetailsFragment.value

            } else if (mainFragment is ScanQrCodeFragment) {
                currentView = TrackingName.ScanQrCodeFragment.value
            } else if (mainFragment is HelpFragment) {
                currentView = TrackingName.HelpFragment.value

            } else if (mainFragment is CheckoutFragment) {
                val childFragment = (mainFragment as CheckoutFragment).childFragmentManager.findFragmentById(R.id.container_checkout)
                val signalFragment = (mainFragment as CheckoutFragment).childFragmentManager.findFragmentById(R.id.container_signature)


                if (childFragment is CheckoutVerificationFragment) {
                    currentView = TrackingName.CheckoutVerificationFragment.value

                } else if (childFragment is PaymentSuccessfulFragment) {
                    currentView = TrackingName.PaymentSuccessfulFragment.value

                }

                if (signalFragment is SignatureFragment) {
                    currentView = TrackingName.SignatureFragment.value
                }
            } else if (mainFragment is SearchPageFragment) {
                val searchType = (mainFragment as SearchPageFragment).getSearchType()

                if (!searchType.isNullOrEmpty()) {

                    if (searchType.equals(SearchType.FACILITIES.toString())) {
                        currentView = TrackingName.SearchFacilityPageFragment.value

                    } else if (searchType.equals(SearchType.COURSES.toString())) {
                        currentView = TrackingName.SearchCoursePageFragment.value

                    } else if (searchType.equals(SearchType.EVENTS.toString())){
                        currentView = TrackingName.SearchEventPageFragment.value
                    } else if (searchType.equals(SearchType.INTEREST_GROUPS.toString())) {
                        currentView = TrackingName.SearchInterestGroupPageFragment.value

                    }else {
                        currentView = TrackingName.SearchAcrossProductFragment.value
                    }
                }
            } else if (mainFragment is EmptyCartFragment) {
                currentView = TrackingName.EmptyCartFragment.value

            } else if (mainFragment is PaymentFragment) {
                currentView = TrackingName.PaymentFragment.value

            } else if (mainFragment is CustomerVerificationFragment) {
                currentView = TrackingName.CustomerVerificationFragment.value

            } else if (mainFragment is RatingFragment) {
                currentView = TrackingName.RatingFragment.value

            } else if (mainFragment is FeedbackFragment) {
                currentView = TrackingName.FeedbackFragment.value

            } else if (mainFragment is IndemnityFragment) {
                currentView = TrackingName.IndemnityFragment.value

            } else if (mainFragment is SettingsFragment) {
                currentView = TrackingName.SettingsFragment.value
                val childFragment = (mainFragment as SettingsFragment).childFragmentManager.findFragmentById(R.id.fl_setting_container)
                if (childFragment is TerminalSettingsFragment) {
                    currentView = TrackingName.TerminalSettingsFragment.value
                }

            }
        }
        return currentView
    }

    fun getDialogCurrentView(dialog: DialogFragment): String? {
        var currentView: String? = null

        if (dialog is AdvancedSearchFragment) {
            currentView = TrackingName.AdvancedSearchFragment.value

//        } else if (dialog is PaymentFragment) {
//            currentView = TrackingName.PaymentFragment.value

        } else if (dialog is RuleAndRegulationsFragment) {
            currentView = TrackingName.RuleAndRegulationsFragment.value

        } else if (dialog is WaitingResultFragment) {
            currentView = TrackingName.WaitingResultFragment.value

        } else if (dialog is CustomerVerificationFragment) {
            currentView = TrackingName.CustomerVerificationFragment.value

        }
        return currentView
    }

    private fun trackAbortTxn() {
        val request = LastScreenLogRequest(currentViewName, System.currentTimeMillis() / 1000,
            getPayerId())
        presenter?.lastScreenLog(request)
    }

    private fun initTimer(time: Int) {
        countDownTimer = object : CountDownTimer((time * 1000).toLong(), 1000) {

            override fun onTick(l: Long) {
                // Do nothing
                return
            }

            override fun onFinish() {
                if (!isMessageCountDown) {
                    stopTimer()
                    showDialog()
                } else {
                    isMessageCountDown = false
                    dismissDialog()
                    timeoutSession()
                }
            }
        }
    }

    fun timeoutSession() {
        backTimerSubject.onNext(true)

        GeneralUtils.hideKeyboard(this@MainActivity)

        Handler().postDelayed({
            if (isDestroyed) {
                return@postDelayed
            }

            supportFragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

            trackAbortTxn()
        }, 500)

        Handler().postDelayed(object : Runnable {
            override fun run() {
                LogManager.i("Timeout session. Delete cart!")
                deleteCart(true)
                endJourney()

                if (supportFragmentManager?.findFragmentById(R.id.container) is HomeFragment) {
                    (supportFragmentManager?.findFragmentById(R.id.container) as HomeFragment).hideSoftKeyboard()
                }
            }
        }, 1200)
    }

    fun startTimer(time: Int, isReset: Boolean) {
        stopTimer()
        initTimer(time)

        countDownTimer?.start()
    }

    fun stopTimer() {
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
    }

    private fun showDialog() {
        dismissDialog()
        extendSessionDialog = MessageDialogFragment.newInstance(R.string.session_ending_soon, R.string.extend_session_question, true)
        extendSessionDialog?.setIsSessionTimeoutDialog()
        extendSessionDialog?.setListener(object : MessageDialogFragment.MessageDialogListener {
            override fun onNegativeClickListener(dialogFragment: DialogFragment) {
                timeoutSession()
            }

            override fun onPositiveClickListener(dialogFragment: DialogFragment) {
                isMessageCountDown = false
                startTimer(TIME_COUNT_DOWN, true)

            }

            override fun onNeutralClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onNeutralClickListener")
            }

        })
        extendSessionDialog?.show(supportFragmentManager, MessageDialogFragment::class.java.simpleName)

        isMessageCountDown = true
        startTimer(TIME_COUNT_DOWN_MESSAGE, true)
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        // Do nothing
        return
    }

    private fun dismissDialog() {
        if (extendSessionDialog != null) {
            extendSessionDialog?.dismiss()
            extendSessionDialog = null
        }
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

    private fun scheduleJob() {
        val componentName = ComponentName(this, MyJobService::class.java)
        val extras = PersistableBundle()
        extras.putLong(MyJobService.EXTRA_WORK_DURATION, WORK_DURATION)
        val jobInfo = JobInfo.Builder(JOB_ID, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPeriodic(INTERVAL_TIME)
            .setExtras(extras)
            .build()

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(jobInfo)
    }

    private fun cancelAllJobs() {
        val tm = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        tm.cancelAll()
    }

    private var compositeHealthReportDaily = CompositeDisposable()
    private fun healthReportDaily() {
        val waitTime = presenter?.calculateWaitingTime(8, 0, 0, true) ?: 0L

        val disposable = Observable.timer(waitTime, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .doAfterNext {
                presenter?.reportDailyHealth()
            }
            .concatMap { t: Long -> Observable.interval(INTERVAL_HEALTH_TIME, TimeUnit.DAYS) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = {},
                onNext = {
                    presenter?.reportDailyHealth()
                }
            )
        compositeHealthReportDaily.add(disposable)
    }

    private fun getScreensaver() {
        presenter?.getListScreensaver()
    }

    private fun checkUpgradeFirmware() {

        val otaTime = presenter?.calculateOtaTime() ?: return

        val waitTime = presenter?.calculateWaitingTime(otaTime[0], otaTime[1], 0, true) ?: 0L

        val disposable = Observable.timer((waitTime / 1000), TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterNext {
                presenter?.rebootForOTA()
            }
            .concatMap { t: Long -> Observable.interval(1, TimeUnit.DAYS) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = {},
                onNext = {
                    presenter?.rebootForOTA()
                }
            )
        compositeHealthReportDaily.add(disposable)

    }

    private fun createLogDaily() {
        if (StoreLogsUtils.checkFolderExternalStorage(this)) {
            val waitTime = presenter?.calculateWaitingTime(0, 2, 0, true) ?: 0L

            StoreLogsUtils.writeLogs()
            val disposable = Observable.timer((waitTime / 1000), TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .doAfterNext {
                    StoreLogsUtils.writeLogs()
                }
                .concatMap { t: Long -> Observable.interval(1, TimeUnit.DAYS) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onError = {},
                    onNext = {
                        StoreLogsUtils.writeLogs()
                    }
                )
            compositeHealthReportDaily.add(disposable)
        }
    }

    private fun generateTykToken() {
        val internalKeyStream = assets.open(TykConfig.INTRANET_KEY_FILE)
        val externalKeyStream = assets.open(TykConfig.EXTERNAL_KEY_FILE)
        TykConfig.generateTykToken(internalKeyStream, externalKeyStream)
    }

    /**
     * alive tracking 20m
     */

    private var tokenTimeInterval= 0
    private var aliveTrackingInterVal: Disposable? = null

    // only ping kiosk from 9:00-22:00
    private fun canPingKiosk(): Boolean {
        val startTimePingKiosk = Calendar.getInstance()
        startTimePingKiosk.set(Calendar.HOUR_OF_DAY, START_TIME_KEEP_ALIVE)
        startTimePingKiosk.set(Calendar.MINUTE, 0)
        startTimePingKiosk.set(Calendar.SECOND, 0)

        val endTimePingKiosk = Calendar.getInstance()
        endTimePingKiosk.set(Calendar.HOUR_OF_DAY, END_TIME_KEEP_ALIVE)
        endTimePingKiosk.set(Calendar.MINUTE, 0)
        endTimePingKiosk.set(Calendar.SECOND, 0)

        val currentTime = Calendar.getInstance().timeInMillis

        return currentTime > startTimePingKiosk.timeInMillis && currentTime < endTimePingKiosk.timeInMillis
    }

    fun aliveTracking() {
        aliveTrackingInterVal?.dispose()
        presenter?.aliveTracking()
        aliveTrackingInterVal = Observable.interval(ALIVE_TRACKING_TIMER, TimeUnit.MINUTES)
            .timeInterval()
            .subscribeOn(Schedulers.io())
            .subscribe {
                tokenTimeInterval += ALIVE_TRACKING_TIMER.toInt()
                if (tokenTimeInterval >= REFRESH_TOKEN_TIMER) {
                    tokenTimeInterval = 0
                    generateTykToken()
                }
                if (canPingKiosk()) {
                    presenter?.aliveTracking()
                }
            }
    }

    fun healthUpdate(deviceInfo: InfoHealthDevice) {
        presenter?.deviceHealthUpdate(HealthDeviceRequest(deviceInfo))
    }

    override fun isConnectPrinter(): Boolean {
        return isConnectPrinterSuccess
    }

    override fun isConnectScanner(): Boolean {
        return isConnectScannerSuccess
    }


    private var locationList: ArrayList<Outlet>? = ArrayList()
    private var neighbourhoods: ArrayList<Neighbourhood> = ArrayList()
    var outlet: Outlet? = null
    private var categoryList: ArrayList<CourseCategory>? = ArrayList()
    private var categoryEventList: ArrayList<CourseCategory>? = ArrayList()

    fun getLocationList(): ArrayList<Outlet> {
        val locations = ArrayList<Outlet>()
        if (locationList != null) {
            for (d in locationList!!) {
                locations.add(Outlet(d))
            }
        }
        return locations
    }

    fun getNeighbourhoods(): ArrayList<Neighbourhood> {
        val neighbourhoods = ArrayList<Neighbourhood>()
        neighbourhoods.addAll(this.neighbourhoods)
        return neighbourhoods
    }

    fun getCategoryList(): ArrayList<CourseCategory> {
        val categories = ArrayList<CourseCategory>()
        if (categoryList != null) {
            for (d in categoryList!!) {
                categories.add(CourseCategory(d))
            }
        }
        return categories
    }

    fun getCategoryEventList(): ArrayList<CourseCategory> {
        val categories = ArrayList<CourseCategory>()
        if (categoryEventList != null) {
            for (d in categoryEventList!!) {
                categories.add(CourseCategory(d))
            }
        }
        return categories
    }

    private var outletDetailResult = false
    private var courseCategoryResult = false
    private var eventCategoryResult = false
    var paymentOptions: List<PaymentOption>? = ArrayList()


    override fun onGetPaymentOptionsSuccess(data: List<PaymentOption>?) {
        this.paymentOptions = data
//        presenter?.getLocationAndCategories(this, outlet?.getOutletTypeId())
    }

    override fun onLocationByOutletSuccess(pageByOutletDetail: PageByOutletDetail?) {
        outletDetailResult = false
        Handler(Looper.getMainLooper()).post(Runnable {
            if (pageByOutletDetail?.getOutletDetailList() != null) {
                locationList?.addAll(pageByOutletDetail.getOutletDetailList())
                neighbourhoods.addAll(pageByOutletDetail.getNeighbourhood())
                locationList = presenter?.sortLocationByName(locationList)
                neighbourhoods = presenter?.sortNeighbourhoods(neighbourhoods) ?: ArrayList()
                outletDetailResult = true
                navigationHomeView()
            }
        })
    }

    override fun onCategorySuccess(categories: CourseCategoryList?) {
        courseCategoryResult = false
        Handler(Looper.getMainLooper()).post(Runnable {
            this.categoryList = categories?.getCourseCategoryList()
            courseCategoryResult = true
            navigationHomeView()
//            dismissLoading()
//            presenter?.navigationHomeView()
        })
    }

    override fun onCategoryEventSuccess(categories: EventCategoryList?) {
        eventCategoryResult = false
        Handler(Looper.getMainLooper()).post(Runnable {
            this.categoryEventList = categories?.eventCategoryList
            eventCategoryResult = true
            navigationHomeView()
//            dismissLoading()
//            presenter?.navigationHomeView()
        })
    }

    private fun navigationHomeView() {
        if (outletDetailResult && courseCategoryResult && eventCategoryResult) {
            dismissLoading()
            initView(true)
            presenter?.navigationHomeView()
        }
    }

    override fun showErrorMessageAndTitle(message: Int, title: Int) {
        dismissLoading()
        fragment?.dismiss()
        fragment = MessageDialogFragment.newInstance(title, message, true)
        fragment?.show(supportFragmentManager, MessageDialogFragment::class.java.simpleName)
    }

    private fun showErrorMessage(message: String) {
        fragment?.dismiss()
        fragment = MessageDialogFragment.newInstance(0, message, false)
        fragment?.show(supportFragmentManager, MessageDialogFragment::class.java.simpleName)
    }

    override fun onGetAllClassInfoSuccess(productListResponse: ProductListResponse?) {
        dismissLoading()
        if (productListResponse?.productList != null && productListResponse.productList!!.size > 0) {
            val classInfo = ClassInfo(productListResponse.productList!![0])
            presenter?.goToClassDetails(classInfo)
        } else {
            showErrorMessageAndTitle(R.string.proceed_to_counter_for_assistance, R.string.class_not_found)
        }
    }

    override fun onGetAllEventInfoSuccess(eventResponse: ProductListResponse?) {
        dismissLoading()
        if (eventResponse?.productList != null && eventResponse.productList!!.size > 0) {
            val eventInfo = EventInfo(productInfo = eventResponse.productList!![0])
            presenter?.goToEventDetails(eventInfo)
        } else {
            showErrorMessageAndTitle(R.string.proceed_to_counter_for_assistance, R.string.event_not_found)
        }
    }

    override fun onGetAllIGSuccess(igResponse: ProductListResponse?) {
        dismissDialog()
        if (igResponse?.productList != null && igResponse.productList!!.size > 0) {
            val igInfo = InterestGroup(productInfo = igResponse.productList!![0])
            //checkvacancy
            presenter?.checkIgVacancy(igInfo, false, false)
        } else {
            showErrorMessageAndTitle(R.string.proceed_to_counter_for_assistance, R.string.ig_not_found)
        }
    }

    override fun onScanQrSuccess(data: String?) {
        presenter?.getAllProductInfo(data, this)
    }


    private var startTime: Long? = null
    private var endTime: Long? = null
    private var journeyViewName: String? = null
    private var listJourneyScreen = ArrayList<JourneyScreenInfo>()
    private var journeyRequest: JourneyRequest? = JourneyRequest(null, null, null)
    private var isAdd = true
    private var isPopHome = false

    fun addRecord(isAdd: Boolean) {
        endTime = System.currentTimeMillis()
        if (!journeyViewName.isNullOrEmpty() && startTime != null && endTime != null && this.isAdd) {
            var duration = 0.0
            try {
                duration = (endTime!! - startTime!!) * 1.0 / 1000
            } catch (e: Exception) {
                LogManager.i("Error when calculating journey duration")
            }

            if (duration >= 0.6) {
                listJourneyScreen.add(JourneyScreenInfo(journeyViewName!!, (startTime!! / 1000).toString(), duration))
            }
        }
        this.isAdd = isAdd
        startTime = System.currentTimeMillis()
    }

    fun setJourneyViewName(name: String?) {
        this.journeyViewName = name
    }

    fun setIsAdd(isAdd: Boolean) {
        this.isAdd = isAdd
        if (this.isPopHome) {
            this.isAdd = false
            this.isPopHome = false
        }
    }

    fun setPaymentInfo(info: CustomerInfo?) {
        journeyRequest?.userInfo = UserInfo(info)
    }

    fun endJourney() {
        journeyRequest?.sessionId = UUID.randomUUID().toString()
        journeyRequest?.screenList = listJourneyScreen

        presenter?.trackUserJourney(journeyRequest)
        this.isAdd = true
        Handler().postDelayed({
            listJourneyScreen.clear()
            journeyRequest = JourneyRequest(null, null, null)
        }, 2000)
        startTime = System.currentTimeMillis()
    }

    override fun showMaintenanceView(isShow: Boolean, content: String?) {
        showOnlyPassionMember(!isShow)
        if (isShow) {
            txt_maintenance_info?.visibility = View.VISIBLE
            txt_maintenance_info?.text = content
        } else {
            if (iv_only_passion_member?.visibility != View.VISIBLE) {
                iv_only_passion_member?.visibility = View.VISIBLE
            }
        }
    }

    private fun showOnlyPassionMember(isShow: Boolean) {
        if (isShow) {
            iv_only_passion_member?.visibility = View.VISIBLE
        } else {
            if (iv_only_passion_member?.visibility != View.INVISIBLE) {
                iv_only_passion_member?.visibility = View.INVISIBLE
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            StoreLogsUtils.PERMISSION_REQUEST_CODE -> {
                if (grantResults.size > 0 && permissions.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        initView(null)
                        doCreate()
                    } else {
                        pausePlayer()

                        val showRationale = shouldShowRequestPermissionRationale(permissions[0])
                        if (showRationale) {
                            txt_status?.text = getString(R.string.external_storage_deny)

                            retriedWriteExternalStoragePermission()
                        } else {
                            txt_status?.text = getString(R.string.external_storage_deny_retry)
                        }
                    }
                }
            }
        }
    }

    private val permissionSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            if (SystemClock.elapsedRealtime() - globalLastClickTime < CLICK_TIMER) {
                return
            }
            globalLastClickTime = SystemClock.elapsedRealtime()
            StoreLogsUtils.requestPermission(this@MainActivity)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = true
        }
    }

    private fun retriedWriteExternalStoragePermission() {
        val text = txt_status.text.toString()
        val span = GeneralTextUtil.underLineAndSetEventText(text, text.indexOf(getString(R.string.try_again)),
            (text.length - 1), ContextCompat.getColor(this, R.color.colorPrimary),
            permissionSpan)
        txt_status.text = span
        txt_status.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun finishUpgradeFirmware() {
        runOnUiThread {
            val kioskInfo = Gson().fromJson(MySharedPref(this).kioskInfo, KioskInfo::class.java)
            if (isFirst) {
                presenter?.getKioskInfo(MySharedPref(this).eKioskHeader, kioskInfo?.id)
                outlet = kioskInfo?.outlet
            }
            presenter?.refreshToken(expiry, kioskInfo)
        }
    }

    fun navigateSettingsView() {
        presenter?.navigateSettingsView()
    }

    fun getPeripheralManager(): PeripheralsService? {
        return presenter?.peripheralsService
    }

    fun enableHeader(isEnable: Boolean) {
        if (isEnable) {
            ll_top_bar?.visibility = View.VISIBLE
        } else {
            ll_top_bar?.visibility = View.GONE
        }
    }

    fun setBackgroundBottomBar(tag: String) {
        view_bg_home.setBackgroundResource(R.drawable.bg_bottom_default)
        view_bg_course.setBackgroundResource(R.drawable.bg_bottom_default)
        view_bg_event.setBackgroundResource(R.drawable.bg_bottom_default)
        view_bg_facility.setBackgroundResource(R.drawable.bg_bottom_default)
        view_bg_scan_course.setBackgroundResource(R.drawable.bg_bottom_default)
        view_bg_interest_group.setBackgroundResource(R.drawable.bg_bottom_default)
        when (tag) {
            MODE_HOME -> {
                view_bg_home.setBackgroundResource(R.drawable.bg_home_tab)
            }
            MODE_COURSES -> {
                view_bg_course.setBackgroundResource(R.drawable.bg_course_tab)
            }
            MODE_EVENTS -> {
                view_bg_event.setBackgroundResource(R.drawable.bg_gradient_event_vertical)
            }
            MODE_FACILITIES -> {
                view_bg_facility.setBackgroundResource(R.drawable.bg_gradient_facility_vertical)
            }
            MODE_SCAN_COURSES_EVENTS -> {
                view_bg_scan_course.setBackgroundResource(R.drawable.bg_scan_tab)
            }
            MODE_INTEREST_GROUPS -> {
                view_bg_interest_group.setBackgroundResource(R.drawable.bg_gradient_interestgroup)
            }
        }
    }

    fun setBackgroundLine(resource: Int?) {
        enableLine(true)
        if (resource == null) {
            view_line?.setBackgroundResource(R.drawable.bg_gradient_orange)
        } else {
            view_line?.setBackgroundResource(resource)
        }
    }

    fun enableLine(enable: Boolean) {
        if (enable) {
            view_line?.visibility = View.VISIBLE
        } else {
            view_line?.visibility = View.GONE
        }
    }

    fun showProgressPayment(isShow: Boolean) {
        if (isShow) {
            progress_payment?.visibility = View.VISIBLE
        } else {
            progress_payment?.visibility = View.GONE
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setProgressPayment(step: Int?) {
        resetStateProgressPayment()
        val enableColor = Color.WHITE
        val disableColor = ContextCompat.getColor(this, R.color.dark_grey_color)
        when (step) {
            PARTICIPANT -> {
                iv_participant?.setImageResource(R.drawable.bg_arrow_enable)
                iv_number_1?.setImageResource(R.drawable.ring_selected)
                tv_participant?.setTextColor(enableColor)
                tv_number_1?.setTextColor(enableColor)
            }
            SUMMARY -> {
                iv_summary?.setImageResource(R.drawable.bg_arrow_enable)
                iv_number_2?.setImageResource(R.drawable.ring_selected)
                tv_summary?.setTextColor(enableColor)
                tv_number_2?.setTextColor(enableColor)

                iv_number_1?.setImageResource(R.drawable.ring_enable)
                tv_number_1?.setTextColor(enableColor)
                tv_participant?.setTextColor(disableColor)
                iv_participant?.setImageResource(R.drawable.bg_arrow_disable)
            }
            PAYMENT -> {
                iv_payment?.setImageResource(R.drawable.bg_arrow_enable)
                iv_number_3?.setImageResource(R.drawable.ring_selected)
                tv_payment?.setTextColor(enableColor)
                tv_number_3?.setTextColor(enableColor)

                iv_number_1?.setImageResource(R.drawable.ring_enable)
                tv_number_1?.setTextColor(enableColor)
                tv_participant?.setTextColor(disableColor)
                iv_participant?.setImageResource(R.drawable.bg_arrow_disable)

                iv_number_2?.setImageResource(R.drawable.ring_enable)
                tv_number_2?.setTextColor(enableColor)
                tv_summary?.setTextColor(disableColor)
                iv_summary?.setImageResource(R.drawable.bg_arrow_disable)
            }
            CONFIRMATION -> {
                iv_confirm?.setImageResource(R.drawable.bg_arrow_enable)
                iv_number_4?.setImageResource(R.drawable.ring_selected)
                tv_confirmation?.setTextColor(enableColor)
                tv_number_4?.setTextColor(enableColor)

                iv_number_1?.setImageResource(R.drawable.ring_enable)
                tv_number_1?.setTextColor(enableColor)
                tv_participant?.setTextColor(disableColor)
                iv_participant?.setImageResource(R.drawable.bg_arrow_disable)

                iv_number_2?.setImageResource(R.drawable.ring_enable)
                tv_number_2?.setTextColor(enableColor)
                tv_summary?.setTextColor(disableColor)
                iv_summary?.setImageResource(R.drawable.bg_arrow_disable)

                iv_number_3?.setImageResource(R.drawable.ring_enable)
                tv_number_3?.setTextColor(enableColor)
                tv_payment?.setTextColor(disableColor)
                iv_payment?.setImageResource(R.drawable.bg_arrow_disable)
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun resetStateProgressPayment() {
        iv_participant.isEnabled = false
        iv_summary.isEnabled = false
        iv_payment.isEnabled = false
        iv_confirm.isEnabled = false

        iv_participant?.setImageResource(R.drawable.bg_arrow_disable)
        iv_number_1?.setImageResource(R.drawable.ring_disable)
        tv_participant?.setTextColor(R.color.dark_grey_color)
        tv_number_1?.setTextColor(R.color.bright_gray)

        iv_summary?.setImageResource(R.drawable.bg_arrow_disable)
        iv_number_2?.setImageResource(R.drawable.ring_disable)
        tv_summary?.setTextColor(R.color.dark_grey_color)
        tv_number_2?.setTextColor(R.color.bright_gray)

        iv_payment?.setImageResource(R.drawable.bg_arrow_disable)
        iv_number_3?.setImageResource(R.drawable.ring_disable)
        tv_payment?.setTextColor(R.color.dark_grey_color)
        tv_number_3?.setTextColor(R.color.bright_gray)

        iv_confirm?.setImageResource(R.drawable.bg_arrow_disable)
        iv_number_4?.setImageResource(R.drawable.ring_disable)
        tv_confirmation?.setTextColor(R.color.dark_grey_color)
        tv_number_4?.setTextColor(R.color.bright_gray)
    }

    fun showProgressParticipant() {
        showProgressPayment(true)
        setProgressPayment(PARTICIPANT)
        iv_participant.isEnabled = false
        iv_summary.isEnabled = false
    }

    fun showProgressSummary() {
        showProgressPayment(true)
        setProgressPayment(SUMMARY)
        iv_participant.isEnabled = true
        iv_summary.isEnabled = false
    }

    fun showProgressPayment() {
        setProgressPayment(PAYMENT)
    }

    fun showProgressConfirmation() {
        showProgressPayment(true)
        setProgressPayment(CONFIRMATION)
    }

    override fun getDataAppPath(): String {
        return getExternalFilesDir(null)?.absolutePath?: ""
    }

    override fun getPayer(): CustomerInfo? {
        return if (isQuickBook) {
            quickbookCart.payer
        } else {
            cart?.payer
        }

    }

    override fun updateCanOrder(isAlive: Boolean) {
        if (this.canOrder != isAlive) {
            this.canOrder = isAlive
            ll_cart.isEnabled = canOrder
            if (canOrder) {
                txt_total_item.setBackgroundResource(R.drawable.bg_red_oval)
            } else {
                txt_total_item.setBackgroundResource(R.drawable.bg_gray_oval)
            }
            val currentFragment = getCurrentObject()
            if (currentFragment is Base2Fragment) {
                currentFragment.setCanOrder(isAlive)
            }
        }
    }
}
