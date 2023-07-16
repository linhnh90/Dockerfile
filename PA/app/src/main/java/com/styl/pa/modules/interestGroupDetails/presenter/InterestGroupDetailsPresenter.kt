package com.styl.pa.modules.interestGroupDetails.presenter

import android.content.Context
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.modules.home.interactor.HomeInteractor
import com.styl.pa.modules.interestGroupDetails.InterestGroupDetailsContract
import com.styl.pa.modules.interestGroupDetails.interactor.InterestGroupDetailsInteractor
import com.styl.pa.modules.vacancyChecking.presenter.VacancyCheckingPresenter

class InterestGroupDetailsPresenter(
    var view: InterestGroupDetailsContract.View?,
    var context: Context?
) : InterestGroupDetailsContract.Presenter,InterestGroupDetailsContract.InteractorOutput,
    VacancyCheckingPresenter() {

    private var interactor = InterestGroupDetailsInteractor()
    private var homeInteractor: HomeInteractor = HomeInteractor(null, context)

    init {
        initVacancyPresenter(view, context, homeInteractor)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        interactor.onDestroy()
        view = null
        homeInteractor.onDestroy()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onSuccess(data: InterestGroup?) {
        view?.dismissLoading()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onError(data: BaseResponse<InterestGroup>) {
        view?.dismissLoading()
        view?.showErrorMessage(data)
    }
}