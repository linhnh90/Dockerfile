package com.styl.pa.modules.courseDetails

import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.vacancyChecking.IVacancyCheckingContact

/**
 * Created by Ngatran on 09/13/2018.
 */
interface ICourseDetailsContact {
    interface IView : IVacancyCheckingContact.IView {

        fun reloadDetails(classInfo: ClassInfo?)
    }

    interface IPresenter : IBaseContract.IBasePresenter

    interface IInteractor : IBaseContract.IBaseInteractor

    interface IInteractorOutput : IBaseContract.IBaseInteractorOutput<ClassInfo>

    interface IRouter : IBaseContract.IBaseRouter
}