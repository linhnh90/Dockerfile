package com.styl.pa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.styl.pa.R
import com.styl.pa.entities.payment.PaymentRequest
import com.styl.pa.utils.DateUtils
import kotlinx.android.synthetic.main.item_txn_log_layout.view.*

class TransactionAdapter(private var context: Context?, private var txnLogs: ArrayList<PaymentRequest>) : RecyclerView.Adapter<TransactionAdapter.TxnViewHolder>() {

    companion object {
        private const val DATE_FORMAT_PATTERN = "yyyy/MM/dd HH:mm:ss"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TxnViewHolder {
        return TxnViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_txn_log_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return txnLogs.size
    }

    fun getSelectedItems(): List<PaymentRequest> {
        return txnLogs.filter { it.isSelected }
    }

    override fun onBindViewHolder(holder: TxnViewHolder, position: Int) {
        val item = txnLogs[position]
        holder.itemView?.txt_customer_id?.text = item.customerId
        holder.itemView?.txt_customer_name?.text = item.customerName
        holder.itemView?.txt_txn_no?.text = item.txnNo
        holder.itemView?.txt_cart_id?.text = item.cartId
        holder.itemView?.txt_payment_status?.text = PaymentRequest.getPaymentStatusString(item.paymentStatus ?: -2)
        holder.itemView?.txt_created_at?.text = DateUtils.convertLongToString(
                (item.createdAt ?: 0) * 1000, DATE_FORMAT_PATTERN)
        holder.itemView?.txt_completed_at?.text = DateUtils.convertLongToString(
                (item.completedAt ?: 0) * 1000, DATE_FORMAT_PATTERN)

        holder.itemView?.cb_selected_log?.isChecked = item.isSelected

        holder.itemView?.ll_log_item?.setOnClickListener { v ->
            val adapterPosition = holder.adapterPosition
            if (adapterPosition == position) {
                holder.itemView.cb_selected_log.isChecked = !holder.itemView.cb_selected_log.isChecked
                txnLogs[adapterPosition].isSelected = holder.itemView.cb_selected_log.isChecked
                notifyDataSetChanged()
            }
        }

    }

    class TxnViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}