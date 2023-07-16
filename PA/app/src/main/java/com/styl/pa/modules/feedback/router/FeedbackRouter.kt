package com.styl.pa.modules.feedback.router

import com.styl.pa.R
import com.styl.pa.enums.TagName
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.feedback.IFeedbackContact
import com.styl.pa.modules.thankfulness.view.ThankfulnessFragment

/**
 * Created by Ngatran on 09/27/2019.
 */
class FeedbackRouter(var view: BaseFragment?) : IFeedbackContact.IRouter {
    override fun navigationThankfulnessView() {
        val f = ThankfulnessFragment()
        val ft = view?.fragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.FeedbackFragment.value)
        ft?.addToBackStack(TagName.FeedbackFragment.value)
        ft?.commit()
        if (view != null) {
            ft?.hide(view!!)
        }
    }
}