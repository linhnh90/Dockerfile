package com.styl.pa.modules.advancedSearch.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.styl.pa.R
import com.styl.pa.adapters.MultiSelectionAdapter
import com.styl.pa.customViews.TextViewBehavior
import com.styl.pa.customViews.datePicker.CustomDatePicker
import com.styl.pa.entities.advancedSearch.MultiSelectionObject
import com.styl.pa.entities.courseCategory.CourseCategory
import com.styl.pa.entities.generateToken.Outlet
import com.styl.pa.enums.SearchType
import com.styl.pa.enums.TrackingName
import com.styl.pa.interfaces.OnMultiSelectionSpinnerItem
import com.styl.pa.interfaces.TimeOutDialogEvent
import com.styl.pa.modules.advancedSearch.IAdvancedSearchContact
import com.styl.pa.modules.advancedSearch.presenter.AdvancedSearchPresenter
import com.styl.pa.modules.base.CustomBaseDialogFragment
import com.styl.pa.modules.dialog.MessageDialogFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.utils.*
import kotlinx.android.synthetic.main.fragment_advanced_search.view.*
import kotlinx.android.synthetic.main.multi_selection_popup.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Ngatran on 09/18/2018.
 */
class AdvancedSearchFragment : CustomBaseDialogFragment(), View.OnClickListener, IAdvancedSearchContact.IView, TimeOutDialogEvent {

    private var fragment: MessageDialogFragment? = null

    override fun setCurrentName() {
        super.setCurrentName()

        if (activity != null) {
            (activity as MainActivity).setCurrentViewName(TrackingName.AdvancedSearchFragment.value)
        }
    }

    private var locationMultiSelection = object : OnMultiSelectionSpinnerItem.OnMultiSelectionOutletSpinnerItem {
        override fun onClickMulti(arrayList: java.util.ArrayList<Outlet>, string: String,
                                  indexList: java.util.ArrayList<Int>) {
            getView?.txt_location?.text = string
            outletInfoIndexList.clear()
            outletInfoIndexList.addAll(indexList)
        }
    }
    private var categoryMultiSelection = object : OnMultiSelectionSpinnerItem.OnMultiSelectionCategorySpinnerItem {
        override fun onClickMulti(arrayList: java.util.ArrayList<CourseCategory>,
                                  string: String, indexList: java.util.ArrayList<Int>) {
            getView?.txt_categories?.text = string
            categoriesIndexList.clear()
            categoriesIndexList.addAll(indexList)
        }
    }

    private var preferredDayMultiSelection = object : OnMultiSelectionSpinnerItem.OnMultiSelectionStringSpinnerItem {
        override fun onClickMulti(arrayList: java.util.ArrayList<MultiSelectionObject>,
                                  string: String, indexList: java.util.ArrayList<Int>) {
            getView?.txt_preferred_day?.text = string
            preferredDayIndexList.clear()
            preferredDayIndexList.addAll(indexList)
        }
    }

    override fun onClick(v: View?) {
        GeneralUtils.hideSoftKeyboard(v)
        touchListener()
        when (v?.id) {
            R.id.edt_preferred_date -> {
                datePickerDialogUtils?.show()
            }

            R.id.txt_location -> {
                outletInfoAdapter?.isLocation = true
                createPopupWindow(outletInfoAdapter, getView?.txt_location)
            }

            R.id.txt_categories -> {
                categoriesInfoAdapter?.isLocation = false
                createPopupWindow(categoriesInfoAdapter, getView?.txt_categories)
            }

            R.id.txt_preferred_day -> {
                preferredDayAdapter?.isLocation = false
                createPopupWindow(preferredDayAdapter, getView?.txt_preferred_day)
            }

            R.id.img_close -> {
                dismiss()
            }

            R.id.btn_search -> {
                val keyword = getView?.edt_find_courses?.text.toString()

                if (isChooseLocation()) {
                    presenter?.goToAdvancedSearch(keyword, getStartDate(),
                            getOutletIds(), getOutletNames(), getCategories(), getSuitables(), getDays(), searchType)
                    dismiss()
                } else {
                    presenter?.pleaseChooseLocation()
                }
            }
        }
    }

