package com.styl.pa.modules.peripheralsManager

import android.app.Activity
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.security.keystore.KeyProperties
import android.text.TextUtils
import android.util.Base64
import android.view.inputmethod.InputMethodManager
import com.google.gson.Gson
import com.styl.pa.utils.HexBytesUtils
import com.styl.pa.utils.LogManager
import org.json.JSONArray
import java.io.*
import java.net.URLDecoder
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList
import kotlin.experimental.and

class GeneralUtils {
    companion object {

        private const val FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss"
        const val FORMAT_DATE_TIME_2 = "yyyy-MM-dd"

        fun hexToString(data: ByteArray?): String {
            return data?.toHexString() ?: ""
        }

        fun reverseBytesToString(bytes: ByteArray?): String {
            var reVal = ""
            if (bytes != null && bytes.size > 0) {
                for (i in bytes.size - 1 downTo 0) {
                    reVal = (reVal + Integer.toHexString((bytes[i].toInt() shr 4) and 0xf)
                            + Integer.toHexString((bytes[i] and 0xf).toInt()))
                }
            }
            return reVal
        }

        fun reverseBytes(bytes: ByteArray?): ByteArray? {
            if (bytes == null) {
                return bytes
            }

            var reVal: ByteArray = ByteArray(bytes?.size!!)
            var index = 0
            for (i in bytes.size - 1 downTo 0) {
                reVal[index] = bytes[i]
                index = index.inc()
            }
            return reVal
        }

        fun ByteArray.toHexString(): String {
            return this.joinToString(" ") {
                java.lang.String.format("%02x", it)
            }
        }

        fun Int.toHexString(): String {
            var hexString = Integer.toHexString(this)
            if (hexString.length < 4) {
                for (index in 0 until 4 - hexString.length) {
                    hexString = "0$hexString"
                }
            }
            return hexString
        }

        fun convertIntToByte(data: Int?): Byte {
            return data?.toByte() ?: 0
        }

        fun convertIntToTwoByte(value: Int?): ByteArray {
            var data = ByteArray(2)

            if (value != null) {
                data[0] = (value and 0xFF).toByte()
                data[1] = (value shr 8 and 0xFF).toByte()
            }

            return data
        }

        fun convertIntToByteArray(data: Int?, length: Int): ByteArray {
            if (data == null) {
                return ByteArray(length)
            }

            var value = ByteArray(length)
            var d = length - 1
            for (i in 0 until length) {
                value[i] = (data!! shr (d * 8) and 0xff).toByte()
                d = d.dec()
            }

            return value
        }

        @ExperimentalUnsignedTypes
        fun convertByteArrayToInt(data: ByteArray?): Int {
            if (data == null) {
                return 0
            }

            var value = 0
            val length = data.size
            for (i in 0 until length) {
                value = value shl 8
                value = value or ((data[i] and 0xff.toByte()).toUByte().toInt())
//                value = value or (data[i] and 0xFF.toByte()).toInt()
            }
            return value
        }

        fun convertIntToHexString(data: Int?, byteFormat: Int): String {
            val byteArrayData = convertIntToByteArray(data, byteFormat)
            val hexString = HexBytesUtils.bytesToHex(byteArrayData)
            return hexString.replace("0x", "").replace(" ", "")
        }

        fun generateSignature(
                secretKey: String,
                content: String?,
                queryString: String?,
                nonce: String
        ): String {
            return generateSignature(secretKey, content?.toByteArray(), queryString, nonce)
        }

        private fun generateSignature(
                secretKey: String,
                content: ByteArray?,
                queryString: String?,
                nonce: String
        ): String {
            try {
                if (secretKey.length > 0) {
                    // generate signature
                    val sha256HMAC = Mac.getInstance("HmacSHA256")
                    val secretKeySpec =
                            SecretKeySpec(secretKey.toByteArray(charset("UTF-8")), "HmacSHA256")
                    sha256HMAC.init(secretKeySpec)

                    sha256HMAC.update(content)
                    if (queryString != null) {
                        sha256HMAC.update(queryString.toByteArray())
                    }

                    val bytes = sha256HMAC.doFinal(nonce.toByteArray())
                    return String(Base64.encode(bytes, Base64.NO_WRAP))
                }
            } catch (e: java.lang.Exception) {
                LogManager.i("Error when generate HmacSHA256 signature")
                return ""
            }

            return ""
        }

        fun getCurrentDate(): Date {
            return Calendar.getInstance().time
        }

        fun getCurrentTimeStamp(): Long {
            return Calendar.getInstance().timeInMillis
        }

        private val ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm"
        fun randomString(length: Int): String {
            var sb = StringBuilder(length)
            var random = SecureRandom()
            for (i in 0..length - 1) {
                sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
            }
            return sb.toString()
        }

        fun randomBetween(min: Int, max: Int): Int {
            var random = SecureRandom()
            return random.nextInt(max - min) + min
        }

        inline fun <reified T> convertStringToObject(data: String?): T? {
            if (TextUtils.isEmpty(data))
                return null
            var gson = Gson()
            return gson.fromJson(data, T::class.java)
        }

        inline fun <reified T> convertObjectToString(data: T): String? {
            val gson = Gson()
            return gson.toJson(data, T::class.java)
        }

        inline fun <reified T> convertStringToListObject(data: String?): ArrayList<T>? {
            var listData = ArrayList<T>()
            if (!TextUtils.isEmpty(data)) {
                var datas = JSONArray(data)
                for (d in 0..datas.length() - 1) {
                    var value = convertStringToObject<T>(datas.get(d).toString())
                    if (value != null) {
                        listData.add(value)
                    }
                }
            }
            return listData
        }

        inline fun <reified T> convertListObjectToString(data: ArrayList<T>?): String? {
            var jsonObjects = JSONArray()
            if (data != null) {
                for (d in data) {
                    jsonObjects.put(convertObjectToString(d))
                }
            }
            return jsonObjects.toString()
        }

        fun convertStringToInt(data: String?): Int {
            try {
                var value = 0
                if (!TextUtils.isEmpty(data)) {
                    value = data!!.toInt()
                }
                return value
            } catch (e: Exception) {
                LogManager.i("Error when formatting number from string")
                return 0
            }
        }

        fun formatStringTwoPlaces(data: Int): String {
            return String.format("%02d", data)
        }

        fun formatAmountSymbols(symbols: String, number: Double?, fractionDigits: Int): String {
            if (number == null)
                return "0"

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

        fun convertCentToDollar(cent: Long?): Double {
            return (cent ?: 0).toDouble() / 100
        }

        fun generateKeyPair(): KeyPair {
            val generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA)

            generator.initialize(2048, SecureRandom())
            val keypair = generator.genKeyPair()
            return keypair
        }

        fun generatePublicKey(keyPair: KeyPair?): String {
            return String(Base64.encode(keyPair?.public?.encoded, Base64.NO_WRAP))
        }

        fun urlDecode(url: String?): String {
            if (TextUtils.isEmpty(url)) {
                return ""
            }
            var temp = URLDecoder.decode(url, "UTF-8")
            return temp.replace(" ", "%20")
        }

/*
        fun certificatesSSL() {
            // Create a new trust manager that trust all certificates
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(
                        chain: Array<out X509Certificate>?,
                        authType: String?
                ) {
                }

                override fun checkServerTrusted(
                        chain: Array<out X509Certificate>?,
                        authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate>? {
                    return null
                }

            })

            // Activate the new trust manager
            try {
                val sc = SSLContext.getInstance("SSL")
                sc.init(null, trustAllCerts, java.security.SecureRandom())
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())
            } catch (e: Exception) {
            }

        }
*/

        fun hideKeyboard(activity: Activity?, dialog: Dialog?) {
            if (activity != null) {
                var imm =
                        activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                var view = dialog?.window?.decorView
                imm.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)
            }
        }

