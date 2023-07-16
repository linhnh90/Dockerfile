package com.styl.pa.modules.dialog.scanNric

import androidx.fragment.app.FragmentManager
import com.styl.pa.R
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.enums.TagName
import com.styl.pa.modules.base.CustomBaseFragment
import com.styl.pa.modules.courseDetails.view.CourseDetailsFragment

class ScanNricRouter(var view: CustomBaseFragment?) : IScanNricContact.IRouter {

    override fun navigationCourseDetailPage(classInfo: ClassInfo?) {
        view?.fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val f = CourseDetailsFragment.newInstance(classInfo!!)
        val ft = view?.fragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.CourseDetailsFragment.value)
        ft?.addToBackStack(TagName.CourseDetailsFragment.value)
        ft?.commit()
        val currentFragment = view?.fragmentManager?.findFragmentByTag(TagName.HomeFragment.value)
        if (currentFragment != null) {
            ft?.hide(currentFragment)
        }
    }
}