package com.styl.pa.modules.main

import android.content.Context
import com.styl.pa.TestBase
import com.styl.pa.entities.api.API
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.generateToken.BookingResponse
import com.styl.pa.entities.generateToken.Outlet
import com.styl.pa.entities.generateToken.PageByOutletDetail
import com.styl.pa.entities.healthDevice.HealthDeviceRequest
import com.styl.pa.entities.healthDevice.InfoHealthDevice
import com.styl.pa.entities.journey.JourneyRequest
import com.styl.pa.entities.log.LastScreenLogRequest
import com.styl.pa.entities.ota.OtaStatusRequest
import com.styl.pa.entities.pacesRequest.ProductRequest
import com.styl.pa.entities.pacesRequest.ProductRequestItem
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.entities.proxy.ProxyRequestHeader
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.main.interactor.MainInteractor
import com.styl.pa.modules.main.presenter.MainPresenter
import com.styl.pa.services.IKioskServices
import com.styl.pa.utils.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.*
import java.util.*
import kotlin.collections.ArrayList

class MainTest: TestBase() {

    private var mainPresenter: MainPresenter? = null
    private var mainView: IMainContract.IView? = null
    private var mainInteractor: MainInteractor? = null

    @Before
    override fun setUp() {
        super.setUp()
        mainInteractor = MainInteractor()
        mainInteractor!!.setKioskService(
                MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer)
        )
    }

    @Test(expected = Test.None::class)
    fun deleteCart_should_success() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter!!.deleteCart(MockInterceptor.TEST_EKIOSK_HEADER, MockInterceptor.TEST_USER_ID, MockInterceptor.TEST_CART_ID)
    }

    @Test
    fun deleteCart_should_error() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter?.deleteCartOutput = mock()
        mainPresenter!!.deleteCart(MockInterceptor.TEST_EKIOSK_HEADER, MockInterceptor.TEST_USER_ID, "")
        verify(mainPresenter?.deleteCartOutput, never())?.onSuccess("true")
    }

    @Test
    fun addCourseToCart_should_success() {
        val course = GeneralUtils.convertStringToObject<ClassInfo>(
                TestUtils.readJsonFile("course.json")
        )
        val cartItem = CartItem(
                UUID.randomUUID().toString(), course, null,
                null, null, null, null, null
        )
        val courseRequestItem = ProductRequestItem(cartItem.classInfo?.sku, "1")
        val productRequest = ProductRequest(
                MockInterceptor.TEST_CART_ID,
                arrayListOf(courseRequestItem)
        )
        val output : IBaseContract.IBaseInteractorOutput<BookingResponse> = mock()
        val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
        val request = ProxyRequest(
                header,
                productRequest,
                ProxyRequest.POST_METHOD,
                "",
                API.uriAddCourseToCart
        )
        mainInteractor?.addCourseToCart(
                MockInterceptor.TEST_EKIOSK_HEADER,
                MockInterceptor.TEST_USER_ID,
                request,
                output
        )
        verify(output, never()).onError(any())
    }

    @Test
    fun addCourseToCart_should_error() {
        val course = GeneralUtils.convertStringToObject<ClassInfo>("")
        val cartItem = CartItem(
                UUID.randomUUID().toString(), course, null,
                null, null, null, null, null
        )
        val courseRequestItem = ProductRequestItem(cartItem.classInfo?.sku, "1")
        val productRequest = ProductRequest(
                MockInterceptor.TEST_CART_ID,
                arrayListOf(courseRequestItem)
        )
        val output : IBaseContract.IBaseInteractorOutput<BookingResponse> = mock()
        val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
        val request = ProxyRequest(
                header,
                productRequest,
                ProxyRequest.POST_METHOD,
                "",
                API.uriQuickBookFacility
        )
        mainInteractor?.addCourseToCart(
                MockInterceptor.TEST_EKIOSK_HEADER,
                MockInterceptor.TEST_USER_ID,
                request,
                output
        )
        verify(output, never()).onSuccess(any())
    }


    @Test(expected = Test.None::class)
    fun addCourseToCart_view_null() {
        mainView = null
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter?.addCourseToCart(
                MockInterceptor.TEST_EKIOSK_HEADER,
                null,
                ArrayList(),
                ArrayList(),
                ""
        )
    }

    @Test(expected = Test.None::class)
    fun addCourseToCart_view_not_null() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val productRequestItem = ProductRequestItem("TestItem")
        mainPresenter?.addCourseToCart(
                MockInterceptor.TEST_EKIOSK_HEADER,
                null,
                arrayListOf(productRequestItem),
                ArrayList(),
                ""
        )
    }

    @Test
    fun getSharePreference_view_null() {
        mainPresenter = MainPresenter(null, mainInteractor!!)
        val sharePref = mainPresenter?.getSharePreference()
        assert(sharePref != null)
    }

    @Test
    fun getSharePreference_view_not_null() {
        mainView = mock()
        mainPresenter = MainPresenter(mock(), mainInteractor!!)
        val sharePref = mainPresenter?.getSharePreference()
        assert(sharePref != null)
    }

    @Test(expected = Test.None::class)
    fun deleteCart_cartId_not_empty_view_not_null() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter!!.deleteCart(MockInterceptor.TEST_EKIOSK_HEADER, MockInterceptor.TEST_USER_ID, MockInterceptor.TEST_CART_ID)
    }


    @Test(expected = Test.None::class)
    fun deleteCart_cartId_empty_view_not_null() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter!!.deleteCart(MockInterceptor.TEST_EKIOSK_HEADER, MockInterceptor.TEST_USER_ID, "")
    }

    @Test(expected = Test.None::class)
    fun deleteCart_cartId_not_empty_view_null() {
        mainView = null
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter!!.deleteCart(MockInterceptor.TEST_EKIOSK_HEADER, MockInterceptor.TEST_USER_ID, MockInterceptor.TEST_CART_ID)
    }

    @Test(expected = Test.None::class)
    fun deleteCart_cartId_empty_view_null() {
        mainView = null
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter!!.deleteCart(MockInterceptor.TEST_EKIOSK_HEADER, MockInterceptor.TEST_USER_ID, "")
    }

    @Test
    fun getAppVersion_should_not_null_or_empty() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val appVersion = mainPresenter?.getAppVersion()
        assert(!appVersion.isNullOrEmpty())
    }

    @Test
    fun calculateOtaTime_should_2_45() {
        mainView = mock()
        val context: Context = mock()
        doReturn(context).`when`(mainView)?.getContext()
        mainPresenter = MainPresenter(mainView)
        val spyMainPresenter = spy(mainPresenter)
        val sharedPref: MySharedPref = mock()
        doReturn(sharedPref).`when`(spyMainPresenter)?.getSharePreference()
        doReturn("{\"id\": 60}").`when`(sharedPref).kioskInfo
        val otaTime = spyMainPresenter?.calculateOtaTime()
        assert(otaTime != null)
        assert(otaTime!![0] == 2)
        assert(otaTime[1] == 45)
    }

    @Test
    fun calculateOtaTime_should_2_30() {
        mainView = mock()
        val context: Context = mock()
        doReturn(context).`when`(mainView)?.getContext()
        mainPresenter = MainPresenter(mainView)
        val spyMainPresenter = spy(mainPresenter)
        val sharedPref: MySharedPref = mock()
        doReturn(sharedPref).`when`(spyMainPresenter)?.getSharePreference()
        doReturn("{\"id\": 70}").`when`(sharedPref).kioskInfo
        val otaTime = spyMainPresenter?.calculateOtaTime()
        assert(otaTime != null)
        assert(otaTime!![0] == 2)
        assert(otaTime[1] == 30)
    }

    @Test
    fun calculateOtaTime_should_3_15() {
        mainView = mock()
        val context: Context = mock()
        doReturn(context).`when`(mainView)?.getContext()
        mainPresenter = MainPresenter(mainView)
        val spyMainPresenter = spy(mainPresenter)
        val sharedPref: MySharedPref = mock()
        doReturn(sharedPref).`when`(spyMainPresenter)?.getSharePreference()
        doReturn("{\"id\": 80}").`when`(sharedPref).kioskInfo
        val otaTime = spyMainPresenter?.calculateOtaTime()
        assert(otaTime != null)
        assert(otaTime!![0] == 3)
        assert(otaTime[1] == 15)
    }

    @Test
    fun calculateOtaTime_should_3_0() {
        mainView = mock()
        val context: Context = mock()
        doReturn(context).`when`(mainView)?.getContext()
        mainPresenter = MainPresenter(mainView)
        val spyMainPresenter = spy(mainPresenter)
        val sharedPref: MySharedPref = mock()
        doReturn(sharedPref).`when`(spyMainPresenter)?.getSharePreference()
        doReturn("{\"id\": 90}").`when`(sharedPref).kioskInfo
        val otaTime = spyMainPresenter?.calculateOtaTime()
        assert(otaTime != null)
        assert(otaTime!![0] == 3)
        assert(otaTime[1] == 0)
    }

    @Test
    fun calculateOtaTime_should_null() {
        mainView = mock()
        val context: Context = mock()
        doReturn(context).`when`(mainView)?.getContext()
        mainPresenter = MainPresenter(mainView)
        val spyMainPresenter = spy(mainPresenter)
        val sharedPref: MySharedPref = mock()
        doReturn(sharedPref).`when`(spyMainPresenter)?.getSharePreference()
        doReturn(null).`when`(sharedPref).kioskInfo
        val otaTime = spyMainPresenter?.calculateOtaTime()
        assert(otaTime == null)

    }


    @Test
    fun calculateOtaTime_should_1_45() {
        mainView = mock()
        val context: Context = mock()
        doReturn(context).`when`(mainView)?.getContext()
        mainPresenter = MainPresenter(mainView)
        val spyMainPresenter = spy(mainPresenter)
        val sharedPref: MySharedPref = mock()
        doReturn(sharedPref).`when`(spyMainPresenter)?.getSharePreference()
        doReturn("{\"id\": 10}").`when`(sharedPref).kioskInfo
        val otaTime = spyMainPresenter?.calculateOtaTime()
        assert(otaTime != null)
        assert(otaTime!![0] == 1)
        assert(otaTime[1] == 45)
    }

    @Test
    fun calculateOtaTime_should_1_30() {
        mainView = mock()
        val context: Context = mock()
        doReturn(context).`when`(mainView)?.getContext()
        mainPresenter = MainPresenter(mainView)
        val spyMainPresenter = spy(mainPresenter)
        val sharedPref: MySharedPref = mock()
        doReturn(sharedPref).`when`(spyMainPresenter)?.getSharePreference()
        doReturn("{\"id\": 20}").`when`(sharedPref).kioskInfo
        val otaTime = spyMainPresenter?.calculateOtaTime()
        assert(otaTime != null)
        assert(otaTime!![0] == 1)
        assert(otaTime[1] == 30)
    }

    @Test
    fun calculateOtaTime_should_2_15() {
        mainView = mock()
        val context: Context = mock()
        doReturn(context).`when`(mainView)?.getContext()
        mainPresenter = MainPresenter(mainView)
        val spyMainPresenter = spy(mainPresenter)
        val sharedPref: MySharedPref = mock()
        doReturn(sharedPref).`when`(spyMainPresenter)?.getSharePreference()
        doReturn("{\"id\": 30}").`when`(sharedPref).kioskInfo
        val otaTime = spyMainPresenter?.calculateOtaTime()
        assert(otaTime != null)
        assert(otaTime!![0] == 2)
        assert(otaTime[1] == 15)
    }

    @Test
    fun calculateOtaTime_should_2_0() {
        mainView = mock()
        val context: Context = mock()
        doReturn(context).`when`(mainView)?.getContext()
        mainPresenter = MainPresenter(mainView)
        val spyMainPresenter = spy(mainPresenter)
        val sharedPref: MySharedPref = mock()
        doReturn(sharedPref).`when`(spyMainPresenter)?.getSharePreference()
        doReturn("{\"id\": 40}").`when`(sharedPref).kioskInfo
        val otaTime = spyMainPresenter?.calculateOtaTime()
        assert(otaTime != null)
        assert(otaTime!![0] == 2)
        assert(otaTime[1] == 0)
    }

    @Test(expected = Test.None::class)
    fun authenticateKiosk_success() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter?.authenticateKiosk("Test", "Test")
    }

    @Test(expected = Test.None::class)
    fun authenticateKiosk_failed() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter?.authenticateKiosk(null, "Test")
    }

    @Test(expected = Test.None::class)
    fun authenticateKiosk_view_null() {
        mainView = null
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter?.authenticateKiosk("Test", "Test")
    }

    @Test(expected = Test.None::class)
    fun updateKioskInfo_success() {
        mainView = null
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        Mockito.`when`(spyMainPresenter?.getAppVersion()).thenReturn("3.0.0")
        Mockito.`when`(spyMainPresenter?.getOs()).thenReturn("3.0.0")
        spyMainPresenter?.updateKioskInfo("Test", "10", "2.3")
    }

    @Test(expected = Test.None::class)
    fun updateKioskInfo_failed() {
        mainView = null
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        Mockito.`when`(spyMainPresenter?.getAppVersion()).thenReturn("3.0.0")
        Mockito.`when`(spyMainPresenter?.getOs()).thenReturn(null)
        spyMainPresenter?.updateKioskInfo("Test", "11", "2.3")
    }

    @Test(expected = Test.None::class)
    fun getKioskInfo_success() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        spyMainPresenter?.getKioskInfo(MockInterceptor.TEST_EKIOSK_HEADER, 10)
    }

    @Test(expected = Test.None::class)
    fun getKioskInfo_failed() {
        mainView = null
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        spyMainPresenter?.getKioskInfo(MockInterceptor.TEST_EKIOSK_HEADER, 11)
    }

    @Test(expected = Test.None::class)
    fun help_view_not_null() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter?.help(MockInterceptor.TEST_EKIOSK_HEADER)
    }

    @Test(expected = Test.None::class)
    fun help_view_null() {
        mainView = null
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter?.help(MockInterceptor.TEST_EKIOSK_HEADER)
    }

    @Test(expected = Test.None::class)
    fun aliveTracking() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        Mockito.`when`(mainView?.getToken()).thenReturn(MockInterceptor.TEST_EKIOSK_HEADER)
        spyMainPresenter?.aliveTracking()
    }

    @Test(expected = Test.None::class)
    fun aliveTracking_view_null() {
        mainView = null
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        spyMainPresenter?.aliveTracking()
    }

    @Test(expected = Test.None::class)
    fun aliveTracking_token_empty() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        Mockito.`when`(mainView?.getToken()).thenReturn("")
        spyMainPresenter?.aliveTracking()
    }

    @Test(expected = Test.None::class)
    fun aliveTracking_token_null() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        Mockito.`when`(mainView?.getToken()).thenReturn(null)
        spyMainPresenter?.aliveTracking()
    }

    @Test(expected = Test.None::class)
    fun lastScreenLog_view_null() {
        mainView = null
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        spyMainPresenter?.lastScreenLog(LastScreenLogRequest())
    }

    @Test(expected = Test.None::class)
    fun lastScreenLog_token_null() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        Mockito.`when`(mainView?.getToken()).thenReturn(null)
        spyMainPresenter?.lastScreenLog(LastScreenLogRequest())
    }

    @Test(expected = Test.None::class)
    fun lastScreenLog_token_not_null() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        Mockito.`when`(mainView?.getToken()).thenReturn(MockInterceptor.TEST_EKIOSK_HEADER)
        spyMainPresenter?.lastScreenLog(LastScreenLogRequest())
    }

    @Test(expected = Test.None::class)
    fun lastScreenLog_token_empty() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        Mockito.`when`(mainView?.getToken()).thenReturn("")
        spyMainPresenter?.lastScreenLog(LastScreenLogRequest())
    }

    @Test(expected = Test.None::class)
    fun deviceHealthUpdate_token_null() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        val healthRequest = HealthDeviceRequest(
                InfoHealthDevice(InfoHealthDevice.SCANNER_NAME, InfoHealthDevice.SCANNER_CONNECT)
        )
        Mockito.`when`(spyMainPresenter?.checkTimeRangeReportHealthStatus(healthRequest)).thenReturn(true)
        Mockito.`when`(mainView?.getToken()).thenReturn(null)
        spyMainPresenter?.deviceHealthUpdate(healthRequest)
    }

    @Test(expected = Test.None::class)
    fun deviceHealthUpdate_token_empty() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        val healthRequest = HealthDeviceRequest(
                InfoHealthDevice(InfoHealthDevice.SCANNER_NAME, InfoHealthDevice.SCANNER_CONNECT)
        )
        Mockito.`when`(spyMainPresenter?.checkTimeRangeReportHealthStatus(healthRequest)).thenReturn(true)
        Mockito.`when`(mainView?.getToken()).thenReturn("")
        spyMainPresenter?.deviceHealthUpdate(healthRequest)
    }

    @Test(expected = Test.None::class)
    fun deviceHealthUpdate_time_range_false() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        val healthRequest = HealthDeviceRequest(
                InfoHealthDevice(InfoHealthDevice.SCANNER_NAME, InfoHealthDevice.SCANNER_CONNECT)
        )
        Mockito.`when`(spyMainPresenter?.checkTimeRangeReportHealthStatus(healthRequest)).thenReturn(false)
        Mockito.`when`(mainView?.getToken()).thenReturn("")
        spyMainPresenter?.deviceHealthUpdate(healthRequest)
    }

    @Test(expected = Test.None::class)
    fun deviceHealthUpdate_time_range_true() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        val healthRequest = HealthDeviceRequest(
                InfoHealthDevice(InfoHealthDevice.SCANNER_NAME, InfoHealthDevice.SCANNER_CONNECT)
        )
        Mockito.`when`(spyMainPresenter?.checkTimeRangeReportHealthStatus(healthRequest)).thenReturn(true)
        Mockito.`when`(mainView?.getToken()).thenReturn(MockInterceptor.TEST_EKIOSK_HEADER)
        spyMainPresenter?.deviceHealthUpdate(healthRequest)
    }

    @Test(expected = Test.None::class)
    fun deviceHealthUpdate_time_range_failed() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        val healthRequest = HealthDeviceRequest(
                InfoHealthDevice("", InfoHealthDevice.SCANNER_CONNECT)
        )
        Mockito.`when`(spyMainPresenter?.checkTimeRangeReportHealthStatus(healthRequest)).thenReturn(true)
        Mockito.`when`(mainView?.getToken()).thenReturn(MockInterceptor.TEST_EKIOSK_HEADER)
        spyMainPresenter?.deviceHealthUpdate(healthRequest)
    }

    @Test(expected = Test.None::class)
    fun printerNotFound_view_notNull() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter?.printerNotFound()
    }

    @Test(expected = Test.None::class)
    fun printerNotFound_view_null() {
        mainView = null
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter?.printerNotFound()
    }

    @Test(expected = Test.None::class)
    fun scannerNotFound_view_notNull() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter?.scannerNotFound()
    }

    @Test(expected = Test.None::class)
    fun scannerNotFound_view_Null() {
        mainView = null
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter?.scannerNotFound()
    }

    @Test(expected = Test.None::class)
    fun deviceHealthUpdate_view_null() {
        mainView = null
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        val healthRequest = HealthDeviceRequest(
                InfoHealthDevice("", InfoHealthDevice.SCANNER_CONNECT)
        )
        Mockito.`when`(spyMainPresenter?.checkTimeRangeReportHealthStatus(healthRequest)).thenReturn(true)
        spyMainPresenter?.deviceHealthUpdate(healthRequest)
    }

    @Test(expected = Test.None::class)
    fun trackUserJourney_view_null() {
        mainView = null
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter?.trackUserJourney(null)
    }

    @Test(expected = Test.None::class)
    fun trackUserJourney_token_empty() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        Mockito.`when`(mainView?.getToken()).thenReturn("")
        spyMainPresenter?.trackUserJourney(null)
    }

    @Test(expected = Test.None::class)
    fun trackUserJourney_success() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        Mockito.`when`(mainView?.getToken()).thenReturn(MockInterceptor.TEST_EKIOSK_HEADER)
        val request = JourneyRequest()
        request.sessionId = "Test"
        spyMainPresenter?.trackUserJourney(request)
    }

    @Test(expected = Test.None::class)
    fun trackUserJourney_failed() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val spyMainPresenter = spy(mainPresenter)
        Mockito.`when`(mainView?.getToken()).thenReturn(MockInterceptor.TEST_EKIOSK_HEADER)
        val request = JourneyRequest()
        spyMainPresenter?.trackUserJourney(request)
    }

    @Test(expected = Test.None::class)
    fun getLocationAndCategories_view_null() {
        mainView = null
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val context: Context = mock()
        mainPresenter?.getLocationAndCategories(context, null)
    }

    @Test(expected = Test.None::class)
    fun getLocationAndCategories_view_not_null() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        val context: Context = mock()
        mainPresenter?.getLocationAndCategories(context, null)
    }

    @Test(expected = Test.None::class)
    fun getOtaCredentials() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter?.getOtaCredentials()
    }

    @Test(expected = Test.None::class)
    fun getOtaCredentials_view_null() {
        mainView = null
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter?.getOtaCredentials()
    }

    @Test(expected = Test.None::class)
    fun getListScreensaver_view_null() {
        mainView = null
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter?.getListScreensaver()
    }

    @Test(expected = Test.None::class)
    fun getListScreensaver_view_not_null() {
        mainView = mock()
        mainPresenter = MainPresenter(mainView, mainInteractor!!)
        mainPresenter?.getListScreensaver()
    }

    @Test(expected = Test.None::class)
    fun reportOtaStatus_success() {
        mainView = mock()
        val request = OtaStatusRequest()
        request.kioskId = 10
        mainInteractor?.reportOtaStatus(
                MockInterceptor.TEST_EKIOSK_HEADER,
                request,
                null
        )
    }

    @Test(expected = Test.None::class)
    fun reportOtaStatus_failed() {
        mainView = mock()
        val request = OtaStatusRequest()
        mainInteractor?.reportOtaStatus(
                MockInterceptor.TEST_EKIOSK_HEADER,
                request,
                null
        )
    }


    @Test
    fun sortLocationByName_list_not_null() {
        val locationResponse = GeneralUtils.convertStringToObject<PageByOutletDetail>(
                TestUtils.readJsonFile("location.json")
        )
        val outletList = locationResponse?.getOutletDetailList()
        mainPresenter = MainPresenter(null, mainInteractor!!)
        val sortedList = mainPresenter?.sortLocationByName(outletList)
        assert(sortedList != null)
        assert(sortedList!![0].getFriendlyName() == "ACE THE PLACE CC")
    }

    @Test
    fun sortLocationByName_list_null() {
        val outletList: ArrayList<Outlet>? = null
        mainPresenter = MainPresenter(null, mainInteractor!!)
        val sortedList = mainPresenter?.sortLocationByName(outletList)
        assert(sortedList?.isEmpty() == true)
    }

    @Test
    fun sortNeighbourhoods() {
        val locationResponse = GeneralUtils.convertStringToObject<PageByOutletDetail>(
                TestUtils.readJsonFile("location.json")
        )
        val neighbourhoodList = locationResponse?.getNeighbourhood()
        assert(neighbourhoodList?.isNotEmpty() == true)
        mainPresenter = MainPresenter(null, mainInteractor!!)
        val sortedList = mainPresenter!!.sortNeighbourhoods(neighbourhoodList!!)
        assert(sortedList.isNotEmpty())
        assert(sortedList[0].title == "Ang Mo Kio")
    }


    @After
    override fun tearDown() {
        super.tearDown()
    }
}