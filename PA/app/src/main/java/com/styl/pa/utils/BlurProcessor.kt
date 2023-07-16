package com.styl.pa.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Canvas
import android.graphics.Color
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.annotation.FloatRange
import android.view.View
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import java.util.concurrent.atomic.AtomicReference

/**
 * Created by NguyenHang on 12/30/2020.
 */

@ExcludeFromJacocoGeneratedReport
class BlurProcessor {
    private var context: Context? = null

    constructor(context: Context?) {
        this.context = context
    }

    private fun getBitmapFromView(view: View): Bitmap? {
        //Define a bitmap with the same size as the view
        val returnedBitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, ARGB_8888)
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        //Get the view's background
        val bgDrawable = view.background
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas)
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE)
        }
        // draw the view on the canvas
        view.draw(canvas)
        //return the bitmap
        return returnedBitmap
    }

    private val sRenderscript: AtomicReference<RenderScript> = AtomicReference()

    fun blur(view: View): Bitmap? {
        return blur(getBitmapFromView(view))
    }

    fun blur(bitmap: Bitmap?): Bitmap? {
        return blur(context, bitmap, 20f, false, false)
    }

    fun blur(bitmap: Bitmap?, radius: Float): Bitmap? {
        return blur(context, bitmap, radius, false, false)
    }

    private fun blur(context: Context?, bitmapOriginal: Bitmap?, @FloatRange(from = 0.0, to = 25.0) radius: Float, overrideOriginal: Boolean, recycleOriginal: Boolean): Bitmap? {
        if (bitmapOriginal == null || bitmapOriginal.isRecycled) return null
        var rs: RenderScript? = sRenderscript.get()
        if (rs == null) if (!sRenderscript.compareAndSet(null, RenderScript.create(context).also { rs = it }) && rs != null) rs?.destroy() else rs = sRenderscript.get()
        val inputBitmap: Bitmap = if (bitmapOriginal.config == Config.ARGB_8888) bitmapOriginal else bitmapOriginal.copy(Config.ARGB_8888, true)
        val outputBitmap: Bitmap = if (overrideOriginal) bitmapOriginal else Bitmap.createBitmap(bitmapOriginal.width, bitmapOriginal.height, Config.ARGB_8888)
        val input: Allocation = Allocation.createFromBitmap(rs, inputBitmap)
        val output: Allocation = Allocation.createTyped(rs, input.getType())
        val script: ScriptIntrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        script.setRadius(radius)
        script.setInput(input)
        script.forEach(output)
        if (recycleOriginal && !overrideOriginal) bitmapOriginal.recycle()
        output.copyTo(outputBitmap)
        return outputBitmap
    }
}