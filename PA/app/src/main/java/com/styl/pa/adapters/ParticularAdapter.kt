package com.styl.pa.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.styl.pa.R
import com.styl.pa.entities.cart.Attendee
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.interfaces.OnClickItem
import com.styl.pa.utils.GeneralTextUtil
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.item_particular.view.*

/**
 * Created by Ngatran on 09/26/2018.
 */
class ParticularAdapter : RecyclerView.Adapter<ParticularAdapter.ParticularVH> {
    private var cartItem: CartItem? = null
    private var context: Context? = null

    private var parentIndex = -1

    private var isEdit: Boolean = true
    private var deleteAttendeeItem: OnClickItem.OnClickMultiIndex? = null

    private var isBookingMyself: Boolean = true
    private var facilityParticipant: CustomerInfo? = null

    fun setIsBookingMyself(value: Boolean) {
        isBookingMyself = value
    }

    fun setFacilityParticipant(info: CustomerInfo?) {
        facilityParticipant = info
    }

    fun setIsEdit(isEdit: Boolean) {
        this.isEdit = isEdit
    }

    fun setDeleteAttendeeItem(deleteAttendeeItem: OnClickItem.OnClickMultiIndex?) {
        this.deleteAttendeeItem = deleteAttendeeItem
    }

    fun setParentIndex(parentIndex: Int) {
        this.parentIndex = parentIndex
    }

