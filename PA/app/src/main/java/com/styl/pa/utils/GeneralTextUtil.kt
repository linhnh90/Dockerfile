package com.styl.pa.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.res.ResourcesCompat
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.R


/**
 * Created by Ngatran on 09/11/2018.
 */

@ExcludeFromJacocoGeneratedReport
class GeneralTextUtil {

    @ExcludeFromJacocoGeneratedReport
    companion object {
        fun underLineText(string: String, isUnderLine: Boolean): SpannableString {
            var spannableString = SpannableString(string)
            if (isUnderLine) {
                spannableString.setSpan(UnderlineSpan(), 0, string.length, 0)
            }
            return spannableString
        }

        fun underLineAndSetEventText(string: String, start: Int, end: Int,
                                     color: Int, clickableSpan: ClickableSpan): SpannableString {
            val spannableString = SpannableString(string)
            spannableString.setSpan(UnderlineSpan(), start, end, 0)
            spannableString.setSpan(clickableSpan, start, end, 0)
            spannableString.setSpan(ForegroundColorSpan(color), start, end, 0)
            return spannableString
        }

        fun maskText(string: String?, character: Int?, isStart: Boolean): String {
            var result = ""
            if (!TextUtils.isEmpty(string) && character != null) {
                for (i in 0..character - 1) {
                    result += "x"
                }
                if (string!!.length <= character) {
                    return result
                } else {
                    if (isStart) {
                        result += string.substring(character)
                    } else {
                        result = string.substring(0, string.length - character) + result
                    }
                }
            }
            return result
        }

        fun maskTextBetween(string: String?, character: Int?): String {
            var result = ""
            if (!string.isNullOrEmpty() && character != null) {
                var maskResult = ""
                if (string!!.length > character) {
                    for (i in 0..string.length - character - 1) {
                        maskResult += "x"
                    }
                    result += string.substring(0, 3) + maskResult + string.substring(3 - 1 + maskResult.length)
                } else if (string.length <= character && string.length > 2) {
                    for (i in 0..string.length - 2 - 1) {
                        maskResult += "x"
                    }
                    result += string.substring(0, 1) + maskResult + string.substring(maskResult.length)
                } else {
                    result = string
                }
            }
            return result
        }

        fun calculateWidthString(context: Context, text: String, sizeText: Int, font: Int): Int {
            val typeface = ResourcesCompat.getFont(context, font)

            val paint = Paint()
            paint.textSize = context.resources.getDimension(sizeText)
            paint.typeface = typeface
//            paint.color = Color.BLACK
//            paint.style = Paint.Style.FILL
            val result = Rect()
            paint.getTextBounds(text, 0, text.length, result)

            return result.width()
        }

        fun getTextSizeZoomIn(context: Context?, size: Int): Float {
            when(size) {
                R.dimen.text_size_small -> return context?.resources?.getDimension(R.dimen.text_size_mini)?: 0f
                R.dimen.text_size_normal -> return context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                R.dimen.text_size_large -> return context?.resources?.getDimension(R.dimen.text_size_medium)?: 0f
                R.dimen.text_size_xxhlarge -> return context?.resources?.getDimension(R.dimen.text_size_xhlarge)?: 0f
                R.dimen.text_size_lmini -> return context?.resources?.getDimension(R.dimen.text_size_mmini)?: 0f
                R.dimen.text_size_mini -> return context?.resources?.getDimension(R.dimen.text_size_lmini)?: 0f
                R.dimen.text_size_xxxlarge -> return context?.resources?.getDimension(R.dimen.text_size_xxlarge)?: 0f
            }
            return context?.resources?.getDimension(R.dimen.text_size_medium)?: 0f
        }

        fun getTextSizeZoomOut(context: Context?, size: Int): Float {
            when(size) {
                R.dimen.text_size_small -> return context?.resources?.getDimension(R.dimen.text_size_normal)?: 0f
                R.dimen.text_size_normal -> return context?.resources?.getDimension(R.dimen.text_size_xnormal)?: 0f
                R.dimen.text_size_large -> return context?.resources?.getDimension(R.dimen.text_size_xlarge)?: 0f
                R.dimen.text_size_xxhlarge -> return context?.resources?.getDimension(R.dimen.text_size_xxxhlarge)?: 0f
                R.dimen.text_size_lmini -> return context?.resources?.getDimension(R.dimen.text_size_mini)?: 0f
                R.dimen.text_size_mini -> return context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                R.dimen.text_size_xxxlarge -> return context?.resources?.getDimension(R.dimen.text_size_hlarge)?: 0f
            }
            return context?.resources?.getDimension(R.dimen.text_size_large)?: 0f
        }
    }
}