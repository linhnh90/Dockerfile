package com.styl.pa.modules.selectTicketType.view

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.SystemClock
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.adapters.TicketTypeAdapter
import com.styl.pa.entities.pacesRequest.EventTicket
import com.styl.pa.modules.base.CustomBaseDialogFragment
import com.styl.pa.modules.dialog.MessageDialogFragment
import com.styl.pa.modules.selectTicketType.SelectTicketTypeContract
import com.styl.pa.modules.selectTicketType.presenter.SelectTicketTypePresenter
import com.styl.pa.utils.LogManager
import kotlinx.android.synthetic.main.fragment_select_ticket_type.view.*

class SelectTicketTypeFragment : CustomBaseDialogFragment(), SelectTicketTypeContract.View,
    View.OnClickListener {
    companion object {
        const val SELECT_TICKET_TYPE_REQUEST_CODE = 111
        const val ARG_LIST_EVENT_TICKET = BuildConfig.APPLICATION_ID + "args.ARG_LIST_EVENT_TICKET"
        const val ARG_EVENT_CODE = BuildConfig.APPLICATION_ID + "args.ARG_EVENT_CODE"
        const val ARG_SELECTED_NUMBER = BuildConfig.APPLICATION_ID + "args.ARG_SELECTED_NUMBER"
        private const val TIME_DELAY = 1000
        private const val MAX_TICKET_QTY = 2
        const val MAX_TICKET_TYPE_IN_EVENT = 2
        fun newInstance(eventCode: String?, listEventTicket: ArrayList<EventTicket>?, selectedNumber: Int?) =
            SelectTicketTypeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_EVENT_CODE, eventCode)
                    putParcelableArrayList(ARG_LIST_EVENT_TICKET, listEventTicket)
                    putInt(ARG_SELECTED_NUMBER, selectedNumber ?: 0)
                }
            }

        fun isMaxTicketLimit(
            currentQty: Int,
            minQty: Int,
            maxQty: Int,
            availableQty: Int
        ): Boolean {
            var limitQty = if (minQty > 1) maxQty else MAX_TICKET_QTY
            if (availableQty < limitQty) {
                limitQty = availableQty
            }
            return currentQty >= limitQty
        }
    }

    private var presenter: SelectTicketTypePresenter? = SelectTicketTypePresenter(this)
    private var getView: View? = null
    private var listTicket: ArrayList<EventTicket> = ArrayList()
    private var ticketTypeAdapter: TicketTypeAdapter? = null
    private var eventCode: String? = null
    private var mLastClickTime = 0L
    private var messageDialog: MessageDialogFragment? = null
    private var selectedNumber: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.getBundle()
    }

    private fun getBundle() {
        listTicket.clear()
        val listData = arguments?.getParcelableArrayList<EventTicket>(ARG_LIST_EVENT_TICKET)
        if (listData != null) {
            listTicket.addAll(listData)
        }
        eventCode = arguments?.getString(ARG_EVENT_CODE, "") ?: ""
        selectedNumber = arguments?.getInt(ARG_SELECTED_NUMBER) ?: 0
        LogManager.d("SelectTicketTypeFragment: eventCode = ${eventCode}, listTicket size = ${listTicket.size}, selectedNumber = $selectedNumber")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        getView = activity?.layoutInflater?.inflate(R.layout.fragment_select_ticket_type, null)
        onSetEventDismissDialog(this)
        this.init()

        val dialog = AlertDialog.Builder(activity)
            .setView(getView)
            .create()
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    private fun init() {
        getView?.btn_proceed?.setOnClickListener(this)
        getView?.iv_close?.setOnClickListener(this)

        ticketTypeAdapter = TicketTypeAdapter(
            context = activity,
            listData = listTicket,
            listener = object : TicketTypeAdapter.OnTicketTypeActionListener {
                override fun onCheckboxChangeListener(position: Int, isChecked: Boolean) {
                    LogManager.d("onCheckboxChangeListener: $position, isChecked = $isChecked")
                    listTicket[position].isSelected = isChecked
                    if (listTicket[position].isSelected) {
                        listTicket[position].selectedQty = listTicket[position].minQty
                    } else {
                        listTicket[position].selectedQty = 0
                    }
//                    listTicket[position].isEnableMinus = isChecked
//                    listTicket[position].isEnablePlus = isChecked
                    ticketTypeAdapter?.notifyItemChanged(position)
                }

                override fun onMinusListener(position: Int) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < TIME_DELAY) {
                        return
                    }
                    mLastClickTime = SystemClock.elapsedRealtime()

                    var currentQty = listTicket[position].selectedQty ?: 0
                    if (currentQty > 0) {
                        --currentQty
                        listTicket[position].selectedQty = currentQty
                        ticketTypeAdapter?.notifyItemChanged(position)
                    }
                }

                override fun onPlusListener(position: Int) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < TIME_DELAY) {
                        return
                    }
                    mLastClickTime = SystemClock.elapsedRealtime()

                    var currentQty = listTicket[position].selectedQty ?: 0
                    ++currentQty
                    listTicket[position].selectedQty = currentQty
                    ticketTypeAdapter?.notifyItemChanged(position)
                }
            }
        )

        getView?.rcv_ticket_type?.layoutManager = LinearLayoutManager(activity)
        getView?.rcv_ticket_type?.adapter = ticketTypeAdapter
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_proceed -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < TIME_DELAY) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()

                this.presenter?.backToCartPage(
                    requestCode = SELECT_TICKET_TYPE_REQUEST_CODE,
                    eventCode = eventCode ?: "",
                    listEventTicket = listTicket,
                    selectedNumber = selectedNumber
                )
            }
            R.id.iv_close -> {
                dismiss()
            }
        }
    }

    override fun showErrorMessage(msg: String) {
        messageDialog?.dismiss()
        messageDialog = MessageDialogFragment.newInstance(0, msg, false)
        if (fragmentManager != null) {
            messageDialog?.show(fragmentManager!!, MessageDialogFragment::class.java.simpleName)
        }
    }


}