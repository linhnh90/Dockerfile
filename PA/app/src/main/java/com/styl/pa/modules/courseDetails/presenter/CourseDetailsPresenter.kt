package com.styl.pa.modules.courseDetails.presenter

import android.content.Context
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.modules.courseDetails.ICourseDetailsContact
import com.styl.pa.modules.courseDetails.interactor.CourseDetailsInteractor
import com.styl.pa.modules.home.interactor.HomeInteractor
import com.styl.pa.modules.vacancyChecking.presenter.VacancyCheckingPresenter

/**
 * Created by Ngatran on 09/13/2018.
 */
class CourseDetailsPresenter(var view: ICourseDetailsContact.IView?, var context: Context?) : ICourseDetailsContact.IPresenter, ICourseDetailsContact.IInteractorOutput,
        VacancyCheckingPresenter() {

    private var interactor: CourseDetailsInteractor = CourseDetailsInteractor()
    private var homeInteractor: HomeInteractor = HomeInteractor(null, context)

    init {
        initVacancyPresenter(view, context, homeInteractor)
    }

    override fun onDestroy() {
        interactor.onDestroy()
        view = null
        homeInteractor.onDestroy()
    }

    override fun onSuccess(data: ClassInfo?) {
        view?.dismissLoading()
        view?.reloadDetails(data)
    }

    override fun onError(data: BaseResponse<ClassInfo>) {
        view?.dismissLoading()
        view?.showErrorMessage(data)
    }
}