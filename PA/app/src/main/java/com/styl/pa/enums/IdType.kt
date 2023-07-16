package com.styl.pa.enums

enum class IdType(var type: Int) {
    NRIC(1),
    PASSPORT(2),
    FIN(3);

    companion object {
        fun getIdType(type: String?): String {
            return when {
                type?.equals(NRIC.name, true) == true -> {
                    NRIC.type.toString()
                }
                type?.equals(FIN.name, true) == true -> {
                    FIN.type.toString()
                }
                else -> {
                    PASSPORT.type.toString()
                }
            }
        }
    }
}