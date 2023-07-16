package com.styl.pa.modules.declaration.view

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.entities.product.Product
import com.styl.pa.modules.base.CustomBaseDialogFragment
import com.styl.pa.modules.declaration.DeclarationContract
import com.styl.pa.modules.declaration.presenter.DeclarationPresenter
import com.styl.pa.utils.LogManager
import kotlinx.android.synthetic.main.fragment_declaration.view.*

class DeclarationFragment : CustomBaseDialogFragment(), DeclarationContract.View,
    View.OnClickListener {
    companion object {
        private const val ARG_QUICK_BOOK_PRODUCT_TYPE =
            BuildConfig.APPLICATION_ID + ".args.ARG_QUICK_BOOK_PRODUCT_TYPE"

        fun newInstance(quickBookProductType: String?) = DeclarationFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_QUICK_BOOK_PRODUCT_TYPE, quickBookProductType)
            }
        }
    }

    private var getView: View? = null
    private var presenter: DeclarationContract.Presenter? = DeclarationPresenter(this)
    private var quickBookProductType = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        getView = activity?.layoutInflater?.inflate(R.layout.fragment_declaration, null)
        onSetEventDismissDialog(this)

        this.getBundle()
        this.init()

        val dialog = AlertDialog.Builder(activity)
            .setView(getView)
            .create()
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    private fun getBundle() {
        quickBookProductType = arguments?.getString(ARG_QUICK_BOOK_PRODUCT_TYPE) ?: ""
        LogManager.d("DeclarationFragment: quickBookProductType = $quickBookProductType")
    }

    private fun init() {
        getView?.iv_close?.setOnClickListener(this)
        getView?.btn_ok?.setOnClickListener(this)

        val termAndConditionStr = getString(R.string.term_and_conditions)

        val textCbFacility1 = getString(R.string.declaration_facility)
        val startIndexCbFacility1 = textCbFacility1.indexOf(termAndConditionStr)
        val endIndexCbFacility1 = startIndexCbFacility1 + termAndConditionStr.length
        val textToShow = SpannableStringBuilder(textCbFacility1)
        textToShow.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.light_orange)),
            startIndexCbFacility1, // start
            endIndexCbFacility1, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        getView?.cb_facility_1?.text = textToShow

        val textCbIg1 = getString(R.string.declaration_ig_1)
        val startIndexCbIg1 = textCbIg1.indexOf(termAndConditionStr)
        val endIndexCbIg1 = startIndexCbIg1 + termAndConditionStr.length
        val textToShowCbIg1 = SpannableStringBuilder(textCbIg1)
        textToShowCbIg1.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.light_orange)),
            startIndexCbIg1, // start
            endIndexCbIg1, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        getView?.cb_ig_1?.text = textToShowCbIg1

        this.visibleLayout(quickBookProductType)

    }

    private fun visibleLayout(productType: String) {
        when (productType) {
            Product.PRODUCT_FACILITY -> {
                getView?.ll_for_facility?.visibility = View.VISIBLE
                getView?.ll_for_ig?.visibility = View.GONE
            }
            else -> {
                getView?.ll_for_facility?.visibility = View.GONE
                getView?.ll_for_ig?.visibility = View.VISIBLE
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.iv_close -> {
                dismiss()
            }
            R.id.btn_ok -> {
                val isChecked = isAllowCheckout()
                this.presenter?.navigateToCheckout(isChecked)
            }
        }
    }

    private fun isAllowCheckout(): Boolean {
        return when (quickBookProductType) {
            Product.PRODUCT_FACILITY -> {
                getView?.cb_facility_1?.isChecked ?: false
            }
            else -> {
                (getView?.cb_ig_1?.isChecked ?: false) && (getView?.cb_ig_2?.isChecked ?: false)
            }
        }
    }
}