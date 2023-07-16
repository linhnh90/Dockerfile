package com.styl.pa.modules.interestGroupDetails.interactor

import com.styl.pa.modules.base.BaseInteractor
import com.styl.pa.modules.interestGroupDetails.InterestGroupDetailsContract
import io.reactivex.disposables.CompositeDisposable

class InterestGroupDetailsInteractor : InterestGroupDetailsContract.Interactor, BaseInteractor() {
    private val compositeDisposable = CompositeDisposable()
    override fun onDestroy() {
        compositeDisposable.clear()
    }
}