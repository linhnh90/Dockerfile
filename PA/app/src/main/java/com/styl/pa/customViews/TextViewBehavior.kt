package com.styl.pa.customViews

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.utils.LogManager

/**
 * Created by Ngatran on 11/14/2019.
 */
object TextViewBehavior {
    private val DRAWABLE_RIGHT = 2

    private val TAG = TextViewBehavior::class.java.simpleName

    fun deleteEvent(view: EditText?): View.OnTouchListener {
        return object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (MotionEvent.ACTION_UP == event?.action) {
                    val index: Float = ((view?.right
                            ?: 0) - (view?.compoundDrawables?.get(DRAWABLE_RIGHT)?.bounds?.width()
                            ?: 0)) * 1F


                    if (index <= (event.rawX ?: -1F)) {
                        view?.text?.clear()

                        return true
                    }
                }

                return false
            }

        }
    }

    fun changeTextEvent(view: EditText?, context: Context?, startIcon: Int?): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if ((s?.trim()?.length ?: 0) == 0) {
                    invisibleDeleteIcon(view, false, context, startIcon)
                } else {
                    invisibleDeleteIcon(view, true, context, startIcon)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (BuildConfig.DEBUG) {
                    LogManager.i(TAG, "beforeTextChanged")
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (BuildConfig.DEBUG) {
                    LogManager.i(TAG, "onTextChanged")
                }
            }
        }
    }

    fun invisibleDeleteIcon(view: EditText?, isShow: Boolean, context: Context?, startIcon: Int?) {
        val drawables: Array<Drawable>? = view?.compoundDrawables

        if (isShow && drawables != null && drawables.get(DRAWABLE_RIGHT) != null) {
            return
        }

        var drawable: Drawable? = null
        if (isShow) {
            if (startIcon != null) {
                drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_delete2) }
            } else {
                drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_delete3) }
            }
        }

        view?.setCompoundDrawablesWithIntrinsicBounds(
                context?.let { startIcon?.let { it1 -> ContextCompat.getDrawable(it, it1) } },
                null, drawable, null)
    }
}