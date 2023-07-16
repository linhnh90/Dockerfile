package com.styl.pa.customViews

import android.content.Context
import androidx.core.content.ContextCompat
import android.widget.TextView
import android.view.ViewGroup
import android.graphics.Typeface
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.content.res.ResourcesCompat
import android.view.View
import android.widget.ArrayAdapter
import com.styl.pa.R


/**
 * Created by Ngatran on 09/11/2018.
 */
class CustomSpinnerAdapter(@NonNull context: Context, @LayoutRes resource: Int, @NonNull objects: List<String>) : ArrayAdapter<String>(context, resource, objects) {

    private var tf: Typeface? = null

    fun setFont(font: Int) {
        tf = ResourcesCompat.getFont(context, font)
    }

    override fun getView(position: Int, @Nullable convertView: View, @NonNull parent: ViewGroup): View {
        val textView = super.getView(position, convertView, parent) as TextView
        textView.typeface = tf
        textView.setTextColor(ContextCompat.getColor(context, R.color.white_color))
        return textView
    }

    override fun getDropDownView(position: Int, @Nullable convertView: View, @NonNull parent: ViewGroup): View {
        val textView = super.getDropDownView(position, convertView, parent) as TextView
        textView.typeface = tf
        textView.setTextColor(ContextCompat.getColor(context, R.color.white_color))
        return textView
    }
}
