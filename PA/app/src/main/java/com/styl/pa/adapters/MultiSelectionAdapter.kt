package com.styl.pa.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.styl.pa.R
import com.styl.pa.entities.advancedSearch.MultiSelectionObject
import com.styl.pa.entities.courseCategory.CourseCategory
import com.styl.pa.entities.generateToken.Outlet
import com.styl.pa.interfaces.OnMultiSelectionSpinnerItem
import com.styl.pa.modules.home.ISelectedLocation
import com.styl.pa.utils.PreferredDayUtils
import kotlinx.android.synthetic.main.multi_selection_popup_item.view.*


/**
 * Created by Ngatran on 09/19/2018.
 */
class MultiSelectionAdapter : RecyclerView.Adapter<MultiSelectionAdapter.MultiSelectionVH> {

    companion object {
        val CATEGORY_TYPE = "CATEGORY_TYPE"
        val OUTLET_TYPE = "OUTLET_TYPE"
        val STRING_TYPE = "STRING_TYPE"
    }

    private var getContext: Context? = null
    private var itemList: ArrayList<MultiSelectionObject> = ArrayList()
    var itemSelectionList: ArrayList<String> = ArrayList()
    var itemSelectionIndexList: ArrayList<Int> = ArrayList()


    private var stringList: ArrayList<MultiSelectionObject> = ArrayList()
    private var categoryList: ArrayList<CourseCategory> = ArrayList()
    private var outletList: ArrayList<Outlet> = ArrayList()
    private var type = ""

    private var onSelectionStringItem: OnMultiSelectionSpinnerItem.OnMultiSelectionStringSpinnerItem? = null
    private var onSelectionOutletItem: OnMultiSelectionSpinnerItem.OnMultiSelectionOutletSpinnerItem? = null
    private var onSelectionCategoryItem: OnMultiSelectionSpinnerItem.OnMultiSelectionCategorySpinnerItem? = null

    constructor(context: Context?, type: String) {
        getContext = context
        this.type = type
    }

    var isLocation: Boolean = false
    var iSelectLocationListener: ISelectedLocation? = null

    fun setSelectLocationListener(listener: ISelectedLocation) {
        this.iSelectLocationListener = listener
    }

    fun setStringList(stringList: ArrayList<MultiSelectionObject>) {
        this.stringList = stringList
    }

    fun setOutletList(outletList: ArrayList<Outlet>) {
        this.outletList = outletList
    }

    fun setCategoryList(categoryList: ArrayList<CourseCategory>) {
        this.categoryList = categoryList
    }

    fun setOnMultiSelectionStringSpinnerItem(onSelectionItem: OnMultiSelectionSpinnerItem.OnMultiSelectionStringSpinnerItem?) {
        this.onSelectionStringItem = onSelectionItem
    }

    fun setOnMultiSelectionOutletSpinnerItem(onSelectionItem: OnMultiSelectionSpinnerItem.OnMultiSelectionOutletSpinnerItem?) {
        this.onSelectionOutletItem = onSelectionItem
    }

