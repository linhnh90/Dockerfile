package com.styl.pa.enums

import org.junit.Test

class DaysTypeTest {

    @Test
    fun daysType_test() {
        var dateType = DaysType.valueOf(0)
        assert(dateType == DaysType.SUNDAY)
        dateType = DaysType.valueOf(1)
        assert(dateType == DaysType.MONDAY)
        dateType = DaysType.valueOf(2)
        assert(dateType == DaysType.TUESDAY)
        dateType = DaysType.valueOf(3)
        assert(dateType == DaysType.WEDNESDAY)
        dateType = DaysType.valueOf(4)
        assert(dateType == DaysType.THURSDAY)
        dateType = DaysType.valueOf(5)
        assert(dateType == DaysType.FRIDAY)
        dateType = DaysType.valueOf(6)
        assert(dateType == DaysType.SATURDAY)
    }

}