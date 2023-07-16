package com.styl.pa.modules.thankfulness.view


import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.styl.pa.R
import com.styl.pa.modules.main.view.MainActivity
import kotlinx.android.synthetic.main.fragment_thankfulness.view.*

/**
 * A simple [Fragment] subclass.
 */
class ThankfulnessFragment : Fragment() {

    private var getView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        getView = inflater.inflate(R.layout.fragment_thankfulness, container, false)
        initView()
        return getView
    }

    private fun initView() {
        getView?.isFocusableInTouchMode = true
        getView?.requestFocus()
        getView?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                return@OnKeyListener true
            }
            false
        })

        if (activity != null && activity is MainActivity) {
            (activity as MainActivity).enableOutsideView(false)
        }

        getView?.isFocusableInTouchMode = true
        getView?.requestFocus()
        getView?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                return@OnKeyListener true
            }
            false
        })

        getView?.img_thanks?.visibility = View.VISIBLE
        playVideo(R.drawable.animation_thankfulness1)

        Handler().postDelayed({
            fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }, 3 * 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (activity != null && activity is MainActivity) {
            (activity as MainActivity).enableOutsideView(true)
        }
    }

    private fun playVideo(video: Int) {
        activity?.let {
            getView?.img_thanks?.let { imageView ->
                Glide.with(it)
                        .asGif()
                        .load(video)
                        .into(imageView)
            }
        }
    }
}
