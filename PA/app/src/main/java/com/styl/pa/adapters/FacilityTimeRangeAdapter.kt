package com.styl.pa.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.styl.pa.R
import com.styl.pa.entities.facility.SlotSessionInfo
import kotlinx.android.synthetic.main.facility_resource_name_item.view.*
import kotlinx.android.synthetic.main.facility_time_range_item.view.*

/**
 * Created by Ngatran on 09/29/2018.
 */
class FacilityTimeRangeAdapter : RecyclerView.Adapter<FacilityTimeSlotAdapter.FacilityTimeSlotVH> {
    private var listTimeSlot: ArrayList<SlotSessionInfo> = ArrayList()
    private var context: Context? = null
    private var widthSize = 0

    constructor(listTimeSlot: ArrayList<SlotSessionInfo>, context: Context) {
        this.listTimeSlot = listTimeSlot
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityTimeSlotAdapter.FacilityTimeSlotVH {
        return FacilityTimeSlotAdapter.FacilityTimeSlotVH(LayoutInflater.from(parent.context).inflate(R.layout.facility_time_range_item, parent, false))
    }

    override fun getItemCount(): Int {
        return listTimeSlot.size
    }

    override fun onBindViewHolder(holder: FacilityTimeSlotAdapter.FacilityTimeSlotVH, position: Int) {
        val textParam = LinearLayout.LayoutParams(widthSize, context?.resources?.getDimensionPixelOffset(R.dimen.dp_35)!!)
        holder.itemView.txt_time_range_name.layoutParams = textParam
        holder.itemView.txt_time_range_name.text = listTimeSlot.get(position).mTimeRageName
    }

    fun setWidth(width: Int) {
        this.widthSize = width
    }


    class FacilityTimeRangeVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}