package com.styl.pa.entities.api

/**
 * Created by Ngatran on 09/27/2018.
 */
class API {
    companion object {
        var uriGetCustomerID = "customers/idnos/{idNo}"
        var uriGetCustomerInfo = "customers/{idType}/{idNo}"
        var uriDeleteReservation = "bookings/{reservationId}"
        var generateReceipt = "bookings/receipts"
        var uriGetClassCode = "classes?classcode={classcode}"
        var uriGetEventCode = "events?eventcode={eventcode}"
        var uriGetFacilitySession = "outlets/{outletid}/resourcesubtypes/{resourcesubtypeid}/dates/{date}"
        var uriGetCourseCategoryList = "courses/categories?pageIndex={pageIndex}&pageSize={pageSize}"
        var uriGetEventCategoryList = "eventcategories?pageIndex={pageIndex}&pageSize={pageSize}"
        val classDetails = "classDetails?classid={classId}"
        val eventSearch = "events?outletid={outletid}&pageIndex={pageIndex}&pageSize={pageSize}"
        val uriGetClassId = "classes?ispublic=true&registerviaportal=true&classid={classId}"
        val uriGetEventId = "events?eventid={eventId}&pageIndex=1&pageSize=10"
        val uriGetClassVacancy = "classVacancies"
        val uriGetEventVacancy = "events/vacancies"


        //new APIs
        val uriGetCategories = "common/catalog/categories?type={type}&id={id}"
        val uriLoadCart = "common/ShoppingCart/Get/{cartId}"
        val uriDeleteCart = "common/ShoppingCart/Delete/{cartId}"
        val uriQuickBookFacility = "common/Facilities/QuickBook"
        val uriQuickBookCourse = "common/Course/QuickBook"
        val uriQuickBookIg = "common/Ig/QuickBookIg"
        val uriQuickBookEvent = "common/Events/QuickBookEvent"
        val uriAddEventToCart = "common/Events/AddEventToCart"
        val uriAddCourseToCart = "common/Course/AddCourse"
        val uriAddIgToCart = "common/Ig/AddIg"
        val uriRemoveItemFromCart = "common/Course/Remove"
        val uriRemoveEventItemFromCart = "common/Events/RemoveEventTicketFromCart"
        val uriRemoveItemIgFromCart = "common/Ig/Remove"
        val uriAddParticipant = "common/Course/AddParticipant"
        val uriAddParticipantIg = "common/IG/AddIgParticipant"
        val uriAddEventParticipant = "common/Events/AddEventParticipant"
        val uriDeleteParicipant = "common/Course/DeleteParticipant"
        val uriDeleteParicipantIg = "common/IG/DeleteIgParticipant"
        val uriGetCustomerDetail = "common/Customers/{Id}"
        val uriGetFacilityAvailability = "common/facilities/GetFacilitySlots?selectedFacility={facilityId}&selectedDate={date}"
        val uriGetCourseAvailability = "common/course/CheckVacancy"
        val uriGetIgAvailability = "common/Ig/CheckVacancy"
        val uriGetEventAvailability = "common/Events/CheckVacancy"
        val uriPrepareCheckout = "common/ShoppingCart/PrepareCartCheckout"
        val uriCreateOrder = "common/Orders/Create"
        val uriApplyPromoCode = "common/Promo/ApplyPromo"
        val uriRemovePromoCode = "common/Promo/RemovePromo/{cartId}"
        val uriGetPaymentOptions = "payment/ClientApi/PaymentOptions"
        val uriCreatePaymentReference = "payment/ClientApi/PaymentReference"
        val uriUpdatePaymentReference = "payment/ClientApi/UpdatePaymentReference"
        val uriCheckPaymentStatus = "payment/ClientApi/GetPaymentStatus?referenceId={referenceId}"
        val uriCheckResidency = "common/Customers/CheckResidency?residentialAddressPostalCode={residentialAddressPostalCode}&committeePostalCode={committeePostalCode}"

        val apiOtaDownload = "api/ota/download"

        fun getUriRemovePromo(cartId: String): String {
            return uriRemovePromoCode.replace("{cartId}", cartId)
        }

        fun loadCart(cartId: String): String {
            return uriLoadCart.replace("{cartId}", cartId)
        }

        fun deleteCart(cartId: String): String {
            return uriDeleteCart.replace("{cartId}", cartId)
        }

        fun getCustomerDetail(id: String?): String {
            return uriGetCustomerDetail.replace("{Id}", id ?: "")
        }

        fun getFacilityAvailability(facilityId: String, date: String) : String {
            return uriGetFacilityAvailability.replace("{facilityId}", facilityId)
                    .replace("{date}", date)
        }

        fun checkPaymentStatus(referenceId: String?): String {
            return uriCheckPaymentStatus.replace("{referenceId}", referenceId ?: "")
        }


        fun getCustomerIDURL(idNo: String): String {
            return uriGetCustomerID.replace("{idNo}", idNo)
        }

        fun getCustomerInfoURL(idType: String, idNo: String): String {
            return uriGetCustomerInfo.replace("{idType}", idType).replace("{idNo}", idNo)
        }

        fun getClassCode(classCode: String): String {
            return uriGetClassCode.replace("{classcode}", classCode)
        }

        fun getEventCode(eventCode: String): String {
            return uriGetEventCode.replace("{eventcode}", eventCode)
        }

        fun getFacilitySession(outletID: String, resourceTypeId: String, date: String): String {
            return uriGetFacilitySession.replace("{outletid}", outletID).replace("{resourcesubtypeid}", resourceTypeId).replace("{date}", date)
        }

        fun deleteReservation(reservationId: String): String {
            return uriDeleteReservation.replace("{reservationId}", reservationId)
        }

        fun getCategories(type: String, id: String): String {
            return uriGetCategories.replace("{type}", type)
                    .replace("{id}", id)
        }

        fun getCourseCategoryList(pageIndex: Int, pageSize: Int): String {
            return uriGetCourseCategoryList.replace("{pageIndex}", pageIndex.toString()).replace("{pageSize}", pageSize.toString())
        }

        fun getEventCategoryList(pageIndex: Int, pageSize: Int): String {
            return uriGetEventCategoryList.replace("{pageIndex}", pageIndex.toString()).replace("{pageSize}", pageSize.toString())
        }

        fun getClassDetails(classId: String?): String {
            return classDetails.replace("{classId}", classId ?: "")
        }

        fun getEventSearch(outletId: String?, pageIndex: Int, pageSize: Int): String {
            return eventSearch.replace("{outletid}", outletId
                    ?: "").replace("{pageIndex}", pageIndex.toString()).replace("{pageSize}", pageSize.toString())
        }

        fun getClassId(classId: String): String {
            return uriGetClassId.replace("{classId}", classId)
        }

        fun getEventId(eventId: String): String {
            return uriGetEventId.replace("{eventId}", eventId)
        }

        fun getUriCheckResidency(addressPostalCode: String, committeePostalCode: String): String {
            return uriCheckResidency.replace("{residentialAddressPostalCode}", addressPostalCode)
                .replace("{committeePostalCode}", committeePostalCode)
        }
    }
}