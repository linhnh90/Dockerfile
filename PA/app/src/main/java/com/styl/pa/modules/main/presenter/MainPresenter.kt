package com.styl.pa.modules.main.presenter

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Base64
import android.webkit.URLUtil
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.styl.coresdk.OtaManager
import com.styl.kioskcore.SystemManager
import com.styl.pa.BuildConfig
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.R
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.FirmwareFile
import com.styl.pa.entities.GeneralException
import com.styl.pa.entities.InitData
import com.styl.pa.entities.api.API
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.generateToken.*
import com.styl.pa.entities.healthDevice.HealthDeviceRequest
import com.styl.pa.entities.healthDevice.InfoHealthDevice
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.entities.journey.JourneyRequest
import com.styl.pa.entities.keepAlive.KeepAliveResponse
import com.styl.pa.entities.kioskactivation.KioskInfo
import com.styl.pa.entities.log.LastScreenLogRequest
import com.styl.pa.entities.main.HelpResponse
import com.styl.pa.entities.main.KioskAuthenticationRequest
import com.styl.pa.entities.main.KioskAuthenticationResponse
import com.styl.pa.entities.main.KioskUpdateRequest
import com.styl.pa.entities.ota.*
import com.styl.pa.entities.pacesRequest.ProductRequest
import com.styl.pa.entities.pacesRequest.ProductRequestItem
import com.styl.pa.entities.product.ProductListResponse
import com.styl.pa.entities.proxy.EmptyRequest
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.entities.proxy.ProxyRequestHeader
import com.styl.pa.entities.screensaver.ScreensaverRequest
import com.styl.pa.entities.screensaver.ScreensaverResponse
import com.styl.pa.entities.search.SearchLocationRequest
import com.styl.pa.enums.BookingType
import com.styl.pa.enums.OtaError
import com.styl.pa.interfaces.DownloadFilesListener
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.home.interactor.HomeInteractor
import com.styl.pa.modules.main.IMainContract
import com.styl.pa.modules.main.interactor.MainInteractor
import com.styl.pa.modules.main.router.MainRouter
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.main.view.MainActivity.Companion.ACTION_TERMINAL_DAILY
import com.styl.pa.modules.main.view.MainActivity.Companion.TERMINAL_DAILY_CODE
import com.styl.pa.modules.peripheralsManager.peripheralsService.PeripheralsInfo
import com.styl.pa.modules.peripheralsManager.peripheralsService.PeripheralsService
import com.styl.pa.modules.peripheralsManager.terminalManager.IPaymentResultListener
import com.styl.pa.modules.peripheralsManager.terminalManager.TerminalDailyUtils
import com.styl.pa.modules.printer.IPrinterFontConfig
import com.styl.pa.modules.printer.customPrinterService.HandlePrintStatus
import com.styl.pa.modules.printer.customPrinterService.PrinterErrorType.Companion.VALID_CODE
import com.styl.pa.modules.scanner.zebraScannerService.DcssdkListener
import com.styl.pa.modules.vacancyChecking.presenter.VacancyCheckingPresenter
import com.styl.pa.receiver.TerminalDailyReceiver
import com.styl.pa.serverlocal.WebServerService
import com.styl.pa.utils.*
import com.zebra.scannercontrol.DCSScannerInfo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException
import org.jetbrains.annotations.TestOnly
import java.io.File
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by trangpham on 9/3/2018
 */
