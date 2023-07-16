package com.styl.pa.modules.search.router

import com.styl.pa.R
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.courseDetails.view.CourseDetailsFragment
import com.styl.pa.modules.interestGroupDetails.view.InterestGroupDetailsFragment
import com.styl.pa.modules.search.ISearchContact

/**
 * Created by Ngatran on 09/12/2018.
 */
class SearchRouter(var view: BaseFragment?) : ISearchContact.IRouter {
    override fun navigationCourseDetailPage(classInfo: ClassInfo) {
        var f = CourseDetailsFragment.newInstance(classInfo)
        val ft = view?.fragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, CourseDetailsFragment::class.java?.getSimpleName())
        ft?.addToBackStack(CourseDetailsFragment::class.java?.getSimpleName())
        ft?.commit()
        if (view != null) {
            ft?.hide(view!!)
        }
    }

    override fun navigationIgDetailPage(igInfo: InterestGroup) {
        val f = InterestGroupDetailsFragment.newInstance(igInfo)
        val ft = view?.fragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, InterestGroupDetailsFragment::class.java.simpleName)
        ft?.addToBackStack(InterestGroupDetailsFragment::class.java.simpleName)
        ft?.commit()
        if (view != null) {
            ft?.hide(view!!)
        }
    }

}