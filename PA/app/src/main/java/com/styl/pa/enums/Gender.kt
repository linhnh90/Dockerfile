package com.styl.pa.enums

enum class Gender(var value: Int) {
    MALE(1),
    FEMALE(2),
    OTHERS(953830000);

    companion object {
        fun getGender(gender: String?): String {
            return when {
                gender?.equals(MALE.name, true) == true -> {
                    MALE.value.toString()
                }
                gender?.equals(FEMALE.name, true) == true -> {
                    FEMALE.value.toString()
                }
                else -> {
                    OTHERS.value.toString()
                }
            }
        }
    }
}