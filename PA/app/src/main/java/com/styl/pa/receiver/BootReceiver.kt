package com.styl.pa.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.modules.main.view.MainActivity

/**
 * Created by trangpham on 9/20/2018
 */
@ExcludeFromJacocoGeneratedReport
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val i = Intent(context, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(i)
    }
}