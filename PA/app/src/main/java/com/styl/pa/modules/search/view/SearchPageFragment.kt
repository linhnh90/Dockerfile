package com.styl.pa.modules.search.view


import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.adapters.ProductAdapter
import com.styl.pa.adapters.ProximityAdapter
import com.styl.pa.adapters.SearchLocationAdapter
import com.styl.pa.customViews.TextViewBehavior
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.advancedSearch.AdvancedSearchRequest
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.event.EventResponse
import com.styl.pa.entities.generateToken.*
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.entities.interestgroup.PageByInterestGroup
import com.styl.pa.entities.product.Product
import com.styl.pa.entities.product.ProductListResponse
import com.styl.pa.entities.proximity.ProximityLocationInfo
import com.styl.pa.entities.proximity.ProximityLocationResponse
import com.styl.pa.entities.recommendatetions.RecommendationItem
import com.styl.pa.entities.search.PriceClass
import com.styl.pa.entities.search.SearchProductRequest
import com.styl.pa.enums.SearchType
import com.styl.pa.interfaces.AddToCartEvent
import com.styl.pa.interfaces.OnClickRecyclerViewItem
import com.styl.pa.modules.base.Base2Fragment
import com.styl.pa.modules.dialog.LoadingFragment
import com.styl.pa.modules.dialog.MessageDialogFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.proximity.ProximityHandle
import com.styl.pa.modules.search.ISearchContact
import com.styl.pa.modules.search.presenter.SearchPresenter
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.LogManager
import kotlinx.android.synthetic.main.fragment_search_page.view.*
import java.util.*


class SearchPageFragment : Base2Fragment(), SeekBar.OnSeekBarChangeListener, OnClickRecyclerViewItem, ISearchContact.IView {

    override fun onClick(view: View, position: Int) {
        if (SystemClock.elapsedRealtime() - MainActivity.globalLastClickTime < MainActivity.CLICK_TIMER) {
            return
        }
        MainActivity.globalLastClickTime = SystemClock.elapsedRealtime()

        val product = productList[position]
        if (product.typeItem == SearchType.FACILITIES.toString()) {
            if (position >= 0 && position < productList.size && outlet != null) {
                val facility = product.infoItem as Facility
                presenter?.navigationFacilityDetailPage(facility, outlet!!)
            }
        } else if (product.typeItem == SearchType.EVENTS.toString()) {
            if (position >= 0 && position < productList.size) {
                val event = product.infoItem as EventInfo
                presenter?.navigationEventDetailPage(event)
            }
        } else if (product.typeItem == SearchType.COURSES.toString() && position >= 0
                && position < productList.size) {
            val clazz = product.infoItem as ClassInfo
            checkClassVacancy(clazz, false, false)
        } else if (product.typeItem == SearchType.INTEREST_GROUPS.toString() && position >= 0 && position < productList.size){
            val ig = product.infoItem as InterestGroup
            checkIgVacancy(ig, false, false)
        }
    }

    fun getSearchViewType(): String? {
        return searchType
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // Do nothing
        return
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        maxPrice = getView?.sb_price?.progress!!.toInt()
    }

    fun setThumbPosition(seekBar: SeekBar?, progress: Int) {
        val bounds = seekBar?.getThumb()?.bounds
        getView?.txt_price?.setText(GeneralUtils.formatAmountSymbols("$", progress.toFloat(), 0))
        var pos = (seekBar?.getLeft()!! + bounds?.left!!).toFloat() + bounds.width()/2f - ((getView?.txt_price?.width?: 0)/2f)
        if (pos < 5f) {
            pos = 5f
        }
        getView?.ll_price?.setTranslationX(pos - 3)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//        getView?.sb_price?.setThumb(getThumb(progress))

        setThumbPosition(seekBar, progress)
    }

    companion object {

        private val TAG = SearchPageFragment::class.java.simpleName

        private const val MAX_PRICE = 2000

        private const val ARG_SEARCH_TYPE = BuildConfig.APPLICATION_ID + ".args.ARG_SEARCH_TYPE"
        private const val ARG_KEYWORD = BuildConfig.APPLICATION_ID + ".args.ARG_KEYWORD"
        private const val ARG_LOCATION = BuildConfig.APPLICATION_ID + ".args.ARG_LOCATION"
        private const val ARG_CLASS_LIST = BuildConfig.APPLICATION_ID + ".args.ARG_CLASS_LIST"
        private const val ARG_FROM_SEARCH = BuildConfig.APPLICATION_ID + ".args.FROM_SEARCH"
        private const val ARG_ADVANCED_SEARCH = BuildConfig.APPLICATION_ID + ".args.ARG_ADVANCED_SEARCH"

        fun newInstance(searchType: String?, keyword: String?, location: Outlet?,
                        classList: ArrayList<ClassInfo>?, isFromSearch: Boolean): SearchPageFragment {
            val f = SearchPageFragment()
            val args = Bundle()
            args.putString(ARG_SEARCH_TYPE, searchType)
            args.putString(ARG_KEYWORD, keyword)
            args.putParcelable(ARG_LOCATION, location)
            args.putParcelableArrayList(ARG_CLASS_LIST, classList)
            args.putBoolean(ARG_FROM_SEARCH, isFromSearch)
            f.arguments = args
            return f
        }

        fun newInstance(searchType: String?, advancedSearchRequest: AdvancedSearchRequest?): SearchPageFragment {
            val f = SearchPageFragment()
            val args = Bundle()
            args.putString(ARG_SEARCH_TYPE, searchType)
            args.putParcelable(ARG_ADVANCED_SEARCH, advancedSearchRequest)
            f.arguments = args
            return f
        }
    }

    private var presenter: SearchPresenter? = null

    private var getView: View? = null

    private var searchType: String? = ""
    private var keyword: String? = ""
    private var outlet: Outlet? = null
    private var isFromSearch: Boolean = false

    private val loadingFragment = LoadingFragment()
    private var fragment: MessageDialogFragment? = null

    private var locationAdapter: SearchLocationAdapter? = null
    private var locationList: ArrayList<Outlet>? = ArrayList()
    private var classInfoList = ArrayList<ClassInfo>()
//    private var searchByCourseAdapter: SearchByCourseAdapter? = null
    private var pageByClassInfo: PageByClassInfo? = null

    //search facility
//    private var searchByFacilityAdapter: SearchByFacilityAdapter? = null
    private var facilityList: ArrayList<Facility> = ArrayList()
    private var pageByFacility: PageByFacility? = null

    private var productListResponse: ProductListResponse? = null

    //search interest group
    private var igList: ArrayList<InterestGroup> = ArrayList()
    private var pageByInterestGroup: PageByInterestGroup? = null

    //search event
//    private var searchByEventAdapter: SearchAdapter<EventInfo>? = null
    private var eventList: ArrayList<EventInfo> = ArrayList()
    private var pageByEvent: EventResponse? = null

    private var maxPrice = MAX_PRICE
    private var preferredDaysUtils: PreferredDaysUtils? = null
    private var isFirst = false
    private var outletTypeName: String? = null
    private var isFilter: Boolean = false
    private var mLastClickTime: Long = 0

    //advanced search
    private var advancedSearchRequest: AdvancedSearchRequest? = null
    private var isFirstAdvancedSearch = true

