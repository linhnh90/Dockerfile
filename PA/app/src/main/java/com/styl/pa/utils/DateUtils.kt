package com.styl.pa.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    val DATE_FORMAT = "dd/MM/yyyy"
    val DATE_FORMAT_2 = "dd MMM, yyyy"
    val DATE_FORMAT_3 = "yyyy-MM-dd"
    private val ONE_SECOND = (1 * 1000).toLong()
    private val ONE_MINUTE = ONE_SECOND * 60
    private val ONE_HOUR = ONE_MINUTE * 60
    private val ONE_DAY = ONE_HOUR * 24

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat(DATE_FORMAT)

        return dateFormat.format(Date())
    }

    fun convertMillisToString(time: Long): String {
        val dateFormat = SimpleDateFormat(DATE_FORMAT)

        return dateFormat.format(time)
    }

    fun convertLongToString(time: Long, format: String): String {
        if (time == 0L) {
            return ""
        }
        val dateFormat = SimpleDateFormat(format)

        return dateFormat.format(Date(time))
    }

    fun parseStringDate(day: Int, month: Int, year: Int, dateFormat: String): String {
        val simpleDateFormat = SimpleDateFormat(dateFormat)
        return simpleDateFormat.format(GregorianCalendar(year, month, day).timeInMillis)
    }

    fun convertStringToMillisecond(dateString: String, formatDate: String): Long {
        var convertedDate = Date()
        val dateFormat = SimpleDateFormat(formatDate)
        try {
            convertedDate = dateFormat.parse(dateString)
        } catch (e: ParseException) {
            LogManager.d("Parse string date failed")
        }

        return convertedDate.time
    }

    fun formatDateLocal(date: Date): String {
        var df = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return df.format(date).toString()
    }
}