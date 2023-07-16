package com.styl.pa.modules.promo.view

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.SystemClock
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.adapters.PromoCodeAdapter
import com.styl.pa.entities.promocode.PromoCode
import com.styl.pa.enums.TrackingName
import com.styl.pa.interfaces.OnItemSelectedListener
import com.styl.pa.modules.base.BaseDialogFragment
import com.styl.pa.modules.checkout.view.CheckoutFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.promo.IPromoCodeContact
import com.styl.pa.modules.promo.presenter.PromoCodePresenter
import kotlinx.android.synthetic.main.fragment_promo_code.view.*


class PromoCodeFragment : BaseDialogFragment(), View.OnClickListener, IPromoCodeContact.IView {

    override fun setCurrentName() {
        super.setCurrentName()
        if (activity != null) {
            (activity as MainActivity).setCurrentViewName(TrackingName.PromoCodeFragment.value)
        }
    }

    private var dialogView: View? = null

    private var promoCodeList: ArrayList<PromoCode>? = null

    private var promoCode: PromoCode? = null

    private var presenter: PromoCodePresenter? = null

    private var listener = object : OnItemSelectedListener {
        override fun onItemSelected(position: Int) {
            touchListener()
            val promoCodeListSize =  promoCodeList?.size ?: -1
            if (position in 0 until promoCodeListSize) {
                promoCode = promoCodeList?.get(position)
                dialogView?.btn_apply_promo?.isEnabled = true
            }
        }

    }

    private var onClickListener = object : PromoCodeAdapter.OnClickListener {
        override fun onReadTnCClicked() {
            touchListener()
            presenter?.navigateToTermAndCondition(promoCode?.termsAndConditionsLink)
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = activity!!.layoutInflater.inflate(R.layout.fragment_promo_code, null)

        presenter = PromoCodePresenter(this)

        onSetEventDismissDialog(this)

        getBundle()

        initView()

        isCancelable = false

        val dialog = AlertDialog.Builder(activity)
            .setView(dialogView)
            .create()
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    companion object {

        const val ARG_PROMO_CODE_LIST = BuildConfig.APPLICATION_ID + ".args.ARG_PROMO_CODE_LIST"
        const val ARG_SELECTED_PROMO_CODE = BuildConfig.APPLICATION_ID + ".args.ARG_SELECTED_PROMO_CODE"

        @JvmStatic
        fun newInstance(promoCodeList: ArrayList<PromoCode>?) =
            PromoCodeFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_PROMO_CODE_LIST, promoCodeList)
                }
            }
    }

    private fun getBundle() {
        promoCodeList = arguments?.getParcelableArrayList(ARG_PROMO_CODE_LIST)
    }

    override fun onClick(v: View?) {
        touchListener()
        if (SystemClock.elapsedRealtime() - MainActivity.globalLastClickTime < 2000) {
            return
        }
        MainActivity.globalLastClickTime = SystemClock.elapsedRealtime()
        when (v?.id) {
            R.id.btn_apply_promo -> {
                val intent = Intent()
                intent.putExtra(ARG_SELECTED_PROMO_CODE, promoCode?.code)
                targetFragment?.onActivityResult(CheckoutFragment.SELECT_PROMO_CODE_REQUEST_CODE, Activity.RESULT_OK, intent)
                dismiss()
            }
            R.id.btn_cancel_promo -> {
                dismiss()
            }
            else -> return
        }
    }

    private fun initView() {
        if (promoCodeList != null) {
            val promoCodeAdapter = PromoCodeAdapter(promoCodeList!!, requireActivity())
            val params = dialogView?.rcv_list_promo_code?.layoutParams
            params?.apply {
                width = context?.resources?.displayMetrics?.widthPixels ?: 0
                height = context?.resources?.displayMetrics?.heightPixels ?: 0
            }
            dialogView?.rcv_list_promo_code?.layoutParams = params
            dialogView?.rcv_list_promo_code?.layoutManager = GridLayoutManager(requireContext(), 1)
            dialogView?.rcv_list_promo_code?.adapter = promoCodeAdapter
            promoCodeAdapter.listener = this.listener
            promoCodeAdapter.onClickListener = this.onClickListener
        }

        dialogView?.btn_apply_promo?.setOnClickListener(this)
        dialogView?.btn_apply_promo?.isEnabled = false
        dialogView?.btn_cancel_promo?.setOnClickListener(this)
    }
}