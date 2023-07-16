package com.styl.pa.modules.courseDetails.view

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.styl.pa.R
import com.styl.pa.entities.classes.ClassFee
import com.styl.pa.entities.event.EventFee
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.LogManager
import kotlinx.android.synthetic.main.facility_amount_item.view.*

class FeeAdapter<T>(var items: List<T>?) : RecyclerView.Adapter<FeeAdapter.FeeVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeeVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.facility_amount_item, parent, false)
        return FeeVH(view)
    }

    fun setItem(items: List<T>?) {
        this.items = items
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: FeeVH, position: Int) {
        val item = items?.get(holder.adapterPosition)

        var feeName: String? = null
        var fee: Float? = null
        if (item is ClassFee) {
            feeName = item.getClassFeeName()
            try {
                fee = item.getClassFeeAmount()?.toFloat()
            } catch (e: Exception) {
                LogManager.i("Format course price failed")
            }
        } else if (item is EventFee) {
            feeName = item.feeName
            try {
                fee = item.feeAmount?.toFloat()
            } catch (e: Exception) {
                LogManager.i("Format event fee failed")
            }
        }
        holder.itemView?.txt_name_rate?.text = feeName
        holder.itemView?.txt_amount_range?.text = GeneralUtils.formatAmountSymbols("$", fee
                ?: 0f, 2)
    }

    class FeeVH(v: View) : RecyclerView.ViewHolder(v)
}