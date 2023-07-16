package com.styl.pa.modules.proximity

import android.content.Context
import android.graphics.Color
import android.os.SystemClock
import android.view.View
import android.webkit.WebView
import android.widget.RadioGroup
import com.google.gson.Gson
import com.styl.pa.R
import com.styl.pa.entities.advancedSearch.AdvancedSearchRequest
import com.styl.pa.entities.generateToken.Outlet
import com.styl.pa.entities.proximity.ProximityLocationInfo
import com.styl.pa.entities.proximity.ProximityLocationResponse
import com.styl.pa.entities.search.PriceClass
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.search.ISearchContact
import com.styl.pa.utils.LogManager
import kotlinx.android.synthetic.main.fragment_search_page.view.*
import java.util.*

/**
 * Created by Ngatran on 11/26/2019.
 */
class ProximityHandle(var searchView: ISearchContact.IView?, var view: View?, var context: Context?) : ISearchContact.IHandleProximity {

    companion object {
        private const val JAVASCRIPT_STR = "javascript: "
    }

    private val markerClickEvent = object : IMarkerClickEvent {
        override fun markerClick(proximityLocationInfo: ProximityLocationInfo) {
            if (SystemClock.elapsedRealtime() - MainActivity.globalLastClickTime < 500) {
                return
            }
            MainActivity.globalLastClickTime = SystemClock.elapsedRealtime()

            if (context is MainActivity) {
                (context as MainActivity).runOnUiThread {
                    pauseWebView(true)

                    refreshMarkerResultView(true)
                    searchView?.searchLocationCourse(proximityLocationInfo, true)
                }
            }
        }
    }

    private fun refreshMarkerResultView(isClickMarker: Boolean) {
        if (isClickMarker) {
            view?.rcv_class_info_list?.visibility = View.VISIBLE
            view?.v_proximity?.visibility = View.GONE
            view?.rg_map_option?.visibility = View.INVISIBLE
            view?.btn_show_all?.visibility = View.VISIBLE
        } else {
            view?.rcv_class_info_list?.visibility = View.GONE
            view?.v_proximity?.visibility = View.VISIBLE
            view?.rg_map_option?.visibility = View.VISIBLE
            view?.btn_show_all?.visibility = View.GONE
        }
    }

