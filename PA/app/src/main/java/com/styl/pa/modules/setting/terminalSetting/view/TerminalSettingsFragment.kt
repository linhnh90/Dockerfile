package com.styl.pa.modules.setting.terminalSetting.view

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.styl.castle_terminal_upt1000_api.message.device.SOFItem
import com.styl.castle_terminal_upt1000_api.message.device.SOFListResponse
import com.styl.pa.R
import com.styl.pa.adapters.SOFAdapter
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.dialog.LoadingFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.peripheralsManager.peripheralsService.Peripheral
import com.styl.pa.modules.peripheralsManager.peripheralsService.PeripheralsInfo
import com.styl.pa.modules.peripheralsManager.peripheralsService.PeripheralsService
import com.styl.pa.modules.terminal.upt1000fterminal.CastleTerminalService
import com.styl.pa.modules.terminal.upt1000fterminal.SettingCallbacks
import com.styl.pa.utils.MySharedPref
import kotlinx.android.synthetic.main.fragment_terminal_settings.*
import kotlinx.android.synthetic.main.fragment_terminal_settings.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * Created by NgaTran on 10/18/2020.
 */
class TerminalSettingsFragment : BaseFragment(), View.OnClickListener {
    companion object {
        const val CARD_NETS = "NETS"
        const val CARD_CREDIT = "CREDIT"
        const val CARD_EZLINK = "EZLINK"
    }

    private var logs: StringBuilder = StringBuilder()
    private var peripheralsService: PeripheralsService? = null
    private var sharedPref: MySharedPref? = null
    private var getView: View? = null
    private var terminalList = HashMap<Int, PeripheralsInfo>()
    private var serialPort: String? = CastleTerminalService.UART_PORT_NAME
    private var sofItems: MutableList<SOFItem> = ArrayList()
    private var sofAdapter: SOFAdapter? = null

