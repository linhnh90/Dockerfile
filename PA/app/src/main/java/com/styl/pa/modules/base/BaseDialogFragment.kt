package com.styl.pa.modules.base

import android.content.DialogInterface
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.styl.pa.R
import com.styl.pa.interfaces.TimeOutDialogEvent
import com.styl.pa.modules.advancedSearch.view.AdvancedSearchFragment
import com.styl.pa.modules.customerverification.view.CustomerVerificationFragment
import com.styl.pa.modules.dialog.WaitingResultFragment
import com.styl.pa.modules.home.view.HomeFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.ruleAndRegulations.view.RuleAndRegulationsFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by trangpham on 8/29/2018
 */
open class BaseDialogFragment : DialogFragment(), TimeOutDialogEvent {
    override fun setCurrentName() {
        // Do nothing because this is base fragment, no need to set the name
    }

    private var dialog = ArrayList<DialogFragment>()
    private val compositeDisposable = CompositeDisposable()
    private var dismissFromCountDown = false

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if ((this is AdvancedSearchFragment || this is RuleAndRegulationsFragment || this is WaitingResultFragment || this is CustomerVerificationFragment)
                && activity != null) {
            (activity as MainActivity).addRecord(true)
            (activity as MainActivity).setJourneyViewName((activity as MainActivity).getCurrentView().toString())

            if (dismissFromCountDown) {
                (activity as MainActivity).setIsAdd(false)
            }
        }
    }


    fun onSetEventDismissDialog(dialog: DialogFragment) {
        dismissFromCountDown = false
//        (activity as MainActivity).setCurrentViewName(dialog::class.java.simpleName)
        (activity as MainActivity).addRecord(true)
        (activity as MainActivity).setJourneyViewName((activity as MainActivity).getDialogCurrentView(dialog))

        setCurrentName()
        (activity as MainActivity).dispatchTouchEvent()
        var backDisposable = (activity as MainActivity).backTimerSubject
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = {

                        },
                        onNext = {
                            dialog.dismiss()
                            dismissFromCountDown = true
                        }
                )
        compositeDisposable.add(backDisposable)
        var stopCountDownDisposable = (activity as MainActivity).stopCountDownTimerFromHome
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = {

                        },
                        onNext = {
                            (activity as MainActivity).dispatchTouchEvent()
                        }
                )
        compositeDisposable.add(stopCountDownDisposable)
    }

    fun touchListener() {
        (activity as MainActivity).dispatchTouchEvent()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (fragmentManager?.findFragmentById(R.id.container) is HomeFragment
                && activity != null && activity is MainActivity) {
            (activity as MainActivity).stopCountDownTimerFromHome.onNext(true)
        }
        if (activity != null && activity is MainActivity) {
            (activity as MainActivity).setCurrentViewNameFragment()
        }
        setCurrentName()

        compositeDisposable.clear()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    override fun dismiss() {
        dismissAllowingStateLoss()
    }

}