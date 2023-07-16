package com.styl.pa.customViews.datePicker

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.widget.DatePicker
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.utils.LogManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.*


/**
 * Created by Ngatran on 10/18/2018.
 */
@SuppressWarnings("kotlin:S1874") // no sonar here
class CustomDatePicker : DatePickerDialog {
    private val compositeDisposable = CompositeDisposable()
    private var getContext: Context? = null

    companion object {
        var MIN_TIME = 2
        var MAX_TIME = 3
    }

    constructor(context: Context?, theme: Int, listener: DatePickerDialog.OnDateSetListener, year: Int, month: Int, day: Int) : super(context, theme, listener, year, month, day) {
        this.getContext = context
        datePicker.calendarViewShown = false
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        try {
            var childPicker = datePicker.findViewById<ViewGroup?>(Resources.getSystem().getIdentifier("month", "id", "android"))
            var parentParam = childPicker?.layoutParams
            parentParam?.width = 150
            childPicker?.layoutParams = parentParam

            childPicker = datePicker.findViewById<ViewGroup?>(Resources.getSystem().getIdentifier("day", "id", "android"))
            parentParam = childPicker?.layoutParams
            parentParam?.width = 150
            childPicker?.layoutParams = parentParam

            childPicker = datePicker.findViewById<ViewGroup?>(Resources.getSystem().getIdentifier("year", "id", "android"))
            parentParam = childPicker?.layoutParams
            parentParam?.width = 150
            childPicker?.layoutParams = parentParam
        } catch (e: Exception) {
            LogManager.i("Error when create custom date picker")
        }


        if (getContext != null) {
            (context as MainActivity).dispatchTouchEvent()
            val backDisposable = (getContext as MainActivity).backTimerSubject
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onError = {

                            },
                            onNext = {
                                dismiss()
                            }
                    )
            compositeDisposable.add(backDisposable)
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        super.onClick(dialog, which)

        if (getContext != null && getContext is MainActivity) {
            (getContext as MainActivity).dispatchTouchEvent()
        }
    }

    override fun onDateChanged(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        super.onDateChanged(view, year, month, dayOfMonth)

        if (getContext != null && getContext is MainActivity) {
            (getContext as MainActivity).dispatchTouchEvent()
        }

        val validDate: Boolean
        if (isCheckMinDate) {
            if (year < minYear) {
                validDate = false
            } else if (year == minYear && month < minMonth) {
                validDate = false
            } else if (year == minYear && month == minMonth && dayOfMonth < minDate) {
                validDate = false
            } else if (year == maxYear && month == maxMonth && dayOfMonth > maxDate) {
                validDate = false
            } else if (year == maxYear && month > maxMonth) {
                validDate = false
            } else if (year > maxYear) {
                validDate = false
            } else {
                validDate = true
            }

            if (validDate) {
                currentYear = year
                currentMonth = month
                currentDate = dayOfMonth
            } else {
                updateDate(currentYear, currentMonth, currentDate)
            }
        }
    }

    fun setCurrentSelection(calendar: Calendar) {
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    }

    override fun dismiss() {
        super.dismiss()
        if (getContext != null && getContext is MainActivity) {
            (getContext as MainActivity).stopCountDownTimerFromHome.onNext(true)
        }
        compositeDisposable.clear()
    }

    fun setMinDate(minDate: Date) {
        val c = Calendar.getInstance()
        c.time = minDate
        c.set(Calendar.DATE, c.get(Calendar.DATE))
        c.set(Calendar.HOUR_OF_DAY, 0)
        this.datePicker.minDate = c.time.time - 10000
    }

    fun setMaxDate(maxDate: Date) {
        val c = Calendar.getInstance()
        c.time = maxDate
        c.set(Calendar.DATE, c.get(Calendar.DATE))
        c.set(Calendar.HOUR_OF_DAY, 23)
        this.datePicker.maxDate = c.time.time + 10000
    }


    private var isCheckMinDate = false
    private var minYear = 0
    private var minMonth = 0
    private var minDate = 0

    private var maxYear = 0
    private var maxMonth = 0
    private var maxDate = 0

    private var currentYear = 0
    private var currentMonth = 0
    private var currentDate = 0
    fun setLimitDate(mMinDate: Date, mMaxDate: Date, mCurrentDate: Date) {
        isCheckMinDate = true

        val c = Calendar.getInstance()

        c.time = mCurrentDate

        currentYear = c.get(Calendar.YEAR)
        currentMonth = c.get(Calendar.MONTH)
        currentDate = c.get(Calendar.DAY_OF_MONTH)

        c.time = mMinDate
        minYear = c.get(Calendar.YEAR)
        minMonth = c.get(Calendar.MONTH)
        minDate = c.get(Calendar.DAY_OF_MONTH)

        c.time = mMaxDate
        maxYear = c.get(Calendar.YEAR)
        maxMonth = c.get(Calendar.MONTH)
        maxDate = c.get(Calendar.DAY_OF_MONTH)
    }

    fun setDate(mMinDate: Date, mMaxDate: Date, mCurrentDate: Date) {
        val cal = Calendar.getInstance()
        cal.time = mCurrentDate
        setCurrentSelection(cal)
        this.datePicker.minDate = 0
        this.datePicker.maxDate = 0
        setMaxDate(mMaxDate)
        setMinDate(mMinDate)
    }
}