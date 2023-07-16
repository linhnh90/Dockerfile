package com.styl.pa.modules.home.presenter

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.generateToken.Outlet
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.entities.recommendatetions.RecommendationResponse
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.home.IHomeContact
import com.styl.pa.modules.home.interactor.HomeInteractor
import com.styl.pa.modules.home.router.HomeRouter
import com.styl.pa.modules.vacancyChecking.presenter.VacancyCheckingPresenter

/**
 * Created by Ngatran on 09/11/2018.
 */
class HomePresenter(var view: IHomeContact.IView?, var context: Context) : IHomeContact.IPresenter, IHomeContact.IInteractorOutput,
        VacancyCheckingPresenter() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var interactor: HomeInteractor? = HomeInteractor(this, context)

    private var router: HomeRouter? = HomeRouter(view as? BaseFragment)

    init {
        initVacancyPresenter(view, context, interactor)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationSearchPage(searchType: String?, keyword: String?, location: Outlet?,
                                      classList: ArrayList<ClassInfo>?, isFromSearch: Boolean) {
        router?.navigationSearchPage(searchType, keyword, location, classList, isFromSearch)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationCourseDetailPage(classInfo: ClassInfo) {
        router?.navigationCourseDetailPage(classInfo)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationIgDetailPage(igInfo: InterestGroup) {
        router?.navigationIgDetailPage(igInfo)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationEventDetailPage(eventInfo: EventInfo) {
        router?.navigationEventDetailPage(eventInfo)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationScreenSaverFragment() {
        router?.navigationScreenSaverFragment()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        interactor?.onDestroy()
        view = null
        interactor = null
        router = null
    }


    private var searchClassCallback = object : IBaseContract.IBaseInteractorOutput<RecommendationResponse> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: RecommendationResponse?) {
            view?.dismissLoading()
            view?.onGetClassByOutletSuccess(data)
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<RecommendationResponse>) {
            view?.dismissLoading()
        }

    }

    fun getRecommendation() {
        interactor?.searchClass(searchClassCallback)
    }
}