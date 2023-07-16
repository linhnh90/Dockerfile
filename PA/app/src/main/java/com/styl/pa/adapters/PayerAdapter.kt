package com.styl.pa.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.styl.pa.R
import com.styl.pa.entities.customer.CustomerInfo
import kotlinx.android.synthetic.main.raw_payer.view.*

/**
 * Created by NguyenHang on 12/28/2020.
 */
class PayerAdapter : RecyclerView.Adapter<PayerAdapter.ViewHolder> {
    private var context: Context? = null
    private var payer: ArrayList<CustomerInfo>? = null
    var selectedPosition: Int? = null

    constructor(context: Context?, payer: ArrayList<CustomerInfo>) {
        this.context = context
        this.payer = initPayerList(payer)
    }

    private fun initPayerList(payer: ArrayList<CustomerInfo>): ArrayList<CustomerInfo> {
        val payerList = ArrayList<CustomerInfo>()
        for (itemPayer in payer) {
            var isContain = false
            payerList.forEach { customerInfo ->
                if (itemPayer.mCustomerId != null &&
                        customerInfo.mCustomerId?.contentEquals(itemPayer.mCustomerId!!) == true) {
                    isContain = true
                    return@forEach
                }
            }
            if (!isContain) payerList.add(itemPayer)
        }
        return payerList
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(payer: CustomerInfo?) {
            if (payer != null) {
                itemView.tv_payer_name?.text = payer.mFullName
                itemView.rb_payer?.isChecked = selectedPosition == adapterPosition
                itemView.rl_payer?.setOnClickListener {
                    itemView.rb_payer?.isChecked = !(itemView.rb_payer?.isChecked?: false)
                    if (itemView.rb_payer?.isChecked == true) {
                        selectedPosition = adapterPosition
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.raw_payer, parent, false))
    }

    override fun getItemCount(): Int {
        return (this.payer?.size ?: 0) + 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position >= payer?.size ?: 0) {
            holder.itemView.tv_payer_name?.text = context?.getString(R.string.not_listed_here)
            holder.itemView.rb_payer?.isChecked =  selectedPosition == position
            holder.itemView.rl_payer?.setOnClickListener {
                holder.itemView.rb_payer?.isChecked = !(holder.itemView.rb_payer?.isChecked?: false)
                if ( holder.itemView.rb_payer?.isChecked == true) {
                    selectedPosition = position
                }
                notifyDataSetChanged()
            }
        } else {
            holder.bind(payer?.get(position))
        }
    }
}