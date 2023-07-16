package com.styl.pa.modules.signature

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import java.util.ArrayList
import com.styl.pa.R


@ExcludeFromJacocoGeneratedReport
internal class SketchSheetView(context: Context?) : View(context) {

    private val DrawingClassArrayList = ArrayList<DrawingClass>()
    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null
    private var path2: Path? = null
    private var paint: Paint? = null

    init {

        initLinePath()

        bitmap = Bitmap.createBitmap(820, 480, Bitmap.Config.ARGB_4444)

        canvas = Canvas(bitmap)

        this.setBackgroundColor(Color.WHITE)
    }

    fun initLinePath() {
        paint = Paint()

        path2 = Path()

        paint?.setDither(true)

        paint?.setColor(ContextCompat.getColor(context, R.color.black_color))

        paint?.setStyle(Paint.Style.STROKE)

        paint?.setStrokeJoin(Paint.Join.ROUND)

        paint?.setStrokeCap(Paint.Cap.ROUND)

        paint?.setStrokeWidth(5F)
    }

    fun clear() {
        bitmap = Bitmap.createBitmap(820, 480, Bitmap.Config.ARGB_4444)
        canvas = Canvas(bitmap)
        path2?.reset()
        invalidate()
    }

    fun getBitmap(): Bitmap? {
        return bitmap
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val pathWithPaint = DrawingClass()

        canvas?.drawPath(path2, paint)

        if (event.action == MotionEvent.ACTION_DOWN) {

            path2?.moveTo(event.x, event.y)

            path2?.lineTo(event.x, event.y)
        } else if (event.action == MotionEvent.ACTION_MOVE) {

            path2?.lineTo(event.x, event.y)

            pathWithPaint.setPath(path2!!)

            pathWithPaint.setPaint(paint!!)

            DrawingClassArrayList.add(pathWithPaint)
        }

        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (DrawingClassArrayList.size > 0) {

            canvas.drawPath(
                    DrawingClassArrayList[DrawingClassArrayList.size - 1].getPath(),

                    DrawingClassArrayList[DrawingClassArrayList.size - 1].getPaint())
        }
    }
}