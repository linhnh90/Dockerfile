package com.styl.pa.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.styl.pa.R
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.enums.SearchType
import com.styl.pa.interfaces.AddToCartEvent
import com.styl.pa.interfaces.OnClickRecyclerViewItem
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.search_class_item.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ngatran on 03/11/2019.
 */
class SearchAdapter<T> : RecyclerView.Adapter<SearchAdapter<T>.SearchVH> {
    private val sdfEventDateInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    private val sdfEventDateOutput = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    private var itemList: ArrayList<T> = ArrayList()
    private var context: Context? = null
    private var type: SearchType? = null

    private var onClickRecyclerViewItem: OnClickRecyclerViewItem? = null
    private var addToCart: AddToCartEvent.AddToCart? = null

    constructor(itemList: ArrayList<T>, context: Context?, type: SearchType?) {
        this.itemList = itemList
        this.context = context
        this.type = type
    }

    fun setOnClickRecyclerViewItem(onClickRecyclerViewItem: OnClickRecyclerViewItem?) {
        this.onClickRecyclerViewItem = onClickRecyclerViewItem
    }

    fun setAddToCart(addToCart: AddToCartEvent.AddToCart) {
        this.addToCart = addToCart
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchVH {
        return SearchVH(LayoutInflater.from(parent.context).inflate(R.layout.search_class_item, parent, false))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: SearchVH, position: Int) {
        var imageURL: String? = ""
        var nameItem: String? = ""
        var price = 0F
        var outletName: String? = ""
        var rangDate = ""
        var vacancies = ""
        var rangeTime = ""
        if (type.toString().equals(SearchType.EVENTS.toString())) {
            val item = itemList[position] as EventInfo

            imageURL = item.getImageUrl()
            nameItem = item.getDecodedTitle().toString()

            if (item.eventFees != null && item.eventFees?.size!! > 0) {
                for (i in 0..item.eventFees!!.size - 1) {
                    val fee = item.eventFees!![i]
                    if (!fee.feeAmount.isNullOrEmpty() && price < fee.feeAmount!!.toFloat()) {
                        price = fee.feeAmount!!.toFloat()
                    }
                }
            }

            outletName = item.outletName

            rangDate = GeneralUtils.eventFormatDate(item.dateFrom, item.dateTo, sdfEventDateInput, sdfEventDateOutput)

            vacancies = (item.vacancies ?: "").toString()

            val startTime = GeneralUtils.formatToTime(item.dateFrom)
            val endTime = GeneralUtils.formatToTime(item.dateTo)

            rangeTime = StringBuilder()
                    .append(startTime)
                    .append(" ~ ")
                    .append(endTime)
                    .toString()
        }

        if (context != null) {
            val options = RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_pa_default)
                    .error(R.drawable.ic_pa_default)

            Glide.with(context!!)
                    .load(imageURL)
                    .apply(options)
                    .into(holder.itemView.img_logo)
        }
        holder.itemView.txt_class_name.text = nameItem
        holder.itemView.txt_price?.text = context?.getString(R.string.non_member_price,
                GeneralUtils.formatAmountSymbols("$", price, 2))
        holder.itemView.txt_price?.visibility = View.VISIBLE

        holder.itemView.txt_time.text = rangeTime
        holder.itemView.txt_date.text = rangDate
        holder.itemView.txt_branch.text = outletName

        holder.bind(onClickRecyclerViewItem)

        holder.itemView.btn_add_to_card?.visibility = View.VISIBLE
        holder.itemView.btn_add_to_card?.setOnClickListener {
            addToCart?.addItem(holder.itemView, position)
        }

    }

    inner class SearchVH(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
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