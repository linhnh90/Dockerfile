package com.styl.pa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.styl.pa.R
import com.styl.pa.entities.pacesRequest.EventTicket
import com.styl.pa.modules.selectTicketType.view.SelectTicketTypeFragment
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.LogManager
import kotlinx.android.synthetic.main.item_ticket_type.view.*

class TicketTypeAdapter(
    var context: Context?,
    var listData: ArrayList<EventTicket>,
    var listener: OnTicketTypeActionListener?
) : RecyclerView.Adapter<TicketTypeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_ticket_type, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.cb_ticket_type.setOnClickListener {
            val isChecked = holder.itemView.cb_ticket_type.isChecked
            listener?.onCheckboxChangeListener(holder.adapterPosition, isChecked)
        }
        holder.itemView.btn_minus.setOnClickListener {
            listener?.onMinusListener(holder.adapterPosition)
        }
        holder.itemView.btn_plus.setOnClickListener {
            listener?.onPlusListener(holder.adapterPosition)
        }
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(ticket: EventTicket) {
            val price = ticket.price ?: 0f
            val priceStr = if (price == 0f) (context?.getString(R.string.free) ?: "")
            else GeneralUtils.formatAmountSymbols(
                "$",
                ticket.price ?: 0f,
                2
            )
            val cbText = ticket.name + " - " + priceStr
            itemView.cb_ticket_type.text = cbText
            itemView.cb_ticket_type.isChecked = ticket.isSelected

            val currentQty = ticket.selectedQty ?: 0
            itemView.tv_selected_qty.text = currentQty.toString()

            if (ticket.isSelected) {
                ticket.isEnableMinus = currentQty > (ticket.minQty ?: 0)
                ticket.isEnablePlus = !SelectTicketTypeFragment.isMaxTicketLimit(
                    currentQty = currentQty,
                    minQty = ticket.minQty ?: 0,
                    maxQty = ticket.maxQty ?: 0,
                    availableQty = ticket.availableQty ?: 0
                )
            } else {
                ticket.isEnablePlus = false
                ticket.isEnableMinus = false
            }

            if ((ticket.availableQty ?: 0) <= 0) {
                itemView.tv_selected_qty.text = context?.getString(R.string.sold_out)
                itemView.cb_ticket_type.isEnabled = false
                ticket.isEnableMinus = false
                ticket.isEnablePlus = false
            }

            LogManager.d("TicketTypeAdapter: ticket.isEnableMinus = ${ticket.isEnableMinus},  ticket.isEnablePlus = ${ticket.isEnablePlus}")
            itemView.btn_minus.isEnabled = ticket.isEnableMinus
            itemView.btn_plus.isEnabled = ticket.isEnablePlus
            context?.let {
                itemView.btn_minus.setTextColor(
                    ContextCompat.getColor(
                        it,
                        if (ticket.isEnableMinus) R.color.black_color_2 else R.color.gray_color_1
                    )
                )
                itemView.btn_plus.setTextColor(
                    ContextCompat.getColor(
                        it,
                        if (ticket.isEnablePlus) R.color.red_color_2 else R.color.gray_color_1
                    )
                )

            }
        }
    }

    interface OnTicketTypeActionListener {
        fun onCheckboxChangeListener(position: Int, isChecked: Boolean)
        fun onMinusListener(position: Int)
        fun onPlusListener(position: Int)
    }
}