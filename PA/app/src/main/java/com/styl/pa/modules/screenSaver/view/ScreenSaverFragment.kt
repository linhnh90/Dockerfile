package com.styl.pa.modules.screenSaver.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ViewFlipper
import com.bumptech.glide.Glide
import com.styl.pa.R
import com.styl.pa.modules.base.BaseDialogFragment
import com.styl.pa.modules.home.view.HomeFragment
import com.styl.pa.modules.main.presenter.MainPresenter
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.fragment_screen_saver.view.*
import java.io.File

@SuppressLint("ValidFragment")
class ScreenSaverFragment : BaseDialogFragment() {

    companion object {
        const val FLIP_INTERVAL = 60*1000
    }

    private var getView: View? = null

    private var imageList : Array<File> = arrayOf()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        getView = activity!!.layoutInflater.inflate(R.layout.fragment_screen_saver, null)

        val dialog = AlertDialog.Builder(activity)
                .setView(getView)
                .create()

        // clear background
        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        init()

        return dialog
    }

    private fun createImageSlider(viewFlipper: ViewFlipper) {
        for (i in imageList.indices) {
            addImageToViewFlipper(viewFlipper, i);
        }
        viewFlipper.inAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_left);
        viewFlipper.outAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_out_right);

        viewFlipper.isAutoStart = true
        viewFlipper.setFlipInterval(FLIP_INTERVAL)
    }

    private fun addImageToViewFlipper(viewFlipper: ViewFlipper, position: Int) {
        val imageView = ImageView(activity)
        imageView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        imageView.adjustViewBounds = true
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        activity?.let { Glide.with(it).load(imageList.get(position).absolutePath).into(imageView) }
        viewFlipper.addView(imageView)
    }

    fun init() {
        if (getView?.view_flipper != null) {
            val screenSaverDir = File(
                    Environment.getExternalStorageDirectory().absolutePath + "/${MainPresenter.SCREENSAVER_PATH}")
            if (screenSaverDir.exists() && screenSaverDir.listFiles().isNotEmpty()) {
                imageList = screenSaverDir.listFiles()
                createImageSlider(getView?.view_flipper!!)
            }
        }
        getView?.view_flipper?.setOnClickListener {
            targetFragment?.onActivityResult(HomeFragment.SCREENSAVER_REQ, Activity.RESULT_OK, Intent())
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()

        val window = dialog?.window
        val metrics = GeneralUtils.getScreenSize(activity)
        window?.setGravity(Gravity.TOP or Gravity.START)
        window?.setLayout(metrics.widthPixels,
                metrics.heightPixels + GeneralUtils.getDefaultBar(activity, false))

    }

}