package com.styl.pa.serverlocal

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.R
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.LogManager
import java.io.File


/**
 * Created by NguyenHang on 9/27/2020.
 */
@ExcludeFromJacocoGeneratedReport
class WebServerService : Service() {
    companion object {
        private const val NOTIFICATION_ID = 101
        private const val PORT = 8080
        private const val IP = "127.0.0.1"
        const val ROOT_FOLDER = "ServerLocal"
    }

    private var webServer: NanoHTTPD? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        startForeground()
        val file = File(Environment.getExternalStorageDirectory().absolutePath, "/$ROOT_FOLDER/")
        if (!file.exists() && GeneralUtils.mkdirs(file)) {
            LogManager.i("Created Home directory of Local server")
        }
        webServer = NanoHTTPD(this, PORT, IP, file)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    override fun onDestroy() {
        webServer?.stop()
        super.onDestroy()
    }

    private fun startForeground() {
        val channelId =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel("Server Local", "My Background Service")
                } else {
                    // If earlier version channel ID is not used
                    // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                    ""
                }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("Server started")
                .setPriority(1)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
}