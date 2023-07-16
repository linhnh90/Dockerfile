package com.styl.pa.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.styl.pa.R
import com.styl.pa.entities.generateToken.ResourceFeeList
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.facility_amount_item.view.*
import kotlinx.android.synthetic.main.facility_resource_name_item.view.*

/**
 * Created by Ngatran on 10/01/2018.
 */

class FacilityAmountAdapter : RecyclerView.Adapter<FacilityAmountAdapter.FacilityAmountVH> {
    private var facilityAmountList: ArrayList<ResourceFeeList> = ArrayList()
    private var context: Context? = null

    constructor(facilityAmountList: ArrayList<ResourceFeeList>, context: Context?) {
        this.facilityAmountList = facilityAmountList
        this.context = context
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityAmountVH {
        return FacilityAmountVH(LayoutInflater.from(parent.context).inflate(R.layout.facility_amount_item, parent, false))
    }

    override fun getItemCount(): Int {
        return facilityAmountList.size
    }

    override fun onBindViewHolder(holder: FacilityAmountVH, position: Int) {
        holder.itemView?.txt_name_rate?.text = facilityAmountList.get(position).getFeeName()
        var feeNormalAmount = 0f
        var feePeakAmount = 0f
        if (facilityAmountList.get(position).getFeeNormalAmount()?.isNullOrEmpty() == false) {
            try {
                feeNormalAmount = facilityAmountList.get(position).getFeeNormalAmount()!!.toFloat()
            } catch (e: NumberFormatException) {
                feeNormalAmount = 0f
            }
        }

        if (facilityAmountList.get(position).getFeePeakAmount()?.isNullOrEmpty() == false) {
            try {
                feePeakAmount = facilityAmountList.get(position).getFeePeakAmount()!!.toFloat()
            } catch (e: NumberFormatException) {
                feePeakAmount = 0f
            }
        }
        holder.itemView?.txt_amount_range?.text = (GeneralUtils.formatAmountSymbols("$", feeNormalAmount, 2) + "-" + GeneralUtils.formatAmountSymbols("$", feePeakAmount, 2))
    }

    class FacilityAmountVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}
