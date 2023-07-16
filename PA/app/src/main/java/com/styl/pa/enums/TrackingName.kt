package com.styl.pa.enums

/**
 * Created by Ngatran on 11/16/2018.
 */
enum class TrackingName {
    HomeFragment("My home page"),
    AdvancedSearchFragment("Advanced search page"),
    CartFragment("My cart page"),
    CheckoutVerificationFragment("My particulars page"),
    CourseDetailsFragment("Class description page"),
    FacilityDetailsFragment("Facility description page"),
    EventDetailsFragment("Event description page"),
    HelpFragment("Help page"),
    PaymentFragment("Payment method page"),
    PaymentSuccessfulFragment("Payment successful page"),
    RuleAndRegulationsFragment("Rules & Regulations page"),
    SearchCoursePageFragment("Attend courses main page"),
    SearchInterestGroupPageFragment("Attend interest group main page"),
    SearchAcrossProductFragment("Search across product main page"),
    SearchFacilityPageFragment("Book facilities main page"),
    SearchEventPageFragment("Book events main page"),
    SignatureFragment("Payment signature page"),
    MessageDialogFragment("Error message notification page"),
    EmptyCartFragment("Empty cart page"),
    WaitingResultFragment("Waiting page"),
    CustomerVerificationFragment("Customer verification page"),
    RatingFragment("Rating page"),
    AdminCheckFragment("Admin check page"),
    FeedbackFragment("Feedback page"),
    IndemnityFragment("Indemnity page"),
    SettingsFragment("Settings page"),
    TerminalSettingsFragment("Terminal Settings page"),
    ScanQrCodeFragment("Scan Qrcode page"),
    PromoCodeFragment("Promo code page");


    constructor(name: String) {
        this.value = name
    }

    var value: String = ""
}