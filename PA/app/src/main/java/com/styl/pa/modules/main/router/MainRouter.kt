package com.styl.pa.modules.main.router

import androidx.fragment.app.FragmentManager
import androidx.appcompat.app.AppCompatActivity
import com.styl.pa.R
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.generateToken.Outlet
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.enums.SearchType
import com.styl.pa.enums.TagName
import com.styl.pa.modules.advancedSearch.view.AdvancedSearchFragment
import com.styl.pa.modules.cart.view.CartFragment
import com.styl.pa.modules.cart.view.EmptyCartFragment
import com.styl.pa.modules.commingSoon.view.ComingSoonFragment
import com.styl.pa.modules.courseDetails.view.CourseDetailsFragment
import com.styl.pa.modules.eventDetails.view.EventDetailsFragment
import com.styl.pa.modules.help.HelpFragment
import com.styl.pa.modules.home.view.HomeFragment
import com.styl.pa.modules.interestGroupDetails.view.InterestGroupDetailsFragment
import com.styl.pa.modules.main.IMainContract
import com.styl.pa.modules.scanQrCode.ScanQrCodeFragment
import com.styl.pa.modules.search.view.SearchPageFragment
import com.styl.pa.modules.setting.SettingsFragment


/**
 * Created by trangpham on 9/3/2018
 */
class MainRouter(var activity: AppCompatActivity?) : IMainContract.IRouter {

    override fun navigationHomeView() {
        if (activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value) == null) {
            val f = HomeFragment()
            val ft = activity?.supportFragmentManager?.beginTransaction()
            ft?.add(R.id.container, f, TagName.HomeFragment.value)
            ft?.commitAllowingStateLoss()
        }

    }

    override fun navigationAttendCoursesView(location: Outlet?,
                                             classList: ArrayList<ClassInfo>?) {
        val f = SearchPageFragment.newInstance(SearchType.COURSES.toString(), "", location, classList, false)
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.SearchPageFragment.value)
        ft?.addToBackStack(TagName.SearchPageFragment.value)
        ft?.commit()

        if (activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value) != null) {
            ft?.hide(activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value)!!)
        }
    }

    override fun navigationBookFacilitiesView(location: Outlet?,
                                              classList: ArrayList<ClassInfo>?) {
        val f = SearchPageFragment.newInstance(SearchType.FACILITIES.toString(), "", location, classList, false)
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.SearchPageFragment.value)
        ft?.addToBackStack(TagName.SearchPageFragment.value)
        ft?.commit()

        if (activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value) != null) {
            ft?.hide(activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value)!!)
        }
    }

    override fun navigationParticipateEvent(location: Outlet?,
                                            classList: ArrayList<ClassInfo>?) {
        val f = SearchPageFragment.newInstance(SearchType.EVENTS.toString(), "", location, classList, false)
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.SearchPageFragment.value)
        ft?.addToBackStack(TagName.SearchPageFragment.value)
        ft?.commit()

        if (activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value) != null) {
            ft?.hide(activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value)!!)
        }
    }

    override fun navigationInterestGroupsView(location: Outlet?, classList: ArrayList<ClassInfo>?) {
        val f = SearchPageFragment.newInstance(SearchType.INTEREST_GROUPS.toString(), "", location, classList, false)
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.SearchPageFragment.value)
        ft?.addToBackStack(TagName.SearchPageFragment.value)
        ft?.commit()

        if (activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value) != null) {
            ft?.hide(activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value)!!)
        }
    }

    override fun navigationAdvancedSearchEvent() {
        var f = AdvancedSearchFragment()
        if (activity?.supportFragmentManager != null) {
            f.show(activity?.supportFragmentManager!!, AdvancedSearchFragment::class.java?.simpleName)
        }
    }

    override fun navigationScanQRView(listener: IMainContract.IView?) {
        val f = ScanQrCodeFragment()
        f.setScanQRListener(listener)
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.ScanQrCodeFragment.value)
        ft?.addToBackStack(TagName.ScanQrCodeFragment.value)
        ft?.commit()
    }

    override fun navigationComingSoonPage() {
        val f = ComingSoonFragment()
        val tagName = ComingSoonFragment::class.java.simpleName
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, tagName)
        ft?.addToBackStack(tagName)

        if (activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value) != null) {
            ft?.hide(activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value)!!)
        }

        ft?.commit()
    }

    override fun navigationCardView() {
//        if (activity?.supportFragmentManager?.findFragmentByTag(CartFragment::class.java?.simpleName) != null) {
//            activity?.supportFragmentManager?.popBackStack(CartFragment::class.java?.simpleName, 0)
//        } else {
        val f = CartFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.CartFragment.value)
        ft?.addToBackStack(TagName.CartFragment.value)

        if (activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value) != null) {
            ft?.hide(activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value)!!)
        }
        ft?.commit()
//        }
    }

    override fun navigationCartView(isBookingMyself: Boolean) {
        val f = CartFragment.newInstance(isBookingMyself)
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.CartFragment.value)
        ft?.addToBackStack(TagName.CartFragment.value)

        if (activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value) != null) {
            ft?.hide(activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value)!!)
        }
        ft?.commit()
    }

    override fun navigationEmptyCardView() {
        val f = EmptyCartFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.EmptyCartFragment.value)
        ft?.addToBackStack(TagName.EmptyCartFragment.value)

        if (activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value) != null) {
            ft?.hide(activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value)!!)
        }

        ft?.commit()
    }

    override fun navigateHelpView(content: String?) {
        val f = HelpFragment.newInstance(content)
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.HelpFragment.value)
        ft?.addToBackStack(TagName.HelpFragment.value)

        val currentView = activity?.supportFragmentManager?.findFragmentById(R.id.container)
        if (currentView != null && currentView::class.java != HelpFragment::class.java) {
            activity?.supportFragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        if (activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value) != null) {
            ft?.hide(activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value)!!)
        }

        ft?.commit()
    }

    override fun navigationCourseDetailPage(classInfo: ClassInfo?) {
        activity?.supportFragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val fragment = CourseDetailsFragment.newInstance(classInfo!!, true)
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.container, fragment, TagName.CourseDetailsFragment.value)
        ft?.addToBackStack(TagName.CourseDetailsFragment.value)
        ft?.commit()
        val currentFragment = activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value)
        if (currentFragment != null) {
            ft?.hide(currentFragment)
        }
    }

    override fun navigateEventDetailPage(eventInfo: EventInfo) {
        activity?.supportFragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val fragment = EventDetailsFragment.newInstance(eventInfo)
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.container, fragment, TagName.EventDetailsFragment.value)
        ft?.addToBackStack(TagName.EventDetailsFragment.value)
        ft?.commit()
        val currentFragment = activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value)
        if (currentFragment != null) {
            ft?.hide(currentFragment)
        }
    }

    override fun navigateIgDetailPage(igInfo: InterestGroup) {
        activity?.supportFragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val fragment = InterestGroupDetailsFragment.newInstance(igInfo)
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.container, fragment, TagName.InterestGroupDetailsFragment.value)
        ft?.addToBackStack(TagName.InterestGroupDetailsFragment.value)
        ft?.commit()
        val currentFragment = activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value)
        if (currentFragment != null) {
            ft?.hide(currentFragment)
        }
    }

    override fun navigateSettingsView() {
        activity?.supportFragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val f = SettingsFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.SettingsFragment.value)
        ft?.addToBackStack(TagName.SettingsFragment.value)

        if (activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value) != null) {
            ft?.hide(activity?.supportFragmentManager?.findFragmentByTag(TagName.HomeFragment.value)!!)
        }
        ft?.commit()
    }
}