package com.styl.pa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.styl.pa.R
import com.styl.pa.entities.event.EventClassTicket
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.item_event_pricing.view.*

class EventPricingAdapter(
    var context: Context?,
    var listData: ArrayList<EventClassTicket>
): RecyclerView.Adapter<EventPricingAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(classTicket: EventClassTicket){
            val amount = classTicket.price ?: 0.0f
            itemView.tv_amount.text = if (amount > 0) GeneralUtils.formatAmountSymbols("$", amount, 2) else (context?.getString(R.string.free) ?: "")
            itemView.tv_ticket_type_name.text = classTicket.type ?: ""
            itemView.tv_desc.text = classTicket.description ?: ""
            itemView.tv_endDate.text = context?.getString(R.string.valid_till_with_datetime, GeneralUtils.formatToDateTime(classTicket.endDate))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_event_pricing, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}