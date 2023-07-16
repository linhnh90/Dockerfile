package com.styl.pa.modules.feedback.view


import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.FragmentManager
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.entities.feedback.FeedbackRequest
import com.styl.pa.entities.rating.RatingRequest
import com.styl.pa.modules.base.CustomBaseFragment
import com.styl.pa.modules.feedback.IFeedbackContact
import com.styl.pa.modules.feedback.presenter.FeedbackPresenter
import com.styl.pa.modules.main.view.MainActivity
import kotlinx.android.synthetic.main.fragment_feedback.view.*

class FeedbackFragment : CustomBaseFragment(), IFeedbackContact.IView, View.OnClickListener {

    private var getView: View? = null
    private var presenter: FeedbackPresenter = FeedbackPresenter(this)
    private var ratingInfo: RatingRequest? = null
    private var mLastClickTime: Long = 0
    private var isSendFeedback = false

    companion object {
        private const val ARG_RATING_INFO = BuildConfig.APPLICATION_ID + ".args.RATING"
        fun newInstance(rating: RatingRequest?): FeedbackFragment {
            val f = FeedbackFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_RATING_INFO, rating)
            f.arguments = bundle
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ratingInfo = arguments?.getParcelable(ARG_RATING_INFO)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        getView = inflater.inflate(R.layout.fragment_feedback, container, false)
        initView()
        return getView
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBackgroundLine(R.drawable.bg_gradient_orange)
        setTitle(context?.getString(R.string.feedback))
    }

    private fun initView() {
        getView?.btn_submit?.setOnClickListener(this)

        getView?.isFocusableInTouchMode = true
        getView?.requestFocus()
        getView?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                reportFeedback(false)
                fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                return@OnKeyListener true
            }
            false
        })
    }

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        when (v?.id) {
            R.id.btn_submit -> {
                reportFeedback(true)
            }
        }
    }

    private fun reportFeedback(isFromSubmit: Boolean) {
        if (!isSendFeedback) {
            presenter.reportFeedBack(FeedbackRequest(ratingInfo, getView?.edt_feedback?.text.toString()))

            if (activity != null && activity is MainActivity) {
                (activity as MainActivity).addRecord(false)
                (activity as MainActivity).endJourney()
            }

            isSendFeedback = true

            if (isFromSubmit) {
                presenter.navigationThankfulnessView()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        reportFeedback(false)
    }

    override fun getContext(): Context? {
        return activity
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
