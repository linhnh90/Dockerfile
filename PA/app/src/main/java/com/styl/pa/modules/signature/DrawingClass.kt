package com.styl.pa.modules.signature

import android.graphics.Path
import android.graphics.Paint
import com.styl.pa.ExcludeFromJacocoGeneratedReport

@ExcludeFromJacocoGeneratedReport
class DrawingClass {
    var DrawingClassPath: Path? = null
    var DrawingClassPaint: Paint? = null

    fun getPath(): Path? {
        return DrawingClassPath
    }

    fun setPath(path: Path) {
        this.DrawingClassPath = path
    }


    fun getPaint(): Paint? {
        return DrawingClassPaint
    }

    fun setPaint(paint: Paint) {
        this.DrawingClassPaint = paint
    }
}