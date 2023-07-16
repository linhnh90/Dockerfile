package com.styl.pa.modules.rating

import android.content.Context
import com.styl.pa.entities.rating.RatingRequest
import com.styl.pa.entities.sendmail.SendMailResponse
import com.styl.pa.modules.base.IBaseContract

/**
 * Created by Ngatran on 03/22/2019.
 */
interface IRatingContact {

    interface IView : IBaseContract.IBaseView {
        fun backHomeView()
        fun getContext(): Context?
    }

    interface IPresenter : IBaseContract.IBasePresenter {
        fun reportRatingFeedBack(request: RatingRequest)
        fun navigationFeedbackView(request: RatingRequest)
    }

    interface IInteractor : IBaseContract.IBaseInteractor {
        fun reportRatingFeedBack(token: String, request: RatingRequest, output: IBaseContract.IBaseInteractorOutput<SendMailResponse>?)
    }

    interface IRouter : IBaseContract.IBaseRouter {
        fun navigationFeedbackView(request: RatingRequest)
    }
}