package com.styl.pa.modules.main

import android.content.Context
import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import com.styl.pa.entities.GeneralException
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.courseCategory.CourseCategoryList
import com.styl.pa.entities.courseCategory.EventCategoryList
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.generateToken.BookingResponse
import com.styl.pa.entities.generateToken.Outlet
import com.styl.pa.entities.generateToken.PageByOutletDetail
import com.styl.pa.entities.generateToken.PageByOutletType
import com.styl.pa.entities.healthDevice.HealthDeviceRequest
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.entities.journey.JourneyRequest
import com.styl.pa.entities.keepAlive.KeepAliveResponse
import com.styl.pa.entities.kioskactivation.KioskInfo
import com.styl.pa.entities.log.LastScreenLogRequest
import com.styl.pa.entities.main.HelpResponse
import com.styl.pa.entities.main.KioskAuthenticationRequest
import com.styl.pa.entities.main.KioskAuthenticationResponse
import com.styl.pa.entities.main.KioskUpdateRequest
import com.styl.pa.entities.ota.OtaCredentials
import com.styl.pa.entities.ota.OtaStatusRequest
import com.styl.pa.entities.ota.OtaStatusResponse
import com.styl.pa.entities.pacesRequest.ProductRequest
import com.styl.pa.entities.pacesRequest.ProductRequestItem
import com.styl.pa.entities.payment.PaymentOption
import com.styl.pa.entities.product.ProductListResponse
import com.styl.pa.entities.proxy.EmptyRequest
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.entities.screensaver.ScreensaverRequest
import com.styl.pa.entities.screensaver.ScreensaverResponse
import com.styl.pa.entities.sendmail.SendMailResponse
import com.styl.pa.enums.BookingType
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.peripheralsManager.peripheralsService.PeripheralsInfo
import com.styl.pa.modules.peripheralsManager.terminalManager.IPaymentResultListener
import com.styl.pa.modules.printer.IPrinterFontConfig
import com.styl.pa.modules.printer.customPrinterService.HandlePrintStatus
import com.styl.pa.modules.scanner.zebraScannerService.DcssdkListener
import com.styl.pa.modules.vacancyChecking.IVacancyCheckingContact
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by trangpham on 9/3/2018
 */
interface IMainContract {

    interface IView : IBaseContract.IBaseView, IVacancyCheckingContact.IView {
        fun setScannerResult(result: Boolean)

        fun setPrinterResult(result: Boolean)

        fun getContext(): Context

        fun onGetOutletTypeSuccess(outletType: PageByOutletType)

        fun onSearchOutletDetailByName(outletDetail: PageByOutletDetail)

        fun onAuthenticationSuccess(token: String?, expiry: Int?, isFirst: Boolean)

        fun onAuthenticationFailed()

        fun getToken(): String?

        fun onGetInfoSuccess(info: String)

        fun showActivationScreen()

        fun isConnectScanner(): Boolean
        fun isConnectPrinter(): Boolean

        fun onLocationByOutletSuccess(pageByOutletDetail: PageByOutletDetail?)
        fun onCategorySuccess(categoryList: CourseCategoryList?)
        fun onCategoryEventSuccess(categories: EventCategoryList?)
        fun onGetAllClassInfoSuccess(productListResponse: ProductListResponse?)
        fun onGetAllEventInfoSuccess(eventResponse: ProductListResponse?)
        fun onGetAllIGSuccess(igResponse: ProductListResponse?)
        fun showErrorMessageAndTitle(message: Int, title: Int)
        fun onScanQrSuccess(data: String?)

        fun updateStatus(resource: Int, isSuccess: Boolean)
        fun updateStatus(message: String, isSuccess: Boolean)
        fun showMaintenanceView(isShow: Boolean, content: String?)

        fun showLoadingDialog()
        fun finishUpgradeFirmware()