    constructor(cartItem: CartItem, context: Context?) {
        this.cartItem = cartItem
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticularVH {
        return ParticularVH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_particular, parent, false)
        )
    }

    override fun getItemCount(): Int {
        if (cartItem?.classInfo != null) {
            return cartItem?.attendees?.size ?: 0
        } else if (cartItem?.igInfo != null) {
            return cartItem?.attendees?.size ?: 0
        } else if (cartItem?.facility != null) {
            if (cartItem?.attendees != null && cartItem?.attendees!!.size > 0) {
                return 1
            }
        } else if (cartItem?.event != null) {
            if (cartItem?.event?.registerForMyself == false && cartItem?.event?.registerRequired == true) {
                return cartItem?.attendees?.size ?: 0
            } else if (cartItem?.event?.registerForMyself == false && cartItem?.event?.registerRequired == false) {
                return 1
            } else if (cartItem?.event?.registerForMyself == true && cartItem?.event?.registerRequired == true) {
                return cartItem?.attendees?.size ?: 0
            } else {
                return 1
            }
        }
        return 0
    }

    override fun onBindViewHolder(holder: ParticularVH, position: Int) {
        val attendee = cartItem?.attendees?.get(position)
        holder.bind(attendee)
    }

    @SuppressLint("SetTextI18n")
    inner class ParticularVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(attendee: Attendee?) {
            if (cartItem?.classInfo != null || cartItem?.igInfo != null) {
                initAttendeeView(attendee, adapterPosition)
            } else if (cartItem?.facility != null) {
                initFacilityView()
            } else if (cartItem?.event != null) {
                if (cartItem?.event?.registerForMyself == false && cartItem?.event?.registerRequired == true) {
                    initAttendeeView(attendee, adapterPosition)
                } else if (cartItem?.event?.registerForMyself == false && cartItem?.event?.registerRequired == false) {
                    initNoneAttendee()
                } else if (cartItem?.event?.registerForMyself == true && cartItem?.event?.registerRequired == true) {
                    initAttendeeView(attendee, adapterPosition)
                } else {
                    itemView.ll_attendee?.visibility = View.GONE
                    itemView.ll_none_attendee?.visibility = View.GONE
                    itemView.img_delete_attendee?.visibility = View.INVISIBLE
                }
            }

            showDiscount(attendee)
            showPrice(attendee)

            itemView.img_delete_attendee?.setOnClickListener {
                deleteAttendeeItem?.onClick(
                    itemView,
                    parentIndex,
                    adapterPosition
                )
            }

            if (adapterPosition == ((cartItem?.attendees?.size ?: 0) - 1)) {
                itemView.v_particular_line?.visibility = View.GONE
            } else {
                itemView.v_particular_line?.visibility = View.VISIBLE
            }
        }

        private fun showDiscount(attendee: Attendee?) {
            if ((attendee?.productInfo?.promoDiscountAmount ?: 0f) > 0f) {
                itemView.txt_promo_discount.text = "-" + GeneralUtils.formatAmountSymbols(
                    "", (attendee?.productInfo?.promoDiscountAmount
                        ?: 0.00f), 2
                )
                itemView.txt_promo_discount_lable.visibility = View.VISIBLE
                itemView.txt_promo_discount.visibility = View.VISIBLE
            } else {
                itemView.txt_promo_discount.visibility = View.GONE
                itemView.txt_promo_discount_lable.visibility = View.GONE
            }
            if ((attendee?.productInfo?.getDiscountAmount() ?: 0f) > 0f
                && ((attendee?.productInfo?.getBeforeDiscountAmount() ?: 0f) > 0f)
            ) {
                val percent =
                    ((attendee?.productInfo?.getDiscountAmount()!!) / (attendee.productInfo?.getBeforeDiscountAmount()
                        ?: 0f)) * 100
                itemView.txt_discount_percent?.text = "${context?.getString(R.string.pa_disc)} ${
                    GeneralUtils.formatAmountSymbols(
                        "",
                        percent,
                        1
                    )
                }%"
                itemView.txt_discount_percent?.visibility = View.VISIBLE
                itemView.txt_discount?.visibility = View.VISIBLE
                itemView.txt_discount?.text = "-" + GeneralUtils.formatAmountSymbols(
                    "", (attendee.productInfo?.getDiscountAmount()
                        ?: 0.00f), 2
                )
            } else {
                itemView.txt_discount_percent?.text = context?.getString(R.string.pa_disc) + "0%"
                itemView.txt_discount?.visibility = View.GONE
                itemView.txt_discount_percent?.visibility = View.GONE
            }
        }

        private fun showPrice(attendee: Attendee?) {
            if (attendee?.productInfo?.getBeforeDiscountAmount() == null) {
                itemView.txt_price?.text = ""
            } else {
                itemView.txt_price?.text = GeneralUtils.formatAmountSymbols(
                    "", (attendee.productInfo?.getBeforeDiscountAmount()
                        ?: 0.00f), 2
                )
            }
        }

        private fun initFacilityView() {
            itemView.ll_attendee?.visibility = View.VISIBLE
            itemView.ll_none_attendee?.visibility = View.GONE
            itemView.img_delete_attendee?.visibility = View.INVISIBLE

            itemView.txt_participant?.text = context?.getString(R.string.attendee_no, 1)
            if (isBookingMyself) {
                itemView.txt_participant?.text =
                    facilityParticipant?.mFullName + "\n" + GeneralTextUtil.maskText(
                        facilityParticipant?.mIdNo,
                        5,
                        true
                    )
            } else {
                itemView.txt_participant?.text =
                    facilityParticipant?.mFullName + "\n" + GeneralTextUtil.maskText(
                        facilityParticipant?.mMobile,
                        5,
                        true
                    )
            }
        }

        private fun initAttendeeView(attendee: Attendee?, adapterPosition: Int) {
            itemView.ll_attendee?.visibility = View.VISIBLE
            itemView.ll_none_attendee?.visibility = View.GONE
            itemView.txt_name_info?.text =
                (attendee?.customerInfo?.mFullName + "\n" + GeneralTextUtil.maskText(
                    attendee?.customerInfo?.mIdNo,
                    5,
                    true
                ))
            itemView.txt_participant?.text =
                context?.getString(R.string.attendee_no, adapterPosition + 1)
            itemView.img_delete_attendee?.visibility = View.INVISIBLE

        }

        private fun initNoneAttendee() {
            itemView.ll_attendee?.visibility = View.GONE
            itemView.ll_none_attendee?.visibility = View.VISIBLE
            itemView.img_delete_attendee?.visibility = View.INVISIBLE
            val noOfMember = cartItem?.noOfEvent ?: 0
            itemView.txt_no_attendee?.text = noOfMember.toString()
        }

    }
}