package com.styl.pa.modules.feedback.presenter

import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.entities.feedback.FeedbackRequest
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.feedback.IFeedbackContact
import com.styl.pa.modules.feedback.interactor.FeedbackInteractor
import com.styl.pa.modules.feedback.router.FeedbackRouter
import com.styl.pa.utils.MySharedPref
import org.jetbrains.annotations.TestOnly

/**
 * Created by Ngatran on 09/27/2019.
 */
class FeedbackPresenter(var view: IFeedbackContact.IView?) : IFeedbackContact.IPresenter {
    private var interactor: FeedbackInteractor? = FeedbackInteractor()
    private var router: FeedbackRouter? = FeedbackRouter(view as? BaseFragment)

    @TestOnly
    constructor(view: IFeedbackContact.IView?, interactor: FeedbackInteractor?): this(view) {
        this.interactor = interactor
    }

    override fun reportFeedBack(request: FeedbackRequest) {
        val sharedPref = getSharePref()
        val token = sharedPref.eKioskHeader

        if (!token.isNullOrEmpty()) {
            interactor?.reportFeedBack(token, request, null)
        }
    }

    fun getSharePref(): MySharedPref {
        return MySharedPref(view?.getContext())
    }

    @ExcludeFromJacocoGeneratedReport
    override fun navigationThankfulnessView() {
        router?.navigationThankfulnessView()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        interactor?.onDestroy()
        view = null
        router = null
    }
}