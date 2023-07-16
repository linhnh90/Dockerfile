package com.styl.pa.modules.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.View
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.enums.TrackingName
import com.styl.pa.interfaces.TimeOutDialogEvent
import com.styl.pa.modules.base.BaseDialogFragment
import com.styl.pa.modules.main.view.MainActivity
import kotlinx.android.synthetic.main.fragment_message_dialog.view.*


/**
 * Created by trangpham on 7/31/2018
 */

class MessageDialogFragment : BaseDialogFragment(), View.OnClickListener, TimeOutDialogEvent {
    override fun setCurrentName() {
        super.setCurrentName()

        if (isSessionTimeoutDialog || isOrderNotFinishedDialog) {
            return
        }

        (activity as? MainActivity)?.setCurrentViewName(TrackingName.MessageDialogFragment.value)
    }

    companion object {

        private const val ARG_TITLE = BuildConfig.APPLICATION_ID + ".args.ARG_TITLE"
        private const val ARG_MESSAGE = BuildConfig.APPLICATION_ID + ".args.ARG_MESSAGE"
        private const val ARG_TITLE_RES_ID = BuildConfig.APPLICATION_ID + ".args.ARG_TITLE_RES_ID"
        private const val ARG_MESSAGE_RES_ID = BuildConfig.APPLICATION_ID + ".args.ARG_MESSAGE_RES_ID"
        private const val ARG_ERROR_CODE = BuildConfig.APPLICATION_ID + ".args.ARG_ERROR_CODE"
        private const val ARG_SHOW_TITLE = BuildConfig.APPLICATION_ID + ".args.ARG_SHOW_TITLE"

        fun newInstance(title: String, message: String): MessageDialogFragment {
            val f = MessageDialogFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_MESSAGE, message)
            f.arguments = args
            return f
        }

        fun newInstance(@StringRes titleResId: Int, @StringRes messageResId: Int): MessageDialogFragment {
            val f = MessageDialogFragment()
            val args = Bundle()
            args.putInt(ARG_TITLE_RES_ID, titleResId)
            args.putInt(ARG_MESSAGE_RES_ID, messageResId)
            f.arguments = args
            return f
        }

        fun newInstance(errorCode: Int, title: String, message: String): MessageDialogFragment {
            val f = MessageDialogFragment()
            val args = Bundle()
            args.putInt(ARG_ERROR_CODE, errorCode)
            args.putString(ARG_TITLE, title)
            args.putString(ARG_MESSAGE, message)
            f.arguments = args
            return f
        }

        fun newInstance(@StringRes titleResId: Int, @StringRes messageResId: Int, isShowTitle: Boolean): MessageDialogFragment {
            val f = MessageDialogFragment()
            val args = Bundle()
            args.putInt(ARG_TITLE_RES_ID, titleResId)
            args.putInt(ARG_MESSAGE_RES_ID, messageResId)
            args.putBoolean(ARG_SHOW_TITLE, isShowTitle)
            f.arguments = args
            return f
        }

