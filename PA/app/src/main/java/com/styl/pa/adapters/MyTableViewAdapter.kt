package com.styl.pa.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.styl.pa.R
import com.styl.pa.entities.facility.FacilitySessionInfo
import com.styl.pa.entities.facility.SlotSessionInfo
import com.styl.pa.entities.facility.SlotSessionInfo.CREATOR.AVAILABLE_TYPE
import com.styl.pa.entities.facility.SlotSessionInfo.CREATOR.BOOKED_TYPE
import com.styl.pa.entities.facility.SlotSessionInfo.CREATOR.PEAK_TYPE
import com.styl.pa.entities.facility.SlotSessionInfo.CREATOR.UN_AVAILABLE_TYPE
import com.styl.pa.modules.courseDetails.view.CourseDetailsFragment
import com.styl.pa.utils.GeneralTextUtil
import kotlinx.android.synthetic.main.facility_resource_name_item.view.*

/**
 * Created by Ngatran on 10/02/2018.
 */
class MyTableViewAdapter : AbstractTableAdapter<FacilitySessionInfo, SlotSessionInfo, SlotSessionInfo> {
    private var context: Context? = null

    private var facilitySessionInfoList: ArrayList<FacilitySessionInfo> = ArrayList()

    private var heightSize = 0
    private var widthSize = 0
    private var mode = CourseDetailsFragment.ZOOM_NORMAL

    constructor(context: Context?, facilitySessionInfoList: ArrayList<FacilitySessionInfo>) : super(context) {
        this.context = context
        this.facilitySessionInfoList = facilitySessionInfoList
    }

    fun setHeightSize(heightSize: Int) {
        this.heightSize = heightSize
    }

    fun setWidthSize(widthSize: Int) {
        this.widthSize = widthSize
    }

    fun setZoomMode(mode: Int) {
        this.mode = mode
    }


