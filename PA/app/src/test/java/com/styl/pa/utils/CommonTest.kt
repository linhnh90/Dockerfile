package com.styl.pa.utils

import com.styl.pa.entities.customer.CustomerInfo
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.text.SimpleDateFormat
import java.util.*

class CommonTest {

    companion object {
        const val TEST_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCyF32XSymD8bj7D/t673BogGaf8HbJlviGu1ahk5jshBJOh+VTNVjhhvB9FOHLg3NCieDrSMoKVqu++romXRSkn2y0QI9HpN07oKHxzVNExsPB/Y+IlS6ERcTqfQBL/TSquQVAnjknj/xKtjJ02d78b+FKI+9GMyLpDR78zGd9ocT8HwEvzemCSTb5jow8lm4toqQKcYz0whaCnvZQnE+TcPkMDKHvdB9AfUyt3Id3Ix1QFQoqqdm7swibK5wY5Bkiz6rJfbnD8XEBGQOl/nOPXDUaPbYlD8nOWSnxOU9DDS+nj0DdxOCRMB2BtoIY6O7dqEkAs2a9ewkE6YcSWqv9AgMBAAECggEAfs5LFpbmEJTk6Z8PqREkMMR8ThWh/xgIDacGoDXABWhkwJXyq7SofKzgP6/Gtmn6f7488Er70TC1SV6tfPN55128wPvXCWRSQkq2BL32NFpyuIYa1V2jqXskIvKua2z3OzhSjRgcChdzxJ7Gfn1DUOs4ttmvXYS7rsUw8z930PNVwiNafhwKm5cwQluVUx22hL1FyFJJEvCEDiiEwU2rTgwpAjO402nTf9zhk+xaUp4f+gwT8Znvy+k0UqGlVI88FLHfTbbhonG0Odi2gnZI8WBC+dXX6rOUGV3ehbtakDAkvxbKNSPVPtyBZz+Fz0K8JYH7zNAmB8v7Q1gpaZtcAQKBgQDlL/l1mLTndd0EE9O6qVmBUCP5oQP+EcGTPUSzun0PyQhuRjl+J1BNOMbJnb58wUNjEndTYqmpPpCD7GpkYH16i+1zH/1DyxPogeSc908cGrY5CKYwCMv2527m5KvpIa002xSliWcTjdxCgADKNtYkx4xe/9+jLulRfPgnVn43IQKBgQDG7Tt2x3h2ANOI0nEWkxbz9i7c8LwngFUigTDmwyNZceOFpZW5Odb6ZeUdBLe/fd6/ayZZWyAtHwhyiSdYg8D7WPf2zC72xtu9tr12SgXe2klxw/L6MRe5BlQIe33RJ4Eh0dBbc1y8A3MaOqC2RBKjmFXtWGgL7cutLVa6ys0FXQKBgHwhx9NRqmnTBKo4suze3IOVjGagNzFdHKNLA/L0htgN4msXzfvt/yhUIBcDQTEA3CbE0axwO8RXBp7tZZKIUG4Xa7x3Dw2w6E9+SvxZVpe6QAgUDy7lFNPd9ctI/3N9sfp2zm/6Q8Hri+jYu5TKrqGtEvJBVDdmKSjhruZfim0BAoGAb1FeD0/8Zvx7Y3tv8VQKaib/fn/wqqI0AgUNmnTkmS8URTuuF2L4nFLc7WowcSY8uh157lMCJH3TOlTqAaxpF75ntGH6ffo2CWnzh/hQZK7+nn0qwTsrrUyPRdflRl+28Xf6AtMBt+yuQqVAPf7F92pN2223e8VsTtKqUAkgxbkCgYEAvMAzU7UXy9Wn+icYJD1GFK7WEGdHvLartTLxOpd/NhhCrF39vz85x046PPS6sBo/EactECp/GV37s6jbMNnaToiL6hhtS0cj29RS7ZgXpp9pZ/kmLBibSZF8m1nx8+hLN6l6fJUeys0L5raK6u1dvoN1tNDx8LoWzITMPjYKkoA="
        const val TEST_PUBLICK_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAshd9l0spg/G4+w/7eu9waIBmn/B2yZb4hrtWoZOY7IQSToflUzVY4YbwfRThy4NzQong60jKClarvvq6Jl0UpJ9stECPR6TdO6Ch8c1TRMbDwf2PiJUuhEXE6n0AS/00qrkFQJ45J4/8SrYydNne/G/hSiPvRjMi6Q0e/MxnfaHE/B8BL83pgkk2+Y6MPJZuLaKkCnGM9MIWgp72UJxPk3D5DAyh73QfQH1MrdyHdyMdUBUKKqnZu7MImyucGOQZIs+qyX25w/FxARkDpf5zj1w1Gj22JQ/Jzlkp8TlPQw0vp49A3cTgkTAdgbaCGOju3ahJALNmvXsJBOmHElqr/QIDAQAB"
    }