class MainPresenter(var view: IMainContract.IView?) : IMainContract.IPresenter, IMainContract.IPeripheralPresenter,
        IMainContract.IInteractorOutput,
        DcssdkListener.DcssdkInit, VacancyCheckingPresenter() {

    @TestOnly
    constructor(view: IMainContract.IView?, interactor: MainInteractor) : this(view) {
        this.interactor = interactor
    }

    companion object {
        private val TAG = MainPresenter::class.java.simpleName

        const val DEFAULT_EXPIRY = 2L

        private const val READ_CONFIG = 0
        private const val READ_FIRMWARE_PACKAGE = 1
        private const val UPGRADING_SYSTEM = 2
        private const val UPGRADING_APPLICATION = 3
        private const val UPGRADING_ANTI_VIRUS = 4
        const val FIRMWARE_FOLDER = "firmware"
        private const val UNZIP_FIRMWARE_FOLDER = "ota_unzip"
        const val FIRMWARE_FILE_NAME = "firmware.zip"
        const val RAW_FIRMWARE_FILE_NAME = "rawFirmware.zip"
        private const val FOLDER_DB_ANTIVIRUS = "database"
        private const val CONFIG_FILE_NAME = "configurations.zip"
        const val SCREENSAVER_PATH = "Screensaver"

        const val COURSE_CATEGORY_TYPE = "course"
        const val EVENT_CATEGORY_TYPE = "event"
    }

    private var interactor: MainInteractor = MainInteractor()

    private var homeInteractor: HomeInteractor? = HomeInteractor(null, view?.getContext())

    init {
        initVacancyPresenter(view, view?.getContext(), homeInteractor)
    }


    private var router: MainRouter? = MainRouter(view as? AppCompatActivity)

    private var isFirst = true
    private val EVENT_TYPE = "event"
    private val CLASS_TYPE = "class"
    private val COURSES_TYPE = "courses"
    private val INTEREST_GROUPS_TYPE = "interest-groups"
    private var upgradeState: Int? = null
    private var configFw: ConfigFirmware? = null
    private var otaManager: OtaManager? = null
    private var unzipFirmwarePath: String? = null
    private var otaDownloadManager: OtaDownloadManager? = null
    private var downloadedFirmwareFile: FirmwareFile? = null

    private var downloadFirmwarePath = ""
    private var rawFirmwareFilePath = ""

    private var clientPrivateKey: PrivateKey? = null

    private var reportOTAStatusSuccessFlag = false

    private val output2 = object : IMainContract.IInteractorOutput2 {

        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: KioskAuthenticationResponse?) {
            if (!data?.token.isNullOrEmpty()) {
                view?.onAuthenticationSuccess(data?.token, data?.expiry, isFirst)
            }
            isFirst = false
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<KioskAuthenticationResponse>) {
            if (data.errorCode == -1) {
                view?.updateStatus(R.string.starting_kiosk, true)
            } else {
                view?.updateStatus(R.string.authenticating_fail, false)
            }

            if (BaseResponse.UNAUTHORIZED_CODE == data.errorCode) {
                view?.showActivationScreen()
            } else {
                view?.onAuthenticationFailed()
            }
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onKioskInfoUpdated(kioskInfo: KioskInfo?) {
            authenticateKiosk(kioskInfo?.accessKey, kioskInfo?.secretKey)
        }
    }

    private val output3 = object : IMainContract.IInteractorOutput3 {

        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: KioskInfo?) {
            if (KioskInfo.STATUS_REACTIVATED == data?.status) {
                view?.updateStatus(R.string.get_kiosk_info_fail, false)
                view?.showActivationScreen()
            }
            if (data != null) {
                val info = Gson().toJson(data, KioskInfo::class.java)
                view?.onGetInfoSuccess(info)

                rebootKioskDaily()
            }
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<KioskInfo>) {
            view?.updateStatus(R.string.get_kiosk_info_fail, false)
        }
    }

    private val output4 = object : IMainContract.IInteractorOutput4 {

        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: HelpResponse?) {
            view?.dismissLoading()
            router?.navigateHelpView(data?.content)
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<HelpResponse>) {
            view?.dismissLoading()
            view?.showErrorMessage(data)
        }
    }

    init {
        interactor.output2 = output2
        interactor.output3 = output3
        interactor.output4 = output4
    }

    @ExcludeFromJacocoGeneratedReport
    override fun isScanner(device: UsbDevice?): Boolean {
        if (device == null) {
            return false
        }
        return peripheralsService?.peripheralsSupport?.isScanner(device) != null
    }

    @ExcludeFromJacocoGeneratedReport
    override fun isPrinter(device: UsbDevice?): Boolean {
        if (device == null) {
            return false
        }
        return peripheralsService?.peripheralsSupport?.isPrinter(device) != null
    }

    @ExcludeFromJacocoGeneratedReport
    override fun isTerminal(device: UsbDevice?): Boolean {
        if (device == null) {
            return false
        }
        return peripheralsService?.peripheralsSupport?.isTerminal(device) != null
    }

    override fun subscribeKioskInfo(subject: BehaviorSubject<String>) {
        interactor.subscribeKioskInfo(subject)
    }

    override fun authenticateKiosk(accessKey: String?, secretKey: String?) {
        val request = KioskAuthenticationRequest(accessKey, secretKey)
        view?.updateStatus(R.string.authenticating, true)
        interactor.authenticateKiosk(request)
    }

    override fun updateKioskInfo(token: String?, id: String?, version: String?) {
        val request = KioskUpdateRequest(id, version, getAppVersion(), getOs())
        interactor.updateKioskInfo(token, request)
    }

    override fun getKioskInfo(token: String?, kioskId: Int?) {
        view?.updateStatus(R.string.get_kiosk_info, true)
        interactor.getKioskInfo(token, kioskId)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun refreshToken(expiry: Int?, kioskInfo: KioskInfo?) {
        val request = KioskAuthenticationRequest(kioskInfo?.accessKey, kioskInfo?.secretKey)
        val count = expiry?.toLong() ?: DEFAULT_EXPIRY
        interactor.refreshToken(view?.getContext(), count, request)
    }

    override fun help(token: String?) {
        view?.showLoading()

        interactor.help(token)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        interactor.onDestroy()
        view = null
        router = null
        compositeRebootKioskDaily.clear()
        compositeRebootKioskDaily.dispose()
        otaCompositeDisposable.clear()
        otaCompositeDisposable.dispose()
        scannerCompositeDisposable.clear()
        scannerCompositeDisposable.dispose()
        otaManager?.destroy()
        destroyRangeTimeHealthStatusUtils()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationHomeView() {
        router?.navigationHomeView()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationAttendCoursesView(location: Outlet?,
                                             classList: ArrayList<ClassInfo>?) {
        router?.navigationAttendCoursesView(location, classList)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationBookFacilitiesView(location: Outlet?,
                                              classList: ArrayList<ClassInfo>?) {
        router?.navigationBookFacilitiesView(location, classList)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationParticipateEvent(location: Outlet?,
                                            classList: ArrayList<ClassInfo>?) {
        router?.navigationParticipateEvent(location, classList)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationInterestGroupsView(location: Outlet?, classList: ArrayList<ClassInfo>?) {
        router?.navigationInterestGroupsView(location,classList)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationAdvancedSearchEvent() {
        router?.navigationAdvancedSearchEvent()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationScanQRView() {
        router?.navigationScanQRView(view)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationCommingSoonPage() {
        router?.navigationComingSoonPage()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationCardView() {
        router?.navigationCardView()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationCartView(isBookingMyself: Boolean) {
        router?.navigationCartView(isBookingMyself)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationEmptyCardView() {
        router?.navigationEmptyCardView()
    }


    // Peripherals manage
    var peripheralsService: PeripheralsService? = null

    @ExcludeFromJacocoGeneratedReport
    override fun initUsbDevices() {
        if (peripheralsService == null) {
            peripheralsService = PeripheralsService(this)
        }
        peripheralsService?.detectUsbPeripherals(view?.getContext())
    }

    @ExcludeFromJacocoGeneratedReport
    override fun getContext(): Context? {
        return view?.getContext()
    }


    //implement with scanner
    private var isAppearedScanner = false

    @ExcludeFromJacocoGeneratedReport
    override fun getScannerCallback(): DcssdkListener.DcssdkInit? {
        return this
    }

    @ExcludeFromJacocoGeneratedReport
    override fun initScannerManager(peripheralsInfo: PeripheralsInfo) {
        isAppearedScanner = false
        Handler().postDelayed({
            if (!isAppearedScanner) {
                val deviceInfo = InfoHealthDevice(InfoHealthDevice.SCANNER_NAME, InfoHealthDevice.SCANNER_DISCONNECT)
                deviceHealthUpdate(HealthDeviceRequest(deviceInfo))
            }
        }, 500)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun initScannerManager(usbDevice: UsbDevice) {
        peripheralsService?.initScannerService(usbDevice)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun pullTrigger(isPull: Boolean) {
        val disposable = Observable.fromCallable {
            peripheralsService?.pullTrigger(isPull)
            return@fromCallable
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy ( onError = {
                LogManager.i("Trigger scanner ERROR")
            })
        scannerCompositeDisposable.add(disposable)

    }

    @ExcludeFromJacocoGeneratedReport
    override fun setConfigEvent(listener: DcssdkListener.DcssdkConfig) {
        peripheralsService?.setConfigEvent(listener)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun detachScannerUsb() {
        peripheralsService?.detachScannerUsb()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun detachPrinterUsb() {
        val deviceInfo = InfoHealthDevice(InfoHealthDevice.PRINTER_NAME, InfoHealthDevice.PRINTER_DISCONNECT)
        deviceHealthUpdate(HealthDeviceRequest(deviceInfo))
    }

    @ExcludeFromJacocoGeneratedReport
    override fun disconnectScanner() {
        peripheralsService?.disconnectScanner()
    }


    //override DcssdkListener.DcssdkInit
    @ExcludeFromJacocoGeneratedReport
    override fun dcssdkAppeareEvent(usbDevice: DCSScannerInfo) {
        isAppearedScanner = true
    }

    @ExcludeFromJacocoGeneratedReport
    override fun dcssdkDisappeareEvent() {
        view?.setScannerResult(false)

        val deviceInfo = InfoHealthDevice(InfoHealthDevice.SCANNER_NAME, InfoHealthDevice.SCANNER_DISCONNECT)
        deviceHealthUpdate(HealthDeviceRequest(deviceInfo))
    }

    @ExcludeFromJacocoGeneratedReport
    override fun dcssdkConnectEvent(result: Boolean) {
        view?.setScannerResult(result)

        val deviceInfo = InfoHealthDevice(InfoHealthDevice.SCANNER_NAME, InfoHealthDevice.SCANNER_CONNECT)
        deviceHealthUpdate(HealthDeviceRequest(deviceInfo))
    }

    @ExcludeFromJacocoGeneratedReport
    override fun dcssdkDisconnectEvent(result: Boolean) {
        LogManager.d(TAG, "DcssdkDisconnectEvent")
    }

    override fun notFoundScannerUsb() {
        view?.setScannerResult(false)

        val deviceInfo = InfoHealthDevice(InfoHealthDevice.SCANNER_NAME, InfoHealthDevice.SCANNER_DISCONNECT)
        deviceHealthUpdate(HealthDeviceRequest(deviceInfo))
    }

    //implement with printer
    var printerStatus: Int = InfoHealthDevice.PRINTER_DISCONNECT
        get() = field
        set(value) {
            field = value
        }

    @ExcludeFromJacocoGeneratedReport
    override fun getPrinterFontConfig(): IPrinterFontConfig? {
        return peripheralsService?.getPrinterFontConfig()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun initPrinterManager(peripheralsInfo: PeripheralsInfo, printerStatus: GeneralException?) {
        if (VALID_CODE != printerStatus?.code) {
            view?.setPrinterResult(false)
            this.printerStatus = InfoHealthDevice.PRINTER_DISCONNECT
        } else {
            view?.setPrinterResult(true)
            this.printerStatus = InfoHealthDevice.PRINTER_CONNECT
        }
        val deviceInfo = InfoHealthDevice(InfoHealthDevice.PRINTER_NAME, this.printerStatus)
        deviceHealthUpdate(HealthDeviceRequest(deviceInfo))
    }

    @ExcludeFromJacocoGeneratedReport
    override fun initPrinterManager(usbDevice: UsbDevice) {
        peripheralsService?.initPrinterService(usbDevice)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun setHandlePrinterStatusEvent(event: HandlePrintStatus) {
        peripheralsService?.setHandlePrinterStatusEvent(event)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun printText(text: String, printerFont: Int?, fontSize: Int?,
                           isBold: Boolean, isItalic: Boolean,
                           isUnderline: Boolean): GeneralException? {
        return peripheralsService?.printText(text, printerFont, fontSize, isBold,
                isItalic, isUnderline)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun printImage(bitmap: Bitmap?) {
        peripheralsService?.printImage(bitmap)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun cutPage() {
        peripheralsService?.cutPage()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun printFeed() {
        peripheralsService?.printFeed()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun checkStatusPrinter(): GeneralException? {
        return peripheralsService?.checkStatusPrinter()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun startCheckStatusWhenPrint() {
        peripheralsService?.startCheckStatusWhenPrint()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun endCheckStatusWhenPrint() {
        peripheralsService?.endCheckStatusWhenPrint()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun disconnectPrinter() {
        peripheralsService?.disconnectPrinter()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun disconnectPrinterManager() {
        view?.setPrinterResult(false)

        val deviceInfo = InfoHealthDevice(InfoHealthDevice.PRINTER_NAME, InfoHealthDevice.PRINTER_DISCONNECT)
        deviceHealthUpdate(HealthDeviceRequest(deviceInfo))
    }


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    @ExcludeFromJacocoGeneratedReport
    fun getOs(): String? {
        return Build.VERSION.RELEASE
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    @ExcludeFromJacocoGeneratedReport
    fun getAppVersion(): String {
        return BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")"
    }

    @ExcludeFromJacocoGeneratedReport
    fun addBackStackChanged(activity: FragmentActivity?) {
        activity?.supportFragmentManager?.addOnBackStackChangedListener {
            val f: Fragment? = activity.supportFragmentManager.findFragmentById(R.id.container)
            f?.onResume()
        }
    }

    override fun aliveTracking() {
        if (!view?.getToken().isNullOrEmpty()) {
            interactor.aliveTracking(view!!.getToken(), EmptyRequest(), aliveTrackingOutput)
        }
    }

    override fun lastScreenLog(request: LastScreenLogRequest) {
        if (!view?.getToken().isNullOrEmpty()) {
            interactor.lastScreenLog(view!!.getToken(), request)
        }
    }

    override fun deviceHealthUpdate(request: HealthDeviceRequest) {
        if (!checkTimeRangeReportHealthStatus(request)) {
            return
        }
        if (!view?.getToken().isNullOrEmpty()) {
            interactor.deviceHealthUpdate(view!!.getToken(), request)
        }
    }


    // implement terminal
    @ExcludeFromJacocoGeneratedReport
    override fun initTerminalManager(peripheralsInfo: PeripheralsInfo) {
        // Do nothing
        LogManager.d(TAG, "initTerminalManager")
        return
    }

    // manage terminal
    @ExcludeFromJacocoGeneratedReport
    override fun initTerminalManagerResult(isConnected: Boolean) {
        val status = if (isConnected) InfoHealthDevice.TERMINAL_CONNECT else InfoHealthDevice.TERMINAL_DISCONNECT

        val deviceInfo = InfoHealthDevice(InfoHealthDevice.TERMINAL_NAME, status)
        deviceHealthUpdate(HealthDeviceRequest(deviceInfo))
    }

    @ExcludeFromJacocoGeneratedReport
    override fun initTerminalManager(usbDevice: UsbDevice) {
        peripheralsService?.initTerminalService(usbDevice)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun setPaymentCallbacks(listener: IPaymentResultListener?) {
        peripheralsService?.setPaymentCallbacks(listener)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun payProduct(cardType: Int, amount: Int): Boolean {
        return peripheralsService?.payProduct(cardType, amount) ?: false
    }

    override fun printerNotFound() {
        disconnectPrinterManager()
    }

    override fun scannerNotFound() {
        notFoundScannerUsb()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun detachTerminalUsb() {
        peripheralsService?.detachTerminalUsb()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun disconnectTerminal() {
        peripheralsService?.disconnectTerminalService()
    }


    @ExcludeFromJacocoGeneratedReport
    override fun reportDailyHealth() {
        var status = 0
        val infoArray = java.util.ArrayList<InfoHealthDevice>()
        var result: GeneralException? = null
        val isConnectedScanner = view?.isConnectScanner()
        val isConnectedPrinter = view?.isConnectPrinter()

        if (isConnectedPrinter == true) {
            result = peripheralsService?.checkStatusPaper()

            if (result == null || result.code == 0) {
                result = GeneralException(InfoHealthDevice.PRINTER_CONNECT, "")
            }
        } else {
            result = GeneralException(InfoHealthDevice.PRINTER_DISCONNECT, "")
        }
        infoArray.add(InfoHealthDevice(InfoHealthDevice.PRINTER_NAME, result.code))

        if (isConnectedScanner == true) {
            status = InfoHealthDevice.SCANNER_CONNECT
        } else {
            status = InfoHealthDevice.SCANNER_DISCONNECT
        }
        infoArray.add(InfoHealthDevice(InfoHealthDevice.SCANNER_NAME, status))

        status = 0
        if (true == peripheralsService?.isTerminalCommunicated()) {
            status = InfoHealthDevice.TERMINAL_CONNECT
        } else {
            status = InfoHealthDevice.TERMINAL_DISCONNECT
        }
        infoArray.add(InfoHealthDevice(InfoHealthDevice.TERMINAL_NAME, status))

        val request = HealthDeviceRequest(infoArray)
        deviceHealthUpdate(request)
    }

    private var pageIndexLocation = 1
    private var pageSizeLocation = 300

    fun sortLocationByName(lst: ArrayList<Outlet>?): ArrayList<Outlet> {
        val list = ArrayList<Outlet>()
        if (lst != null) {
            list.addAll(lst.sortedWith(compareBy { it.getFriendlyName() }).toList())
        }
        return list
    }

    fun sortNeighbourhoods(lst: ArrayList<Neighbourhood>): ArrayList<Neighbourhood> {
        val list = ArrayList<Neighbourhood>()
        list.addAll(lst.sortedWith(compareBy { it.title }).toList())
        return list
    }

    fun getLocationAndCategories(context: Context, outletTypeName: String?) {
        view?.showLoading()
        val locationRequest = SearchLocationRequest("", pageIndexLocation, pageSizeLocation)
        val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
        val categoryRequest = ProxyRequest(header, EmptyRequest(), ProxyRequest.GET_METHOD, "",
                API.getCategories(COURSE_CATEGORY_TYPE, ""), true)
        val token = MySharedPref(context).eKioskHeader

        val categoryEventRequest = ProxyRequest(header, EmptyRequest(), ProxyRequest.GET_METHOD, "",
                API.getCategories(EVENT_CATEGORY_TYPE, ""), true)

        view?.updateStatus(R.string.get_data, true)
        interactor.getLocationAndCategories(context, token, locationRequest, categoryRequest, categoryEventRequest, initDataCallback)
    }

    private var initDataCallback = object : IBaseContract.IBaseInteractorOutput<InitData> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: InitData?) {
            if (data?.pageByOutletDetail != null) {
                view?.onLocationByOutletSuccess(data.pageByOutletDetail)
                view?.onCategorySuccess(data.courseCategoryList)
                view?.onCategoryEventSuccess(data.eventCategoryList)
            } else {
                updateErrorStatus(R.string.get_locations_fail)
            }
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<InitData>) {
            updateErrorStatus(R.string.get_data_fail)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun updateErrorStatus(source: Int) {
        view?.dismissLoading()
        view?.updateStatus(source, false)
    }

    private var allClassInfoCallback = object : IBaseContract.IBaseInteractorOutput<ProductListResponse> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: ProductListResponse?) {
            view?.onGetAllClassInfoSuccess(data)
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<ProductListResponse>) {
            view?.showErrorMessage(data)
        }

    }

    private var allEventInfoCallback = object : IBaseContract.IBaseInteractorOutput<ProductListResponse> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: ProductListResponse?) {
            view?.onGetAllEventInfoSuccess(data)
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<ProductListResponse>) {
            view?.showErrorMessage(data)
        }

    }

    private var allIGCallback = object : IBaseContract.IBaseInteractorOutput<ProductListResponse> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: ProductListResponse?) {
            view?.onGetAllIGSuccess(data)
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<ProductListResponse>) {
            view?.showErrorMessage(data)
        }

    }

    @ExcludeFromJacocoGeneratedReport
    fun getAllProductInfo(url: String?, context: Context) {
        val pageIndex = 1
        val pageSize = 10

        var data = url?.lowercase(Locale.ENGLISH)
        if (!TextUtils.isEmpty(data) && URLUtil.isValidUrl(data)) {
            if (data!!.endsWith("/")) {
                data = data.substring(0, data.length - 1)
            }

            val index = data.lastIndexOf("/")
            if (index != -1) {
                val code = data.substring(data.lastIndexOf("/") + 1).uppercase(Locale.ENGLISH)
                if (data.contains(EVENT_TYPE)) {
                    view?.showLoadingDialog()
                    interactor.getAllEventInfo(
                        context,
                        code,
                        pageIndex,
                        pageSize,
                        allEventInfoCallback
                    )
                } else if (data.contains(CLASS_TYPE) || data.contains(COURSES_TYPE)) {
                    view?.showLoadingDialog()
                    interactor.getAllClassInfo(
                        context,
                        code,
                        pageIndex,
                        pageSize,
                        allClassInfoCallback
                    )
                } else if(data.contains(INTEREST_GROUPS_TYPE)){
                    view?.showLoadingDialog()
                    //interactor getallInterestGroups
                    interactor.getAllIG(
                        context,
                        code,
                        pageIndex,
                        pageSize,
                        allIGCallback
                    )
                } else {
                    view?.showErrorMessageAndTitle(
                        R.string.scan_qr_code_valid,
                        R.string.invalid_qrcode
                    )
                }
            } else {
                if (data.contains(EVENT_TYPE)) {
                    view?.showErrorMessageAndTitle(R.string.scan_qr_code_valid_for_event, R.string.invalid_qrcode)
                } else if (data.contains(CLASS_TYPE) || data.contains(COURSES_TYPE)) {
                    view?.showErrorMessageAndTitle(R.string.scan_qr_code_valid_for_class, R.string.invalid_qrcode)
                } else if (data.contains(INTEREST_GROUPS_TYPE)) {
                    view?.showErrorMessageAndTitle(R.string.scan_qr_code_valid_for_ig, R.string.invalid_qrcode)
                } else {
                    view?.showErrorMessageAndTitle(R.string.scan_qr_code_valid, R.string.invalid_qrcode)
                }
            }
        } else {
            view?.showErrorMessageAndTitle(R.string.scan_qr_code_valid, R.string.invalid_qrcode)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun goToClassDetails(classInfo: ClassInfo?) {
        router?.navigationCourseDetailPage(classInfo)
    }

    @ExcludeFromJacocoGeneratedReport
    fun goToEventDetails(eventInfo: EventInfo) {
        router?.navigateEventDetailPage(eventInfo)
    }

    @ExcludeFromJacocoGeneratedReport
    fun goToIgDetails(igInfo: InterestGroup) {
        router?.navigateIgDetailPage(igInfo)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigateSettingsView() {
        router?.navigateSettingsView()
    }

    override fun trackUserJourney(request: JourneyRequest?) {
        val token = view?.getToken()
        if (!token.isNullOrEmpty()) {
            interactor.trackUserJourney(token, request, null)
        }
    }

    override fun calculateOtaTime(): Array<Int>? {
        val kioskInfo = Gson().fromJson(getSharePreference().kioskInfo, KioskInfo::class.java)
        if (kioskInfo?.id == null) {
            return null
        }

        val otaNumberOrder = kioskInfo.id!!.rem(MainActivity.NUMBER_KIOSK)

        val numberKioskPerHour = MainActivity.NUMBER_KIOSK / 2
        val numberKioskPer15Minutes = numberKioskPerHour / 4

        var hour = 1
        var n = 0
        if (otaNumberOrder < numberKioskPerHour) {
            hour = 1
        } else {
            hour = 2
        }

        when (otaNumberOrder) {
            in 0..numberKioskPer15Minutes,
            in numberKioskPerHour..numberKioskPerHour + numberKioskPer15Minutes -> {
                n = 3
            }
            in numberKioskPer15Minutes + 1..2 * numberKioskPer15Minutes,
            in numberKioskPerHour + 1 + numberKioskPer15Minutes..numberKioskPerHour + 2 * numberKioskPer15Minutes -> {
                n = 2
            }
            in (2 * numberKioskPer15Minutes) + 1..3 * numberKioskPer15Minutes,
            in numberKioskPerHour + 1 + (2 * numberKioskPer15Minutes)..numberKioskPerHour + 3 * numberKioskPer15Minutes -> {
                n = 1
            }
            else -> {
                n = 0
            }
        }

        if (n < 2) {
            hour += 1
        }

        val minute = n * 15

        return arrayOf(hour, minute)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun calculateWaitingTime(hour: Int, minute: Int, second: Int, takeNextTime: Boolean): Long {
        val c = Calendar.getInstance()
        val specificTime = c.clone() as Calendar
        specificTime.set(Calendar.HOUR_OF_DAY, hour)
        specificTime.set(Calendar.MINUTE, minute)
        specificTime.set(Calendar.SECOND, second)
        specificTime.set(Calendar.MILLISECOND, 0)
        val startTime = specificTime.time.time

        specificTime.set(Calendar.MILLISECOND, 999)
        val endTime = specificTime.time.time

        val currentTime = c.time.time

        var waitTime = 0L

        if (currentTime < startTime) {
            waitTime = startTime - currentTime
        } else if (currentTime > endTime) {
            if (takeNextTime) {
                specificTime.add(Calendar.DAY_OF_MONTH, 1)
                specificTime.set(Calendar.MILLISECOND, 0)

                waitTime = specificTime.time.time - currentTime
            } else {
                waitTime = -1
            }
        }
        return waitTime
    }

    private var compositeRebootKioskDaily = CompositeDisposable()
    private var otaCompositeDisposable = CompositeDisposable()
    private var scannerCompositeDisposable = CompositeDisposable()

    @ExcludeFromJacocoGeneratedReport
    override fun rebootKioskDaily() {
        compositeRebootKioskDaily.clear()
        val kioskInfo = Gson().fromJson(MySharedPref(view?.getContext()).kioskInfo, KioskInfo::class.java)
        var rebootTime: String? = null
        if (kioskInfo != null) {
            rebootTime = kioskInfo.outlet?.rebootTime
        }
        var hour = 1
        var minute = 0
        val second = 0
        if (!rebootTime.isNullOrEmpty()) {
            try {
                val d = SimpleDateFormat("hh:mm", Locale.ENGLISH).parse(rebootTime)
                val c = Calendar.getInstance()
                c.time = d

                hour = c.get(Calendar.HOUR_OF_DAY)
                minute = c.get(Calendar.MINUTE)
            } catch (e: Exception) {
                LogManager.i("Format reboot time failed")
            }
        } else {
            return
        }
        val waitTime = calculateWaitingTime(hour, minute, second, true)
        val disposable = Observable.timer(waitTime, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterNext {
                    rebootKiosk(view?.getContext())
                }
                .concatMap { t: Long -> Observable.interval(MainActivity.INTERVAL_HEALTH_TIME, TimeUnit.DAYS) }
                .subscribeBy(
                        onError = {},
                        onNext = {
                            rebootKiosk(view?.getContext())
                        }
                )
        compositeRebootKioskDaily.add(disposable)
    }

    @ExcludeFromJacocoGeneratedReport
    fun rebootForOTA() {
        LogManager.i("Reboot for OTA update!!!")
        rebootKiosk(view?.getContext())
    }

    @ExcludeFromJacocoGeneratedReport
    private fun rebootKiosk(context: Context?) {
        if (context != null) {
            val systemManager = SystemManager.init(context)
            Handler().postDelayed({
                systemManager.reboot(null)
            }, 500)
        }
    }

    private val aliveTrackingOutput = object : IBaseContract.IBaseInteractorOutput<KeepAliveResponse> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: KeepAliveResponse?) {
            if (data != null) {

                val isAlive = data.health?.status?.equals("ok", true) ?: true
                view?.updateCanOrder(isAlive)

                if (data.maintenance != null) {
                    val maintenance = data.maintenance
                    if (maintenance.startTime != null && maintenance.endTime != null) {
                        showMaintenanceView(maintenance.startTime!!, maintenance.endTime!!)
                        return
                    }
                }

                hideMaintenanceView()
            }
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<KeepAliveResponse>) {
            LogManager.d(TAG, "onError on Ping Kiosk")
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun showMaintenanceView(startTime: Long, endTime: Long) {
        if (startTimeMaintenance != startTime || endTimeMaintenance != endTime) {
            startTimeMaintenance = startTime
            endTimeMaintenance = endTime

            getMaintenanceString(startTime, endTime)

        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun hideMaintenanceView() {
        view?.showMaintenanceView(false, null)
    }

    private var startTimeMaintenance: Long = 0
    private var endTimeMaintenance: Long = 0

    @ExcludeFromJacocoGeneratedReport
    private fun getMaintenanceString(startTime: Long, endTime: Long) {
        if (endTime <= startTime) {
            hideMaintenanceView()
            return
        }

        val formatDay = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
        val formatTime = SimpleDateFormat("hh.mma", Locale.ENGLISH)

        val calendarStart = Calendar.getInstance()
        val calendarEnd = Calendar.getInstance()
        calendarStart.timeInMillis = startTime * 1000
        calendarEnd.timeInMillis = endTime * 1000

        var maintenanceString: String? = null

        if (formatDay.format(calendarStart.time).equals(formatDay.format(calendarEnd.time))) {
            maintenanceString = view?.getContext()?.getString(R.string.maintenance_info_one_day,
                    formatDay.format(calendarStart.time),
                formatTime.format(calendarStart.time).lowercase(Locale.ENGLISH),
                formatTime.format(calendarEnd.time).lowercase(Locale.ENGLISH)
            )
        } else {
            maintenanceString = view?.getContext()?.getString(R.string.maintenance_info_multi_days,
                    formatDay.format(calendarStart.time) + " " +
                            formatTime.format(calendarStart.time).lowercase(Locale.ENGLISH),
                    formatDay.format(calendarEnd.time) + " " +
                            formatTime.format(calendarEnd.time).lowercase(Locale.ENGLISH)
            )
        }

        view?.showMaintenanceView(true, maintenanceString)

        val c = Calendar.getInstance()
        val waitTime = calendarEnd.timeInMillis - c.timeInMillis
        if (waitTime > 0) {
            Handler().postDelayed({
                hideMaintenanceView()
            }, waitTime)
        } else {
            hideMaintenanceView()
        }
    }

    private var rangeTimeHealthStatusUtils: RangeTimeHealthStatusUtils? = null

    @ExcludeFromJacocoGeneratedReport
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun checkTimeRangeReportHealthStatus(request: HealthDeviceRequest): Boolean {
        val c = Calendar.getInstance()

        c.set(Calendar.HOUR_OF_DAY, RangeTimeHealthStatusUtils.startTime)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)

        val startTime = c.timeInMillis / 1000


        c.set(Calendar.HOUR_OF_DAY, RangeTimeHealthStatusUtils.endTime)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)

        val endTime = c.timeInMillis / 1000

        val currentTime = System.currentTimeMillis() / 1000
        if (currentTime <= startTime && currentTime >= endTime) {
            return true
        } else {
            if (rangeTimeHealthStatusUtils == null) {
                rangeTimeHealthStatusUtils = RangeTimeHealthStatusUtils(this)
            }
            rangeTimeHealthStatusUtils?.checkHealthStatusWaitingService(request)
            return false
        }
    }

    override fun destroyRangeTimeHealthStatusUtils() {
        rangeTimeHealthStatusUtils?.destroyHealthStatusWaitingService()
        rangeTimeHealthStatusUtils = null
    }

    @ExcludeFromJacocoGeneratedReport
    override fun getOtaConfig() {
        view?.updateStatus(R.string.checking_software_update, true)

        upgradeState = READ_CONFIG
        view?.getContext()?.let {
            otaManager = OtaManager.init(it)
        }
        downloadFirmwarePath = view?.getDataAppPath() + "/$FIRMWARE_FOLDER/"
        unzipFirmwarePath = "$downloadFirmwarePath/$UNZIP_FIRMWARE_FOLDER"
        rawFirmwareFilePath = "$downloadFirmwarePath/$RAW_FIRMWARE_FILE_NAME"

        getOtaCredentials()
    }

    @ExcludeFromJacocoGeneratedReport
    private fun verifyAndInstallOtaPackage(file: File) {
        if (configFw?.checksum?.isEmpty() == false) {
            if (GeneralUtils.getSHA256File(file).equals(configFw?.checksum, true)) {
                try {
                    val zipFile = ZipFile(file)
                    val headers = zipFile.fileHeaders
                    if (headers.size != 1) {
                        view?.updateStatus(OtaError.PARSE_PACKAGE_FAIL.errorMessage, false)
                        reportOtaStatus(OtaError.PARSE_PACKAGE_FAIL)
                        return
                    }

                    val rsaPrivateKey = clientPrivateKey!!

                    downloadFirmwarePath = view?.getDataAppPath() + "/$FIRMWARE_FOLDER/"
                    if (!File(downloadFirmwarePath).exists() && GeneralUtils.mkdirs(File(downloadFirmwarePath))) {
                        LogManager.i("Created firmware file path")
                    }
                    zipFile.extractFile(headers[0].fileName, downloadFirmwarePath, "firmware.bin")
                    downloadedFirmwareFile = FirmwareFile(view?.getContext())
                    downloadedFirmwareFile?.parse(File("$downloadFirmwarePath/firmware.bin"), rsaPrivateKey)


                    if (downloadedFirmwareFile?.file != null &&
                            downloadedFirmwareFile?.file!!.exists() &&
                            downloadedFirmwareFile?.file!!.length() > 0) {
                        handleOtaPackage(downloadedFirmwareFile?.file)
                    } else {
                        view?.updateStatus(OtaError.PARSE_PACKAGE_FAIL.errorMessage, false)
                        reportOtaStatus(OtaError.PARSE_PACKAGE_FAIL)
                    }

                } catch (e: Exception) {
                    if (e is ZipException) {
                        view?.updateStatus(OtaError.UNABLE_UNZIP.errorMessage, false)
                        reportOtaStatus(OtaError.UNABLE_UNZIP)
                    } else {
                        view?.updateStatus(OtaError.PARSE_PACKAGE_FAIL.errorMessage, false)
                        reportOtaStatus(OtaError.PARSE_PACKAGE_FAIL)
                    }
                }
            } else {
                view?.updateStatus(OtaError.FILE_CHECKSUM_NOT_MATCH.errorMessage, false)
                reportOtaStatus(OtaError.FILE_CHECKSUM_NOT_MATCH)
            }
        } else {
            view?.updateStatus(OtaError.FILE_CHECKSUM_NOT_MATCH.errorMessage, false)
            reportOtaStatus(OtaError.FILE_CHECKSUM_NOT_MATCH)
        }
    }

    private val downloadOtaPackageListener = object : DownloadFilesListener {

        @ExcludeFromJacocoGeneratedReport
        override fun onDownloadSuccess(file: File, isConfigFile: Boolean) {
            if (upgradeState == READ_CONFIG && isConfigFile) {
                configFw = GeneralUtils.parseXmlOtaPackage(file)
                checkOtaConfig()
            } else {
                view?.updateStatus(R.string.checking_package, true)
                val disposable = Observable.fromCallable {
                    verifyAndInstallOtaPackage(file)
                }.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()

                otaCompositeDisposable.add(disposable)
            }
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onDownloadFailed(isConfigFile: Boolean) {
            if (!isConfigFile) {
                view?.updateStatus(OtaError.DOWNLOAD_OTA_FAIL.errorMessage, false)
                reportOtaStatus(OtaError.DOWNLOAD_OTA_FAIL)
            }
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onUnzipSuccess(path: String) {
            when (upgradeState) {
                READ_FIRMWARE_PACKAGE -> {
                    upgradeState = UPGRADING_SYSTEM
                    checkUpgradeSystem()
                }
                UPGRADING_SYSTEM -> {
                    getSharePreference().isUpgradeSystemSuccessUpdated = false
                    try {
                        otaManager?.installSystem(path)
                    } catch (e: java.lang.Exception) {
                        view?.updateStatus(OtaError.INSTALLS_SYSTEM_FAIL.errorMessage, false)
                        reportOtaStatus(OtaError.INSTALLS_SYSTEM_FAIL)
                    }
                }
                UPGRADING_ANTI_VIRUS -> {
                    // Copy the new DB to folder which contain current DB -> delete current DB -> rename the new DB
                    val serverPath = File(Environment.getExternalStorageDirectory().absolutePath + "/${WebServerService.ROOT_FOLDER}").absolutePath
                    val dbFolderName = "anti-virus"
                    var unzipElementPath: String? = null
                    File(path).listFiles()?.forEach lit@{
                        if (it.isDirectory) {
                            unzipElementPath = it.absolutePath
                            return@lit
                        }
                    }
                    val sourceFile = File(unzipElementPath ?: path)
                    val destinationFile = File(serverPath + "/${sourceFile.name}")
                    if (sourceFile.exists()) {
                        //  Copy the new DB
                        val result = sourceFile.copyRecursively(destinationFile, true)
                        if (result) {
                            if (destinationFile.exists()) {
                                // Rename the new DB
                                if (destinationFile.name != dbFolderName) {
                                    // Delete old database
                                    val oldDb = File("$serverPath/$dbFolderName")
                                    if (oldDb.exists() && !oldDb.deleteRecursively()) {
                                        LogManager.i("Unable to delete old antivirus database")
                                    }
                                    if (!destinationFile.renameTo(File(destinationFile.parentFile.absolutePath + "/$dbFolderName"))) {
                                        LogManager.i("Cannot rename anti-virus folder")
                                    }
                                }

                                val info = configFw?.firmwares?.find { it.type == InfoFirmware.ANTI_VIRUS }
                                if (info?.version?.isEmpty() == false) {
                                    MySharedPref(view?.getContext()).antiVirusVersion = info.version
                                    MySharedPref(view?.getContext()).isCopyAntivirusDbSuccess = true
                                }
                                finishUpgradeFirmware()
                                return
                            }
                            return
                        }
                        return
                    }
                    view?.updateStatus(OtaError.COPY_ANTI_VIRUS_FAIL.errorMessage, false)
                    reportOtaStatus(OtaError.COPY_ANTI_VIRUS_FAIL)
                }
            }
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onUnzipFailed() {
            view?.updateStatus(OtaError.UNABLE_UNZIP.errorMessage, false)
            reportOtaStatus(OtaError.UNABLE_UNZIP)
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onProgressDownload(progress: Int) {
            LogManager.i("OTA", "onProgressDownload $progress%")
        }

        @ExcludeFromJacocoGeneratedReport
        override fun notFoundConfigFile() {
            upgradeState = null
            view?.finishUpgradeFirmware()
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun checkUpgradeSystem() {
        upgradeState = UPGRADING_SYSTEM
        val info = configFw?.firmwares?.find { it.type == InfoFirmware.SYSTEM }
        val currentSystem = Build.DISPLAY
        if (info?.version != null) {
            if (!info.version.equals(currentSystem, true)) {
                val firmwareSystem = File("$unzipFirmwarePath/${info.path}")
                if (firmwareSystem.exists()) {
                    try {
                        view?.updateStatus(R.string.upgrading_system, true)
                        otaManager?.installSystem(firmwareSystem.absolutePath)
                    } catch (e: java.lang.Exception) {
                        view?.updateStatus(OtaError.INSTALLS_SYSTEM_FAIL.errorMessage, false)
                        reportOtaStatus(OtaError.INSTALLS_SYSTEM_FAIL)
                    }
                } else {
                    view?.updateStatus(OtaError.INSTALLS_SYSTEM_FAIL.errorMessage, false)
                    reportOtaStatus(OtaError.INSTALLS_SYSTEM_FAIL)
                }
            } else {
                MySharedPref(view?.getContext()).isUpgradeAppSuccess = true
                checkUpgradeApplication()
            }
        } else {
            checkUpgradeApplication()
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun checkUpgradeApplication() {
        upgradeState = UPGRADING_APPLICATION
        val info = configFw?.firmwares?.find { it.type == InfoFirmware.APPLICATION }
        val currentSystem = BuildConfig.VERSION_NAME
        if (info?.version != null) {
            if (GeneralUtils.needUpdateFirmware(currentSystem, info.version ?: "")) {
                if (!unzipFirmwarePath.isNullOrEmpty()) {
                    val pathApkFile = GeneralUtils.getFileWithEnds(unzipFirmwarePath!!, ".apk")?.absolutePath
                    if (pathApkFile?.isEmpty() == false) {
                        view?.updateStatus(R.string.upgrading_application, true)
                        try {
                            otaManager?.installApk(pathApkFile)
                        } catch (e: Exception) {
                            view?.updateStatus(OtaError.INSTALLS_APP_FAIL.errorMessage, false)
                            reportOtaStatus(OtaError.INSTALLS_APP_FAIL)
                        }
                    } else {
                        view?.updateStatus(OtaError.INSTALLS_APP_FAIL.errorMessage, false)
                        reportOtaStatus(OtaError.INSTALLS_APP_FAIL)
                    }
                } else {
                    view?.updateStatus(OtaError.UNABLE_UNZIP.errorMessage, false)
                    reportOtaStatus(OtaError.UNABLE_UNZIP)
                }
            } else {
                MySharedPref(view?.getContext()).isUpgradeAppSuccess = true
                checkUpgradeSoftware()
            }
        } else {
            checkUpgradeSoftware()
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun checkUpgradeSoftware() {
        upgradeState = UPGRADING_ANTI_VIRUS
        val info = configFw?.firmwares?.find { it.type == InfoFirmware.ANTI_VIRUS }
        val currentVersion = MySharedPref(view?.getContext()).antiVirusVersion
        if (info?.version != null) {
            if (GeneralUtils.needUpdateFirmware(currentVersion ?: "", info.version ?: "")) {
                val firmwarePath = "$unzipFirmwarePath/${info.path}"
                val firmwareFile = File(firmwarePath)
                if (firmwareFile.exists()) {
                    view?.updateStatus(R.string.upgrading_software, true)
                    GeneralUtils.unZipFile(firmwareFile, info.type ?: "", downloadOtaPackageListener)
                } else {
                    view?.updateStatus(OtaError.UNABLE_UNZIP.errorMessage, false)
                    reportOtaStatus(OtaError.UNABLE_UNZIP)
                }
            } else {
                MySharedPref(view?.getContext()).isCopyAntivirusDbSuccess = true
                finishUpgradeFirmware()
            }
        } else {
            finishUpgradeFirmware()
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun resetOTAStatus(sharedPref: MySharedPref) {
        sharedPref.isUpgradeSystemSuccessUpdated = true
        sharedPref.isUpgradeAppSuccess = false
        sharedPref.isCopyAntivirusDbSuccess = false
    }

    @ExcludeFromJacocoGeneratedReport
    private fun checkOTASuccessStatus() {
        val sharedPref = MySharedPref(view?.getContext())
        val previousConfig = GeneralUtils.convertStringToObject<ConfigFirmware>(sharedPref.previousConfig)
        if (configFw?.firmwares?.isNotEmpty() == true && configFw?.version == previousConfig?.version) {
            val cfSysInfo = configFw?.firmwares?.find { it.type == InfoFirmware.SYSTEM }
            val cfAppInfo = configFw?.firmwares?.find { it.type == InfoFirmware.APPLICATION }
            val cfAntivirusInfo = configFw?.firmwares?.find { it.type == InfoFirmware.ANTI_VIRUS }

            val isUpgradeSystemSuccess = if (cfSysInfo?.version != null) {
                Build.DISPLAY == cfSysInfo.version && !sharedPref.isUpgradeSystemSuccessUpdated
            } else {
                true
            }
            val isUpgradeAppSuccess = if (cfAppInfo?.version != null) {
                sharedPref.isUpgradeAppSuccess
            } else {
                true
            }
            val isCopyAntivirusDbSuccess = if (cfAntivirusInfo?.version != null) {
                sharedPref.isCopyAntivirusDbSuccess
            } else {
                true
            }
            if (isUpgradeSystemSuccess && isUpgradeAppSuccess && isCopyAntivirusDbSuccess) {
                reportOTAStatusSuccessFlag = true
                reportOtaStatus(OtaError.SUCCESS, false)
            }
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun finishUpgradeFirmware() {
        checkOTASuccessStatus()

        downloadedFirmwareFile = null
        upgradeState = null
        removeOldFirmware()
        view?.finishUpgradeFirmware()
    }

    @ExcludeFromJacocoGeneratedReport
    private fun removeOldFirmware() {
        val folder = File(downloadFirmwarePath)
        if (folder.exists() && !folder.deleteRecursively()) {
            LogManager.i("Unable to delete old firmware")
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun isNeedDownloadFirmware(): Boolean {
        val cfSysInfo = configFw?.firmwares?.find { it.type == InfoFirmware.SYSTEM }
        val cfAppInfo = configFw?.firmwares?.find { it.type == InfoFirmware.APPLICATION }
        val cfAntivirusInfo = configFw?.firmwares?.find { it.type == InfoFirmware.ANTI_VIRUS }

        val needUpdateSystem = cfSysInfo?.version != null && cfSysInfo.version != Build.DISPLAY
        val needUpdateApp = cfAppInfo?.version != null && GeneralUtils.needUpdateFirmware(
                BuildConfig.VERSION_NAME,
                cfAppInfo.version ?: "")
        val needUpdateAntivirus = cfAntivirusInfo?.version != null && GeneralUtils.needUpdateFirmware(
                MySharedPref(view?.getContext()).antiVirusVersion ?: "",
                cfAntivirusInfo.version ?: "")

        return needUpdateApp || needUpdateSystem || needUpdateAntivirus
    }

    @ExcludeFromJacocoGeneratedReport
    private fun checkDownloadFirmware() {
        val pref = MySharedPref(view?.getContext())
        pref.previousConfig = Gson().toJson(configFw)
        val currentVersion = pref.firmwareVersion
        if (configFw?.version?.isEmpty() == false) {
            val fwFile = File(downloadFirmwarePath, FIRMWARE_FILE_NAME)
            if (!GeneralUtils.needUpdateFirmware(currentVersion ?: "", configFw?.version
                            ?: "") && fwFile.exists()) {
                // Already download firmware
                checkUpgradeSystem()
            } else {
                if (isNeedDownloadFirmware()) {
                    view?.updateStatus(R.string.download_ota_package, true)
                    if (!configFw?.path.isNullOrEmpty()) {
                        upgradeState = READ_FIRMWARE_PACKAGE
                        otaDownloadManager?.downloadOtaPackage()
                    } else {
                        view?.updateStatus(OtaError.PACKAGE_LINK_NOT_FOUND.errorMessage, false)
                        reportOtaStatus(OtaError.PACKAGE_LINK_NOT_FOUND)
                    }
                } else {
                    finishUpgradeFirmware()
                }
            }
        } else {
            finishUpgradeFirmware()
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun reportOtaStatus(otaError: OtaError, isFinalStep: Boolean = true) {
        view?.getContext()?.let {
            val pref = MySharedPref(it)
            val kioskInfo = Gson().fromJson<KioskInfo>(pref.kioskInfo, KioskInfo::class.java)

            val request = OtaStatusRequest()
            request.kioskId = kioskInfo.id
            request.errorStatus = otaError.errorCode
            request.errorDescription = otaError.errorMessage
            request.newVersion = configFw?.version

            when (upgradeState) {
                READ_FIRMWARE_PACKAGE -> {
                    request.type = InfoFirmware.SETUP
                }
                UPGRADING_SYSTEM -> {
                    val info = configFw?.firmwares?.find { fwInfo -> fwInfo.type == InfoFirmware.SYSTEM }
                    request.type = info?.type
                }
                UPGRADING_APPLICATION -> {
                    val info = configFw?.firmwares?.find { fwInfo -> fwInfo.type == InfoFirmware.APPLICATION }
                    request.type = info?.type
                }
                UPGRADING_ANTI_VIRUS -> {
                    val info = configFw?.firmwares?.find { fwInfo -> fwInfo.type == InfoFirmware.ANTI_VIRUS }
                    request.type = info?.type
                }
            }

            if (otaError == OtaError.SUCCESS) {
                request.type = ""
            }

            interactor.reportOtaStatus(pref.eKioskHeader, request, reportOtaStatusResponse)

            if (isFinalStep) {
                // finish update after 2s
                Handler(Looper.getMainLooper()).postDelayed({
                    finishUpgradeFirmware()
                }, 2000)
            }
        }
    }

    private val reportOtaStatusResponse = object : IBaseContract.IBaseInteractorOutput<OtaStatusResponse> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: OtaStatusResponse?) {
            if (reportOTAStatusSuccessFlag) {
                resetOTAStatus(getSharePreference())
            }
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<OtaStatusResponse>) {
            LogManager.d(TAG, "reportOtaStatusResponse - onError")
        }

    }

    private var terminalDailyScheduleIntent: PendingIntent? = null
    @ExcludeFromJacocoGeneratedReport
    override fun terminalDailySchedule() {
        // terminal daily schedule
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)

        val alarmIntent = Intent(view?.getContext(), TerminalDailyReceiver::class.java)
        terminalDailyScheduleIntent = PendingIntent.getBroadcast(
                view?.getContext(),
                TERMINAL_DAILY_CODE,
                alarmIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        )

        val alarmManager: AlarmManager? = view?.getContext()?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        alarmManager?.setRepeating(
                AlarmManager.RTC,
                cal.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                terminalDailyScheduleIntent
        )

    }

    @ExcludeFromJacocoGeneratedReport
    override fun destroyTerminalDailySchedule() {
        val alarmManager: AlarmManager? = view?.getContext()?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        alarmManager?.cancel(terminalDailyScheduleIntent)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var deleteCartOutput = object : IBaseContract.IBaseInteractorOutput<String> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: String?) {
            onDeleteCartResponse()
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<String>) {
            onDeleteCartResponse()
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun onDeleteCartResponse() {
        view?.onDeleteCartResponse()
    }

    override fun deleteCart(token: String?, userId: String?, cartId: String) {
        interactor.deleteCart(token, userId, cartId, null)
    }

    private fun addToCartResult(itemsToAdd: ArrayList<CartItem>, errorMessage: String, addType: BookingType) : IBaseContract.IBaseInteractorOutput<BookingResponse> {
        return object : IBaseContract.IBaseInteractorOutput<BookingResponse> {
            @ExcludeFromJacocoGeneratedReport
            override fun onSuccess(data: BookingResponse?) {
                view?.dismissLoading()
                view?.onAddToCartSuccess(data, itemsToAdd, errorMessage, addType)
            }

            @ExcludeFromJacocoGeneratedReport
            override fun onError(data: BaseResponse<BookingResponse>) {
                view?.dismissLoading()
                view?.showErrorMessage(data)
            }
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun saveScreensaver(screensaverResponse: ScreensaverResponse?) {
        val screensaverFolder = File(Environment.getExternalStorageDirectory().absolutePath + "/$SCREENSAVER_PATH")

        if (!screensaverFolder.exists() && !GeneralUtils.mkdirs(screensaverFolder)) {
            LogManager.i("Unable to create screensaver folder")
            return
        }

        if (screensaverResponse?.items?.isEmpty() == true) { // no screen saver
            if (screensaverFolder.deleteRecursively()) {
                LogManager.i("Removed all screensaver")
            }
            MySharedPref(view?.getContext()).screensaver = null
            MySharedPref(view?.getContext()).lastUpdateScreensaver = 0
            return
        }

        val currentScreensaver: MutableList<Int?> = ArrayList()

        val listFileNameResponse: MutableList<String> = ArrayList()

        screensaverResponse?.items?.forEach ca@ { screensaver ->
            if (screensaver.fileName.isNullOrEmpty() || screensaver.id == null) {
                return@ca
            }

            val imgFile = File(screensaverFolder, screensaver.fileName!!)

            if (imgFile.exists() && screensaver.status == 0) { // screensaver is deactivated
                if (imgFile.delete()) {
                    LogManager.i("Deleted old screensaver ${screensaver.id}")
                }
                return@ca
            }

            if (screensaver.status == 1) { // screensaver is activated
                currentScreensaver.add(screensaver.id)
                listFileNameResponse.add(screensaver.fileName!!);
            }

            if (screensaver.imageBase64.isNullOrEmpty()) {
                return@ca
            }

            val imgBytes = Base64.decode(screensaver.imageBase64!!, Base64.DEFAULT)
            imgFile.writeBytes(imgBytes)
        }

        // remove screensaver that not returned from server
        screensaverFolder.listFiles()?.forEach {
            if (!listFileNameResponse.contains(it.name) && it.delete()) {
                LogManager.i("")
            }
        }

        MySharedPref(view?.getContext()).screensaver = Gson().toJson(currentScreensaver) // store screensaver IDs
        MySharedPref(view?.getContext()).lastUpdateScreensaver = System.currentTimeMillis() / 1000
    }

    var getListScreensaverResult = object : IBaseContract.IBaseInteractorOutput<ScreensaverResponse> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: ScreensaverResponse?) {
            saveScreensaver(data)
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<ScreensaverResponse>) {
            LogManager.d(TAG, "getListScreensaverResult - onError")
        }
    }

    override fun addCourseToCart(token: String, cartId: String?,
                                 requestItems: ArrayList<ProductRequestItem>,
                                 itemsToAdd: ArrayList<CartItem>, errorMessage: String) {
        view?.showLoadingDialog()
        val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
        val productRequest = ProductRequest(cartId, requestItems)
        val request = ProxyRequest(
                header,
                productRequest,
                ProxyRequest.POST_METHOD,
                "",
                API.uriAddCourseToCart
        )
        interactor.addCourseToCart(
                token, view?.getPayer()?.mCustomerId ?: "", request,
                addToCartResult(itemsToAdd, errorMessage, BookingType.ADD_TO_CART))
    }

    override fun getListScreensaver() {
        val token = MySharedPref(view?.getContext()).eKioskHeader
        val lastUpdateScreenSaver = MySharedPref(view?.getContext()).lastUpdateScreensaver
        val screensaverIds = MySharedPref(view?.getContext()).screensaver
        var listId = ArrayList<Int?>()
        if (!screensaverIds.isNullOrEmpty()) {
            val type = object : TypeToken<ArrayList<Int?>>() {}.type
            listId = Gson().fromJson(screensaverIds, type)
        }
        val screensaverRequest = ScreensaverRequest(lastUpdateScreenSaver, listId)
        interactor.getListScreensaver(token, screensaverRequest, getListScreensaverResult)
    }

    private fun getOtaCredentialsResult(): IBaseContract.IBaseInteractorOutput<OtaCredentials> {
        return object : IBaseContract.IBaseInteractorOutput<OtaCredentials> {
            @ExcludeFromJacocoGeneratedReport
            override fun onSuccess(data: OtaCredentials?) {
                if (data != null) {
                    try {
                        if (data.privateKey != null) {
                            val privateKeyDec = Base64.decode(data.privateKey!!, Base64.DEFAULT)
                            val key = String(privateKeyDec)
                            // remove header and footer of key
                            val privateKeyStr = GeneralUtils.removeRsaKeyHeaderAndFooter(key)
                                    .replace(Regex(System.lineSeparator()), "")
                            val encoded: ByteArray = Base64.decode(privateKeyStr, Base64.NO_WRAP)
                            val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
                            val keySpec = PKCS8EncodedKeySpec(encoded)
                            clientPrivateKey = keyFactory.generatePrivate(keySpec)
                        }
                        if (clientPrivateKey != null) {
                            otaDownloadManager = OtaDownloadManager(view?.getContext(), File(downloadFirmwarePath))
                            otaDownloadManager?.listener = downloadOtaPackageListener
                            otaDownloadManager?.downloadOtaConfig()
                        } else {
                            finishUpgradeFirmware()
                        }
                    } catch (e: Exception) {
                        LogManager.i("OTA", "Cannot download OTA config")
                        finishUpgradeFirmware()
                    }
                } else {
                    finishUpgradeFirmware()
                }
            }

            @ExcludeFromJacocoGeneratedReport
            override fun onError(data: BaseResponse<OtaCredentials>) {
                finishUpgradeFirmware()
            }

        }
    }

    override fun getOtaCredentials() {
        interactor.getOtaCredentials(view?.getToken(), EmptyRequest(), getOtaCredentialsResult())
    }

    private var terminalDailyUtils: TerminalDailyUtils? = null
    private var isTerminalDailyListenerRegister = false
    private val terminalDailyListener = object : BroadcastReceiver() {
        @ExcludeFromJacocoGeneratedReport
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (terminalDailyUtils == null) {
                terminalDailyUtils = TerminalDailyUtils()
            }
            terminalDailyUtils?.setTerminalManager(peripheralsService)
            terminalDailyUtils?.handleTerminalDaily()
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun registerTerminalDailySchedule(isRegister: Boolean) {
        view?.getContext()?.let {
            if (isRegister && !isTerminalDailyListenerRegister) {
                LocalBroadcastManager.getInstance(it).registerReceiver(terminalDailyListener, IntentFilter(ACTION_TERMINAL_DAILY))
                isTerminalDailyListenerRegister = true
            } else if (!isRegister && isTerminalDailyListenerRegister) {
                LocalBroadcastManager.getInstance(it).unregisterReceiver(terminalDailyListener)
                isTerminalDailyListenerRegister = false
            }
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun checkOtaConfig() {
        if (configFw != null) {
            // Check if the new firmware was assigned kiosk -> check version firmware -> download the new firmware and check integrity data (if need)
            // Check system -> check app -> check DB
            if (configFw?.kioskIds?.isNullOrEmpty() == false) {
                val pref = MySharedPref(view?.getContext())
                val kioskInfo = GeneralUtils.convertStringToObject<KioskInfo>(pref.kioskInfo)
                val isAssignedKiosk = (configFw?.kioskIds?.filter { it == kioskInfo?.id.toString() }?.size
                        ?: 0) > 0
                if (isAssignedKiosk) {
                    checkDownloadFirmware()
                } else {
                    finishUpgradeFirmware()
                }
            } else {
                checkDownloadFirmware()
            }
        } else {
            finishUpgradeFirmware()
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun handleOtaPackage(file: File?) {
        if (file != null) {
            try {
                if (file.exists()) {
                    configFw?.version?.let {
                        val pref = MySharedPref(view?.getContext())
                        pref.firmwareVersion = it
                    }
                    upgradeState = READ_FIRMWARE_PACKAGE
                    view?.updateStatus(R.string.extracting_ota, true)
                    GeneralUtils.unZipFile(file, UNZIP_FIRMWARE_FOLDER, downloadOtaPackageListener)
                } else {
                    view?.updateStatus(OtaError.PARSE_PACKAGE_FAIL.errorMessage, false)
                    reportOtaStatus(OtaError.PARSE_PACKAGE_FAIL)
                }
            } catch (e: Exception) {
                view?.updateStatus(OtaError.PARSE_PACKAGE_FAIL.errorMessage, false)
                reportOtaStatus(OtaError.PARSE_PACKAGE_FAIL)
            }
        } else {
            view?.updateStatus(OtaError.PARSE_PACKAGE_FAIL.errorMessage, false)
            reportOtaStatus(OtaError.PARSE_PACKAGE_FAIL)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getSharePreference(): MySharedPref {
        return MySharedPref(view?.getContext())
    }
}