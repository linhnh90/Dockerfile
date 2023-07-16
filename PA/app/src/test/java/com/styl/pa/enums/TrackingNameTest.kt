package com.styl.pa.enums

import org.junit.Test

class TrackingNameTest {
    @Test
    fun getTrackingName() {
        var name = TrackingName.HomeFragment
        assert(name.value == "My home page")
        name = TrackingName.AdvancedSearchFragment
        assert(name.value == "Advanced search page")
        name = TrackingName.CartFragment
        assert(name.value == "My cart page")
        name = TrackingName.CheckoutVerificationFragment
        assert(name.value == "My particulars page")
        name = TrackingName.CourseDetailsFragment
        assert(name.value == "Class description page")
        name = TrackingName.FacilityDetailsFragment
        assert(name.value == "Facility description page")
        name = TrackingName.EventDetailsFragment
        assert(name.value == "Event description page")
        name = TrackingName.HelpFragment
        assert(name.value == "Help page")
        name = TrackingName.PaymentFragment
        assert(name.value == "Payment method page")
        name = TrackingName.PaymentSuccessfulFragment
        assert(name.value == "Payment successful page")
        name = TrackingName.RuleAndRegulationsFragment
        assert(name.value == "Rules & Regulations page")
        name = TrackingName.SearchCoursePageFragment
        assert(name.value == "Attend courses main page")
        name = TrackingName.SearchInterestGroupPageFragment
        assert(name.value == "Attend interest group main page")
        name = TrackingName.SearchFacilityPageFragment
        assert(name.value == "Book facilities main page")
        name = TrackingName.SearchEventPageFragment
        assert(name.value == "Book events main page")
        name = TrackingName.SignatureFragment
        assert(name.value == "Payment signature page")
        name = TrackingName.MessageDialogFragment
        assert(name.value == "Error message notification page")
        name = TrackingName.EmptyCartFragment
        assert(name.value == "Empty cart page")
        name = TrackingName.WaitingResultFragment
        assert(name.value == "Waiting page")
        name = TrackingName.CustomerVerificationFragment
        assert(name.value == "Customer verification page")
        name = TrackingName.RatingFragment
        assert(name.value == "Rating page")
        name = TrackingName.AdminCheckFragment
        assert(name.value == "Admin check page")
        name = TrackingName.FeedbackFragment
        assert(name.value == "Feedback page")
        name = TrackingName.IndemnityFragment
        assert(name.value == "Indemnity page")
        name = TrackingName.SettingsFragment
        assert(name.value == "Settings page")
        name = TrackingName.TerminalSettingsFragment
        assert(name.value == "Terminal Settings page")
        name = TrackingName.ScanQrCodeFragment
        assert(name.value == "Scan Qrcode page")
        name = TrackingName.PromoCodeFragment
        assert(name.value == "Promo code page")
    }
}