    @Test
    fun needUpdateFirmware_test() {
        assert(!GeneralUtils.needUpdateFirmware("1.0.0", "1.0.0"))
        assert(!GeneralUtils.needUpdateFirmware("1.0.0", "0.3.1"))

        assert(GeneralUtils.needUpdateFirmware("1.0.0", "1.0.1"))
        assert(GeneralUtils.needUpdateFirmware("1.0.0", "1.1"))
        assert(GeneralUtils.needUpdateFirmware("1.0.0", "2.0.0"))
        assert(GeneralUtils.needUpdateFirmware("0.0.1", "0.0.2"))
        assert(GeneralUtils.needUpdateFirmware("0.0.9", "1.0.0"))
        assert(GeneralUtils.needUpdateFirmware("1.1", "1.2"))
        assert(GeneralUtils.needUpdateFirmware("1.1", "2.0"))
        assert(GeneralUtils.needUpdateFirmware("", "3.0.0"))
        assert(GeneralUtils.needUpdateFirmware("a.1.0", "1.1.1"))
        assert(GeneralUtils.needUpdateFirmware("1.1.0", "a.1.1"))
        assert(GeneralUtils.needUpdateFirmware("1.1.0", "1.1.1"))
        assert(GeneralUtils.needUpdateFirmware("1.1.0", "1.1.1.1"))
        assert(GeneralUtils.needUpdateFirmware("1.1.0.0", "1.1.1"))
        assert(!GeneralUtils.needUpdateFirmware("1.1.0.0", ""))
    }

    @Test(expected = Test.None::class)
    fun getSHA256File_normal() {
        val filePath = javaClass.classLoader?.getResource("facility.json")
        GeneralUtils.getSHA256File(File(filePath?.file))
    }

    @Test(expected = Test.None::class)
    fun getCurrentDate() {
        GeneralUtils.getCurrentDate()
    }

    @Test
    fun getSHA256File_file_not_exist() {
        val sha256 = GeneralUtils.getSHA256File(File("test.txt"))
        assert(sha256.isEmpty())
    }

    @Test
    fun getSHA256File_file_empty() {
        val sha256 = GeneralUtils.getSHA256File(File("test.txt"))
        assert(sha256.isEmpty())
    }

    @Test
    fun bytesToHexString_normal() {
        val hex = GeneralUtils.bytesToHexString(byteArrayOf(0xAD.toByte(), 0x0A.toByte()))
        assert(hex.equals("ad 0a", true))
    }

    @Test
    fun bytesToHexString_byteArray_null() {
        val hex = GeneralUtils.bytesToHexString(null)
        assert(hex == null)
    }

    @Test
    fun convertStringToObject_normal() {
        val str = TestUtils.readJsonFile("customer.json")
        val customer = GeneralUtils.convertStringToObject<CustomerInfo>(str)
        assert(customer != null)
    }

    @Test
    fun convertStringToObject_data_null() {
        val customer = GeneralUtils.convertStringToObject<CustomerInfo>(null)
        assert(customer == null)
    }

    @Test
    fun formatToDate_normal() {
        val date = "2021-08-01T10:10:10"
        val formatDate = GeneralUtils.formatToDate(date)
        assertEquals(formatDate, "01 Aug 2021")
    }

    @Test
    fun formatToDate_date_empty() {
        val formatDate = GeneralUtils.formatToDate("")
        assert(formatDate.isEmpty())
    }

    @Test
    fun formatToDate_date_null() {
        val formatDate = GeneralUtils.formatToDate(null)
        assert(formatDate.isEmpty())
    }

    @Test
    fun formatToDate_wrong_format() {
        val date = "2021/08/01T10:10:10"
        val formatDate = GeneralUtils.formatToDate(date)
        assert(formatDate.isNotEmpty())
    }

