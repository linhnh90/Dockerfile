package com.styl.pa.services

import com.google.gson.internal.LinkedTreeMap
import com.styl.pa.entities.advancedSearch.AdvancedSearchRequest
import com.styl.pa.entities.checkResidency.CheckResidencyResponse
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.courseCategory.CourseCategoryList
import com.styl.pa.entities.courseCategory.EventCategoryList
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.event.EventResponse
import com.styl.pa.entities.facility.BookingByEmailRequest
import com.styl.pa.entities.facility.FacilitySessionResponse
import com.styl.pa.entities.feedback.FeedbackRequest
import com.styl.pa.entities.generateToken.*
import com.styl.pa.entities.healthDevice.HealthDeviceRequest
import com.styl.pa.entities.interestgroup.PageByInterestGroup
import com.styl.pa.entities.journey.JourneyRequest
import com.styl.pa.entities.keepAlive.KeepAliveResponse
import com.styl.pa.entities.kioskactivation.KioskActivationRequest
import com.styl.pa.entities.kioskactivation.KioskInfo
import com.styl.pa.entities.log.LastScreenLogRequest
import com.styl.pa.entities.main.*
import com.styl.pa.entities.ota.*
import com.styl.pa.entities.pacesRequest.*
import com.styl.pa.entities.pacesRequest.addEventParticipant.AddEventParticipantRequest
import com.styl.pa.entities.participant.ParticipantResponse
import com.styl.pa.entities.payment.PaymentData
import com.styl.pa.entities.payment.PaymentRequest
import com.styl.pa.entities.payment.PaymentResponse
import com.styl.pa.entities.payment.PaymentStatus
import com.styl.pa.entities.product.ProductListResponse
import com.styl.pa.entities.promocode.AvailablePromoCodeRequest
import com.styl.pa.entities.promocode.ListPromoCodeResponse
import com.styl.pa.entities.promocode.PromoCodeRequest
import com.styl.pa.entities.proximity.ProximityLocationResponse
import com.styl.pa.entities.proxy.EmptyRequest
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.entities.proxy.ProxyResponse
import com.styl.pa.entities.recommendatetions.RecommendationResponse
import com.styl.pa.entities.reservation.*
import com.styl.pa.entities.rulesAndRegulations.RulesAndRegulationsResponse
import com.styl.pa.entities.screensaver.ScreensaverRequest
import com.styl.pa.entities.screensaver.ScreensaverResponse
import com.styl.pa.entities.search.SearchLocationRequest
import com.styl.pa.entities.search.SearchProductRequest
import com.styl.pa.entities.search.SearchRequest
import com.styl.pa.entities.sendEmail.ErrorReceiptRequest
import com.styl.pa.entities.sendEmail.SendEmailRequest
import com.styl.pa.entities.sendEmail.SendEmailResponse
import com.styl.pa.entities.sendmail.SendMailRequest
import com.styl.pa.entities.sendmail.SendMailResponse
import com.styl.pa.entities.vacancy.VacancyResponse
import com.styl.pa.utils.TykConfig
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import java.util.*

/**
 * Created by trangpham on 9/25/2018
 */
interface IKioskServices {

