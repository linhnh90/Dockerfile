package com.styl.pa.modules.interestGroupDetails.view

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.modules.base.CustomBaseFragment
import com.styl.pa.modules.cart.presenter.CartPresenter
import com.styl.pa.modules.courseDetails.view.CourseDetailsFragment
import com.styl.pa.modules.dialog.MessageDialogFragment
import com.styl.pa.modules.interestGroupDetails.InterestGroupDetailsContract
import com.styl.pa.modules.interestGroupDetails.presenter.InterestGroupDetailsPresenter
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.fragment_interest_group_details.view.*
import java.util.*

class InterestGroupDetailsFragment : CustomBaseFragment(), InterestGroupDetailsContract.View,
    View.OnClickListener {

    companion object {
        private const val ARG_IG_INFO = BuildConfig.APPLICATION_ID + ".args.ARG_IG_INFO"
        private const val ARG_SHOULD_CHECK_VACANCY =
            BuildConfig.APPLICATION_ID + ".args.ARG_SHOULD_CHECK_VACANCY"

        fun newInstance(igInfo: InterestGroup, shouldCheckVacancy: Boolean = false) =
            InterestGroupDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_IG_INFO, igInfo)
                    putBoolean(ARG_SHOULD_CHECK_VACANCY, shouldCheckVacancy)
                }
            }
    }


    private lateinit var getView: View
    private var presenter: InterestGroupDetailsPresenter? = null
    private var igInfo: InterestGroup? = null
    private var shouldCheckVacancy: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.presenter = InterestGroupDetailsPresenter(this, activity)
        this.getBundle()
    }

    private fun getBundle() {
        this.igInfo = arguments?.getParcelable(ARG_IG_INFO)
        this.shouldCheckVacancy = arguments?.getBoolean(ARG_SHOULD_CHECK_VACANCY, false) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        getView = inflater.inflate(R.layout.fragment_interest_group_details, container, false)
        this.init()
        this.bindData()

        //get vacancy
        if (igInfo != null && GeneralUtils.isNetworkAvailable(activity)) {
            presenter?.checkIgVacancy(igInfo = igInfo!!, isAddCart = false, isQuickBook = false)
        }

        return getView
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBackgroundLine(R.drawable.bg_gradient_interestgroup)
        (activity as? MainActivity)?.showProgressPayment(false)
        setTitle(igInfo?.getDecodedTitle().toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
        presenter = null
    }

    override fun setCanOrder(isAlive: Boolean) {
        getView.btn_book_now.isEnabled = isAlive
        getView.btn_add_to_card.isEnabled = isAlive
        getView.img_add_cart.isEnabled = isAlive

        if (activity != null) {
            if (isAlive) {
                getView.txt_add_to_cart.setTextColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.red_color_3
                    )
                )
            } else {
                getView.txt_add_to_cart.setTextColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.gray_color_1
                    )
                )
            }
        }
    }

    private fun bindData() {
        setTitle((igInfo?.getDecodedTitle() ?: "").toString())

        //img info
        val options = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.ic_pa_default)
            .error(R.drawable.ic_pa_default)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
        Glide.with(activity!!).load(igInfo?.getImageURLBase())
            .apply(options)
            .into(getView.img_info)

        getView.v_opacity?.setBackgroundColor(GeneralUtils.getColorWithAlpha(Color.BLACK, 0.6f))

        //ig code
        getView.tv_ig_code.text = igInfo?.igCode ?: ""

        //ig name
        getView.tv_ig_name.text = (igInfo?.getDecodedTitle() ?: "").toString()

        //organize name
        getView.txt_organize_name.text = getString(R.string.organised_by, igInfo?.outletName ?: "")

        //outlet header
        getView.tv_outlet_header.text = igInfo?.outletName ?: ""

        //group description
        getView.tv_group_description.text = igInfo?.getDecodedDescription() ?: ""

        //organising committee
        getView.tv_organising_committee.text = igInfo?.outletName ?: ""

        //fee
        val memberFeeCount: Int
        val priceRange = igInfo?.getPriceRange() ?: FloatArray(2)
        val minFee = priceRange[0]
        memberFeeCount = if (minFee == priceRange[1]) {
            0
        } else {
            igInfo?.igFees?.size ?: 0
        }
        if (minFee > 0) {
            var fromStr = ""
            if (memberFeeCount > 1) {
                fromStr = getString(R.string.fee_from) + " "
            }
            val memberFeeStr = fromStr + GeneralUtils.formatAmountSymbols("$", minFee, 2)
            getView.tv_price_member.text = memberFeeStr
        }