    @Test
    fun formatDateTimeToDate_normal() {
        val date = "2021-08-01T10:10:10"
        val formatDate = GeneralUtils.formatDateTimeToDate(date)
        assertEquals(formatDate, "2021-08-01")
    }

    @Test
    fun formatDateTimeToDate_empty() {
        val date = ""
        val formatDate = GeneralUtils.formatDateTimeToDate(date)
        assertEquals(formatDate, "")
    }

    @Test
    fun formatDateTimeToDate_null() {
        val formatDate = GeneralUtils.formatDateTimeToDate(null)
        assert(formatDate.isEmpty())
    }

    @Test
    fun formatDateTimeToDate_wrong_format() {
        val date = "2021/08/01T10:10:10"
        val formatDate = GeneralUtils.formatDateTimeToDate(date)
        assert(formatDate.isNotEmpty())
    }

    @Test
    fun formatDateToNumber_normal() {
        val date = "2021-08-01T10:10:10"
        val formatDate = GeneralUtils.formatDateToNumber(date)
        assertEquals(formatDate, "01/08/2021")
    }

    @Test
    fun formatDateToNumber_null() {
        val formatDate = GeneralUtils.formatDateToNumber(null)
        assert(formatDate.isEmpty())
    }

    @Test
    fun formatDateToNumber_empty() {
        val formatDate = GeneralUtils.formatDateToNumber("")
        assert(formatDate.isEmpty())
    }

    @Test
    fun formatDateToNumber_wrong_format() {
        val date = "2021/08/01T10:10:10"
        val formatDate = GeneralUtils.formatDateToNumber(date)
        assert(formatDate.isNotEmpty())
    }


    @Test
    fun formatToDateTime_normal() {
        val date = "2021-08-01T10:10:10"
        val formatDate = GeneralUtils.formatToDateTime(date)
        assertEquals(formatDate, "01 Aug 2021 10:10 AM")
    }

    @Test
    fun formatToDateTime_null() {
        val formatDate = GeneralUtils.formatToDateTime(null)
        assert(formatDate.isEmpty())
    }

    @Test
    fun formatToDateTime_empty() {
        val formatDate = GeneralUtils.formatToDateTime("")
        assert(formatDate.isEmpty())
    }

    @Test
    fun formatToDateTime_wrong_format() {
        val date = "2021/08/01T10:10:10"
        val formatDate = GeneralUtils.formatToDateTime(date)
        assert(formatDate.isNotEmpty())
    }

    @Test
    fun formatToDayMonth_normal() {
        val date = "2021-08-01T10:10:10"
        val formatDate = GeneralUtils.formatToDayMonth(date)
        assertEquals(formatDate, "01 Aug")
    }

    @Test
    fun formatToDayMonth_null() {
        val formatDate = GeneralUtils.formatToDayMonth(null)
        assert(formatDate.isEmpty())
    }

    @Test
    fun formatToDayMonth_empty() {
        val formatDate = GeneralUtils.formatToDayMonth("")
        assert(formatDate.isEmpty())
    }

    @Test
    fun formatToDayMonth_wrong_format() {
        val date = "2021/08/01T10:10:10"
        val formatDate = GeneralUtils.formatToDayMonth(date)
        assert(formatDate.isNotEmpty())
    }

    @Test
    fun formatToTime_normal() {
        val date = "2021-08-01T10:10:10"
        val formatDate = GeneralUtils.formatToTime(date)
        assertEquals(formatDate, "10:10 AM")
    }

    @Test
    fun formatToTime_null() {
        val formatDate = GeneralUtils.formatToTime(null)
        assert(formatDate.isEmpty())
    }

    @Test
    fun formatToTime_empty() {
        val formatDate = GeneralUtils.formatToTime("")
        assert(formatDate.isEmpty())
    }

    @Test
    fun formatToTime_wrong_format() {
        val date = "2021/08/01T10:10:10"
        val formatDate = GeneralUtils.formatToTime(date)
        assert(formatDate.isNotEmpty())
    }

    @Test
    fun formatToWeeksday_normal() {
        val date = "2021-08-01T10:10:10"
        val formatDate = GeneralUtils.formatToWeeksday(date)
        assertEquals(formatDate, "Sunday")
    }

    @Test
    fun formatToWeeksday_null() {
        val formatDate = GeneralUtils.formatToWeeksday(null)
        assert(formatDate.isEmpty())
    }

    @Test
    fun formatToWeeksday_empty() {
        val formatDate = GeneralUtils.formatToWeeksday("")
        assert(formatDate.isEmpty())
    }

