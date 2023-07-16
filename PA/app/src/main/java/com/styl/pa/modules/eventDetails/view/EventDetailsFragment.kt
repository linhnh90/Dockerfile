package com.styl.pa.modules.eventDetails.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.adapters.EventPricingAdapter
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.event.EventClassTicket
import com.styl.pa.entities.event.EventFee
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.interfaces.AddToCartEvent
import com.styl.pa.modules.base.CustomBaseFragment
import com.styl.pa.modules.courseDetails.view.CourseDetailsFragment
import com.styl.pa.modules.courseDetails.view.FeeAdapter
import com.styl.pa.modules.dialog.MessageDialogFragment
import com.styl.pa.modules.eventDetails.IEventDetailsContact
import com.styl.pa.modules.eventDetails.presenter.EventDetailsPresenter
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.fragment_event_details.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Ngatran on 03/11/2019.
 */
class EventDetailsFragment : CustomBaseFragment(), IEventDetailsContact.IEventDetailsView,
    View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add_to_card -> {
                if (eventInfo != null) {
                    val cartItem =
                        CartItem(
                            UUID.randomUUID().toString(),
                            null,
                            null,
                            eventInfo,
                            null,
                            null,
                            null,
                            null
                        )
                    (activity as? MainActivity)?.addToCart(arrayOf(cartItem).toList())
                }
            }
            R.id.btn_book_now -> {
                if (eventInfo != null) {
                    val cartItem =
                        CartItem(
                            UUID.randomUUID().toString(),
                            null,
                            null,
                            eventInfo,
                            null,
                            null,
                            null,
                            null
                        )
                    (activity as? MainActivity)?.doQuickBook(arrayOf(cartItem).toList())
                }
            }
            R.id.btn_back -> {
                fragmentManager?.popBackStack()
            }
            R.id.tv_zoom_out -> {
                changeSizeText(CourseDetailsFragment.ZOOM_OUT)
            }
            R.id.tv_zoom -> {
                changeSizeText(CourseDetailsFragment.ZOOM_NORMAL)
            }
            R.id.tv_zoom_in -> {
                changeSizeText(CourseDetailsFragment.ZOOM_IN)
            }
        }
    }

    companion object {
        private const val ARG_EVENT_INFO = BuildConfig.APPLICATION_ID + ".args.ARG_EVENT_INFO"

        fun newInstance(eventInfo: EventInfo): EventDetailsFragment {
            val f = EventDetailsFragment()
            val args = Bundle()
            args.putParcelable(ARG_EVENT_INFO, eventInfo)
            f.arguments = args
            return f
        }
    }

    private val sdfEventDateInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    private val sdfEventDateOutput = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    private var presenter: EventDetailsPresenter? = null
    private var eventInfo: EventInfo? = null
    private var getView: View? = null
    private var adapter: FeeAdapter<EventFee>? = null

    private var navigationView = object : AddToCartEvent.NavigationView {
        override fun navigation() {
            presenter?.navigationCardPage(eventInfo)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = EventDetailsPresenter(this, activity)

        if (arguments != null) {
            eventInfo = arguments?.getParcelable(ARG_EVENT_INFO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        getView = inflater.inflate(R.layout.fragment_event_details, container, false)
        init()
        bindData()

        getView?.btn_back?.setOnClickListener(this)

        return getView
    }

    private fun init() {
        getView?.tv_zoom_out?.setOnClickListener(this)
        getView?.tv_zoom?.setOnClickListener(this)
        getView?.tv_zoom_in?.setOnClickListener(this)

        getView?.btn_add_to_card?.setOnClickListener(this)
        getView?.btn_book_now?.setOnClickListener(this)
        val minPrice = eventInfo?.minPrice ?: 0.0f
        val maxPrice = eventInfo?.maxPrice ?: 0.0f
        var textPrice = GeneralUtils.formatAmountSymbols("$", 0f, 2)
        if (minPrice == maxPrice && minPrice == 0f){
            textPrice = getString(R.string.free)
            getView?.tv_from?.visibility = View.GONE
        } else if (minPrice < maxPrice){
            getView?.tv_from?.text = getString(R.string.fee_from)
            getView?.tv_from?.visibility = View.VISIBLE
            textPrice = GeneralUtils.formatAmountSymbols("$", minPrice, 2)
        } else {
            textPrice = GeneralUtils.formatAmountSymbols("$", minPrice, 2)
            getView?.tv_from?.visibility = View.GONE
        }
        getView?.tv_price_member?.text = textPrice

        adapter = FeeAdapter(eventInfo?.eventFees)
        getView?.list_price?.layoutManager = LinearLayoutManager(activity)
        getView?.list_price?.adapter = adapter

        val canOrder = (activity as? MainActivity)?.canOrder ?: true
        getView?.btn_add_to_card?.isEnabled = canOrder
    }

    private fun bindData() {
        if (eventInfo != null) {
            setTitle(eventInfo?.getDecodedTitle().toString())

            val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_pa_default)
                .error(R.drawable.ic_pa_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)

            if (activity != null && getView?.img_info != null) {
                Glide.with(activity!!).load(eventInfo?.getImageUrl())
                    .apply(options)
                    .into(getView?.img_info!!)
            }

            getView?.v_opacity?.setBackgroundColor(
                GeneralUtils.getColorWithAlpha(
                    Color.BLACK,
                    0.6f
                )
            )

            getView?.tv_event_code?.text = eventInfo?.eventCode
            getView?.tv_event_name?.text = eventInfo?.getDecodedTitle()
            getView?.txt_organize_name?.text =
                getString(R.string.organised_by, eventInfo?.outletName)
            getView?.tv_outlet_header?.text = eventInfo?.outletName ?: ""

            val startTime = GeneralUtils.formatToTime(eventInfo?.dateFrom)
            val endTime = GeneralUtils.formatToTime(eventInfo?.dateTo)

            val rangeTime = StringBuilder()
                .append(startTime)
                .append(" - ")
                .append(endTime)
                .toString()

            getView?.txt_time?.text = rangeTime

            adapter?.setItem(eventInfo?.eventFees)
            adapter?.notifyDataSetChanged()

            var vacancy = "0"
            if (null != eventInfo?.vacancies)
                vacancy = (eventInfo?.vacancies ?: 0).toString()
            getView?.tv_vacancy?.text = vacancy
            getView?.txt_date?.text = GeneralUtils.eventFormatDate(
                eventInfo?.dateFrom,
                eventInfo?.dateTo,
                sdfEventDateInput,
                sdfEventDateOutput
            )
            getView?.txt_course_description?.text = eventInfo?.getDecodedDescription()
            getView?.txt_close_date?.text = GeneralUtils.formatToDate(eventInfo?.closingDate)

            checkFeeRecyclerMaxHeight()

            val listClassTicket = ArrayList<EventClassTicket>()
            if (eventInfo?.classTickets?.isNotEmpty() == true){
                listClassTicket.addAll(eventInfo?.classTickets!!)
            }
            val adapterPricing = EventPricingAdapter(
                context = activity,
                listData = listClassTicket
            )
            getView?.rcv_pricing?.layoutManager = LinearLayoutManager(activity)
            getView?.rcv_pricing?.adapter = adapterPricing

            getView?.tv_venue?.text = eventInfo?.venue ?: ""

            getView?.tv_organising_committee?.text = eventInfo?.outletName ?: ""

            getView?.tv_special_instructions?.text = eventInfo?.specialInstruction ?: ""

            getView?.tv_event_faq?.text = eventInfo?.getFaq()
        }
    }

    private fun checkFeeRecyclerMaxHeight() {
        val viewObserver = getView?.ll_price_container?.viewTreeObserver
        if (true == viewObserver?.isAlive) {
            viewObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    getView?.ll_price_container?.viewTreeObserver?.removeOnGlobalLayoutListener(this)

                    val maxHeight = resources.getDimensionPixelSize(R.dimen.dp_260)
                    if ((getView?.ll_price_container?.height
                            ?: 0) > maxHeight
                    ) {
                        getView?.ll_price_container?.layoutParams?.height = maxHeight
                    }
                }

            })
        }
    }

    override fun handleCourseEvent(classInfo: ClassInfo, isAddCart: Boolean, isQuickBook: Boolean) {
        // Do nothing because this method is never called
    }

    override fun handleIgEvent(classInfo: InterestGroup, isAddCart: Boolean, isQuickBook: Boolean) {
        // Do nothing because this method is never called
    }

    private var fragment: MessageDialogFragment? = null
    override fun showErrorMessage(titleResId: Int, messageResId: Int) {
        reloadVacancy(null)

        if (fragment != null && fragment!!.isVisible) {
            fragment?.dismiss()
        }
        fragment = MessageDialogFragment.newInstance(titleResId, messageResId, true)
        if (fragmentManager != null) {
            fragment?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }

    private fun reloadVacancy(data: Int?) {
        var vacancy = "0"
        if (null != data)
            vacancy = (data ?: 0).toString()
        getView?.tv_vacancy?.text = vacancy
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showProgressPayment(false)
        setTitle(eventInfo?.getDecodedTitle().toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
        presenter = null
    }

    private fun changeSizeText(mode: Int) {
        getView?.let { getView ->
            when (mode) {
                CourseDetailsFragment.ZOOM_IN -> {
                    getView.tv_event_code.textSize = getTextSizeZoomIn(R.dimen.text_size_normal)
                    getView.tv_event_name.textSize = getTextSizeZoomIn(R.dimen.text_size_large)
                    getView.txt_organize_name.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                    getView.tv_vacancy.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                    getView.tv_outlet_header.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                    getView.txt_course_description.textSize =
                        getTextSizeZoomIn(R.dimen.text_size_small)
                    getView.tv_event_desc_label.textSize =
                        getTextSizeZoomIn(R.dimen.text_size_small)
                    getView.tv_price_member.textSize = getTextSizeZoomIn(R.dimen.text_size_xxhlarge)
                    getView.txt_detail_note_1.textSize = getTextSizeZoomIn(R.dimen.text_size_lmini)
                    getView.txt_detail_note_2.textSize = getTextSizeZoomIn(R.dimen.text_size_lmini)
                    getView.txt_detail_note_3.textSize = getTextSizeZoomIn(R.dimen.text_size_lmini)
                    getView.tv_date_label.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                    getView.txt_date.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                    getView.txt_time.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                    getView.txt_close_date_label.textSize =
                        getTextSizeZoomIn(R.dimen.text_size_small)
                    getView.txt_close_date.textSize = getTextSizeZoomIn(R.dimen.text_size_small)

                }
                CourseDetailsFragment.ZOOM_NORMAL -> {
                    getView.tv_event_code.textSize =
                        context?.resources?.getDimension(R.dimen.text_size_normal)
                            ?: 0f
                    getView.tv_event_name.textSize =
                        context?.resources?.getDimension(R.dimen.text_size_large)
                            ?: 0f
                    getView.txt_organize_name.textSize =
                        context?.resources?.getDimension(R.dimen.text_size_small)
                            ?: 0f
                    getView.tv_vacancy.textSize =
                        context?.resources?.getDimension(R.dimen.text_size_small)
                            ?: 0f
                    getView.tv_outlet_header.textSize =
                        context?.resources?.getDimension(R.dimen.text_size_small)
                            ?: 0f
                    getView.txt_course_description.textSize =
                        context?.resources?.getDimension(R.dimen.text_size_small)
                            ?: 0f
                    getView.tv_event_desc_label.textSize =
                        context?.resources?.getDimension(R.dimen.text_size_small)
                            ?: 0f
                    getView.tv_price_member.textSize =
                        context?.resources?.getDimension(R.dimen.text_size_xxhlarge)
                            ?: 0f
                    getView.txt_detail_note_1.textSize =
                        context?.resources?.getDimension(R.dimen.text_size_lmini) ?: 0f
                    getView.txt_detail_note_2.textSize =
                        context?.resources?.getDimension(R.dimen.text_size_lmini) ?: 0f
                    getView.txt_detail_note_3.textSize =
                        context?.resources?.getDimension(R.dimen.text_size_lmini) ?: 0f
                    getView.tv_date_label.textSize =
                        context?.resources?.getDimension(R.dimen.text_size_small)
                            ?: 0f
                    getView.txt_date.textSize =
                        context?.resources?.getDimension(R.dimen.text_size_small)
                            ?: 0f
                    getView.txt_time.textSize =
                        context?.resources?.getDimension(R.dimen.text_size_small)
                            ?: 0f
                    getView.txt_close_date_label.textSize =
                        context?.resources?.getDimension(R.dimen.text_size_small)
                            ?: 0f
                    getView.txt_close_date.textSize =
                        context?.resources?.getDimension(R.dimen.text_size_small)
                            ?: 0f
                }
                CourseDetailsFragment.ZOOM_OUT -> {
                    getView.tv_event_code.textSize = getTextSizeZoomOut(R.dimen.text_size_normal)
                    getView.tv_event_name.textSize = getTextSizeZoomOut(R.dimen.text_size_large)
                    getView.txt_organize_name.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                    getView.tv_vacancy.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                    getView.tv_outlet_header.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                    getView.txt_course_description.textSize =
                        getTextSizeZoomOut(R.dimen.text_size_small)
                    getView.tv_event_desc_label.textSize =
                        getTextSizeZoomOut(R.dimen.text_size_small)
                    getView.tv_price_member.textSize =
                        getTextSizeZoomOut(R.dimen.text_size_xxhlarge)
                    getView.txt_detail_note_1.textSize = getTextSizeZoomOut(R.dimen.text_size_lmini)
                    getView.txt_detail_note_2.textSize = getTextSizeZoomOut(R.dimen.text_size_lmini)
                    getView.txt_detail_note_3.textSize = getTextSizeZoomOut(R.dimen.text_size_lmini)
                    getView.tv_date_label.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                    getView.txt_date.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                    getView.txt_time.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                    getView.txt_close_date_label.textSize =
                        getTextSizeZoomOut(R.dimen.text_size_small)
                    getView.txt_close_date.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                }
            }
        }
    }

    private fun getTextSizeZoomIn(size: Int): Float {
        when (size) {
            R.dimen.text_size_small -> return context?.resources?.getDimension(R.dimen.text_size_mini)
                ?: 0f
            R.dimen.text_size_normal -> return context?.resources?.getDimension(R.dimen.text_size_small)
                ?: 0f
            R.dimen.text_size_large -> return context?.resources?.getDimension(R.dimen.text_size_medium)
                ?: 0f
            R.dimen.text_size_xxhlarge -> return context?.resources?.getDimension(R.dimen.text_size_xhlarge)
                ?: 0f
            R.dimen.text_size_lmini -> return context?.resources?.getDimension(R.dimen.text_size_mmini)
                ?: 0f
            R.dimen.text_size_mini -> return context?.resources?.getDimension(R.dimen.text_size_lmini)
                ?: 0f
            R.dimen.text_size_xxxlarge -> return context?.resources?.getDimension(R.dimen.text_size_xxlarge)
                ?: 0f
        }
        return context?.resources?.getDimension(R.dimen.text_size_medium) ?: 0f
    }

    private fun getTextSizeZoomOut(size: Int): Float {
        when (size) {
            R.dimen.text_size_small -> return context?.resources?.getDimension(R.dimen.text_size_normal)
                ?: 0f
            R.dimen.text_size_normal -> return context?.resources?.getDimension(R.dimen.text_size_xnormal)
                ?: 0f
            R.dimen.text_size_large -> return context?.resources?.getDimension(R.dimen.text_size_xlarge)
                ?: 0f
            R.dimen.text_size_xxhlarge -> return context?.resources?.getDimension(R.dimen.text_size_xxxhlarge)
                ?: 0f
            R.dimen.text_size_lmini -> return context?.resources?.getDimension(R.dimen.text_size_mini)
                ?: 0f
            R.dimen.text_size_mini -> return context?.resources?.getDimension(R.dimen.text_size_small)
                ?: 0f
            R.dimen.text_size_xxxlarge -> return context?.resources?.getDimension(R.dimen.text_size_hlarge)
                ?: 0f
        }
        return context?.resources?.getDimension(R.dimen.text_size_large) ?: 0f
    }

    override fun setCanOrder(isAlive: Boolean) {
        getView?.btn_add_to_card?.isEnabled = isAlive
        getView?.btn_book_now?.isEnabled = isAlive

    }

}