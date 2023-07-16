package com.styl.pa.utils

import com.styl.pa.entities.api.API
import java.net.HttpURLConnection
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

/**
 * For testing purposes only
 * only for SIT
 */
class MockInterceptor: Dispatcher() {

    override fun dispatch(recordedRequest: RecordedRequest?): MockResponse {
        val request = recordedRequest?.body?.readUtf8() ?: ""
        var responseString: String = ""
        var code: Int = 500
        val uri = recordedRequest?.path ?: ""
        when {
            // mock delete cart response
            request.contains(API.deleteCart(TEST_CART_ID)) -> {
                responseString = TestUtils.readJsonFile(DELETE_CART_RESPONSE)
                code = HttpURLConnection.HTTP_OK
            }
            request.contains(API.deleteCart("")) -> {
                responseString = "test"
                code = HttpURLConnection.HTTP_OK
            }

            // mock quickbook facility response
            request.contains(API.uriQuickBookFacility) &&
                    request.contains("facilityId") -> {
                responseString = TestUtils.readJsonFile(QUICKBOOK_FACILITY_RESPONSE)
                code = HttpURLConnection.HTTP_OK
            }
            request.contains(API.uriQuickBookFacility) &&
                    !request.contains("facilityId") -> {
                responseString = ""
                code = HttpURLConnection.HTTP_OK
            }

            // mock add course to cart response
            request.contains(API.uriAddCourseToCart) &&
                    request.contains("itemId") -> {
                responseString = TestUtils.readJsonFile(ADD_COURSE_TO_CART_RESPONSE)
                code = HttpURLConnection.HTTP_OK
            }
            request.contains(API.uriAddCourseToCart) &&
                    !request.contains("itemId") -> {
                responseString = ""
                code = HttpURLConnection.HTTP_OK
            }

            // mock add participant response
            request.contains(API.uriAddParticipant)
                    && request.contains("pham.duc.doan.trang") -> {
                responseString = TestUtils.readJsonFile(ADD_PARTICIPANT_RESPONSE)
                code = HttpURLConnection.HTTP_OK
            }
            request.contains(API.uriAddParticipant)
                    && !request.contains("pham.duc.doan.trang") -> {
                responseString = ""
                code = HttpURLConnection.HTTP_OK
            }

            // mock delete participant response
            request.contains(API.uriDeleteParicipant)
                    && request.contains("TestCourse1") -> {
                responseString = TestUtils.readJsonFile(DELETE_PARTICIPANT_RESPONSE)
                code = HttpURLConnection.HTTP_OK
            }
            request.contains(API.uriDeleteParicipant)
                    && request.contains("TestCourse3") -> {
                responseString = ""
                code = HttpURLConnection.HTTP_OK
            }

            // mock create payment ref response
            request.contains(API.uriCreatePaymentReference)
                    && !request.contains("customer") -> {
                responseString = ""
                code = HttpURLConnection.HTTP_OK
            }
            request.contains(API.uriPrepareCheckout)
                    && !request.contains("mobile") -> {
                responseString = ""
                code = HttpURLConnection.HTTP_OK
            }

            // mock remove
            request.contains(API.uriRemoveItemFromCart) &&
                    request.contains("TestCourse2") &&
                    request.contains(TEST_CART_ID) -> {
                responseString = TestUtils.readJsonFile(REMOVE_ITEM_FROM_CART_RESPONSE)
                code = HttpURLConnection.HTTP_OK
            }
            request.contains(API.uriRemoveItemFromCart) &&
                    !request.contains(TEST_CART_ID) -> {
                responseString = ""
                code = HttpURLConnection.HTTP_OK
            }

            // mock get customer detail
            request.contains(API.getCustomerDetail("123")) -> {
                responseString = ""
                code = HttpURLConnection.HTTP_OK
            }

            // mock submit payment response
            uri.contains("api/kiosk/payment/submit") &&
                    !request.contains("CustomerId") -> {
                responseString = ""
                code = HttpURLConnection.HTTP_OK
            }

            // mock activate kiosk response
            uri.contains("api/kiosk/activate") -> {
                if (request.contains("hardwareId")) {
                    responseString = TestUtils.readJsonFile(ACTIVATE_KIOSK_RESPONSE)
                    code = HttpURLConnection.HTTP_OK
                }
            }

            // mock search class response
            uri.contains("api/kiosk/cache/class/search")
                    && !request.contains("Keyword") -> {
                responseString = ""
                code = HttpURLConnection.HTTP_OK
            }

            // mock search event response
            uri.contains("api/kiosk/cache/event/search")
                    && !request.contains("Keyword") -> {
                responseString = ""
                code = HttpURLConnection.HTTP_OK
            }

            // mock search facility response
            uri.contains("api/kiosk/cache/facility/search")
                    && !request.contains("Keyword") -> {
                responseString = ""
                code = HttpURLConnection.HTTP_OK
            }

            // mock search product response
            uri.contains("api/kiosk/product/search")
                    && !request.contains("Keyword") -> {
                responseString = ""
                code = HttpURLConnection.HTTP_OK
            }

            // mock screen saver resposne
            uri.contains("api/kiosk/screensaver/get") -> {
                responseString = TestUtils.readJsonFile(SCREENSAVER_RESPONSE)
                code = HttpURLConnection.HTTP_OK
            }

            // mock update OTA status response
            uri.contains("api/kiosk/ota/updatestatus") -> {
                if (request.contains("KioskId")) {
                    responseString = TestUtils.readJsonFile(SUCCESS_RESPONSE_WITH_ID)
                    code = HttpURLConnection.HTTP_OK
                } else {
                    responseString = ""
                    code = HttpURLConnection.HTTP_OK
                }

            }

            // mock authentication response
            uri.contains("api/kiosk/authenticate") -> {
                if (request.contains("AccessKey")) {
                    responseString = TestUtils.readJsonFile(AUTHENTICATION_SUCCESS_RESPONSE)
                    code = HttpURLConnection.HTTP_OK
                }
            }

            // mock kiosk info response
            uri.contains("api/kiosk/information/update") -> {
                if (request.contains("OsVersion")) {
                    responseString = TestUtils.readJsonFile(SUCCESS_RESPONSE_WITH_ID)
                    code = HttpURLConnection.HTTP_OK
                }
            }
            uri.contains("api/kiosk/11/information") -> {
                responseString = ""
                code = HttpURLConnection.HTTP_OK
            }
            uri.contains("api/kiosk/10/information") -> {
                responseString = TestUtils.readJsonFile(KIOSK_INFO_RESPONSE)
                code = HttpURLConnection.HTTP_OK
            }

            // mock help content
            uri.contains("api/kiosk/help") -> {
                responseString = "{\"content\": \"\"}"
                code = HttpURLConnection.HTTP_OK
            }

            // mock ping kiosk respone
            uri.contains("api/kiosk/ping") -> {
                responseString = TestUtils.readJsonFile(SUCCESS_RESPONSE_WITH_MSG)
                code = HttpURLConnection.HTTP_OK
            }

            // mock health update response
            uri.contains("/api/kiosk/health/update") -> {
                if (request.contains("Scanner")) {
                    responseString = TestUtils.readJsonFile(SUCCESS_RESPONSE_WITH_MSG)
                    code = HttpURLConnection.HTTP_OK
                }
            }

            // mock user journey response
            uri.contains("api/kiosk/user/journey/submit") -> {
                if (request.contains("sessionId")) {
                    responseString = TestUtils.readJsonFile(SUCCESS_RESPONSE_WITH_ID)
                    code = HttpURLConnection.HTTP_OK
                }
            }

            // mock search location
            uri.contains("api/kiosk/cache/location/search") -> {
                responseString = TestUtils.readJsonFile(SEARCH_LOCATION_RESPONSE)
                code = HttpURLConnection.HTTP_OK
            }
            uri.contains("api/kiosk/ota/getcert") -> {
                responseString = TestUtils.readJsonFile(OTA_KEY_CERT_RESPONSE)
                code = HttpURLConnection.HTTP_OK
            }

            // mock submit rating response
            uri.contains("api/kiosk/rating/submit") && request.contains("TestPayerId") -> {
                responseString = ""
                code = HttpURLConnection.HTTP_OK
            }
            uri.contains("api/kiosk/rating/submit") && request.contains("TestFeedBackId") -> {
                responseString = ""
                code = HttpURLConnection.HTTP_OK
            }

            // mock list available promo code response
            uri.contains("api/kiosk/promotion/eligible") -> {
                responseString = TestUtils.readJsonFile(PROMOTION_LIST_RESPONSE)
                code = HttpURLConnection.HTTP_OK
            }

            // mock apply promo code response
            request.contains(API.uriApplyPromoCode) -> {
                if (request.contains("CODE_ERROR")) {
                    responseString = TestUtils.readJsonFile(APPLY_PROMO_CODE_FAILED_RESPONE)
                    code = HttpURLConnection.HTTP_OK
                } else if (request.contains("CODE_SUCCESS")){
                    responseString = TestUtils.readJsonFile(CART_WITH_PROMO_CODE)
                    code = HttpURLConnection.HTTP_OK
                } else {
                    responseString = ""
                    code = HttpURLConnection.HTTP_OK
                }
            }

            // mock remove promocode
            request.contains(API.getUriRemovePromo(TEST_CART_ID)) -> {
                responseString = TestUtils.readJsonFile(CART_WITHOUT_PROMO_CODE)
                code = HttpURLConnection.HTTP_OK
            }
            request.contains(API.getUriRemovePromo("")) -> {
                responseString = ""
                code = HttpURLConnection.HTTP_OK
            }

            // default response
            else -> {
                responseString = ""
                code = HttpURLConnection.HTTP_INTERNAL_ERROR
            }
        }
        val errorCode = if (code == HttpURLConnection.HTTP_OK) {
            0
        } else {
            -1
        }
        return MockResponse().addHeader("errorCode", errorCode.toString())
            .addHeader("content-type", "appication/json")
            .setBody(responseString)
            .setResponseCode(code)

    }

