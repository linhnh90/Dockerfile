package com.styl.pa.enums

enum class RaceType(var type: Int) {
    CHINESE(1),
    MALAY(2),
    INDIAN(3),
    OTHERS(99);

    companion object {
        fun getRaceType(race: String?): String {
            return when {
                race?.equals(CHINESE.name, true) == true -> {
                    CHINESE.type.toString()
                }
                race?.equals(MALAY.name, true) == true -> {
                    MALAY.type.toString()
                }
                race?.equals(INDIAN.name, true) == true -> {
                    INDIAN.type.toString()
                }
                else -> {
                    OTHERS.type.toString()
                }
            }
        }
    }
}