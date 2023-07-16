package com.styl.pa.modules.addParticipantEvent.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.customViews.datePicker.CustomDatePicker
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.event.EventParticipant
import com.styl.pa.entities.event.FieldObject
import com.styl.pa.entities.event.Fields
import com.styl.pa.enums.EventFormDataFieldType
import com.styl.pa.modules.addParticipantEvent.AddParticipantEventContract
import com.styl.pa.modules.addParticipantEvent.presenter.AddParticipantEventPresenter
import com.styl.pa.modules.base.CustomBaseDialogFragment
import com.styl.pa.utils.DateUtils
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.LogManager
import kotlinx.android.synthetic.main.fragment_add_participant_event.view.*
import org.json.JSONObject
import java.util.*


class AddParticipantEventFragment : CustomBaseDialogFragment(), AddParticipantEventContract.View,
    View.OnClickListener {

    companion object {
        const val ADD_PARTICIPANT_REQUEST_CODE = 333
        const val ARG_EVENT_INFO = BuildConfig.APPLICATION_ID + "args.ARG_EVENT_INFO"
        const val ARG_CART_ITEM_POSITION =
            BuildConfig.APPLICATION_ID + "args.ARG_CART_ITEM_POSITION"
        const val ARG_EVENT_TICKET_POSITION =
            BuildConfig.APPLICATION_ID + "args.ARG_EVENT_TICKET_POSITION"
        const val ARG_TICKET_ENTITY_POSITION =
            BuildConfig.APPLICATION_ID + "args.ARG_TICKET_ENTITY_POSITION"
        const val ARG_PARTICIPANT_POSITION =
            BuildConfig.APPLICATION_ID + "args.ARG_PARTICIPANT_POSITION"
        const val ARG_PARTICIPANT_INFO =
            BuildConfig.APPLICATION_ID + "args.ARG_PARTICIPANT_INFO"

        fun newInstance(
            eventInfo: EventInfo?,
            cartItemPosition: Int,
            eventTicketPosition: Int,
            ticketEntityPosition: Int,
            participantPosition: Int
        ) = AddParticipantEventFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_EVENT_INFO, eventInfo)
                putInt(ARG_CART_ITEM_POSITION, cartItemPosition)
                putInt(ARG_EVENT_TICKET_POSITION, eventTicketPosition)
                putInt(ARG_TICKET_ENTITY_POSITION, ticketEntityPosition)
                putInt(ARG_PARTICIPANT_POSITION, participantPosition)
            }
        }

        fun getListFieldObjectEventForm(jsonStr: String?): ArrayList<FieldObject> {
            val result: ArrayList<FieldObject> = ArrayList()
            if (jsonStr.isNullOrEmpty()) {
                return result
            }
            val jsonObj: JSONObject = JSONObject(jsonStr)
            val fieldSections = jsonObj.getJSONObject("FieldSections")
            val page1 = fieldSections.getJSONObject("Page 1")
            val fieldsObj = page1.getJSONObject("Fields")
            for (key in fieldsObj.keys()) {
                val value = fieldsObj.getJSONObject(key)
                LogManager.d("JsonObject: key = $key, value = $value")
                val fieldObject = GeneralUtils.convertStringToObject<FieldObject>(value.toString())
                if (fieldObject != null && !key.equals("lastname")) {
                    result.add(fieldObject)
                }
            }


            return result
        }
    }

    private var getView: View? = null
    private var presenter: AddParticipantEventContract.Presenter? =
        AddParticipantEventPresenter(this)
    private var eventInfo: EventInfo? = null
    private var cartItemPosition: Int = -1
    private var eventTicketPosition: Int = -1
    private var ticketEntityPosition: Int = -1
    private var participantPosition: Int = -1
    private var fieldsFromFormData: Fields? = null
    private var eventParticipant: EventParticipant? = null
    private val listFieldObjectNotDefined: ArrayList<FieldObject> = ArrayList()
    private val listErrorObjectNotDefined: ArrayList<String> = ArrayList()
    private var isObjectNotDefinedHasError = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        getView = activity?.layoutInflater?.inflate(
            R.layout.fragment_add_participant_event,
            null
        )
        onSetEventDismissDialog(this)

        this.getBundle()
        this.init()

        val dialog = AlertDialog.Builder(activity)
            .setView(getView)
            .create()
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        return dialog
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private fun getBundle() {
        this.eventInfo = arguments?.getParcelable(ARG_EVENT_INFO)
        this.cartItemPosition = arguments?.getInt(ARG_CART_ITEM_POSITION, -1) ?: -1
        this.eventTicketPosition = arguments?.getInt(ARG_EVENT_TICKET_POSITION, -1) ?: -1
        this.ticketEntityPosition = arguments?.getInt(ARG_TICKET_ENTITY_POSITION, -1) ?: -1
        this.participantPosition = arguments?.getInt(ARG_PARTICIPANT_POSITION, -1) ?: -1
    }

    private fun init() {
        var participantInfo: Fields? = null
        if (eventTicketPosition >= 0 && ticketEntityPosition >= 0 && participantPosition >= 0) {
            eventParticipant = eventInfo?.listSelectedTicket?.get(eventTicketPosition)
                ?.listTicketParticipantEntity?.get(ticketEntityPosition)
                ?.listParticipant?.get(participantPosition)
            getView?.tv_participant_name?.text = eventParticipant?.nameToShow ?: ""
            participantInfo = eventParticipant?.participantInfo
        }


        getView?.btn_cancel?.setOnClickListener(this)
        getView?.btn_save?.setOnClickListener(this)


        listFieldObjectNotDefined.clear()
        if (eventInfo != null) {
            LogManager.d("AddParticipantEventFragment: init : getEventFormDataFromHTML = ${eventInfo?.getEventFormDataFromHTML()}")

            //get list object not defined
            listFieldObjectNotDefined.addAll(participantInfo?.listNotDefined ?: ArrayList())
            LogManager.d("AddParticipantEventFragment: listFieldObjectNotDefined size = ${listFieldObjectNotDefined.size}")
            fieldsFromFormData = participantInfo ?: Fields()
        }

        //init list not defined
        this.initListNotDefined()
    }

    private fun initListNotDefined() {
        for (obj in listFieldObjectNotDefined) {
            if (obj.itemName?.equals("lastname") == true) {
                continue
            }
            //linear object
            val llObject = LinearLayout(context)
            llObject.orientation = LinearLayout.VERTICAL
            val layoutObjectParams =
                LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            layoutObjectParams.bottomMargin = 10
            llObject.layoutParams = layoutObjectParams

            //linear header
            val llHeader = LinearLayout(context)
            llHeader.orientation = LinearLayout.HORIZONTAL
            val layoutHeaderParams =
                LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            llHeader.layoutParams = layoutHeaderParams
            //text view header name
            val textViewHeaderName = TextView(context)
            val tvHeaderNameLayoutParams =
                LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            textViewHeaderName.layoutParams = tvHeaderNameLayoutParams
            textViewHeaderName.typeface =
                context?.let { ResourcesCompat.getFont(it, R.font.opensans_bold) }
            textViewHeaderName.setTextColor(resources.getColor(R.color.black_color_2, null))
            textViewHeaderName.textSize = resources.getDimension(R.dimen.text_size_xsmall)
            textViewHeaderName.text = obj.title ?: ""
            llHeader.addView(textViewHeaderName)

            // text view icon
            if (obj.validation?.required == true) {
                val tvIconRequired = TextView(context)
                val layoutParamIcon =
                    LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                layoutParamIcon.gravity = Gravity.CLIP_VERTICAL
                layoutParamIcon.marginStart = 10
                tvIconRequired.layoutParams = layoutParamIcon
                tvIconRequired.typeface =
                    context?.let { ResourcesCompat.getFont(it, R.font.opensans_bold) }
                tvIconRequired.setTextColor(resources.getColor(R.color.black_color_2, null))
                tvIconRequired.textSize = resources.getDimension(R.dimen.text_size_xsmall)
                tvIconRequired.text = getString(R.string.asterisk)
                llHeader.addView(tvIconRequired)
            }
            llObject.addView(llHeader)

            //text input layout
            val textInputLayout = context?.let { TextInputLayout(it) }
            val tlTag = "tl_" + obj.itemName
            textInputLayout?.tag = tlTag
            val layoutParamsTL =
                LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            textInputLayout?.layoutParams = layoutParamsTL
            textInputLayout?.isErrorEnabled = true
            textInputLayout?.isHintEnabled = false

            when (obj.type) {
                EventFormDataFieldType.DROP_DOWN.value -> {
                    val spinner = Spinner(context)
                    val tag = "spn_" + obj.itemName
                    spinner.tag = tag
                    val layoutParams = LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                    )
                    spinner.layoutParams = layoutParams
                    spinner.minimumHeight = 53
                    spinner.setPadding(0, 0, 0, 3)
                    spinner.setBackgroundResource(R.drawable.bg_spinner)

                    val arrayString = arrayOf("")
                    if (obj.item != null && (obj.item?.size ?: 0) > 0) {
                        obj.item?.forEach {
                            if (!it.value.isNullOrEmpty()) {
                                arrayString.plus(it.value ?: "")
                            }
                        }
                    }
                    val spinnerAdapter = context?.let {
                        ArrayAdapter<String>(
                            it,
                            R.layout.spinner_proximity_item,
                            arrayString
                        )
                    }
                    spinnerAdapter?.setDropDownViewResource(R.layout.spinner_proximity_dropdown_item)
                    spinner.adapter = spinnerAdapter
                    spinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                p0: AdapterView<*>?,
                                p1: View?,
                                p2: Int,
                                p3: Long
                            ) {
                                if (p2 > 0 && arrayString.isNotEmpty() && p2 < arrayString.size) {
                                    LogManager.d("item ${obj.itemName} spinner onItemSelected:  $p2 = ${arrayString[p2]}")
                                }
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {
                                LogManager.d("item ${obj.itemName} spinner onNothingSelected")
                            }
                        }

                    textInputLayout?.addView(spinner)
                    llObject.addView(textInputLayout)
                }
                EventFormDataFieldType.CHECKBOX.value,
                EventFormDataFieldType.RADIO.value -> {
                    val radioGroup = RadioGroup(context)
                    val tagRg = "rg_" + obj.itemName
                    radioGroup.tag = tagRg
                    val rgLayoutParams = LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                    )
                    radioGroup.layoutParams = rgLayoutParams
                    radioGroup.orientation = RadioGroup.VERTICAL
                    obj.item?.forEach {
                        val radioButton = RadioButton(context)
                        val tag = "rb_" + obj.itemName + "_" + it.value
                        radioButton.tag = tag
                        val rbLayoutParams = LinearLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT
                        )
                        radioButton.layoutParams = rbLayoutParams
                        radioButton.setPadding(20, 0, 20, 0)
                        radioButton.buttonDrawable = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.button_radio_red,
                            null
                        )
                        radioButton.typeface =
                            context?.let { ResourcesCompat.getFont(it, R.font.opensans_regular) }
                        radioButton.setTextColor(resources.getColor(R.color.black_color_2, null))
                        radioButton.textSize = resources.getDimension(R.dimen.text_size_xsmall)
                        radioButton.text = it.value
                        if (it.value == obj.inputValue) {
                            radioButton.isChecked = true
                        }

                        radioGroup.addView(radioButton)
                    }
                    textInputLayout?.addView(radioGroup)
                    llObject.addView(textInputLayout)


                }
                EventFormDataFieldType.DATE.value -> {
                    val textView = context?.let { TextInputEditText(it) }
                    val tag = "tv_" + obj.itemName
                    if (textView != null){
                        textView.tag = tag
                        val edtLayoutParams = LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT
                        )
                        textView.setPadding(13, 13, 13, 13)
                        textView.layoutParams = edtLayoutParams
                        textView.background = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.border_corner_black_transparent,
                            null
                        )
                        textView.typeface =
                            context?.let { ResourcesCompat.getFont(it, R.font.opensans_regular) }
                        textView.setTextColor(resources.getColor(R.color.black_color_2, null))
                        textView.textSize = resources.getDimension(R.dimen.text_size_xsmall)
                        textView.setText((obj.inputValue ?: "").toString())
                        textView.isFocusable = false
                        textView.isFocusableInTouchMode = false

                        val datePickerListener =
                            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                                val selectedCalendar = Calendar.getInstance()
                                selectedCalendar.set(year, month, dayOfMonth, 0, 0, 0)
                                selectedCalendar.set(Calendar.MILLISECOND, 0)
                                val stringShow = DateUtils.convertLongToString(
                                    selectedCalendar.timeInMillis,
                                    "yyyy-MM-dd"
                                )
                                textView.setText(stringShow)
                            }

                        val datePickerDialogUtils = CustomDatePicker(
                            activity, R.style.CustomDatePickerStyle, datePickerListener,
                            Calendar.getInstance().get(Calendar.YEAR),
                            Calendar.getInstance().get(Calendar.MONTH),
                            Calendar.getInstance().get(Calendar.DATE)
                        )
                        datePickerDialogUtils.setCurrentSelection(Calendar.getInstance())

                        textView.setOnClickListener {
                            datePickerDialogUtils.show()
                        }

                        textInputLayout?.addView(textView)
                        llObject.addView(textInputLayout)
                    }
                }
                EventFormDataFieldType.TIME.value -> {
                    val textView = context?.let { TextInputEditText(it) }
                    val tag = "tv_" + obj.itemName
                    if (textView != null){
                        textView.tag = tag
                        val edtLayoutParams = LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT
                        )
                        textView.setPadding(13, 13, 13, 13)
                        edtLayoutParams.setMargins(0,0,0,0)
                        textView.layoutParams = edtLayoutParams
                        textView.background = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.border_corner_black_transparent,
                            null
                        )
                        textView.typeface =
                            context?.let { ResourcesCompat.getFont(it, R.font.opensans_regular) }
                        textView.setTextColor(resources.getColor(R.color.black_color_2, null))
                        textView.textSize = resources.getDimension(R.dimen.text_size_xsmall)
                        textView.setText((obj.inputValue ?: "").toString())
                        textView.isFocusable = false
                        textView.isFocusableInTouchMode = false
                        val timePickerListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                            val stringToShow = "$hourOfDay:${String.format("%02d", minute)}"
                            textView.setText(stringToShow)
                        }

                        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                        val minute = Calendar.getInstance().get(Calendar.MINUTE)

                        val timePickerDialog = TimePickerDialog(
                            context, timePickerListener, hour, minute, true
                        )

                        timePickerDialog.setTitle(R.string.select_time_title)
                        textView.setOnClickListener {
                            timePickerDialog.show()
                        }

                        textInputLayout?.addView(textView)
                        llObject.addView(textInputLayout)
                    }
                }
                else -> {
                    //text input edit text
                    val textInputEditText = context?.let { TextInputEditText(it) }
                    val edtTag = "edt_" + obj.itemName
                    textInputEditText?.tag = edtTag
                    val edtLayoutParams = LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                    )
                    textInputEditText?.setPadding(13, 13, 13, 13)
                    textInputEditText?.layoutParams = edtLayoutParams
                    textInputEditText?.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.border_corner_black_transparent,
                        null
                    )
                    textInputEditText?.typeface =
                        context?.let { ResourcesCompat.getFont(it, R.font.opensans_regular) }
                    textInputEditText?.maxLines = 1
                    textInputEditText?.setTextColor(resources.getColor(R.color.black_color_2, null))
                    textInputEditText?.textSize = resources.getDimension(R.dimen.text_size_xsmall)
                    textInputEditText?.setText((obj.inputValue ?: "").toString())
                    if (obj.type == "number" || obj.type == "phone") {
                        textInputEditText?.inputType = InputType.TYPE_CLASS_NUMBER
                    } else {
                        textInputEditText?.inputType = InputType.TYPE_CLASS_TEXT
                    }
                    textInputEditText?.imeOptions = EditorInfo.IME_ACTION_DONE
                    if (textInputEditText != null) {
                        textInputLayout?.addView(textInputEditText)
                    }
                    llObject.addView(textInputLayout)
                }
            }

            if ((listFieldObjectNotDefined.indexOf(obj) % 2) == 0) {
                getView?.layout_content_left?.addView(llObject)
            } else {
                getView?.layout_content_right?.addView(llObject)
            }
        }
    }


    override fun onClick(p0: View?) {
        GeneralUtils.hideSoftKeyboard(p0)
        when (p0?.id) {
            R.id.btn_cancel -> {
                dismiss()
            }
            R.id.btn_save -> {
                validateBeforeSave()
            }
        }
    }

    private fun validateBeforeSave() {
        //validate list not defined
        listErrorObjectNotDefined.clear()
        isObjectNotDefinedHasError = false
        for (obj in listFieldObjectNotDefined) {
            LogManager.d("AddParticipantEventFragment: Validate object not defined: itemName = ${obj.itemName}")
            var errorStr = ""

            when (obj.type) {
                EventFormDataFieldType.DROP_DOWN.value -> {
                    val tag = "spn_" + obj.itemName
                    val spinner = getView?.findViewWithTag<Spinner>(tag)
                    val inputStr = (spinner?.selectedItem ?: "").toString()
                    LogManager.d("AddParticipantEventFragment: validate dropdown: value = $inputStr")
                    if (obj.validation?.required == true && inputStr.isEmpty()) {
                        errorStr = obj.validation?.errorMessage ?: ""
                        isObjectNotDefinedHasError = true
                    }
                }
                EventFormDataFieldType.CHECKBOX.value,
                EventFormDataFieldType.RADIO.value -> {
                    var inputStr = ""
                    obj.item?.forEach {
                        val tag = "rb_" + obj.itemName + "_" + it.value
                        val rb = getView?.findViewWithTag<RadioButton>(tag)
                        if (rb?.isChecked == true) {
                            inputStr = rb.text.toString()
                        }
                    }
                    if (obj.validation?.required == true && inputStr.isEmpty()) {
                        errorStr = obj.validation?.errorMessage ?: ""
                        isObjectNotDefinedHasError = true
                    }
                }
                EventFormDataFieldType.DATE.value,
                EventFormDataFieldType.TIME.value -> {
                    val tag = "tv_" + obj.itemName
                    val tv = getView?.findViewWithTag<TextView>(tag)
                    val inputStr = (tv?.text ?: "").toString()
                    LogManager.d("check valid: $tag, input string = $inputStr")
                    if (obj.validation?.required == true && inputStr.isEmpty()) {
                        errorStr = obj.validation?.errorMessage ?: ""
                        isObjectNotDefinedHasError = true
                    }
                }
                else -> {
                    val edtTag = "edt_" + obj.itemName
                    val edt = getView?.findViewWithTag<TextInputEditText>(edtTag)
                    if (edt != null) {
                        val inputStr = edt.text ?: ""
                        LogManager.d("check valid: $tag, input string = $inputStr")
                        if (obj.validation?.required == true && inputStr.isEmpty()) {
                            errorStr = obj.validation?.errorMessage ?: ""
                            isObjectNotDefinedHasError = true
                        }

                        val regex = obj.validation?.regex ?: ""
                        LogManager.d("check valid: $tag, regex = $regex")
                        if (inputStr.isNotEmpty() && regex.isNotEmpty() && !inputStr.matches(
                                Regex(
                                    regex
                                )
                            )
                        ) {
                            LogManager.d("AddParticipantEventFragment: Error Regex")
                            errorStr = obj.validation?.validationErrorMessage ?: ""
                            isObjectNotDefinedHasError = true
                        }

                        if (inputStr.isNotEmpty() && obj.type == "number") {
                            val min =
                                (if (obj.validation?.min.isNullOrEmpty()) "-1" else (obj.validation?.min
                                    ?: "-1")).toInt()
                            val max =
                                (if (obj.validation?.max.isNullOrEmpty()) "-1" else (obj.validation?.max
                                    ?: "-1")).toInt()
                            val inputNumber = inputStr.toString().toInt()
                            LogManager.d("AddParticipantEventFragment: Min Max: min = $min, max = $max, inputNumber = $inputNumber")
                            if ((min > -1 && inputNumber < min) || (max > -1 && inputNumber > max)) {
                                LogManager.d("AddParticipantEventFragment: error Min Max: min = $min, max = $max, inputNumber = $inputNumber")
                                errorStr = obj.validation?.validationErrorMessage ?: ""
                                isObjectNotDefinedHasError = true
                            }
                        }

                    }
                }

            }


            listErrorObjectNotDefined.add(errorStr)
        }

        if (!isObjectNotDefinedHasError) {
            this.saveAndBackToCart()
        } else {
            if (listFieldObjectNotDefined.size > 0) {
                for (i in 0 until listFieldObjectNotDefined.size) {
                    val obj = listFieldObjectNotDefined[i]
                    val tlTag = "tl_" + obj.itemName
                    val textInputLayout = getView?.findViewWithTag<TextInputLayout>(tlTag)
                    if (isObjectNotDefinedHasError) {
                        val error = listErrorObjectNotDefined[i]
                        if (error.isNotEmpty()) {
                            textInputLayout?.error = error
                        } else {
                            textInputLayout?.error = null
                        }
                    } else {
                        textInputLayout?.error = null
                    }
                }
            }
        }
    }

    private fun saveAndBackToCart() {
        for (obj in listFieldObjectNotDefined) {
            var inputStr = ""
            when (obj.type) {
                EventFormDataFieldType.DROP_DOWN.value -> {
                    val tag = "spn_" + obj.itemName
                    val spinner = getView?.findViewWithTag<Spinner>(tag)
                    inputStr = (spinner?.selectedItem ?: "").toString()
                }
                EventFormDataFieldType.CHECKBOX.value,
                EventFormDataFieldType.RADIO.value -> {
                    inputStr = ""
                    obj.item?.forEach {
                        val tag = "rb_" + obj.itemName + "_" + it.value
                        val rb = getView?.findViewWithTag<RadioButton>(tag)
                        if (rb?.isChecked == true) {
                            inputStr = rb.text.toString()
                        }
                    }
                }
                EventFormDataFieldType.DATE.value,
                EventFormDataFieldType.TIME.value -> {
                    val tag = "tv_" + obj.itemName
                    val tv = getView?.findViewWithTag<TextView>(tag)
                    inputStr = (tv?.text ?: "").toString()
                }
                else -> {
                    val edtTag = "edt_" + obj.itemName
                    val edt = getView?.findViewWithTag<TextInputEditText>(edtTag)
                    inputStr = (edt?.text ?: "").toString()
                }

            }

            obj.inputValue = inputStr
        }

        fieldsFromFormData?.listNotDefined = listFieldObjectNotDefined

        this.presenter?.navigateToCart(
            requestCode = ADD_PARTICIPANT_REQUEST_CODE,
            cartItemPosition = cartItemPosition,
            eventTicketPosition = eventTicketPosition,
            ticketEntityPosition = ticketEntityPosition,
            participantPosition = participantPosition,
            participantInfo = fieldsFromFormData
        )
    }
}