    fun setOnMultiSelectionSpinnerCategoryItem(onSelectionItem: OnMultiSelectionSpinnerItem.OnMultiSelectionCategorySpinnerItem?) {
        this.onSelectionCategoryItem = onSelectionItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiSelectionVH {
        val item = MultiSelectionVH(LayoutInflater.from(parent.context).inflate(R.layout.multi_selection_popup_item, parent, false))
        holderList.add(item)
        return item
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.multi_selection_popup_item
    }

    override fun getItemCount(): Int {
        when (type) {
            STRING_TYPE -> return stringList.size

            OUTLET_TYPE -> return outletList.size

            CATEGORY_TYPE -> return categoryList.size
        }
        return itemList.size
    }

    private var holderList = ArrayList<MultiSelectionVH>()

    override fun onBindViewHolder(holder: MultiSelectionVH, position: Int) {
        holder.bind(itemSelectionIndexList, itemSelectionList, type, isLocation, iSelectLocationListener)
        var value = ""
        var isCheck = false
        when (type) {
            STRING_TYPE -> {
                value = stringList.get(position).Value
                isCheck = stringList.get(position).IsCheck

                holder.setStringList(stringList)
                holder.setOnMultiSelectionStringSpinnerItem(onSelectionStringItem)
            }

            OUTLET_TYPE -> {
                value = if (!outletList.get(position).getFriendlyName().isNullOrEmpty()) outletList.get(position).getFriendlyName()!! else ""
                isCheck = outletList.get(position).IsCheck

                holder.setOutletList(outletList)
                holder.setOnMultiSelectionOutletSpinnerItem(onSelectionOutletItem)
            }

            CATEGORY_TYPE -> {
                value = if (!categoryList.get(position).getCategoryName().isNullOrEmpty()) categoryList.get(position).getCategoryName()!! else ""
                isCheck = categoryList.get(position).IsCheck

                holder.setCategoryList(categoryList)
                holder.setOnMultiSelectionSpinnerCategoryItem(onSelectionCategoryItem)
            }
        }
        holder.itemView.txt_value.setText(value)
        if (isCheck) {
            holder.itemView.txt_value.isChecked = true
        } else {
            holder.itemView.txt_value.isChecked = false
        }

    }

    class MultiSelectionVH(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var itemSelectionList: ArrayList<String> = ArrayList()

        private var itemIndexList: ArrayList<Int> = ArrayList()

        private var stringList: ArrayList<MultiSelectionObject> = ArrayList()
        private var categoryList: ArrayList<CourseCategory> = ArrayList()
        private var outletList: ArrayList<Outlet> = ArrayList()
        private var type: String? = null

        private var onSelectionStringItem: OnMultiSelectionSpinnerItem.OnMultiSelectionStringSpinnerItem? = null
        private var onSelectionOutletItem: OnMultiSelectionSpinnerItem.OnMultiSelectionOutletSpinnerItem? = null
        private var onSelectionCategoryItem: OnMultiSelectionSpinnerItem.OnMultiSelectionCategorySpinnerItem? = null

        private var isLocation: Boolean = false
        private var iSelectLocationListener: ISelectedLocation? = null

        override fun onClick(v: View?) {

            var isCheck = false
            var value = ""

            when (type) {
                STRING_TYPE -> {
                    value = stringList.get(adapterPosition).Value
                    isCheck = stringList.get(adapterPosition).IsCheck
                }

                OUTLET_TYPE -> {
                    value = if (!outletList.get(adapterPosition).getFriendlyName().isNullOrEmpty()) outletList.get(adapterPosition).getFriendlyName()!! else ""
                    isCheck = outletList.get(adapterPosition).IsCheck
                }

                CATEGORY_TYPE -> {
                    value = if (!categoryList.get(adapterPosition).getCategoryName().isNullOrEmpty()) categoryList.get(adapterPosition).getCategoryName()!! else ""
                    isCheck = categoryList.get(adapterPosition).IsCheck
                }
            }

            if (isCheck) {
                itemView.txt_value.isChecked = false

                if (itemSelectionList.indexOf(value) > -1) {
                    itemIndexList.removeAt(itemSelectionList.indexOf(value))
                    itemSelectionList.removeAt(itemSelectionList.indexOf(value))
                }

                when (type) {
                    STRING_TYPE -> {
                        stringList.get(adapterPosition).IsCheck = false
                    }

                    OUTLET_TYPE -> {
                        outletList.get(adapterPosition).IsCheck = false
                    }

                    CATEGORY_TYPE -> {
                        categoryList.get(adapterPosition).IsCheck = false
                    }
                }
            } else {

                if (itemSelectionList.size > 3 && isLocation) {
                    iSelectLocationListener?.onWarningSelectLocation()
                } else {
                    itemView.txt_value.isChecked = true
                    itemSelectionList.add(value)
                    var value = adapterPosition

                    when (type) {
                        STRING_TYPE -> {
                            stringList.get(adapterPosition).IsCheck = true
                            value = PreferredDayUtils.preferredDays.get(stringList.get(adapterPosition).Value)
                                    ?: (adapterPosition + 1)
                        }

                        OUTLET_TYPE -> {
                            outletList.get(adapterPosition).IsCheck = true
                        }

                        CATEGORY_TYPE -> {
                            categoryList.get(adapterPosition).IsCheck = true
                        }
                    }

                    itemIndexList.add(value)
                }
            }

            when (type) {
                STRING_TYPE -> {
                    onSelectionStringItem?.onClickMulti(stringList, getString(), itemIndexList)
                }

                OUTLET_TYPE -> {
                    onSelectionOutletItem?.onClickMulti(outletList, getString(), itemIndexList)
                }

                CATEGORY_TYPE -> {
                    onSelectionCategoryItem?.onClickMulti(categoryList, getString(), itemIndexList)
                }
            }
        }

        private fun getString(): String {
            var text = ""
            for (i in 0..itemSelectionList.size - 1) {
                text += itemSelectionList.get(i) + ", "
            }

            if (text.length > 2) {
                return text.substring(0, text.length - 2)
            } else {
                return text
            }
        }

        fun bind(array: ArrayList<Int>, selectionList: ArrayList<String>, type: String, isLocation: Boolean,
                 iSelectLocationListener: ISelectedLocation?) {
            this.itemSelectionList = selectionList
            this.itemIndexList = array
            this.type = type
            this.isLocation = isLocation
            this.iSelectLocationListener = iSelectLocationListener
            itemView.setOnClickListener(this)
        }

        fun setStringList(stringList: ArrayList<MultiSelectionObject>) {
            this.stringList = stringList
        }

        fun setOutletList(outletList: ArrayList<Outlet>) {
            this.outletList = outletList
        }

        fun setCategoryList(categoryList: ArrayList<CourseCategory>) {
            this.categoryList = categoryList
        }

        fun setOnMultiSelectionStringSpinnerItem(onSelectionItem: OnMultiSelectionSpinnerItem.OnMultiSelectionStringSpinnerItem?) {
            this.onSelectionStringItem = onSelectionItem
        }

        fun setOnMultiSelectionOutletSpinnerItem(onSelectionItem: OnMultiSelectionSpinnerItem.OnMultiSelectionOutletSpinnerItem?) {
            this.onSelectionOutletItem = onSelectionItem
        }

        fun setOnMultiSelectionSpinnerCategoryItem(onSelectionItem: OnMultiSelectionSpinnerItem.OnMultiSelectionCategorySpinnerItem?) {
            this.onSelectionCategoryItem = onSelectionItem
        }
    }
}