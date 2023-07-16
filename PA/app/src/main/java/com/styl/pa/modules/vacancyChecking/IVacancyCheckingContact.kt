package com.styl.pa.modules.vacancyChecking

import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.modules.base.IBaseContract

/**
 * Created by Ngatran on 10/15/2019.
 */
interface IVacancyCheckingContact {
    interface IView : IBaseContract.IBaseView {
        fun showErrorMessage(titleResId: Int, messageResId: Int)

        fun handleCourseEvent(classInfo: ClassInfo, isAddCart: Boolean, isQuickBook: Boolean)
        fun handleIgEvent(igInfo: InterestGroup, isAddCart: Boolean, isQuickBook: Boolean)
    }

    interface IPresenter {
        fun checkClassVacancy(classInfo: ClassInfo, isAddCart: Boolean, isQuickBook: Boolean)
        fun checkIgVacancy(igInfo: InterestGroup, isAddCart: Boolean, isQuickBook: Boolean)
    }

}