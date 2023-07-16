package com.styl.pa.modules.terminal.upt1000fterminal;

import com.styl.castle_terminal_upt1000_api.connector.DeviceCallback;
import com.styl.castle_terminal_upt1000_api.connector.SettlementCallback;

public interface SettingCallbacks extends DeviceCallback, SettlementCallback {
    void connectResult(Boolean isConnected);

    void settlementCompletely(Boolean isSuccess);

    void onFinishWaitLogonAgainTimer();

    void onACKTimeout();

    void callForSupportAndLog(int errorCode, int errorRes);

    void printLog(String txt, int colorId, int gravity);

    void onBootUpFlowDone();
}
