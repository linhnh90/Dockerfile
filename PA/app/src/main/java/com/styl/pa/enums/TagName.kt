package com.styl.pa.enums

import androidx.fragment.app.Fragment

/**
 * Created by Ngatran on 11/20/2018.
 */
enum class TagName {
    HomeFragment("PAHomeFragment"),
    CartFragment("PACardFragment"),
    CheckoutVerificationFragment("PACheckoutVerificationFragment"),
    CourseDetailsFragment("PACourseDetailsFragment"),
    InterestGroupDetailsFragment("PAInterestGroupDetailsFragment"),
    FacilityDetailsFragment("PAFacilityDetailsFragment"),
    EventDetailsFragment("PAEventDetailsFragment"),
    HelpFragment("PAHelpFragment"),
    PaymentSuccessfulFragment("PAPaymentSuccessfulFragment"),
    SearchPageFragment("PASearchPageFragment"),
    SignatureFragment("PASignatureFragment"),
    EmptyCartFragment("PAEmptyCartFragment"),
    CheckoutFragment("PACheckoutFragment"),
    RatingFragment("PARatingFragment"),
    FeedbackFragment("PAFeedbackFragment"),
    IndemnityFragment("PAIndemnityFragment"),
    SettingsFragment("SettingsFragment"),
    TerminalSettingsFragment("TerminalSettingsFragment"),
    ScanQrCodeFragment("ScanQrCodeFragment"),
    PaymentFragment("PaymentFragment");

    constructor(name: String) {
        this.value = name
    }

    var value: String = ""

    companion object {
        fun getTagName(mainFragment: Fragment): String {
            if (mainFragment is com.styl.pa.modules.cart.view.CartFragment) {
                return CartFragment.value
            } else if (mainFragment is com.styl.pa.modules.courseDetails.view.CourseDetailsFragment) {
                return CourseDetailsFragment.value
            } else if (mainFragment is com.styl.pa.modules.facilityDetails.view.FacilityDetailsFragment) {
                return FacilityDetailsFragment.value
            } else if (mainFragment is com.styl.pa.modules.help.HelpFragment) {
                return HelpFragment.value
            } else if (mainFragment is com.styl.pa.modules.checkout.view.CheckoutFragment) {
                return CheckoutFragment.value
            } else if (mainFragment is com.styl.pa.modules.search.view.SearchPageFragment) {
                return SearchPageFragment.value
            } else if (mainFragment is com.styl.pa.modules.cart.view.EmptyCartFragment) {
                return EmptyCartFragment.value
            }

            return mainFragment::class.java.simpleName
        }
    }
}