package com.styl.pa.modules.promo.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.modules.base.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_promo_code_tnc.view.*
import android.webkit.WebView
import com.styl.pa.utils.LogManager

class PromoCodeTnCFragment : BaseDialogFragment(), View.OnClickListener {

    companion object {

        private const val DEFAULT_URL = "https://www.onepa.gov.sg/terms-and-conditions"

        private const val ARG_URL = BuildConfig.APPLICATION_ID + ".args.ARG_URL"

        fun newInstance(rulesUrl: String): PromoCodeTnCFragment {
            val f = PromoCodeTnCFragment()
            val args = Bundle()
            args.putString(ARG_URL, rulesUrl)
            f.arguments = args
            return f
        }
    }

    private var dialogView: View? = null

    private var termAndConditionUrl: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = activity!!.layoutInflater.inflate(R.layout.fragment_promo_code_tnc, null)

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

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {

        dialogView?.btn_close?.setOnClickListener(this)

        dialogView?.view_bg?.visibility = View.VISIBLE
        dialogView?.pb_loading?.visibility = View.VISIBLE
        dialogView?.wv_tnc?.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)
        dialogView?.wv_tnc?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                LogManager.i("onPageFinished $url")
                Handler().postDelayed({
                    view?.loadUrl(
                        "javascript:(function() { " +
                                "  const list = document.getElementsByClassName('content-container');\n" +
                                "  list[0].removeChild(list[0].firstElementChild);\n" +
                                "  list[0].removeChild(list[0].lastElementChild);\n" +
                                "  var rating = document.getElementsByClassName('needsclick hydrated');\n" +
                                "  if (rating.length > 0) {\n" +
                                "      rating[0].remove();\n" +
                                "  }\n" +
                                "})()"
                    )
                    dialogView?.pb_loading?.visibility = View.GONE
                    dialogView?.view_bg?.visibility = View.GONE
                }, 1500)
            }
        }
        val settings = dialogView?.wv_tnc?.settings
        settings?.javaScriptEnabled = true
        settings?.cacheMode = WebSettings.LOAD_NO_CACHE
        settings?.loadWithOverviewMode = true
        settings?.domStorageEnabled = true
        if (termAndConditionUrl.isNullOrEmpty()) {
            termAndConditionUrl = DEFAULT_URL
        }
        dialogView?.wv_tnc?.loadUrl(termAndConditionUrl)
    }

    private fun getBundle() {
        termAndConditionUrl = arguments?.getString(ARG_URL)
    }

    override fun onClick(v: View?) {
        touchListener()
        when (v?.id) {
            R.id.btn_close -> {
                dismiss()
            }
            else -> return
        }
    }
}