package com.styl.pa.modules.eventDetails.router

import com.styl.pa.R
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.enums.TagName
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.cart.view.CartFragment
import com.styl.pa.modules.eventDetails.IEventDetailsContact

/**
 * Created by Ngatran on 03/11/2019.
 */
class EventDetailsRouter(val view: BaseFragment?) : IEventDetailsContact.IEventDetailsRouter {

    override fun navigationCardPage(eventInfo: EventInfo?) {
        val f = CartFragment()
        val ft = view?.fragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.CartFragment.value)
        ft?.addToBackStack(TagName.CartFragment.value)
        ft?.commit()
        if (view != null) {
            ft?.hide(view)
        }
    }
}