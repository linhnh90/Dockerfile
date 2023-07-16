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
import com.styl.pa.entities.ota.OtaCredentials
import com.styl.pa.entities.ota.OtaStatusRequest
import com.styl.pa.entities.ota.OtaStatusResponse
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

    @POST("activate-kiosk")
    fun activateKiosk(
            @Body request: KioskActivationRequest?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<KioskInfo>>

    @POST("authenticate-kiosk")
    fun authenticateKiosk(
            @Body request: KioskAuthenticationRequest?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<KioskAuthenticationResponse>>

    @POST("update-kiosk-info")
    fun updateKioskInfo(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: KioskUpdateRequest?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<KioskUpdateResponse>>

    @GET("get-kiosk-info/api/kiosk/{id}/information")
    fun getKioskInfo(
            @Header("ekiosk-Authorization") token: String?,
            @Path("id") kioskId: Int?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<KioskInfo>>

    @POST("kiosk-proxy-paces")
    fun proxyRequestCustomer(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<CustomerInfo>>>>

    @POST("kiosk-proxy-paces")
    fun proxyRequestDeleteReservation(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<Boolean>>>>

    @POST("kiosk-proxy-paces")
    fun proxyRequestGenerateReceipt(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<ReceiptRequest>,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<ReceiptResponse>>>>

    @POST("search-new-class")
    fun searchClass(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: AdvancedSearchRequest,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<PageByClassInfo>>

    @POST("search-interestgroup")
    fun searchInterestGroup(
        @Header("ekiosk-Authorization") token: String?,
        @Body request: AdvancedSearchRequest,
        @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
        @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<PageByInterestGroup>>

    @GET("get-kiosk-recommendation-list")
    fun searchRecommendation(
            @Header("ekiosk-Authorization") token: String?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<RecommendationResponse>>

    @POST("search-new-facility")
    fun searchFacility(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: SearchRequest,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<PageByFacility>>

    @POST("kiosk-proxy-paces")
    fun getClassByCode(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<PageByClassInfo>>>>

    @POST("kiosk-proxy-paces")
    fun getEventByCode(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<EventResponse>>>>

    @POST("get-location-list")
    fun searchLocation(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: SearchLocationRequest,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<PageByOutletDetail>>

    @POST("kiosk-proxy-paces")
    fun getFacilitySessionInfo(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<FacilitySessionResponse>>>>

    @POST("kiosk-proxy-paces")
    fun proxyRequestFacilityBookingAuth(@Header("Authorization") token: String?, @Body request: ProxyRequest<FacilityBookingAuthRequest>): Observable<Response<ResponseBody>>

    @POST("kiosk-proxy-paces")
    fun proxyRequestTotalCostData(@Header("Authorization") token: String?, @Body request: ProxyRequest<TotalCostsRequest>): Observable<Response<ResponseBody>>

    @POST("api/kiosk/sendmail")
    fun sendMail(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: SendMailRequest?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<SendMailResponse>>

    @GET("rules-regulations")
    fun getRulesAndRegulations(
            @Header("ekiosk-Authorization") token: String?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<RulesAndRegulationsResponse>>

    @POST("kiosk-proxy-paces")
    fun getCourseCategoryList(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<CourseCategoryList>>>>

    @POST("submit-payment-txn-to-ekiosk")
    fun submitPayment(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: PaymentRequest?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<PaymentResponse>>

    @POST("update-txn-info-with-terminal-resp")
    fun updatePayment(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: PaymentRequest?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<PaymentResponse>>

    @GET("get-kiosk-help-content")
    fun help(
            @Header("ekiosk-Authorization") token: String?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<HelpResponse>>

    @POST("email-booking-request")
    fun bookingRequestEmail(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: BookingByEmailRequest,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<SendMailResponse>>

    @POST("api/kiosk/receipt/send")
    fun sendEmail(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: SendEmailRequest?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<SendEmailResponse>>

    @POST("ping-kiosk")
    fun aliveTracking(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: EmptyRequest,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<KeepAliveResponse>>

    @POST("api/kiosk/class/advsearch")
    fun advancedSearch(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: AdvancedSearchRequest?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<PageByClassInfo>>

    @POST("track-session-timeout")
    fun trackingAbortAction(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: LastScreenLogRequest,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<SendMailResponse>>

    @POST("update-peripheral-status")
    fun healthUpdate(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: HealthDeviceRequest?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<SendEmailResponse>>

    @POST("kiosk-proxy-paces")
    fun getBookingDetails(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<EmptyRequest>?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<BookingDetail>>>>

    @POST("kiosk-proxy-paces")
    fun getClassDetails(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<EmptyRequest>?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<ClassInfo>>>>

    @POST("kiosk-proxy-paces")
    fun getEventByOutlet(@Header("Authorization") token: String?,
                         @Body request: ProxyRequest<EmptyRequest>): Observable<Response<ProxyResponse<Data<EventResponse>>>>

    @POST("kiosk-proxy-paces")
    fun proxyBookingAuthProgramme(@Header("Authorization") token: String?, @Body request: ProxyRequest<ProgrammeBookingAuthRequest>): Observable<Response<ProxyResponse<Data<BookingAuthResponse>>>>

    @POST("kiosk-proxy-paces")
    fun proxyBookingAuthFacility(@Header("Authorization") token: String?, @Body request: ProxyRequest<FacilityBookingAuthRequest>): Observable<Response<ProxyResponse<Data<BookingAuthResponse>>>>

    @POST("submit-user-rating")
    fun reportFeedback(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: FeedbackRequest?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<SendMailResponse>>

    @POST("search-new-event")
    fun searchEvent(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: AdvancedSearchRequest,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<EventResponse>>

    @POST("kiosk-proxy-paces")
    fun getEventCategoryList(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<EventCategoryList>>>>

    @POST("track-user-journey")
    fun trackUserJourney(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: JourneyRequest?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<SendMailResponse>>

    @POST("kiosk-proxy-paces")
    fun proxyRequestEventVacancy(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ProxyRequest<TreeMap<String, String>>,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<VacancyResponse>>>>

    @POST("api/kiosk/receipt/notification")
    fun sendMailReportErrorReceipt(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ErrorReceiptRequest?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<SendMailResponse>>

    @POST("search-nearby-classes")
    fun searchProximityLocations(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: AdvancedSearchRequest?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProximityLocationResponse>>

    @POST("search-nearby-interestgroup")
    fun searchProximityLocationsInterestGroup(
        @Header("ekiosk-Authorization") token: String?,
        @Body request: AdvancedSearchRequest?,
        @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
        @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProximityLocationResponse>>

    @POST("search-nearby-event")
    fun searchProximityLocationsEvent(
        @Header("ekiosk-Authorization") token: String?,
        @Body request: AdvancedSearchRequest?,
        @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
        @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProximityLocationResponse>>

    @POST("ota/api/kiosk/ota/updatestatus")
    fun reportOtaStatus(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: OtaStatusRequest?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<OtaStatusResponse>>

    @POST("search-product")
    fun searchProduct(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: SearchProductRequest,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProductListResponse>>

    @POST("kiosk-proxy-paces")
    fun loadCart(
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("ekiosk-Authorization") token: String,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<LinkedTreeMap<String, Any?>>>>

    @POST("kiosk-proxy-paces")
    fun deleteCart(
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("ekiosk-Authorization") token: String?,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<String>>>>

    @POST("kiosk-proxy-paces")
    fun quickBookFacility(
            @Body request: ProxyRequest<FacilityRequest>,
            @Header("ekiosk-Authorization") token: String,
            @Header("x-user-id") userId: String,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("kiosk-proxy-paces")
    fun quickBookCourse(
            @Body request: ProxyRequest<ProductRequest>,
            @Header("ekiosk-Authorization") token: String,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("kiosk-proxy-paces")
    fun quickBookIg(
        @Body request: ProxyRequest<ProductRequest>,
        @Header("ekiosk-Authorization") token: String,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
        @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("kiosk-proxy-paces")
    fun quickBookEvent(
        @Body request: ProxyRequest<ProductRequest>,
        @Header("ekiosk-Authorization") token: String,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
        @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("kiosk-proxy-paces")
    fun addCourseToCart(
            @Body request: ProxyRequest<ProductRequest>,
            @Header("ekiosk-Authorization") token: String,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("kiosk-proxy-paces")
    fun addIgToCart(
        @Body request: ProxyRequest<ProductRequest>,
        @Header("ekiosk-Authorization") token: String,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
        @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("kiosk-proxy-paces")
    fun addEventToCart(
        @Body request: ProxyRequest<ProductRequest>,
        @Header("ekiosk-Authorization") token: String,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
        @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("kiosk-proxy-paces")
    fun removeItemFromCart(
            @Body request: ProxyRequest<ProductRequest>,
            @Header("ekiosk-Authorization") token: String,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("kiosk-proxy-paces")
    fun removeEventItemFromCart(
        @Body request: ProxyRequest<ProductRequest>,
        @Header("ekiosk-Authorization") token: String,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
        @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("kiosk-proxy-paces")
    fun removeItemIgFromCart(
        @Body request: ProxyRequest<ProductRequest>,
        @Header("ekiosk-Authorization") token: String,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
        @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<BookingResponse>>>>

    @POST("kiosk-proxy-paces")
    fun addParticipant(
            @Body request: ProxyRequest<ParticipantRequest<ParticipantItem>>,
            @Header("ekiosk-Authorization") token: String,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<ParticipantResponse>>>>

    @POST("kiosk-proxy-paces")
    fun addEventParticipant(
        @Body request: ProxyRequest<AddEventParticipantRequest>,
        @Header("ekiosk-Authorization") token: String,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
        @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<ParticipantResponse>>>>

    @POST("kiosk-proxy-paces")
    fun updateQuantity(
            @Body request: ProxyRequest<ProductRequest>,
            @Header("ekiosk-Authorization") token: String,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<Unit>>>>

    @POST("kiosk-proxy-paces")
    fun deleteParticipant(
            @Body request: ProxyRequest<ParticipantRequest<ParticipantUserId>>,
            @Header("ekiosk-Authorization") token: String?,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<ParticipantResponse>>>>

    @POST("kiosk-proxy-paces")
    fun deleteParticipantIg(
        @Body request: ProxyRequest<ParticipantRequest<ParticipantUserId>>,
        @Header("ekiosk-Authorization") token: String?,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
        @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<ParticipantResponse>>>>

    @POST("kiosk-proxy-paces")
    fun prepareCheckout(
            @Body request: ProxyRequest<CheckoutRequest>,
            @Header("ekiosk-Authorization") token: String,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<String>>>>

    @POST("kiosk-proxy-paces")
    fun createOrder(
            @Body request: ProxyRequest<OrderRequest>,
            @Header("ekiosk-Authorization") token: String?,
            @Header("x-user-id") userId: String?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<Unit>>>>

    @POST("kiosk-proxy-paces")
    fun getCustomerDetail(
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("ekiosk-Authorization") token: String?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<CustomerInfo>>>>

    @POST("kiosk-proxy-paces")
    fun getCourseAvailability(
            @Body request: ProxyRequest<CourseAvailabilityRequest>,
            @Header("ekiosk-Authorization") token: String?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<VacancyResponse>>>>

    @POST("kiosk-proxy-paces")
    fun getIgAvailability(
        @Body request: ProxyRequest<IgAvailabilityRequest>,
        @Header("ekiosk-Authorization") token: String?,
        @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
        @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<VacancyResponse>>>>

    @POST("kiosk-proxy-paces")
    fun getFacilityAvailability(
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("ekiosk-Authorization") token: String,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<FacilitySessionResponse>>>>

    @POST("kiosk-proxy-paces")
    fun getPaymentOptions(
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("ekiosk-Authorization") token: String?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<LinkedTreeMap<String, Any>>>>

    @POST("kiosk-proxy-paces")
    fun createPaymentReference(
            @Body request: ProxyRequest<PaymentRefRequest>,
            @Header("ekiosk-Authorization") token: String?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<PaymentData<String>>>>

    @POST("kiosk-proxy-paces")
    fun updatePaymentReference(
            @Body request: ProxyRequest<PaymentStateRequest>,
            @Header("ekiosk-Authorization") token: String?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<PaymentData<String>>>>

    @POST("kiosk-proxy-paces")
    fun checkPaymentStatus(
            @Body request: ProxyRequest<EmptyRequest>,
            @Header("ekiosk-Authorization") token: String?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<PaymentData<PaymentStatus>>>>

    @POST("get-screensaver-images")
    fun getListScreensaver(
            @Header("ekiosk-Authorization") token: String?,
            @Body request: ScreensaverRequest?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ScreensaverResponse>>

    @POST("get-ota-client-cert-details")
    fun getOtaCredentials(
            @Header("ekiosk-Authorization") token: String?,
            @Body keyCertRequest: EmptyRequest?,
            @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
            @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<OtaCredentials>>

    @POST("kiosk-proxy-paces")
    fun applyPromoCode(
        @Header("ekiosk-Authorization") token: String?,
        @Body promoCodeRequest: ProxyRequest<PromoCodeRequest>?,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
        @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<LinkedTreeMap<String, Any?>>>>

    @POST("kiosk-proxy-paces")
    fun removePromoCode(
        @Header("ekiosk-Authorization") token: String?,
        @Body promoCodeRequest: ProxyRequest<EmptyRequest>,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
        @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<LinkedTreeMap<String, Any?>>>>

    @POST("search-promotion-eligible")
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

    @POST("kiosk-proxy-paces")
    fun getEventAvailability(
        @Body request: ProxyRequest<EventAvailabilityRequest>,
        @Header("ekiosk-Authorization") token: String?,
        @Header("x-user-id") userId: String?,
        @Header("Authorization") internalToken: String = TykConfig.internalJwtHeader,
        @Header("Authorization-ext") externalToken: String = TykConfig.externalJwtHeader
    ): Observable<Response<ProxyResponse<Data<VacancyResponse>>>>
}