package com.styl.pa.modules.rating.router

import com.styl.pa.R
import com.styl.pa.entities.rating.RatingRequest
import com.styl.pa.enums.TagName
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.feedback.view.FeedbackFragment
import com.styl.pa.modules.rating.IRatingContact

/**
 * Created by Ngatran on 09/27/2019.
 */
class RatingRouter(var view: BaseFragment?) : IRatingContact.IRouter {
    override fun navigationFeedbackView(request: RatingRequest) {
        val f = FeedbackFragment.newInstance(request)
        val ft = view?.fragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.FeedbackFragment.value)
        ft?.addToBackStack(TagName.FeedbackFragment.value)
        ft?.commit()
        if (view != null) {
            ft?.hide(view!!)
        }
    }
}