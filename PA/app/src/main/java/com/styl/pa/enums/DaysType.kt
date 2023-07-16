package com.styl.pa.enums

import kotlin.collections.HashMap

enum class DaysType {
    SUNDAY(0),
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6);

    constructor(value: Int) {
        this.value = value
    }

    var value: Int = -1

    companion object {
        val map: MutableMap<Int, DaysType> = HashMap()

        init {
            for (i in DaysType.values()) {
                map[i.value] = i
            }
        }

        fun valueOf(type: Int?): DaysType? {
            return map[type]
        }
    }

}