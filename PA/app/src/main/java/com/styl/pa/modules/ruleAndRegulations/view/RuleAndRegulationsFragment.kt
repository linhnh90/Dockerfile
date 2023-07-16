package com.styl.pa.modules.ruleAndRegulations.view


import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.enums.TrackingName
import com.styl.pa.interfaces.TimeOutDialogEvent
import com.styl.pa.modules.base.BaseDialogFragment
import com.styl.pa.modules.dialog.LoadingFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.fragment_rule_and_regulations.view.*

class RuleAndRegulationsFragment : BaseDialogFragment(), View.OnClickListener, TimeOutDialogEvent {
    override fun setCurrentName() {
        super.setCurrentName()

        if (activity != null) {
            (activity as MainActivity).setCurrentViewName(TrackingName.RuleAndRegulationsFragment.value)
        }
    }

    override fun onClick(v: View?) {
        touchListener()
        when (v?.id) {
            R.id.btn_close -> {
                dismiss()
            }
        }
    }

    companion object {
        private const val ARG_RULES_URL = BuildConfig.APPLICATION_ID + ".args.ARG_RULES_URL"

        private const val FONT_BOLD = "[FONT_BOLD]"
        private const val FONT_REGULAR = "[FONT_REGULAR]"

        fun newInstance(rulesUrl: String): RuleAndRegulationsFragment {
            var f = RuleAndRegulationsFragment()
            var args = Bundle()
            args.putString(ARG_RULES_URL, rulesUrl)
            f.arguments = args
            return f
        }
    }

    private var getView: View? = null
    private var rulesUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            rulesUrl = arguments?.getString(ARG_RULES_URL)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        getView = activity!!.layoutInflater.inflate(R.layout.fragment_rule_and_regulations, null)

        onSetEventDismissDialog(this)

        init()

        val dialog = AlertDialog.Builder(activity)
                .setView(getView)
                .create()

        // clear background
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    override fun onResume() {
        super.onResume()

        val window = dialog?.window
        val metrics = GeneralUtils.getScreenSize(activity)
        window?.setGravity(Gravity.TOP or Gravity.START)
        window?.setLayout(metrics.widthPixels, metrics.heightPixels + GeneralUtils.getScreenBarStatus(false, activity))
    }

    private fun init() {
        getView?.web_view?.setBackgroundColor(ContextCompat.getColor(activity!!, android.R.color.transparent))
        getView?.web_view?.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)
        getView?.web_view?.settings?.setAppCacheEnabled(false)
        getView?.web_view?.settings?.cacheMode = WebSettings.LOAD_NO_CACHE
        getView?.web_view?.setOnLongClickListener {
            return@setOnLongClickListener true
        }

        rulesUrl = rulesUrl?.replace(FONT_REGULAR, "file:///android_asset/font/opensans_regular.ttf")
                ?.replace(FONT_BOLD, "file:///android_asset/font/opensans_bold.ttf")

//        getView?.web_view?.webViewClient = MyWebViewClient()
//        getView?.web_view?.loadData(rulesUrl, "text/html", "UTF-8")

        getView?.web_view?.loadDataWithBaseURL("file:///android_asset/", rulesUrl, "text/html", "utf-8", null)

        getView?.btn_close?.setOnClickListener(this)
    }

    private inner class MyWebViewClient : WebViewClient() {

        private var loadingDialog: LoadingFragment? = null

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
            super.onPageStarted(view, url, favicon)
            if (loadingDialog == null) {
                loadingDialog = LoadingFragment()
                if (fragmentManager != null) {
                    loadingDialog?.show(fragmentManager!!, LoadingFragment::class.java.simpleName)
                }
            }
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if (loadingDialog != null) {
                loadingDialog?.dismiss()
                loadingDialog = null
            }
        }
    }

}
