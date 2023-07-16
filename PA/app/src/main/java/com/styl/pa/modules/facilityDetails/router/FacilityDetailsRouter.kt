package com.styl.pa.modules.facilityDetails.router

import com.styl.pa.R
import com.styl.pa.enums.TagName
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.cart.view.CartFragment
import com.styl.pa.modules.facilityDetails.IFacilityDetailsContact
import com.styl.pa.modules.ruleAndRegulations.view.RuleAndRegulationsFragment

class FacilityDetailsRouter(var view: BaseFragment?) : IFacilityDetailsContact.IRouter {
    override fun navigationRuleAndRegulations(rulesUrl: String) {
//        val f = RuleAndRegulationsFragment.newInstance(rulesUrl)
//        val ft = view?.fragmentManager?.beginTransaction()
//        ft?.add(R.id.container, f, RuleAndRegulationsFragment::class.java.simpleName)
//        ft?.addToBackStack(RuleAndRegulationsFragment::class.java.simpleName)
//        ft?.commit()
//        ft?.hide(view)

        val f = RuleAndRegulationsFragment.newInstance(rulesUrl)
        if (view?.fragmentManager != null) {
            f.show(view?.fragmentManager!!, RuleAndRegulationsFragment::class.java.simpleName)
        }
    }

    override fun navigationCardPage() {
        val f = CartFragment()
        val ft = view?.fragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.CartFragment.value)
        ft?.addToBackStack(TagName.CartFragment.value)
        ft?.commit()
        if (view != null) {
            ft?.hide(view!!)
        }
    }
}