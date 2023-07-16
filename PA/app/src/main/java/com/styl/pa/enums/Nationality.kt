package com.styl.pa.enums

import java.util.*

enum class Nationality(val nationalityName: String, val value: String) {

    BANGLADESHI("BANGLADESHI","BD"),
    BRUNEIAN("BRUNEIAN","BN"),
    BURMA("BURMA","BU"),
    CAMBODIAN("CAMBODIAN","CA"),
    CANADIAN("CANADIAN","CD"),
    CHINESE("CHINESE","CN"),
    GERMAN("GERMAN","DE"),
    HONGKONG("HONG KONG", "HK"),
    INDONESIAN("INDONESIAN", "ID"),
    INDIAN("INDIAN", "IN"),
    JAPANESE("JAPANESE", "JP"),
    KOREAN_NORTH("KOREAN, NORTH", "KP"),
    KOREAN_SOUTH("KOREAN, SOUTH", "KR"),
    LAOTIAN("LAOTIAN", "LA"),
    SRI_LANKAN("SRI LANKAN", "LK"),
    MAURITIAN("MAURITIAN", "MU"),
    MALAYSIAN("MALAYSIAN", "MY"),
    DUTCH("DUTCH","NL"),
    NEW_ZEALANDER("NEW ZEALANDER", "NZ"),
    SINGAPORE_CITIZEN("SINGAPORE CITIZEN", "SG"),
    SINGAPORE_PR("SINGAPORE PR", "PR"),
    AMERICAN("AMERICAN", "AM"),
    AMERICAN_SAMOA("AMERICAN SAMOA", "AS"),
    AUSTRIAN("AUSTRIAN", "AT"),
    AUSTRALIAN("AUSTRALIAN", "AU"),
    FILIPINO("FILIPINO", "PH"),
    PAKISTANI("PAKISTANI", "PK"),
    THAI("THAI", "TH"),
    TAIWANESE("TAIWANESE", "TW"),
    BRITISH_SUBJECT("BRITISH SUBJECT", "UK"),
    VIETNAMESE("VIETNAMESE", "VN"),
    FRANCE("FRANCE", "FR");


    companion object {
        const val OTHERS = "99"
        fun getNationality(nationality: String?): String {
            return CountryCode().getCode(nationality)
        }

        private class CountryCode {
            private val map = TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER)

            init {
                val listNationality = values()
                for(i in listNationality.indices) {
                    val nationality = listNationality[i]
                    map[nationality.nationalityName] = nationality.value
                }
            }

            fun getCode(nationality: String?): String {
                var code: String?
                if (nationality != null) {
                    code = map[nationality.uppercase(Locale.ENGLISH)]
                    if (code == null) {
                        code = OTHERS
                    }
                } else {
                    code = OTHERS
                }
                return code
            }
        }
    }

}