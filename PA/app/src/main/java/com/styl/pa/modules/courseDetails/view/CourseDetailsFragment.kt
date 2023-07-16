package com.styl.pa.modules.courseDetails.view


import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.classes.ClassFee
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.modules.base.CustomBaseFragment
import com.styl.pa.modules.courseDetails.ICourseDetailsContact
import com.styl.pa.modules.courseDetails.presenter.CourseDetailsPresenter
import com.styl.pa.modules.dialog.MessageDialogFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.fragment_course_details.view.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 *
 */
class CourseDetailsFragment : CustomBaseFragment(), ICourseDetailsContact.IView, View.OnClickListener {

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add_to_card -> {
//                (activity as MainActivity).checkAddToCart(classInfo, null, SearchType.COURSES, navigationView, null)
                if (classInfo != null) {
                    presenter?.checkClassVacancy(classInfo!!, true, false)
                }
            }
            R.id.btn_book_now -> {
                if (classInfo != null) {
                    presenter?.checkClassVacancy(classInfo!!, true, true)
                }
            }
            R.id.btn_back -> {
                fragmentManager?.popBackStack()
            }
            R.id.tv_zoom_out -> {
                changeSizeText(ZOOM_OUT)
            }
            R.id.tv_zoom -> {
                changeSizeText(ZOOM_NORMAL)
            }
            R.id.tv_zoom_in -> {
                changeSizeText(ZOOM_IN)
            }
            R.id.txt_session_detail, R.id.img_arrow -> {
                if (classInfo?.getClassSessions() != null && classInfo?.getClassSessions()?.size!! > MAX_SESSION_LINE) {
                    if (getView.txt_session_detail.maxLines == MAX_SESSION_LINE) {
                        getView.txt_session_detail.maxLines = Int.MAX_VALUE
                    } else {
                        getView.txt_session_detail.maxLines = MAX_SESSION_LINE
                    }

                    if (getView.img_arrow.rotation == 0f) {
                        getView.img_arrow.rotation = 180f
                    } else {
                        getView.img_arrow.rotation = 0f
                    }
                }
            }
        }
    }

    companion object {
        const val ZOOM_IN = 1
        const val ZOOM_OUT = 2
        const val ZOOM_NORMAL = 3
        const val MAX_SESSION_LINE = 3
        private const val ARG_CLASS_INFO = BuildConfig.APPLICATION_ID + ".args.ARG_CLASS_INFO"
        private const val SHOULD_CHECK_VACANCY = BuildConfig.APPLICATION_ID + ".args.SHOULD_CHECK_VACANCY"

        fun newInstance(classInfo: ClassInfo, shouldCheckVacancy: Boolean = false): CourseDetailsFragment {
            val f = CourseDetailsFragment()
            val args = Bundle()
            args.putParcelable(ARG_CLASS_INFO, classInfo)
            args.putBoolean(SHOULD_CHECK_VACANCY, shouldCheckVacancy)
            f.arguments = args
            return f
        }
    }

    private var presenter: CourseDetailsPresenter? = null
    private var classInfo: ClassInfo? = null
    private var shouldCheckVacancy = false
    private var adapter: FeeAdapter<ClassFee>? = null
    private lateinit var getView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = CourseDetailsPresenter(this, activity)

        if (arguments != null) {
            classInfo = arguments!!.getParcelable(ARG_CLASS_INFO)
            shouldCheckVacancy = arguments!!.getBoolean(SHOULD_CHECK_VACANCY)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        getView = inflater.inflate(R.layout.fragment_course_details, container, false)
        init()
        bindData()

        // get class vacancy in case scan qr code
        if (shouldCheckVacancy && classInfo != null &&
                GeneralUtils.isNetworkAvailable(activity)) {
            presenter?.checkClassVacancy(classInfo!!, false, false)
        }

        getView.btn_back?.setOnClickListener(this)

        return getView
    }

    private fun init() {
        getView.btn_add_to_card.setOnClickListener(this)
        getView.btn_book_now.setOnClickListener(this)
        getView.txt_add_to_cart.paintFlags = getView.txt_add_to_cart.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        getView.tv_class_size_label?.text = getString(R.string.maximum_participants_title)
        getView.txt_material_fee?.text = GeneralUtils.formatAmountSymbols("$", 0f, 2)
        getView.tv_price_member?.text = GeneralUtils.formatAmountSymbols("$", 0f, 2)
        var minMaterialFee = 0f
        var maxMaterialFee = 0f
        val memberFeeCount: Int
        var materialFeeCount = 0

        val priceRange = classInfo?.getPriceRange() ?: FloatArray(2)
        val minFee = priceRange[0]
        if (minFee == priceRange[1]) {
            memberFeeCount = 0
        } else {
            memberFeeCount = classInfo?.getClassFees()?.size ?: 0
        }

        if (classInfo?.materialFees?.isNotEmpty() == true) {
            materialFeeCount = classInfo?.materialFees!!.size
            classInfo?.materialFees?.forEach { materialFee ->
                val feeNormalAmount = materialFee.getFeeNormalAmount()?.toFloat() ?: 0f
                if (minMaterialFee == 0f || minMaterialFee > feeNormalAmount) {
                    minMaterialFee = feeNormalAmount
                }
                if (maxMaterialFee == 0f || maxMaterialFee < feeNormalAmount) {
                    maxMaterialFee = feeNormalAmount
                }
            }
        }

        val memberFeeStr = GeneralUtils.formatAmountSymbols("$", minFee, 2)
        if (memberFeeCount > 1) {
            getView.tv_from.text = getString(R.string.fee_from)
            getView.tv_from.visibility = View.VISIBLE
        } else {
            getView.tv_from.visibility = View.GONE
        }
        getView.tv_material_fee_add.visibility = View.GONE
        var materialFeeStr = GeneralUtils.formatAmountSymbols("$", minMaterialFee, 2)
        if (materialFeeCount > 1 && minMaterialFee != maxMaterialFee) {
            materialFeeStr = "${getString(R.string.fee_from)} $materialFeeStr"
            getView.tv_material_fee_add.visibility = View.VISIBLE
        } else if (materialFeeCount == 0) {
            materialFeeStr = "N/A"
        }
        getView.tv_price_member.text = memberFeeStr
        getView.txt_material_fee.text = materialFeeStr

        adapter = FeeAdapter(classInfo?.getClassFees())
        getView.list_price.layoutManager = LinearLayoutManager(activity)
        getView.list_price.adapter = adapter

        getView.tv_zoom_out.setOnClickListener(this)
        getView.tv_zoom.setOnClickListener(this)
        getView.tv_zoom_in.setOnClickListener(this)
        getView.txt_session_detail.setOnClickListener(this)
        getView.img_arrow.setOnClickListener(this)

        if (classInfo?.isSkillsFutureEnabled == true) {
            getView.img_skill_future.visibility = View.VISIBLE
            getView.txt_skill_future.visibility = View.VISIBLE
        } else {
            getView.img_skill_future.visibility = View.GONE
            getView.txt_skill_future.visibility = View.GONE
        }

        val canOrder = (activity as? MainActivity)?.canOrder ?: true

        if (canOrder) {
            getView.txt_add_to_cart.setTextColor(ContextCompat.getColor(context!!, R.color.red_color_3))
        } else {
            getView.txt_add_to_cart.setTextColor(ContextCompat.getColor(context!!, R.color.gray_color_1))
        }
        getView.btn_add_to_card.isEnabled = canOrder
        getView.btn_book_now.isEnabled = canOrder
        getView.img_add_cart.isEnabled = canOrder
    }

    private fun bindData() {
        if (classInfo != null) {
            setTitle(classInfo?.getDecodedTitle().toString())

            val options = RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_pa_default)
                    .error(R.drawable.ic_pa_default)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
            Glide.with(activity!!).load(classInfo?.getImageURL())
                    .apply(options)
                    .into(getView.img_info)
            getView.v_opacity?.setBackgroundColor(GeneralUtils.getColorWithAlpha(Color.BLACK, 0.6f))
            getView.txt_course_code.text = classInfo?.getClassCode()
            getView.txt_course_name.text = classInfo?.getDecodedTitle()
            getView.txt_organize_name.text = getString(R.string.organised_by, classInfo?.getOutletName())
            
            adapter?.setItem(classInfo?.getClassFees())
            adapter?.notifyDataSetChanged()

            var vacancy = "0"
            if (!classInfo?.getVacancies().isNullOrEmpty())
                vacancy = classInfo?.getVacancies()!!
            getView.txt_vacancy.setText(vacancy)

            val shortDate = GeneralUtils.formatShortDay(classInfo?.getStartDate())
            getView.txt_date.text = (GeneralUtils.formatToDate(classInfo?.getStartDate()) + " ~ " +
                    GeneralUtils.formatToDate(classInfo?.getEndDate()))+"($shortDate)"

            var startTime = ""
            var endTime = ""
            if (classInfo?.getClassSessions() != null && classInfo?.getClassSessions()!!.size > 0) {
                if (!classInfo?.getClassSessions()?.get(0)?.getStartTime().isNullOrEmpty()) {
                    startTime = GeneralUtils.formatToTime(classInfo?.getClassSessions()?.get(0)?.getStartTime())
                }

                if (!classInfo?.getClassSessions()?.get(0)?.getEndTime().isNullOrEmpty()) {
                    endTime = GeneralUtils.formatToTime(classInfo?.getClassSessions()?.get(0)?.getEndTime())
                }
            }
            if (startTime.isEmpty() && endTime.isEmpty()) {
                getView.txt_time.setText("")
            } else {
                getView.txt_time.setText((startTime + " ~ " + endTime))
            }

            val totalSession = classInfo?.totalSessions
            var weeksday: String = ""
            var venue: String? = ""
            var sessions = StringBuilder()
            if (classInfo?.getClassSessions() != null && classInfo?.getClassSessions()?.size!! > 0) {
                weeksday = GeneralUtils.formatToWeeksday(classInfo?.getClassSessions()?.get(0)?.getStartTime())

                for (classSession in classInfo?.getClassSessions()!!) {
                    if (sessions.isNotEmpty()) {
                        sessions.append("\n")
                    }
                    sessions.append(GeneralUtils.formatDateToNumber(classSession.getStartTime()))
                }

                if (classInfo?.getClassSessions()?.size!! > 3) {
                    getView.txt_session_detail.maxLines = MAX_SESSION_LINE
                    getView.img_arrow.visibility = View.VISIBLE
                }

                if (sessions.isNotEmpty()) {
                    getView.txt_session_detail.text = sessions.toString()
                    getView.txt_session_detail.visibility = View.VISIBLE
                } else {
                    getView.txt_session_detail.visibility = View.GONE
                }

                if (!TextUtils.isEmpty(classInfo?.getClassSessions()?.get(0)?.getExternalVenue()))
                    venue = classInfo?.getClassSessions()?.get(0)?.getExternalVenue()
                else
                    venue = classInfo?.getClassSessions()?.get(0)?.getInternalVenue()
            }
            context?.let { context ->
                val contentSession = activity?.resources?.getString(R.string.sessions_on, totalSession, weeksday)
                val typeface = ResourcesCompat.getFont(context, R.font.opensans_italic)
                if (typeface?.style != null) {
                    val spannableString = SpannableString(contentSession)
                    spannableString.setSpan(StyleSpan(typeface.style), (contentSession?.indexOf(weeksday)
                            ?: 0) + weeksday.length + 1, contentSession?.length
                            ?: 0, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    getView.txt_session.text = spannableString
                } else {
                    getView.txt_session.text = contentSession
                }
            }
            getView.txt_venue.setText(venue)
            getView.txt_close_date.setText(GeneralUtils.formatToDate(classInfo?.getClosingDate()))
            getView.txt_maximum_par.text = classInfo?.getMaxVacancy().toString()

            var trainers: String? = ""
            if (classInfo?.getClassTrainers() != null && classInfo?.getClassTrainers()?.size!! > 0) {
                trainers = classInfo?.getClassTrainers()?.get(0)?.getTrainerName()
            }
            getView.txt_trainer_instructor.setText(trainers)

            getView.txt_course_description_label?.text = activity?.getString(R.string.course_description)
            getView.txt_course_description.text = classInfo?.getDecodedDescription()

            var classPrerequisites = ""
            if (classInfo?.getClassPrerequisites() != null && classInfo?.getClassPrerequisites()!!.isNotEmpty()) {
                for (i in classInfo?.getClassPrerequisites()!!) {
                    if (!i.courseTitle.isNullOrEmpty()) {
                        classPrerequisites += i.courseTitle + "\n"
                    }
                }
            }

            if (classPrerequisites.trim().isEmpty()) {
                classPrerequisites = getString(R.string.no_prerequisite)
            }

            getView.txt_prerequisite_course.text = classPrerequisites

            val language = classInfo?.getLanguage()
            if (!language.isNullOrEmpty()) {
                getView.tv_language.visibility = View.VISIBLE
                getView.iv_language.visibility = View.VISIBLE
                val displayLanguage = if (GeneralUtils.isIsoLanguage(classInfo!!.getLanguage())) {
                    Locale(language).displayLanguage
                } else {
                    language
                }
                getView.tv_language.text = displayLanguage
            }

            getView.txt_class_remarks_label.visibility = View.VISIBLE
            getView.txt_class_remarks.visibility = View.VISIBLE
            var classRemarks = classInfo?.classRemarks
            if (classRemarks.isNullOrEmpty()) {
                classRemarks = "N/A"
            }
            getView.txt_class_remarks.text = classRemarks

            getView.txt_class_requirement_label.visibility = View.VISIBLE
            getView.txt_class_requirement.visibility = View.VISIBLE
            var classRequirements = classInfo?.classRequirements
            if (classRequirements.isNullOrEmpty()) {
                classRequirements = "N/A"
            }
            getView.txt_class_requirement.text = classRequirements
        }

        checkFeeRecyclerMaxHeight()
    }

    private fun checkFeeRecyclerMaxHeight() {
        val viewObserver = getView.ll_price_container?.viewTreeObserver
        if (true == viewObserver?.isAlive) {
            viewObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    getView.ll_price_container?.viewTreeObserver?.removeOnGlobalLayoutListener(this)

                    val maxHeight = resources.getDimensionPixelSize(R.dimen.dp_260)
                    if ((getView.ll_price_container?.height
                                    ?: 0) > maxHeight) {
                        getView.ll_price_container?.layoutParams?.height = maxHeight
                    }
                }

            })
        }
    }

    override fun reloadDetails(classInfo: ClassInfo?) {
        if (classInfo?.getClassSessions() != null) {
            this.classInfo?.setClassSessions(classInfo.getClassSessions()!!)
        }
        if (classInfo?.getClassTrainers() != null) {
            this.classInfo?.setClassTrainers(classInfo.getClassTrainers()!!)
        }
        if (classInfo?.getClassPrerequisites() != null) {
            this.classInfo?.setClassPrerequisites(classInfo.getClassPrerequisites()!!)
        }

        bindData()
    }

    override fun handleCourseEvent(classInfo: ClassInfo, isAddCart: Boolean, isQuickBook: Boolean) {
        reloadVacancy(classInfo)

        if (isAddCart) {
            val cartItem = CartItem(UUID.randomUUID().toString(), classInfo, null, null, null, null, null, null)
            if (isQuickBook) {
                (activity as? MainActivity)?.doQuickBook(arrayOf(cartItem).toList())
            } else {
                (activity as? MainActivity)?.addToCart(arrayOf(cartItem).toList())
            }
        }
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

    private fun reloadVacancy(data: ClassInfo?) {
        var vacancy = "0"
        var maxVacancy = "0"
        if (data != null) {
            vacancy = data.getVacancies() ?: "0"
            maxVacancy = (data.getMaxVacancy() ?: 0).toString()
        }
        getView.txt_vacancy.text = vacancy
        getView.txt_maximum_par.text = maxVacancy
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBackgroundLine(R.drawable.bg_gradient_blue)
        (activity as? MainActivity)?.showProgressPayment(false)
        setTitle(classInfo?.getDecodedTitle().toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
        presenter = null
    }

    private fun changeSizeText(mode: Int) {
        when(mode) {
            ZOOM_IN -> {
                getView.tv_from.textSize = getTextSizeZoomIn(R.dimen.text_size_large)
                getView.txt_course_code.textSize = getTextSizeZoomIn(R.dimen.text_size_normal)
                getView.txt_course_name.textSize = getTextSizeZoomIn(R.dimen.text_size_large)
                getView.txt_organize_name.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.tv_language.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_course_description_label.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_course_description.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_trainer_instructor_label.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_trainer_instructor.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_prerequisite_course_label.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_prerequisite_course.textSize = getTextSizeZoomIn(R.dimen.text_size_small)

                getView.tv_price_member.textSize = getTextSizeZoomIn(R.dimen.text_size_xxhlarge)
                getView.txt_detail_amount_note.textSize = getTextSizeZoomIn(R.dimen.text_size_lmini)
                getView.tv_txn_pa.textSize = getTextSizeZoomIn(R.dimen.text_size_mini)

                getView.txt_vacancy.textSize = getTextSizeZoomIn(R.dimen.text_size_xxxlarge)
                getView.tv_available_slot.textSize = getTextSizeZoomIn(R.dimen.text_size_small)

                getView.tv_date_label.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_date.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_time_label.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_time.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_venue_label.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_venue.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_close_date_label.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_close_date.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_session_label.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_session.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.tv_material_label.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_material_fee.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.tv_material_fee_add.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.tv_class_size_label.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_maximum_par.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_session_detail.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_class_remarks.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_class_remarks_label.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_class_requirement.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
                getView.txt_class_requirement_label.textSize = getTextSizeZoomIn(R.dimen.text_size_small)
            }
            ZOOM_NORMAL -> {
                getView.tv_from.textSize = context?.resources?.getDimension(R.dimen.text_size_xlarge) ?: 0f
                getView.txt_course_code.textSize = context?.resources?.getDimension(R.dimen.text_size_normal)?: 0f
                getView.txt_course_name.textSize = context?.resources?.getDimension(R.dimen.text_size_large)?: 0f
                getView.txt_organize_name.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.tv_language.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_course_description_label.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_course_description.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_trainer_instructor_label.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_trainer_instructor.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_prerequisite_course_label.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_prerequisite_course.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f

                getView.tv_price_member.textSize = context?.resources?.getDimension(R.dimen.text_size_xxhlarge)?: 0f
                getView.txt_detail_amount_note.textSize = context?.resources?.getDimension(R.dimen.text_size_lmini)?: 0f
                getView.tv_txn_pa.textSize = context?.resources?.getDimension(R.dimen.text_size_mini)?: 0f

                getView.txt_vacancy.textSize = context?.resources?.getDimension(R.dimen.text_size_xxxlarge)?: 0f
                getView.tv_available_slot.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f

                getView.tv_date_label.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_date.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_time_label.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_time.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_venue_label.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_venue.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_close_date_label.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_close_date.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_session_label.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_session.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.tv_material_label.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_material_fee.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.tv_material_fee_add.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.tv_class_size_label.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_maximum_par.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_session_detail.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_class_remarks.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_class_remarks_label.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_class_requirement.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
                getView.txt_class_requirement_label.textSize = context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
            }
            ZOOM_OUT -> {
                getView.tv_from.textSize = getTextSizeZoomOut(R.dimen.text_size_large)
                getView.txt_course_code.textSize = getTextSizeZoomOut(R.dimen.text_size_normal)
                getView.txt_course_name.textSize = getTextSizeZoomOut(R.dimen.text_size_large)
                getView.txt_organize_name.textSize =getTextSizeZoomOut(R.dimen.text_size_small)
                getView.tv_language.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_course_description_label.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_course_description.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_trainer_instructor_label.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_trainer_instructor.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_prerequisite_course_label.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_prerequisite_course.textSize = getTextSizeZoomOut(R.dimen.text_size_small)

                getView.tv_price_member.textSize = getTextSizeZoomOut(R.dimen.text_size_xxhlarge)
                getView.txt_detail_amount_note.textSize = getTextSizeZoomOut(R.dimen.text_size_lmini)
                getView.tv_txn_pa.textSize = getTextSizeZoomOut(R.dimen.text_size_mini)

                getView.txt_vacancy.textSize = getTextSizeZoomOut(R.dimen.text_size_xxxlarge)
                getView.tv_available_slot.textSize = getTextSizeZoomOut(R.dimen.text_size_small)

                getView.tv_date_label.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_date.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_time_label.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_time.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_venue_label.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_venue.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_close_date_label.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_close_date.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_session_label.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_session.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.tv_material_label.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_material_fee.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.tv_material_fee_add.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.tv_class_size_label.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_maximum_par.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_session_detail.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_class_remarks.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_class_remarks_label.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_class_requirement.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
                getView.txt_class_remarks_label.textSize = getTextSizeZoomOut(R.dimen.text_size_small)
            }
        }
    }

    private fun getTextSizeZoomIn(size: Int): Float {
        when(size) {
            R.dimen.text_size_small -> return context?.resources?.getDimension(R.dimen.text_size_mini)?: 0f
            R.dimen.text_size_normal -> return context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
            R.dimen.text_size_large -> return context?.resources?.getDimension(R.dimen.text_size_medium)?: 0f
            R.dimen.text_size_xxhlarge -> return context?.resources?.getDimension(R.dimen.text_size_xhlarge)?: 0f
            R.dimen.text_size_lmini -> return context?.resources?.getDimension(R.dimen.text_size_mmini)?: 0f
            R.dimen.text_size_mini -> return context?.resources?.getDimension(R.dimen.text_size_lmini)?: 0f
            R.dimen.text_size_xxxlarge -> return context?.resources?.getDimension(R.dimen.text_size_xxlarge)?: 0f
        }
        return context?.resources?.getDimension(R.dimen.text_size_medium)?: 0f
    }

    private fun getTextSizeZoomOut(size: Int): Float {
        when(size) {
            R.dimen.text_size_small -> return context?.resources?.getDimension(R.dimen.text_size_normal)?: 0f
            R.dimen.text_size_normal -> return context?.resources?.getDimension(R.dimen.text_size_xnormal)?: 0f
            R.dimen.text_size_large -> return context?.resources?.getDimension(R.dimen.text_size_xlarge)?: 0f
            R.dimen.text_size_xxhlarge -> return context?.resources?.getDimension(R.dimen.text_size_xxxhlarge)?: 0f
            R.dimen.text_size_lmini -> return context?.resources?.getDimension(R.dimen.text_size_mini)?: 0f
            R.dimen.text_size_mini -> return context?.resources?.getDimension(R.dimen.text_size_small)?: 0f
            R.dimen.text_size_xxxlarge -> return context?.resources?.getDimension(R.dimen.text_size_hlarge)?: 0f
        }
        return context?.resources?.getDimension(R.dimen.text_size_large)?: 0f
    }

    override fun setCanOrder(isAlive: Boolean) {
        getView.btn_book_now.isEnabled = isAlive
        getView.btn_add_to_card.isEnabled = isAlive
        getView.img_add_cart.isEnabled = isAlive
        if (activity != null) {
            if (isAlive) {
                getView.txt_add_to_cart.setTextColor(ContextCompat.getColor(activity!!, R.color.red_color_3))
            } else {
                getView.txt_add_to_cart.setTextColor(ContextCompat.getColor(activity!!, R.color.gray_color_1))
            }
        }
    }

}
