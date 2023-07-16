package com.styl.pa.modules.rating.view


import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.FragmentManager
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import com.bumptech.glide.Glide
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.rating.RatingRequest
import com.styl.pa.entities.rating.RatingRequest.CREATOR.AVERAGE
import com.styl.pa.entities.rating.RatingRequest.CREATOR.EXCELLENT
import com.styl.pa.entities.rating.RatingRequest.CREATOR.GOOD
import com.styl.pa.entities.rating.RatingRequest.CREATOR.POOR
import com.styl.pa.entities.rating.RatingRequest.CREATOR.VERRY_POOR
import com.styl.pa.modules.base.CustomBaseFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.rating.IRatingContact
import com.styl.pa.modules.rating.presenter.RatingPresenter
import com.styl.pa.utils.GeneralUtils
import kotlinx.android.synthetic.main.fragment_rating.view.*

class RatingFragment : CustomBaseFragment(), RadioGroup.OnCheckedChangeListener, IRatingContact.IView {
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        GeneralUtils.disableViewToPreventMultiTouch(getView)
        var level = VERRY_POOR
        when (checkedId) {
            R.id.rb_very_poor -> level = VERRY_POOR
            R.id.rb_poor -> level = POOR
            R.id.rb_average -> level = AVERAGE
            R.id.rb_good -> level = GOOD
            R.id.rb_excellent -> level = EXCELLENT
        }

        val request = RatingRequest(level, payerInfo?.dob, payerInfo?.gender, payerInfo?.mCustomerId)

        when (checkedId) {
            R.id.rb_very_poor,
            R.id.rb_poor -> {
                presenter?.navigationFeedbackView(request)
            }
            R.id.rb_average,
            R.id.rb_good,
            R.id.rb_excellent -> {
                presenter?.reportRatingFeedBack(request)
                navigationThankfulnessView()
            }
        }

    }

    companion object {
        private const val ARG_PAYER_INFO = BuildConfig.APPLICATION_ID + ".args.ARG_PAYER_INFO"
        fun newInstance(payer: CustomerInfo?): RatingFragment {
            val f = RatingFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_PAYER_INFO, payer)
            f.arguments = bundle
            return f
        }
    }

    private var getView: View? = null
    private val presenter: RatingPresenter? = RatingPresenter(this)
    private var payerInfo: CustomerInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        payerInfo = arguments?.getParcelable(ARG_PAYER_INFO)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        getView = inflater.inflate(R.layout.fragment_rating, container, false)

        initView()

        return getView
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBackgroundLine(R.drawable.bg_gradient_orange)
        setTitle(context?.getString(R.string.feedback))
    }

    override fun backHomeView() {
        if (activity != null && activity is MainActivity) {
            (activity as MainActivity).addRecord(false)
            (activity as MainActivity).endJourney()
        }
        fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun initView() {
        getView?.rg_option?.setOnCheckedChangeListener(this)

        getView?.isFocusableInTouchMode = true
        getView?.requestFocus()
        getView?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                backHomeView()
                return@OnKeyListener true
            }
            false
        })
    }

    override fun getContext(): Context? {
        return activity
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (activity != null && activity is MainActivity) {
            (activity as MainActivity).enableOutsideView(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }

    private fun navigationThankfulnessView() {
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

        getView?.rg_option?.visibility = View.GONE
        getView?.txt_rating_label?.text = getString(R.string.thankfulness)
        getView?.img_thanks?.visibility = View.VISIBLE
        playVideo(R.drawable.animation_thankfulness1)

        Handler().postDelayed({
            backHomeView()
        }, 3 * 1000)
    }

    private fun playVideo(video: Int) {
        activity?.let {
            getView?.img_thanks?.let { imageView ->
                Glide.with(it)
                        .load(video)
                        .into(imageView)
            }
        }
    }
}