        fun newInstance(@StringRes titleResId: Int, messageRes: String, isShowTitle: Boolean): MessageDialogFragment {
            val f = MessageDialogFragment()
            val args = Bundle()
            args.putInt(ARG_TITLE_RES_ID, titleResId)
            args.putString(ARG_MESSAGE, messageRes)
            args.putBoolean(ARG_SHOW_TITLE, isShowTitle)
            f.arguments = args
            return f
        }
    }

    private var title: String? = null
    private var message: String? = null

    @StringRes
    private var titleResId: Int? = null
    @StringRes
    private var messageResId: Int? = null

    private var errorCode: Int? = null

    private var isShowTitle: Boolean? = false

    private var isMulti = false
    private var negativeMessage: String? = null
    private var positiveMessage: String? = null

    private var listener: MessageDialogListener? = null

    private var isSessionTimeoutDialog = false

    private var isOrderNotFinishedDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = arguments?.getString(ARG_TITLE)
        message = arguments?.getString(ARG_MESSAGE)
        titleResId = arguments?.getInt(ARG_TITLE_RES_ID)!!
        messageResId = arguments?.getInt(ARG_MESSAGE_RES_ID)
        errorCode = arguments?.getInt(ARG_ERROR_CODE)
        isShowTitle = arguments?.getBoolean(ARG_SHOW_TITLE, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val v = activity!!.layoutInflater.inflate(R.layout.fragment_message_dialog, null)

        if (!isSessionTimeoutDialog) {
            onSetEventDismissDialog(this)
        }

        if (!TextUtils.isEmpty(title)) {
            v.txtTitle.setText(title)
        } else if (titleResId!! > 0) {
            v.txtTitle.setText(titleResId!!)
        }

        if (isShowTitle == true) {
            v.txtTitle.visibility = View.VISIBLE
        } else {
            v.txtTitle.visibility = View.GONE
        }

        if (errorCode!! > 0) {
            // update name here
            val name = "code_" + errorCode
            messageResId = resources.getIdentifier(name, "string", activity?.packageName)
            if (messageResId!! > 0) {
                v.txtMessage.setText(messageResId!!)
            } else {
                v.txtMessage.setText(message)
            }
        } else if (!TextUtils.isEmpty(message)) {
            v.txtMessage.setText(message)
        } else if (messageResId!! > 0) {
            v.txtMessage.setText(messageResId!!)
        }

        if (isMulti) {
            v.btnNegative.visibility = View.VISIBLE
            v.btnNeutral.visibility = View.GONE
            v.btnPositive.visibility = View.VISIBLE

            if (!TextUtils.isEmpty(negativeMessage)) {
                v.btnNegative.text = negativeMessage
            }
            if (!TextUtils.isEmpty(positiveMessage)) {
                v.btnPositive.text = positiveMessage
            }
        }

        if (isSessionTimeoutDialog) {
            v.btnNegative.visibility = View.VISIBLE
            v.btnNeutral.visibility = View.GONE
            v.btnPositive.visibility = View.VISIBLE

            v.btnNegative.text = getString(R.string.end_session_btn)
//            v.btnNegative.setBackgroundResource(R.drawable.ic_end_session_button)

            v.btnPositive.text = getString(R.string.extend_session_btn)
//            v.btnPositive.setBackgroundResource(R.drawable.ic_extend_button)
        }

        isCancelable = false

        v.txtMessage.movementMethod = ScrollingMovementMethod()
        v.btnNegative.setOnClickListener(this)
        v.btnNeutral.setOnClickListener(this)
        v.btnPositive.setOnClickListener(this)
        v.rl_container.setOnClickListener(this)

        val dialog = AlertDialog.Builder(activity)
            .setView(v)
            .create()

        // clear background
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    override fun onResume() {
        super.onResume()

        val window = dialog?.window
        val height = 1080
        val width = 1920
        window?.setLayout(width, height)
    }

    override fun onClick(view: View) {
        if (messageResId != R.string.extend_session_question) {
            touchListener()
        }
        when (view.id) {
            R.id.btnNegative -> {
                dismiss()
                if (listener != null) {
                    listener!!.onNegativeClickListener(this@MessageDialogFragment)
                }
            }
            R.id.btnNeutral -> {
                dismiss()
                if (listener != null) {
                    listener!!.onNeutralClickListener(this@MessageDialogFragment)
                }
            }
            R.id.btnPositive -> {
                dismiss()
                if (listener != null) {
                    listener!!.onPositiveClickListener(this@MessageDialogFragment)
                }
            }
        }
    }

    // MARK: Public methods

    fun setListener(listener: MessageDialogListener) {
        this.listener = listener
    }

    fun setMultiChoice(negativeMessage: String?, positiveMessage: String?) {
        this.isMulti = true
        this.negativeMessage = negativeMessage
        this.positiveMessage = positiveMessage
    }

    fun setIsSessionTimeoutDialog() {
        this.isSessionTimeoutDialog = true
    }

    fun setOrderNotFinishedDialog() {
        this.isOrderNotFinishedDialog = true
    }

    interface MessageDialogListener {

        fun onNegativeClickListener(dialogFragment: DialogFragment)

        fun onPositiveClickListener(dialogFragment: DialogFragment)

        fun onNeutralClickListener(dialogFragment: DialogFragment)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

    override fun dismiss() {
        dismissAllowingStateLoss()
    }
}
