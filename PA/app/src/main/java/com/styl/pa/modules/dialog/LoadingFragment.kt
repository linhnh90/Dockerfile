package com.styl.pa.modules.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import android.view.Gravity
import android.view.View
import com.bumptech.glide.Glide
import com.styl.pa.R
import com.styl.pa.modules.base.BaseDialogFragment
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.fragment_loading.view.*
import java.security.SecureRandom

/**
 * Created by trangpham on 8/29/2018
 */
class LoadingFragment : BaseDialogFragment() {

    private var getView: View? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        getView = activity!!.layoutInflater.inflate(R.layout.fragment_loading, null)

        onSetEventDismissDialog(this)

        isCancelable = false

        val dialog = AlertDialog.Builder(activity)
                .setView(getView)
                .create()

        val select = SecureRandom().nextInt(2 - 0) + 0

        var resource = R.drawable.leaping_animation
        if (select == 0) {
            resource = R.drawable.running_animation
        }

        playVideo(resource)

        // clear background
        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        return dialog
    }

    fun playVideo(video: Int) {
        activity?.let {
            getView?.img_loading?.let { imageView ->
                Glide.with(it)
                        .load(video)
                        .into(imageView)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val window = dialog?.window
        val metrics = GeneralUtils.getScreenSize(activity)
        window?.setGravity(Gravity.TOP or Gravity.START)
        window?.setLayout(metrics.widthPixels, metrics.heightPixels + GeneralUtils.getScreenBarStatus(false, activity))
    }

    override fun show(manager: FragmentManager, tag: String?) {
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }
}