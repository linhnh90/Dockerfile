package com.styl.pa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.styl.pa.R
import com.styl.pa.entities.event.TicketEntity
import kotlinx.android.synthetic.main.item_ticket.view.*

class TicketAdapter(
    var context: Context?,
    var eventTicketPosition: Int,
    var listTicketEntity: ArrayList<TicketEntity>,
    var ticketAdapterListener: OnTicketAdapterListener?,
    var participantAdapterListener: ParticipantAdapter.OnParticipantAdapterListener?
) : RecyclerView.Adapter<TicketAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: TicketEntity) {
            itemView.tv_ticket_number.text =
                context?.getString(R.string.ticket_form, (adapterPosition + 1).toString()) ?: ""

            val adapter = ParticipantAdapter(
                context = context,
                eventTicketPosition = eventTicketPosition,
                ticketEntityPosition = adapterPosition,
                listParticipant = item.listParticipant,
                listener = participantAdapterListener
            )
            itemView.rcv_participant.layoutManager = LinearLayoutManager(context)
            itemView.rcv_participant.adapter = adapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_ticket, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.btn_remove.setOnClickListener {
            ticketAdapterListener?.onBtnRemoveTicket(
                eventTicketPosition = eventTicketPosition,
                ticketEntityPosition = holder.adapterPosition
            )
        }
        holder.bind(listTicketEntity[position])
    }

    override fun getItemCount(): Int {
        return listTicketEntity.size
    }

    interface OnTicketAdapterListener {
        fun onBtnRemoveTicket(eventTicketPosition: Int, ticketEntityPosition: Int)
    }
}