//        val shortDate = GeneralUtils.formatShortDay(igInfo?.startDate)
        //membership period
        getView.tv_membership_period.text = GeneralUtils.formatToDate(igInfo?.startDate) + " ~ " +
                GeneralUtils.formatToDate(igInfo?.endDate)

        var startTime = ""
        var endTime = ""
        if (igInfo?.igSessions != null && igInfo?.igSessions!!.isNotEmpty()) {
            if (!igInfo?.igSessions?.get(0)?.getStartTime().isNullOrEmpty()) {
                startTime = GeneralUtils.formatToTime(igInfo?.igSessions?.get(0)?.getStartTime())
            }

            if (!igInfo?.igSessions?.get(0)?.getEndTime().isNullOrEmpty()) {
                endTime = GeneralUtils.formatToTime(igInfo?.igSessions?.get(0)?.getEndTime())
            }
        }
        if (startTime.isEmpty() && endTime.isEmpty()) {
            getView.txt_time.setText("")
        } else {
            getView.txt_time.setText((startTime + " ~ " + endTime))
        }

    }

    private fun init() {
        getView.btn_back?.setOnClickListener(this)
        getView.btn_add_to_card.setOnClickListener(this)
        getView.btn_book_now.setOnClickListener(this)

        getView.txt_add_to_cart.paintFlags =
            getView.txt_add_to_cart.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        getView.tv_price_member?.text = getString(R.string.free)
        val maxAttendeeStr = CartPresenter.MAX_ATTENDEE.toString()
        val participantNote =
            getString(R.string.interest_group_details_participant_note, maxAttendeeStr)
        getView.tv_participant_note.text = participantNote

        getView.tv_zoom_out.setOnClickListener(this)
        getView.tv_zoom.setOnClickListener(this)
        getView.tv_zoom_in.setOnClickListener(this)

        getView.img_skill_future.visibility = View.GONE

        val canOrder = (activity as? MainActivity)?.canOrder ?: true

        if (canOrder) {
            getView.txt_add_to_cart.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.red_color_3
                )
            )
        } else {
            getView.txt_add_to_cart.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.gray_color_1
                )
            )
        }
        getView.btn_add_to_card.isEnabled = canOrder
        getView.btn_book_now.isEnabled = canOrder
        getView.img_add_cart.isEnabled = canOrder
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

    private fun reloadVacancy(data: InterestGroup?) {
        var vacancyStr = "0"
        var maxVacancyStr = "0"
        if (data != null) {
            val vacancy = data.vacancies ?: 0
            var max = data.maxVacancy ?: 0
            vacancyStr = vacancy.toString()
            maxVacancyStr = max.toString()
            if (max == 0) {
                vacancyStr = getString(R.string.unlimited)
            }
        }
        getView.tv_vacancy.text = vacancyStr
    }

    override fun handleCourseEvent(classInfo: ClassInfo, isAddCart: Boolean, isQuickBook: Boolean) {
        // Do nothing because this method is never called
    }

    override fun handleIgEvent(igInfo: InterestGroup, isAddCart: Boolean, isQuickBook: Boolean) {
        reloadVacancy(igInfo)

        if (isAddCart) {
            val cartItem = CartItem(
                UUID.randomUUID().toString(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                igInfo
            )
            if (isQuickBook) {
                (activity as? MainActivity)?.doQuickBook(arrayOf(cartItem).toList())
            } else {
                (activity as? MainActivity)?.addToCart(arrayOf(cartItem).toList())
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_add_to_card -> {
                if (igInfo != null) {
                    presenter?.checkIgVacancy(
                        igInfo = igInfo!!,
                        isAddCart = true,
                        isQuickBook = false
                    )
                }
            }
            R.id.btn_book_now -> {
                if (igInfo != null) {
                    presenter?.checkIgVacancy(
                        igInfo = igInfo!!,
                        isAddCart = true,
                        isQuickBook = true
                    )
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

    private fun changeSizeText(mode: Int) {
        when (mode) {
            CourseDetailsFragment.ZOOM_NORMAL -> {
                getView.tv_ig_code.textSize =
                    context?.resources?.getDimension(R.dimen.text_size_normal) ?: 0f
                getView.tv_ig_name.textSize =
                    context?.resources?.getDimension(R.dimen.text_size_large) ?: 0f
                getView.txt_organize_name.textSize =
                    context?.resources?.getDimension(R.dimen.text_size_small) ?: 0f
                getView.tv_vacancy.textSize =
                    context?.resources?.getDimension(R.dimen.text_size_small) ?: 0f

                getView.tv_outlet_header.textSize =
                    context?.resources?.getDimension(R.dimen.text_size_small) ?: 0f
                getView.tv_ig_description_label.textSize =
                    context?.resources?.getDimension(R.dimen.text_size_small) ?: 0f
                getView.tv_group_description.textSize =
                    context?.resources?.getDimension(R.dimen.text_size_small) ?: 0f
                getView.tv_organising_committee_label.textSize =
                    context?.resources?.getDimension(R.dimen.text_size_small) ?: 0f
                getView.tv_organising_committee.textSize =
                    context?.resources?.getDimension(R.dimen.text_size_small) ?: 0f

                getView.tv_price_member.textSize =
                    context?.resources?.getDimension(R.dimen.text_size_xxhlarge) ?: 0f
                getView.tv_fee_note.textSize =
                    context?.resources?.getDimension(R.dimen.text_size_lmini) ?: 0f
                getView.tv_participant_note.textSize =
                    context?.resources?.getDimension(R.dimen.text_size_lmini) ?: 0f

                getView.tv_membership_period_label.textSize =
                    context?.resources?.getDimension(R.dimen.text_size_small) ?: 0f
                getView.tv_membership_period.textSize =
                    context?.resources?.getDimension(R.dimen.text_size_small) ?: 0f

            }

            CourseDetailsFragment.ZOOM_IN -> {
                getView.tv_ig_code.textSize = getTextSizeZoomIn(R.dimen.text_size_normal)
                getView.tv_ig_name.textSize = getTextSizeZoomIn(R.dimen.text_size_large)
                getView.txt_organize_name.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.tv_vacancy.textSize = getTextSizeZoomIn(R.dimen.text_size_small)

                getView.tv_outlet_header.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.tv_ig_description_label.textSize =
                    getTextSizeZoomIn(R.dimen.text_size_small)
                getView.tv_group_description.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.tv_organising_committee_label.textSize =
                    getTextSizeZoomIn(R.dimen.text_size_small)
                getView.tv_organising_committee.textSize =
                    getTextSizeZoomIn(R.dimen.text_size_small)

                getView.tv_price_member.textSize = getTextSizeZoomIn(R.dimen.text_size_xxhlarge)
                getView.tv_fee_note.textSize = getTextSizeZoomIn(R.dimen.text_size_lmini)
                getView.tv_participant_note.textSize = getTextSizeZoomIn(R.dimen.text_size_lmini)

                getView.tv_membership_period_label.textSize =
                    getTextSizeZoomIn(R.dimen.text_size_small)
                getView.tv_membership_period.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
            }

            CourseDetailsFragment.ZOOM_OUT -> {
                getView.tv_ig_code.textSize = getTextSizeZoomOut(R.dimen.text_size_normal)
                getView.tv_ig_name.textSize = getTextSizeZoomOut(R.dimen.text_size_large)
                getView.txt_organize_name.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.tv_vacancy.textSize = getTextSizeZoomOut(R.dimen.text_size_small)

                getView.tv_outlet_header.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.tv_ig_description_label.textSize =
                    getTextSizeZoomOut(R.dimen.text_size_small)
                getView.tv_group_description.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.tv_organising_committee_label.textSize =
                    getTextSizeZoomOut(R.dimen.text_size_small)
                getView.tv_organising_committee.textSize =
                    getTextSizeZoomOut(R.dimen.text_size_small)

                getView.tv_price_member.textSize = getTextSizeZoomOut(R.dimen.text_size_xxhlarge)
                getView.tv_fee_note.textSize = getTextSizeZoomOut(R.dimen.text_size_lmini)
                getView.tv_participant_note.textSize = getTextSizeZoomOut(R.dimen.text_size_lmini)

                getView.tv_membership_period_label.textSize =
                    getTextSizeZoomOut(R.dimen.text_size_small)
                getView.tv_membership_period.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
            }
        }
    }
}