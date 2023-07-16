package com.styl.pa.customViews

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatAutoCompleteTextView

class UpOnlyAutoCompleteTextView : AppCompatAutoCompleteTextView {

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr)

    override fun getWindowVisibleDisplayFrame(outRect: Rect) {
        super.getWindowVisibleDisplayFrame(outRect)
        outRect.bottom = -3000 // hack for https://github.com/AndroidSDKSources/android-sdk-sources-for-api-level-23/blob/master/android/widget/PopupWindow.java#L1449
    }
}