    companion object {
        const val TEST_EKIOSK_HEADER    = "TEST_TOKEN"
        const val TEST_USER_ID  = "31321ec1-fe08-42bf-a57b-2b0ead1d4e02"
        const val TEST_CART_ID  = "Online-Quickbook-{bafaec8f-d21b-4430-94e4-7460767da6d6}"

        const val APPLY_PROMO_CODE_FAILED_RESPONE   = "applyPromoCodeFailed.json"
        const val CART_WITH_PROMO_CODE              = "cartWithPromoCode.json"
        const val CART_WITHOUT_PROMO_CODE           = "cartWithoutPromoCode.json"
        const val AUTHENTICATION_SUCCESS_RESPONSE   = "authenticationSuccessResp.json"
        const val SEARCH_LOCATION_RESPONSE          = "searchLocationResp.json"
        const val ACTIVATE_KIOSK_RESPONSE           = "activateKioskResp.json"
        const val SUCCESS_RESPONSE_WITH_MSG         = "successResponseWithOkMessage.json"
        const val SUCCESS_RESPONSE_WITH_ID          = "updateSuccessMsgWithId.json"
        const val KIOSK_INFO_RESPONSE               = "kioskInfo.json"
        const val REMOVE_ITEM_FROM_CART_RESPONSE    = "removeItemFromCartResp.json"
        const val DELETE_CART_RESPONSE              = "deleteCartResp.json"
        const val QUICKBOOK_FACILITY_RESPONSE       = "quickbookFacilityResp.json"
        const val ADD_COURSE_TO_CART_RESPONSE       = "addCourseToCartResp.json"
        const val ADD_PARTICIPANT_RESPONSE          = "addParticipantResp.json"
        const val DELETE_PARTICIPANT_RESPONSE       = "deleteParticipantResp.json"
        const val OTA_KEY_CERT_RESPONSE             = "otaKeyCert.json"
        const val SCREENSAVER_RESPONSE              = "screensaverResp.json"
        const val PROMOTION_LIST_RESPONSE           = "promoList.json"

    }
}