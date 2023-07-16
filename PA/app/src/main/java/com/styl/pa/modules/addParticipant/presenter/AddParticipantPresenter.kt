package com.styl.pa.modules.addParticipant.presenter

import androidx.core.util.PatternsCompat
import androidx.fragment.app.DialogFragment
import com.styl.pa.BuildConfig
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.R
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.modules.addParticipant.AddParticipantContract
import com.styl.pa.modules.addParticipant.router.AddParticipantRouter
import com.styl.pa.modules.addParticipant.view.AddParticipantFragment
import org.jetbrains.annotations.TestOnly

class AddParticipantPresenter(var view: AddParticipantContract.View?) :
    AddParticipantContract.Presenter {

    @TestOnly
    constructor(view: AddParticipantContract.View?, router: AddParticipantRouter?) : this(view) {
        this.router = router
    }

    @ExcludeFromJacocoGeneratedReport
    private var router: AddParticipantRouter? = AddParticipantRouter(view as? DialogFragment)

    companion object {
        const val ADD_PARTICIPANT_FOR_FACILITY_REQUEST_CODE = 102
        const val ARG_PARTICIPANT_INFO = BuildConfig.APPLICATION_ID + "args.ARG_PARTICIPANT_INFO"
        const val ERROR_STR = "error"
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigateToCartPage(participantInfo: CustomerInfo?) {
        this.router?.navigateToCartPage(
            requestCode = ADD_PARTICIPANT_FOR_FACILITY_REQUEST_CODE,
            participantInfo = participantInfo
        )
    }

    override fun validateInput(fullName: String, phone: String, email: String) {
        var errorFullName = ""
        val context = (view as? AddParticipantFragment)?.activity
        if (fullName.isEmpty()) {
            errorFullName = context?.getString(R.string.please_enter_a_name) ?: ERROR_STR
        }

        var errorPhone = ""
        if (phone.isEmpty()) {
            errorPhone = context?.getString(R.string.please_enter_a_mobile_number) ?: ERROR_STR
        }

        var errorEmail = ""
        if (email.isEmpty()) {
            errorEmail = context?.getString(R.string.please_enter_a_email_address) ?: ERROR_STR
        } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()){
            errorEmail = context?.getString(R.string.please_enter_correct_email_address) ?: ERROR_STR
        }

        if (errorFullName.isEmpty() && errorPhone.isEmpty() && errorEmail.isEmpty()) {
            val facilityParticipant = CustomerInfo()
            facilityParticipant.mFullName = fullName
            facilityParticipant.mEmail = email
            facilityParticipant.mMobile = phone
            navigateToCartPage(facilityParticipant)
        } else {
            view?.showErrorInput(
                errorFullName = errorFullName,
                errorPhone = errorPhone,
                errorEmail = errorEmail
            )
        }
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        view = null
        router = null
    }
}