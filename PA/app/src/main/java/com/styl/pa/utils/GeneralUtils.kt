package com.styl.pa.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.*
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Handler
import android.text.TextUtils
import android.util.Base64
import android.util.Base64OutputStream
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.gson.Gson
import com.styl.pa.BuildConfig
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.R
import com.styl.pa.entities.ota.ConfigFirmware
import com.styl.pa.entities.ota.InfoFirmware
import com.styl.pa.interfaces.DownloadFilesListener
import com.styl.pa.modules.main.view.MainActivity
import net.lingala.zip4j.ZipFile
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.*
import java.security.*
import java.security.spec.MGF1ParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList


/**
 * Created by trangpham on 8/29/2018
 */
class GeneralUtils {

    companion object {

        const val COMMON_PERMISSION = BuildConfig.APPLICATION_ID + ".permission.BROADCAST_COMMON_PERMISSION"

        @ExcludeFromJacocoGeneratedReport
        fun isNetworkAvailable(context: Context?): Boolean {
            val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE)
            return if (connectivityManager is ConnectivityManager) {
                val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
                networkInfo?.isConnected ?: false
            } else false
        }

        @ExcludeFromJacocoGeneratedReport
        fun getScreenSize(activity: Activity?): DisplayMetrics {
            val metrics = DisplayMetrics()
            activity?.windowManager?.getDefaultDisplay()?.getMetrics(metrics)
            return metrics
        }

        @ExcludeFromJacocoGeneratedReport
        fun getScreenBarStatus(hasBar: Boolean, context: Context?): Int {
            var height = 0
            if (hasBar) {
                height -= ((context?.resources?.getDimensionPixelSize(R.dimen.dp_35) ?: 0) + getDefaultBar(context, true))
            } else {
                height -= context?.resources?.getDimensionPixelSize(R.dimen.dp_35) ?: 0
            }

            return height
        }

        fun formatToDate(date: String?): String {
            if (date.isNullOrEmpty())
                return ""

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            try {
                return outputFormat.format(inputFormat.parse(date))
            } catch (e: Exception) {
                LogManager.i("Error when formatting date $date from ${inputFormat.toPattern()} to ${outputFormat.toPattern()}")
                return date
            }
        }


        fun formatDateTimeToDate(date: String?): String {
            if (date.isNullOrEmpty())
                return ""

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            try {
                return outputFormat.format(inputFormat.parse(date))
            } catch (e: Exception) {
                LogManager.i("Error when formatting date $date from ${inputFormat.toPattern()} to ${outputFormat.toPattern()}")
                return date
            }
        }

        fun formatDateToNumber(date: String?): String {
            if (date.isNullOrEmpty())
                return ""

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            try {
                return outputFormat.format(inputFormat.parse(date))
            } catch (e: Exception) {
                LogManager.i("Error when formatting date $date from ${inputFormat.toPattern()} to ${outputFormat.toPattern()}")
                return date
            }
        }

