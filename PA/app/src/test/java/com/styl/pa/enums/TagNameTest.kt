package com.styl.pa.enums

import com.styl.pa.modules.cart.view.CartFragment
import com.styl.pa.modules.cart.view.EmptyCartFragment
import com.styl.pa.modules.checkout.view.CheckoutFragment
import com.styl.pa.modules.courseDetails.view.CourseDetailsFragment
import com.styl.pa.modules.facilityDetails.view.FacilityDetailsFragment
import com.styl.pa.modules.help.HelpFragment
import com.styl.pa.modules.search.view.SearchPageFragment
import com.styl.pa.modules.setting.terminalSetting.view.TerminalSettingsFragment
import org.junit.Test
import org.mockito.kotlin.mock

class TagNameTest {
    @Test
    fun getTagName_CartFragment() {
        val cartFragment: CartFragment = mock()
        val tagName = TagName.getTagName(cartFragment)
        assert(tagName == TagName.CartFragment.value)
    }

    @Test
    fun getTagName_CourseDetailsFragment() {
        val fragment: CourseDetailsFragment = mock()
        val tagName = TagName.getTagName(fragment)
        assert(tagName == TagName.CourseDetailsFragment.value)
    }

    @Test
    fun getTagName_FacilityDetailsFragment() {
        val fragment: FacilityDetailsFragment = mock()
        val tagName = TagName.getTagName(fragment)
        assert(tagName == TagName.FacilityDetailsFragment.value)
    }

    @Test
    fun getTagName_HelpFragment() {
        val fragment: HelpFragment = mock()
        val tagName = TagName.getTagName(fragment)
        assert(tagName == TagName.HelpFragment.value)
    }

    @Test
    fun getTagName_CheckoutFragment() {
        val fragment: CheckoutFragment = mock()
        val tagName = TagName.getTagName(fragment)
        assert(tagName == TagName.CheckoutFragment.value)
    }

    @Test
    fun getTagName_EmptyCartFragment() {
        val fragment: EmptyCartFragment = mock()
        val tagName = TagName.getTagName(fragment)
        assert(tagName == TagName.EmptyCartFragment.value)
    }

    @Test
    fun getTagName_SearchPageFragment() {
        val fragment: SearchPageFragment = mock()
        val tagName = TagName.getTagName(fragment)
        assert(tagName == TagName.SearchPageFragment.value)
    }

    @Test
    fun getTagName_MainFragment() {
        val fragment: TerminalSettingsFragment = mock()
        val tagName = TagName.getTagName(fragment)
        assert(tagName.isNotEmpty())
    }
}