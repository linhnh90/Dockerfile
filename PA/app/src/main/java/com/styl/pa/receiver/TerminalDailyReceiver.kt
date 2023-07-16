package com.styl.pa.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.modules.main.view.MainActivity

/**
 * Created by NgaTran on 10/21/2020.
 */
@ExcludeFromJacocoGeneratedReport
class TerminalDailyReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        p0?.let {
            LocalBroadcastManager.getInstance(it).sendBroadcast(Intent(MainActivity.ACTION_TERMINAL_DAILY))
        }
    }
}