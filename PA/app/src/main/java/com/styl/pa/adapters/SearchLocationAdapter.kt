package com.styl.pa.adapters

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Filter
import com.styl.pa.R
import com.styl.pa.entities.generateToken.Outlet
import com.styl.pa.modules.home.ISelectedLocation
import com.styl.pa.utils.LogManager
import kotlinx.android.synthetic.main.item_search_location.view.*
import kotlinx.android.synthetic.main.item_selected_search_location.view.*

class SearchLocationAdapter : ArrayAdapter<Outlet> {

    var locationList: ArrayList<Outlet>? = null
    var resource: Int = -1
    var indexSelectDefault = 0
    var locationListSelected: ArrayList<Outlet>? = ArrayList()
    var listener: ISelectedLocation? = null

    private var suggestions: ArrayList<Outlet> = ArrayList()
    private var tempItems: ArrayList<Outlet> = ArrayList()

    constructor(context: Context, resource: Int, locationList: ArrayList<Outlet>,
                indexSelectDefault: Int, listener: ISelectedLocation) :
            super(context, resource, locationList) {
        this.indexSelectDefault = indexSelectDefault
        this.listener = listener
        this.locationListSelected?.clear()
        if (locationList.size > 0 && this.locationListSelected?.contains(locationList.get(indexSelectDefault)) != true) {
            this.locationListSelected?.add(locationList.get(indexSelectDefault))
        }
        this.locationList = locationList
        this.resource = resource
        tempItems = ArrayList(locationList)
    }

    fun setLocationsSelected(locationList: ArrayList<Outlet>?) {
        locationListSelected = locationList
        notifyDataSetChanged()
    }

    fun getLocationsSelected(): ArrayList<Outlet>? {
        return locationListSelected
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun convertResultToString(resultValue: Any): CharSequence? {
                return (resultValue as Outlet).getFriendlyName()
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                val filterResults = FilterResults()
                synchronized(filterResults) {
                    if (constraint != null) {
                        suggestions.clear()
                        for (outlet in tempItems) {
                            if (true == outlet.getFriendlyName()?.contains(constraint, true)) {
                                suggestions.add(outlet)
                            }
                        }
                        filterResults.values = suggestions
                        filterResults.count = suggestions.size
                        return filterResults
                    } else {
                        filterResults.values = tempItems
                        filterResults.count = tempItems.size
                        return filterResults
                    }
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                try {
                    if (results != null) {
                        val filterList = results.values as ArrayList<Outlet>
                        locationList?.clear()
                        if (filterList.size > 0) {
                            for (output in filterList) {
                                locationList?.add(output)
                            }
                        }
                        notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    LogManager.i("Filter location failed")
                }
            }

        }
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createDropdownItemView(position, convertView, parent, resource)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return  createDropdownItemView(position, convertView, parent, resource)
    }

    fun clearSelected() {
        locationListSelected?.clear()
    }

    fun unselectedLocation(location: Outlet) {
        for (l in locationListSelected!!) {
            if (!l.getOutletId().isNullOrEmpty() && l.getOutletId()?.equals(location.getOutletId())!!) {
                locationListSelected?.remove(l)
                return
            } else {
                if (l.getFriendlyName()?.equals(location.getFriendlyName())!!) {
                    locationListSelected?.remove(l)
                    return
                }
            }
        }
    }

    fun createItemView(position: Int, convertView: View?, parent: ViewGroup?, res: Int): View {
        var view: View? = convertView

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(res, parent, false)
        }

        var locations = ""
        if (getLocationsSelected() != null) {
            for (location in getLocationsSelected()!!) {
                locations += location.getFriendlyName() + ", "
            }
            if (locations.length > 2)
                locations = locations.substring(0, locations.lastIndex - 1)
        }
        view?.txt_name?.setText(locations)

        return view!!
    }

    fun createDropdownItemView(position: Int, convertView: View?, parent: ViewGroup?, res: Int): View {
        var view: View? = convertView

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(res, parent, false)
        }

        val location = locationList?.get(position)
        if (location != null) {
            view?.cb_selected_location?.typeface = ResourcesCompat.getFont(context, R.font.opensans_light)
            view?.cb_selected_location?.setText(location.getFriendlyName())
            view?.cb_selected_location?.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
                    if (isChecked) {
                        if (!locationListSelected?.contains(location!!)!!) {
                            if (locationListSelected?.size!! > 3) {
                                view.cb_selected_location?.isChecked = false
                                listener?.onWarningSelectLocation()
                            } else {
                                locationListSelected?.add(location!!)
                            }
                        }
                    } else {
//                        if (locationListSelected?.size == 1) {
//                            view.cb_selected_location?.isChecked = true
//                        } else {
                        unselectedLocation(location!!)
//                        }
                    }
                }

            })

            if (locationListSelected?.contains(location)!!) {
                view?.cb_selected_location?.isChecked = true
            } else {
                view?.cb_selected_location?.isChecked = false
            }
        }

        return view!!
    }
}