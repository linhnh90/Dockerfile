package com.styl.pa.modules.help

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.core.content.pm.PackageInfoCompat
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.utils.LogManager


/**
 * Created by trangpham on 10/11/2018
 */
class HelpFragment : BaseFragment() {

    companion object {

        private const val ARG_CONTENT = BuildConfig.APPLICATION_ID + ".args.ARG_CONTENT"

        private const val FONT_BOLD = "[FONT_BOLD]"
        private const val FONT_REGULAR = "[FONT_REGULAR]"

        fun newInstance(content: String?): HelpFragment {
            val f = HelpFragment()
            val bundle = Bundle()
            bundle.putString(ARG_CONTENT, content)
            f.arguments = bundle
            return f
        }
    }

    private var content: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        content = arguments?.getString(ARG_CONTENT)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val v = activity?.layoutInflater?.inflate(R.layout.fragment_help, null)

        setTitle(getString(R.string.help))
        (activity as? MainActivity)?.setBackgroundLine(R.drawable.bg_gradient_orange)
        try {
            val pInfo = context?.packageManager?.getPackageInfo(activity?.packageName, 0)
            val version =
                    getString(R.string.version, pInfo?.versionName + "(" + PackageInfoCompat.getLongVersionCode(pInfo!!) + ")")
            val txtVersion = v?.findViewById<TextView>(R.id.txtVersion)
            txtVersion?.text = version
        } catch (e: PackageManager.NameNotFoundException) {
            LogManager.i("Get app version failed")
        }

        val webView = v?.findViewById<WebView>(R.id.webView)
        webView?.setOnLongClickListener {
            return@setOnLongClickListener true
        }

        content = content?.replace(FONT_REGULAR, "file:///android_asset/font/opensans_regular.ttf")
                ?.replace(FONT_BOLD, "file:///android_asset/font/opensans_bold.ttf")

        webView?.loadDataWithBaseURL("file:///android_asset/", content, "text/html", "utf-8", null)

        return v
    }
}