package com.styl.pa.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.styl.pa.R
import com.styl.pa.entities.promocode.PromoCode
import com.styl.pa.enums.PromoCodeType
import com.styl.pa.interfaces.OnItemSelectedListener
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.LogManager
import kotlinx.android.synthetic.main.fragment_checkout.view.*
import kotlinx.android.synthetic.main.layout_promo_code_item.view.*
import java.lang.StringBuilder

class PromoCodeAdapter(
    listPromoCode: ArrayList<PromoCode>,
    private var context: Context
): RecyclerView.Adapter<PromoCodeAdapter.PromoCodeViewHolder>() {

    interface OnClickListener {
        fun onReadTnCClicked()
    }

    private var selectedPosition: Int = -1

    var listener : OnItemSelectedListener? = null
    var onClickListener: OnClickListener? = null

    private var promoCodes: ArrayList<PromoCode> = listPromoCode

    @SuppressLint("NotifyDataSetChanged")
    inner class PromoCodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.txt_read_t_and_c -> {
                    onClickListener?.onReadTnCClicked()
                }
                R.id.rl_t_and_c, R.id.txt_term -> {
                    itemView.ll_promo_code_info.visibility = View.VISIBLE
                    itemView.rl_t_and_c.visibility = View.GONE
                }
                R.id.rl_promo_code_item -> {
                    itemView.rb_selected_promo_code?.isChecked = !(itemView.rb_selected_promo_code?.isChecked?: false)
                    if (itemView.rb_selected_promo_code?.isChecked == true) {
                        listener?.onItemSelected(adapterPosition)
                        selectedPosition = adapterPosition
                    }
                    notifyDataSetChanged()
                }
                R.id.txt_term_apply -> {
                    Log.e("AAAAAAAAAAAAA", "Term & Conditions apply")
                }
                else -> return
            }
        }

        fun setOnClickListener() {
            itemView.rl_promo_code_item.setOnClickListener(this)
            itemView.txt_read_t_and_c.setOnClickListener(this)
            itemView.rl_t_and_c.setOnClickListener(this)
            itemView.txt_term.setOnClickListener(this)
            itemView.txt_term_apply.setOnClickListener(this)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoCodeViewHolder {
        return PromoCodeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_promo_code_item,
            parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: PromoCodeViewHolder, position: Int) {
        val promoCode = promoCodes[position]
        holder.itemView.txt_code.text = promoCode.code
        holder.itemView.txt_promo_code_description.text = promoCode.description
        holder.itemView.txt_valid_till.text = context.resources.getString(
            R.string.valid_till,
            GeneralUtils.formatToDateTime(promoCode.endDate)
        )
        holder.itemView.rb_selected_promo_code?.isChecked =  selectedPosition == position
        setPromoCodeIcon(holder, promoCode)
        val termConditions = StringBuilder()
        if (!promoCode.termsAndConditions1.isNullOrEmpty()) {
            termConditions.append(Html.fromHtml(
                promoCode.termsAndConditions1, FROM_HTML_MODE_LEGACY)).append("\n")
        }
        if (!promoCode.termsAndConditions2.isNullOrEmpty()) {
            termConditions.append(Html.fromHtml(
                promoCode.termsAndConditions2, FROM_HTML_MODE_LEGACY)).append("\n")
        }
        holder.itemView.txt_term.movementMethod = ScrollingMovementMethod()
        holder.itemView.txt_term.text = termConditions.toString()
        if (holder.itemView.rb_selected_promo_code?.isChecked == true) {
            holder.itemView.rl_promo_code_info.setBackgroundResource(R.drawable.border_corner_light_orange)
        } else {
            holder.itemView.rl_promo_code_info.setBackgroundResource(R.drawable.border_corner_white_2)
        }
        holder.setOnClickListener()
    }

    private fun setPromoCodeIcon(holder: PromoCodeViewHolder, promoCode: PromoCode) {
        when (promoCode.type) {
            PromoCodeType.Facility.toString() -> {
                holder.itemView.img_promo_code_icon.setImageResource(R.drawable.ic_facility_promo_code)
                holder.itemView.txt_code.setTextColor(ContextCompat.getColor(context, R.color.blue_color_3))
            }
            PromoCodeType.Course.toString() -> {
                holder.itemView.img_promo_code_icon.setImageResource(R.drawable.ic_course_promo_code)
                holder.itemView.txt_code.setTextColor(ContextCompat.getColor(context, R.color.orange_color_1))
            }
            PromoCodeType.Event.toString() -> {
                holder.itemView.img_promo_code_icon.setImageResource(R.drawable.ic_event_promo_code)
                holder.itemView.txt_code.setTextColor(ContextCompat.getColor(context, R.color.purple_color_1))
            }
            PromoCodeType.Outlet.toString() -> {
                holder.itemView.img_promo_code_icon.setImageResource(R.drawable.ic_global_promo_code)
                holder.itemView.txt_code.setTextColor(ContextCompat.getColor(context, R.color.red_color_3))
            }
            PromoCodeType.Membership.toString() -> {
                holder.itemView.img_promo_code_icon.setImageResource(R.drawable.ic_passion_promo_code)
                holder.itemView.txt_code.setTextColor(ContextCompat.getColor(context, R.color.red_color_3))
            }
            PromoCodeType.InterestGroup.toString() -> {
                holder.itemView.img_promo_code_icon.setImageResource(R.drawable.ic_ig_promo_code)
                holder.itemView.txt_code.setTextColor(ContextCompat.getColor(context, R.color.green_color_3))
            }
            else -> {
                LogManager.i("Not match type")
            }
        }
    }

    override fun getItemCount(): Int {
        return promoCodes.size
    }

}