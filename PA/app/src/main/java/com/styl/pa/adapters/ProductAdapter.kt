package com.styl.pa.adapters

import android.content.Context
import android.graphics.Paint
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.styl.pa.R
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.generateToken.Facility
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.entities.recommendatetions.RecommendationItem
import com.styl.pa.enums.SearchType
import com.styl.pa.interfaces.AddToCartEvent
import com.styl.pa.interfaces.OnClickRecyclerViewItem
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.raw_product.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


/**
 * Created by NguyenHang on 12/7/2020.
 */

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.HomeClassVH> {
    private val sdfEventDateInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    private val sdfEventDateOutput = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

    private var infoList = ArrayList<RecommendationItem>()
    private var context: Context? = null

    private var onClickRecyclerViewItem: OnClickRecyclerViewItem? = null
    private var addToCart: AddToCartEvent.AddToCart? = null

    constructor(infoList: ArrayList<RecommendationItem>, context: Context?) {
        this.infoList = infoList
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeClassVH {
        return HomeClassVH(
            LayoutInflater.from(parent.context).inflate(R.layout.raw_product, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return infoList.size
    }

    override fun onBindViewHolder(holder: HomeClassVH, position: Int) {
        val item = infoList[position]
        val itemInfo = item.infoItem
        var imageURL: String? = ""
        var price = 0f
        var rangeTime = ""
        var startTime = ""
        var endTime = ""
        var weekday = ""
        var startDate = ""
        var endDate = ""
        var feeCount = 0

        if (item.typeItem.equals(SearchType.COURSES.toString())) {
            itemInfo as ClassInfo
            imageURL = itemInfo.getImageURL()
            val priceRange = itemInfo.getPriceRange()
            price = priceRange[0]
            if (price == priceRange[1]) {
                feeCount = 0
            } else {
                feeCount = itemInfo.getClassFees()?.size ?: 0
            }
            holder.itemView.txt_product_title.text = itemInfo.getDecodedTitle()
            holder.itemView.ln_product_id.setBackgroundResource(R.drawable.bg_gradient_blue)
            holder.itemView.iv_product_id.setImageDrawable(context?.getDrawable(R.drawable.ic_course_id))
            holder.itemView.txt_product_id.text =
                context?.getString(R.string.course_id, itemInfo.getClassCode())
            holder.itemView.txt_branch.text = itemInfo.getOutletName()
            holder.itemView.iv_date.visibility = View.GONE
            holder.itemView.txt_date.visibility = View.GONE

            if (itemInfo.getClassSessions() != null && itemInfo.getClassSessions()!!.isNotEmpty()) {
                holder.itemView.iv_time.visibility = View.VISIBLE
                holder.itemView.txt_time.visibility = View.VISIBLE
                if (itemInfo.getClassSessions()?.get(0)?.getStartTime()?.isNullOrEmpty() == false) {
                    startTime = GeneralUtils.formatToTime(
                        itemInfo.getClassSessions()?.get(0)?.getStartTime()
                    )
                }
                if (!itemInfo.getClassSessions()?.get(0)?.getEndTime().isNullOrEmpty()) {
                    endTime =
                        GeneralUtils.formatToTime(itemInfo.getClassSessions()?.get(0)?.getEndTime())
                }
            } else {
                holder.itemView.iv_time.visibility = View.GONE
                holder.itemView.txt_time.visibility = View.GONE
            }

            weekday = GeneralUtils.formatShortDay(itemInfo.getStartDate())
            startDate = GeneralUtils.formatDateToNumber(itemInfo.getStartDate())
            endDate = GeneralUtils.formatDateToNumber(itemInfo.getEndDate())
        } else if (item.typeItem.equals(SearchType.FACILITIES.toString())) {
            itemInfo as Facility
            imageURL = itemInfo.getImageUrl()
            val imgWidth = context?.resources?.getDimension(R.dimen.dp_30) ?: 0f
            val layoutParams =
                LinearLayout.LayoutParams(imgWidth.roundToInt(), imgWidth.roundToInt())
            holder.itemView.iv_product_id.layoutParams = layoutParams
            holder.itemView.iv_product_id.setImageDrawable(context?.getDrawable(R.drawable.ic_facility_id))
            holder.itemView.ln_product_id.setBackgroundResource(R.drawable.bg_gradient_facility_linear)
            holder.itemView.iv_date.visibility = View.GONE
            holder.itemView.txt_date.visibility = View.GONE

            price = itemInfo.getPriceRange()[0]

            rangeTime = itemInfo.getOperatingHours() ?: ""
            holder.itemView.txt_product_id.text =
                context?.getString(R.string.facilities_id, itemInfo.getResourceID())
            val maxTextWidth = context?.resources?.getDimension(R.dimen.dp_175) ?: 0f
            holder.itemView.txt_product_id.maxWidth = maxTextWidth.roundToInt()
            holder.itemView.txt_product_title.text = itemInfo.getDecodedName()
            holder.itemView.txt_branch.text = itemInfo.outletName
        } else if (item.typeItem.equals(SearchType.EVENTS.toString())) {
            itemInfo as EventInfo
            holder.itemView.iv_product_id.setImageDrawable(context?.getDrawable(R.drawable.ic_event_id))
            holder.itemView.ln_product_id.setBackgroundResource(R.drawable.bg_gradient_event_linear)
            holder.itemView.iv_date.visibility = View.VISIBLE
            holder.itemView.txt_date.visibility = View.VISIBLE

            /*if (itemInfo.eventFees != null && itemInfo.eventFees?.size!! > 0) {
                for (i in 0 until itemInfo.eventFees?.size!!) {
                    val classFee = itemInfo.eventFees?.get(i)

                    if (classFee != null && !TextUtils.isEmpty(classFee.feeAmount)
                        && classFee.feeName?.contains("Member", true) == true
                    ) {
                        if (price > classFee.feeAmount!!.toFloat() || price == 0f) {
                            price = classFee.feeAmount!!.toFloat()
                        }
                        feeCount++
                    }
                }
                if (price == 0f) {
                    price = itemInfo.eventFees?.get(0)?.feeAmount?.toFloat() ?: 0f
                }
            }*/

            if (itemInfo.minPrice == itemInfo.maxPrice){
                price = itemInfo.minPrice ?: 0f
                feeCount = 1
            } else {
                price = itemInfo.minPrice ?: 0f
                feeCount = 2
            }

            holder.itemView.txt_product_id.text =
                context?.getString(R.string.events_id, itemInfo.eventCode)
            holder.itemView.txt_product_title.text = itemInfo.eventTitle
            holder.itemView.txt_branch.text = itemInfo.outletName
            holder.itemView.txt_date.text = GeneralUtils.eventFormatDate(
                itemInfo.dateFrom,
                itemInfo.dateTo,
                sdfEventDateInput,
                sdfEventDateOutput
            )


            if (itemInfo.dateFrom?.isEmpty() == false) {
                startTime = GeneralUtils.formatToTime(itemInfo.dateFrom)
                weekday = GeneralUtils.formatShortDay(itemInfo.dateFrom)
            }

            if (itemInfo.dateTo?.isEmpty() == false) {
                endTime = GeneralUtils.formatToTime(itemInfo.dateTo)
            }

            startDate = GeneralUtils.formatDateToNumber(itemInfo.dateFrom)
            endDate = GeneralUtils.formatDateToNumber(itemInfo.dateTo)
        } else if (item.typeItem.equals(SearchType.INTEREST_GROUPS.toString())) {
            itemInfo as InterestGroup
            imageURL = itemInfo.imageURL
            val priceRange = itemInfo.getPriceRange()
            price = priceRange[0]
            if (price == priceRange[1]) {
                feeCount = 0
            } else {
                feeCount = itemInfo.igFees?.size ?: 0
            }
            holder.itemView.txt_product_title.text = itemInfo.igTitle
            holder.itemView.ln_product_id.setBackgroundResource(R.drawable.bg_gradient_interestgroup_linear)
            holder.itemView.iv_product_id.setImageDrawable(context?.getDrawable(R.drawable.ic_course_id))
            holder.itemView.txt_product_id.text = context?.getString(R.string.ig_id, itemInfo.igCode)
            holder.itemView.tv_btn_book_now.text = context?.getString(R.string.register_now)
            holder.itemView.txt_branch.text = itemInfo.outletName

            if (itemInfo.igSessions != null && itemInfo.igSessions!!.isNotEmpty()) {
                holder.itemView.iv_time.visibility = View.VISIBLE
                holder.itemView.txt_time.visibility = View.VISIBLE
                if (!itemInfo.igSessions?.get(0)?.getStartTime().isNullOrEmpty()) {
                    startTime = GeneralUtils.formatToTime(
                        itemInfo.igSessions?.get(0)?.getStartTime()
                    )
                }
                if (!itemInfo.igSessions?.get(0)?.getEndTime().isNullOrEmpty()) {
                    endTime =
                        GeneralUtils.formatToTime(itemInfo.igSessions?.get(0)?.getEndTime())
                }
            } else {
                holder.itemView.iv_time.visibility = View.GONE
                holder.itemView.txt_time.visibility = View.GONE
            }
            weekday = GeneralUtils.formatShortDay(itemInfo.startDate)
            startDate = GeneralUtils.formatDateToNumber(itemInfo.startDate)
            endDate = GeneralUtils.formatDateToNumber(itemInfo.endDate)
            /*holder.itemView.iv_date.visibility = View.GONE
            holder.itemView.txt_date.visibility = View.GONE
            holder.itemView.iv_time.visibility = View.GONE
            holder.itemView.txt_time.visibility = View.GONE*/
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
            .into(holder.itemView.img_product)

        if (price == 0f) {
            holder.itemView.txt_price.text = context?.getText(R.string.free)
        } else {
            var formatPrice = GeneralUtils.formatAmountSymbols("$", price, 2)
            if (formatPrice.contains(".00") || (formatPrice.length > 5 && feeCount > 1)) {
                formatPrice = "$" + price.toInt().toString()
            }
            val priceText = SpannableString(formatPrice)
            priceText.setSpan(
                AbsoluteSizeSpan(
                    context?.resources?.getDimensionPixelSize(R.dimen.text_size_price)!!
                ),
                0,
                priceText.length,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            var priceTextFormat: CharSequence = ""
            if (feeCount > 1) {
                val fromText = SpannableString(context?.getString(R.string.fee_from))
                fromText.setSpan(
                    AbsoluteSizeSpan(
                        context?.resources?.getDimensionPixelSize(R.dimen.text_size_small)!!
                    ),
                    0,
                    fromText.length,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE
                )
                priceTextFormat = TextUtils.concat(fromText, " ", priceText)
            } else {
                priceTextFormat = TextUtils.concat(priceText)
            }
            holder.itemView.txt_price.text = priceTextFormat
        }

        if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
            holder.itemView.txt_date.visibility = View.VISIBLE
            holder.itemView.iv_date.visibility = View.VISIBLE
            if (startDate == endDate) {
                holder.itemView.txt_date.text = ("$startDate ($weekday)")
            } else {
                holder.itemView.txt_date.text = ("$startDate - $endDate ($weekday)")
            }
        } else {
            holder.itemView.txt_date.visibility = View.GONE
            holder.itemView.iv_date.visibility = View.GONE
        }

        if (rangeTime.isNotEmpty()) {
            holder.itemView.iv_time.visibility = View.VISIBLE
            holder.itemView.txt_time.visibility = View.VISIBLE
            holder.itemView.txt_time.text = rangeTime
        } else if ((!startTime.isEmpty() && !endTime.isEmpty())) {
            rangeTime = StringBuilder()
                .append(startTime)
                .append(" - ")
                .append(endTime)
                .toString()
            holder.itemView.txt_time.visibility = View.VISIBLE
            holder.itemView.iv_time.visibility = View.VISIBLE
            holder.itemView.txt_time.text = rangeTime
        } else {
            holder.itemView.txt_time.visibility = View.INVISIBLE
            holder.itemView.iv_time.visibility = View.INVISIBLE
        }

        holder.itemView.txt_add_to_cart_lable.paintFlags =
            holder.itemView.txt_add_to_cart_lable.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        if (SearchType.FACILITIES.toString().equals(item.typeItem)) {
            holder.itemView.btn_add_to_card?.visibility = View.INVISIBLE
            holder.itemView.btn_book_now?.visibility = View.INVISIBLE
        } else {
            holder.itemView.btn_add_to_card?.visibility = View.VISIBLE
            holder.itemView.btn_book_now?.visibility = View.VISIBLE
        }

        holder.itemView.cv_container.setOnClickListener { view ->
            onClickRecyclerViewItem?.onClick(view, holder.adapterPosition)
        }

        holder.itemView.btn_add_to_card?.setOnClickListener {
            addToCart?.addItem(holder.itemView, position)
        }

        holder.itemView.btn_book_now?.setOnClickListener {
            addToCart?.addItem(holder.itemView, position, true)
        }

        if (context is MainActivity) {
            val canOrder = (context as MainActivity).canOrder
            holder.itemView.btn_add_to_card?.isEnabled = canOrder
            holder.itemView.img_add_to_cart?.isEnabled = canOrder
            holder.itemView.btn_book_now?.isEnabled = canOrder
            if (canOrder) {
                holder.itemView.txt_add_to_cart_lable?.setTextColor(
                    ContextCompat.getColor(context as MainActivity, R.color.red_color_3)
                )
            } else {
                holder.itemView.txt_add_to_cart_lable?.setTextColor(
                    ContextCompat.getColor(context as MainActivity, R.color.gray_color_1)
                )
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