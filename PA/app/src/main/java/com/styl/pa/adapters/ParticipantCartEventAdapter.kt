package com.styl.pa.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.styl.pa.R
import com.styl.pa.entities.pacesRequest.EventTicket
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.item_participant_cart_event.view.*

class ParticipantCartEventAdapter(
    var context: Context?,
    var listData: ArrayList<EventTicket>,
    var participantAdapterListener: ParticipantAdapter.OnParticipantAdapterListener?,
    var ticketAdapterListener: TicketAdapter.OnTicketAdapterListener?,
    var listener: OnParticipantCartEventListener?
) : RecyclerView.Adapter<ParticipantCartEventAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: EventTicket) {
            val name = item.name ?: ""
            val price = GeneralUtils.formatAmountSymbols("$", (item.price ?: 0f).toFloat(), 2)
            itemView.tv_ticket_type.text = "$name - $price"

            itemView.tv_total.text = context?.getString(
                R.string.total_with_colon_form,
                (item.selectedQty ?: 0).toString()
            )
            itemView.tv_msg_participant.visibility = if (!item.isAllParticipantRequired && item.ticketMapCount > 1) View.VISIBLE else View.GONE

            val isExpand = item.isExpandContent
            itemView.rcv_ticket.visibility = if (isExpand) View.VISIBLE else View.GONE
            val ivDropdown = if (isExpand) R.drawable.img_up_arrow else R.drawable.img_down_arrow
            itemView.iv_dropdown.setImageResource(ivDropdown)

            val adapter = TicketAdapter(
                context = context,
                eventTicketPosition = adapterPosition,
                listTicketEntity = item.listTicketParticipantEntity,
                ticketAdapterListener = ticketAdapterListener,
                participantAdapterListener = participantAdapterListener
            )
            itemView.rcv_ticket.layoutManager = LinearLayoutManager(context)
            itemView.rcv_ticket.adapter = adapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_participant_cart_event, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.btn_plus_ticket.setOnClickListener {
            listener?.onClickBtnAddTicket(holder.adapterPosition)
        }

        holder.itemView.iv_dropdown.setOnClickListener {
            val isExpand = holder.itemView.rcv_ticket.visibility == View.VISIBLE
            listener?.expandContent(isExpand = isExpand, eventTicketPosition = holder.adapterPosition)
            notifyItemChanged(holder.adapterPosition)
        }

        holder.bind(listData[position])
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    interface OnParticipantCartEventListener {
        fun onClickBtnAddTicket(eventTicketPosition: Int)

        fun expandContent(isExpand: Boolean, eventTicketPosition: Int)
    }
}