    private fun showErrorMessage(titleResId: Int, messageResId: Int) {
        dismissLoading()

        if (fragment != null && fragment!!.isVisible) {
            fragment?.dismiss()
        }
        fragment = MessageDialogFragment.newInstance(titleResId, messageResId, true)
        if (fragmentManager != null) {
            fragment?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    private fun isChooseLocation(): Boolean {
        if (getOutletIds().size > 0)
            return true

        return false
    }

    private fun generateMultiSelectionStringAdapter(
            stringList: ArrayList<MultiSelectionObject>,
            event: OnMultiSelectionSpinnerItem.OnMultiSelectionStringSpinnerItem): MultiSelectionAdapter {
        val adapter = MultiSelectionAdapter(activity, MultiSelectionAdapter.STRING_TYPE)
        adapter.setStringList(stringList)
        adapter.setOnMultiSelectionStringSpinnerItem(event)
        return adapter
    }

    private fun generateMultiSelectionOutletAdapter(
            outletList: ArrayList<Outlet>,
            event: OnMultiSelectionSpinnerItem.OnMultiSelectionOutletSpinnerItem): MultiSelectionAdapter {
        val adapter = MultiSelectionAdapter(activity, MultiSelectionAdapter.OUTLET_TYPE)
        adapter.setOutletList(outletList)
        adapter.setOnMultiSelectionOutletSpinnerItem(event)
        return adapter
    }

    private fun generateMultiSelectionCategoryAdapter(
            categoryList: ArrayList<CourseCategory>,
            event: OnMultiSelectionSpinnerItem.OnMultiSelectionCategorySpinnerItem): MultiSelectionAdapter {
        val adapter = MultiSelectionAdapter(activity, MultiSelectionAdapter.CATEGORY_TYPE)
        adapter.setCategoryList(categoryList)
        adapter.setOnMultiSelectionSpinnerCategoryItem(event)
        return adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }

    private fun createPopupWindow(adapter: MultiSelectionAdapter?, view: TextView?) {
        val rect = GeneralUtils.locateView(view)

        val v = (activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.multi_selection_popup, null)

        v?.rcv_list?.layoutManager = LinearLayoutManager(activity)
        v?.rcv_list?.adapter = adapter

        adapter?.notifyDataSetChanged()

        var height = GeneralUtils.getScreenSize(activity).heightPixels - (rect?.top!! + rect.height() - GeneralUtils.getScreenBarStatus(false, activity))

        var marginTop = (rect.top + rect.height() - 24)

        if (height < 200) {
            if (rect.top < resources.getDimensionPixelOffset(R.dimen.dp_310)) {
                height = rect.top
            } else {
                height = resources.getDimensionPixelOffset(R.dimen.dp_310)
            }

            marginTop = rect.top - height
        }

        val popupWindow = PopupWindow(v, rect.width(), height, true)
        popupWindow.showAtLocation(getView, Gravity.START or Gravity.TOP, rect.left, marginTop)
    }

    private var sdf = SimpleDateFormat("dd/MM/yy", Locale.ENGLISH)

    private var getView: View? = null

    private var presenter: AdvancedSearchPresenter? = null

    private var suitabilityList: Array<String>? = null
    private var suitabilityAdapter: ArrayAdapter<String>? = null
    private var preferredDayList = ArrayList<MultiSelectionObject>()
    private var suitabilitySelected: String? = null

    private var categoriesInfoList = ArrayList<CourseCategory>()
    private var outletInfoList = ArrayList<Outlet>()

    private var preferredDayAdapter: MultiSelectionAdapter? = null
    private var categoriesInfoAdapter: MultiSelectionAdapter? = null
    private var outletInfoAdapter: MultiSelectionAdapter? = null

    private var preferredDayIndexList = ArrayList<Int>()
    private var categoriesIndexList = ArrayList<Int>()
    private var outletInfoIndexList = ArrayList<Int>()
    private var dateSelected: String? = ""

    private var courseCategoriesList = ArrayList<CourseCategory>()
    private var eventCategoriesList = ArrayList<CourseCategory>()

    private var searchType: SearchType = SearchType.COURSES

    private var datePickerDialogUtils: CustomDatePicker? = null

    private var datePickerListener = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth, 0, 0, 0)
            selectedCalendar.set(Calendar.MILLISECOND, 0)

            getView?.edt_preferred_date?.setText(sdf.format(selectedCalendar.time))
            dateSelected = DateUtils.formatDateLocal(selectedCalendar.time)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        getView = activity?.layoutInflater?.inflate(R.layout.fragment_advanced_search, null)

        isCancelable = false

        presenter = AdvancedSearchPresenter(this, activity)
        onSetEventDismissDialog(this)


        if (activity != null && getView?.img_bg != null) {
            Glide.with(activity!!)
                    .load(R.drawable.bg_advanced_search)
                    .into(getView?.img_bg!!)
        }

        val dialog = AlertDialog.Builder(activity)
                .setView(getView)
                .create()

        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (activity is MainActivity) {
            courseCategoriesList = (activity as MainActivity).getCategoryList()
            eventCategoriesList = (activity as MainActivity).getCategoryEventList()
            outletInfoList = (activity as MainActivity).getLocationList()
        }
        initLocationAdapter()
    }

