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
import com.styl.pa.entities.generateToken.Facility
import com.styl.pa.interfaces.AddToCartEvent
import com.styl.pa.interfaces.OnClickRecyclerViewItem
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.search_class_item.view.*

class SearchByFacilityAdapter : RecyclerView.Adapter<SearchByFacilityAdapter.SearchByFacilityVH> {
    private var facilityList = ArrayList<Facility>()
    private var context: Context? = null
    private var outletName: String? = ""

    private var onClickRecyclerViewItem: OnClickRecyclerViewItem? = null
    private var addToCart: AddToCartEvent.AddToCart? = null

    constructor(facilityList: ArrayList<Facility>, context: Context?, outletName: String?) {
        this.facilityList = facilityList
        this.context = context
        this.outletName = outletName
    }

    fun updateAdapter(facilityList: ArrayList<Facility>, outletName: String?) {
        this.facilityList.addAll(facilityList)
        this.outletName = outletName
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchByFacilityVH {
        return SearchByFacilityVH(LayoutInflater.from(parent.context).inflate(R.layout.search_class_item, parent, false))
    }

    override fun getItemCount(): Int {
        return facilityList.size
    }

    override fun onBindViewHolder(holder: SearchByFacilityVH, position: Int) {
        val facility = facilityList.get(position)

        val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_pa_default)
                .error(R.drawable.ic_pa_default)

        Glide.with(context!!)
                .load(facility.getImageUrl())
                .apply(options)
                .into(holder.itemView.img_logo)
        if (facility.getIsBookable()) {
            holder.itemView.txt_class_name.text = facility.getResourceSubTypeName()
        } else {
            holder.itemView.txt_class_name.setText(facility.getResourcetName())
        }

        var price = 0F
        if (facility.getResourceFeeList() != null && facility.getResourceFeeList()?.size!! > 0) {
            for (i in 0..facility.getResourceFeeList()?.size!! - 1) {
                val classFee = facility.getResourceFeeList()?.get(0)
                if (classFee != null) {
                    if (!TextUtils.isEmpty(classFee.getFeeNormalAmount())
                            && price < classFee.getFeeNormalAmount()!!.toFloat()) {
                        price = classFee.getFeeNormalAmount()!!.toFloat()
                    }

                    if (!TextUtils.isEmpty(classFee.getFeePeakAmount())
                            && price < classFee.getFeePeakAmount()!!.toFloat()) {
                        price = classFee.getFeePeakAmount()!!.toFloat()
                    }
                }
            }
        }

        holder.itemView.txt_price?.text = context?.getString(R.string.non_member_price,
                GeneralUtils.formatAmountSymbols("$", price, 2))
        holder.itemView.txt_price?.visibility = View.VISIBLE

        holder.itemView.txt_date.visibility = View.GONE
        holder.itemView.txt_time.text = facility.getOperatingHours()
        holder.itemView.txt_branch.text = facility.outletName

        holder.bind(onClickRecyclerViewItem)
//        holder.itemView.ll_vacancy.visibility = View.GONE

        holder.itemView.btn_add_to_card?.visibility = View.GONE
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

    class SearchByFacilityVH(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
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