        fun onAddToCartSuccess(
                response: BookingResponse?,
                itemsToAdd: ArrayList<CartItem>,
                errorMessage: String?,
                addType: BookingType
        )
        fun onDeleteCartResponse()
        fun onGetPaymentOptionsSuccess(data: List<PaymentOption>?)
        fun getDataAppPath(): String
        fun getPayer(): CustomerInfo?

        fun updateCanOrder(isAlive: Boolean)
    }

    interface IPresenter : IBaseContract.IBasePresenter {
        fun isScanner(device: UsbDevice?): Boolean
        fun isPrinter(device: UsbDevice?): Boolean
        fun isTerminal(device: UsbDevice?): Boolean

        //        fun initScannerService()
        fun initScannerManager(usbDevice: UsbDevice)
        fun pullTrigger(isPull: Boolean)
        fun setConfigEvent(listener: DcssdkListener.DcssdkConfig)
        fun detachScannerUsb()
        fun disconnectScanner()

        //        fun initPrinterService()
        fun initPrinterManager(usbDevice: UsbDevice)
        fun getPrinterFontConfig(): IPrinterFontConfig?
        fun setHandlePrinterStatusEvent(event: HandlePrintStatus)
        fun printText(text: String, printerFont: Int?, fontSize: Int?,
                      isBold: Boolean, isItalic: Boolean, isUnderline: Boolean): GeneralException?
        fun detachPrinterUsb()

        fun printImage(bitmap: Bitmap?)
        fun cutPage()
        fun printFeed()
        fun checkStatusPrinter(): GeneralException?
        fun disconnectPrinter()
        fun startCheckStatusWhenPrint()
        fun endCheckStatusWhenPrint()

        fun initTerminalManager(usbDevice: UsbDevice)
        fun detachTerminalUsb()
        fun disconnectTerminal()

        fun navigationHomeView()
        fun navigationAttendCoursesView(location: Outlet?,
                                        classList: ArrayList<ClassInfo>?)

        fun navigationBookFacilitiesView(location: Outlet?,
                                         classList: ArrayList<ClassInfo>?)

        fun navigationParticipateEvent(location: Outlet?,
                                       classList: ArrayList<ClassInfo>?)

        fun navigationInterestGroupsView(location: Outlet?,
                                        classList: ArrayList<ClassInfo>?)

        fun navigationAdvancedSearchEvent()
        fun navigationScanQRView()
        fun navigationCommingSoonPage()

        fun navigationCardView()
        fun navigationCartView(isBookingMyself: Boolean)
        fun navigationEmptyCardView()

        fun navigateSettingsView()

        fun subscribeKioskInfo(subject: BehaviorSubject<String>)

        fun authenticateKiosk(accessKey: String?, secretKey: String?)
        fun updateKioskInfo(token: String?, id: String?, version: String?)
        fun getKioskInfo(token: String?, kioskId: Int?)
        fun refreshToken(expiry: Int?, kioskInfo: KioskInfo?)
        fun help(token: String?)

        fun aliveTracking()

        fun lastScreenLog(request: LastScreenLogRequest)
        fun deviceHealthUpdate(request: HealthDeviceRequest)

        fun reportDailyHealth()

        fun trackUserJourney(request: JourneyRequest?)

        fun calculateOtaTime(): Array<Int>?

        fun calculateWaitingTime(hour: Int, minute: Int, second: Int, takeNextTime: Boolean): Long
        fun rebootKioskDaily()

        fun destroyRangeTimeHealthStatusUtils()

        // Handle new peripherals
        fun initUsbDevices()

        fun terminalDailySchedule()
        fun destroyTerminalDailySchedule()

        fun deleteCart(token: String?, userId: String?, cartId: String)
        fun addCourseToCart(token: String, cartId: String?,
                            requestItems: ArrayList<ProductRequestItem>,
                            itemsToAdd: ArrayList<CartItem>, errorMessage: String)

        fun getOtaConfig()
        fun getListScreensaver()

        fun getOtaCredentials()
    }

    interface IPeripheralPresenter {
        fun getContext(): Context?

        fun getScannerCallback(): DcssdkListener.DcssdkInit?
        fun initScannerManager(peripheralsInfo: PeripheralsInfo)

