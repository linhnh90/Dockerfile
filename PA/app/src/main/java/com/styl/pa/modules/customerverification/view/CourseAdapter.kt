package com.styl.pa.modules.customerverification.view

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.styl.pa.R
import com.styl.pa.modules.customerverification.CustomerVerificationContract
import com.styl.pa.modules.customerverification.presenter.CustomerVerificationPresenter
import kotlinx.android.synthetic.main.course_attending_item.view.*

class CourseAdapter(var presenter: CustomerVerificationPresenter?, var context: Context?) : RecyclerView.Adapter<CourseAdapter.CourseAdapterVH>(), CustomerVerificationContract.CourseAdapterView {

    private lateinit var holder: CourseAdapterVH

    var onCourseListener: OnCourseListener? = null

    init {
        presenter?.adapterView = this
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseAdapterVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.course_attending_item, parent, false)
        return CourseAdapterVH(view)
    }

    override fun getItemCount(): Int {
        return presenter?.getItemCount() ?: 0
    }

    override fun onBindViewHolder(holder: CourseAdapterVH, position: Int) {
        this.holder = holder
        if (context != null) {
            holder.itemView.cb_attending.typeface = ResourcesCompat.getFont(context!!, R.font.opensans_bold)
        }
        holder.itemView.cb_attending.setOnCheckedChangeListener(null)
        presenter?.bindViewHolder(holder.adapterPosition)

        holder.itemView.cb_attending.setOnCheckedChangeListener { view, isChecked ->

            onCourseListener?.onCheckedChange(view, holder.adapterPosition, isChecked)
        }
    }

    override fun setNoneOption() {
        holder.itemView.cb_attending.setText(R.string.none)
    }

    override fun setCourseName(name: Spanned?) {
        holder.itemView.cb_attending.text = name
    }

    override fun setShowOption(isShown: Boolean) {
        holder.itemView.cb_attending.visibility = if (isShown) View.VISIBLE else View.INVISIBLE
    }

    override fun setChecked(isChecked: Boolean) {
        holder.itemView.cb_attending.isChecked = isChecked
    }

    override fun setEnabled(isEnabled: Boolean) {
        holder.itemView.cb_attending.isEnabled = isEnabled
    }

    override fun updateList() {
        notifyDataSetChanged()
    }

    class CourseAdapterVH(view: View) : RecyclerView.ViewHolder(view)

    interface OnCourseListener {

        fun onCheckedChange(view: View, position: Int, isChecked: Boolean)
    }
}