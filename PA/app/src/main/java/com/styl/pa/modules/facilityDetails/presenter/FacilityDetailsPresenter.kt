package com.styl.pa.modules.facilityDetails.presenter

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import androidx.annotation.VisibleForTesting
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.R
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.facility.BookingByEmailRequest
import com.styl.pa.entities.facility.FacilitySessionInfo
import com.styl.pa.entities.facility.FacilitySessionResponse
import com.styl.pa.entities.rulesAndRegulations.RulesAndRegulationsResponse
import com.styl.pa.entities.sendmail.SendMailResponse
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.facilityDetails.IFacilityDetailsContact
import com.styl.pa.modules.facilityDetails.interactor.FacilityDetailsInteractor
import com.styl.pa.modules.facilityDetails.router.FacilityDetailsRouter
import java.util.*

class FacilityDetailsPresenter(var view: IFacilityDetailsContact.IView?, var context: Context) : IFacilityDetailsContact.IPresenter {

    private var router: FacilityDetailsRouter? = FacilityDetailsRouter(view as? BaseFragment)
    private var interactor: FacilityDetailsInteractor? = FacilityDetailsInteractor()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setInteractor(interactor: FacilityDetailsInteractor?) {
        this.interactor = interactor
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        interactor?.onDestroy()
        view = null
        interactor = null
        router = null
    }

    private var facilitySessionInfoCallback = object : IBaseContract.IBaseInteractorOutput<FacilitySessionResponse> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: FacilitySessionResponse?) {
            view?.dismissLoading()
            if (data != null && !data.mResourceList.isNullOrEmpty()) {
                sortResourceList(data.mResourceList!!)
                view?.onGetFacilitySessionInfoSuccess(data.mResourceList)
            } else {
                view?.onGetFacilitySessionInfoFail()
            }
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<FacilitySessionResponse>) {
            view?.dismissLoading()
            view?.onGetFacilitySessionInfoFail()
        }

    }

    private fun sortResourceList(list: ArrayList<FacilitySessionInfo>) {
        list.sortBy { it.mResourceName }
    }

    override fun getFacilityAvailability(facilityId: String?, selectedDate: String?) {
        val token = view?.getToken()
        if (!token.isNullOrEmpty()) {
            if (facilityId != null && selectedDate != null) {
                view!!.showLoading()
                interactor?.getFacilityAvailability(token,
                        facilityId, selectedDate, facilitySessionInfoCallback
                )
            }
        } else {
            view?.showErrorMessage(R.string.invalid_token)
        }
    }

    private var resultSendEmail = object : IBaseContract.IBaseInteractorOutput<SendMailResponse> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: SendMailResponse?) {
            view?.dismissWaitingDialog()
            view?.sendEmailSuccess()
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<SendMailResponse>) {
            view?.dismissWaitingDialog()
            view?.showErrorMessage(data)
        }

    }

    @ExcludeFromJacocoGeneratedReport
    override fun sendEmail(request: BookingByEmailRequest) {
        if (!TextUtils.isEmpty(view?.getToken())) {
            view?.showWaitingDialog()
            interactor?.sendEmail(view?.getToken()!!, request, resultSendEmail)
        } else {
            view?.showErrorMessage(R.string.invalid_token)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    override fun validate(name: String?, email: String?, mobile: String?, purpose: String?, preferredDate: String?, startDate: String?, distance: String?): Boolean {
        if (name.isNullOrBlank() || email.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() ||
                mobile.isNullOrBlank() || purpose.isNullOrBlank() || preferredDate.isNullOrEmpty() ||
                startDate.isNullOrEmpty() || distance.isNullOrEmpty()) {
            return false
        }
        return true
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationCardPage() {
        router?.navigationCardPage()
    }

    private var resultRulesAndRegulations = object : IBaseContract.IBaseInteractorOutput<RulesAndRegulationsResponse> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: RulesAndRegulationsResponse?) {
            view?.dismissLoading()
            if (data != null && !data.mRulesUrl.isNullOrEmpty()) {
                router?.navigationRuleAndRegulations(data.mRulesUrl!!)
            } else {
                view?.showErrorMessage(R.string.rule_url_empty)
            }
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<RulesAndRegulationsResponse>) {
            view?.dismissLoading()
            view?.showErrorMessage(data)
        }

    }

    override fun getRuleAndRegulations() {
        if (!view?.getToken().isNullOrEmpty()) {
            view!!.showLoading()
            interactor?.getRuleAndRegulations(view!!.getToken()!!, resultRulesAndRegulations)
        } else {
            view?.showErrorMessage(R.string.invalid_token)
        }
    }
}