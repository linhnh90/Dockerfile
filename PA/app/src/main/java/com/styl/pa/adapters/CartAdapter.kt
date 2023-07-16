package com.styl.pa.adapters

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.styl.pa.R
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.modules.base.OnRecyclerViewListener
import com.styl.pa.modules.cart.ICartContact
import com.styl.pa.modules.cart.presenter.CartPresenter
import com.styl.pa.modules.cart.view.AttendeeAdapter
import kotlinx.android.synthetic.main.cart_item.view.*


/**
 * Created by Ngatran on 09/24/2018.
 */
class CartAdapter(var context: Context, var presenter: CartPresenter?) :
    RecyclerView.Adapter<CartAdapter.CartVH>(), ICartContact.ICartAdapterView {

    var onCartListener: OnCartListener? = null
    var onAttendeeListener: AttendeeAdapter.OnAttendeeListener? = null
    var selectedPos = -1

    init {
        presenter?.cartAdapterView = this
    }

    private lateinit var holder: CartVH

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartVH {
        return CartVH(
            LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return presenter?.getCartItemCount() ?: 0
    }

    override fun onBindViewHolder(holder: CartVH, position: Int) {
        this.holder = holder
        if (selectedPos == position) {
            holder.itemView.bg_cart.setBackgroundResource(R.color.white_color)
        } else {
            holder.itemView.bg_cart.background = null
        }

        presenter?.bindCartViewHolder(holder.adapterPosition)

        holder.itemView.btn_remove.setOnClickListener { view ->

            onCartListener?.onItemRemoved(view, holder.adapterPosition)
        }

        holder.itemView.btn_add_attendee.setOnClickListener { view ->
            onCartListener?.onAddAttendeeClick(view, holder.adapterPosition)
        }

        holder.itemView.btn_minus.setOnClickListener { view ->

            onCartListener?.onMinusClick(view, holder.adapterPosition)
        }

        holder.itemView.btn_plus.setOnClickListener { view ->

            onCartListener?.onPlusClick(view, holder.adapterPosition)
        }

        holder.itemView.bg_cart.setOnClickListener { view ->
            onCartListener?.onItemClickListener(view, holder.adapterPosition)
        }
    }

    override fun loadImage(imageUrl: String?) {
        val options = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.ic_pa_default)
            .error(R.drawable.ic_pa_default)
        Glide.with(context)
            .load(imageUrl)
            .apply(options)
            .into(holder.itemView.img_logo)
    }

    override fun setProductName(name: String?) {
        holder.itemView.txt_product_name.text = name
    }

    override fun setInfo(info: String?) {
        holder.itemView.txt_info.text = info
    }

    override fun setLLDescInfo(outletName: String?, date: String?, time: String?) {
        holder.itemView.tv_outlet.text = outletName ?: ""
        holder.itemView.tv_date.text = date ?: ""
        holder.itemView.tv_time.text = time ?: ""
    }

    override fun setNoAttendee(noAttendee: String?) {
        holder.itemView.txt_no_attendee.text = noAttendee
    }

    override fun showAttendeePanel(isShown: Boolean) {
        holder.itemView.attendee_panel.visibility = if (isShown) View.VISIBLE else View.GONE
    }

    override fun showPointerAttendee(isShown: Boolean) {
        if (isShown && holder.adapterPosition == 0) {
            holder.itemView.img_pointer?.visibility = View.VISIBLE
            animationImage()
        } else {
            holder.itemView.img_pointer?.visibility = View.GONE
            hidePrompt()
        }
    }

    override fun setPointerText(text: String) {
        holder.itemView.tv_img_pointer.text = text
    }

    fun selectItem(pos: Int) {
        selectedPos = pos
        notifyDataSetChanged()
    }

    private fun checkIndemnity(holder: CartVH, item: CartItem?) {
        if (holder.itemView.event_panel.visibility == View.VISIBLE) {
            if (item?.event != null && item.event?.indemnityRequired == true) {
                holder.itemView.txt_indemnity?.visibility = View.VISIBLE
                if (item.isIndemnity == true) {
                    holder.itemView.txt_indemnity?.text =
                        context.getText(R.string.indemnity_cleared)
                    holder.itemView.txt_indemnity?.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black_color
                        )
                    )
                    val leftDrawable = ContextCompat.getDrawable(context, R.drawable.ic_check)
                    holder.itemView.txt_indemnity?.setCompoundDrawablesWithIntrinsicBounds(
                        leftDrawable,
                        null,
                        null,
                        null
                    )
                    holder.itemView.txt_indemnity?.compoundDrawablePadding =
                        context.resources.getDimensionPixelSize(R.dimen.dp_10)
                } else {
                    holder.itemView.txt_indemnity?.text =
                        context.getText(R.string.indemnity_not_cleared)
                    holder.itemView.txt_indemnity?.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.red_color
                        )
                    )
                    holder.itemView.txt_indemnity?.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        null,
                        null
                    )
                }

                return
            }

            holder.itemView.txt_indemnity?.visibility = View.GONE
        }
    }

    override fun showEventPanel(isShown: Boolean) {
        holder.itemView.event_panel.visibility = if (isShown) View.VISIBLE else View.GONE
    }

    override fun showAddButton(isShown: Boolean) {
        holder.itemView.btn_add_attendee.visibility = if (isShown) View.VISIBLE else View.GONE

    }

    override fun setTextAddButton(textBtn: String) {
        holder.itemView.btn_add_attendee.text = textBtn
    }

    private var anim: ObjectAnimator? = null
    private var isFirst = false
    private fun animationImage() {
        anim = ObjectAnimator.ofFloat(holder.itemView.img_pointer, "x", 320f, 250f)
        anim?.addUpdateListener {
            holder.itemView.img_pointer?.translationX = it.animatedValue as Float
        }
        anim?.duration = 1200L
        anim?.repeatMode = ObjectAnimator.REVERSE
        anim?.repeatCount = ValueAnimator.INFINITE
        anim?.start()

        isFirst = true
    }

    private fun hidePrompt() {
        if (isFirst) {
            anim?.removeAllUpdateListeners()
            anim?.removeAllListeners()
            anim?.pause()
            anim?.end()
            anim?.cancel()
            anim = null
            holder.itemView.img_pointer?.visibility = View.GONE
            isFirst = false
        }
    }

    override fun setMarginAddAttendeeButton(hasMargin: Boolean) {
        val params = holder.itemView.btn_add_attendee.layoutParams as RelativeLayout.LayoutParams
        if (hasMargin) {
            params.leftMargin = context.resources?.getDimension(R.dimen.dp_80)?.toInt() ?: 0
        } else {
            params.leftMargin = context.resources?.getDimension(R.dimen.dp_20)?.toInt() ?: 0
        }

        holder.itemView.btn_add_attendee.layoutParams = params
    }

    override fun setAttendeeAdapter(item: CartItem?) {
        val adapter = AttendeeAdapter(context, presenter, item)
        adapter.onAttendeeListener = onAttendeeListener
        holder.itemView.list_attendee.layoutManager = LinearLayoutManager(context)
        holder.itemView.list_attendee.adapter = adapter

        checkIndemnity(holder, item)
    }

    override fun setParticipantCartEventAdapter(
        item: CartItem?,
        participantAdapterListener: ParticipantAdapter.OnParticipantAdapterListener?,
        ticketAdapterListener: TicketAdapter.OnTicketAdapterListener?,
        listener: ParticipantCartEventAdapter.OnParticipantCartEventListener?
    ) {
        val adapter = ParticipantCartEventAdapter(
            context = context,
            listData = item?.event?.listSelectedTicket ?: ArrayList(),
            participantAdapterListener = participantAdapterListener,
            ticketAdapterListener = ticketAdapterListener,
            listener = listener
        )
        holder.itemView.list_attendee.layoutManager = LinearLayoutManager(context)
        holder.itemView.list_attendee.adapter = adapter
    }

    override fun setMinusVisibility(isShown: Boolean) {
        holder.itemView.btn_minus.visibility = if (isShown) View.VISIBLE else View.INVISIBLE
    }

    override fun setPlusVisibility(isShown: Boolean) {
        holder.itemView.btn_plus.visibility = if (isShown) View.VISIBLE else View.INVISIBLE
    }

    override fun updateList() {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(), true)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun showSkillFuture(isShown: Boolean) {
        if (isShown) {
            holder.itemView.img_skill_future.visibility = View.VISIBLE
            holder.itemView.txt_skill_future.visibility = View.VISIBLE
        } else {
            holder.itemView.img_skill_future.visibility = View.GONE
            holder.itemView.txt_skill_future.visibility = View.GONE
        }
    }

    override fun visibleDescription(
        isShowTxtInfo: Boolean,
        isShowLLDesc: Boolean,
        isShowTvOutlet: Boolean,
        isShowTvDate: Boolean,
        isShowTvTime: Boolean
    ) {
        holder.itemView.txt_info.visibility = if (isShowTxtInfo) View.VISIBLE else View.GONE
        holder.itemView.ll_desc.visibility = if (isShowLLDesc) View.VISIBLE else View.GONE
        if (isShowLLDesc) {
            holder.itemView.tv_outlet.visibility = if (isShowTvOutlet) View.VISIBLE else View.GONE
            holder.itemView.tv_date.visibility = if (isShowTvDate) View.VISIBLE else View.GONE
            holder.itemView.tv_time.visibility = if (isShowTvTime) View.VISIBLE else View.GONE
        }
    }

    class CartVH(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class DiffCallback : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return presenter?.areItemsTheSame(oldItemPosition, newItemPosition) ?: false
        }

        override fun getOldListSize(): Int {
            return presenter?.getOldListSize() ?: 0
        }

        override fun getNewListSize(): Int {
            return presenter?.getNewListSize() ?: 0
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return presenter?.areContentsTheSame(oldItemPosition, newItemPosition) ?: false
        }
    }

    interface OnCartListener : OnRecyclerViewListener {

        fun onItemRemoved(view: View, position: Int)

        fun onAddAttendeeClick(view: View, position: Int)

        fun onMinusClick(view: View, position: Int)

        fun onPlusClick(view: View, position: Int)
    }
}