    @POST("api/kiosk/activate")
    fun activateKiosk(
            @Body request: KioskActivationRequest?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<KioskInfo>>

    @POST("api/kiosk/authenticate")
    fun authenticateKiosk(
            @Body request: KioskAuthenticationRequest?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<KioskAuthenticationResponse>>

    @POST("api/kiosk/information/update")
    fun updateKioskInfo(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: KioskUpdateRequest?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<KioskUpdateResponse>>

    @GET("api/kiosk/{id}/information")
    fun getKioskInfo(
            @Header("ekiosk-Authorization") token: String?,
            @Path("id") kioskId: Int?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<KioskInfo>>

    @POST("api/proxy/request")
    fun proxyRequestCustomer(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<CustomerInfo>>>>

    @POST("api/proxy/request")
    fun proxyRequestDeleteReservation(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<Boolean>>>>

    @POST("api/proxy/request")
    fun proxyRequestGenerateReceipt(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<ReceiptRequest>,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<ReceiptResponse>>>>

    @POST("api/kiosk/cache/class/search")
    fun searchClass(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: AdvancedSearchRequest,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<PageByClassInfo>>

    @GET("api/kiosk/outlet/recommendation/get")
    fun searchRecommendation(
            @Header("ekiosk-Authorization") token: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<RecommendationResponse>>

    @POST("api/kiosk/cache/facility/search")
    fun searchFacility(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: SearchRequest,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<PageByFacility>>

    @POST("api/kiosk/interestgroup/search")
    fun searchInterestGroup(
        @Header("ekiosk-Authorization") token: String?,
        @Body request: AdvancedSearchRequest,
        @Header("Authorization") internalToken: String = "",
        @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<PageByInterestGroup>>

    @POST("api/proxy/request")
    fun getClassByCode(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<PageByClassInfo>>>>

    @POST("api/proxy/request")
    fun getEventByCode(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<EventResponse>>>>

    @POST("api/kiosk/cache/location/search")
    fun searchLocation(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: SearchLocationRequest,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<PageByOutletDetail>>

    @POST("api/proxy/request")
    fun getFacilitySessionInfo(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<FacilitySessionResponse>>>>

    @POST("api/proxy/request")
    fun proxyRequestFacilityBookingAuth(@Header("Authorization") token: String?, @Body request: ProxyRequest<FacilityBookingAuthRequest>): Observable<Response<ResponseBody>>

    @POST("api/proxy/request")
    fun proxyRequestTotalCostData(@Header("Authorization") token: String?, @Body request: ProxyRequest<TotalCostsRequest>): Observable<Response<ResponseBody>>

    @POST("api/kiosk/sendmail")
    fun sendMail(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: SendMailRequest?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<SendMailResponse>>

    @GET("api/kiosk/facility/rules")
    fun getRulesAndRegulations(
            @Header("ekiosk-Authorization") token: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<RulesAndRegulationsResponse>>

    @POST("api/proxy/request")
    fun getCourseCategoryList(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<CourseCategoryList>>>>

    @POST("api/kiosk/payment/submit")
    fun submitPayment(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: PaymentRequest?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<PaymentResponse>>

    @POST("api/kiosk/payment/update")
    fun updatePayment(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: PaymentRequest?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<PaymentResponse>>

    @GET("api/kiosk/help")
    fun help(
            @Header("ekiosk-Authorization") token: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<HelpResponse>>

    @POST("api/kiosk/booking/send")
    fun bookingRequestEmail(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: BookingByEmailRequest,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<SendMailResponse>>

    @POST("api/kiosk/receipt/send")
    fun sendEmail(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: SendEmailRequest?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<SendEmailResponse>>

    @POST("api/kiosk/ping")
    fun aliveTracking(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: EmptyRequest,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<KeepAliveResponse>>

    @POST("api/kiosk/class/advsearch")
    fun advancedSearch(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: AdvancedSearchRequest?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<PageByClassInfo>>

    @POST("api/kiosk/tracking/submit")
    fun trackingAbortAction(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: LastScreenLogRequest,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<SendMailResponse>>

    @POST("api/kiosk/health/update")
    fun healthUpdate(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: HealthDeviceRequest?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<SendEmailResponse>>

    @POST("api/proxy/request")
    fun getBookingDetails(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<EmptyRequest>?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<BookingDetail>>>>

    @POST("api/proxy/request")
    fun getClassDetails(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<EmptyRequest>?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<ClassInfo>>>>

    @POST("api/proxy/request")
    fun getEventByOutlet(@Header("Authorization") token: String?,
                         @Body request: ProxyRequest<EmptyRequest>): Observable<Response<ProxyResponse<Data<EventResponse>>>>

    @POST("api/proxy/request")
    fun proxyBookingAuthProgramme(@Header("Authorization") token: String?, @Body request: ProxyRequest<ProgrammeBookingAuthRequest>): Observable<Response<ProxyResponse<Data<BookingAuthResponse>>>>

    @POST("api/proxy/request")
    fun proxyBookingAuthFacility(@Header("Authorization") token: String?, @Body request: ProxyRequest<FacilityBookingAuthRequest>): Observable<Response<ProxyResponse<Data<BookingAuthResponse>>>>

    @POST("api/kiosk/rating/submit")
    fun reportFeedback(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: FeedbackRequest?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<SendMailResponse>>

    @POST("api/kiosk/cache/event/search")
    fun searchEvent(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: AdvancedSearchRequest,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<EventResponse>>

    @POST("api/proxy/request")
    fun getEventCategoryList(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<EventCategoryList>>>>

    @POST("api/kiosk/user/journey/submit")
    fun trackUserJourney(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: JourneyRequest?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<SendMailResponse>>

    @POST("api/proxy/request")
    fun proxyRequestEventVacancy(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<TreeMap<String, String>>,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<VacancyResponse>>>>

    @POST("api/kiosk/receipt/notification")
    fun sendMailReportErrorReceipt(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ErrorReceiptRequest?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<SendMailResponse>>

    @POST("api/kiosk/cache/class/distance/search")
    fun searchProximityLocations(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: AdvancedSearchRequest?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProximityLocationResponse>>

    @POST("api/kiosk/interestgroup/distance/search")
    fun searchProximityLocationsInterestGroup(
        @Header("ekiosk-Authorization") token: String?,
        @Body request: AdvancedSearchRequest?,
        @Header("Authorization") internalToken: String = "",
        @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProximityLocationResponse>>

    @POST("api/kiosk/cache/event/distance/search")
    fun searchProximityLocationsEvent(
        @Header("ekiosk-Authorization") token: String?,
        @Body request: AdvancedSearchRequest?,
        @Header("Authorization") internalToken: String = "",
        @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProximityLocationResponse>>

    @POST("api/kiosk/ota/updatestatus")
    fun reportOtaStatus(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: OtaStatusRequest?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<OtaStatusResponse>>

    @POST("api/kiosk/product/search")
    fun searchProduct(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: SearchProductRequest,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProductListResponse>>

    @POST("api/proxy/request")
    fun loadCart(
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("ekiosk-Authorization") token: String,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<LinkedTreeMap<String, Any?>>>>

    @POST("api/proxy/request")
    fun deleteCart(
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("ekiosk-Authorization") token: String?,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<String>>>>

    @POST("api/proxy/request")
    fun quickBookFacility(
            @Body request: ProxyRequest<FacilityRequest>,
            @Header("ekiosk-Authorization") token: String,
            @Header("x-user-id") userId: String,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("api/proxy/request")
    fun quickBookCourse(
            @Body request: ProxyRequest<ProductRequest>,
            @Header("ekiosk-Authorization") token: String,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("api/proxy/request")
    fun quickBookIg(
        @Body request: ProxyRequest<ProductRequest>,
        @Header("ekiosk-Authorization") token: String,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = "",
        @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("kiosk-proxy-paces")
    fun quickBookEvent(
        @Body request: ProxyRequest<ProductRequest>,
        @Header("ekiosk-Authorization") token: String,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = "",
        @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("kiosk-proxy-paces")
    fun addEventToCart(
        @Body request: ProxyRequest<ProductRequest>,
        @Header("ekiosk-Authorization") token: String,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = "",
        @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("api/proxy/request")
    fun addCourseToCart(
            @Body request: ProxyRequest<ProductRequest>,
            @Header("ekiosk-Authorization") token: String,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("api/proxy/request")
    fun addIgToCart(
        @Body request: ProxyRequest<ProductRequest>,
        @Header("ekiosk-Authorization") token: String,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = "",
        @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("api/proxy/request")
    fun removeItemFromCart(
            @Body request: ProxyRequest<ProductRequest>,
            @Header("ekiosk-Authorization") token: String,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("kiosk-proxy-paces")
    fun removeEventItemFromCart(
        @Body request: ProxyRequest<ProductRequest>,
        @Header("ekiosk-Authorization") token: String,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = "",
        @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("api/proxy/request")
    fun removeItemIgFromCart(
        @Body request: ProxyRequest<ProductRequest>,
        @Header("ekiosk-Authorization") token: String,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = "",
        @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("api/proxy/request")
    fun addParticipant(
            @Body request: ProxyRequest<ParticipantRequest<ParticipantItem>>,
            @Header("ekiosk-Authorization") token: String,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<ParticipantResponse>>>>

    @POST("kiosk-proxy-paces")
    fun addEventParticipant(
        @Body request: ProxyRequest<AddEventParticipantRequest>,
        @Header("ekiosk-Authorization") token: String,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = "",
        @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<ParticipantResponse>>>>

    @POST("api/proxy/request")
    fun updateQuantity(
            @Body request: ProxyRequest<ProductRequest>,
            @Header("ekiosk-Authorization") token: String,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<Unit>>>>

    @POST("api/proxy/request")
    fun deleteParticipant(
            @Body request: ProxyRequest<ParticipantRequest<ParticipantUserId>>,
            @Header("ekiosk-Authorization") token: String?,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<ParticipantResponse>>>>

    @POST("api/proxy/request")
    fun deleteParticipantIg(
        @Body request: ProxyRequest<ParticipantRequest<ParticipantUserId>>,
        @Header("ekiosk-Authorization") token: String?,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = "",
        @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<ParticipantResponse>>>>

    @POST("api/proxy/request")
    fun prepareCheckout(
            @Body request: ProxyRequest<CheckoutRequest>,
            @Header("ekiosk-Authorization") token: String,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<String>>>>

    @POST("api/proxy/request")
    fun createOrder(
            @Body request: ProxyRequest<OrderRequest>,
            @Header("ekiosk-Authorization") token: String?,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<Unit>>>>

    @POST("api/proxy/request")
    fun getCustomerDetail(
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("ekiosk-Authorization") token: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<CustomerInfo>>>>

    @POST("api/proxy/request")
    fun getCourseAvailability(
            @Body request: ProxyRequest<CourseAvailabilityRequest>,
            @Header("ekiosk-Authorization") token: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<VacancyResponse>>>>

    @POST("api/proxy/request")
    fun getIgAvailability(
        @Body request: ProxyRequest<IgAvailabilityRequest>,
        @Header("ekiosk-Authorization") token: String?,
        @Header("Authorization") internalToken: String = "",
        @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<VacancyResponse>>>>

    @POST("api/proxy/request")
    fun getEventAvailability(
        @Body request: ProxyRequest<EventAvailabilityRequest>,
        @Header("ekiosk-Authorization") token: String?,
        @Header("Authorization") internalToken: String = "",
        @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<VacancyResponse>>>>

    @POST("api/proxy/request")
    fun getFacilityAvailability(
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("ekiosk-Authorization") token: String,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<Data<FacilitySessionResponse>>>>

    @POST("api/proxy/request")
    fun getPaymentOptions(
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("ekiosk-Authorization") token: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<LinkedTreeMap<String, Any>>>>

    @POST("api/proxy/request")
    fun createPaymentReference(
            @Body request: ProxyRequest<PaymentRefRequest>,
            @Header("ekiosk-Authorization") token: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<PaymentData<String>>>>

    @POST("api/proxy/request")
    fun updatePaymentReference(
            @Body request: ProxyRequest<PaymentStateRequest>,
            @Header("ekiosk-Authorization") token: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<PaymentData<String>>>>

    @POST("api/proxy/request")
    fun checkPaymentStatus(
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("ekiosk-Authorization") token: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ProxyResponse<PaymentData<PaymentStatus>>>>

    @GET("api/ota/getchecksum")
    fun getOtaConfig(
            @Header("ekiosk-Authorization") token: String?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ConfigFirmware>>

    @Streaming
    @GET
    fun downloadOtaFile(
            @Header("ekiosk-Authorization") token: String?,
            @Header("Range") byteRange: String,
            @Url urlPath: String,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ResponseBody>>


    @POST("api/kiosk/screensaver/get")
    fun getListScreensaver(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ScreensaverRequest?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<ScreensaverResponse>>

    @POST("api/kiosk/ota/getcert")
    fun getOtaCredentials(
            @Header("ekiosk-Authorization") token: String?,
            @Body keyCertRequest: EmptyRequest?,
            @Header("Authorization") internalToken: String = "",
            @Header("Authorization-ext") externalToken: String = ""
    ): Observable<Response<OtaCredentials>>

    @POST("api/proxy/request")
    fun applyPromoCode(
        @Header("ekiosk-Authorization") token: String?,
        @Body promoCodeRequest: ProxyRequest<PromoCodeRequest>?,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
        @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<LinkedTreeMap<String, Any?>>>>

    @POST("api/proxy/request")
    fun removePromoCode(
        @Header("ekiosk-Authorization") token: String?,
        @Body promoCodeRequest: ProxyRequest<EmptyRequest>,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
        @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<LinkedTreeMap<String, Any?>>>>

    @POST("api/kiosk/promotion/eligible")
    fun getAllAvailablePromoCodes(
        @Header("ekiosk-Authorization") token: String?,
        @Body request: AvailablePromoCodeRequest,
        @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
        @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ListPromoCodeResponse>>

    @POST("kiosk-proxy-paces")
    fun checkResidency(
        @Header("ekiosk-Authorization") token: String?,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
        @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader,
        @Body request: ProxyRequest<EmptyRequest>
    ): Observable<Response<ProxyResponse<Data<CheckResidencyResponse>>>>

}