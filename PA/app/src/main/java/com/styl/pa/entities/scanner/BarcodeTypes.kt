package com.example.user.printtest.utils

class BarcodeTypes {

    companion object {

        val ST_NOT_APP = 0x00
        val ST_CODE_39 = 0x01
        val ST_CODABAR = 0x02
        val ST_CODE_128 = 0x03
        val ST_D2OF5 = 0x04
        val ST_IATA = 0x05
        val ST_I2OF5 = 0x06
        val ST_CODE93 = 0x07
        val ST_UPCA = 0x08
        val ST_UPCE0 = 0x09
        val ST_EAN8 = 0x0a
        val ST_EAN13 = 0x0b
        val ST_CODE11 = 0x0c
        val ST_CODE49 = 0x0d
        val ST_MSI = 0x0e
        val ST_EAN128 = 0x0f
        val ST_UPCE1 = 0x10
        val ST_PDF417 = 0x11
        val ST_CODE16K = 0x12
        val ST_C39FULL = 0x13
        val ST_UPCD = 0x14
        val ST_TRIOPTIC = 0x15
        val ST_BOOKLAND = 0x16
        val ST_COUPON = 0x17
        val ST_NW7 = 0x18
        val ST_ISBT128 = 0x19
        val ST_MICRO_PDF = 0x1a
        val ST_DATAMATRIX = 0x1b
        val ST_QR_CODE = 0x1c
        val ST_MICRO_PDF_CCA = 0x1d
        val ST_POSTNET_US = 0x1e
        val ST_PLANET_CODE = 0x1f
        val ST_CODE_32 = 0x20
        val ST_ISBT128_CON = 0x21
        val ST_JAPAN_POSTAL = 0x22
        val ST_AUS_POSTAL = 0x23
        val ST_DUTCH_POSTAL = 0x24
        val ST_MAXICODE = 0x25
        val ST_CANADIN_POSTAL = 0x26
        val ST_UK_POSTAL = 0x27
        val ST_MACRO_PDF = 0x28
        val ST_MACRO_QR_CODE = 0x29
        val ST_MICRO_QR_CODE = 0x2c
        val ST_AZTEC = 0x2d
        val ST_AZTEC_RUNE_CODE = 0x2E
        val ST_FRENCH_LOTTERY = 0x2F
        val ST_RSS14 = 0x30
        val ST_RSS_LIMITET = 0x31
        val ST_RSS_EXPANDED = 0x32
        val ST_PARAMETER_FNC3 = 0x33
        val ST_4STATE_US = 0x34
        val ST_4STATE_US4 = 0x35
        val ST_ISSN = 0x36
        val ST_SCANLET = 0x37
        val ST_CUE_CAT_CODE = 0x38
        val ST_MATRIX_2_OF_5 = 0x39
        val ST_UPCA_2 = 0x48
        val ST_UPCE0_2 = 0x49
        val ST_EAN8_2 = 0x4a
        val ST_EAN13_2 = 0x4b
        val ST_UPCE1_2 = 0x50
        val ST_CCA_EAN128 = 0x51
        val ST_CCA_EAN13 = 0x52
        val ST_CCA_EAN8 = 0x53
        val ST_CCA_RSS_EXPANDED = 0x54
        val ST_CCA_RSS_LIMITED = 0x55
        val ST_CCA_RSS14 = 0x56
        val ST_CCA_UPCA = 0x57
        val ST_CCA_UPCE = 0x58
        val ST_CCC_EAN128 = 0x59
        val ST_TLC39 = 0x5A
        val ST_CCB_EAN128 = 0x61
        val ST_CCB_EAN13 = 0x62
        val ST_CCB_EAN8 = 0x63
        val ST_CCB_RSS_EXPANDED = 0x64
        val ST_CCB_RSS_LIMITED = 0x65
        val ST_CCB_RSS14 = 0x66
        val ST_CCB_UPCA = 0x67
        val ST_CCB_UPCE = 0x68
        val ST_SIGNATURE_CAPTURE = 0x69
        val ST_MATRIX2OF5_OLD = 0x71
        val ST_CHINESE2OF5 = 0x72
        val ST_KOREAN2OF5 = 0x73
        val ST_UPCA_5 = 0x88
        val ST_UPCE0_5 = 0x89
        val ST_EAN8_5 = 0x8a
        val ST_EAN13_5 = 0x8b
        val ST_UPCE1_5 = 0x90
        val ST_MACRO_MICRO_PDF = 0x9A
        val ST_OCRB = 0xA0
        val ST_NEW_COUPON = 0xB4
        val ST_HAN_XIN = 0xB7
        val ST_GS1_DATAMATRIX = 0xC1
        val ST_RFID_RAW = 0xE0
        val ST_RFID_URI = 0xE1

        fun getBarcodeTypeName(barcodeType: Int): String {
            when (barcodeType) {
                ST_NOT_APP -> return "Unknown,"
                ST_CODE_39 -> return "Code 39,"
                ST_CODABAR -> return "Codabar,"
                ST_CODE_128 -> return "Code 128,"
                ST_D2OF5 -> return "Discrete 2 of 5,"
                ST_IATA -> return "IATA,"
                ST_I2OF5 -> return "Interleaved 2 of 5,"
                ST_CODE93 -> return "Code 93,,"
                ST_UPCA -> return "UPCA,"
                ST_UPCE0 -> return "UPCE 0,"
                ST_EAN8 -> return "EAN 8,"
                ST_EAN13 -> return "EAN 13,"
                ST_CODE11 -> return "Code 11,"
                ST_CODE49 -> return "Code 49,"
                ST_MSI -> return "MSI,"
                ST_EAN128 -> return "EAN 128,"
                ST_UPCE1 -> return "UPCE 1,"
                ST_PDF417 -> return "PDF 417,"
                ST_CODE16K -> return "Code 16K,"
                ST_C39FULL -> return "Code 39 Full ASCII,"
                ST_UPCD -> return "UPCD,"
                ST_TRIOPTIC -> return "Trioptic,"
                ST_BOOKLAND -> return "Bookland,"
                ST_COUPON -> return "Coupon Code,"
                ST_NW7 -> return "NW7,"
                ST_ISBT128 -> return "ISBT-128,"
                ST_MICRO_PDF -> return "Micro PDF,"
                ST_DATAMATRIX -> return "Data Matrix,"
                ST_QR_CODE -> return "QR Code,"
                ST_MICRO_PDF_CCA -> return "Micro PDF CCA,"
                ST_POSTNET_US -> return "Postnet US,"
                ST_PLANET_CODE -> return "Planet Code,"
                ST_CODE_32 -> return "Code 32,"
                ST_ISBT128_CON -> return "ISBT-128 Concat,"
                ST_JAPAN_POSTAL -> return "Japan Postal,"
                ST_AUS_POSTAL -> return "Aus Postal,"
                ST_DUTCH_POSTAL -> return "Dutch Postal,"
                ST_MAXICODE -> return "Maxicode,"
                ST_CANADIN_POSTAL -> return "Canada Postal,"
                ST_UK_POSTAL -> return "UK Postal,"
                ST_MACRO_PDF -> return "Macro PDF-417,"
                ST_MACRO_QR_CODE -> return "Macro QR Code,"
                ST_RSS14 -> return "GS1 Databar,"
                ST_RSS_LIMITET -> return "GS1 Databar Limited,"
                ST_RSS_EXPANDED -> return "GS1 Databar Expanded,"
                ST_SCANLET -> return "Scanlet Webcode,"
                ST_UPCA_2 -> return "UPCA + 2,"
                ST_UPCE0_2 -> return "UPCE0 + 2,"
                ST_EAN8_2 -> return "EAN8 + 2,"
                ST_EAN13_2 -> return "EAN13 + 2,"
                ST_UPCE1_2 -> return "UPCE1 + 2,"
                ST_CCA_EAN128 -> return "CC-A + EAN-128,"
                ST_CCA_EAN13 -> return "CC-A + EAN-13,"
                ST_CCA_EAN8 -> return "CC-A + EAN-8,"
                ST_CCA_RSS_EXPANDED -> return "CC-A + GS1 Databar Expanded,"
                ST_CCA_RSS_LIMITED -> return "CC-A + GS1 Databar Limited,"
                ST_CCA_RSS14 -> return "CC-A + GS1 Databar,"
                ST_CCA_UPCA -> return "CC-A + UPCA,"
                ST_CCA_UPCE -> return "CC-A + UPC-E,"
                ST_CCC_EAN128 -> return "CC-C + EAN-128,"
                ST_TLC39 -> return "TLC-39,"
                ST_CCB_EAN128 -> return "CC-B + EAN-128,"
                ST_CCB_EAN13 -> return "CC-B + EAN-13,"
                ST_CCB_EAN8 -> return "CC-B + EAN-8,"
                ST_CCB_RSS_EXPANDED -> return "CC-B + GS1 Databar Expanded,"
                ST_CCB_RSS_LIMITED -> return "CC-B + GS1 Databar Limited,"
                ST_CCB_RSS14 -> return "CC-B + GS1 Databar,"
                ST_CCB_UPCA -> return "CC-B + UPC-A,"
                ST_CCB_UPCE -> return "CC-B + UPC-E,"
                ST_SIGNATURE_CAPTURE -> return "Signature,"
                ST_MATRIX2OF5_OLD -> return "Matrix 2 Of 5,"
                ST_MATRIX_2_OF_5 -> return "Matrix 2 Of 5,"
                ST_CHINESE2OF5 -> return "Chinese 2 Of 5,"
                ST_UPCA_5 -> return "UPCA 5,"
                ST_UPCE0_5 -> return "UPCE0 5,"
                ST_EAN8_5 -> return "EAN8 5,"
                ST_EAN13_5 -> return "EAN13 5,"
                ST_UPCE1_5 -> return "UPCE1 5,"
                ST_MACRO_MICRO_PDF -> return "Macro Micro PDF,"
                ST_MICRO_QR_CODE -> return "Micro QR Code,"
                ST_AZTEC -> return "Aztec Code,"
                ST_AZTEC_RUNE_CODE -> return "Aztec Rune Code,"
                ST_FRENCH_LOTTERY -> return "French Lottery,"
                ST_PARAMETER_FNC3 -> return "Parameter (FNC3),"
                ST_4STATE_US -> return "4 State US,"
                ST_4STATE_US4 -> return "4 State US4,"
                ST_CUE_CAT_CODE -> return "Cue CAT Code,"
                ST_KOREAN2OF5 -> return "Korean 3 Of 5,"
                ST_OCRB -> return "OCRB,"
                ST_RFID_RAW -> return "RFID Raw,"
                ST_RFID_URI -> return "RFID URI,"
                ST_ISSN -> return "ISSN,"
                ST_HAN_XIN -> return "Han Xin,"
                ST_NEW_COUPON -> return "GS1 Databar Expanded Coupon,"
                ST_GS1_DATAMATRIX -> return "GS1 Datamatrix,"
                else -> return ""
            }
        }
    }
}