    @Test
    fun formatToWeeksday_wrong_format() {
        val date = "2021/08/01T10:10:10"
        val formatDate = GeneralUtils.formatToWeeksday(date)
        assert(formatDate.isNotEmpty())
    }

    @Test
    fun formatShortDay_normal() {
        val date = "2021-08-01T10:10:10"
        val formatDate = GeneralUtils.formatShortDay(date)
        assertEquals(formatDate, "Sun")
    }

    @Test
    fun formatShortDay_null() {
        val formatDate = GeneralUtils.formatShortDay(null)
        assert(formatDate.isEmpty())
    }

    @Test
    fun formatShortDay_emtpy() {
        val formatDate = GeneralUtils.formatShortDay("")
        assert(formatDate.isEmpty())
    }

    @Test
    fun formatShortDay_wrong_format() {
        val date = "2021/08/01T10:10:10"
        val formatDate = GeneralUtils.formatShortDay(date)
        assert(formatDate.isNotEmpty())
    }

    @Test
    fun formatToDefaultTime_normal() {
        val date = "01/08/2021"
        val formatDate = GeneralUtils.formatToDefaultTime(date)
        assert(formatDate.contains("2021-08-01"))
    }

    @Test
    fun formatToDefaultTime_null() {
        val formatDate = GeneralUtils.formatToDefaultTime(null)
        assert(formatDate.isEmpty())
    }

    @Test
    fun formatToDefaultTime_empty() {
        val formatDate = GeneralUtils.formatToDefaultTime("")
        assert(formatDate.isEmpty())
    }

    @Test
    fun formatToDefaultTime_wrong_format() {
        val date = "123"
        val formatDate = GeneralUtils.formatToDefaultTime(date)
        assert(formatDate.isNotEmpty())
    }

    @Test
    fun formatToTime_sdf_normal() {
        val date = "2021-08-01T10:10:10"
        val inSfd = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val outSdf = SimpleDateFormat("dd/MM/yyyy")
        val formatDate = GeneralUtils.formatToTime(date, inSfd, outSdf)
        assert(formatDate == "01/08/2021")
    }

    @Test
    fun formatToTime_sdf_null() {
        val inSfd = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val outSdf = SimpleDateFormat("dd/MM/yyyy")
        val formatDate = GeneralUtils.formatToTime(null, inSfd, outSdf)
        assert(formatDate.isEmpty())
    }

    @Test
    fun formatToTime_sdf_emtpy() {
        val inSfd = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val outSdf = SimpleDateFormat("dd/MM/yyyy")
        val formatDate = GeneralUtils.formatToTime("", inSfd, outSdf)
        assert(formatDate.isEmpty())
    }

    @Test
    fun formatToTime_sdf_wrong_format() {
        val date = "2021/08/01T10:10:10"
        val inSfd = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val outSdf = SimpleDateFormat("dd/MM/yyyy")
        val formatDate = GeneralUtils.formatToTime(date, inSfd, outSdf)
        assert(formatDate.isNotEmpty())
    }

    @Test
    fun decryptRsa() {
        val data = "Q6yQt6mBnVUOz4IX9U4sdSN83By49emfflBcrGITEF5cbg1VVMgNh9GZM7y0Hvp9WsTyKfOkeP7/MxLhviZzUYdFPD1HdegcwGOG9bHFvaCJu23vJwJyDeXwjbRj8Qf+G/hea3cCbgUguPoDiVv8jsFeDambxka4Fiy2pFOowU0vpix5XLpwfZ/WT1tPTPk4BTwzsGWl6RGLreAhMaLW7dO7cYLzGiBnnk11IE9eeVh11X3trbMVdKcH7yv6amKiVwkRPAiT7Ltrz9j9GTgMuSVYaSuUf5GysRU4YW8n7sFPJmL5nbobdmv03WBa69F5yvX54llqqfNYRqd3GZ5vGQ=="
        val dataBytes = Base64.getDecoder().decode(data)
        val keyEncoded = Base64.getDecoder().decode(TEST_PRIVATE_KEY)
        val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
        val keySpec = PKCS8EncodedKeySpec(keyEncoded)
        val privateKey = keyFactory.generatePrivate(keySpec)
        val dateDecrypted = GeneralUtils.decryptRsa(dataBytes, privateKey)
        assertEquals(String(dateDecrypted), "abc")
    }

