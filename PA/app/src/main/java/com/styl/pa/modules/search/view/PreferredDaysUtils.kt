package com.styl.pa.modules.search.view

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.styl.pa.R
import com.styl.pa.enums.DaysType

class PreferredDaysUtils : View.OnClickListener {

    var view: View? = null
    var context: Context? = null
    var txtMon: TextView? = null
    var txtTue: TextView? = null
    var txtWed: TextView? = null
    var txtThurs: TextView? = null
    var txtFri: TextView? = null
    var txtSat: TextView? = null
    var txtSun: TextView? = null

    var dayListSelected = ArrayList<Int>()

    constructor(view: View?, context: Context?) {
        this.context = context
        this.view = view
        init()
    }

    fun init() {
        txtMon = view?.findViewById(R.id.txt_mon)
        txtTue = view?.findViewById(R.id.txt_tue)
        txtWed = view?.findViewById(R.id.txt_wed)
        txtThurs = view?.findViewById(R.id.txt_thurs)
        txtFri = view?.findViewById(R.id.txt_fri)
        txtSat = view?.findViewById(R.id.txt_sat)
        txtSun = view?.findViewById(R.id.txt_sun)

        txtMon?.setOnClickListener(this)
        txtTue?.setOnClickListener(this)
        txtWed?.setOnClickListener(this)
        txtThurs?.setOnClickListener(this)
        txtFri?.setOnClickListener(this)
        txtSat?.setOnClickListener(this)
        txtSun?.setOnClickListener(this)
    }

    fun updateView(isSelect: Boolean, textView: TextView?) {
        if (context == null) return
        if (isSelect) {
            textView?.background = ContextCompat.getDrawable(context!!, R.drawable.border_corner_white)
            textView?.setTextColor(ContextCompat.getColor(context!!, R.color.black_color))
        } else {
            textView?.background = ContextCompat.getDrawable(context!!, R.drawable.border_white_bg_trans)
            textView?.setTextColor(ContextCompat.getColor(context!!, R.color.white_color))
        }
        return
    }

    fun selectedDay(day: Int, isSelect: Boolean) {
        var dayType = DaysType.valueOf(day)
        var textView: TextView? = null
        when (dayType) {
            DaysType.MONDAY -> {
                textView = txtMon
            }
            DaysType.TUESDAY -> {
                textView = txtTue
            }
            DaysType.WEDNESDAY -> {
                textView = txtWed
            }
            DaysType.THURSDAY -> {
                textView = txtThurs
            }
            DaysType.FRIDAY -> {
                textView = txtFri
            }
            DaysType.SATURDAY -> {
                textView = txtSat
            }
            DaysType.SUNDAY -> {
                textView = txtSun
            }
            else -> textView = null
        }
        if (isSelect) {
            if (!dayListSelected.contains(day))
                dayListSelected.add(day)
        } else {
            dayListSelected.remove(day)
        }
        updateView(isSelect, textView)
    }

    override fun onClick(p0: View?) {
        var day: Int = -1
        when (p0?.id) {
            R.id.txt_mon -> {
                day = DaysType.MONDAY.value
            }
            R.id.txt_tue -> {
                day = DaysType.TUESDAY.value
            }
            R.id.txt_wed -> {
                day = DaysType.WEDNESDAY.value
            }
            R.id.txt_thurs -> {
                day = DaysType.THURSDAY.value
            }
            R.id.txt_fri -> {
                day = DaysType.FRIDAY.value
            }
            R.id.txt_sat -> {
                day = DaysType.SATURDAY.value
            }
            R.id.txt_sun -> {
                day = DaysType.SUNDAY.value
            }
        }

        if (dayListSelected.contains(day)) {
            selectedDay(day, false)
        } else {
            selectedDay(day, true)
        }
    }

    fun setDays(days: ArrayList<Int>) {
        dayListSelected.clear()
        dayListSelected = days

        for (day in ArrayList(dayListSelected)) {
            selectedDay(day, true)
        }
    }

    fun getDays(): ArrayList<Int> {
        return dayListSelected
    }
}