        fun formatToMinutes(time: Long): String {
            return String.format(
                    Locale.ENGLISH, "%02d:%02d ",
                    TimeUnit.MILLISECONDS.toMinutes(time) % 60,
                    TimeUnit.MILLISECONDS.toSeconds(time) % 60
            )
        }

        fun formatDate(time: Long?): String {
            if (time != null && time > 0) {
                var ft = SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.ENGLISH)
                return ft.format(time)
            }

            return ""
        }

        fun formatDate(time: Long?, format: String): String {
            if (time != null && time > 0) {
                var ft = SimpleDateFormat(format, Locale.ENGLISH)
                return ft.format(time)
            }

            return ""
        }


        fun getPathStorage(): String {
            var extStore = Environment.getExternalStorageDirectory()
            var url = extStore.path
            return url
        }

        fun convertHourTo24H(hour: String?, am: String?): Int {
            try {
                var value = 0
                if (!TextUtils.isEmpty(hour)) {
                    value = hour!!.toInt()
                }

                if (am.equals("AM")) {
                    return value
                } else {
                    return (value + 12)
                }
            } catch (e: Exception) {
                LogManager.i("Format hour failed")
            }
            return 0
        }

        fun convertBitmapToByteArray(bitmap: Bitmap?): ByteArray? {
            if (bitmap == null) {
                return null
            }
            var outputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            return outputStream.toByteArray()
        }

        fun convertByteArrayToBitmap(bytes: ByteArray?): Bitmap? {
            if (bytes == null) {
                return null
            }
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

        fun getCurrentTimeLog(): String {
            val ft = SimpleDateFormat(FORMAT_DATE_TIME, Locale.ENGLISH)
            return ft.format(System.currentTimeMillis())
        }

        fun replaceNoneNumbericCharacter(text: String?): String {
            val re = Regex("[^0-9]")
            return re.replace(text ?: "0", "")
        }

        @ExperimentalUnsignedTypes
        fun convertShortToBytes(value: Short): ByteArray {
            val mBytes = ByteArray(2)
            mBytes[0] = ((value.toInt() shr 8) and 0xff).toByte()
            mBytes[1] = (value and 0xff).toByte()
            return mBytes
        }

    }
}