        fun formatToDateTime(date: String?): String {
            if (date.isNullOrEmpty())
                return ""

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val outputFormat = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.ENGLISH)
            try {
                return outputFormat.format(inputFormat.parse(date))
            } catch (e: Exception) {
                LogManager.i("Error when formatting date $date from ${inputFormat.toPattern()} to ${outputFormat.toPattern()}")
                return date
            }
        }

        fun formatToDayMonth(date: String?): String {
            if (date.isNullOrEmpty())
                return ""

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val outputFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)
            try {
                return outputFormat.format(inputFormat.parse(date))
            } catch (e: Exception) {
                LogManager.i("Error when formatting date $date from ${inputFormat.toPattern()} to ${outputFormat.toPattern()}")
                return date
            }
        }

        fun formatToTime(date: String?): String {
            if (date.isNullOrEmpty())
                return ""

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val outputFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
            try {
                return outputFormat.format(inputFormat.parse(date))
            } catch (e: Exception) {
                LogManager.i("Error when formatting date $date from ${inputFormat.toPattern()} to ${outputFormat.toPattern()}")
                return date
            }

        }

        fun formatToWeeksday(date: String?): String {
            if (date.isNullOrEmpty())
                return ""

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val outputFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
            try {
                return outputFormat.format(inputFormat.parse(date))
            } catch (e: Exception) {
                LogManager.i("Error when formatting date $date from ${inputFormat.toPattern()} to ${outputFormat.toPattern()}")
                return date
            }
        }

        fun formatShortDay(date: String?): String {
            if (date.isNullOrEmpty())
                return ""

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val outputFormat = SimpleDateFormat("EEE", Locale.ENGLISH)
            try {
                return outputFormat.format(inputFormat.parse(date))
            } catch (e: Exception) {
                LogManager.i("Error when formatting date $date from ${inputFormat.toPattern()} to ${outputFormat.toPattern()}")
                return date
            }
        }

        fun formatToDefaultTime(date: String?): String {
            if (date.isNullOrEmpty())
                return ""

            var c = Calendar.getInstance()
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)

            try {
                val inputDate = inputFormat.parse(date)
                c.time = inputDate
                c.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                c.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE))
                c.set(Calendar.MILLISECOND, Calendar.getInstance().get(Calendar.MILLISECOND))

                return outputFormat.format(c.time)
            } catch (e: Exception) {
                LogManager.i("Error when formatting date $date from ${inputFormat.toPattern()} to ${outputFormat.toPattern()}")
                return date
            }

        }

        fun formatToTime(date: String?, sdfInputFormat: SimpleDateFormat, sdfOutputFormat: SimpleDateFormat): String {
            if (date.isNullOrEmpty())
                return ""

            try {
                return sdfOutputFormat.format(sdfInputFormat.parse(date))
            } catch (e: Exception) {
                LogManager.i("Error when formatting date $date from ${sdfInputFormat.toPattern()} to ${sdfOutputFormat.toPattern()}")
                return date
            }
        }

        fun eventFormatDate(fromDate: String?, toDate: String?, sdfInputFormat: SimpleDateFormat, sdfOutputFormat: SimpleDateFormat): String {
            var startTime = ""
            var endTime = ""
            if (!fromDate.isNullOrEmpty()) {
                startTime = formatToTime(fromDate, sdfInputFormat, sdfOutputFormat)
            }

            if (!toDate.isNullOrEmpty()) {
                endTime = formatToTime(toDate, sdfInputFormat, sdfOutputFormat)
            }

            if (startTime.isEmpty() && endTime.isEmpty()) {
                return ""
            } else {
                return (startTime + " ~ " + endTime)
            }
        }

        @ExcludeFromJacocoGeneratedReport
        fun locateView(v: View?): Rect? {
            val locInt = IntArray(2)
            if (v == null) return null

            v.getLocationOnScreen(locInt)

            val location = Rect()
            location.left = locInt[0]
            location.top = locInt[1]
            location.right = location.left + v.getWidth()
            location.bottom = location.top + v.getHeight()
            return location
        }

        fun formatAmountSymbols(symbols: String, number: Float, fractionDigits: Int): String {
            var df = DecimalFormat()
            df.groupingSize = 3
            df.maximumFractionDigits = fractionDigits
            df.minimumFractionDigits = fractionDigits
            var dfs = DecimalFormatSymbols()
            dfs.groupingSeparator = ','
            dfs.decimalSeparator = '.'
            df.decimalFormatSymbols = dfs
            return symbols + df.format(number)
        }

        @ExcludeFromJacocoGeneratedReport
        fun bitmapToString(bitmap: Bitmap?): String {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }

        @ExcludeFromJacocoGeneratedReport
        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        @ExcludeFromJacocoGeneratedReport
        fun hideSoftKeyboard(view: View?) {
            try {
                if (view != null) {
                    val inputMethodManager = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                }
            } catch (e: Exception) {
                LogManager.d("Hide soft keyboard failed")
            }

        }

        @ExcludeFromJacocoGeneratedReport
        fun mkdirs(file: File?): Boolean {
            try {
                return file?.mkdirs() ?: false
            } catch (e: Exception) {
                LogManager.i("Unable to make directory")
            }
            return false
        }

        inline fun <reified T> convertStringToObject(data: String?): T? {
            if (TextUtils.isEmpty(data))
                return null
            val gson = Gson()
            return gson.fromJson(data, T::class.java)
        }

        inline fun <reified T> convertObjectToString(data: T?): String? {
            if (data == null){
                return null
            }
            val gson = Gson()
            return gson.toJson(data)
        }

        @ExcludeFromJacocoGeneratedReport
        fun hideBottomBar(v: View, activity: Activity) {
            v.viewTreeObserver.addOnGlobalLayoutListener(@ExcludeFromJacocoGeneratedReport {
                val r = Rect()
                v.getWindowVisibleDisplayFrame(r)
                if (v.rootView.height - (r.bottom - r.top) > 300) {
                    (activity as MainActivity).showBottomBar(false)
                } else {
                    (activity as MainActivity).showBottomBar(true)
                }
            })
        }

        @ExcludeFromJacocoGeneratedReport
        fun getDefaultBar(context: Context?, isStatusBar: Boolean): Int {
            if (isStatusBar) {
                var statusBarHeight = 0
                var resourceId = 0

                if (context?.resources?.getIdentifier("status_bar_height", "dimen", "android") != null) {
                    resourceId = context.resources?.getIdentifier("status_bar_height", "dimen", "android")!!
                }

                if (resourceId > 0 && context?.resources?.getDimensionPixelSize(resourceId) != null) {
                    statusBarHeight = context.resources?.getDimensionPixelSize(resourceId)!!
                }
                return statusBarHeight

            } else {
                var navigationBarHeight = 0
                var resourceId = 0

                if (context?.resources?.getIdentifier("navigation_bar_height", "dimen", "android") != null) {
                    resourceId = context.resources?.getIdentifier("navigation_bar_height", "dimen", "android")!!
                }

                if (resourceId > 0 && context?.resources?.getDimensionPixelSize(resourceId) != null) {
                    navigationBarHeight = context.resources?.getDimensionPixelSize(resourceId)!!
                }
                return navigationBarHeight
            }
        }

        @ExcludeFromJacocoGeneratedReport
        fun isAppInBackground(context: Context): Boolean {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningAppProcessInfos = am.runningAppProcesses
            for (processInfo in runningAppProcessInfos) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == context.packageName) {
                            return false
                        }
                    }
                }
            }
            return true
        }

        @ExcludeFromJacocoGeneratedReport
        fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
            val width = bm.width
            val height = bm.height
            val scaleWidth = newWidth.toFloat() / width
            val scaleHeight = (newWidth.toFloat() * height / width) / height
            // CREATE A MATRIX FOR THE MANIPULATION
            val matrix = Matrix()
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight)

            // "RECREATE" THE NEW BITMAP
            if (bm != null && !bm.isRecycled) {
                val resizedBitmap = Bitmap.createBitmap(
                        bm, 0, 0, width, height, matrix, false)
                bm.recycle()
                return resizedBitmap
            }
            return bm
        }

        fun getCurrentDate(): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date = Date()
            return dateFormat.format(date)
        }

        @ExcludeFromJacocoGeneratedReport
        fun checkBitmapEmpty(bitmap: Bitmap?): Boolean {
            if (bitmap != null) {
                return bitmap.sameAs(Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config))
            } else {
                return true
            }
        }

        @ExcludeFromJacocoGeneratedReport
        fun trimBitmap(bmp: Bitmap): Bitmap {
            val imgHeight = bmp.height
            val imgWidth = bmp.width


            //TRIM WIDTH - LEFT
            var startWidth = 0
            for (x in 0 until imgWidth) {
                if (startWidth == 0) {
                    for (y in 0 until imgHeight) {
                        if (bmp.getPixel(x, y) != Color.TRANSPARENT) {
                            startWidth = x
                            break
                        }
                    }
                } else
                    break
            }


            //TRIM WIDTH - RIGHT
            var endWidth = 0
            for (x in imgWidth - 1 downTo 0) {
                if (endWidth == 0) {
                    for (y in 0 until imgHeight) {
                        if (bmp.getPixel(x, y) != Color.TRANSPARENT) {
                            endWidth = x
                            break
                        }
                    }
                } else
                    break
            }


            //TRIM HEIGHT - TOP
            var startHeight = 0
            for (y in 0 until imgHeight) {
                if (startHeight == 0) {
                    for (x in 0 until imgWidth) {
                        if (bmp.getPixel(x, y) != Color.TRANSPARENT) {
                            startHeight = y
                            break
                        }
                    }
                } else
                    break
            }


            //TRIM HEIGHT - BOTTOM
            var endHeight = 0
            for (y in imgHeight - 1 downTo 0) {
                if (endHeight == 0) {
                    for (x in 0 until imgWidth) {
                        if (bmp.getPixel(x, y) != Color.TRANSPARENT) {
                            endHeight = y
                            break
                        }
                    }
                } else
                    break
            }


            val bitmap = Bitmap.createBitmap(
                    bmp,
                    startWidth,
                    startHeight,
                    endWidth - startWidth,
                    endHeight - startHeight
            )

            val newBitmap: Bitmap = Bitmap.createBitmap(
                    bitmap.width + 100,
                    bitmap.height, bitmap.config
            )

            val canvas = Canvas(newBitmap)
            canvas.drawColor(Color.WHITE)
            canvas.drawBitmap(bitmap, 50f, 0f, null)

            return newBitmap
        }

        @ExcludeFromJacocoGeneratedReport
        fun parsePdfToBase64(file: File): String {
            var output = ""
            FileInputStream(file).use { inputStream ->
                ByteArrayOutputStream().use { outputStream ->
                    Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
                        inputStream.copyTo(base64FilterStream)
                        base64FilterStream.flush()
                        output = outputStream.toString()
                        outputStream.close()
                        base64FilterStream.close()
                    }
                }
                inputStream.close()
            }
            return output
        }

        @ExcludeFromJacocoGeneratedReport
        fun disableViewToPreventMultiTouch(v: View?) {
            v?.setEnabled(false)
            Handler().postDelayed({ v?.setEnabled(true) }, 300)
        }

        fun convertDateToAge(date: String?): String? {
            if (date.isNullOrEmpty())
                return null

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            try {
                return getAgeFromDateOfBirth(inputFormat.parse(date).time).toString()
            } catch (e: Exception) {
                LogManager.i("Error when convert date to age")
                return null
            }
        }

        private fun getAgeFromDateOfBirth(dateOfBirth: Long): Int {
            val dob = Calendar.getInstance()
            val currentTime = Calendar.getInstance()
            dob.timeInMillis = dateOfBirth

            var age = currentTime.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
            if (dob.get(Calendar.MONTH) > currentTime.get(Calendar.MONTH) || (dob.get(Calendar.MONTH) == currentTime.get(Calendar.MONTH) && dob.get(Calendar.DATE) > currentTime.get(Calendar.DATE))) {
                age--
            }
            return age
        }

        @ExcludeFromJacocoGeneratedReport
        fun unZipFile(file: File, folderName: String, task: DownloadFilesListener) {

            try {
                val parentFolder = file.parentFile.path

                val zipPath = "$parentFolder/$folderName"
                val parentFile = File(zipPath)
                if (!parentFile.deleteRecursively()) {
                    LogManager.d("Cannot delete old unzip folder")
                }
                if (!parentFile.exists() && !parentFile.mkdir()) {
                    LogManager.i("Cannot create unzip folder")
                }

                val zipFile = ZipFile(file)
                zipFile.extractAll(zipPath)
                task.onUnzipSuccess(zipPath)
            } catch (e: Exception) {
                task.onUnzipFailed()
            }
        }

        fun getSHA256File(file: File): String {
            var result = ""
            if (file.exists()) {
                val buffer = ByteArray(8192)
                val md = MessageDigest.getInstance("SHA-256")
                val bis = BufferedInputStream(FileInputStream(file))
                var count: Int
                while (bis.read(buffer).also { count = it } >= 0) {
                    md.update(buffer, 0, count)
                }
                bis.close()
                result = bytesToHexString(md.digest(), "") ?: ""
            }
            return result
        }

        fun bytesToHexString(byteArray: ByteArray?, separator: String = " "): String? {
            return byteArray?.joinToString(separator) {
                java.lang.String.format("%02x", it)
            }
        }

        @ExcludeFromJacocoGeneratedReport
        fun parseXmlOtaPackage(inputFile: File): ConfigFirmware {
            var result = ConfigFirmware()
            try {
                val parserFactory = XmlPullParserFactory.newInstance()
                parserFactory.isNamespaceAware = true
                val parser: XmlPullParser = parserFactory.newPullParser()
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)
                FileInputStream(inputFile).use {
                    parser.setInput(it, null)
                    result = processingParse(parser)
                }
            } catch (e: Exception) {
                LogManager.i("Error when parsing xml file")
            }
            return result
        }

        @ExcludeFromJacocoGeneratedReport
        private fun processingParse(parser: XmlPullParser): ConfigFirmware {
            val result = ConfigFirmware()
            val firmwares = ArrayList<InfoFirmware>()
            val ids = ArrayList<String>()
            var infoFirmware: InfoFirmware? = null
            var tag: String?
            var text: String? = ""
            var event = parser.eventType
            var infoType = 0
            while (event != XmlPullParser.END_DOCUMENT) {
                tag = parser.name
                when (event) {
                    XmlPullParser.START_TAG -> {
                        if (tag == "information" && parser.attributeCount > 0) {
                            infoType = 0
                            result.version = parser.getAttributeValue(null, "version")
                        } else if (tag == "firmwares") {
                            infoType = 1
                        } else if (tag == "firmware" && parser.attributeCount > 0) {
                            val type = parser.getAttributeValue(null, "type")
                            infoFirmware = InfoFirmware(type)
                            val version = parser.getAttributeValue(null, "version")
                            infoFirmware.version = version
                        }
                    }
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> {
                        when {
                            tag == "date-time" -> {
                                result.date = text
                            }
                            tag == "path" -> {
                                if (infoType == 0) {
                                    result.path = text
                                } else {
                                    infoFirmware?.path = text
                                }
                            }
                            tag == "checksum" -> {
                                if (infoType == 0) {
                                    result.checksum = text
                                } else {
                                    infoFirmware?.checksum = text
                                }
                            }
                            tag == "id" && text != null && text.trim().isNotEmpty() -> {
                                ids.add(text.trim())
                            }
                            tag == "firmware" -> {
                                if (infoFirmware != null) {
                                    firmwares.add(infoFirmware)
                                }
                            }
                            else -> {
                                LogManager.d("SKIP XML end tag: $tag")
                            }
                        }
                    }
                }
                event = parser.next()
            }
            result.firmwares = firmwares
            result.kioskIds = ids
            return result
        }

        @ExcludeFromJacocoGeneratedReport
        fun getFileWithEnds(folder: String, end: String): File? {
            val file = File(folder)
            if (file.isDirectory && file.listFiles() != null) {
                for (child in file.listFiles()) {
                    if (child.name.endsWith(end)) {
                        return child
                    }
                }
            }
            return null

        }

        fun standardizeVersion(version: String): String {
            return version.replace(Regex(".sit|.uat"), "")
        }

        fun needUpdateFirmware(currentVersion: String, downloadVersion: String): Boolean {

            if (currentVersion.isEmpty()) return true

            if (downloadVersion.isEmpty()) return false

            val currVersion = standardizeVersion(currentVersion)
            val checkVersion = standardizeVersion(downloadVersion)

            val arrCurrent = currVersion.split(".")
            val arrDownload = checkVersion.split(".")
            val length = Math.max(arrCurrent.size, arrDownload.size)
            for (i in 0..length) {
                var currentNum = 0
                if (i < arrCurrent.size) {
                    try {
                        currentNum = Integer.parseInt(arrCurrent[i])
                    } catch (e: Exception) {
                        continue
                    }
                }

                var downloadNum = 0
                if (i < arrDownload.size) {
                    try {
                        downloadNum = Integer.parseInt(arrDownload[i])
                    } catch (e: Exception) {
                        continue
                    }
                }

                if (currentNum < downloadNum) {
                    return true
                } else if (currentNum > downloadNum) {
                    return false
                }
            }
            return false
        }

        fun getColorWithAlpha(color: Int, ratio: Float): Int {
            var newColor = 0
            val alpha = Math.round(Color.alpha(color) * ratio)
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)
            newColor = Color.argb(alpha, r, g, b)
            return newColor
        }

        @ExcludeFromJacocoGeneratedReport
        fun readOTADecryptInfo(inputFile: String, posStart: Long): ByteArray? {
            return try {
                readFileFromPosition(inputFile, posStart)
            } catch (e: IOException) {
                null
            }
        }

        @ExcludeFromJacocoGeneratedReport
        @Throws(IOException::class)
        fun readFileFromPosition(inputFile: String, posStart: Long): ByteArray? {
            val file = File(inputFile)
            var ret: ByteArray? = null
            if (file.exists() && file.isFile) {
                var rand: RandomAccessFile? = null
                var bos: ByteArrayOutputStream? = null
                try {
                    rand = RandomAccessFile(file, "r")
                    bos = ByteArrayOutputStream()
                    rand.seek(posStart)
                    val buf = ByteArray(1024)
                    var read: Int
                    while (rand.read(buf).also { read = it } != -1) {
                        bos.write(buf, 0, read)
                    }
                    ret = bos.toByteArray()
                } catch (e: Exception) {
                    LogManager.i("Unable to read for start from position")
                    ret = null
                } finally {
                    bos?.close()
                    rand?.close()
                }
            }
            return ret
        }


        @ExcludeFromJacocoGeneratedReport
        fun readAndDecryptOTAPackage(file: File, outputFilePath: String, position: Long,
                                     iv: ByteArray?, tag: ByteArray?, aesKey: SecretKeySpec): File? {
            return try {
                readAndDecryptFileToPosition(file, outputFilePath, position, iv, tag, aesKey)
            } catch (e: IOException) {
                null
            }
        }

        @ExcludeFromJacocoGeneratedReport
        @Throws(IOException::class)
        fun readAndDecryptFileToPosition(file: File, outputFilePath: String, position: Long,
                                         iv: ByteArray?, tag: ByteArray?, aesKey: SecretKeySpec): File? {
            val outFile = File(outputFilePath)
            var raf: RandomAccessFile? = null
            var fos: FileOutputStream? = null
            var retFile: File? = null
            try {
                raf = RandomAccessFile(file, "r")
                fos = FileOutputStream(outputFilePath)
                val cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC")
                cipher.init(Cipher.DECRYPT_MODE, aesKey, GCMParameterSpec(16 * 8, iv))
                var totalRemainingBytes = file.length() - position
                var offset = 0
                var n = 0
                val b = ByteArray(8192)
                while (totalRemainingBytes > 0) {
                    if (totalRemainingBytes >= b.size) {
                        n = raf.read(b)
                    } else {
                        raf.seek(offset.toLong())
                        n = raf.read(b, 0, totalRemainingBytes.toInt())
                    }
                    val dec = cipher.update(b, 0, n)
                    fos.write(dec)
                    offset += n
                    totalRemainingBytes -= n
                }
                fos.write(cipher.doFinal(tag))
                retFile = outFile
            } catch (e: Exception) {
                retFile = null
                if (outFile.exists() && !outFile.delete()) {
                    LogManager.i("Delete file failed")
                }
                LogManager.i("Decrypt file failed!")
            } finally {
                raf?.close()
                fos?.close()
            }
            return retFile
        }

        fun decryptRsa(input: ByteArray?, privateKey: PrivateKey): ByteArray {
            val sp = OAEPParameterSpec(
                    "SHA-256",
                    "MGF1",
                    MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT
            )
            val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
            cipher.init(Cipher.DECRYPT_MODE, privateKey, sp)
            return cipher.doFinal(input)
        }

        fun isIsoLanguage(language: String?): Boolean {
            return Locale.getISOLanguages().contains(language)
        }

        @ExcludeFromJacocoGeneratedReport
        fun removeRsaKeyHeaderAndFooter(key: String): String {
            if (key.isEmpty()) {
                return key
            }
            var startIndex = 28
            var endIndex = 26
            if (key.contains(" RSA ")) {
                startIndex = 32
                endIndex = 30
            }
            return key.substring(startIndex, key.length - endIndex)
        }

        @ExcludeFromJacocoGeneratedReport
        fun getKeyFromAsset(inputStream: InputStream): PrivateKey? {
            var privateKey: PrivateKey? = null
            try {
                var content = ""
                inputStream.use {
                    InputStreamReader(it).use { inputStreamReader ->
                        BufferedReader(inputStreamReader).use { reader ->
                            content = reader.readText()
                        }
                    }
                }
                if (content.isNotEmpty()) {
                    val keyBase64 = Base64.decode(content, Base64.DEFAULT)
                    val keyBase64Str = String(keyBase64, Charsets.UTF_8)
                    val keyStr = removeRsaKeyHeaderAndFooter(keyBase64Str) // remove header+footer of RSA key
                    val rawKeyContent = Base64.decode(keyStr, Base64.DEFAULT)
                    val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
                    val keySpec = PKCS8EncodedKeySpec(rawKeyContent)
                    privateKey = keyFactory.generatePrivate(keySpec)
                }
            } catch (e: IOException) {
                LogManager.d("Unable to get RSA key")
            }
            return privateKey
        }

        fun isValidEmailAddress(email: String?): Boolean {
            if (email.isNullOrEmpty()) {
                return false
            }
            val expression = "^\\w{1}.*\\w{1}\\@([\\w\\-]+\\.)+\\w{2,}$"

            val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(email)
            return matcher.matches()
        }

        fun isSimulateTerminal(): Boolean { // check in build.gradle
            return BuildConfig.FLAVOR == "uat" && BuildConfig.SIMULATE_TERMINAL
        }
    }
}