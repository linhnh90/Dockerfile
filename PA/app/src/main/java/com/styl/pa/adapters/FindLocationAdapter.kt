package com.styl.pa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.styl.pa.entities.generateToken.Outlet
import com.styl.pa.utils.LogManager
import kotlinx.android.synthetic.main.layout_item_search_outlet.view.*
import java.util.*
import kotlin.collections.ArrayList


class FindLocationAdapter : ArrayAdapter<Outlet> {

    var resource: Int? = null
    var outletDetailList = ArrayList<Outlet>()

    constructor(context: Context?, resource: Int, outletList: ArrayList<Outlet>?) :
            super(context, resource, outletList) {
        this.resource = resource
        var lst: ArrayList<Outlet>? = ArrayList()
        if (outletList != null)
            lst = sortLocationByName(outletList)

        this.outletDetailList = lst!!
        tempItems = ArrayList(outletDetailList)
    }

    fun updateAdapter(outletList: ArrayList<Outlet>?) {
        var lst: ArrayList<Outlet>? = ArrayList()
        if (outletList != null)
            lst = sortLocationByName(outletList)

        this.outletDetailList = lst!!
        notifyDataSetChanged()
    }

    fun getOutletListFilter(): ArrayList<Outlet> {
        return outletDetailList
    }

    override fun getCount(): Int {
        return this.outletDetailList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = convertView

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(resource!!, parent, false)
        }

        if (position >= 0 && position < outletDetailList.size) {
            val course = outletDetailList.get(position)
            view?.txtName?.setText(course.getFriendlyName())
        }

        return view!!
    }

    fun sortLocationByName(lst: ArrayList<Outlet>): ArrayList<Outlet> {
        val list = ArrayList<Outlet>()
        list.addAll(lst.sortedWith(compareBy { it.getFriendlyName() }).toList())
        return list
    }

    override fun getFilter(): Filter {
        return nameFilter
    }

    var isSearch = true

    private var suggestions: ArrayList<Outlet> = ArrayList()
    private var tempItems: ArrayList<Outlet> = ArrayList()

    var nameFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): CharSequence? {
            return (resultValue as Outlet).getFriendlyName()
        }

        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults? {
            val filterResults = FilterResults()
            synchronized(filterResults) {
                if (constraint != null) {
                    suggestions.clear()
                    for (outlet in tempItems) {
                        if (true == outlet.getFriendlyName()?.lowercase(Locale.getDefault())
                                ?.startsWith(constraint.toString().lowercase(Locale.getDefault()))) {
                            suggestions.add(outlet)
                        }
                    }

                    filterResults.values = suggestions
                    filterResults.count = suggestions.size
                    return filterResults
                } else {
                    return null
                }
            }
        }

        override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults?) {
            try {
                if (results != null) {
                    val filterList = results.values as ArrayList<Outlet>
                    outletDetailList.clear()
                    if (filterList.size > 0) {
                        for (output in filterList) {
                            outletDetailList.add(output)
                        }
                    }

                    if (isSearch) {
                        updateAdapter(outletDetailList)
                    } else {
                        isSearch = true
                    }
                }
            } catch (e: Exception) {
                LogManager.i("Filter failed!")
            }
        }
    }
}