    override fun onCreateColumnHeaderViewHolder(parent: ViewGroup?, viewType: Int): AbstractViewHolder {
        return MyColumnHeaderViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.facility_resource_name_item, parent, false))
    }

    override fun getColumnHeaderItemViewType(position: Int): Int {
        return 0
    }

    override fun onBindColumnHeaderViewHolder(holder: AbstractViewHolder?, columnHeaderItemModel: Any?, columnPosition: Int) {
        val txtResourceName = holder?.itemView?.findViewById<TextView>(R.id.txt_resource_name)
        txtResourceName?.text = facilitySessionInfoList.get(columnPosition).mResourceName
        if (mode == CourseDetailsFragment.ZOOM_NORMAL) {
            txtResourceName?.textSize =  context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
        } else if (mode == CourseDetailsFragment.ZOOM_IN) {
            txtResourceName?.textSize = GeneralTextUtil.getTextSizeZoomIn(context, R.dimen.text_size_small)
        } else if (mode == CourseDetailsFragment.ZOOM_OUT) {
            txtResourceName?.textSize = GeneralTextUtil.getTextSizeZoomOut(context, R.dimen.text_size_small)
        }
        val llContainerResourceName = holder?.itemView?.findViewById<LinearLayout>(R.id.ll_container_resource_item)

        txtResourceName?.layoutParams?.height = context?.resources?.getDimensionPixelOffset(R.dimen.dp_50)
        llContainerResourceName?.layoutParams?.width = widthSize

        llContainerResourceName?.gravity = Gravity.CENTER_HORIZONTAL

        txtResourceName?.requestLayout()

    }

    class MyColumnHeaderViewHolder(itemView: View) : AbstractViewHolder(itemView) {

    }


    override fun onCreateRowHeaderViewHolder(parent: ViewGroup?, viewType: Int): AbstractViewHolder {
        return MyRowHeaderViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.facility_time_range_item, parent, false))
    }

    override fun getRowHeaderItemViewType(position: Int): Int {
        return 0
    }

    override fun onBindRowHeaderViewHolder(holder: AbstractViewHolder?, rowHeaderItemModel: Any?, rowPosition: Int) {

        val txtTimeRangeName = holder?.itemView?.findViewById<TextView>(R.id.txt_time_range_name)
        txtTimeRangeName?.text = (rowHeaderItemModel as SlotSessionInfo).mTimeRageName

        if (mode == CourseDetailsFragment.ZOOM_NORMAL) {
            txtTimeRangeName?.textSize =  context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
        } else if (mode == CourseDetailsFragment.ZOOM_IN) {
            txtTimeRangeName?.textSize = GeneralTextUtil.getTextSizeZoomIn(context, R.dimen.text_size_small)
        } else if (mode == CourseDetailsFragment.ZOOM_OUT) {
            txtTimeRangeName?.textSize = GeneralTextUtil.getTextSizeZoomOut(context, R.dimen.text_size_small)
        }

        txtTimeRangeName?.layoutParams?.height = context?.resources?.getDimensionPixelOffset(R.dimen.dp_50)

        txtTimeRangeName?.requestLayout()
    }

    class MyRowHeaderViewHolder(itemView: View) : AbstractViewHolder(itemView) {
        public var timeRangeName = itemView?.findViewById<TextView>(R.id.txt_time_range_name)
    }


    override fun onCreateCellViewHolder(parent: ViewGroup?, viewType: Int): AbstractViewHolder {
        return MyCellViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.facility_timeslot_item, parent, false))
    }

    override fun getCellItemViewType(position: Int): Int {
        return 0
    }

    override fun onBindCellViewHolder(holder: AbstractViewHolder?, cellItemModel: Any?, columnPosition: Int, rowPosition: Int) {
        val rbRadio = holder?.itemView?.findViewById<RadioButton>(R.id.rb_timeslot_item)

        val llContainerCell = holder?.itemView?.findViewById<LinearLayout>(R.id.ll_container_timeslot_item)

        llContainerCell?.layoutParams?.height = context?.resources?.getDimensionPixelOffset(R.dimen.dp_50)
        llContainerCell?.layoutParams?.width = widthSize

        llContainerCell?.gravity = Gravity.CENTER_HORIZONTAL

        llContainerCell?.requestLayout()

        var timeSlotInfo = cellItemModel as SlotSessionInfo

        if (timeSlotInfo.mIsChoose != SlotSessionInfo.AVAIL_TYPE) {
            rbRadio?.isChecked = true
        } else {
            rbRadio?.isChecked = false
        }

        when (timeSlotInfo.mAvailabilityStatus) {
            BOOKED_TYPE -> {
                rbRadio?.buttonDrawable = context?.getDrawable(R.drawable.ic_booked)
                rbRadio?.isEnabled = false
            }

            PEAK_TYPE -> {
                rbRadio?.buttonDrawable = context?.getDrawable(R.drawable.bg_session_peak_rate)
            }

            AVAILABLE_TYPE -> {
                if (timeSlotInfo.mIsPeak) {
                    rbRadio?.buttonDrawable = context?.getDrawable(R.drawable.bg_session_peak_rate)
                } else {
                    rbRadio?.buttonDrawable = context?.getDrawable(R.drawable.bg_session_normal_rate)
                }
                rbRadio?.isEnabled = true
            }

            UN_AVAILABLE_TYPE -> {
                rbRadio?.buttonDrawable = context?.getDrawable(R.drawable.ic_unavailable_small)
                rbRadio?.isEnabled = false
            }
        }

    }

    class MyCellViewHolder(itemView: View) : AbstractViewHolder(itemView) {
        public var timeRangeName = itemView?.findViewById<TextView>(R.id.txt_time_range_name)
    }


    override fun onCreateCornerView(): View {
        var v = LayoutInflater.from(context).inflate(R.layout.facility_resource_name_item, null)
        v?.txt_resource_name?.text = context?.getString(R.string.select_your_timeslot)

        v?.txt_resource_name?.layoutParams?.height = context?.resources?.getDimensionPixelOffset(R.dimen.dp_50)
        v?.txt_resource_name?.gravity = Gravity.NO_GRAVITY

        v?.txt_resource_name?.requestLayout()
        return v
    }

}