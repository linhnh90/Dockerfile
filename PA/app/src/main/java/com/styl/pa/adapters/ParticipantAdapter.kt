package com.styl.pa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.styl.pa.R
import com.styl.pa.entities.event.EventParticipant
import kotlinx.android.synthetic.main.item_participant.view.*

class ParticipantAdapter(
    var context: Context?,
    var eventTicketPosition: Int,
    var ticketEntityPosition: Int,
    var listParticipant: ArrayList<EventParticipant>,
    var listener: OnParticipantAdapterListener?
) : RecyclerView.Adapter<ParticipantAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_participant, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.iv_edit.setOnClickListener {
            listener?.onClickBtnEdit(
                eventTicketPosition = eventTicketPosition,
                ticketEntityPosition = ticketEntityPosition,
                eventParticipantPosition = holder.adapterPosition)
        }

        holder.bind(listParticipant[holder.adapterPosition])
    }

    override fun getItemCount(): Int {
        return listParticipant.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: EventParticipant) {
            itemView.tv_participant_name.text = item.nameToShow ?: context?.getString(
                R.string.participant_form,
                (adapterPosition + 1).toString()
            )
            itemView.tv_warning.visibility = if (item.isFullFillInfo) View.GONE else View.VISIBLE
            val textColor = if (item.isFullFillInfo) R.color.green_color_4 else R.color.black_color_2
            context?.getColor(textColor)?.let { itemView.tv_participant_name.setTextColor(it) }
            val ivParticipant = if (item.isFullFillInfo) R.drawable.img_single_participant_green else R.drawable.img_single_participant
            itemView.iv_profile.setImageResource(ivParticipant)
        }
    }

    interface OnParticipantAdapterListener {
        fun onClickBtnEdit(
            eventTicketPosition: Int,
            ticketEntityPosition: Int,
            eventParticipantPosition: Int
        )
    }

}