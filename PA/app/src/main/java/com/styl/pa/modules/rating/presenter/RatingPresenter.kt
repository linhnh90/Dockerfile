package com.styl.pa.modules.rating.presenter

import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.entities.rating.RatingRequest
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.rating.IRatingContact
import com.styl.pa.modules.rating.interactor.RatingInteractor
import com.styl.pa.modules.rating.router.RatingRouter
import com.styl.pa.utils.MySharedPref
import org.jetbrains.annotations.TestOnly

/**
 * Created by Ngatran on 03/22/2019.
 */
class RatingPresenter(var view: IRatingContact.IView?) : IRatingContact.IPresenter {
    private var interactor: RatingInteractor? = RatingInteractor()
    private var router: RatingRouter? = RatingRouter(view as? BaseFragment)

    @TestOnly
    constructor(view: IRatingContact.IView?, interactor: RatingInteractor?): this(view) {
        this.interactor = interactor
    }

    override fun reportRatingFeedBack(request: RatingRequest) {
        val token = getSharePref().eKioskHeader

        if (!token.isNullOrEmpty()) {
            interactor?.reportRatingFeedBack(token, request, null)
        }
    }

    fun getSharePref(): MySharedPref {
        return MySharedPref(view?.getContext())
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationFeedbackView(request: RatingRequest) {
        router?.navigationFeedbackView(request)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        interactor?.onDestroy()
        view = null
        router = null
    }
}