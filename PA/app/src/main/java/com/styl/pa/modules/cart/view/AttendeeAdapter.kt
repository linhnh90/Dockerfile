package com.styl.pa.modules.cart.view

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.styl.pa.R
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.modules.base.OnRecyclerViewListener
import com.styl.pa.modules.cart.presenter.CartPresenter
import com.styl.pa.utils.GeneralTextUtil
import kotlinx.android.synthetic.main.cart_attendee_item.view.*

class AttendeeAdapter(
        val context: Context,
        var presenter: CartPresenter?,
        var cartItem: CartItem?
) : RecyclerView.Adapter<AttendeeAdapter.AttendeeVH>() {

    var onAttendeeListener: OnAttendeeListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendeeVH {
        val view =
                LayoutInflater.from(parent.context).inflate(R.layout.cart_attendee_item, parent, false)
        return AttendeeVH(view)
    }

    override fun getItemCount(): Int {
        return cartItem?.attendees?.size ?: 0
    }

    override fun onBindViewHolder(holder: AttendeeVH, position: Int) {
        val customerInfo = cartItem?.attendees?.get(holder.adapterPosition)?.customerInfo

        holder.itemView?.txt_no?.text =
                context.getString(R.string.attendee_no, holder.adapterPosition + 1)
        holder.itemView?.txt_name?.text = customerInfo?.mFullName
        holder.itemView?.txt_id?.text = GeneralTextUtil.maskText(customerInfo?.mIdNo, 5, true)

        checkIndemnity(holder, position)

        holder.itemView.btn_remove.setOnClickListener { view ->

            onAttendeeListener?.onRemoveAttendee(view, cartItem, holder.adapterPosition)
        }
    }

    private fun checkIndemnity(holder: AttendeeVH, position: Int) {
        if ((cartItem?.classInfo != null && cartItem?.classInfo?.getIndemnityRequired() == true) ||
                (cartItem?.event != null && cartItem?.event?.indemnityRequired == true)) {
            holder.itemView?.txt_indemnity?.visibility = View.VISIBLE
            if (cartItem?.attendees?.get(holder.adapterPosition)?.isIndemnity == true) {
                holder.itemView?.txt_indemnity?.text = context.getText(R.string.indemnity_cleared)
                holder.itemView?.txt_indemnity?.setTextColor(ContextCompat.getColor(context, R.color.black_color))
                val leftDrawable = ContextCompat.getDrawable(context, R.drawable.ic_check)
                holder.itemView?.txt_indemnity?.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null)
                holder.itemView?.txt_indemnity?.compoundDrawablePadding = context.resources.getDimensionPixelSize(R.dimen.dp_10)
            } else {
                holder.itemView?.txt_indemnity?.text = context.getText(R.string.indemnity_not_cleared)
                holder.itemView?.txt_indemnity?.setTextColor(ContextCompat.getColor(context, R.color.red_color))
                holder.itemView?.txt_indemnity?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            return
        }

        holder.itemView?.txt_indemnity?.visibility = View.GONE
    }

    class AttendeeVH(view: View) : RecyclerView.ViewHolder(view)

    interface OnAttendeeListener : OnRecyclerViewListener {

        fun onRemoveAttendee(view: View, cartItem: CartItem?, position: Int)
    }
}