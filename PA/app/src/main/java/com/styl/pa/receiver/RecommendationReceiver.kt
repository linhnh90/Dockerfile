package com.styl.pa.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.modules.home.view.HomeFragment

@ExcludeFromJacocoGeneratedReport
class RecommendationReceiver: BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        p0?.let { context ->
            val intent = Intent(HomeFragment.ACTION_RECOMMENDATION_REFRESH)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
    }
}