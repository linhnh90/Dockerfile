package com.styl.pa.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.modules.main.view.MainActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ngatran on 10/15/2019.
 */
@ExcludeFromJacocoGeneratedReport
object StoreLogsUtils {
    const val PERMISSION_REQUEST_CODE = 1405
    private const val LIMIT_DAYS_LOG = 14

    private var process: Process? = null

    const val DATE_FORMAT_PATERN = "yyyyMMdd"

    fun checkExternalStorageState(): Boolean {
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true
        }

        return false
    }

    private fun checkRequestPermission(context: Context): Boolean {
        val result = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true
        }

        return false
    }

    fun requestPermission(context: Context) {
        ActivityCompat.requestPermissions((context as MainActivity), arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }

    private fun createFolderExternalStorage(): File? {
        val currentDate = Calendar.getInstance()
        val folder = File(Environment.getExternalStorageDirectory().absolutePath + "/logs/")
        if (!folder.exists() && !GeneralUtils.mkdirs(folder)) {
            return null
        }

        // remove files beyond 14 days
        removeLogFileBeyond14Days(folder)

        val latestLogFile = getLatestLogFile(folder)

        val file: File
        if (latestLogFile != null && currentDate.time.before(getTimeOfLogFile(latestLogFile))) {
            file = latestLogFile
        } else {
            val dateFormat = SimpleDateFormat(DATE_FORMAT_PATERN, Locale.ENGLISH)
            val fileName = String.format("log-%s.txt", dateFormat.format(currentDate.time))
            file = File(folder, fileName)
        }

        if (!file.exists()) {
            //remove all of logs
            val clearLogProcess = Runtime.getRuntime().exec("/system/bin/logcat -b all -c")
            try {
                if (clearLogProcess.exitValue() == 0) {
                    LogManager.d("Cleared log before create new one")
                }
                process?.destroy()
                process = null
            } catch (e: Exception) {
                LogManager.i("Cannot check clear log process exit code")
            }
        }

        return file
    }

    private fun removeLogFileBeyond14Days(folder: File) {
        val removeCalendar = Calendar.getInstance()
        removeCalendar.add(Calendar.DAY_OF_YEAR, -LIMIT_DAYS_LOG)
        val limitDate = removeCalendar.time

        for (f in folder.listFiles()) {
            val startIndex = f.name.lastIndexOf("-") + 1
            val endIndex = f.name.lastIndexOf(".")
            if (startIndex in 0 until endIndex) {
                val logDate = getTimeFromDateFormat(f.name.substring(startIndex, endIndex))
                if (logDate.before(limitDate) && f.delete()) {
                    LogManager.i("Removed log ${f.name}")
                }
            }
        }
    }

    private fun getLatestLogFile(logFolder: File): File? {
        val listFileLog = logFolder.listFiles()
        if (listFileLog.isEmpty()) {
            return null
        }
        var latestFile = listFileLog[0]
        for (i in 1 until listFileLog.size) {
            if (latestFile.lastModified() < listFileLog[i].lastModified()) {
                latestFile = listFileLog[i]
            }
        }
        return latestFile
    }

    private fun getTimeOfLogFile(f: File): Date? {
        val startIndex = f.name.lastIndexOf("-") + 1
        val endIndex = f.name.lastIndexOf(".")
        if (startIndex in 0 until endIndex) {
            return getTimeFromDateFormat(f.name.substring(startIndex, endIndex))
        }
        return null
    }

    private fun getTimeFromDateFormat(source: String): Date {
        val dateFormat = SimpleDateFormat(DATE_FORMAT_PATERN, Locale.ENGLISH)
        try {
            return dateFormat.parse(source)
        } catch (e: Exception) {
            // parse date failed
            LogManager.i("Unable to parse log file date")
        }
        return Date()
    }

    fun writeLogs() {
        try {
            val file: File? = createFolderExternalStorage()
            if (file != null && process == null) {
                process = Runtime.getRuntime().exec("/system/bin/logcat -f ${file.absolutePath} -v time *:V")
            }
        } catch (e: Exception) {
            LogManager.i("Write logs failed")
        }
    }

    fun checkFolderExternalStorage(context: Context): Boolean {
        if (checkExternalStorageState()) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkRequestPermission(context)) {
                    return true
                } else {
                    requestPermission(context)
                }
            } else {
                return true
            }
        }
        return false
    }

    fun logErrorMessage(code: Int?, message: String?) {
        Log.i("PA onError", String.format(Locale.ENGLISH,
                "---------- ErrorCode: %s, ErrorMessage: %s",
                code,
                message
        ))
    }
}