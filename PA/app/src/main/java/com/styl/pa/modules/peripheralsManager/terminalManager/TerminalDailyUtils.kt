package com.styl.pa.modules.peripheralsManager.terminalManager

import android.os.Handler
import android.os.HandlerThread
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import com.styl.castle_terminal_upt1000_api.message.device.SOFListResponse
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.modules.peripheralsManager.peripheralsService.PeripheralsService
import com.styl.pa.modules.setting.terminalSetting.view.TerminalSettingsFragment
import com.styl.pa.modules.terminal.upt1000fterminal.SettingCallbacks
import com.styl.pa.utils.LogManager

/**
 * Created by NgaTran on 10/21/2020.
 */
@ExcludeFromJacocoGeneratedReport
class TerminalDailyUtils {
    private var peripheralsService: PeripheralsService? = null
    fun setTerminalManager(peripheralsService: PeripheralsService? = null) {
        this.peripheralsService = peripheralsService
    }

    private val terminalSettingCallbacks =
            @ExcludeFromJacocoGeneratedReport
            object : SettingCallbacks {
                override fun onResetSequenceNumber(p0: Boolean) {
                    // Do nothing
                    return
                }

                override fun onSetSofResponse(p0: Boolean) {
                    // Do nothing
                    return
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
                    peripheralsService?.resetTerminal()
                }

                override fun onResetDeviceResponse(p0: Boolean) {
                    peripheralsService?.logOnTerminal()
                }

                override fun onLogonResponse(p0: Boolean) {
                    finishTerminalDaily()
                }

                override fun onNetsSettlementFail(p0: Int) {
                    // Do nothing
                    return
                }

                override fun callForSupportAndLog(errorCode: Int, errorRes: Int) {
                    finishTerminalDaily()
                }

                override fun printLog(txt: String?, colorId: Int, gravity: Int) {
                    // Do nothing
                    return
                }

                override fun onBootUpFlowDone() {
                    return
                }

                override fun onDeviceStatusResponse(p0: Int) {
                    // Do nothing
                    return
                }

                override fun onLastTxnResponse(p0: Boolean) {
                    // Do nothing
                    return
                }


                override fun connectResult(isConnected: Boolean?) {
                    // Do nothing
                    return
                }

                override fun onSofListResponse(p0: SOFListResponse?) {
                    // Do nothing
                    return
                }

                override fun onCreditSettlementSuccess() {
                    // Do nothing
                    return
                }

                override fun onACKTimeout() {
                    finishTerminalDaily()
                }

                override fun onTMSResponse(p0: Boolean) {
                    // Do nothing
                    return
                }

                override fun onNetsSettlementSuccess() {
                    // Do nothing
                    return
                }

                override fun onFinishWaitLogonAgainTimer() {
                    finishTerminalDaily()
                }

            }

    private var handlerThread: HandlerThread? = null
    private var handler: Handler? = null
    private val terminalDailyRunnable = object : Runnable {
        override fun run() {
            startTerminalDaily()
        }
    }

    fun handleTerminalDaily() {
        var delay : Long = 0
        if (handlerThread?.state != null && handlerThread?.state != Thread.State.NEW) {
            handlerThread?.quitSafely()
            handlerThread = null
            delay = 1000
        }
        Handler().postDelayed({
            handlerThread = HandlerThread("TerminalDailyUtils", THREAD_PRIORITY_BACKGROUND)
            handlerThread?.start()
            if (handler == null) {
                handler = Handler(handlerThread?.looper)
                handler?.post(terminalDailyRunnable)
            }
        }, delay)
    }


    private fun startTerminalDaily() {
        LogManager.i("Terminal daily settlement")
        peripheralsService?.setSettingResultListener(terminalSettingCallbacks)

        val cardTypeList = arrayListOf(TerminalSettingsFragment.CARD_NETS,
                TerminalSettingsFragment.CARD_CREDIT, TerminalSettingsFragment.CARD_EZLINK)
        peripheralsService?.settlementTerminal(cardTypeList)
    }

    private fun finishTerminalDaily() {
        if (handlerThread != null) {
            handlerThread?.quitSafely()
        }

        handler?.removeCallbacks(terminalDailyRunnable)
        handler = null

        peripheralsService?.setSettingResultListener(null)
    }
}