    @Test
    fun formatAmountSymbol() {
        val formattedStr = GeneralUtils.formatAmountSymbols("$", 1.2f, 2)
        assert(formattedStr.equals("$1.20"))
    }

    @Test
    fun getColorWithAlpha() {
        val x= GeneralUtils.getColorWithAlpha(250, 0f)
        assert(x == 0)
    }

    @Test
    fun convertDateToAge_normal() {
        val date = "1994-11-11T20:12:11"
        val age = GeneralUtils.convertDateToAge(date)
        assertTrue(age != null);
    }

    @Test
    fun convertDateToAge_dateNull() {
        val date = null
        val age = GeneralUtils.convertDateToAge(date)
        assertNull(age)
    }

    @Test
    fun convertDateToAge_dateEmpty() {
        val date = null
        val age = GeneralUtils.convertDateToAge(date)
        assertNull(age)
    }

    @Test
    fun convertDateToAge_wrongFormat() {
        val date = "11/11/1994"
        val age = GeneralUtils.convertDateToAge(date)
        assertNull(age)
    }

    @Test
    fun eventFormatDate_normal() {
        val sdfEventDateInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        val sdfEventDateOutput = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val fromDate = "2021-10-01T10:10:10"
        val toDate = "2021-11-01T10:10:10"
        val date = GeneralUtils.eventFormatDate(fromDate, toDate, sdfEventDateInput, sdfEventDateOutput)
        assertEquals("01 Oct 2021 ~ 01 Nov 2021", date)
    }

    @Test
    fun eventFormatDate_fromDate_empty() {
        val sdfEventDateInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        val sdfEventDateOutput = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val fromDate = ""
        val toDate = "2021-11-01T10:10:10"
        val date = GeneralUtils.eventFormatDate(fromDate, toDate, sdfEventDateInput, sdfEventDateOutput)
        assertEquals(" ~ 01 Nov 2021", date)
    }

    @Test
    fun eventFormatDate_fromDate_null() {
        val sdfEventDateInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        val sdfEventDateOutput = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val fromDate = null
        val toDate = "2021-11-01T10:10:10"
        val date = GeneralUtils.eventFormatDate(fromDate, toDate, sdfEventDateInput, sdfEventDateOutput)
        assertEquals(" ~ 01 Nov 2021", date)
    }

    @Test
    fun eventFormatDate_toDate_empty() {
        val sdfEventDateInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        val sdfEventDateOutput = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val fromDate = "2021-10-01T10:10:10"
        val toDate = ""
        val date = GeneralUtils.eventFormatDate(fromDate, toDate, sdfEventDateInput, sdfEventDateOutput)
        assertEquals("01 Oct 2021 ~ ", date)
    }

    @Test
    fun eventFormatDate_toDate_null() {
        val sdfEventDateInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        val sdfEventDateOutput = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val fromDate = "2021-10-01T10:10:10"
        val toDate = null
        val date = GeneralUtils.eventFormatDate(fromDate, toDate, sdfEventDateInput, sdfEventDateOutput)
        assertEquals("01 Oct 2021 ~ ", date)
    }

    @Test
    fun eventFormatDate_toDate_empty_fromDate_empty() {
        val sdfEventDateInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        val sdfEventDateOutput = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val fromDate = ""
        val toDate = ""
        val date = GeneralUtils.eventFormatDate(fromDate, toDate, sdfEventDateInput, sdfEventDateOutput)
        assert(date.isEmpty())
    }

    @Test
    fun isValidEmailAddress_emailNull() {
        val result = GeneralUtils.isValidEmailAddress(null)
        assert(!result)
    }

    @Test
    fun isValidEmailAddress_emailEmpty() {
        val result = GeneralUtils.isValidEmailAddress("")
        assert(!result)
    }

    @Test
    fun isValidEmailAddress_validEmail() {
        var email = "test.sl@styl.com"
        var result = GeneralUtils.isValidEmailAddress(email)
        assertTrue(result)

        email = "test123.f@pa.gov.sg"
        result = GeneralUtils.isValidEmailAddress(email)
        assertTrue(result)
    }

    @Test
    fun isValidEmailAddress_invalidEmail() {
        var email = "test"
        var result = GeneralUtils.isValidEmailAddress(email)
        assertFalse(result)

        email = "test123.f!@"
        result = GeneralUtils.isValidEmailAddress(email)
        assertFalse(result)
    }


}
