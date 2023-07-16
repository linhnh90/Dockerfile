package com.styl.pa.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.styl.pa.R
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.recommendatetions.RecommendationItem
import com.styl.pa.enums.SearchType
import com.styl.pa.interfaces.AddToCartEvent
import com.styl.pa.interfaces.OnClickRecyclerViewItem
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.home_class_item.view.*
import kotlinx.android.synthetic.main.home_class_item.view.btn_add_to_card
import kotlinx.android.synthetic.main.home_class_item.view.cv_container
import kotlinx.android.synthetic.main.home_class_item.view.txt_branch
import kotlinx.android.synthetic.main.home_class_item.view.txt_date
import kotlinx.android.synthetic.main.home_class_item.view.txt_price
import kotlinx.android.synthetic.main.home_class_item.view.txt_time
import kotlinx.android.synthetic.main.raw_product.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ngatran on 09/10/2018.
 */
class HomeClassAdapter : RecyclerView.Adapter<HomeClassAdapter.HomeClassVH> {
    private val sdfEventDateInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    private val sdfEventDateOutput = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

    private var infoList = ArrayList<RecommendationItem>()
    private var context: Context? = null

    private var onClickRecyclerViewItem: OnClickRecyclerViewItem? = null
    private var addToCart: AddToCartEvent.AddToCart? = null

    constructor(infoList: ArrayList<RecommendationItem>, context: Context?) {
        this.infoList = infoList
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeClassVH {
        return HomeClassVH(LayoutInflater.from(parent.context).inflate(R.layout.home_class_item, parent, false))
    }

    override fun getItemCount(): Int {
        return infoList.size
    }

    override fun onBindViewHolder(holder: HomeClassVH, position: Int) {
        val item = infoList.get(position)
        val itemInfo = item.infoItem
        if (itemInfo != null) {

            var imageURL: String? = ""
            var price = 0f
            var vacancies = "0"
            var rangeTime = ""
            if (SearchType.COURSES.toString().equals(item.typeItem)) {
                itemInfo as ClassInfo
                imageURL = itemInfo.getImageURL()

                if (itemInfo.getClassFees() != null && itemInfo.getClassFees()?.size!! > 0) {
                    for (i in 0..itemInfo.getClassFees()?.size!! - 1) {
                        val classFee = itemInfo.getClassFees()?.get(i)
                        if (classFee != null && !TextUtils.isEmpty(classFee.getClassFeeAmount())
                                && price < classFee.getClassFeeAmount()!!.toFloat()) {
                            price = classFee.getClassFeeAmount()!!.toFloat()
                        }
                    }
                }

                holder.itemView.txt_class_name.text = itemInfo.getDecodedTitle()

                if (!TextUtils.isEmpty(itemInfo.getVacancies()))
                    vacancies = itemInfo.getVacancies()!!

                holder.itemView.txt_branch.setText(itemInfo.getOutletName())

                holder.itemView.txt_date.text = (GeneralUtils.formatToDate(itemInfo.getStartDate()) + " ~ " + GeneralUtils.formatToDate(itemInfo.getEndDate()))

                var startTime = ""
                var endTime = ""
                var weekday = ""
                if (itemInfo.getClassSessions() != null && itemInfo.getClassSessions()!!.size > 0) {
                    if (!itemInfo.getClassSessions()?.get(0)?.getStartTime().isNullOrEmpty()) {
                        startTime = GeneralUtils.formatToTime(itemInfo.getClassSessions()?.get(0)?.getStartTime())
                        weekday = GeneralUtils.formatShortDay(itemInfo.getClassSessions()?.get(0)?.getStartTime())
                    }

                    if (!itemInfo.getClassSessions()?.get(0)?.getEndTime().isNullOrEmpty()) {
                        endTime = GeneralUtils.formatToTime(itemInfo.getClassSessions()?.get(0)?.getEndTime())
                    }
                }
                if (!startTime.isNullOrEmpty() || !endTime.isNullOrEmpty()) {
                    rangeTime = context!!.getString(R.string.time_range_format2, startTime, endTime, weekday)
                }


            } else {
                itemInfo as EventInfo
                imageURL = itemInfo.getImageUrl()

                if (itemInfo.eventFees != null && itemInfo.eventFees?.size!! > 0) {
                    for (i in 0..itemInfo.eventFees?.size!! - 1) {
                        val classFee = itemInfo.eventFees?.get(i)
                        if (classFee != null && !TextUtils.isEmpty(classFee.feeAmount)
                                && price < classFee.feeAmount!!.toFloat()) {
                            price = classFee.feeAmount!!.toFloat()
                        }
                    }
                }

                holder.itemView.txt_class_name.text = itemInfo.eventTitle

                if (itemInfo.vacancies != null)
                    vacancies = (itemInfo.vacancies ?: "").toString()

                holder.itemView.txt_branch.text = itemInfo.outletName

                holder.itemView.txt_date.text = GeneralUtils.eventFormatDate(itemInfo.dateFrom, itemInfo.dateTo, sdfEventDateInput, sdfEventDateOutput)

                val startTime = GeneralUtils.formatToTime(itemInfo.dateFrom)
                val endTime = GeneralUtils.formatToTime(itemInfo.dateTo)

                rangeTime = StringBuilder()
                        .append(startTime)
                        .append(" ~ ")
                        .append(endTime)
                        .toString()
            }

            val options = RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_pa_default)
                    .error(R.drawable.ic_pa_default)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)

            Glide.with(context!!)
                    .load(imageURL)
                    .apply(options)
                    .into(holder.itemView.img_logo)
            holder.itemView.txt_price.text = context?.getString(R.string.non_member_price,
                    GeneralUtils.formatAmountSymbols("$", price, 2))
//            holder.itemView.txt_vacancy.text = vacancies

            holder.itemView.txt_time.text = rangeTime

            holder.itemView.cv_container.setOnClickListener { view ->

                onClickRecyclerViewItem?.onClick(view, holder.adapterPosition)
            }

            holder.itemView?.btn_add_to_card?.setOnClickListener {
                addToCart?.addItem(holder.itemView, position)
            }

        }
    }

    fun setOnClickRecyclerViewItem(onClickRecyclerViewItem: OnClickRecyclerViewItem?) {
        this.onClickRecyclerViewItem = onClickRecyclerViewItem
    }

    fun setAddToCart(addToCart: AddToCartEvent.AddToCart) {
        this.addToCart = addToCart
    }

    inner class HomeClassVH(itemView: View) : RecyclerView.ViewHolder(itemView)
}