package com.styl.pa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.styl.pa.entities.generateToken.Outlet
import kotlinx.android.synthetic.main.layout_item_search_outlet.view.*

class SearchOutletAdapter : ArrayAdapter<Outlet> {

    var resource: Int? = null
    var outletDetailList = ArrayList<Outlet>()
    var outletDetailListFilter = ArrayList<Outlet>()

    constructor(context: Context, resource: Int, outletList: ArrayList<Outlet>) :
            super(context, resource, outletList) {
        this.resource = resource
        this.outletDetailList = ArrayList(outletList)
        this.outletDetailListFilter = ArrayList(outletList)
    }

    fun updateAdapter(outletList: ArrayList<Outlet>) {
        this.outletDetailList = ArrayList(outletList)
        this.outletDetailListFilter = ArrayList(outletList)
    }

    override fun getCount(): Int {
        return this.outletDetailListFilter.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = convertView

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(resource!!, parent, false)
        }

        var outletDetail = outletDetailListFilter.get(position)
        if (outletDetail != null) {
            view!!.txtName.setText(outletDetail.getName())
        }

        return view!!
    }

    var mFilter = object : Filter() {
        override fun convertResultToString(resultValue: Any?): String? {
            return (resultValue as Outlet).getName()
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            var filterResults = FilterResults()

            filterResults.values = outletDetailListFilter
            filterResults.count = outletDetailListFilter.size

            return filterResults
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {
            if (results != null && results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }

    }

    override fun getFilter(): Filter {

        return mFilter
    }
}