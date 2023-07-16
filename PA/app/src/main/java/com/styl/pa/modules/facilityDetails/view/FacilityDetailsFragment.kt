package com.styl.pa.modules.facilityDetails.view


import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.evrencoskun.tableview.listener.ITableViewListener
import com.google.gson.Gson
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.adapters.FacilityAmountAdapter
import com.styl.pa.adapters.MyTableViewAdapter
import com.styl.pa.customViews.datePicker.CustomDatePicker
import com.styl.pa.customViews.datePicker.CustomDatePicker.Companion.MAX_TIME
import com.styl.pa.customViews.datePicker.CustomDatePicker.Companion.MIN_TIME
import com.styl.pa.customViews.timePicker.CustomTimePicker
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.facility.BookingByEmailRequest
import com.styl.pa.entities.facility.FacilitySessionInfo
import com.styl.pa.entities.facility.SlotSessionInfo
import com.styl.pa.entities.facility.SlotSessionInfo.CREATOR.AVAIL_TYPE
import com.styl.pa.entities.facility.SlotSessionInfo.CREATOR.BOOKING_TYPE
import com.styl.pa.entities.facility.SlotSessionInfo.CREATOR.PEAKING_TYPE
import com.styl.pa.entities.generateToken.Facility
import com.styl.pa.entities.generateToken.Outlet
import com.styl.pa.entities.generateToken.ResourceFeeList
import com.styl.pa.entities.kioskactivation.KioskInfo
import com.styl.pa.interfaces.AddToCartEvent
import com.styl.pa.modules.base.Base2Fragment
import com.styl.pa.modules.courseDetails.view.CourseDetailsFragment
import com.styl.pa.modules.dialog.LoadingFragment
import com.styl.pa.modules.dialog.MessageDialogFragment
import com.styl.pa.modules.dialog.WaitingResultFragment
import com.styl.pa.modules.facilityDetails.IFacilityDetailsContact
import com.styl.pa.modules.facilityDetails.presenter.FacilityDetailsPresenter
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.utils.*
import com.styl.pa.utils.GeneralTextUtil.Companion.getTextSizeZoomIn
import com.styl.pa.utils.GeneralTextUtil.Companion.getTextSizeZoomOut
import kotlinx.android.synthetic.main.facility_timeslot_item.view.*
import kotlinx.android.synthetic.main.fragment_facility_details.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 *
 */
class FacilityDetailsFragment : Base2Fragment(), IFacilityDetailsContact.IView {
    private val MIN_HOUR = 9
    private val MIN_MINUTE = 0
    private val MAX_HOUR = 21
    private val MAX_MINUTE = 59

    private var getView: View? = null
    private var facility: Facility? = null
    private var presenter: FacilityDetailsPresenter? = null
    private val loadingFragment = LoadingFragment()
    private var fragment: MessageDialogFragment? = null
    private var startDateSelected: String? = ""
    private var endDateSelected: String? = ""
    private var startDatePickerDialogUtils: CustomDatePicker? = null
    private var endDatePickerDialogUtils: CustomDatePicker? = null
    private var outlet: Outlet? = null
    private var isOnline: Boolean = false

    private var facilitySessionInfoList: ArrayList<FacilitySessionInfo> = ArrayList()

    private var timeRangeList: ArrayList<SlotSessionInfo> = ArrayList()

    private var maxResourceNameWidth = 0
    private var maxTimeRangeWidth = 0

    private var resourceWidth = 0

    private var facilityAmountList: ArrayList<ResourceFeeList> = ArrayList()
    private var facilityAmountAdapter: FacilityAmountAdapter? = null

    private var tableSessionAdapter: MyTableViewAdapter? = null
    private var listCell: ArrayList<ArrayList<SlotSessionInfo>> = ArrayList()

    private var columnNumber = -1

    private var distanceList: Array<String>? = null

    private var mLastClickTime = 0L

    private var keyboardListenersAttached = false

    private var maxAllowBookingDate = Calendar.getInstance()
    private var zoomMode = CourseDetailsFragment.ZOOM_NORMAL

    private val layoutListener = ViewTreeObserver.OnGlobalLayoutListener {

        val r = Rect()
        view?.rootView?.getWindowVisibleDisplayFrame(r)
        val screenHeight = view?.rootView?.height ?: 0

        // r.bottom is the position above soft keypad or device button.
        // if keypad is shown, the r.bottom is smaller than that before.
        val keypadHeight = screenHeight - r.bottom

        if (keypadHeight > screenHeight * 0.15) {
            showFacilityInfo(false)
        } else {
            showFacilityInfo(true)
        }
    }

