package com.styl.pa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.styl.pa.R
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.interfaces.OnClickItem
import com.styl.pa.interfaces.OnDeleteRecyclerViewItem
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.item_cart.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Ngatran on 03/14/2019.
 */
class CartSummaryAdapter : RecyclerView.Adapter<CartSummaryAdapter.CartSummaryVH> {
    private val sdfEventDateInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    private val sdfEventDateOutput = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    private val sdfEventTimeOutput = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

    private var cartSummaryList: ArrayList<CartItem> = ArrayList()
    private var context: Context? = null

    private var isEdit: Boolean = true
    private var deleteCartItem: OnDeleteRecyclerViewItem? = null
    private var deleteAttendeeItem: OnClickItem.OnClickMultiIndex? = null

    private var isBookingMyself: Boolean = true
    private var facilityParticipant: CustomerInfo? = null

    fun setIsBookingMyself(value: Boolean){
        isBookingMyself = value
    }
    fun setFacilityParticipant(info: CustomerInfo?){
        facilityParticipant = info
    }

    fun setIsEdit(isEdit: Boolean) {
        this.isEdit = isEdit
    }

    fun setDeleteCartItem(deleteCartItem: OnDeleteRecyclerViewItem?) {
        this.deleteCartItem = deleteCartItem
    }

    fun setDeleteAttendeeItem(deleteAttendeeItem: OnClickItem.OnClickMultiIndex?) {
        this.deleteAttendeeItem = deleteAttendeeItem
    }

    constructor(cartSummaryList: ArrayList<CartItem>, context: Context?) {
        this.cartSummaryList = cartSummaryList
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartSummaryVH {
        return CartSummaryVH(LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false))
    }

    override fun getItemCount(): Int {
        return cartSummaryList.size
    }

    override fun onBindViewHolder(holder: CartSummaryVH, position: Int) {
        holder.bin(cartSummaryList[position])
    }

    inner class CartSummaryVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bin(cartSummaryItem: CartItem) {
            val adapter = ParticularAdapter(cartSummaryItem, context) //use for product is not event
            adapter.setIsEdit(isEdit)
            adapter.setParentIndex(adapterPosition)
            adapter.setDeleteAttendeeItem(deleteAttendeeItem)
            adapter.setIsBookingMyself(isBookingMyself)
            adapter.setFacilityParticipant(facilityParticipant)
            itemView.rcv_particulars_list?.layoutManager = LinearLayoutManager(context)
            itemView.rcv_particulars_list?.setHasFixedSize(true)
            itemView.rcv_particulars_list?.isNestedScrollingEnabled = false
            itemView.rcv_particulars_list?.adapter = adapter

            if (isEdit) {
                itemView.img_delete_cart_item?.visibility = View.VISIBLE
            } else {
                itemView.img_delete_cart_item?.visibility = View.INVISIBLE
            }

            var itemCartName: String? = null
            var itemCartInfo: String? = null
            var outletString = ""
            var timeString = ""
            var dateString = ""
            if (cartSummaryItem.classInfo != null) {
                itemCartName = cartSummaryItem.classInfo?.getDecodedTitle().toString() + " (" + cartSummaryItem.classInfo?.getClassCode() + ")"
                val date = (GeneralUtils.eventFormatDate(cartSummaryItem.classInfo?.getStartDate(), cartSummaryItem.classInfo?.getEndDate(), sdfEventDateInput, sdfEventDateOutput))
                val outlet = cartSummaryItem.classInfo?.getOutletName()
                if (cartSummaryItem.classInfo?.getClassSessions() != null && cartSummaryItem.classInfo?.getClassSessions()!!.size > 0) {
                    val time = (GeneralUtils.eventFormatDate(cartSummaryItem.classInfo?.getClassSessions()?.get(0)?.getStartTime(),
                            cartSummaryItem.classInfo?.getClassSessions()?.get(0)?.getEndTime(), sdfEventDateInput, sdfEventTimeOutput))
                    val weekday = GeneralUtils.formatShortDay(cartSummaryItem.classInfo?.getClassSessions()?.get(0)?.getStartTime())
                    itemCartInfo = date + "\n" + time + " (" + weekday + ")" + "\n" + outlet
                } else {
                    itemCartInfo = date + "\n" + outlet
                }
            } else if (cartSummaryItem.igInfo != null) {
                itemCartName = cartSummaryItem.igInfo?.getDecodedTitle().toString() + " (" + cartSummaryItem.igInfo?.igCode + ")"

                itemView.txt_item_cart_info.visibility = View.GONE
                itemView.ll_desc.visibility = View.VISIBLE
                itemView.tv_time.visibility = View.GONE
                itemView.tv_date.visibility = View.GONE

                outletString = cartSummaryItem.igInfo?.outletName ?: ""
            } else if (cartSummaryItem.facility != null) {
                itemView.txt_item_cart_info.visibility = View.GONE
                itemView.ll_desc.visibility = View.VISIBLE

                itemCartName = cartSummaryItem.facility?.getResourcetName()

                val date = StringBuilder()
                val startDate = GeneralUtils.formatToDate(cartSummaryItem.selectedDate)
                val endDate = GeneralUtils.formatToDate(cartSummaryItem.selectedDate)
                date.append(startDate)
                        .append(" - ")
                        .append(endDate)
                dateString = date.toString()

                val time = StringBuilder()
                cartSummaryItem.slotList?.forEach { slotList ->
                    time.append(slotList.mTimeRageName)
                }
                timeString = time.toString()

                outletString = cartSummaryItem.facility?.outletName ?: ""

            } else if (cartSummaryItem.event != null) {
                itemView.txt_item_cart_info.visibility = View.GONE
                itemView.ll_desc.visibility = View.VISIBLE

                itemView.rcv_particulars_list?.adapter = ContentParticularEventAdapter(
                    context = context,
                    listData = cartSummaryItem.event?.listSelectedTicket ?: ArrayList()
                )

                itemCartName = cartSummaryItem.event?.getDecodedTitle().toString() + " (" + cartSummaryItem.event?.eventCode + ")"

                dateString = (GeneralUtils.eventFormatDate(cartSummaryItem.event?.dateFrom, cartSummaryItem.event?.dateTo, sdfEventDateInput, sdfEventDateOutput))
                timeString = (GeneralUtils.eventFormatDate(cartSummaryItem.event?.dateFrom, cartSummaryItem.event?.dateTo, sdfEventDateInput, sdfEventTimeOutput))
                outletString = cartSummaryItem.event?.outletName ?: ""
            }
            itemView.txt_item_cart_name?.text = itemCartName
            itemView.txt_item_cart_info?.text = itemCartInfo
            itemView.txt_stt?.text = (adapterPosition + 1).toString()

            itemView.tv_outlet.text = outletString
            itemView.tv_date.text = dateString
            itemView.tv_time.text = timeString

            if (adapterPosition == (cartSummaryList.size - 1)) {
                itemView.v_separate?.visibility = View.INVISIBLE
            } else {
                itemView.v_separate?.visibility = View.VISIBLE
            }

            itemView.img_delete_cart_item?.setOnClickListener {
                deleteCartItem?.onClick(itemView, adapterPosition)
            }
        }
    }
}