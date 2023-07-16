package com.styl.pa.modules.advancedSearch

import com.styl.pa.entities.advancedSearch.AdvancedSearchRequest
import com.styl.pa.entities.courseCategory.CourseCategory
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.home.ISelectedLocation

/**
 * Created by Ngatran on 10/11/2018.
 */
interface IAdvancedSearchContact {
    interface IView : IBaseContract.IBaseView, ISelectedLocation{
        fun getToken(): String?

        fun setCourseCategoryList(courseCategoryList: ArrayList<CourseCategory>, isClear: Boolean)
    }

    interface IPresenter : IBaseContract.IBasePresenter

    interface IInteractor : IBaseContract.IBaseInteractor

    interface IInteractorOutput<T> : IBaseContract.IBaseInteractorOutput<T>

    interface IRouter : IBaseContract.IBaseRouter {
        fun navigationSearchPage(searchType:String, request: AdvancedSearchRequest?)
    }
}