    private var tableViewListener = object : ITableViewListener {
        override fun onCellLongPressed(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {
            if (row < listCell.size && row >= 0 && column < listCell.get(row).size && column >= 0
                    && true == cellView.itemView.rb_timeslot_item?.isEnabled) {

                if (listCell.get(row).get(column).mIsChoose == AVAIL_TYPE) {
                    cellView.itemView.rb_timeslot_item.isChecked = true
                    if (!listCell.get(row).get(column).mIsPeak) {
                        listCell.get(row).get(column).mIsChoose = BOOKING_TYPE
                    } else if (listCell.get(row).get(column).mIsPeak) {
                        listCell.get(row).get(column).mIsChoose = PEAKING_TYPE
                    }
                    columnNumber = column
                } else {
                    cellView.itemView.rb_timeslot_item.isChecked = false
                    listCell.get(row).get(column).mIsChoose = AVAIL_TYPE
                }

                tableSessionAdapter?.notifyDataSetChanged()
            }
        }

        override fun onColumnHeaderLongPressed(columnHeaderView: RecyclerView.ViewHolder, column: Int) {
            LogManager.d(TAG, "onColumnHeaderLongPressed")
        }

        override fun onRowHeaderClicked(rowHeaderView: RecyclerView.ViewHolder, row: Int) {
            LogManager.d(TAG, "onRowHeaderClicked")
        }

        override fun onColumnHeaderClicked(columnHeaderView: RecyclerView.ViewHolder, column: Int) {
            LogManager.d(TAG, "onColumnHeaderClicked")
        }

        override fun onRowHeaderLongPressed(rowHeaderView: RecyclerView.ViewHolder, row: Int) {
            LogManager.d(TAG, "onRowHeaderLongPressed")
        }

        override fun onCellClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {

            if (row < listCell.size && row >= 0 && column < listCell.get(row).size && column >= 0
                    && true == cellView.itemView.rb_timeslot_item?.isEnabled) {

                if (listCell.get(row).get(column).mIsChoose == AVAIL_TYPE) {
                    cellView.itemView.rb_timeslot_item.isChecked = true
                    if (!listCell.get(row).get(column).mIsPeak) {
                        listCell.get(row).get(column).mIsChoose = BOOKING_TYPE
                    } else if (listCell.get(row).get(column).mIsPeak) {
                        listCell.get(row).get(column).mIsChoose = PEAKING_TYPE
                    }
                    columnNumber = column
                } else {
                    cellView.itemView.rb_timeslot_item.isChecked = false
                    listCell.get(row).get(column).mIsChoose = AVAIL_TYPE
                }

                tableSessionAdapter?.notifyDataSetChanged()
            }
        }

    }

    var startTime: Date? = null
    var endTime: Date? = null

    private var startTimePickerListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)

        val lastTime = startTime
        startTime = c.time
        if (endTime != null && startTime != null && endDate != null && startDate != null) {
            val startTimeCalendar = Calendar.getInstance()
            startTimeCalendar.time = startDate
            val endTimeCalendar = Calendar.getInstance()
            endTimeCalendar.time = endDate
            if (startTimeCalendar.get(Calendar.DAY_OF_MONTH)
                    .compareTo(endTimeCalendar.get(Calendar.DAY_OF_MONTH)) == 0
                && startTimeCalendar.get(Calendar.HOUR_OF_DAY) > endTimeCalendar.get(Calendar.HOUR_OF_DAY)
            ) {
                startTime = lastTime
            }
        }

