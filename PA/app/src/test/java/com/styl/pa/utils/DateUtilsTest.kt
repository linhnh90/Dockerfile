package com.styl.pa.utils

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class DateUtilsTest {

    @Test
    fun convertMillisToString() {
        val date = DateUtils.convertMillisToString(1632376705586)
        assert(date.isNotEmpty())
    }

    @Test
    fun convertLongToString_normal() {
        val date = DateUtils.convertLongToString(1632376705586, "dd/MM/yyyy")
        assert(date.isNotEmpty())
    }

    @Test
    fun convertLongToString_time_0() {
        val date = DateUtils.convertLongToString(0, "dd/MM/yyyy")
        assert(date.isEmpty())
    }

    @Test
    fun parseStringDate() {
        val date = DateUtils.parseStringDate(3, 1, 2021, "dd/MM/yyyy")
        assert(date.isNotEmpty())
    }

    @Test
    fun convertStringToMillisecond_normal() {
        val date = DateUtils.convertStringToMillisecond("2021-09-23", "yyyy-MM-dd")
        assert(date != 0L)
    }

    @Test
    fun convertStringToMillisecond_wrong_format() {
        val date = DateUtils.convertStringToMillisecond("23-09-2021", "dd/MM/yyyy")
        assert(date != 0L)
    }

    @Test
    fun formatDateLocal() {
        val date = DateUtils.formatDateLocal(Date(1632376705586))
        assert(date.isNotEmpty())
    }
}