        fun initPrinterManager(peripheralsInfo: PeripheralsInfo, printerStatus: GeneralException?)
        fun disconnectPrinterManager()

        fun initTerminalManager(peripheralsInfo: PeripheralsInfo)

        fun initTerminalManagerResult(isConnected: Boolean)
        fun setPaymentCallbacks(listener: IPaymentResultListener?)
        fun payProduct(cardType: Int, amount: Int): Boolean
        fun printerNotFound()
        fun scannerNotFound()
    }

    interface IInteractor : IBaseContract.IBaseInteractor {

        fun subscribeKioskInfo(subject: BehaviorSubject<String>)

        fun authenticateKiosk(request: KioskAuthenticationRequest?)

        fun updateKioskInfo(token: String?, request: KioskUpdateRequest?)

        fun getKioskInfo(token: String?, kioskId: Int?)

        fun refreshToken(context: Context?, count: Long, request: KioskAuthenticationRequest?)

        fun help(token: String?)

        fun aliveTracking(token: String?, request: EmptyRequest, callback: IBaseContract.IBaseInteractorOutput<KeepAliveResponse>?)

        fun lastScreenLog(token: String?, request: LastScreenLogRequest)

        fun deviceHealthUpdate(token: String?, request: HealthDeviceRequest)

        fun trackUserJourney(token: String, request: JourneyRequest?, output: IBaseContract.IBaseInteractorOutput<SendMailResponse>?)

        fun reportOtaStatus(token: String?, request: OtaStatusRequest, callback: IBaseContract.IBaseInteractorOutput<OtaStatusResponse>?)

        fun deleteCart(token: String?, userId: String?, cartId: String,
                       output: IBaseContract.IBaseInteractorOutput<String>?)
        fun addCourseToCart(token: String, userId: String, request: ProxyRequest<ProductRequest>,
                            output: IBaseContract.IBaseInteractorOutput<BookingResponse>)
        fun getListScreensaver(token: String?, request: ScreensaverRequest?, output: IBaseContract.IBaseInteractorOutput<ScreensaverResponse>)

        fun getOtaCredentials(token: String?, request: EmptyRequest, callback: IBaseContract.IBaseInteractorOutput<OtaCredentials>)

    }

    interface IInteractorOutput {
    }

    interface IInteractorOutput2 : IBaseContract.IBaseInteractorOutput<KioskAuthenticationResponse> {

        fun onKioskInfoUpdated(kioskInfo: KioskInfo?)
    }

    interface IInteractorOutput3 : IBaseContract.IBaseInteractorOutput<KioskInfo> {

    }

    interface IInteractorOutput4 : IBaseContract.IBaseInteractorOutput<HelpResponse> {

    }

    interface IInteractorOutputAliveTracking : IBaseContract.IBaseInteractorOutput<SendMailResponse> {

    }

    interface IRouter : IBaseContract.IBaseRouter {
        fun navigationHomeView()
        fun navigationAttendCoursesView(location: Outlet?,
                                        classList: ArrayList<ClassInfo>?)

        fun navigationBookFacilitiesView(location: Outlet?,
                                         classList: ArrayList<ClassInfo>?)

        fun navigationParticipateEvent(location: Outlet?,
                                       classList: ArrayList<ClassInfo>?)

        fun navigationInterestGroupsView(location: Outlet?,
                                        classList: ArrayList<ClassInfo>?)

        fun navigationAdvancedSearchEvent()
        fun navigationScanQRView(listener: IMainContract.IView?)
        fun navigationComingSoonPage()

        fun navigationCardView()
        fun navigationCartView(isBookingMyself: Boolean)
        fun navigationEmptyCardView()

        fun navigateHelpView(content: String?)
        fun navigationCourseDetailPage(classInfo: ClassInfo?)
        fun navigateEventDetailPage(eventInfo: EventInfo)
        fun navigateIgDetailPage(igInfo: InterestGroup)

        fun navigateSettingsView()
    }
}