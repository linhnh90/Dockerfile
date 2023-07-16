package com.styl.pa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.styl.pa.R
import com.styl.pa.utils.LogManager
import kotlinx.android.synthetic.main.location_item.view.*
import kotlin.math.roundToInt

class NeighbourhoodAdapter : ArrayAdapter<NeighbourhoodAdapter.Location> {

    var resource: Int? = null

    private var listLocation = ArrayList<Location>()
    private var tempItems = ArrayList<Location>()
    private var suggestions = ArrayList<Location>()


    constructor(context: Context?, resource: Int, locations: ArrayList<Location>) :
            super(context, resource, locations) {
        this.resource = resource
        this.listLocation = locations
        tempItems = ArrayList(listLocation)
    }

    fun getAllItems(): ArrayList<Location> {
        return listLocation
    }

    override fun getCount(): Int {
        return listLocation.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(context).inflate(resource!!, parent, false)
        }

        val location = listLocation[position]
        view?.txt_location?.text = location.location
        val right = context.resources?.getDimension(R.dimen.dp_20) ?: 0f
        val top = context.resources?.getDimension(R.dimen.dp_10) ?: 0f
        val bottom = context.resources?.getDimension(R.dimen.dp_10) ?: 0f
        val left = if (location.type == TYPE_TITLE) {
            view?.isEnabled = false
            context.resources?.getDimension(R.dimen.dp_20) ?: 0f
        } else {
            view?.isEnabled =true
            context.resources?.getDimension(R.dimen.dp_60) ?: 0f
        }
         view?.txt_location?.setPadding(
                left.roundToInt(),
                top.roundToInt(),
                right.roundToInt(),
                bottom.roundToInt()
        )

        return view!!
    }

    override fun isEnabled(position: Int): Boolean {
        val loc = listLocation[position]
        return loc.type != TYPE_TITLE
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun convertResultToString(resultValue: Any?): CharSequence? {
                return (resultValue as Location).location
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResult = FilterResults()
                if (constraint != null) {
                    suggestions.clear()
                    for (item in tempItems) {
                        if (item.location?.contains(constraint, true) == true
                                && item.type == TYPE_VALUE) {
                            if (suggestions.find { it.location?.contains(item.locationTitle.toString()) == true } == null) {
                                suggestions.add(Location(TYPE_TITLE, item.locationTitle, null))
                            }
                            suggestions.add(item)
                        }
                    }
                    filterResult.values = suggestions
                    filterResult.count = suggestions.size
                } else {
                    filterResult.values = tempItems
                    filterResult.count = tempItems.size
                }
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                try {
                    if (results != null) {
                        val filterList = results.values as ArrayList<Location>
                        listLocation.clear()
                        if (filterList.size > 0) {
                            for (output in filterList) {
                                listLocation.add(output)
                            }
                        }
                        notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    LogManager.i("Filter neighbourhood failed")
                }
            }

        }
    }


    class Location(var type: Int?, var location: String?, var locationTitle: String?)

    companion object {
        const val TYPE_TITLE = 1
        const val TYPE_VALUE = 2
    }

}