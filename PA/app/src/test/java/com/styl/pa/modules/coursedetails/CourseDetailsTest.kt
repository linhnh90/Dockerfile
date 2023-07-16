package com.styl.pa.modules.coursedetails

import android.content.Context
import com.styl.pa.TestBase
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.modules.courseDetails.ICourseDetailsContact
import com.styl.pa.modules.courseDetails.presenter.CourseDetailsPresenter
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock

class CourseDetailsTest: TestBase() {

    private var courseDetailsPresenter: CourseDetailsPresenter? = null
    private var courseDetailsView: ICourseDetailsContact.IView? = null


    @Test(expected = Test.None::class)
    fun onDestroy() {
        courseDetailsView = mock()
        val context: Context = mock()
        courseDetailsPresenter = CourseDetailsPresenter(courseDetailsView, context)
        courseDetailsPresenter?.onDestroy()
    }

    @Test(expected = Test.None::class)
    fun onSuccess_view_null() {
        courseDetailsView = null
        val context: Context = mock()
        courseDetailsPresenter = CourseDetailsPresenter(courseDetailsView, context)
        courseDetailsPresenter?.onSuccess(null)
    }

    @Test(expected = Test.None::class)
    fun onSuccess_view_not_null() {
        courseDetailsView = mock()
        val context: Context = mock()
        courseDetailsPresenter = CourseDetailsPresenter(courseDetailsView, context)
        courseDetailsPresenter?.onSuccess(null)
    }

    @Test(expected = Test.None::class)
    fun onError_view_null() {
        courseDetailsView = null
        val context: Context = mock()
        courseDetailsPresenter = CourseDetailsPresenter(courseDetailsView, context)
        val baseResponse: BaseResponse<ClassInfo> = mock()
        courseDetailsPresenter?.onError(baseResponse)
    }

    @Test(expected = Test.None::class)
    fun onError_view_not_null() {
        courseDetailsView = mock()
        val context: Context = mock()
        courseDetailsPresenter = CourseDetailsPresenter(courseDetailsView, context)
        val baseResponse: BaseResponse<ClassInfo> = mock()
        courseDetailsPresenter?.onError(baseResponse)
    }

}