        if (startTime != null) {
            getView?.txt_start_time?.text = SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(startTime)
        }
    }

    private var endTimePickerListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)

        val lastTime = endTime
        endTime = c.time
        if (endTime != null && startTime != null && endDate != null && startDate != null) {
            val startTimeCalendar = Calendar.getInstance()
            startTimeCalendar.time = startDate
            val endTimeCalendar = Calendar.getInstance()
            endTimeCalendar.time = endDate
            if (startTimeCalendar.get(Calendar.DAY_OF_MONTH)
                    .compareTo(endTimeCalendar.get(Calendar.DAY_OF_MONTH)) == 0
                && startTimeCalendar.get(Calendar.HOUR_OF_DAY) > endTimeCalendar.get(Calendar.HOUR_OF_DAY)
            ) {
                endTime = lastTime
            }
        }

        if (endTime != null) {
            getView?.txt_end_time?.text = SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(endTime)
        }
    }

    private fun generateListCell() {
        listCell.clear()
        if (facilitySessionInfoList.size > 0) {
            var row = 0
            if (facilitySessionInfoList[0].mSlotList != null) {
                row = facilitySessionInfoList[0].mSlotList!!.size
            }

            if (row > 0) {
                for (i in 0..row - 1) {
                    val rowList = ArrayList<SlotSessionInfo>()
                    for (j in 0..facilitySessionInfoList.size - 1) {
                        rowList.add(facilitySessionInfoList[j].mSlotList?.get(i)!!)
                    }
                    listCell.add(rowList)
                }
            }
        }
    }

    private fun generateTimeRangeList() {
        timeRangeList.clear()
        if (facilitySessionInfoList.size > 0 && facilitySessionInfoList[0].mSlotList != null) {
            timeRangeList.addAll(facilitySessionInfoList[0].mSlotList!!)
        }
    }

    private fun generateMaxResourceNameWidth() {
        for (i in 0..facilitySessionInfoList.size - 1) {
            if (!TextUtils.isEmpty(facilitySessionInfoList.get(i).mResourceName)) {
                val widthText = GeneralTextUtil.calculateWidthString(activity!!, facilitySessionInfoList.get(i).mResourceName!!, R.dimen.text_size_small, R.font.opensans_bold)
                if (maxResourceNameWidth < widthText) {
                    maxResourceNameWidth = widthText
                }
            }
        }
    }

    private fun generateMaxTimeRangeWidth() {
        var resource = R.dimen.text_size_small
        if (zoomMode == CourseDetailsFragment.ZOOM_IN) {
            resource = R.dimen.text_size_mini
        } else if (zoomMode == CourseDetailsFragment.ZOOM_OUT) {
            resource = R.dimen.text_size_normal
        }
        for (i in 0..timeRangeList.size - 1) {
            if (!TextUtils.isEmpty(timeRangeList.get(i).mTimeRageName)) {
                val widthText = GeneralTextUtil.calculateWidthString(activity!!, timeRangeList.get(i).mTimeRageName!!, resource, R.font.opensans_bold)
                if (maxTimeRangeWidth < widthText) {
                    maxTimeRangeWidth = widthText + 20
                }
            }
        }

        val widthText = GeneralTextUtil.calculateWidthString(activity!!, getString(R.string.select_your_timeslot), resource, R.font.opensans_bold)
        if (maxTimeRangeWidth < widthText) {
            maxTimeRangeWidth = widthText
        }
    }


    private fun generateResourceWidth() {
        val screenSize = GeneralUtils.getScreenSize(activity)
        resourceWidth = ((screenSize.widthPixels * 2 / 3.2) - 2 * resources.getDimensionPixelSize(R.dimen.dp_50) - maxTimeRangeWidth).toInt()
    }

    private fun createResourceWidthItem() {
        if (maxResourceNameWidth * facilitySessionInfoList.size < resourceWidth) {
            maxResourceNameWidth = resourceWidth / facilitySessionInfoList.size
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        getView = inflater.inflate(R.layout.fragment_facility_details, container, false)

        maxResourceNameWidth = resources.getDimensionPixelSize(R.dimen.dp_50)
        resourceWidth = resources.getDimensionPixelSize(R.dimen.dp_50)
        maxTimeRangeWidth = GeneralTextUtil.calculateWidthString(activity!!, getString(R.string.select_your_timeslot), R.dimen.text_size_small, R.font.opensans_bold)


        presenter = FacilityDetailsPresenter(this, activity!!)
        getBundle()

        init()
        loadData()

        getView?.btn_back?.setOnClickListener(this)

        return getView
    }

    override fun onStart() {
        super.onStart()

        if (!keyboardListenersAttached) {
            view?.rootView?.viewTreeObserver?.addOnGlobalLayoutListener(layoutListener)
            keyboardListenersAttached = true
        }
    }

    override fun onStop() {
        super.onStop()

        if (keyboardListenersAttached) {
            view?.rootView?.viewTreeObserver?.removeOnGlobalLayoutListener(layoutListener)
            keyboardListenersAttached = false
        }
    }

    fun initDate() {
        val currentDate = Calendar.getInstance()

        //start preferred date
        startDatePickerDialogUtils = CustomDatePicker(activity!!, R.style.CustomDatePickerStyle, listenerStartDatePicker,
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DATE))
        startDatePickerDialogUtils?.setCurrentSelection(currentDate)

        //end preferred date
        endDatePickerDialogUtils = CustomDatePicker(activity!!, R.style.CustomDatePickerStyle, listenerEndDatePicker,
                currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE))
        endDatePickerDialogUtils?.setCurrentSelection(currentDate)
    }

    fun init() {
        getView?.v_opacity?.setBackgroundColor(GeneralUtils.getColorWithAlpha(Color.BLACK, 0.6f))
        isOnline = facility?.getIsBookable() ?: false

        reloadLayout()

        initDate()

        initFacilityAmount()

        if (isOnline) {
            getView?.txt_date?.text = DateUtils.getCurrentDate()
            startDateSelected = DateUtils.formatDateLocal(Calendar.getInstance().time)
            getView?.txt_date?.setOnClickListener(this)
            getView?.btn_next?.setOnClickListener(this)
            getView?.btn_search?.setOnClickListener(this)
//            presenter?.getFacilitySessionInfo(facility?.outletId, facility?.getResourceSubTypeId(), startDateSelected)
            presenter?.getFacilityAvailability(facility?.sku, startDateSelected)

            when (facility?.bookingInAdvanceType) {
                Facility.DAY_TYPE, Facility.DAYS_TYPE -> {
                    maxAllowBookingDate.add(Calendar.DAY_OF_YEAR, facility?.bookingInAdvance
                            ?: MAX_ALLOW_BOOKING_DATE)
                }

                Facility.MONTH_TYPE -> {
                    maxAllowBookingDate.add(Calendar.MONTH, facility?.bookingInAdvance
                            ?: MAX_ALLOW_BOOKING_DATE)
                }

                else -> {
                    maxAllowBookingDate.add(Calendar.DAY_OF_YEAR, MAX_ALLOW_BOOKING_DATE)
                }
            }
        } else {
            getView?.btn_submit_form?.setOnClickListener(this)
            getView?.txt_start_preferred_date?.setOnClickListener(this)
            getView?.txt_end_preferred_date?.setOnClickListener(this)
            getView?.txt_start_time?.setOnClickListener(this)
            getView?.txt_end_time?.setOnClickListener(this)

            initTime()
        }

        getView?.tv_zoom_out?.setOnClickListener(this)
        getView?.tv_zoom?.setOnClickListener(this)
        getView?.tv_zoom_in?.setOnClickListener(this)

        val canOrder = (activity as? MainActivity)?.canOrder ?: true
        getView?.btn_next?.isEnabled = canOrder
    }

    fun initTime() {
        distanceList = resources.getStringArray(R.array.distance_facilities)

        //start preferred time
        val startDistanceAdapter = ArrayAdapter<String>(activity, R.layout.spinner_facility_distance_item, distanceList)
        startDistanceAdapter.setDropDownViewResource(R.layout.spinner_facility_distance_drop_down_item)
        getView?.spn_start_distance?.adapter = startDistanceAdapter

        //end preferred time
        val endDistanceAdapter = ArrayAdapter<String>(activity, R.layout.spinner_facility_distance_item, distanceList)
        endDistanceAdapter.setDropDownViewResource(R.layout.spinner_facility_distance_drop_down_item)
        getView?.spn_end_distance?.adapter = endDistanceAdapter
    }

    private fun initFacilityAmount() {
        facilityAmountAdapter = FacilityAmountAdapter(facilityAmountList, activity)

        getView?.rcv_list_amount?.layoutManager = LinearLayoutManager(activity)
        getView?.rcv_list_amount?.adapter = facilityAmountAdapter
    }

    private fun reloadLayout() {
        if (isOnline) {
            getView?.ll_form_offline?.visibility = View.GONE
            getView?.ll_form_online?.visibility = View.VISIBLE
            getView?.ll_legend?.visibility = View.VISIBLE
            getView?.btn_next?.visibility = View.VISIBLE
            getView?.btn_submit_form?.visibility = View.GONE
            getView?.rcv_list_amount?.visibility = View.GONE
        } else {
            getView?.ll_form_offline?.visibility = View.VISIBLE
            getView?.ll_form_online?.visibility = View.GONE
            getView?.ll_legend?.visibility = View.INVISIBLE
            getView?.btn_next?.visibility = View.GONE
            getView?.btn_submit_form?.visibility = View.VISIBLE
            getView?.rcv_list_amount?.visibility = View.GONE
        }

        getView?.txt_rule_and_regulations?.setOnClickListener(this)
    }

    private fun initTableSession() {
        columnNumber = -1

        tableSessionAdapter = MyTableViewAdapter(activity, facilitySessionInfoList)
        tableSessionAdapter?.setWidthSize(maxResourceNameWidth)
        getView?.tbv_list_session?.adapter = tableSessionAdapter

        getView?.tbv_list_session?.cellRecyclerView?.removeOnScrollListener(tableScrollListener)
        getView?.tbv_list_session?.cellRecyclerView?.addOnScrollListener(tableScrollListener)

        getView?.tbv_list_session?.rowHeaderRecyclerView?.removeOnScrollListener(tableScrollListener)
        getView?.tbv_list_session?.rowHeaderRecyclerView?.addOnScrollListener(tableScrollListener)

        tableSessionAdapter?.setAllItems(facilitySessionInfoList, timeRangeList, listCell as List<MutableList<SlotSessionInfo>>?)

        getView?.tbv_list_session?.setHasFixedWidth(false)
        getView?.tbv_list_session?.rowHeaderWidth = maxTimeRangeWidth

        getView?.tbv_list_session?.tableViewListener = this.tableViewListener

        scrollItem = ((getView?.ll_scroll_container?.height
                ?: 250) * 1f - (getView?.v_scroll?.height ?: 200) * 1f) / (timeRangeList.size - 4)
    }

    private var tableScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val position = (recyclerView?.layoutManager as LinearLayoutManager)
                    .findFirstCompletelyVisibleItemPosition()
            animationScrollView(position)
        }
    }

    private var scrollItem = 20F
    private var valueAnimator: ValueAnimator? = null
    private fun animationScrollView(position: Int) {
        animationDestroy()
        valueAnimator = ObjectAnimator.ofFloat(getView?.v_scroll, "translationY", scrollItem * position).apply {
            duration = 80
            start()
        }

    }

    private fun animationDestroy() {
        valueAnimator?.removeAllUpdateListeners()
        valueAnimator?.removeAllListeners()
        valueAnimator?.pause()
        valueAnimator?.end()
        valueAnimator?.cancel()
        valueAnimator = null
    }

    private fun loadData() {
        if (facility != null) {

            getView?.txt_facility_code?.text = facility?.getResourceID()
            if (true == facility?.getIsBookable()) {
                getView?.txt_facility_name?.text = facility?.getResourceSubTypeName()
            } else {
                getView?.txt_facility_name?.text = facility?.getResourcetName()
            }

            val options = RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_pa_default)
                    .error(R.drawable.ic_pa_default)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
            Glide.with(activity!!).load(facility?.getImageUrl())
                    .apply(options)
                    .into(getView?.img_logo!!)

            if (facility?.getResourceFeeList() != null && facility?.getResourceFeeList()!!.size > 0) {
                facilityAmountList.addAll(facility?.getResourceFeeList()!!)
                facilityAmountAdapter?.notifyDataSetChanged()
            }

            val priceRange = facility?.getPriceRange() ?: FloatArray(2)
            val minFee = priceRange[0]
            val maxFee = priceRange[1]

            if (minFee == maxFee) {
                getView?.tv_price_member?.text = GeneralUtils.formatAmountSymbols("$", minFee, 2)
            } else {
                getView?.tv_price_member?.text = (GeneralUtils.formatAmountSymbols("$", minFee, 2) + "-" + GeneralUtils.formatAmountSymbols("$", maxFee, 2))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showProgressPayment(false)
        setTitle(facility?.outletName ?: "")
        getView?.txt_organize_name?.text = facility?.outletName
    }

    companion object {
        private val TAG = FacilityDetailsFragment::class.java.simpleName

        private const val ARG_FACILITY = BuildConfig.APPLICATION_ID + ".args.ARG_FACILITY"
        private const val ARG_OUTLET_DETAIL = BuildConfig.APPLICATION_ID + ".args.ARG_OUTLET_DETAIL"

        private const val MAX_ALLOW_BOOKING_DATE = 15

        fun newInstance(facility: Facility, outlet: Outlet): FacilityDetailsFragment {
            val f = FacilityDetailsFragment()
            val args = Bundle()
            args.putParcelable(ARG_FACILITY, facility)
            args.putParcelable(ARG_OUTLET_DETAIL, outlet)
            f.arguments = args
            return f
        }
    }

    private fun getBundle() {
        if (arguments != null) {
            facility = arguments?.getParcelable(ARG_FACILITY)
            outlet = arguments?.getParcelable(ARG_OUTLET_DETAIL)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }

    override fun onGetFacilitySessionInfoSuccess(facilitySessionInfo: ArrayList<FacilitySessionInfo>?) {

        if (facilitySessionInfo != null && facilitySessionInfo.size > 0) {
            getView?.tbv_list_session?.visibility = View.VISIBLE
            getView?.ll_scroll_container?.visibility = View.VISIBLE

            this.facilitySessionInfoList.clear()

            this.facilitySessionInfoList.addAll(facilitySessionInfo)

            generateListCell()
            generateTimeRangeList()

            generateMaxTimeRangeWidth()
            generateMaxResourceNameWidth()
            generateResourceWidth()
            createResourceWidthItem()

            initTableSession()

        } else {
            showErrorMessage(R.string.no_available_time_slot)
            getView?.tbv_list_session?.visibility = View.INVISIBLE
            getView?.ll_scroll_container?.visibility = View.INVISIBLE
        }
    }

    override fun onGetFacilitySessionInfoFail() {
        showErrorMessage(R.string.no_available_time_slot)
        getView?.tbv_list_session?.visibility = View.INVISIBLE
        getView?.ll_scroll_container?.visibility = View.INVISIBLE
    }

    override fun showLoading() {
        if (!loadingFragment.isVisible && fragmentManager != null) {
            loadingFragment.show(fragmentManager!!, LoadingFragment::class.java.simpleName)
        }
    }

    override fun dismissLoading() {
        if (loadingFragment.isAdded) {
            loadingFragment.dismiss()
        }
    }

    override fun <T> showErrorMessage(response: BaseResponse<T>) {
        fragment?.dismiss()

        if (response.errorCode < 0) {
            if (response.messageResId != null && response.messageResId!!.compareTo(0) > 0) {
                fragment = MessageDialogFragment.newInstance(0, response.messageResId!!)
            } else {
                fragment = MessageDialogFragment.newInstance("", response.errorMessage!!)
            }
        } else {
            fragment = MessageDialogFragment.newInstance(
                    response.errorCode,
                    "",
                    response.errorMessage!!
            )
        }
        if (fragmentManager != null) {
            fragment?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun showErrorMessage(messageResId: Int) {
        fragment?.dismiss()

        fragment = MessageDialogFragment.newInstance(0, messageResId)
        if (fragmentManager != null) {
            fragment?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    private fun showMessageAndTitle(messageResId: Int, title: Int) {
        fragment?.dismiss()
        fragment = MessageDialogFragment.newInstance(title, messageResId, true)
        if (fragmentManager != null) {
            fragment?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    var endDate: Date? = null
    var startDate: Date? = null

    private var listenerStartDatePicker = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            val c = Calendar.getInstance()
            c.set(year, month, dayOfMonth, 0, 0, 0)

            if ((endDate != null && c.time <= endDate) || (endDate == null)) {
                getView?.txt_date?.text = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(c.time)
                getView?.txt_start_preferred_date?.text = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(c.time)

                startDate = c.time
                startDateSelected = DateUtils.formatDateLocal(startDate!!)
            }
        }
    }

    private var listenerEndDatePicker = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            val c = Calendar.getInstance()
            c.set(year, month, dayOfMonth, 0, 0, 0)

            if ((startDate != null && c.time >= startDate) || (startDate == null)) {
                getView?.txt_end_preferred_date?.text = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(c.time)

                endDate = c.time
                endDateSelected = DateUtils.formatDateLocal(endDate!!)
            }

            if (isOnline) {
//                presenter?.getFacilitySessionInfo(facility?.outletId, facility?.getResourceSubTypeId(), startDateSelected)
                presenter?.getFacilityAvailability(facility?.sku, startDateSelected)
            }
        }
    }

    private var navigationView = object : AddToCartEvent.NavigationView {
        override fun navigation() {
            presenter?.navigationCardPage()
        }

    }

    override fun onClick(p0: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < MainActivity.CLICK_TIMER) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        animationDestroy()

        when (p0?.id) {
            R.id.txt_date -> {
                val c = Calendar.getInstance()
//                startDatePickerDialogUtils?.setMinDate(c.time)
//                c.add(Calendar.DAY_OF_YEAR, 14)
//                startDatePickerDialogUtils?.setMaxDate(c.time)
//                startDatePickerDialogUtils?.show()
                val minDate = (c.clone() as Calendar).time

                startDatePickerDialogUtils?.setDate(minDate, maxAllowBookingDate.time, minDate)
                startDatePickerDialogUtils?.show()
            }
            R.id.txt_start_preferred_date -> {
                val c = Calendar.getInstance()
                c.add(Calendar.MONTH, MIN_TIME)
                val minDate = c.time
                val cal = Calendar.getInstance()
                cal.add(Calendar.MONTH, MAX_TIME)
                val maxDate = cal.time

                startDatePickerDialogUtils?.setDate(minDate, endDate ?: maxDate, startDate
                        ?: minDate)
                startDatePickerDialogUtils?.show()
            }
            R.id.txt_end_preferred_date -> {
                val c = Calendar.getInstance()
                c.add(Calendar.MONTH, MIN_TIME)
                val minDate = c.time
                val cal = Calendar.getInstance()
                cal.add(Calendar.MONTH, MAX_TIME)
                val maxDate = cal.time

                endDatePickerDialogUtils?.setDate(startDate ?: minDate, maxDate, endDate ?: minDate)
                endDatePickerDialogUtils?.show()
            }
            R.id.btn_next -> {
                doNext()
            }
            R.id.btn_submit_form -> {
                val name = getView?.edt_name?.text.toString()
                val email = getView?.edt_email?.text.toString()
                val mobile = getView?.edt_mobile?.text.toString()
                val purpose = getView?.edt_purpose?.text.toString()
                val startDate = getView?.txt_start_preferred_date?.text.toString()
                val startTime = getView?.txt_start_time?.text.toString()
                val duration = getView?.spn_start_distance?.selectedItem.toString()

                if (presenter?.validate(name, email, mobile, purpose, startDate, startTime, duration) == true) {

//                    var content = getString(R.string.facility_content, getView?.edt_name?.text.toString(),
//                            getView?.edt_email?.text.toString(),
//                            getView?.edt_mobile?.text.toString(),
//                            getView?.edt_purpose?.text.toString(),
//                            getView?.txt_start_preferred_date?.text.toString(),
//                            getView?.txt_start_time?.text.toString(),
//                            getView?.spn_start_distance?.selectedItem.toString())

                    val receivers = ArrayList<String>()
                    if (!TextUtils.isEmpty(MySharedPref(activity).kioskInfo)) {
                        val objects = Gson().fromJson(MySharedPref(activity).kioskInfo, KioskInfo::class.java)

                        if (!objects?.outlet?.getEmail().isNullOrEmpty()) {
                            receivers.add(objects?.outlet?.getEmail()!!)
                        }
                    }
//                    var subject = getString(R.string.facility_subject)

                    var companyName = getView?.edt_company?.text.toString()
                    if (companyName.isBlank()) {
                        companyName = ""
                    }
                    var roc = getView?.edt_roc?.text.toString()
                    if (roc.isBlank()) {
                        roc = ""
                    }

                    var endDate = getView?.txt_end_preferred_date?.text.toString()
                    if (endDate.isBlank()) {
                        endDate = ""
                    }
                    var endTime = getView?.txt_end_time?.text.toString()
                    val endDuration: String
                    if (endTime.isBlank()) {
                        endTime = ""
                        endDuration = ""
                    } else {
                        endDuration = getView?.spn_end_distance?.selectedItem.toString()
                    }

                    val request = BookingByEmailRequest(name, startDate,
                            facility?.getResourcetName(), email, mobile,
                            purpose, startTime,
                            duration, companyName, roc, endDate,
                            endTime, endDuration)

                    presenter?.sendEmail(request)
                } else {
                    showMessageAndTitle(R.string.complete_form, 0)
                }
            }
            R.id.btn_search -> {
                if (isOnline) {
//                    presenter?.getFacilitySessionInfo(facility?.outletId, facility?.getResourceSubTypeId(), startDateSelected)
                    presenter?.getFacilityAvailability(facility?.sku, startDateSelected)
                }
            }

            R.id.txt_start_time -> {
                val c = Calendar.getInstance()
                var currentHour = c.get(Calendar.HOUR_OF_DAY)
                var currentMinute = c.get(Calendar.MINUTE)
                if (currentHour < MIN_HOUR || currentHour > MAX_HOUR || currentHour == MIN_HOUR && currentMinute < MIN_MINUTE || currentHour == MAX_MINUTE && currentMinute > MAX_MINUTE) {
                    currentHour = 9
                    currentMinute = 0
                }
                val customTimePickerDialog = CustomTimePicker(activity!!, R.style.CustomTimePickerStyle, startTimePickerListener, currentHour, currentMinute, true)
                customTimePickerDialog.setMin(MIN_HOUR, MIN_MINUTE)
                customTimePickerDialog.setMax(MAX_HOUR, MAX_MINUTE)
                customTimePickerDialog.show()
            }

            R.id.txt_end_time -> {
                val c = Calendar.getInstance()
                var currentHour = c.get(Calendar.HOUR_OF_DAY)
                var currentMinute = c.get(Calendar.MINUTE)
                if (currentHour < MIN_HOUR || currentHour > MAX_HOUR || currentHour == MIN_HOUR && currentMinute < MIN_MINUTE ||
                        currentHour == MAX_MINUTE && currentMinute > MAX_MINUTE) {
                    currentHour = 9
                    currentMinute = 0
                }
                val customTimePickerDialog = CustomTimePicker(activity!!, R.style.CustomTimePickerStyle, endTimePickerListener, currentHour, currentMinute, true)
                customTimePickerDialog.setMin(MIN_HOUR, MIN_MINUTE)
                customTimePickerDialog.setMax(MAX_HOUR, MAX_MINUTE)
                customTimePickerDialog.show()
            }

            R.id.txt_rule_and_regulations -> {
                presenter?.getRuleAndRegulations()
            }
            R.id.btn_back -> {
                fragmentManager?.popBackStack()
            }
            R.id.tv_zoom_out -> {
                getView?.let {
                    changeSizeText(it, CourseDetailsFragment.ZOOM_OUT)
                }
            }
            R.id.tv_zoom -> {
                getView?.let {
                    changeSizeText(it, CourseDetailsFragment.ZOOM_NORMAL)
                }
            }
            R.id.tv_zoom_in -> {
                getView?.let {
                    changeSizeText(it, CourseDetailsFragment.ZOOM_IN)
                }
            }
        }
    }

    private fun doNext() {
        val cartItems = ArrayList<CartItem>()
        var isNotSelected = true
        var totalSelectedSlot = 0
        for (column in facilitySessionInfoList.indices) {
            val selectedSlots = ArrayList<SlotSessionInfo>()
            if (facilitySessionInfoList[column].mSlotList != null) {
                for (row in facilitySessionInfoList[column].mSlotList!!.indices) {
                    if (listCell[row][column].mIsChoose != SlotSessionInfo.AVAIL_TYPE) {
                        selectedSlots.add(listCell[row][column])
                    }
                }
            }

            if (selectedSlots.size > 0) {
                totalSelectedSlot += selectedSlots.size
                val timeSelection = GeneralUtils.formatToDefaultTime(getView?.txt_date?.text.toString())
//                val timeSelection = getView?.txt_date?.text.toString()
                val selectedFacility = facility?.copyFacility()
                selectedFacility?.crmResourceId = facilitySessionInfoList[column].mResourceId
                selectedFacility?.setResourcetName(facility?.getResourceSubTypeName() + " " + facilitySessionInfoList[column].mResourceName)
                val cartItem = CartItem(UUID.randomUUID().toString(), null, selectedFacility, null, null, null, timeSelection, selectedSlots)
                cartItems.add(cartItem)
                isNotSelected = false
            }
        }

        if (totalSelectedSlot > 1) {
            showErrorMessage(R.string.booking_number_slot_error)
            return
        }

        if (!isNotSelected) {
            val isBookingMyself = isBookingMyself()
            if (isBookingMyself) {
                (activity as? MainActivity)?.doQuickBook(cartItems)
            } else {
                (activity as? MainActivity)?.doQuickBook(cartItems, isBookingMyself = false)
            }
        } else {
            showErrorMessage(R.string.select_time_slot)
        }
    }

    private fun isBookingMyself(): Boolean{
        return getView?.rg_profile?.checkedRadioButtonId == R.id.rb_booking_myself
    }


    override fun getToken(): String? {
        return MySharedPref(activity).eKioskHeader
    }

    override fun sendEmailSuccess() {
        val f = MessageDialogFragment.newInstance(R.string.thanks, getString(R.string.sent_email_success), true)
        f.setListener(object : MessageDialogFragment.MessageDialogListener {
            override fun onNegativeClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onNegativeClickListener")
            }

            override fun onPositiveClickListener(dialogFragment: DialogFragment) {
                LogManager.d(TAG, "onPositiveClickListener")
            }

            override fun onNeutralClickListener(dialogFragment: DialogFragment) {
                fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

            }

        })
        if (fragmentManager != null) {
            f.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    private var waitingSendEmailDialog: WaitingResultFragment? = null

    override fun showWaitingDialog() {
        waitingSendEmailDialog = WaitingResultFragment()
        waitingSendEmailDialog?.setActionName(WaitingResultFragment.EMAIL_EVENT)
        if (fragmentManager != null) {
            waitingSendEmailDialog?.show(fragmentManager!!, WaitingResultFragment::class.java.simpleName)
        }
    }

    override fun dismissWaitingDialog() {
        waitingSendEmailDialog?.dismiss()
        waitingSendEmailDialog = null
    }

    private fun showFacilityInfo(isShown: Boolean) {
        if (isShown) {
            view?.img_logo?.visibility = View.VISIBLE
            view?.txt_facility_name?.visibility = View.VISIBLE
            view?.txt_rule_and_regulations?.visibility = View.VISIBLE
            view?.txt_organize_name?.visibility = View.VISIBLE
        } else {
            view?.img_logo?.visibility = View.GONE
            view?.txt_facility_name?.visibility = View.GONE
            view?.txt_rule_and_regulations?.visibility = View.GONE
            view?.txt_organize_name?.visibility = View.GONE
        }
    }

    private fun changeSizeText(getView: View, mode: Int) {
        zoomMode = mode

        generateMaxTimeRangeWidth()
        generateMaxResourceNameWidth()
        tableSessionAdapter?.setZoomMode(mode)
        tableSessionAdapter?.setWidthSize(maxResourceNameWidth)
        getView.tbv_list_session?.rowHeaderWidth = maxTimeRangeWidth
        tableSessionAdapter?.notifyDataSetChanged()

        when(mode) {
            CourseDetailsFragment.ZOOM_IN -> {
                getView.txt_facility_code.textSize = getTextSizeZoomIn(context, R.dimen.text_size_normal)
                getView.txt_facility_name.textSize = getTextSizeZoomIn(context, R.dimen.text_size_large)
                getView.txt_organize_name.textSize = getTextSizeZoomIn(context, R.dimen.text_size_small)
                getView.txt_rule_and_regulations.textSize = getTextSizeZoomIn(context, R.dimen.text_size_small)
                getView.rb_not_available.textSize = getTextSizeZoomIn(context, R.dimen.text_size_small)
                getView.rb_normal.textSize = getTextSizeZoomIn(context, R.dimen.text_size_small)
                getView.rb_peak.textSize = getTextSizeZoomIn(context, R.dimen.text_size_small)
                getView.rb_booked.textSize = getTextSizeZoomIn(context, R.dimen.text_size_small)
                getView.txt_date.textSize = getTextSizeZoomIn(context, R.dimen.text_size_normal)
                getView.txt_start_time.textSize = getTextSizeZoomIn(context, R.dimen.text_size_medium)
                getView.tv_select_a_day.textSize = getTextSizeZoomIn(context, R.dimen.text_size_small)


                getView.txt_detail_amount_note.textSize = getTextSizeZoomIn(context, R.dimen.text_size_lmini)
                getView.tv_txn_pa.textSize = getTextSizeZoomIn(context, R.dimen.text_size_mini)
                getView.tv_legend.textSize = getTextSizeZoomIn(context,R.dimen.text_size_normal)
                getView.tv_price_member.textSize = getTextSizeZoomIn(context, R.dimen.text_size_xxhlarge)
            }
            CourseDetailsFragment.ZOOM_NORMAL -> {
                getView.txt_facility_code.textSize = context?.resources?.getDimension(R.dimen.text_size_normal)?: 0f
                getView.txt_facility_name.textSize = context?.resources?.getDimension(R.dimen.text_size_large)?: 0f
                getView.txt_organize_name.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_rule_and_regulations.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.rb_not_available.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.rb_normal.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.rb_peak.textSize =  context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.rb_booked.textSize =  context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_date.textSize = context?.resources?.getDimension(R.dimen.text_size_normal)?: 0f
                getView.txt_start_time.textSize = context?.resources?.getDimension(R.dimen.text_size_medium)?: 0f
                getView.tv_select_a_day.textSize= context?.resources?.getDimension(R.dimen.text_size_small)?: 0f

                getView.txt_detail_amount_note.textSize = context?.resources?.getDimension(R.dimen.text_size_lmini)?: 0f
                getView.tv_txn_pa.textSize = context?.resources?.getDimension(R.dimen.text_size_mini)?: 0f
                getView.tv_legend.textSize =  context?.resources?.getDimension(R.dimen.text_size_normal)?: 0f
                getView.tv_price_member.textSize = context?.resources?.getDimension(R.dimen.text_size_xxhlarge)?: 0f
            }
            CourseDetailsFragment.ZOOM_OUT -> {
                getView.txt_facility_code.textSize = getTextSizeZoomOut(context, R.dimen.text_size_normal)
                getView.txt_facility_name.textSize = getTextSizeZoomOut(context, R.dimen.text_size_large)
                getView.txt_organize_name.textSize =getTextSizeZoomOut(context, R.dimen.text_size_small)
                getView.txt_rule_and_regulations.textSize = getTextSizeZoomOut(context, R.dimen.text_size_small)
                getView.rb_not_available.textSize = getTextSizeZoomOut(context, R.dimen.text_size_small)
                getView.rb_normal.textSize = getTextSizeZoomOut(context, R.dimen.text_size_small)
                getView.rb_peak.textSize = getTextSizeZoomOut(context, R.dimen.text_size_small)
                getView.rb_booked.textSize = getTextSizeZoomOut(context, R.dimen.text_size_small)
                getView.txt_date.textSize = getTextSizeZoomOut(context, R.dimen.text_size_normal)
                getView.txt_start_time.textSize = getTextSizeZoomOut(context, R.dimen.text_size_medium)
                getView.tv_select_a_day.textSize = getTextSizeZoomOut(context, R.dimen.text_size_small)

                getView.txt_detail_amount_note.textSize = getTextSizeZoomOut(context, R.dimen.text_size_lmini)
                getView.tv_txn_pa.textSize = getTextSizeZoomOut(context, R.dimen.text_size_mini)
                getView.tv_legend.textSize = getTextSizeZoomOut(context, R.dimen.text_size_normal)
                getView.tv_price_member.textSize = getTextSizeZoomOut(context, R.dimen.text_size_xxhlarge)
            }
        }
    }

    override fun setCanOrder(isAlive: Boolean) {
        getView?.btn_next?.isEnabled = isAlive
    }

}
