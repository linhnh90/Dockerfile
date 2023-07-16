package com.styl.pa.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.styl.pa.R
import com.styl.pa.entities.facility.FacilitySessionInfo
import com.styl.pa.entities.facility.SlotSessionInfo
import com.styl.pa.entities.facility.SlotSessionInfo.CREATOR.AVAIL_TYPE
import com.styl.pa.entities.facility.SlotSessionInfo.CREATOR.BOOKING_TYPE
import com.styl.pa.interfaces.OnClickRecyclerViewItem
import kotlinx.android.synthetic.main.facility_resource_name_item.view.*
import kotlinx.android.synthetic.main.facility_timeslot_item.view.*

/**
 * Created by Ngatran on 09/25/2018.
 */
class FacilityTimeSlotAdapter : RecyclerView.Adapter<FacilityTimeSlotAdapter.FacilityTimeSlotVH>{

    private var TEXT_TYPE = 0
    private var RADIO_TYPE = 1

    private var AVAILABLE_TYPE = "Available"
    private var BOOKED_TYPE = "Booked"
    private var PEAK_TYPE = "Peak"
    private var UN_AVAILABLE_TYPE = "Unavailable"

    private var facilitySessionInfoList: ArrayList<FacilitySessionInfo> = ArrayList()
    private var facilityTimeSlotList: ArrayList<SlotSessionInfo> = ArrayList()
    private var context: Context? = null

    private var col = 0
    private var row = 0

    private var widthSize = 0

    private var colChoose = -1

    private var clickRecyclerViewItem: OnClickRecyclerViewItem? = null

    fun setClickRecyclerViewItem(clickRecyclerViewItem: OnClickRecyclerViewItem) {
        this.clickRecyclerViewItem = clickRecyclerViewItem
    }

    constructor(facilitySessionInfoList: ArrayList<FacilitySessionInfo>, facilityTimeSlotList: ArrayList<SlotSessionInfo>, context: Context) {
        this.facilitySessionInfoList = facilitySessionInfoList
        this.facilityTimeSlotList = facilityTimeSlotList
        this.context = context
    }

    fun setWidth(width: Int) {
        this.widthSize = width
    }

    fun setCol(col: Int) {
        this.col = col
    }

    override fun getItemViewType(position: Int): Int {
        if (position < col) {
            return TEXT_TYPE
        } else {
            return RADIO_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityTimeSlotVH {
        if (viewType == TEXT_TYPE) {
            return FacilityTimeSlotVH(LayoutInflater.from(parent.context).inflate(R.layout.facility_resource_name_item, parent, false))
        } else {
            return FacilityTimeSlotVH(LayoutInflater.from(parent.context).inflate(R.layout.facility_timeslot_item, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return facilityTimeSlotList.size + facilitySessionInfoList.size
    }

    override fun onBindViewHolder(holder: FacilityTimeSlotVH, position: Int) {
        if (position < col) {
            val textParam = LinearLayout.LayoutParams(widthSize, context?.resources?.getDimensionPixelOffset(R.dimen.dp_40)!!)
            holder.itemView.txt_resource_name.layoutParams = textParam
            holder.itemView.txt_resource_name?.text = facilitySessionInfoList.get(position).mResourceName
        } else {
            val textParam = LinearLayout.LayoutParams(widthSize, LinearLayout.LayoutParams.WRAP_CONTENT)
            holder.itemView.ll_container_timeslot_item?.layoutParams = textParam

            if (facilityTimeSlotList[position - col].mIsChoose != AVAIL_TYPE) {
                holder.itemView.rb_timeslot_item.isChecked = true
            }

            when (facilityTimeSlotList.get(position - col).mAvailabilityStatus) {
                BOOKED_TYPE -> {
                    holder.itemView.rb_timeslot_item.buttonDrawable = context?.getDrawable(R.drawable.ic_booked)
                    holder.itemView.rb_timeslot_item.isEnabled = false
                }

                PEAK_TYPE -> {
                    holder.itemView.rb_timeslot_item.buttonDrawable = context?.getDrawable(R.drawable.ic_peak_small)
                    holder.itemView.rb_timeslot_item.isEnabled = false
                }

                AVAILABLE_TYPE -> {
                    holder.itemView.rb_timeslot_item.buttonDrawable = context?.getDrawable(R.drawable.cb_unchecked)
                }

                UN_AVAILABLE_TYPE -> {
                    holder.itemView.rb_timeslot_item.buttonDrawable = context?.getDrawable(R.drawable.ic_unavailable_small)
                    holder.itemView.rb_timeslot_item.isEnabled = false
                }
            }

            holder.itemView.rb_timeslot_item.setOnClickListener {
                var colUserChoose = position % col
                if (colUserChoose != colChoose) {
                    for (i in 0..facilityTimeSlotList.size - 1) {
                        if (i == position - col) {
                            if (facilityTimeSlotList[position - col].mIsChoose != AVAIL_TYPE) {
                                facilityTimeSlotList[position - col].mIsChoose = AVAIL_TYPE
                            } else {
                                facilityTimeSlotList[position - col].mIsChoose = BOOKING_TYPE
                            }
                        }
                        facilityTimeSlotList[i].mIsChoose = AVAIL_TYPE
                    }
                } else {
                    if (facilityTimeSlotList[position - col].mIsChoose != AVAIL_TYPE) {
                        facilityTimeSlotList[position - col].mIsChoose = AVAIL_TYPE
                    } else {
                        facilityTimeSlotList[position - col].mIsChoose = BOOKING_TYPE
                    }
                }
                if (clickRecyclerViewItem != null) {
                    clickRecyclerViewItem?.onClick(holder.itemView, position)
                }
            }

        }
    }


    class FacilityTimeSlotVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}