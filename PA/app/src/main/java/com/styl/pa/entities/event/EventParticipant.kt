package com.styl.pa.entities.event

import com.styl.pa.entities.cart.Participant
import com.styl.pa.entities.customer.CustomerInfo

class EventParticipant {
    var externalLineId: String? = null
    var componentId: String? = null
    var userId: String? = null
    var isFullFillInfo: Boolean = false
    var isNew: Boolean = true
    var nameToShow: String? = null
    var id: String? = null
    var customerInfo: CustomerInfo? = null
    var participantInfo: Fields? = null
    var participantAfterLoadCart: Participant? = null
    var beforeDiscountAmount: Float? = null //subTotal
    var discountAmount: Float? = null //discountTotal
    var paymentAmount: Float? = null //grandTotal
    var promoDiscountAmount: Float? = null
    var outletName: String? = null
}