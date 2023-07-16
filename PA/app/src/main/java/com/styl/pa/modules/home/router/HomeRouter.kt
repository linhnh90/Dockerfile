package com.styl.pa.modules.home.router

import com.styl.pa.R
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.generateToken.Facility
import com.styl.pa.entities.generateToken.Outlet
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.enums.TagName
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.courseDetails.view.CourseDetailsFragment
import com.styl.pa.modules.eventDetails.view.EventDetailsFragment
import com.styl.pa.modules.facilityDetails.view.FacilityDetailsFragment
import com.styl.pa.modules.home.IHomeContact
import com.styl.pa.modules.home.view.HomeFragment
import com.styl.pa.modules.interestGroupDetails.view.InterestGroupDetailsFragment
import com.styl.pa.modules.screenSaver.view.ScreenSaverFragment
import com.styl.pa.modules.search.view.SearchPageFragment

/**
 * Created by Ngatran on 09/11/2018.
 */
class HomeRouter(var view: BaseFragment?) : IHomeContact.IRouter {
    override fun navigationSearchPage(searchType: String?, keyword: String?,
                                      location: Outlet?, classList: ArrayList<ClassInfo>?, isFromSearch: Boolean) {
        val f = SearchPageFragment.newInstance(searchType, keyword, location, classList, isFromSearch)
        val ft = view?.fragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.SearchPageFragment.value)
        ft?.addToBackStack(TagName.SearchPageFragment.value)
        ft?.commit()
        if (view != null) {
            ft?.hide(view!!)
        }
    }

    override fun navigationCourseDetailPage(classInfo: ClassInfo) {
        val f = CourseDetailsFragment.newInstance(classInfo)
        val ft = view?.fragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.CourseDetailsFragment.value)
        ft?.addToBackStack(TagName.CourseDetailsFragment.value)
        ft?.commit()
        if (view != null) {
            ft?.hide(view!!)
        }
    }

    override fun navigationFacilityDetailPage(facility: Facility, outlet: Outlet) {
        val f = FacilityDetailsFragment.newInstance(facility, outlet)
        val ft = view?.fragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.FacilityDetailsFragment.value)
        ft?.addToBackStack(TagName.FacilityDetailsFragment.value)
        ft?.commit()
        if (view != null) {
            ft?.hide(view!!)
        }
    }

    override fun navigationEventDetailPage(eventInfo: EventInfo) {
        val f = EventDetailsFragment.newInstance(eventInfo)
        val ft = view?.fragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.EventDetailsFragment.value)
        ft?.addToBackStack(TagName.EventDetailsFragment.value)
        ft?.commit()
        if (view != null) {
            ft?.hide(view!!)
        }
    }

    override fun navigationIgDetailPage(igInfo: InterestGroup) {
        val f = InterestGroupDetailsFragment.newInstance(igInfo)
        val ft = view?.fragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.InterestGroupDetailsFragment.value)
        ft?.addToBackStack(TagName.InterestGroupDetailsFragment.value)
        ft?.commit()
        if (view != null) {
            ft?.hide(view!!)
        }
    }

    override fun navigationScreenSaverFragment() {
        val f = ScreenSaverFragment()
        f.setTargetFragment(view, HomeFragment.SCREENSAVER_REQ)
        if (view?.fragmentManager != null) {
            f.show(view?.fragmentManager!!, ScreenSaverFragment::class.java.simpleName)
        }
    }
}