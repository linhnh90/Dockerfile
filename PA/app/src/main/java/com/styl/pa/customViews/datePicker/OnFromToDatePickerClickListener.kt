package com.styl.pa.customViews.datePicker

interface OnFromToDatePickerClickListener {
    fun onFromClick(milliseconds: Long)

    fun onToClick(milliseconds: Long)
}