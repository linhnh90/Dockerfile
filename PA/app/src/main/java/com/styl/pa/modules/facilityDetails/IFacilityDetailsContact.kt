package com.styl.pa.modules.facilityDetails

import android.view.View
import com.styl.pa.entities.facility.BookingByEmailRequest
import com.styl.pa.entities.facility.FacilitySessionInfo
import com.styl.pa.entities.facility.FacilitySessionResponse
import com.styl.pa.entities.rulesAndRegulations.RulesAndRegulationsResponse
import com.styl.pa.entities.sendmail.SendMailResponse
import com.styl.pa.modules.base.IBaseContract

interface IFacilityDetailsContact {
    interface IView : IBaseContract.IBaseView, View.OnClickListener {
        fun onGetFacilitySessionInfoSuccess(facilitySessionInfo: ArrayList<FacilitySessionInfo>?)

        fun onGetFacilitySessionInfoFail()

        fun getToken(): String?

        fun sendEmailSuccess()

        fun showWaitingDialog()

        fun dismissWaitingDialog()
    }

    interface IPresenter : IBaseContract.IBasePresenter {

        fun navigationCardPage()

        fun validate(name: String?, email: String?, mobile: String?, purpose: String?, preferredDate: String?, startDate: String?, distance: String?): Boolean

        fun sendEmail(request: BookingByEmailRequest)

        fun getRuleAndRegulations()

        fun getFacilityAvailability(facilityId: String?, selectedDate: String?)
    }

    interface IInteractor : IBaseContract.IBaseInteractor {

        fun getFacilityAvailability(token: String, facilityId: String, selectedDate: String,
                                    output: IBaseContract.IBaseInteractorOutput<FacilitySessionResponse>
        )

        fun sendEmail(token: String, request: BookingByEmailRequest, event: IBaseContract.IBaseInteractorOutput<SendMailResponse>)

        fun getRuleAndRegulations(token: String, event: IBaseContract.IBaseInteractorOutput<RulesAndRegulationsResponse>)
    }

    interface IInteractorOutput {

    }

    interface IRouter : IBaseContract.IBaseRouter {
        fun navigationCardPage()

        fun navigationRuleAndRegulations(rulesUrl: String)
    }
}