    private val settingResultCallback = object : SettingCallbacks {
        override fun onResetSequenceNumber(p0: Boolean) {
            dismissLoading()
        }

        override fun onSetSofResponse(p0: Boolean) {
            dismissLoading()
        }

        override fun onCreditSettlementFail(p0: Int) {
            // Do nothing
            return
        }

        override fun onCepasSettlementSuccess() {
            // Do nothing
            return
        }

        override fun onCepasSettlementFail(p0: Int) {
            // Do nothing
            return
        }

        override fun settlementCompletely(isSuccess: Boolean?) {
            Log.e("command", "settlementCompletely: --------" + isSuccess)
            dismissLoading()
            if (false == isSuccess) {
                logFailMessage(getString(R.string.setting_settlement_fail))
            }
        }

        override fun onResetDeviceResponse(p0: Boolean) {
            dismissLoading()
        }

        override fun onLogonResponse(p0: Boolean) {
            dismissLoading()
        }

        override fun onNetsSettlementFail(p0: Int) {
            // Do nothing
            return
        }

        override fun callForSupportAndLog(errorCode: Int, errorRes: Int) {
            dismissLoading()
            logFailMessage(getString(R.string.setting_process_fail))
        }

        override fun printLog(txt: String?, colorId: Int, gravity: Int) {
            logs.append(txt)
            logs.append("\n")
            setActivityLogs()
        }

        override fun onBootUpFlowDone() {
            // nothing
            return
        }

        override fun onDeviceStatusResponse(p0: Int) {
            dismissLoading()
        }

        override fun onLastTxnResponse(p0: Boolean) {
            dismissLoading()
        }

        override fun onSofListResponse(p0: SOFListResponse?) {
            dismissLoading()
            if (p0 != null) {
                sofItems.clear()
                sofItems.addAll(p0.sofList)
                sofAdapter?.notifyDataSetChanged()
            }
        }

        override fun connectResult(isConnected: Boolean?) {
            // Do nothing
            return
        }

        override fun onCreditSettlementSuccess() {
            // Do nothing
            return
        }

        override fun onACKTimeout() {
            dismissLoading()
        }

        override fun onTMSResponse(p0: Boolean) {
            dismissLoading()
        }

        override fun onNetsSettlementSuccess() {
            // Do nothing
            return
        }

        override fun onFinishWaitLogonAgainTimer() {
            dismissLoading()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getView = inflater.inflate(R.layout.fragment_terminal_settings, container, false)
        return getView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        context?.let {
            sharedPref = MySharedPref(it)
        }

        peripheralsService = (activity as? MainActivity)?.getPeripheralManager()
        peripheralsService?.setSettingResultListener(settingResultCallback)

        btn_log_on?.setOnClickListener(this)
        btn_reset?.setOnClickListener(this)
        btn_reset_sequence?.setOnClickListener(this)
        btn_settlement?.setOnClickListener(this)
        btn_update_firmware?.setOnClickListener(this)
        btn_last_txn?.setOnClickListener(this)
        btn_clear_logs?.setOnClickListener(this)
        btn_save_terminal_config?.setOnClickListener(this)
        btn_sof_list?.setOnClickListener(this)
        btn_sof_priority.setOnClickListener(this)

        loadTerminalConfig()

        sofAdapter = SOFAdapter(sofItems, requireContext())
        rcv_sof_list?.layoutManager = LinearLayoutManager(activity)
        rcv_sof_list?.adapter = sofAdapter
    }

    private fun padLeftHex(number: Int): String? {
        return String.format("%04X", number)
    }

    private fun addPeripheral(pos: Int, peripheralsInfo: PeripheralsInfo) {
        terminalList.put(pos, peripheralsInfo)
    }

    private fun loadTerminalConfig() {
        terminalList = Gson().fromJson<HashMap<Int,PeripheralsInfo>>(sharedPref?.terminalList,  object : TypeToken<HashMap<Int, PeripheralsInfo>>() {}.type)
        for (entry in terminalList.entries) {
            if (entry.key == 0) {
                getView?.edt_vid_1?.setText(padLeftHex(entry.value.vid))
                getView?.edt_pid_1?.setText(padLeftHex(entry.value.pid))
            } else if (entry.key == 1) {
                getView?.edt_vid_2?.setText(padLeftHex(entry.value.vid))
                getView?.edt_pid_2?.setText(padLeftHex(entry.value.pid))
            } else if (entry.key == 2) {
                getView?.edt_vid_3?.setText(padLeftHex(entry.value.vid))
                getView?.edt_pid_3?.setText(padLeftHex(entry.value.pid))
            }
        }
    }

    private fun checkTerminalList() {
        if (getView?.edt_vid_1?.text?.isNotEmpty() == true && getView?.edt_pid_1?.text?.isNotEmpty() == true) {
            try {
                val vid = Integer.parseInt(edt_vid_1?.text.toString(), 16)
                val pid = Integer.parseInt(edt_pid_1?.text.toString(), 16)
                addPeripheral(0, PeripheralsInfo(Peripheral.TERMINAL_TYPE, vid, pid, "", ""))
            } catch (e: Exception) {
                Toast.makeText(context, "Invalid VID/PID", Toast.LENGTH_LONG).show()
            }
        } else if (getView?.edt_vid_1?.text?.isEmpty() == true && getView?.edt_pid_1?.text?.isEmpty() == true){
            terminalList.remove(0)
        }

        if (getView?.edt_vid_2?.text?.isNotEmpty() == true && getView?.edt_pid_2?.text?.isNotEmpty() == true) {
            try {
                val vid = Integer.parseInt(edt_vid_2?.text.toString(), 16)
                val pid = Integer.parseInt(edt_pid_2?.text.toString(), 16)
                addPeripheral(1, PeripheralsInfo(Peripheral.TERMINAL_TYPE, vid, pid, "", ""))
            } catch (e: Exception) {
                Toast.makeText(context, "Invalid VID/PID", Toast.LENGTH_LONG).show()
            }
        } else if (getView?.edt_vid_2?.text?.isEmpty() == true && getView?.edt_pid_2?.text?.isEmpty() == true) {
            terminalList.remove(1)
        }

        if (getView?.edt_vid_3?.text?.isNotEmpty() == true && getView?.edt_pid_3?.text?.isNotEmpty() == true) {
            try {
                val vid = Integer.parseInt(edt_vid_3?.text.toString(), 16)
                val pid = Integer.parseInt(edt_pid_3?.text.toString(), 16)
                addPeripheral(2, PeripheralsInfo(Peripheral.TERMINAL_TYPE, vid, pid, "", ""))
            } catch (e: Exception) {
                Toast.makeText(context, "Invalid VID/PID", Toast.LENGTH_LONG).show()
            }
        } else if (getView?.edt_vid_3?.text?.isEmpty() == true && getView?.edt_pid_3?.text?.isEmpty() == true) {
            terminalList.remove(2)
        }

        if (terminalList.size > 0) {
            sharedPref?.terminalList = Gson().toJson(terminalList)
        } else {
            Toast.makeText(context, "Must be at least 1 terminal config", Toast.LENGTH_LONG).show()
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_save_terminal_config -> {
                checkTerminalList()
            }
            R.id.btn_log_on -> {
                logTitle(getString(R.string.log_on))
                if (true == peripheralsService?.logOnTerminal()) {
                    showLoading()
                } else {
                    logFailMessage(getString(R.string.setting_logon_fail))
                }
            }

            R.id.btn_sof_list -> {
                logTitle(getString(R.string.title_get_sof_list))
                if (true == peripheralsService?.requestSofList()) {
                    showLoading()
                } else {
                    logFailMessage(getString(R.string.get_sof_list_failed))
                }
            }

            R.id.btn_sof_priority -> {
                logTitle(getString(R.string.title_set_sof_list))
                if (sofItems.size > 0) {
                    var isDuplicatePriority = false
                    loop@ for (i in 0 until sofItems.size) {
                        for (j in i+1 until sofItems.size) {
                            if (sofItems[i].idSofPriority == sofItems[j].idSofPriority) {
                                isDuplicatePriority = true
                                break@loop
                            }
                        }
                    }
                    if (isDuplicatePriority) {
                        logFailMessage(getString(R.string.set_sof_priority_value_warning))
                    } else {
                        if (true == peripheralsService?.setSofPriority(sofItems)) {
                            showLoading()
                        } else {
                            logFailMessage(getString(R.string.set_sof_list_failed))
                        }
                    }
                } else {
                    logFailMessage(getString(R.string.get_sof_first))
                }
            }

            R.id.btn_reset -> {
                logTitle(getString(R.string.reset))
                if (true == peripheralsService?.resetTerminal()) {
                    showLoading()
                } else {
                    logFailMessage(getString(R.string.setting_reset_fail))
                }
            }

            R.id.btn_reset_sequence -> {
                logTitle(getString(R.string.reset_sequence_number))
                if (true == peripheralsService?.resetSequenceNumberTerminal()) {
                    showLoading()
                } else {
                    logFailMessage(getString(R.string.setting_reset_sequence_fail))
                }
            }

            R.id.btn_settlement -> {
                val listCardType = arrayListOf<String>()
                if (true == ckb_nets?.isChecked) {
                    listCardType.add(CARD_NETS)
                }
                if (true == ckb_credit?.isChecked) {
                    listCardType.add(CARD_CREDIT)
                }
                if (true == ckb_ezl?.isChecked) {
                    listCardType.add(CARD_EZLINK)
                }
                if (listCardType.isEmpty()) {
                    activity?.let {
                        Toast.makeText(it, "Choose the card type", Toast.LENGTH_LONG).show()
                    }
                    return
                }

                logTitle(getString(R.string.settlement))
                showLoading()
                peripheralsService?.settlementTerminal(listCardType)
            }

            R.id.btn_update_firmware -> {
                logTitle(getString(R.string.check_for_update))
                if (true == peripheralsService?.updateTerminalFirmware()) {
                    showLoading()
                } else {
                    logFailMessage(getString(R.string.setting_update_firmware_fail))
                }
            }

            R.id.btn_last_txn -> {
                logTitle(getString(R.string.last_transaction))
                if (true == peripheralsService?.getLastTxnTerminal()) {
                    showLoading()
                } else {
                    logFailMessage(getString(R.string.setting_last_txn_fail))
                }
            }

            R.id.btn_clear_logs -> {
                logs.clear()
                setActivityLogs()
            }
        }
    }

    private fun logTitle(title: String) {
        logs.append("\n")
        logs.append(String.format(Locale.ENGLISH, "-------------------------------------------------%s--------------------------------------------------", title))
        logs.append("\n")

        setActivityLogs()
    }

    private fun logFailMessage(message: String) {
        logs.append(message)
        logs.append("\n")

        setActivityLogs()
    }

    private fun setActivityLogs() {
        txt_log?.text = logs.toString()
        Handler().postDelayed({
            scrollView?.fullScroll(View.FOCUS_DOWN)
        }, 200)
    }

    private var loadingDialog: LoadingFragment? = null

    fun showLoading() {
        if (loadingDialog == null || loadingDialog?.dialog?.isShowing == false) {
            loadingDialog = LoadingFragment()
            if (fragmentManager != null) {
                loadingDialog?.show(fragmentManager!!, LoadingFragment::class.java.simpleName)
            }
        }

    }

    fun dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog?.dismiss()
            loadingDialog = null
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        peripheralsService?.setSettingResultListener(null)
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}