    override fun onStart() {
        super.onStart()

        getView?.rb_course?.isChecked = true

        init()
        reloadButtonSearch()
    }

    override fun onResume() {
        super.onResume()

        val window = dialog?.window
        val metrics = GeneralUtils.getScreenSize(activity)
        window?.setGravity(Gravity.TOP or Gravity.START)
        window?.setLayout(metrics.widthPixels, metrics.heightPixels + GeneralUtils.getScreenBarStatus(false, activity))
    }

    override fun getTheme(): Int {
        return R.style.DialogUpDown
    }

    private fun generatePreferredDay() {
        PreferredDayUtils.preferredDays.forEach {
            preferredDayList.add(MultiSelectionObject(it.key))
        }
    }

    fun initDatePicker() {
        dateSelected = DateUtils.formatDateLocal(Calendar.getInstance().time)
        datePickerDialogUtils = CustomDatePicker(activity, R.style.CustomDatePickerStyle, datePickerListener,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DATE))
        datePickerDialogUtils?.setCurrentSelection(Calendar.getInstance())
        datePickerDialogUtils?.setMinDate(Calendar.getInstance().time)
        getView?.edt_preferred_date?.setText(sdf.format(Calendar.getInstance().time))
    }

    private fun init() {

        generatePreferredDay()

        initDatePicker()

        getView?.edt_preferred_date?.setFocusable(false)
        getView?.edt_preferred_date?.setOnClickListener(this)

        getView?.txt_location?.setOnClickListener(this)
        getView?.txt_categories?.setOnClickListener(this)
        getView?.txt_preferred_day?.setOnClickListener(this)

        suitabilityList = resources.getStringArray(R.array.suitability_list)
        suitabilityAdapter = setFontSpinner(suitabilityList)
        getView?.spn_suitability?.adapter = suitabilityAdapter
        getView?.spn_suitability?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                LogManager.d("onNothingSelected")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (pos > 0 && suitabilityList != null && pos < suitabilityList?.size!!) {
                    if (pos == 0)
                        suitabilitySelected = null
                    else
                        suitabilitySelected = suitabilityList?.get(pos)
                }
            }

        })

        preferredDayAdapter = generateMultiSelectionStringAdapter(preferredDayList, preferredDayMultiSelection)
        categoriesInfoAdapter = generateMultiSelectionCategoryAdapter(categoriesInfoList, categoryMultiSelection)

        getView?.rl_container?.setOnClickListener(this)
        getView?.btn_search?.setOnClickListener(this)
        getView?.img_close?.setOnClickListener(this)
        getView?.rg_option?.setOnCheckedChangeListener(checkedChangeEvent)

        getView?.edt_find_courses?.setOnTouchListener(
                TextViewBehavior.deleteEvent(getView?.edt_find_courses)
        )
        getView?.edt_find_courses?.addTextChangedListener(
                TextViewBehavior.changeTextEvent(getView?.edt_find_courses, activity, R.drawable.ic_course2)
        )
    }

    fun initLocationAdapter() {
        outletInfoAdapter = generateMultiSelectionOutletAdapter(outletInfoList, locationMultiSelection)
        outletInfoAdapter?.setSelectLocationListener(this)
    }

    private fun setFontSpinner(list: Array<String>?): ArrayAdapter<String> {
        val adapter = ArrayAdapter<String>(activity, R.layout.spinner_item, list)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_normal_text_item)

        return adapter
    }

    override fun getToken(): String? {
        return MySharedPref(activity).eKioskHeader
    }

    override fun setCourseCategoryList(courseCategoryList: ArrayList<CourseCategory>, isClear: Boolean) {
        if (isClear) {
            categoriesInfoList.clear()
            categoriesInfoList.addAll(courseCategoryList)
        } else {
            categoriesInfoList.addAll(courseCategoryList)
        }
    }

    private fun getStartDate(): String? {
        return dateSelected
    }

    private fun getOutletIds(): ArrayList<String> {
        val outletIds: ArrayList<String> = ArrayList()

        for (index in outletInfoIndexList) {
            if (index >= 0 && index < outletInfoList.size && !outletInfoList.get(index).getOutletId().isNullOrEmpty()) {
                outletIds.add(outletInfoList.get(index).getOutletId()!!)
            }
        }
        return outletIds
    }

    private fun getOutletNames(): ArrayList<String> {
        val outletNames: ArrayList<String> = ArrayList()

        for (index in outletInfoIndexList) {
            if (index >= 0 && index < outletInfoList.size && !outletInfoList.get(index).getFriendlyName().isNullOrEmpty()) {
                var name = outletInfoList.get(index).getFriendlyName()?.trim()
                // remove last two characters of outlet name in case of CCMC only
                if (true == name?.endsWith("cc", true)) {
                    name = name.substring(0, name.length - 2).trim()
                }
                if (!name.isNullOrEmpty()) {
                    outletNames.add(name)
                }
            }
        }
        return outletNames
    }

    private fun getCategories(): ArrayList<String>? {
        val categories: ArrayList<String>? = ArrayList()
        for (index in categoriesIndexList) {
            if (index >= 0 && index < categoriesInfoList.size && !categoriesInfoList.get(index).getCategoryId().isNullOrEmpty()) {
                categories?.add(categoriesInfoList.get(index).getCategoryId()!!)
            }
        }
        return categories
    }

    private fun getSuitables(): ArrayList<String>? {
        val suitables: ArrayList<String>? = ArrayList()
        if (!suitabilitySelected.isNullOrEmpty())
            suitables?.add(suitabilitySelected!!)
        return suitables
    }

    private fun getDays(): ArrayList<Int>? {
        return preferredDayIndexList
    }

    override fun onWarningSelectLocation() {
        showErrorMessage(R.string.only_4_locations)
    }

    private val checkedChangeEvent = object : RadioGroup.OnCheckedChangeListener {
        override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
            when (checkedId) {
                R.id.rb_course -> {
                    searchType = SearchType.COURSES
                    reloadButtonSearch()
                }
                R.id.rb_event -> {
                    searchType = SearchType.EVENTS
                    reloadButtonSearch()
                }
            }
        }
    }

    fun reloadButtonSearch() {
        categoriesIndexList.clear()
        preferredDayIndexList.clear()
        getView?.txt_categories?.text = ""
        if (searchType.toString().equals(SearchType.EVENTS.toString())) {
            setCourseCategoryList(eventCategoriesList, true)
            getView?.txt_title?.text = getString(R.string.find_your_events_here)
            getView?.edt_find_courses?.setHint(activity?.resources?.getString(R.string.advanced_search_event_hint))
            getView?.rb_course?.setBackgroundColor(Color.TRANSPARENT)
            getView?.rb_event?.setBackgroundResource(R.drawable.border_white_bg_trans_25)

            getView?.txt_preferred_day?.visibility = View.INVISIBLE
            getView?.txt_preferred_day_label?.visibility = View.INVISIBLE
            getView?.txt_preferred_day_suggest?.visibility = View.INVISIBLE

            getView?.txt_categories_label?.text = getString(R.string.select_event_categories)
        } else if (searchType.toString().equals(SearchType.COURSES.toString())) {
            setCourseCategoryList(courseCategoriesList, true)
            getView?.edt_find_courses?.setHint(activity?.resources?.getString(R.string.search_course_home_hint))
            getView?.txt_title?.text = getString(R.string.find_your_courses_here)
            getView?.rb_course?.setBackgroundResource(R.drawable.border_white_bg_trans_25)
            getView?.rb_event?.setBackgroundColor(Color.TRANSPARENT)

            getView?.txt_preferred_day?.visibility = View.VISIBLE
            getView?.txt_preferred_day_label?.visibility = View.VISIBLE
            getView?.txt_preferred_day_suggest?.visibility = View.VISIBLE

            getView?.txt_categories_label?.text = getString(R.string.select_course_categories)
        }
    }
}