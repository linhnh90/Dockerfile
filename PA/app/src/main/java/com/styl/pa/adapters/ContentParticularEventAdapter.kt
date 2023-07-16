package com.styl.pa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.styl.pa.R
import com.styl.pa.entities.pacesRequest.EventTicket
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.item_content_particular_event.view.*

class ContentParticularEventAdapter(
    var context: Context?,
    var listData: ArrayList<EventTicket>
): RecyclerView.Adapter<ContentParticularEventAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(eventTicket: EventTicket){
            val ticketName = eventTicket.name
            val price = eventTicket.price ?: 0f
            val priceStr = GeneralUtils.formatAmountSymbols("$", price.toFloat(), 2)
            itemView.tv_ticket_type_name.text = context?.getString(R.string.ticket_type_name_with_price, ticketName, priceStr)

            val totalTicket = eventTicket.selectedQty ?: 0
            itemView.tv_total_ticket.text = context?.getString(R.string.total_ticket_with_qty, totalTicket.toString())

            val totalAttendee = totalTicket * eventTicket.ticketMapCount
            itemView.tv_total_attendee.text = context?.getString(R.string.total_attendee_with_qty, totalAttendee.toString())

            val totalPrice = price * totalTicket
            var beforeDiscountAmount = 0f
            var paDiscountAmount = 0f
            var promoCodeDiscountAmount = 0f
            eventTicket.listTicketParticipantEntity.forEach {
                beforeDiscountAmount += it.beforeDiscountAmount ?: 0f
                paDiscountAmount += it.discountAmount ?: 0f
                promoCodeDiscountAmount += it.promoDiscountAmount ?: 0f
            }
            if (beforeDiscountAmount == 0f){
                beforeDiscountAmount = totalPrice
            }

            itemView.tv_total_price.text = GeneralUtils.formatAmountSymbols("", beforeDiscountAmount, 2)
            itemView.tv_pa_discount_amount.text = GeneralUtils.formatAmountSymbols("", paDiscountAmount, 2)
            itemView.tv_promo_code_discount_amount.text = GeneralUtils.formatAmountSymbols("", promoCodeDiscountAmount, 2)
            itemView.tv_pa_discount_percent.visibility = if (paDiscountAmount > 0f) View.VISIBLE else View.GONE
            itemView.tv_pa_discount_amount.visibility = if (paDiscountAmount > 0f) View.VISIBLE else View.GONE
            itemView.tv_promo_code_discount_header.visibility = if (promoCodeDiscountAmount > 0f) View.VISIBLE else View.GONE
            itemView.tv_promo_code_discount_amount.visibility = if (promoCodeDiscountAmount > 0f) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_content_particular_event, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}