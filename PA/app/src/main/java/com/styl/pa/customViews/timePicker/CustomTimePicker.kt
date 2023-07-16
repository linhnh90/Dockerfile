package com.styl.pa.customViews.timePicker

import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.widget.AppCompatTextView
import android.view.Gravity
import android.widget.NumberPicker
import android.widget.TimePicker
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.utils.LogManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers


/**
 * Created by Ngatran on 10/10/2018.
 */
class CustomTimePicker : TimePickerDialog {
    private var mMinHour = -1
    private var mMinMinute = -1
    private var mMaxHour = 100
    private var mMaxMinute = 100
    private var mCurrentHour: Int = 0
    private var mCurrentMinute: Int = 0

    private var getContext: Context? = null
    private val compositeDisposable = CompositeDisposable()

    constructor(context: Context, theme: Int, listener: OnTimeSetListener, hour: Int, minute: Int, is24hours: Boolean) : super(context, theme, listener, hour, minute, is24hours) {
        this.getContext = context
        mCurrentHour = hour
        mCurrentMinute = minute

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (getContext != null) {

            (context as MainActivity).dispatchTouchEvent()
            var backDisposable = (getContext as MainActivity).backTimerSubject
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

    fun setMin(hour: Int, minute: Int) {
        mMinHour = hour
        mMinMinute = minute
    }

    fun setMax(hour: Int, minute: Int) {
        mMaxHour = hour
        mMaxMinute = minute
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        super.onClick(dialog, which)

        if (getContext != null && getContext is MainActivity) {
            (getContext as MainActivity).dispatchTouchEvent()
        }
    }

    override fun onTimeChanged(view: TimePicker, hourOfDay: Int, minute: Int) {
        super.onTimeChanged(view, hourOfDay, minute)

        if (getContext != null && getContext is MainActivity) {
            (getContext as MainActivity).dispatchTouchEvent()
        }

        val validTime: Boolean
        if (hourOfDay < mMinHour || hourOfDay == mMinHour && minute < mMinMinute || hourOfDay > mMaxHour || hourOfDay == mMaxHour && minute > mMaxMinute) {
            validTime = false
        } else {
            validTime = true
        }
        if (validTime) {
            mCurrentHour = hourOfDay
            mCurrentMinute = minute
        } else {
            updateTime(mCurrentHour, mCurrentMinute)
        }
    }

    override fun show() {
        super.show()
        initView()
    }

    fun initView() {
        try {
            val mTimePicker: TimePicker = findViewById(Resources.getSystem().getIdentifier("timePicker", "id", "android"))

            val childHourPicker = mTimePicker.findViewById<NumberPicker?>(Resources.getSystem().getIdentifier("hour", "id", "android"))
            var parentParam = childHourPicker?.layoutParams
            parentParam?.width = 200
            childHourPicker?.layoutParams = parentParam

            val childDividerPicker = mTimePicker.findViewById<AppCompatTextView?>(Resources.getSystem().getIdentifier("divider", "id", "android"))
            parentParam = childDividerPicker?.layoutParams
            parentParam?.width = 50
            childDividerPicker?.layoutParams = parentParam
            childDividerPicker?.gravity = Gravity.CENTER

            val childMinutePicker = mTimePicker.findViewById<NumberPicker?>(Resources.getSystem().getIdentifier("minute", "id", "android"))
            parentParam = childMinutePicker?.layoutParams
            parentParam?.width = 200
            childMinutePicker?.layoutParams = parentParam
        } catch (e: Exception) {
            LogManager.i("Error when create custom time picker")
        }

    }

    override fun dismiss() {
        super.dismiss()
        if (getContext != null && getContext is MainActivity) {
            (getContext as MainActivity).stopCountDownTimerFromHome.onNext(true)
        }
        compositeDisposable.clear()
    }
}