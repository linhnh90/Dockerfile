package com.styl.pa.modules.home.view


import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Rect
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.adapters.FindLocationAdapter
import com.styl.pa.adapters.NeighbourhoodAdapter
import com.styl.pa.adapters.NeighbourhoodAdapter.Location
import com.styl.pa.adapters.ProductAdapter
import com.styl.pa.customViews.CustomLinearLayoutManager
import com.styl.pa.customViews.TextViewBehavior
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.generateToken.Neighbourhood
import com.styl.pa.entities.generateToken.Outlet
import com.styl.pa.entities.generateToken.PageByOutletDetail
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.entities.kioskactivation.KioskInfo
import com.styl.pa.entities.recommendatetions.RecommendationItem
import com.styl.pa.entities.recommendatetions.RecommendationResponse
import com.styl.pa.entities.recommendatetions.RecommendationResponse.Companion.MAX_ITEM
import com.styl.pa.enums.SearchType
import com.styl.pa.enums.TagName
import com.styl.pa.interfaces.AddToCartEvent
import com.styl.pa.interfaces.OnClickRecyclerViewItem
import com.styl.pa.modules.advancedSearch.view.AdvancedSearchFragment
import com.styl.pa.modules.base.Base2Fragment
import com.styl.pa.modules.dialog.LoadingFragment
import com.styl.pa.modules.dialog.MessageDialogFragment
import com.styl.pa.modules.home.IHomeContact
import com.styl.pa.modules.home.presenter.HomePresenter
import com.styl.pa.modules.main.presenter.MainPresenter
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.receiver.RecommendationReceiver
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.LogManager
import com.styl.pa.utils.MySharedPref
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Base2Fragment(), IHomeContact.IView {

    private val loadingFragment = LoadingFragment()
    private var fragment: MessageDialogFragment? = null

    companion object {

        private val TAG = HomeFragment::class.java.simpleName

        const val ACTION_RECOMMENDATION_REFRESH = BuildConfig.APPLICATION_ID + ".actions.ACTION_RECOMMENDATION_REFRESH"
        const val SCREENSAVER_REQ = 5000

        var MAX_CLASS_ITEM = 4
    }

    private var presenter: HomePresenter? = null

    private var getView: View? = null
    private var classInfoList: ArrayList<ClassInfo> = ArrayList()

    private var recommendationList: ArrayList<RecommendationItem> = ArrayList()
    private var homeClassAdapter: ProductAdapter? = null
    private var llRecommendationManager: CustomLinearLayoutManager? = null

    private var findLocationAdapter: FindLocationAdapter? = null
    private var neighbourhoodAdapter: NeighbourhoodAdapter? = null
    private var outletList: ArrayList<Outlet>? = ArrayList()
    private var neighbourhoods = ArrayList<Neighbourhood>()
    private var locationList = ArrayList<Location>()
    private var outletDetailTypeId: String? = ""
    private var searchType: SearchType = SearchType.COURSES
    private var outletSelected: Outlet? = null
    private var kioskInfo: KioskInfo? = null

    private var isHomeScreen = true
    private var isKeyboardShowing = false

    // timer auto refresh top recommended
    private var alarmManager: AlarmManager? = null
    private var alarmIntent1: PendingIntent? = null
    private var alarmIntent2: PendingIntent? = null

    private var canOrder = true

    private val recommendationReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (ACTION_RECOMMENDATION_REFRESH.equals(intent?.action) && !isHidden &&
                    GeneralUtils.isNetworkAvailable(context)) {
                presenter?.getRecommendation()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity is MainActivity) {
            (activity as MainActivity).aliveTracking()
        }

        outletList = (activity as MainActivity).getLocationList()
        neighbourhoods = (activity as MainActivity).getNeighbourhoods()

        alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        val intent = Intent(activity, RecommendationReceiver::class.java)
        val requestCode: Int = (System.currentTimeMillis() / 1000).toInt()
        alarmIntent1 = PendingIntent.getBroadcast(activity, requestCode, intent, 0)

        alarmIntent2 = PendingIntent.getBroadcast(activity, requestCode + 1, intent, 0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        presenter = HomePresenter(this, activity!!)

        // Inflate the layout for this fragment
        getView = inflater.inflate(R.layout.fragment_home, container, false)

        init()
        loadData()

        fragmentManager?.addOnBackStackChangedListener {
            val f = fragmentManager?.findFragmentById(R.id.container)
            isHomeScreen = TagName.HomeFragment.value == f?.tag
        }

        getView!!.viewTreeObserver.addOnGlobalLayoutListener {
            if (!isHomeScreen || !getView!!.actFindLocation.hasFocus()) {
                return@addOnGlobalLayoutListener
            }
            val r = Rect()
            // r will be populated with the coordinates of your view that area still visible.
            getView!!.getWindowVisibleDisplayFrame(r)
            val heightDiff: Int = getView!!.rootView.height - (r.bottom - r.top)
            isKeyboardShowing = heightDiff > 100 // if more than 100 pixels, its probably a keyboard...
            handleLocationPosition()
        }

        return getView
    }

    private fun loadData() {
        showLoading()
        kioskInfo = GeneralUtils.convertStringToObject<KioskInfo>(MySharedPref(activity!!).kioskInfo)
        outletSelected = kioskInfo?.outlet
        outletDetailTypeId = outletSelected?.getOutletTypeId()

        presenter?.getRecommendation()

        alarmIntent1?.let { intent ->

            stopRefreshingRecommendation(intent)
        }
        alarmIntent1?.let { intent ->

            refreshRecommendation(9, 0, 0, intent)
        }

        alarmIntent2?.let { intent ->

            stopRefreshingRecommendation(intent)
        }
        alarmIntent2?.let { intent ->

            refreshRecommendation(17, 0, 0, intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == SCREENSAVER_REQ) {
            startAutoScroll()
        }
    }

    override fun onDestroyView() {
        alarmIntent1?.let { intent ->

            stopRefreshingRecommendation(intent)
        }

        alarmIntent2?.let { intent ->

            stopRefreshingRecommendation(intent)
        }
        super.onDestroyView()
    }

    private fun init() {
        /*  // below code for view pager
            initPageAdapter()
        */

        initRecommendationList()
        getView?.rg_option?.setOnCheckedChangeListener(this)
        getView?.rb_course?.isChecked = true

        reloadButtonSearch()

        initFindLocationAdapter()

        getView?.btn_view_all?.setOnClickListener(this)
        getView?.btn_search?.setOnClickListener(this)

        getView?.txt_find_courses?.setOnTouchListener(
                TextViewBehavior.deleteEvent(getView?.txt_find_courses)
        )
        getView?.actFindLocation?.setOnTouchListener(
                TextViewBehavior.deleteEvent(getView?.actFindLocation)
        )

        getView?.txt_find_courses?.addTextChangedListener(
                TextViewBehavior.changeTextEvent(getView?.txt_find_courses, activity, R.drawable.ic_course)
        )
        getView?.actFindLocation?.addTextChangedListener(
                locationChangingTextEvent
        )

    }

    private fun handleLocationPosition() {
        Handler().postDelayed({
            getView!!.actFindLocation.dropDownHeight = resources.getDimensionPixelOffset(R.dimen.dp_300)
            if (isKeyboardShowing) {
                getView!!.actFindLocation.dropDownAnchor = R.id.view_tmp_2
            } else {
                getView!!.actFindLocation.dropDownAnchor = R.id.actFindLocation
            }
            if (neighbourhoodAdapter?.count ?: 0 > 0) {
                getView!!.actFindLocation.showDropDown()
            }
        }, 50)

    }

    private var locationChangingFirstTime = false
    private val locationChangingTextEvent = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if ((s?.length ?: 0) == 0) {
                getView!!.actFindLocation.requestFocus()
            }
            if ((s?.trim()?.length ?: 0) == 0) {
                TextViewBehavior.invisibleDeleteIcon(getView?.actFindLocation, false, activity, R.drawable.ic_location)
            } else {
                TextViewBehavior.invisibleDeleteIcon(getView?.actFindLocation, true, activity, R.drawable.ic_location)
            }

            if (locationChangingFirstTime) {
                locationChangingFirstTime = false
            } else if (outletSelected != null) {
                outletSelected = null
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            LogManager.d(TAG, "beforeTextChanged")
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            LogManager.d(TAG, "onTextChanged")
        }
    }

    override fun onClick(view: View, position: Int) {
        if (SystemClock.elapsedRealtime() - MainActivity.globalLastClickTime < MainActivity.CLICK_TIMER) {
            return
        }
        MainActivity.globalLastClickTime = SystemClock.elapsedRealtime()

        // hide keyboard
        GeneralUtils.hideSoftKeyboard(view)

        //code if use recycler view
        if (position >= 0 && position < recommendationList.size) {
            if (SearchType.COURSES.toString().equals(recommendationList[position].typeItem)) {
                checkClassVacancy(recommendationList[position].infoItem as ClassInfo, false, false)
            } else if (SearchType.EVENTS.toString().equals(recommendationList[position].typeItem)) {
                presenter?.navigationEventDetailPage(recommendationList[position].infoItem as EventInfo)
            } else if (SearchType.INTEREST_GROUPS.toString().equals(recommendationList[position].typeItem)){
                checkIgVacancy(recommendationList[position].infoItem as InterestGroup, false, false)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_search -> {
                (activity as? MainActivity)?.setBackgroundBottomBar("")
                if (getView?.actFindLocation?.text.toString().isNullOrEmpty() ||
                        !checkExitsLocation()) {
                    showErrorMessage(R.string.please_refine_your_search, R.string.please_choose_outlet)
                    return
                }

                presenter?.navigationSearchPage(SearchType.FULL.toString(), getView?.txt_find_courses?.text.toString(),
                        outletSelected, classInfoList, true)
            }
            R.id.btn_view_all -> {
                if (!MySharedPref(activity).kioskInfo.isNullOrEmpty()) {
                    val objects = Gson().fromJson(MySharedPref(activity).kioskInfo, KioskInfo::class.java)

                    if (objects?.outlet != null) {
                        presenter?.navigationSearchPage(SearchType.COURSES.toString(), "",
                                objects.outlet, classInfoList, false)
                    }
                }
            }
        }
    }

    private fun checkExitsLocation(): Boolean {
        if (outletSelected != null) {
            return true
        }

        outletList?.forEach { result ->
            if (getView?.actFindLocation?.text.toString().trim().equals(
                            result.getFriendlyName()?.trim(), true)) {
                outletSelected = result
                return true
            }
        }

        return false
    }

    override fun onResume() {
        super.onResume()
        setLocation()
        (activity as? MainActivity)?.enableLine(false)
        (activity as? MainActivity)?.setBackgroundBottomBar(MainActivity.MODE_HOME)
        (activity as? MainActivity)?.showProgressPayment(false)
        getView?.txt_find_courses?.setText("")

        if ((activity as? MainActivity)?.canOrder != this.canOrder) {
            this.canOrder = (activity as? MainActivity)?.canOrder ?: true
            homeClassAdapter?.notifyDataSetChanged()
        }
//        startScreenSaver()
        reloadButtonSearch()
        startAutoScroll()
        refreshRecommendationView()
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter()
        filter.addAction(ACTION_RECOMMENDATION_REFRESH)
        LocalBroadcastManager.getInstance(activity as Context).registerReceiver(recommendationReceiver, filter)
    }

    fun hideSoftKeyboard() {
        GeneralUtils.hideSoftKeyboard(getView)
    }

    fun setLocation() {
        if (!MySharedPref(activity).kioskInfo.isNullOrEmpty()) {
            val objects = Gson().fromJson(MySharedPref(activity).kioskInfo, KioskInfo::class.java)

            if (objects?.outlet != null) {
                setTitle(getString(R.string.home_title, objects?.outlet?.getFriendlyName()))
                getView?.btn_view_all?.text = getString(R.string.view_all, objects?.outlet?.getFriendlyName())
            }
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {

        when (checkedId) {
            R.id.rb_course -> {
                searchType = SearchType.COURSES
                reloadButtonSearch()
            }

            R.id.rb_facilities -> {
                searchType = SearchType.FACILITIES
                reloadButtonSearch()
            }

            R.id.rb_event -> {
                searchType = SearchType.EVENTS
                reloadButtonSearch()
            }
        }
    }

    private fun reloadButtonSearch() {
        locationChangingFirstTime = true
        outletSelected = kioskInfo?.outlet
        if (searchType.toString().equals(SearchType.EVENTS.toString())) {
            getView?.txt_find_courses?.setHint(activity?.resources?.getString(R.string.advanced_search_event_hint))
            getView?.rb_course?.setBackgroundColor(Color.TRANSPARENT)
            getView?.rb_facilities?.setBackgroundColor(Color.TRANSPARENT)
            getView?.rb_event?.setBackgroundResource(R.drawable.border_white_bg_trans_25)
            getView?.actFindLocation?.isEnabled = true
            getView?.actFindLocation?.setHint(activity?.resources?.getString(R.string.search_location_home_hint))
            getView?.actFindLocation?.setText(outletSelected?.getFriendlyName())
        } else if (searchType.toString().equals(SearchType.COURSES.toString())) {
            getView?.txt_find_courses?.setHint(activity?.resources?.getString(R.string.search_course_home_hint))
            getView?.rb_course?.setBackgroundResource(R.drawable.border_white_bg_trans_25)
            getView?.rb_facilities?.setBackgroundColor(Color.TRANSPARENT)
            getView?.rb_event?.setBackgroundColor(Color.TRANSPARENT)
            getView?.actFindLocation?.isEnabled = true
            getView?.actFindLocation?.setHint(activity?.resources?.getString(R.string.search_location_home_hint))
            getView?.actFindLocation?.setText(outletSelected?.getFriendlyName())
        } else if (searchType.toString().equals(SearchType.FULL.toString())) {
            LogManager.d(searchType.toString())

        } else {
            getView?.txt_find_courses?.setHint(activity?.resources?.getString(R.string.search_facility_home_hint))
            getView?.rb_course?.setBackgroundColor(Color.TRANSPARENT)
            getView?.rb_facilities?.setBackgroundResource(R.drawable.border_white_bg_trans_25)
            getView?.rb_event?.setBackgroundColor(Color.TRANSPARENT)
            getView?.actFindLocation?.isEnabled = true
            getView?.actFindLocation?.setText(kioskInfo?.outlet?.getFriendlyName())
        }
    }


    private val onTouchListener = object : View.OnTouchListener {
        var s1 = 0f
        var s2 = 0f
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    s1 = event.x
                    return true
                }

                MotionEvent.ACTION_UP -> {
                    s2 = event.x

                    val delta = s2 - s1

                    if (delta < 0) { // r -> l
                        flag = true
                    } else if (delta > 0) { //l -> r
                        flag = false
                    }
                }
            }

            return false
        }

    }

    private val onScrollList = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val findLastVisibleItemPosition = llRecommendationManager?.findLastVisibleItemPosition()
                    ?: 0

            if (findLastVisibleItemPosition % MAX_CLASS_ITEM == 0 || (findLastVisibleItemPosition + 1) % MAX_CLASS_ITEM == 0) {
                val index = findLastVisibleItemPosition / MAX_CLASS_ITEM
                checkIndicationForPage(index)
            }
        }
    }

    private var addToCart = object : AddToCartEvent.AddToCart {
        override fun addItem(view: View, position: Int, isQuickBook: Boolean) {
            if (SystemClock.elapsedRealtime() - MainActivity.globalLastClickTime < MainActivity.CLICK_TIMER) {
                return
            }
            MainActivity.globalLastClickTime = SystemClock.elapsedRealtime()

            if (position >= 0 && position < recommendationList.size) {
                if (SearchType.COURSES.toString().equals(recommendationList[position].typeItem)) {
                    checkClassVacancy(recommendationList[position].infoItem as ClassInfo, true, isQuickBook)
                } else if (SearchType.EVENTS.toString().equals(recommendationList[position].typeItem)) {
                    val cartItem = CartItem(UUID.randomUUID().toString(), null, null, recommendationList[position].infoItem as EventInfo, null, null, null, null)
                    if(isQuickBook) {
                        (activity as? MainActivity)?.doQuickBook(arrayOf(cartItem).toList())
                    }else{
                        (activity as? MainActivity)?.addToCart(arrayOf(cartItem).toList())
                    }
                } else if (SearchType.INTEREST_GROUPS.toString().equals(recommendationList[position].typeItem)){
                    checkIgVacancy(recommendationList[position].infoItem as InterestGroup, true, isQuickBook)
                }
            }
        }
    }

    private fun initRecommendationList() {
        homeClassAdapter = ProductAdapter(recommendationList, activity)
        homeClassAdapter?.setAddToCart(addToCart)
        homeClassAdapter?.setOnClickRecyclerViewItem(this)

        llRecommendationManager = CustomLinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        getView?.rcv_recommendation_list?.setHasFixedSize(true)
        getView?.rcv_recommendation_list?.isNestedScrollingEnabled = false
        getView?.rcv_recommendation_list?.layoutManager = llRecommendationManager
        getView?.rcv_recommendation_list?.adapter = homeClassAdapter
        getView?.rcv_recommendation_list?.addOnScrollListener(onScrollList)
        getView?.rcv_recommendation_list?.setOnTouchListener(onTouchListener)

        generateIndicationForPage()
    }

    private fun generateIndicationForPage() {
        getView?.rb_option?.removeAllViews()
        getView?.rb_option?.clearCheck()

        val size = Math.ceil(recommendationList.size * 1.0 / MAX_CLASS_ITEM).toInt()

        if (size > 0) {
            for (i in 0 until size) {
                val rb = RadioButton(activity)
                rb.buttonDrawable = null
                rb.background = ContextCompat.getDrawable(activity!!, R.drawable.bg_indicator_dot)
                val params = RadioGroup.LayoutParams((activity?.resources?.getDimensionPixelSize(R.dimen.dp_25)
                        ?: 0), (activity?.resources?.getDimensionPixelSize(R.dimen.dp_25) ?: 0))
                params.setMargins((activity?.resources?.getDimensionPixelSize(R.dimen.dp_6)
                        ?: 0), 0, (activity?.resources?.getDimensionPixelSize(R.dimen.dp_6)
                        ?: 0), 0)
                rb.layoutParams = params
                rb.id = i
                getView?.rb_option?.addView(rb)
            }

            checkIndicationForPage(0)
        }
    }

    private fun checkIndicationForPage(index: Int) {
        val size = getView?.rb_option?.childCount ?: -1
        if (size > 0 && index >= 0 && index < size) {
            getView?.rb_option?.check(index)
        }
    }

    var itemLocationClick = object : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position < locationList.size && position >= 0) {
                val loc = neighbourhoodAdapter?.getAllItems()?.get(position)
                if (loc != null && loc.type != NeighbourhoodAdapter.TYPE_TITLE) {
                    locationChangingFirstTime = true
                    getView?.actFindLocation?.setText(loc.location)
                    getView?.actFindLocation?.setSelection(getView?.actFindLocation?.text?.length
                            ?: 0)
                    getView?.actFindLocation?.clearFocus()
                    outletSelected = outletList?.find { it.getFriendlyName().equals(loc.location, true) }
                    GeneralUtils.hideSoftKeyboard(getView)
                }
            }
        }
    }

    private fun reloadFindLocationAdapter() {
        findLocationAdapter = FindLocationAdapter(activity, R.layout.item_find_location, outletList)
        findLocationAdapter?.notifyDataSetChanged()
        locationList = handleListNeighbourhood(neighbourhoods)
        neighbourhoodAdapter = NeighbourhoodAdapter(activity, R.layout.location_item, locationList)
        getView?.actFindLocation?.setAdapter(neighbourhoodAdapter)
        getView?.actFindLocation?.onItemClickListener = itemLocationClick
    }

    private fun handleListNeighbourhood(lst: ArrayList<Neighbourhood>): ArrayList<Location> {
        val listLocation = ArrayList<Location>()
        for (item in lst) {
            item.outlets.sort()
            val locationsTmp = ArrayList<Location>()
            for (outlet in item.outlets) {
                val outletNameUppercase = outlet.uppercase(Locale.ENGLISH)
                if (!outletNameUppercase.endsWith("CC")) {
                    continue
                }
                locationsTmp.add(
                    Location(
                        NeighbourhoodAdapter.TYPE_VALUE,
                        outlet.uppercase(Locale.ENGLISH), item.title?.uppercase(Locale.ENGLISH)
                    )
                )
            }
            if (locationsTmp.isNotEmpty()) {
                listLocation.add(
                    Location(
                        NeighbourhoodAdapter.TYPE_TITLE,
                        item.title?.uppercase(Locale.ENGLISH), null
                    ))
                listLocation.addAll(locationsTmp)
            }
        }
        return listLocation
    }

    private fun initFindLocationAdapter() {
        reloadFindLocationAdapter()
        getView?.actFindLocation?.threshold = 1
    }

    fun getOnClickRecyclerViewItem(): OnClickRecyclerViewItem {
        return this
    }


    override fun showLoading() {
        if (loadingFragment.isAdded) {
            return
        }

        val ft = fragmentManager?.beginTransaction()
        ft?.add(loadingFragment, LoadingFragment::class.java.simpleName)
        ft?.commitAllowingStateLoss()
    }

    override fun dismissLoading() {
        if (loadingFragment.isAdded || loadingFragment.isVisible) {
            loadingFragment.dismissAllowingStateLoss()
        }
    }

    override fun <T> showErrorMessage(response: BaseResponse<T>) {
        dismissLoading()

        if (fragment != null) {
            fragment?.dismiss()
        }
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
            Handler().post {
                fragment?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
            }
        }
    }

    override fun showErrorMessage(messageResId: Int) {
        dismissLoading()

        if (fragment != null) {
            fragment?.dismiss()
        }
        fragment = MessageDialogFragment.newInstance(0, messageResId)
        if (fragmentManager != null) {
            Handler().post {
                fragment?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
            }
        }
    }

    override fun showErrorMessage(titleResId: Int, messageResId: Int) {
        dismissLoading()

        if (fragment != null && fragment!!.isVisible) {
            fragment?.dismiss()
        }
        fragment = MessageDialogFragment.newInstance(titleResId, messageResId, true)
        if (fragmentManager != null) {
            Handler().post {
                fragment?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
            }
        }
    }

    override fun onSearchLocationByOutlet(outletDetail: PageByOutletDetail?) {
        outletList = outletDetail?.getOutletDetailList()
        findLocationAdapter?.notifyDataSetChanged()
        reloadFindLocationAdapter()
    }

    override fun onGetClassByOutletSuccess(recommendationResponse: RecommendationResponse?) {
        classInfoList.clear()
        if (recommendationResponse?.classList != null && recommendationResponse.classList!!.size > MAX_ITEM) {
            classInfoList.addAll(ArrayList(recommendationResponse.classList!!.subList(0, MAX_ITEM)))
        } else {
            if (recommendationResponse?.classList != null)
                classInfoList.addAll(recommendationResponse.classList!!)
        }

        recommendationList.clear()
        if (recommendationResponse != null) {
            recommendationList.addAll(recommendationResponse.generateRecommendationList())
        }

        if (activity is MainActivity) {
            (activity as MainActivity).classInfoList = classInfoList
        }

//update adapter
//        customPagerAdapter?.notifyDataSetChanged()

        homeClassAdapter?.notifyDataSetChanged()

        if (recommendationList.size > 0) {
            getView?.rcv_recommendation_list?.scrollToPosition(0)
        }

        generateIndicationForPage()

        if (recommendationList.size > 0 && !isStop) {
            autoScroll()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        dismissLoading()
        if (hidden) {
            stopScreenSaver()
            stopAutoScroll()
        }
    }

    override fun onDestroy() {
        presenter?.onDestroy()
        super.onDestroy()
    }

    private fun refreshRecommendation(hourOfDay: Int, minute: Int, second: Int, alarmIntent: PendingIntent) {
        val calendar = Calendar.getInstance()
        if (calendar.get(Calendar.HOUR_OF_DAY) >= hourOfDay) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, second)

        alarmManager?.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, alarmIntent)
    }

    private fun stopRefreshingRecommendation(alarmIntent: PendingIntent) {
        alarmManager?.cancel(alarmIntent)
    }

    private fun startScreensaver() {
        val screenSaverDir = File(
                Environment.getExternalStorageDirectory().absolutePath + "/${MainPresenter.SCREENSAVER_PATH}")
        if (screenSaverDir.exists() && screenSaverDir.listFiles().isNotEmpty()) {
            presenter?.navigationScreenSaverFragment()
        }
    }

    private fun stopScreenSaver() {
        countDownTimerScreen?.cancel()
    }

    private var countDownTimerScreen: CountDownTimer? = null
    fun dispatchTouchEvent(event: MotionEvent?) {
        stopScreenSaver()

        if (event != null) {
            startAutoScroll()
            Handler().postDelayed({
                stopAutoScrollRecommendation()
            }, 1000)
        } else {
            Handler().postDelayed({
                isStartAutoScroll()
            }, 1000)
        }

//        startScreenSaver()
    }

    private var countDownTimerInHome: CountDownTimer? = null
    private val TIME_IN_HOME = 5 * 60 * 1000L
    private val INTERVAL_RECOMMENDATION_TIME = 3000L

    private fun initAutoScroll() {
        countDownTimerInHome = object : CountDownTimer(TIME_IN_HOME, 1000L) {
            override fun onFinish() {
                resetDefaultState()
                startAutoScrollRecommendationList()
                startScreensaver()
            }

            override fun onTick(p0: Long) {
                // Do nothing - no need to handle this method
            }
        }
    }

    private fun startAutoScroll() {
        if (isHidden) {
            return
        }
        if (countDownTimerInHome != null) {
            countDownTimerInHome?.cancel()
        } else {
            initAutoScroll()
        }
        countDownTimerInHome?.start()
    }

    private fun isStartAutoScroll() {
        val advancedSearchFragment = activity?.supportFragmentManager?.findFragmentByTag(AdvancedSearchFragment::class.java?.simpleName)
        if (advancedSearchFragment == null) {
            startAutoScroll()
        } else {
            refreshRecommendationView()
            stopAutoScroll()
        }
    }

    private fun stopAutoScroll() {
        if (countDownTimerInHome != null) {
            countDownTimerInHome?.cancel()
            stopAutoScrollRecommendation()
        }
    }

    private var currentItem = 0
    var flag = true
    private var isStop = true
    private val handler: Handler? = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        if (currentItem < (homeClassAdapter?.itemCount ?: 0)) {
            if (currentItem == (homeClassAdapter?.itemCount ?: 0) - 1) {
                flag = false
                currentItem -= (MAX_CLASS_ITEM - 2)
            } else if (currentItem == 0) {
                flag = true
                currentItem += (MAX_CLASS_ITEM - 2)
            }
            if (flag) {
                currentItem++
                getView?.rcv_recommendation_list?.smoothScrollToPosition(currentItem)
            } else {
                currentItem--
                getView?.rcv_recommendation_list?.smoothScrollToPosition(currentItem)
            }
            if (!isStop) {
                autoScroll()
            }
        }
    }

    private fun startAutoScrollRecommendationList() {
//        currentItem = (llRecommendationManager?.findLastCompletelyVisibleItemPosition() ?: 0)

        isStop = false
        autoScroll()
    }

    private fun autoScroll() {
        handler?.removeCallbacks(runnable)
        handler?.postDelayed(runnable, INTERVAL_RECOMMENDATION_TIME)
    }

    private fun stopAutoScrollRecommendation() {
        if (!isStop) {
            refreshRecommendationView()
            isStop = true
            handler?.removeCallbacks(runnable)
        }
    }

    private fun refreshRecommendationView() {
        currentItem = 0
        getView?.rcv_recommendation_list?.scrollToPosition(currentItem)
        if ((getView?.rb_option?.childCount ?: -1) > 0) {
            getView?.rb_option?.check(0)
        }
    }

    override fun getContext(): Context? {
        return activity
    }

    override fun onStop() {
        super.onStop()
        dismissLoading()
        stopAutoScroll()

        LocalBroadcastManager.getInstance(activity as Context).unregisterReceiver(recommendationReceiver)
    }

    private fun resetDefaultState() {
        if (activity != null && activity is MainActivity) {
            LogManager.i("Delete cart because no activity within 5 mins")
            (activity as MainActivity).deleteCart(true)
        }

        if (getView?.rb_option?.checkedRadioButtonId != R.id.rb_course) {
            getView?.rb_course?.isChecked = true

            reloadButtonSearch()
        }

        getView?.txt_find_courses?.setText("")
        findLocationAdapter?.isSearch = false

        if (outletSelected != null && !locationChangingFirstTime) {
            locationChangingFirstTime = true
        }
        getView?.actFindLocation?.setText(outletSelected?.getFriendlyName() + " " ?: "")

        Handler().postDelayed({
            findLocationAdapter?.isSearch = true
        }, 3000)

        if (activity != null) {
            GeneralUtils.hideKeyboard(activity!!)
        }
    }

    private fun checkClassVacancy(classInfo: ClassInfo, isAddToCart: Boolean, isQuickBook: Boolean) {
        presenter?.checkClassVacancy(classInfo, isAddToCart, isQuickBook)
    }

    private fun checkIgVacancy(ig: InterestGroup, isAddToCart: Boolean, isQuickBook: Boolean) {
        presenter?.checkIgVacancy(ig, isAddToCart, isQuickBook)
    }

    override fun handleCourseEvent(classInfo: ClassInfo, isAddCart: Boolean, isQuickBook: Boolean) {
        if (isAddCart) {
            val cartItem = CartItem(UUID.randomUUID().toString(), classInfo, null, null, null, null, null, null)
            if(isQuickBook) {
                (activity as? MainActivity)?.doQuickBook(arrayOf(cartItem).toList())
            } else {
                (activity as? MainActivity)?.addToCart(arrayOf(cartItem).toList())
            }
        } else {
            Handler().post { presenter?.navigationCourseDetailPage(classInfo) }
        }
    }

    override fun handleIgEvent(igInfo: InterestGroup, isAddCart: Boolean, isQuickBook: Boolean) {
        if (isAddCart) {
            val cartItem = CartItem(UUID.randomUUID().toString(), null, null, null, null, null, null, null, igInfo = igInfo)
            if(isQuickBook) {
                (activity as? MainActivity)?.doQuickBook(arrayOf(cartItem).toList())
            } else {
                (activity as? MainActivity)?.addToCart(arrayOf(cartItem).toList())
            }
        } else {
            Handler().post { presenter?.navigationIgDetailPage(igInfo) }
        }
    }

    override fun setCanOrder(isAlive: Boolean) {
        this.canOrder = isAlive
        homeClassAdapter?.notifyDataSetChanged()
    }
}
