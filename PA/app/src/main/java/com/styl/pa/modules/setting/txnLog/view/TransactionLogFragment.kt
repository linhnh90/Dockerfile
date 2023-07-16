package com.styl.pa.modules.setting.txnLog.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.styl.pa.R
import com.styl.pa.adapters.TransactionAdapter
import com.styl.pa.entities.payment.PaymentRequest
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.dialog.LoadingFragment
import com.styl.pa.modules.setting.txnLog.ITransactionLogContact
import com.styl.pa.modules.setting.txnLog.presenter.TransactionLogPresenter
import kotlinx.android.synthetic.main.fragment_transaction_log.view.*


class TransactionLogFragment : BaseFragment(), View.OnClickListener, ITransactionLogContact.IView {

    var presenter: TransactionLogPresenter? = null

    private var getView: View? = null
    private var txnAdapter: TransactionAdapter? = null

    private val txnLogs = ArrayList<PaymentRequest>()

    private var logPerRequest: Int = 25
    private var currentOffset: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getView = inflater.inflate(R.layout.fragment_transaction_log, container, false)
        return getView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getView?.btn_delete_txn?.setOnClickListener(this)
        getView?.btn_next?.setOnClickListener(this)
        getView?.btn_previous?.setOnClickListener(this)

        presenter = TransactionLogPresenter(activity as Context, this)
        presenter?.onViewCreated()

        init()
        getTransactionLogs(currentOffset, logPerRequest)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_delete_txn -> {
                val selectedItems = txnAdapter?.getSelectedItems()
                if (selectedItems != null) {
                    for (item in selectedItems) {
                        presenter?.deleteTxn(item)
                        txnLogs.remove(item)
                    }
                    txnAdapter?.notifyDataSetChanged()
                }
            }
            R.id.btn_next -> {
                currentOffset += logPerRequest
                getTransactionLogs(currentOffset, logPerRequest)
            }

            R.id.btn_previous -> {
                if (currentOffset >= logPerRequest) {
                    currentOffset -= logPerRequest
                    getTransactionLogs(currentOffset, logPerRequest)
                }
            }
        }
    }

    private fun init() {
        txnAdapter = TransactionAdapter(activity, txnLogs)
        getView?.rcv_txn_log?.layoutManager = LinearLayoutManager(activity)
        getView?.rcv_txn_log?.adapter = txnAdapter
        getView?.rcv_txn_log?.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        txnAdapter?.notifyDataSetChanged()
    }

    private fun getTransactionLogs(currentOffset: Int, logPerRequest: Int) {
        presenter?.getTxnLogs(currentOffset, logPerRequest)
    }

    private var loadingDialog: LoadingFragment? = null

    override fun showLoading() {
        if (loadingDialog == null || loadingDialog?.dialog?.isShowing == false) {
            loadingDialog = LoadingFragment()
            if (fragmentManager != null) {
                loadingDialog?.show(fragmentManager!!, LoadingFragment::class.java.simpleName)
            }
        }
    }

    override fun dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog?.dismiss()
            loadingDialog = null
        }
    }

    override fun onDestroy() {
        presenter?.onDestroy()
        super.onDestroy()
    }

    override fun getTxnLogsResponse(txnLogs: List<PaymentRequest>?) {
        if (txnLogs?.size ?: 0 > 0) {
            txnLogs?.toTypedArray()?.let {
                this.txnLogs.clear()
                this.txnLogs.addAll(it)
                txnAdapter?.notifyDataSetChanged()
            }
        }
    }
}