    private val checkedEvent = object : RadioGroup.OnCheckedChangeListener {
        override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
            when (checkedId) {
                R.id.rb_map -> {
                    refreshMapOptionView(true)
                    setProximityLocationTitle()
                    searchView?.searchProximity()
                }

                R.id.rb_list -> {
                    refreshMapOptionView(false)
                    searchView?.searchAllOfProximityCourse()
                }
            }
        }

    }

    private fun refreshMapOptionView(isMap: Boolean) {
        if (isMap) {
            view?.v_proximity?.visibility = View.VISIBLE
            view?.rcv_class_info_list?.visibility = View.GONE
            view?.txt_info?.visibility = View.GONE

            pauseWebView(false)
        } else {
            view?.v_proximity?.visibility = View.GONE
            view?.rcv_class_info_list?.visibility = View.VISIBLE
            view?.txt_info?.visibility = View.GONE

            pauseWebView(true)
        }
    }

    private val onClickEvent = object : View.OnClickListener {
        override fun onClick(v: View?) {
            if (SystemClock.elapsedRealtime() - MainActivity.globalLastClickTime < 500) {
                return
            }
            MainActivity.globalLastClickTime = SystemClock.elapsedRealtime()

            when (v?.id) {
                R.id.btn_show_all -> {
                    pauseWebView(false)

                    refreshMarkerResultView(false)
                    setProximityLocationTitle()

                    searchView?.searchProximity()
                }
            }
        }

    }

    private var proximityLocationInfo: ProximityLocationResponse? = null
    private var proximitySearchRequest: AdvancedSearchRequest? = null
    private var courseSearchRequest: AdvancedSearchRequest? = null

    fun isSearchProximity(keyword: String?, nearestLocation: String, distance: Int, price: PriceClass?): Boolean {
        try {
            if (proximitySearchRequest != null &&
                    (true == proximitySearchRequest?.keyword?.trim()?.lowercase(Locale.ENGLISH)
                        ?.equals(keyword?.toString()?.trim()?.lowercase(Locale.ENGLISH))
                            && true == proximitySearchRequest?.nearestLocation?.equals(nearestLocation)
                            && true == proximitySearchRequest?.distance?.toString().equals(distance.toString().replace("km", "").trim())
                            && proximitySearchRequest?.price?.max ?: 0 == price?.max ?: -1
                            && proximitySearchRequest?.price?.min ?: 0 == price?.min ?: -1)) {
                return false
            }
            return true
        } catch (e: Exception) {
            LogManager.i("Unable to check proximity search")
        }
        return true
    }

    fun setProximitySearchRequest(proximitySearchRequest: AdvancedSearchRequest?) {
        this.proximitySearchRequest = proximitySearchRequest
    }

    fun isSearchCourse(keyword: String?, outletNames: ArrayList<String>?, price: PriceClass?,
                       nearestLocation: String?, distance: Int?): Boolean {
        try {
            if (courseSearchRequest != null &&
                    (true == courseSearchRequest?.keyword?.trim()?.lowercase(Locale.ENGLISH)
                        ?.equals(keyword?.toString()?.trim()?.lowercase(Locale.ENGLISH))
                            && true == (courseSearchRequest?.nearestLocation
                            ?: "").equals(nearestLocation
                            ?: "")
                            && true == (courseSearchRequest?.distance ?: 0).toString().equals((distance
                            ?: 0).toString().replace("km", "").trim())
                            && proximitySearchRequest?.price?.max ?: 0 == price?.max ?: -1
                            && proximitySearchRequest?.price?.min ?: 0 == price?.min ?: -1
                            && isSearchAllOfCourse(outletNames))) {
                return false
            }
            return true
        } catch (e: Exception) {
            LogManager.i("Unable to check course search")
        }
        return true
    }

    private fun isSearchAllOfCourse(outletNames: ArrayList<String>?): Boolean {
        if (outletNames.isNullOrEmpty() && courseSearchRequest?.outletNames.isNullOrEmpty()) {
            return true
        }

        if (!outletNames.isNullOrEmpty() && !courseSearchRequest?.outletNames.isNullOrEmpty() &&
                true == outletNames[0].equals((courseSearchRequest?.outletNames?.get(0) ?: ""))) {
            return true
        }

        return false
    }

    fun setCourseSearchRequest(courseSearchRequest: AdvancedSearchRequest?) {
        this.courseSearchRequest = courseSearchRequest
    }

    override fun initProximityView() {
        showProximityView()
        view?.rg_map_option?.setOnCheckedChangeListener(checkedEvent)
        refreshMapOptionView(true)

        view?.btn_show_all?.setOnClickListener(onClickEvent)

        showDefaultMap()
    }

    private fun showDefaultMap() {
        view?.v_proximity?.loadUrl("file:///android_asset/proximity/map.html")
        val webSettings = view?.v_proximity?.settings
        webSettings?.javaScriptEnabled = true
        view?.v_proximity?.setBackgroundColor(Color.TRANSPARENT)

        view?.v_proximity?.addJavascriptInterface(JavaScriptInterface(markerClickEvent, searchView?.getContext()), "AndroidInterface")
        WebView.setWebContentsDebuggingEnabled(false)
    }

    override fun showProximityLocationInfo(distance: Int, currentLocation: Outlet?, response: ProximityLocationResponse?) {
        pauseWebView(false)

        showProximityView()
        this.proximityLocationInfo = response

        setProximityLocationTitle()

        view?.v_proximity?.evaluateJavascript(JAVASCRIPT_STR +
                "setZoomView(\"" + distance + "\")",
                null)
        view?.v_proximity?.evaluateJavascript(JAVASCRIPT_STR +
                "deleteAllOfMarkers()",
                null)

        val latlngList = response?.outletList

        if (!latlngList.isNullOrEmpty()) {

            latlngList.forEach { outlet ->
                outlet.outlet?.setAddress("")
            }

            var outletData: String = Gson().toJson(currentLocation)
            outletData = outletData.replace("\"", "\\\"")

            view?.v_proximity?.evaluateJavascript(JAVASCRIPT_STR +
                    "setCurrentLocation(\"" + outletData + "\")",
                    null)

            var data: String = Gson().toJson(latlngList)
            data = data.replace("\"", "\\\"")

            view?.v_proximity?.evaluateJavascript( JAVASCRIPT_STR +
                    "showAllOfLocation(\"" + data + "\")",
                    null)
        }
    }

    private fun setProximityLocationTitle() {
        searchView?.showTotalRecord(proximityLocationInfo?.totalProduct ?: -1)
    }

    fun deleteAllOfMarker() {
        view?.v_proximity?.evaluateJavascript(JAVASCRIPT_STR +
                "deleteAllOfMarkers()",
                null)
    }

    private fun showProximityView() {
        if (view?.rg_map_option?.visibility != View.VISIBLE) {
            view?.rcv_class_info_list?.visibility = View.GONE
            view?.rg_map_option?.visibility = View.VISIBLE
            view?.btn_show_all?.visibility = View.GONE
            view?.txt_info?.visibility = View.GONE
        }

        if (view?.v_proximity?.visibility == View.GONE) {
            view?.v_proximity?.visibility = View.VISIBLE
        }

        view?.rb_map?.isChecked = true
    }

    private fun pauseWebView(isPause: Boolean) {
        if (isPause) {
            view?.v_proximity?.onPause()
            view?.v_proximity?.pauseTimers()
        } else {
            view?.v_proximity?.resumeTimers()
            view?.v_proximity?.onResume()
        }
    }

    private fun releaseWebView() {
        view?.v_proximity?.clearHistory()
        view?.v_proximity?.clearCache(true)
        view?.v_proximity?.loadUrl("about:blank")
        pauseWebView(true)
    }

    override fun destroyProximityView() {
        releaseWebView()
        proximityLocationInfo = null
        proximitySearchRequest = null
        courseSearchRequest = null
        view?.rcv_class_info_list?.visibility = View.VISIBLE
        view?.rg_map_option?.visibility = View.GONE
        view?.btn_show_all?.visibility = View.GONE
        view?.v_proximity?.visibility = View.GONE
    }
}