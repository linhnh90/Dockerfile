package com.styl.pa.modules.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.styl.pa.R
import com.styl.pa.modules.checkout.view.CheckoutFragment
import com.styl.pa.modules.home.view.HomeFragment
import com.styl.pa.modules.main.view.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by trangpham on 8/29/2018
 */
abstract class BaseFragment : Fragment() {
    fun setTitle(title: String?) {
        (activity as MainActivity).setTitle(title)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (this::class.java != CheckoutFragment::class.java) {
            (activity as MainActivity).addRecord(true)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (fragmentManager?.findFragmentById(R.id.container) !is HomeFragment) {
            (activity as MainActivity).startTimer((activity as MainActivity).TIME_COUNT_DOWN, true)
        } else {
            if (fragmentManager?.findFragmentById(R.id.container) is HomeFragment) {
                (activity as MainActivity).stopCountDownTimerFromHome
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onError = {

                                },
                                onNext = {
                                    (activity as MainActivity).stopTimer()
                                }
                        )
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (activity != null && fragmentManager?.findFragmentById(R.id.container) != null && fragmentManager?.findFragmentById(R.id.container) !is HomeFragment) {
            (activity as MainActivity).setCurrentViewNameFragment()
        }

        if (this::class.java != CheckoutFragment::class.java) {
            (activity as MainActivity).setJourneyViewName((activity as MainActivity).getCurrentView())
            (activity as MainActivity).setIsAdd(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (fragmentManager?.backStackEntryCount == 0 && parentFragment == null) {
            (activity as MainActivity).stopTimer()
        }

        if (this::class.java != CheckoutFragment::class.java) {
            (activity as MainActivity).addRecord(false)
        }
    }

}