package com.styl.pa.modules.courseDetails.interactor

import com.styl.pa.modules.base.BaseInteractor
import com.styl.pa.modules.courseDetails.ICourseDetailsContact
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Ngatran on 09/13/2018.
 */
class CourseDetailsInteractor : ICourseDetailsContact.IInteractor, BaseInteractor() {

    private val compositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        compositeDisposable.clear()
    }

}