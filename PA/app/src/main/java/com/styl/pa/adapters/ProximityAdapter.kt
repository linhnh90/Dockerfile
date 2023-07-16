package com.styl.pa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.styl.pa.R
import com.styl.pa.entities.generateToken.Outlet
import com.styl.pa.utils.LogManager
import kotlinx.android.synthetic.main.spinner_proximity_dropdown_item.view.*

/**
 * Created by Ngatran on 09/30/2019.
 */
class ProximityAdapter : ArrayAdapter<Outlet> {

    var locationList: ArrayList<Outlet>? = null
    var resource: Int? = null

    private var suggestions: ArrayList<Outlet> = ArrayList()
    private var tempItems: ArrayList<Outlet> = ArrayList()

    constructor(context: Context?, resource: Int, locationList: ArrayList<Outlet>?) :
            super(context, resource, locationList) {
        this.locationList = locationList
        this.resource = resource
        this.tempItems = ArrayList(locationList)
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
                    LogManager.i("Filter proximity failed!")
                }
            }

        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createItemView(position, convertView, parent, R.layout.spinner_proximity_dropdown_item)
    }

    fun createItemView(position: Int, convertView: View?, parent: ViewGroup?, res: Int): View {
        var view: View? = convertView

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(res, parent, false)
        }

        view?.txt_location?.text = locationList?.get(position)?.getFriendlyName()

        return view!!
    }

}