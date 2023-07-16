package com.styl.pa.modules.feedback

import android.content.Context
import com.styl.pa.entities.feedback.FeedbackRequest
import com.styl.pa.entities.sendmail.SendMailResponse
import com.styl.pa.modules.base.IBaseContract

/**
 * Created by Ngatran on 09/27/2019.
 */
interface IFeedbackContact {
    interface IView : IBaseContract.IBaseView {
        fun getContext(): Context?
    }

    interface IPresenter : IBaseContract.IBasePresenter {
        fun reportFeedBack(request: FeedbackRequest)
        fun navigationThankfulnessView()
    }

    interface IInteractor : IBaseContract.IBaseInteractor {
        fun reportFeedBack(token: String, request: FeedbackRequest, output: IBaseContract.IBaseInteractorOutput<SendMailResponse>?)
    }

    interface IRouter : IBaseContract.IBaseRouter {
        fun navigationThankfulnessView()
    }
}