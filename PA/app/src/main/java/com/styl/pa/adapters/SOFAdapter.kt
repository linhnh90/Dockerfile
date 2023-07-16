package com.styl.pa.adapters

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.styl.castle_terminal_upt1000_api.message.device.SOFItem
import com.styl.pa.R
import com.styl.pa.utils.LogManager
import kotlinx.android.synthetic.main.item_sof.view.*


class SOFAdapter(
        sofItems: List<SOFItem>,
        private var context: Context
) : RecyclerView.Adapter<SOFAdapter.SoFViewHolder>() {

    companion object {
        private val TAG = SOFAdapter::class.java.simpleName
        const val SPINNER_TEXT_COLOR = "#808080"
    }

    private var sofList: List<SOFItem> = sofItems
    private var sofPriorityList: List<Short> = ArrayList()

    private fun getSofPriority() : MutableList<Short> {
        val sofPriorityList : MutableList<Short> = ArrayList()
        for (sofItem in sofList) {
            sofPriorityList.add(sofItem.idSofPriority)
        }
        sofPriorityList.sort()
        return sofPriorityList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SOFAdapter.SoFViewHolder {
        return SoFViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sof, parent, false))
    }

    override fun getItemCount(): Int {
        return sofList.size
    }

    override fun onBindViewHolder(holder: SOFAdapter.SoFViewHolder, position: Int) {
        sofPriorityList = getSofPriority()
        val sofItem = sofList.get(position)
        holder.itemView.tv_sof_description?.text = sofItem.idSofDescription
        val priorityAdapter : ArrayAdapter<Short> = object : ArrayAdapter<Short>(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                sofPriorityList
        ) {
            override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(
                        position,
                        convertView,
                        parent
                ) as TextView
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
                view.setTextColor(Color.parseColor(SPINNER_TEXT_COLOR))
                return view
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view: TextView = super.getView(
                        position,
                        convertView,
                        parent
                ) as TextView
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
                view.setTextColor(Color.parseColor(SPINNER_TEXT_COLOR))
                return view
            }
        }
        holder.itemView.sp_priority?.adapter = priorityAdapter
        holder.itemView.sp_priority?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                LogManager.d(TAG, "onNothingSelected")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, selectedPosition: Int, id: Long) {
                sofList[holder.adapterPosition].idSofPriority = sofPriorityList[selectedPosition]
            }

        }

        holder.itemView.sp_priority?.setSelection(sofPriorityList.indexOf(sofItem.idSofPriority))
    }

    class SoFViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}