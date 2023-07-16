package com.styl.pa.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.styl.pa.R
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.interfaces.AddToCartEvent
import com.styl.pa.interfaces.OnClickRecyclerViewItem
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.search_class_item.view.*


class SearchByCourseAdapter : RecyclerView.Adapter<SearchByCourseAdapter.SearchByCourseVH> {
    private var classInfoList = ArrayList<ClassInfo>()
    private var context: Context? = null

    private var onClickRecyclerViewItem: OnClickRecyclerViewItem? = null
    private var addToCart: AddToCartEvent.AddToCart? = null

    constructor(classInfoList: ArrayList<ClassInfo>, context: Context?) {
        this.classInfoList = classInfoList
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchByCourseVH {
        return SearchByCourseVH(LayoutInflater.from(parent.context).inflate(R.layout.search_class_item, parent, false))
    }

    override fun getItemCount(): Int {
        return classInfoList.size
    }

    override fun onBindViewHolder(holder: SearchByCourseVH, position: Int) {
        val classInfo = classInfoList.get(position)

        val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_pa_default)
                .error(R.drawable.ic_pa_default)
        Glide.with(context!!)
                .load(classInfo.getImageURL())
                .apply(options)
                .into(holder.itemView.img_logo)
        holder.itemView.txt_class_name.text = classInfo.getDecodedTitle()

        var price = 0f
        if (classInfo.getClassFees() != null && classInfo.getClassFees()?.size!! > 0) {
            for (i in 0..classInfo.getClassFees()?.size!! - 1) {
                val classFee = classInfo.getClassFees()?.get(i)
                if (classFee != null && !TextUtils.isEmpty(classFee.getClassFeeAmount())
                        && price < classFee.getClassFeeAmount()!!.toFloat()) {
                    price = classFee.getClassFeeAmount()!!.toFloat()
                }
            }
        }
        holder.itemView.txt_price.text = context?.getString(R.string.non_member_price,
                GeneralUtils.formatAmountSymbols("$", price, 2))

        var vacancies = "0"
        if (!classInfo.getVacancies().isNullOrEmpty())
            vacancies = classInfo.getVacancies()!!
//        holder.itemView.txt_vacancy.setText(vacancies)

        holder.itemView.txt_date.setText(GeneralUtils.formatToDate(classInfo.getStartDate()) + " ~ " + GeneralUtils.formatToDate(classInfo.getEndDate()))

        var startTime = ""
        var endTime = ""
        var weekday = ""
        if (classInfo.getClassSessions() != null && classInfo.getClassSessions()!!.size > 0) {
            if (!classInfo.getClassSessions()?.get(0)?.getStartTime().isNullOrEmpty()) {
                startTime = GeneralUtils.formatToTime(classInfo.getClassSessions()?.get(0)?.getStartTime())
                weekday = GeneralUtils.formatShortDay(classInfo.getClassSessions()?.get(0)?.getStartTime())
            }

            if (!classInfo.getClassSessions()?.get(0)?.getEndTime().isNullOrEmpty()) {
                endTime = GeneralUtils.formatToTime(classInfo.getClassSessions()?.get(0)?.getEndTime())
            }
        }

        var rangeTime = ""
        if (!startTime.isNullOrEmpty() || !endTime.isNullOrEmpty()) {
            rangeTime = context!!.getString(R.string.time_range_format2, startTime, endTime, weekday)
        }

        holder.itemView.txt_time.setText(rangeTime)
        holder.itemView.txt_branch.setText(classInfo.getOutletName())

        holder.bind(onClickRecyclerViewItem)

        holder.itemView.btn_add_to_card?.setOnClickListener {
            addToCart?.addItem(holder.itemView, position)
        }
    }

    fun setOnClickRecyclerViewItem(onClickRecyclerViewItem: OnClickRecyclerViewItem?) {
        this.onClickRecyclerViewItem = onClickRecyclerViewItem
    }

    fun setAddToCart(addToCart: AddToCartEvent.AddToCart) {
        this.addToCart = addToCart
    }

    class SearchByCourseVH(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var onClickRecyclerViewItem: OnClickRecyclerViewItem? = null

        override fun onClick(v: View?) {
            if (onClickRecyclerViewItem != null) {
                onClickRecyclerViewItem!!.onClick(itemView, adapterPosition)
            }
        }

        fun bind(onClickRecyclerViewItem: OnClickRecyclerViewItem?) {
            this.onClickRecyclerViewItem = onClickRecyclerViewItem
            itemView.setOnClickListener(this)
        }
    }
}