    private var distanceOption: Array<String>? = null

    private var proximityHandle: ProximityHandle? = null

    // General search
    private var productAdapter: ProductAdapter? = null
    private var productList: ArrayList<RecommendationItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getBundle()
    }

    private fun getBundle() {
        if (arguments != null) {
            searchType = arguments?.getString(ARG_SEARCH_TYPE)
            keyword = arguments?.getString(ARG_KEYWORD)
            outlet = arguments?.getParcelable(ARG_LOCATION)
            if (arguments?.containsKey(ARG_CLASS_LIST)!!)
                classInfoList = ArrayList(arguments?.getParcelableArrayList(ARG_CLASS_LIST))
            advancedSearchRequest = arguments?.getParcelable(ARG_ADVANCED_SEARCH)
            isFromSearch = arguments?.getBoolean(ARG_FROM_SEARCH) ?: false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        getView = inflater.inflate(R.layout.fragment_search_page, container, false)
        presenter = SearchPresenter(this, activity)

        reloadView()

        //get location
        fetchLocation()

        init()
        bindData()
        addTextChanged()

        // search
        classInfoList.clear()
        val outletIds = ArrayList<String>()
        outletIds.add(outlet?.getOutletId() ?: "")
        val outletNames = ArrayList<String>()

//        if (SearchType.FACILITIES.toString() != searchType || isFromSearch) {
//            // remove last two characters of outlet name in case of CCMC only
//            var name = outlet?.getFriendlyName()?.trim()
//            if (true == name?.endsWith("cc", true)) {
//                name = name.substring(0, name.length - 2).trim()
//            }
//            if (!name.isNullOrEmpty()) {
//                outletNames.add(name)
//            }
//        }

        var name = outlet?.getFriendlyName()?.trim()
        if (!name.isNullOrEmpty()) {
            outletNames.add(name)
        }


        defaultSearch(outletIds, outletNames)

        return getView
    }

    private fun defaultSearch(outletIds: ArrayList<String>?, outletNames: ArrayList<String>?) {
        productList.clear()
        presenter?.reloadSearch()
        if (SearchType.FULL.toString() != searchType) {
            if (advancedSearchRequest != null) {
                showLoading()
                presenter?.advancedSearch(advancedSearchRequest, searchType)
                if (false == advancedSearchRequest?.keyword.isNullOrEmpty()) {
                    TextViewBehavior.invisibleDeleteIcon(
                            getView?.txt_find_courses, true, context, null
                    )
                }
            } else {
                if (outletIds != null && outletNames != null) {
                    showLoading()
                    presenter?.searchByClass(keyword, outletIds, outletNames, getPrice(), getDays(), getProductType(searchType))
                    return
                }

                val outletNameList = getOutletNames()
                if (outletNameList.size <= 0) {
                    showErrorMessage(R.string.please_refine_your_search, R.string.choose_outlet)
                    return
                }

                reloadFilter()
                showLoading()
                presenter?.searchByClass(getKeyword(), getOutletIds(), outletNameList,
                        getPrice(), getDays(), getProductType(searchType))
            }
        } else {
            reloadFilter()
            val filterType = getFilterType()
            showLoading()
            val outletNamesList = ArrayList<String>()
            if (outletNames != null) {
                outletNamesList.addAll(outletNames)
            } else {
                outletNamesList.addAll(getOutletNames())
            }
            presenter?.searchProduct(getKeyword(), filterType, getPrice(), getSortType(), outletNamesList)
        }
    }

    private fun getLocationByName(outletName: String, outlets: List<Outlet>?): Outlet? {
        outlets?.forEach { outlet ->

            if (outlet.getFriendlyName().equals(outletName, true)) {
                return outlet
            }
        }
        return null
    }

    private fun fetchLocation() {
//        val kioskInfo = GeneralUtils.convertStringToObject<KioskInfo>(MySharedPref(activity).kioskInfo)
//        outletTypeId = kioskInfo?.outlet?.getOutletTypeId()
//        if (searchType.equals(SearchType.FACILITIES.toString())) {
//            if (outlet != null) {
//                locationList?.add(outlet!!)
//            }
//            initLocationAdapter()
//            getView?.spn_location?.setSelection(0)
//        } else {
////            showLoading()
//            presenter?.getOutletByOutletType(outletTypeId)
//        }
//        if (searchType.equals(SearchType.FACILITIES.toString())) {
//            if (outlet != null) {
//                locationList?.add(outlet!!)
//            }
//        } else {
        if (activity is MainActivity) {
            locationList = (activity as MainActivity).getLocationList()
        }
//        }

        // hardcode to load default facilities at OTH for Tampines Central CC
        if ((SearchType.EVENTS.toString() == searchType || SearchType.COURSES.toString() == searchType) &&
                true == outlet?.getFriendlyName()?.trim()?.equals("Our Tampines Hub", true)) {
            outlet = getLocationByName("TAMPINES CENTRAL CC", locationList)
        }

        initLocationAdapter()

        //update list location selected in advanced search
        updateLocationSelected()
    }

    private fun addTextChanged() {
        getView?.txt_find_courses?.setOnTouchListener(
                TextViewBehavior.deleteEvent(getView?.txt_find_courses)
        )

        getView?.txt_find_courses?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                hidePrompt()
                if ((p0?.trim()?.length ?: 0) == 0) {
                    TextViewBehavior.invisibleDeleteIcon(
                            getView?.txt_find_courses, false, context, null
                    )
                } else {
                    TextViewBehavior.invisibleDeleteIcon(
                            getView?.txt_find_courses, true, context, null
                    )
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
                return
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
                return
            }

        })
    }

    private fun reloadView() {
        if (searchType.equals(SearchType.FACILITIES.toString())) {
            getView?.ll_search_price_days?.visibility = View.GONE
            getView?.txt_nearest_location?.isEnabled = true
            getView?.txt_find_courses?.hint = getString(R.string.search_facilities_hint)
        } else if (searchType.equals(SearchType.EVENTS.toString())) {
            getView?.ll_search_price_days?.visibility = View.VISIBLE
            getView?.txt_nearest_location?.isEnabled = true
            getView?.txt_find_courses?.hint = getString(R.string.search_event_hint)
        } else if (searchType.equals(SearchType.FULL.toString())) {
            getView?.txt_nearest_location?.isEnabled = true
            getView?.ll_search_price_days?.visibility = View.VISIBLE
            getView?.txt_find_courses?.hint = getString(R.string.search_your_courses_hint)
        } else {
            getView?.ll_search_price_days?.visibility = View.VISIBLE
            getView?.txt_nearest_location?.isEnabled = true
            getView?.txt_find_courses?.hint = getString(R.string.search_your_courses_hint)
        }

        val maxPriceStr = GeneralUtils.formatAmountSymbols("$", maxPrice.toFloat(), 0);
        getView?.txt_max_price?.text = maxPriceStr
        getView?.sb_price?.max = maxPrice
        getView?.sb_price?.progress = maxPrice
        getView?.ll_price?.setTranslationX(230F)
        getView?.txt_price?.text = maxPriceStr
    }

    private fun bindData() {
        updateKeyword()
        updateDaySelected()
        getView?.txt_find_courses?.setText(keyword)
    }

    private var addToCartEvent = object : AddToCartEvent.AddToCart {
        override fun addItem(view: View, position: Int, isQuickBook: Boolean) {
            if (SystemClock.elapsedRealtime() - MainActivity.globalLastClickTime < MainActivity.CLICK_TIMER) {
                return
            }
            MainActivity.globalLastClickTime = SystemClock.elapsedRealtime()

            if (position >= 0 && position < productList.size) {
                val product = productList[position]
                if (product.typeItem == SearchType.COURSES.name) {
                    checkClassVacancy(productList[position].infoItem as ClassInfo, true, isQuickBook)
                } else if (product.typeItem == SearchType.INTEREST_GROUPS.name){
                    checkIgVacancy(productList[position].infoItem as InterestGroup, true, isQuickBook)
                } else if (product.typeItem == SearchType.EVENTS.name){
                    val cartItem = CartItem(UUID.randomUUID().toString(), null, null, productList[position].infoItem as EventInfo, null, null, null, null)
                    if (isQuickBook) {
                        (activity as? MainActivity)?.doQuickBook(arrayOf(cartItem).toList())
                    }else {
                        (activity as? MainActivity)?.addToCart(arrayOf(cartItem).toList())
                    }
                }
            }
        }

    }

    private fun setTitleSearch() {
        var title: String?
        val titleSearch: String?
        val hintSearch: String?
        if (searchType?.equals(SearchType.FACILITIES.toString())!!) {
            title = activity?.resources?.getString(R.string.facilities_at, outlet?.getFriendlyName())
            titleSearch = activity?.resources?.getString(R.string.search_your_facilities)
            hintSearch = activity?.resources?.getString(R.string.search_facilities_hint)

        } else if (true == searchType?.equals(SearchType.EVENTS.toString())) {
            title = activity?.resources?.getString(R.string.events_at, outlet?.getFriendlyName())
            titleSearch = activity?.resources?.getString(R.string.search_your_events)
            hintSearch = activity?.resources?.getString(R.string.search_event_hint)

        } else if (true == searchType?.equals(SearchType.FULL.toString())) {
            title = activity?.resources?.getString(R.string.search_results)
            titleSearch = activity?.resources?.getString(R.string.search_by_keyword)
            hintSearch = activity?.resources?.getString(R.string.search_event_hint)
        } else if(searchType?.equals(SearchType.INTEREST_GROUPS.toString()) == true) {
            title = activity?.resources?.getString(R.string.interest_group_at, outlet?.getFriendlyName())
            titleSearch = activity?.resources?.getString(R.string.search_your_interest_group)
            hintSearch = activity?.resources?.getString(R.string.search_ig_hint)
        }else {
            title = activity?.resources?.getString(R.string.courses_at, outlet?.getFriendlyName())
            titleSearch = activity?.resources?.getString(R.string.search_your_courses)
            hintSearch = activity?.resources?.getString(R.string.search_your_courses_hint)
        }

        if (isFromSearch) {
            title = activity?.resources?.getString(R.string.search_results)
        }

        setTitle(title)
        getView?.txt_find_courses?.hint = hintSearch
        getView?.txt_title_search?.setText(titleSearch)
    }

    private fun init() {
        val params = getView?.rcv_class_info_list?.layoutParams
        params?.apply {
            width = context?.resources?.displayMetrics?.widthPixels ?: 0
            height = context?.resources?.displayMetrics?.heightPixels ?: 0
        }
        getView?.rcv_class_info_list?.layoutParams = params
        getView?.rcv_class_info_list?.layoutManager = GridLayoutManager(activity, 3)
        getView?.rcv_class_info_list?.setOnLoadMoreListener(this)
        getView?.btnFilter?.setOnClickListener(this)

//        preferredDaysUtils = PreferredDaysUtils(getView?.ll_preferred_day, activity)

        initSearchAdapter()

        getView?.sb_price?.setOnSeekBarChangeListener(this)
        getView?.rg_search_type?.setOnCheckedChangeListener(searchFilterOption)
        getView?.rb_sort_start_date?.isChecked = false
        getView?.rb_sort_za?.isChecked = false
        getView?.rb_sort_az?.isChecked = false
        getView?.rb_course?.isChecked = false
        getView?.rb_event?.isChecked = false
        getView?.rb_facility?.isChecked = false
        getView?.rb_interest_group?.isChecked = false

        getView?.txt_find_courses?.requestFocus()

        animationImage()
        initProximity()
    }

    private var distanceSelection: String? = null
    private var nearestLocation: Outlet? = null
    override fun getDistanceSelection(): String? {
        return distanceSelection
    }

    override fun getNearestProximity(): Outlet? {
        return nearestLocation
    }

    private val proximityOption = object : RadioGroup.OnCheckedChangeListener {
        override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
            when (checkedId) {
                R.id.rb_location -> {
                    getView?.ll_location?.visibility = View.VISIBLE
                    getView?.ll_proximity?.visibility = View.GONE
                    getView?.txt_inform_txn_pa?.visibility = View.VISIBLE
                    getView?.txt_info?.visibility = View.VISIBLE

                    initProximityHandle(false)
                    defaultSearch(null, null)

                }

                R.id.rb_proximity -> {
                    getView?.ll_location?.visibility = View.GONE
                    getView?.ll_proximity?.visibility = View.VISIBLE
                    getView?.txt_inform_txn_pa?.visibility = View.GONE
                    getView?.txt_info?.visibility = View.GONE

                    initProximityHandle(true)
                }
            }
        }

    }

    private val searchFilterOption = object : RadioGroup.OnCheckedChangeListener {
        override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
            when (checkedId) {
                R.id.rb_facility -> {
                    maxPrice = MAX_PRICE
                    getView?.ll_location?.visibility = View.VISIBLE
                    getView?.ll_search_price_days?.visibility = View.GONE
                    getView?.ll_proximity?.visibility = View.GONE
                    getView?.txt_venue_label?.visibility = View.GONE
                    getView?.rg_option?.visibility = View.GONE
                    if (getView?.rb_proximity?.isChecked == true) {
                        initProximityHandle(false)
                        reloadFilter()
                        productAdapter?.notifyDataSetChanged()
                        showTotalRecord(0)
                    }

                }
                R.id.rb_event,
                R.id.rb_course,
                R.id.rb_interest_group -> {
                    maxPrice = getView?.sb_price?.progress!!.toInt()
                    getView?.ll_search_price_days?.visibility = View.VISIBLE
                    getView?.ll_proximity?.visibility = View.GONE
                    getView?.txt_venue_label?.visibility = View.VISIBLE
                    getView?.rg_option?.visibility = View.VISIBLE
                    getView?.rb_location?.isChecked = true
                }
            }
        }
    }

    private fun initProximityHandle(isInit: Boolean) {
        if (isInit) {
            if (proximityHandle == null) {
                proximityHandle = ProximityHandle(this, getView, activity)
            }
            showTotalRecord(-1)
            proximityHandle?.initProximityView()
        } else {
            proximityHandle?.destroyProximityView()
            proximityHandle = null
        }
    }

    private fun initProximity() {
        when (searchType) {
            SearchType.FACILITIES.toString() -> {
                getView?.txt_inform_txn_pa?.visibility = View.VISIBLE
                getView?.txt_venue_label?.visibility = View.GONE
                getView?.rg_option?.visibility = View.GONE
                getView?.ll_location?.visibility = View.VISIBLE
                getView?.ll_proximity?.visibility = View.GONE
                getView?.txt_filter_by?.visibility = View.GONE
                getView?.rg_search_type?.visibility = View.GONE
                getView?.rl_sort?.visibility = View.GONE
                (activity as? MainActivity)?.setBackgroundLine(R.drawable.bg_gradient_facility_linear)
                return
            }
            SearchType.EVENTS.toString(),
            SearchType.COURSES.toString(),
            SearchType.INTEREST_GROUPS.toString() -> {
                getView?.txt_inform_txn_pa?.visibility = View.VISIBLE
                getView?.txt_venue_label?.visibility = View.VISIBLE
                getView?.rg_option?.visibility = View.VISIBLE
                getView?.ll_location?.visibility = View.VISIBLE
                getView?.ll_proximity?.visibility = View.GONE
                getView?.txt_filter_by?.visibility = View.GONE
                getView?.rg_search_type?.visibility = View.GONE
                getView?.rl_sort?.visibility = View.VISIBLE
                when (searchType) {
                    SearchType.EVENTS.toString() -> {
                        (activity as? MainActivity)?.setBackgroundLine(R.drawable.bg_gradient_event_linear)
                    }
                    SearchType.INTEREST_GROUPS.toString() -> {
                        (activity as? MainActivity)?.setBackgroundLine(R.drawable.bg_gradient_interestgroup_linear)
                    }
                    else -> {
                        (activity as? MainActivity)?.setBackgroundLine(R.drawable.bg_gradient_purple)
                    }
                }

            }
            else -> {
                getView?.txt_inform_txn_pa?.visibility = View.VISIBLE
                (activity as? MainActivity)?.setBackgroundLine(R.drawable.bg_gradient_blue)
                getView?.txt_filter_by?.visibility = View.VISIBLE
                getView?.txt_venue_label?.visibility = View.VISIBLE
                getView?.rg_option?.visibility = View.VISIBLE
                getView?.ll_location?.visibility = View.VISIBLE
                getView?.ll_proximity?.visibility = View.GONE
                getView?.rg_search_type?.visibility = View.VISIBLE
                getView?.rl_sort?.visibility = View.VISIBLE
                getView?.rg_option?.check(R.id.rb_location)
            }
        }

        distanceOption = resources.getStringArray(R.array.distance_list)
        val distanceAdapter = ArrayAdapter<String>(activity, R.layout.spinner_proximity_item, distanceOption)
        distanceAdapter.setDropDownViewResource(R.layout.spinner_proximity_dropdown_item)
        getView?.spn_distance?.adapter = distanceAdapter
        getView?.spn_distance?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                LogManager.d(TAG, "onNothingSelected")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (pos >= 0 && distanceOption != null && pos < distanceOption?.size!!) {
                    distanceSelection = distanceOption?.get(pos)
                }
            }

        })

        val index = getIndexLocation()
        nearestLocation = locationList?.get(index)
        val proximityAdapter = ProximityAdapter(activity, R.layout.spinner_proximity_dropdown_item, locationList)
        getView?.txt_nearest_location?.setText(nearestLocation?.getFriendlyName())
        getView?.txt_nearest_location?.setAdapter(proximityAdapter)
        getView?.txt_nearest_location?.threshold = 1
        getView?.txt_nearest_location?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                Handler().postDelayed({
                    getView?.txt_nearest_location?.hint = getString(R.string.search_location_hint)
                    getView?.txt_nearest_location?.showDropDown()
                }, 50)
                getView?.txt_nearest_location?.setText("")
            }
        }

        getView?.txt_nearest_location?.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if ((s?.length ?: 0) == 0) {
                            Handler().postDelayed({
                                getView?.txt_nearest_location?.showDropDown()
                            }, 50)
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        // Do nothing
                        return
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        // Do nothing
                        return
                    }
                })

        getView?.txt_nearest_location?.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (pos >= 0 && locationList != null && pos < locationList?.size!!) {
                    nearestLocation = locationList?.get(pos)
                    GeneralUtils.hideSoftKeyboard(getView)
                }
            }
        })

        getView?.rg_option?.setOnCheckedChangeListener(proximityOption)
    }

    override fun onResume() {
        super.onResume()
        setTitleSearch()
    }

    private fun getThumb(progress: Int): Drawable {
        val thumbView = LayoutInflater.from(activity).inflate(R.layout.layout_seekbar_thumb,
                null, false)
        (thumbView.findViewById(R.id.tvProgress) as TextView).text = progress.toString() + ""

        thumbView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        thumbView.layout(0, 0, thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight())
        thumbView.draw(canvas)

        return BitmapDrawable(resources, bitmap)
    }

    private fun initSearchAdapter() {
        setTotalRecords()
        productList.clear()
        productAdapter = ProductAdapter(productList, context)
        productAdapter?.setOnClickRecyclerViewItem(this)
        productAdapter?.setAddToCart(addToCartEvent)
        getView?.rcv_class_info_list?.adapter = productAdapter
        if (searchType.equals(SearchType.COURSES.toString())) {
            getView?.bg_shade?.setBackgroundResource(R.drawable.bg_blue_shade)
            (activity as? MainActivity)?.setBackgroundBottomBar(MainActivity.MODE_COURSES)
        } else if (searchType.equals(SearchType.FACILITIES.toString())) {
            getView?.bg_shade?.setBackgroundResource(R.drawable.bg_purple_shape)
            (activity as? MainActivity)?.setBackgroundBottomBar(MainActivity.MODE_FACILITIES)
        } else if (searchType.equals(SearchType.EVENTS.toString())) {
            getView?.bg_shade?.setBackgroundResource(R.drawable.bg_purple_shape)
            (activity as? MainActivity)?.setBackgroundBottomBar(MainActivity.MODE_EVENTS)
        }else if(searchType.equals(SearchType.INTEREST_GROUPS.toString())){
            getView?.bg_shade?.setBackgroundResource(R.drawable.bg_green_shade)
            (activity as? MainActivity)?.setBackgroundBottomBar(MainActivity.MODE_INTEREST_GROUPS)
        } else {
            (activity as? MainActivity)?.setBackgroundBottomBar("")
        }
        getView?.rcv_class_info_list?.linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        getView?.rcv_class_info_list?.setItemViewCacheSize(20)
    }

    private fun getIndexLocation(): Int {
        if (outlet != null && locationList != null && locationList?.size!! > 0) {
            for (i in 0..locationList?.size!! - 1) {
                if (!locationList?.get(i)?.getOutletId().isNullOrEmpty() &&
                        !outlet?.getOutletId().isNullOrEmpty() &&
                        locationList?.get(i)?.getOutletId()!!.equals(outlet?.getOutletId()!!)) {
                    return i
                }
            }
        }

        return 0
    }


    private fun setSelectedLocation() {
        var locations = " "
        val selectedLocation = locationAdapter?.locationListSelected
        if (selectedLocation != null) {
            for (location in selectedLocation) {
                locations += location.getFriendlyName() + ", "
            }
            if (locations.length > 2)
                locations = locations.substring(0, locations.lastIndex - 1)
        }
        if (getView?.txt_location != null) {
            val width: Int = getView?.txt_location!!.measuredWidth - (getView?.txt_location!!.paddingLeft + getView?.txt_location!!.paddingRight)
            val truncatedText: String = TextUtils.ellipsize(locations, getView?.txt_location?.paint, width.toFloat(), TextUtils.TruncateAt.END).toString()
            if (truncatedText.isNotEmpty()) {
                getView?.txt_location?.setText(truncatedText)
            } else {
                getView?.txt_location?.setText(locations)
            }
        }
    }

    private fun initLocationAdapter() {
        val index = getIndexLocation()
        locationAdapter = SearchLocationAdapter(activity!!, R.layout.item_search_location, locationList!!, index, this)
        getView?.txt_location?.setAdapter(locationAdapter)
        getView?.txt_location?.threshold = 1
        getView?.txt_location?.setOnEditorActionListener(OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setSelectedLocation()
                getView?.txt_location?.clearFocus()
                GeneralUtils.hideSoftKeyboard(getView)
                return@OnEditorActionListener true
            }
            false
        })
        setSelectedLocation()
        getView?.txt_location?.setOnDismissListener {
            Handler().postDelayed({
                if (getView?.txt_find_courses?.hasFocus() == true) {
                    setSelectedLocation()
                }
            }, 50)
        }
        getView?.txt_location?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                Handler().postDelayed({
                    locationAdapter?.clearSelected()
                    getView?.txt_location?.hint = getString(R.string.search_location_hint)
                    getView?.txt_location?.showDropDown()
                }, 50)
                getView?.txt_location?.setText("")
            }
        }
        getView?.txt_location?.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if ((s?.length ?: 0) == 0) {
                            Handler().postDelayed({
                                getView?.txt_location?.showDropDown()
                            }, 50)
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        // Do nothing
                        return
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        // Do nothing
                        return
                    }
                })
    }

    private fun sortLocationByName(lst: ArrayList<Outlet>?): ArrayList<Outlet> {
        val list = ArrayList<Outlet>()
        if (lst?.sortedWith(compareBy { it.getFriendlyName() })?.toList() != null) {
            list.addAll(lst.sortedWith(compareBy { it.getFriendlyName() }).toList())
        }
        return list
    }

    private fun setTotalRecords() {
        var total = 0
        if (searchType.equals(SearchType.FACILITIES.toString())) {
            if (pageByFacility != null) {
                total = pageByFacility?.getTotalRecord() ?: 0
            }
        } else if (searchType.equals(SearchType.EVENTS.toString())) {
            if (pageByEvent != null) {
                total = pageByEvent?.getTotalRecord() ?: 0
            }
        } else if(searchType.equals(SearchType.INTEREST_GROUPS.toString())){
            if (pageByInterestGroup != null){
                total = pageByInterestGroup?.getTotalRecord() ?: 0
            }
        }else {
            if (pageByClassInfo != null) {
                total = pageByClassInfo?.getTotalRecord() ?: 0
            }
        }

        showTotalRecord(total)
    }

    override fun showTotalRecord(total: Int) {
        if (activity != null) {
            if (total < 0) {
                getView?.txt_num_result?.visibility = View.INVISIBLE
            } else {
                getView?.txt_num_result?.visibility = View.VISIBLE
                getView?.txt_num_result?.setText(activity!!.resources.getString(R.string.listings, total))
            }
        }
    }

    private var anim: ObjectAnimator? = null
    private fun animationImage() {
        anim = ObjectAnimator.ofFloat(getView?.img_pointer, "x", 320f, 250f)
        anim?.addUpdateListener {
            getView?.img_pointer?.translationX = it.animatedValue as Float
        }
        anim?.duration = 1200L
        anim?.repeatMode = ObjectAnimator.REVERSE
        anim?.repeatCount = ValueAnimator.INFINITE
        anim?.start()

        isFirst = true
    }

    override fun onLocationByOutlet(pageByOutletDetail: PageByOutletDetail?) {
        if (pageByOutletDetail?.getOutletDetailList() != null)
            locationList?.addAll(pageByOutletDetail.getOutletDetailList())

        if (pageByOutletDetail?.getTotalRecord() != null &&
                pageByOutletDetail.getTotalRecord()!! > locationList?.size!!) {
            presenter?.getMoreOutlet(outletTypeName)
        } else {
            locationList = sortLocationByName(locationList)

            initLocationAdapter()

            //update list location selected in advanced search
            updateLocationSelected()
        }

//        dismissLoading()
    }

    private fun updateLocationSelected() {
        val locationSelected = java.util.ArrayList<Outlet>()
        if (advancedSearchRequest != null && advancedSearchRequest?.outletId != null && locationList != null) {

            for (location in advancedSearchRequest?.outletId!!) {
                for (l in locationList!!) {
                    if (l.getOutletId().equals(location)) {
                        locationSelected.add(l)
//                        break
                    }
                }
            }

        } else {
            if (locationList != null && locationList?.size!! > 0) {
                for (i in 0..locationList?.size!! - 1) {
                    if (locationList?.get(i) != null && locationList?.get(i)?.getOutletId().equals(outlet?.getOutletId())) {
                        locationSelected.add(locationList?.get(i)!!)
                    }
                }
            } else {
                if (outlet != null)
                    locationSelected.add(outlet!!)
            }
        }
        locationAdapter?.setLocationsSelected(locationSelected)
    }

    private fun updateKeyword() {
        if (advancedSearchRequest != null && advancedSearchRequest?.keyword != null) {
            keyword = advancedSearchRequest?.keyword
        }
    }

    private fun updateDaySelected() {
        if (advancedSearchRequest != null && advancedSearchRequest?.day != null) {
            val days = ArrayList(advancedSearchRequest?.day!!)
            preferredDaysUtils?.setDays(days)
        }
    }

    override fun showLoading() {
        if (loadingFragment.isAdded) {
            return
        }

        if (!loadingFragment.isVisible && fragmentManager != null) {
            loadingFragment.show(fragmentManager!!, LoadingFragment::class.java.simpleName)
        }
    }

    override fun dismissLoading() {
        if (loadingFragment.isAdded || loadingFragment.isVisible) {
            loadingFragment.dismiss()
        }
    }

    override fun <T> showErrorMessage(response: BaseResponse<T>) {
        dismissLoading()

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
//        dismissLoading()

        if (fragment != null && fragment!!.isVisible) {
            fragment?.dismiss()
        }
        fragment = MessageDialogFragment.newInstance(0, messageResId)
        if (fragmentManager != null) {
            fragment?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun showErrorMessage(titleResId: Int, messageResId: Int) {
//        dismissLoading()

        if (fragment != null && fragment!!.isVisible) {
            fragment?.dismiss()
        }
        fragment = MessageDialogFragment.newInstance(titleResId, messageResId, true)
        if (fragmentManager != null) {
            fragment?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        hidePrompt()
        initProximityHandle(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }

    fun getKeyword(): String {
        if (getView?.txt_find_courses?.text != null)
            return getView?.txt_find_courses?.text.toString()

        return ""
    }

    override fun onLoadMore() {
//        if (isFirstAdvancedSearch && searchType?.equals(SearchType.ADVANCED_SEARCH.toString())!! && presenter?.checkLoadMoreAdvancedSearch()!!) {
//            presenter?.advancedSearch(advancedSearchRequest)
//            return
//        }

        if (checkLoadMore()) {
            if (isFirstAdvancedSearch && advancedSearchRequest != null) {
                presenter?.loadMore(advancedSearchRequest, searchType)

                return
            }

            when (searchType) {

                SearchType.FULL.toString() -> {
                    if (getView?.rg_option?.checkedRadioButtonId != R.id.rb_proximity
                            || (getView?.rg_option?.checkedRadioButtonId == R.id.rb_proximity
                                    && getView?.rg_search_type?.checkedRadioButtonId != -1
                                    && getView?.rg_search_type?.checkedRadioButtonId != R.id.rb_course
                                    && getView?.rg_search_type?.checkedRadioButtonId != R.id.rb_interest_group
                                    && getView?.rg_search_type?.checkedRadioButtonId != R.id.rb_event
                                )
                    ) {
                        val filterType = getFilterType()
                        val sort = getSortType()
                        val outletNameList = getOutletNames()
                        presenter?.loadMore(keyword, filterType, getPrice(), sort, outletNameList)
                        return
                    }
                }

                SearchType.FACILITIES.toString() -> {
                    presenter?.loadMore(getKeyword(), getOutletIds(), getOutletNames(), getPrice(), getDays(), getProductType(searchType))
                    return
                }
            }

            when (getView?.rg_option?.checkedRadioButtonId) {
                R.id.rb_location -> {
                    if (getView?.rg_sort?.checkedRadioButtonId == -1) {
                        presenter?.loadMore(getKeyword(), getOutletIds(), getOutletNames(), getPrice(), getDays(), getProductType(searchType))
                    } else {
                        val productType = getProductType(searchType)
                        presenter?.loadMore(keyword, productType, getPrice(), getSortType(), getOutletNames())
                    }
                }

                R.id.rb_proximity -> {
                    if (getView?.btn_show_all?.visibility == View.VISIBLE) {
                        searchLocationCourse(proximityOutletInfo, false)
                        return
                    }
                    if (nearestLocation != null && !nearestLocation?.getOutletId().isNullOrEmpty()) {
                        presenter?.loadMore(getKeyword(), nearestLocation?.getOutletId()!!,
                                distanceSelection!!.replace("km", "").trim().toInt(),
                                getPrice(), getDays(), getProductType(searchType))
                    }
                }
            }

        }
    }

    fun checkLoadMore(): Boolean {
        if (productListResponse != null && productListResponse!!.getTotalRecord()!! > productList.size) {
            return true
        } else if (pageByFacility != null && pageByFacility?.getTotalRecord()!! > facilityList.size) {
            return true
        } else if (pageByEvent != null && pageByEvent?.getTotalRecord()!! > eventList.size) {
            return true
        } else if (pageByClassInfo != null && pageByClassInfo?.getTotalRecord()!! > classInfoList.size) {
            return true
        } else if (pageByInterestGroup != null && pageByInterestGroup?.getTotalRecord()!! > igList.size){
            return true
        }
        return false
    }

    fun getSearchType(): String? {
        return searchType
    }

    fun hidePrompt() {
        if (isFirst) {
            anim?.removeAllUpdateListeners()
            anim?.removeAllListeners()
            anim?.pause()
            anim?.end()
            anim?.cancel()
            anim = null
            getView?.img_pointer?.visibility = View.GONE
            isFirst = false
        }
    }

    fun reloadFilter() {
        classInfoList.clear()
        facilityList.clear()
        eventList.clear()
        productList.clear()
        igList.clear()
        getView?.rcv_class_info_list?.setLoaded()
        pageByFacility = null
        pageByClassInfo = null
        pageByEvent = null
        pageByInterestGroup = null
        productListResponse = null
        presenter?.reloadSearch()
        isFilter = true
        isFirstAdvancedSearch = false
        advancedSearchRequest = null
        proximityOutletInfo = null
    }

    override fun onClick(p0: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        when (p0?.id) {
            R.id.btnFilter -> {
                setTitle(activity?.resources?.getString(R.string.search_results))

                keyword = getKeyword()

                LogManager.i("Search Keyword: $keyword")

                hidePrompt()
                reloadFilter()
                initSearchAdapter()

                LogManager.d("search type = $searchType")
                when (searchType) {
                    SearchType.FULL.toString() -> {
                        if (getView?.rg_option?.checkedRadioButtonId != R.id.rb_proximity
                                || (getView?.rg_option?.checkedRadioButtonId == R.id.rb_proximity
                                        && getView?.rg_search_type?.checkedRadioButtonId != -1
                                        && getView?.rg_search_type?.checkedRadioButtonId != R.id.rb_course
                                        && getView?.rg_search_type?.checkedRadioButtonId != R.id.rb_interest_group
                                        && getView?.rg_search_type?.checkedRadioButtonId != R.id.rb_event
                                    )
                        ) {
                            val outletNameList = getOutletNames()
                            if (outletNameList.size <= 0) {
                                showErrorMessage(R.string.please_refine_your_search, R.string.choose_outlet)
                                return
                            }
                            val filterType = getFilterType()
                            val sort = getSortType()
                            showLoading()
                            presenter?.searchProduct(keyword, filterType, getPrice(), sort, outletNameList)
                            return
                        }
                    }

                    SearchType.FACILITIES.toString() -> {
                        val outletNameList = getOutletNames()
                        if (outletNameList.size <= 0) {
                            showErrorMessage(R.string.please_refine_your_search, R.string.choose_outlet)
                            return
                        }

                        showLoading()
                        presenter?.searchByClass(keyword, getOutletIds(), outletNameList,
                                getPrice(), getDays(), getProductType(searchType))
                        return
                    }
                }

                when (getView?.rg_option?.checkedRadioButtonId) {
                    R.id.rb_location -> {
                        getView?.txt_inform_txn_pa?.visibility = View.VISIBLE
                        val outletNameList = getOutletNames()
                        if (outletNameList.size <= 0) {
                            showErrorMessage(R.string.please_refine_your_search, R.string.choose_outlet)
                            return
                        }

                        showLoading()
                        if (getView?.rg_sort?.checkedRadioButtonId == -1) {
                            presenter?.searchByClass(keyword, getOutletIds(), outletNameList,
                                    getPrice(), getDays(), getProductType(searchType))
                        } else {
//                            val searchTypeProduct = Product.PRODUCT_COURSE
                            val searchTypeProduct = if (searchType?.equals(SearchType.FULL.toString()) == true){
                                getFilterType()
                            } else {
                                getProductType(searchType)
                            }
                            val sort = getSortType()
                            presenter?.searchProduct(keyword, searchTypeProduct, getPrice(), sort, outletNameList)
                        }

                    }

                    R.id.rb_proximity -> {
                        getView?.txt_inform_txn_pa?.visibility = View.GONE
                        proximityHandle?.setProximitySearchRequest(null)
                        proximityHandle?.setCourseSearchRequest(null)
                        searchProximity()
                    }
                }
            }
        }
    }

    private fun getSortType(): String? {
        var sort: String? = null
        when (getView?.rg_sort?.checkedRadioButtonId) {
            R.id.rb_sort_az -> {
                sort = "${SearchProductRequest.TITLE} ${SearchProductRequest.ASC}"
            }
            R.id.rb_sort_za -> {
                sort = "${SearchProductRequest.TITLE} ${SearchProductRequest.DESC}"
            }
            R.id.rb_sort_start_date -> {
                sort = "${SearchProductRequest.START_DATE} ${SearchProductRequest.ASC}"
            }
        }
        return sort
    }

    private fun getFilterType(): String? {
        var filterType: String? = null
        when (getView?.rg_search_type?.checkedRadioButtonId) {
            R.id.rb_course -> {
                filterType = Product.PRODUCT_COURSE
            }
            R.id.rb_facility -> {
                filterType = Product.PRODUCT_FACILITY
            }
            R.id.rb_event -> {
                filterType = Product.PRODUCT_EVENT
            }
            R.id.rb_interest_group -> {
                filterType = Product.PRODUCT_INTEREST_GROUP
            }
        }
        return filterType
    }

    private fun getProductType(searchType: String?): String? {
        return if (searchType.equals(SearchType.FULL.toString())){
            getFilterType()
        } else {
            when (searchType) {
                SearchType.COURSES.toString() -> Product.PRODUCT_COURSE
                SearchType.FACILITIES.toString() -> Product.PRODUCT_FACILITY
                SearchType.EVENTS.toString() -> Product.PRODUCT_EVENT
                SearchType.INTEREST_GROUPS.toString() -> Product.PRODUCT_INTEREST_GROUP
                else -> null
            }
        }
    }

    override fun onSearchProductSuccess(data: ProductListResponse?) {
        getView?.rcv_class_info_list?.setLoaded()
        this.productListResponse = data
        val productListResponse = ArrayList<RecommendationItem>()
        data?.productList?.forEach { product ->
            if (product.productType == Product.PRODUCT_FACILITY) {
                val recommendationItem = RecommendationItem()
                recommendationItem.typeItem = SearchType.FACILITIES.name
                recommendationItem.infoItem = Facility(product)
                productListResponse.add(recommendationItem)
            } else if (product.productType == Product.PRODUCT_COURSE) {
                val recommendationItem = RecommendationItem()
                recommendationItem.typeItem = SearchType.COURSES.name
                recommendationItem.infoItem = ClassInfo(product)
                productListResponse.add(recommendationItem)
            } else if (product.productType == Product.PRODUCT_EVENT) {
                val recommendationItem = RecommendationItem()
                recommendationItem.typeItem = SearchType.EVENTS.name
                recommendationItem.infoItem = EventInfo(product)
                productListResponse.add(recommendationItem)
            } else if (product.productType == Product.PRODUCT_INTEREST_GROUP){
                val recommendationItem = RecommendationItem()
                recommendationItem.typeItem = SearchType.INTEREST_GROUPS.name
                recommendationItem.infoItem = InterestGroup(product)
                productListResponse.add(recommendationItem)
            }
        }
        productList.addAll(productListResponse)
        showTotalRecord(this.productListResponse?.getTotalRecord() ?: 0)
        productAdapter?.notifyDataSetChanged()
    }

    override fun getPrice(): PriceClass {
        return PriceClass(0, maxPrice)
    }

    private fun getOutletIds(): ArrayList<String> {
        val outletIds = ArrayList<String>()
        if (locationAdapter?.getLocationsSelected() != null) {
            for (location in locationAdapter?.getLocationsSelected()!!) {
                if (!location.getOutletId().isNullOrEmpty())
                    outletIds.add(location.getOutletId()!!)
            }
        }
        return outletIds
    }

    private fun getOutletNames(): ArrayList<String> {
        val outletNames = ArrayList<String>()
        if (locationAdapter?.getLocationsSelected() != null) {
            for (location in locationAdapter?.getLocationsSelected()!!) {
                val name = location.getFriendlyName()?.trim()
                // remove last two characters of outlet name in case of CCMC only
//                if (true == name?.endsWith("cc", true)) {
//                    name = name.substring(0, name.length - 2).trim()
//                }
                if (!name.isNullOrEmpty()) {
                    outletNames.add(name)
                }
            }
        }
        return outletNames
    }

    fun getDays(): ArrayList<Int> {
        if (preferredDaysUtils == null)
            return ArrayList()

        return preferredDaysUtils?.getDays()!!
    }

    override fun onGetClassSuccess(classInfo: PageByClassInfo?, request: AdvancedSearchRequest) {
        proximityHandle?.setCourseSearchRequest(request)
        getView?.rcv_class_info_list?.setLoaded()
        pageByClassInfo = classInfo
        if (pageByClassInfo != null &&
                pageByClassInfo?.getClassList() != null && pageByClassInfo?.getClassList()?.size!! > 0) {
            classInfoList.addAll(pageByClassInfo?.getClassList()!!)
            mapDataIntoAdapter()
        }

        setTotalRecords()
        showTotalRecord(pageByClassInfo?.getTotalRecord() ?: 0)

        dismissLoading()
    }

    override fun onGetInterestGroupSuccess(
        igInfo: PageByInterestGroup?,
        request: AdvancedSearchRequest
    ) {
        proximityHandle?.setCourseSearchRequest(request)
        getView?.rcv_class_info_list?.setLoaded()
        pageByInterestGroup = igInfo
        if (pageByInterestGroup != null && pageByInterestGroup?.getIgList() != null && pageByInterestGroup?.getIgList()?.size!! > 0){
            igList.addAll(pageByInterestGroup?.getIgList()!!)
            mapDataIntoAdapter()
        }
        setTotalRecords()
        showTotalRecord(pageByInterestGroup?.getTotalRecord() ?: 0)

        dismissLoading()
    }

    override fun onGetFacilitySuccess(facilities: PageByFacility?) {
        getView?.rcv_class_info_list?.setLoaded()
        this.pageByFacility = facilities
        if (pageByFacility != null && pageByFacility?.getFacilityList() != null && pageByFacility?.getFacilityList()?.size!! > 0) {
            facilityList.addAll(pageByFacility?.getFacilityList()!!)
//            searchByFacilityAdapter?.notifyDataSetChanged()
            mapDataIntoAdapter()
        }

        setTotalRecords()
        showTotalRecord(pageByFacility?.getTotalRecord() ?: 0)

        dismissLoading()
    }

    override fun onGetEventSuccess(response: EventResponse?, request: AdvancedSearchRequest) {
        proximityHandle?.setCourseSearchRequest(request)
        getView?.rcv_class_info_list?.setLoaded()
        this.pageByEvent = response
        if (pageByEvent != null && pageByEvent?.eventList != null && pageByEvent?.eventList!!.size > 0) {
            eventList.addAll(pageByEvent?.eventList!!)
            mapDataIntoAdapter()
        } else {
            this.eventList.clear()
            productList.clear()
            productAdapter?.notifyDataSetChanged()
        }
        setTotalRecords()
        showTotalRecord(pageByEvent?.getTotalRecord() ?: 0)

        dismissLoading()
    }


    // search proximity
    override fun proximityLocationInfoResult(response: ProximityLocationResponse?, request: AdvancedSearchRequest) {
        var distance = 5
        if (distanceSelection != null) {
            distance = distanceSelection!!.replace("km", "").trim().toInt()
        }
        proximityHandle?.setProximitySearchRequest(request)
        proximityHandle?.showProximityLocationInfo(distance, nearestLocation, response)
    }

    private var proximityOutletInfo: ProximityLocationInfo? = null
    override fun searchLocationCourse(proximityLocationInfo: ProximityLocationInfo?, isReset: Boolean) {
        val outletId = proximityLocationInfo?.outlet?.getOutletId()
        var outletName = proximityLocationInfo?.outlet?.getFriendlyName()?.trim()
//        if (true == outletName?.endsWith("cc", true)) {
//            outletName = outletName.substring(0, outletName.length - 2).trim()
//        }

        if (outletId.isNullOrEmpty() || outletName.isNullOrEmpty()) {
            showErrorMessage(R.string.please_refine_your_search, R.string.choose_outlet)
            return
        }

        if (isReset) {
            if (false == proximityHandle?.isSearchCourse(getKeyword(),
                            arrayListOf(outletName),
                            getPrice(),
                            null,
                            null)) {
                setTotalRecords()
                return
            }

            reloadFilter()
            this.proximityOutletInfo = proximityLocationInfo
        } else {
            presenter?.loadMore()
        }

        showLoading()
        presenter?.searchByClass(getKeyword(), arrayListOf(outletId), arrayListOf(outletName),
                getPrice(), null, getProductType(searchType))
    }

    override fun searchAllOfProximityCourse() {
        if (nearestLocation == null || nearestLocation?.getOutletId().isNullOrEmpty()
                || distanceSelection.isNullOrEmpty()) {
            showErrorMessage(R.string.please_refine_your_search, R.string.choose_outlet)
            return
        }

        if (false == proximityHandle?.isSearchCourse(getKeyword(),
                        null,
                        getPrice(),
                        nearestLocation?.getOutletId()!!,
                        distanceSelection!!.replace("km", "").trim().toInt())) {
            setTotalRecords()
            return
        }

        reloadFilter()

        showLoading()
        val productType = getProductType(searchType)
        presenter?.searchByClass(getKeyword(),
                nearestLocation?.getOutletId()!!,
                distanceSelection!!.replace("km", "").trim().toInt(),
                getPrice(), getDays(), productType)
    }

    override fun searchProximity() {
        if (nearestLocation == null || nearestLocation?.getOutletId().isNullOrEmpty()
                || distanceSelection.isNullOrEmpty()) {
            showErrorMessage(R.string.please_refine_your_search, R.string.choose_outlet)
            return
        }

        if (false == proximityHandle?.isSearchProximity(getKeyword(),
                        nearestLocation?.getOutletId()!!,
                        distanceSelection!!.replace("km", "").trim().toInt(),
                        getPrice())) {
            return
        }
        val productType = if (searchType.equals(SearchType.FULL.toString())){
            getFilterType()
        }else {
            getProductType(searchType)
        }
        LogManager.d("searchProximity: product type = $productType")
        presenter?.searchProximity(
            getKeyword(),
            nearestLocation?.getOutletId()!!,
            distanceSelection!!.replace("km", "").trim().toInt(),
            getPrice(),
            productType
        )
    }


    override fun onWarningSelectLocation() {
        locationAdapter?.clearSelected()
        getView?.txt_location?.clearFocus()
        GeneralUtils.hideSoftKeyboard(getView)
        showErrorMessage(R.string.only_4_locations)
    }

    override fun getContext(): Context? {
        return activity
    }

    private fun checkClassVacancy(classInfo: ClassInfo, isAddToCart: Boolean, isQuickBook: Boolean) {
        presenter?.checkClassVacancy(classInfo, isAddToCart, isQuickBook)

    }

    private fun checkIgVacancy(ig: InterestGroup, isAddCart: Boolean, isQuickBook: Boolean){
        presenter?.checkIgVacancy(ig, isAddCart, isQuickBook)
    }

    override fun handleCourseEvent(classInfo: ClassInfo, isAddCart: Boolean, isQuickBook: Boolean) {
        if (isAddCart) {
            val cartItem = CartItem(UUID.randomUUID().toString(), classInfo, null, null, null, null, null, null)
            if (isQuickBook) {
                (activity as? MainActivity)?.doQuickBook(arrayOf(cartItem).toList())
            } else {
                (activity as? MainActivity)?.addToCart(arrayOf(cartItem).toList())
            }
        } else {
            presenter?.navigationCourseDetailPage(classInfo)
        }
    }

    override fun handleIgEvent(igInfo: InterestGroup, isAddCart: Boolean, isQuickBook: Boolean) {
        if (isAddCart) {
            val cartItem = CartItem(UUID.randomUUID().toString(), null, null, null, null, null, null, null, igInfo = igInfo)
            if (isQuickBook) {
                (activity as? MainActivity)?.doQuickBook(arrayOf(cartItem).toList())
            } else {
                (activity as? MainActivity)?.addToCart(arrayOf(cartItem).toList())
            }
        } else {
            presenter?.navigationIgDetailPage(igInfo)
        }
    }

    private fun mapDataIntoAdapter() {
        productList.clear()
        if (searchType.equals(SearchType.COURSES.toString()))
            classInfoList.forEach {
                val recommendationItem = RecommendationItem()
                recommendationItem.typeItem = SearchType.COURSES.toString()
                recommendationItem.infoItem = it
                productList.add(recommendationItem)

            } else if (searchType.equals(SearchType.FACILITIES.toString())) {
            facilityList.forEach {
                val recommendationItem = RecommendationItem()
                recommendationItem.typeItem = SearchType.FACILITIES.toString()
                recommendationItem.infoItem = it
                productList.add(recommendationItem)
            }
        } else if (searchType.equals(SearchType.EVENTS.toString())) {
            eventList.forEach {
                val recommendationItem = RecommendationItem()
                recommendationItem.typeItem = SearchType.EVENTS.toString()
                recommendationItem.infoItem = it
                productList.add(recommendationItem)
            }
        } else if(searchType.equals(SearchType.INTEREST_GROUPS.toString())){
            igList.forEach {
                val recommendationItem = RecommendationItem()
                recommendationItem.typeItem = SearchType.INTEREST_GROUPS.toString()
                recommendationItem.infoItem = it
                productList.add(recommendationItem)
            }
        }else {
            // SEARCH FULL
            classInfoList.forEach {
                val recommendationItem = RecommendationItem()
                recommendationItem.typeItem = SearchType.COURSES.toString()
                recommendationItem.infoItem = it
                productList.add(recommendationItem)
            }
            facilityList.forEach {
                val recommendationItem = RecommendationItem()
                recommendationItem.typeItem = SearchType.FACILITIES.toString()
                recommendationItem.infoItem = it
                productList.add(recommendationItem)
            }
            eventList.forEach {
                val recommendationItem = RecommendationItem()
                recommendationItem.typeItem = SearchType.EVENTS.toString()
                recommendationItem.infoItem = it
                productList.add(recommendationItem)
            }
            igList.forEach {
                val recommendationItem = RecommendationItem()
                recommendationItem.typeItem = SearchType.INTEREST_GROUPS.toString()
                recommendationItem.infoItem = it
                productList.add(recommendationItem)
            }
        }
        productAdapter?.notifyDataSetChanged()
    }

    override fun setCanOrder(isAlive: Boolean) {
        productAdapter?.notifyDataSetChanged()
    }
}
