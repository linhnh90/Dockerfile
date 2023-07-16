package com.styl.pa.utils

import com.google.gson.Gson
import com.styl.pa.entities.CertData
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.services.api_encryption.ApiCryptoUtils
import io.mockk.every
import io.mockk.mockkObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.io.ByteArrayInputStream
import java.util.Base64
import org.mockito.MockedStatic


class ApiCryptoUtilsTest {

    private val encryptKey  = TestUtils.readResource(filename = "sample_key.txt")
    private val signKey     = TestUtils.readResource(filename = "sample_sign_key.txt")

    private var mockedSettings: MockedStatic<ApiCryptoUtils>? = null

    @Before
    fun setup() {
        mockedSettings = Mockito.mockStatic(ApiCryptoUtils::class.java)
    }

    private fun generateCert(
        isNoSignKey: Boolean = false,
        isNoEncryptKey: Boolean = false
    ): HashMap<String, HashMap<String, Any>> {
        val cert = HashMap<String, HashMap<String, Any>>()
        if (!isNoSignKey) {
            val signCert = TestUtils.readResource("sign_cert")
            val signCertStream = ByteArrayInputStream(Base64.getDecoder().decode(signCert))
            val signKeyPair = ApiCryptoUtils.getKeyPair(signCertStream, signKey)
            cert["SignCert"] = signKeyPair!!
        }
        if (!isNoEncryptKey) {
            val encryptCert = TestUtils.readResource("encrypt_cert")
            val encryptCertStream = ByteArrayInputStream(Base64.getDecoder().decode(encryptCert))
            val encryptKeyPair = ApiCryptoUtils.getKeyPair(encryptCertStream, encryptKey)
            cert["EncryptCert"] = encryptKeyPair!!
        }
        return cert
    }

    @Test
    fun testGetCertKeys() {
        val certFileContent = TestUtils.readResource("sign_cert")
        val keyPair = ApiCryptoUtils.getKeyPairFromCertData(CertData(signKey, certFileContent))
        assert(keyPair != null)
        assert(keyPair!!["PrivateKey"] != null)
        assert(keyPair["PublicKey"] != null)
    }

    @Test
    fun testEncryptRequest_normal() {
        val payload = TestUtils.readJsonFile("customer.json")
        mockkObject(ApiCryptoUtils)
        every { ApiCryptoUtils.getEncryptCert() } returns generateCert()
        val encryptResult = ApiCryptoUtils.encryptRequest(payload)
        assert(encryptResult.isNotEmpty())
        assert(encryptResult !== payload)
    }

    @Test
    fun testEncryptRequest_signKeyNull() {
        val payload = TestUtils.readJsonFile("customer.json")
        mockkObject(ApiCryptoUtils)
        every { ApiCryptoUtils.getEncryptCert() } returns generateCert(true, false)
        val encryptResult = ApiCryptoUtils.encryptRequest(payload)
        assert(encryptResult == payload)
    }

    @Test
    fun testEncryptRequest_encryptKeyNull() {
        val payload = TestUtils.readJsonFile("customer.json")
        mockkObject(ApiCryptoUtils)
        every { ApiCryptoUtils.getEncryptCert() } returns generateCert(false, true)
        val encryptResult = ApiCryptoUtils.encryptRequest(payload)
        assert(encryptResult == payload)
    }

    @Test
    fun testDecryptResponse_normal() {
        val encryptedData = "eyJ0eXAiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiUlNBLU9BRVAtMjU2In0.LDPHzntUsBJwxBCoLxfzEP5phziKeRawmpIr55oC9GtRqBQHSO43TEwEF13vz93guBO5gPV5V5kh8rW46pHtSSQLMyhtIXzMG3Soi41qxpbNGyEEvIZyBPFXMaC-kHuJARPqvMzsRF8UKJAjV49WLEH8toGJy-1HpeCjIteNHJfBQcPfSE5c9mHeLM8LurLqVwlIDirYc9kY6JvkaJ8AKZgAqMiShFgCrmHR_3cNiUHR6LukYeyXWiXNubvTkmlQQcx5d3On9xZNDHHyLysJv-z9QtP5BdMK32Z3zDP7Y43lijZ0L75yR-Bnm6tX_Xlts0Upy37HWizX0tDQj2L9DQ.8_3obUDo5t5jSwpPmuIsEQ.0HzoBvuYbWfqYHGskv0RWVCyCycZ_zkLObjO6GhK5A0O4RofVDrRbMKtFSWLdxufZjFJIm_4EGFBC8h4r2W30jPWgZCrfIDAT9Lh4waiAy3ONjLKzKYeW_piHTNyDq4BnK7fL2Zw4mkAU1U9VZkqknmJRgnIQiZ1ceqZkqdw5f2KmV2AHiWBM8kK-03WY3LqXxxaS6wzpYtyFaFdTJEjDKTidL-0DCut_lQfy06USdO7TNVttUZt0fv9LrLr4L-AjEzApiowB5M1yKChzChqsNCBG-opPjBsY3A3AU0lCDVib2qc15BOnrmUliLxZYDYu9BSJ-_n4o_wSn0loab81q-a1e4uYT7eax3-8FdNX5YM_p678fBCqeKzurUL8hAdypL8FvT6PdCMZAUEthereLlGM55ib33pQ19RwgSZDMPFpYKxtzW9kxerx6HJ_waevUVEeA0oDa7zugzmDZHtxsLCKV6epgqDgdZbXyca9FJRhNJjBGU5ptIr4kA3tUAy3yIn0gp0yA2nKRRI4sAMr1DFrjs0mR9A8ttYKxDFDVaYDTJeEuR4HdP0rjK9LUXIGT_U3mbKl63nloACvkG3vw74SnjZKk70wNnSYfZunkMK1tuH5NTuq7rH_ltWSyINZsWftV1VQamgcBmROFd0qcuL67vQNRQbNE6AVeRwK4mrOFjdGH3URcIcdO4x0GvIeOrJFsw9gEyu0AY8kqePww-YtJOzmcK2GtFpvDLVWKirnU8kvC6RO1W8BKCF-yl3-ercfGjhskIDebmCk-q5tVTMxAM_QyfOF5_DShIslFC2REiEPXJRVq-2S7n5P0b2cFyh6I87et-u-KL8-AE3mQsSqC5ZfRUHfuiLNkNWMyObnHAjj4lmgwKB8zCfdYRGoGtKcFc8tAh5wjsG1kiMdfGGDE5aepRpI8v6cb7nHTbcz_HdRFj-zc4CCxV-G-pNoBMZTBHoTuHzwyZY5-T_XpyhK3SEzzfUeqlffbuEh03iXBfRd1eAnsbpt_5c9zMOwnLtVSVWkEd86ORQLo2HHMFYeZc0RtTDLyAoPM0CeoSh4iSgggnyN-Rgn78ByCxNpML-Zlvjg8S2ux-tOOrs2fyR9uT5M9KdMA8LrvjSbyly-L4uEPhkgDKO9wzhxkQWJTyHLtQh75Mh_M5F57nyQFX3ej1Pp0hK_wu293ZwxQwrD3tWgWwKB_Z1VIzCVMKJQJo0nrYKhy4Zdjtszb6aL_J8T6NMltZDxtGTUMWLgC9RZAJFEqBP4HqHb4ThOoSau2jU7lXtGczwAVlogIfeFDqLgqZ06-r7hckIiLUvFM5fYgkNomZK4nugKK2bP-t9SIPIFVzlwLSXaiD8Uq2Pba6hX__B1Xd9qXwb1snIIF4oFG7vkCwl9nUY4zPn-WiUjaC7vyeRIGCk_LDAdqxXPmcx-XKy4DNgMexf3dslCz4fvPh6PDlQxFs8fymkFXe_X8sht6_kVoSRldeSm-IbnouXVCFaS061Z6u5BTp2JeqghCC45SQKyMtFi8OnAf88PeqY_klzVpNJhaoH_upqt6qgwEXCuPJC60TYTgz0BNJInbuGT6X222xlO_Oy0OrSO0mX2CfMNnz0eHvy8RbAj_4QE3JgU4ALGG5_ovgFw00oXQEUtbLMyxvov7mkb4xEUIakrtpp1JRUvKhTpYiqmZFw59RPdY2MYu7lz_HuBmyXN9TJikcVJjQi3C2g4T5ImDxHrw-mKmJSElf_bFoHe4PdIt92fH3GjWIwfMRhtKEQLZqVk8_2TC7m9XSoNhscegNd4Y-g68ZVW_Z4Oi5Y702S8bspRrZRT7vC61Fd2wtx6j3S0IOENI016MFhyr53E5ctyDK6znxIl7l8QL4Wy8owH7GcwHcRUQNIzFiLYAFuZQPrsUD9tP-QIyh5cLqpM7I0qOFf0HbwoW2CWeKIK6lD_p4Lw2rCIHk4tfnbPuYt-sfgchbJvvBZ8maS7Qqfocvhuh44rGTHqJ37tMtR7JPIYfDVQBWYoexID-8pVSbxgJ31joR-xwzCj8gKiocbgL41EROjALXZtmKgAnlShD_XqeNDtN1Mp5gUitKxQ3vVZEsbsL1Bs4iiriOL34JtBQNpKyQ-BS8vaf7QVL79oPI0MQqljWbU8XjfY4oZ73M1l2H_YMYhE7yXSoMKrY-R3ZKIQgts1aQ6xcFsmHrVyw.1Sb7S_BMlJLzDPzYsLSOYw"
        mockkObject(ApiCryptoUtils)
        every { ApiCryptoUtils.getEncryptCert() } returns generateCert()
        val decryptedResult = ApiCryptoUtils.decryptResponse(encryptedData)
        assert(decryptedResult.isNotEmpty())
        val customerInfo = Gson().fromJson(decryptedResult, CustomerInfo::class.java)
        assert(customerInfo != null)
        assert(customerInfo.mFullName == "PHAM DUC DOAN TRANG")
    }

    @Test
    fun testDecryptResponse_signKey_null() {
        val encryptedData = "eyJ0eXAiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiUlNBLU9BRVAtMjU2In0.LDPHzntUsBJwxBCoLxfzEP5phziKeRawmpIr55oC9GtRqBQHSO43TEwEF13vz93guBO5gPV5V5kh8rW46pHtSSQLMyhtIXzMG3Soi41qxpbNGyEEvIZyBPFXMaC-kHuJARPqvMzsRF8UKJAjV49WLEH8toGJy-1HpeCjIteNHJfBQcPfSE5c9mHeLM8LurLqVwlIDirYc9kY6JvkaJ8AKZgAqMiShFgCrmHR_3cNiUHR6LukYeyXWiXNubvTkmlQQcx5d3On9xZNDHHyLysJv-z9QtP5BdMK32Z3zDP7Y43lijZ0L75yR-Bnm6tX_Xlts0Upy37HWizX0tDQj2L9DQ.8_3obUDo5t5jSwpPmuIsEQ.0HzoBvuYbWfqYHGskv0RWVCyCycZ_zkLObjO6GhK5A0O4RofVDrRbMKtFSWLdxufZjFJIm_4EGFBC8h4r2W30jPWgZCrfIDAT9Lh4waiAy3ONjLKzKYeW_piHTNyDq4BnK7fL2Zw4mkAU1U9VZkqknmJRgnIQiZ1ceqZkqdw5f2KmV2AHiWBM8kK-03WY3LqXxxaS6wzpYtyFaFdTJEjDKTidL-0DCut_lQfy06USdO7TNVttUZt0fv9LrLr4L-AjEzApiowB5M1yKChzChqsNCBG-opPjBsY3A3AU0lCDVib2qc15BOnrmUliLxZYDYu9BSJ-_n4o_wSn0loab81q-a1e4uYT7eax3-8FdNX5YM_p678fBCqeKzurUL8hAdypL8FvT6PdCMZAUEthereLlGM55ib33pQ19RwgSZDMPFpYKxtzW9kxerx6HJ_waevUVEeA0oDa7zugzmDZHtxsLCKV6epgqDgdZbXyca9FJRhNJjBGU5ptIr4kA3tUAy3yIn0gp0yA2nKRRI4sAMr1DFrjs0mR9A8ttYKxDFDVaYDTJeEuR4HdP0rjK9LUXIGT_U3mbKl63nloACvkG3vw74SnjZKk70wNnSYfZunkMK1tuH5NTuq7rH_ltWSyINZsWftV1VQamgcBmROFd0qcuL67vQNRQbNE6AVeRwK4mrOFjdGH3URcIcdO4x0GvIeOrJFsw9gEyu0AY8kqePww-YtJOzmcK2GtFpvDLVWKirnU8kvC6RO1W8BKCF-yl3-ercfGjhskIDebmCk-q5tVTMxAM_QyfOF5_DShIslFC2REiEPXJRVq-2S7n5P0b2cFyh6I87et-u-KL8-AE3mQsSqC5ZfRUHfuiLNkNWMyObnHAjj4lmgwKB8zCfdYRGoGtKcFc8tAh5wjsG1kiMdfGGDE5aepRpI8v6cb7nHTbcz_HdRFj-zc4CCxV-G-pNoBMZTBHoTuHzwyZY5-T_XpyhK3SEzzfUeqlffbuEh03iXBfRd1eAnsbpt_5c9zMOwnLtVSVWkEd86ORQLo2HHMFYeZc0RtTDLyAoPM0CeoSh4iSgggnyN-Rgn78ByCxNpML-Zlvjg8S2ux-tOOrs2fyR9uT5M9KdMA8LrvjSbyly-L4uEPhkgDKO9wzhxkQWJTyHLtQh75Mh_M5F57nyQFX3ej1Pp0hK_wu293ZwxQwrD3tWgWwKB_Z1VIzCVMKJQJo0nrYKhy4Zdjtszb6aL_J8T6NMltZDxtGTUMWLgC9RZAJFEqBP4HqHb4ThOoSau2jU7lXtGczwAVlogIfeFDqLgqZ06-r7hckIiLUvFM5fYgkNomZK4nugKK2bP-t9SIPIFVzlwLSXaiD8Uq2Pba6hX__B1Xd9qXwb1snIIF4oFG7vkCwl9nUY4zPn-WiUjaC7vyeRIGCk_LDAdqxXPmcx-XKy4DNgMexf3dslCz4fvPh6PDlQxFs8fymkFXe_X8sht6_kVoSRldeSm-IbnouXVCFaS061Z6u5BTp2JeqghCC45SQKyMtFi8OnAf88PeqY_klzVpNJhaoH_upqt6qgwEXCuPJC60TYTgz0BNJInbuGT6X222xlO_Oy0OrSO0mX2CfMNnz0eHvy8RbAj_4QE3JgU4ALGG5_ovgFw00oXQEUtbLMyxvov7mkb4xEUIakrtpp1JRUvKhTpYiqmZFw59RPdY2MYu7lz_HuBmyXN9TJikcVJjQi3C2g4T5ImDxHrw-mKmJSElf_bFoHe4PdIt92fH3GjWIwfMRhtKEQLZqVk8_2TC7m9XSoNhscegNd4Y-g68ZVW_Z4Oi5Y702S8bspRrZRT7vC61Fd2wtx6j3S0IOENI016MFhyr53E5ctyDK6znxIl7l8QL4Wy8owH7GcwHcRUQNIzFiLYAFuZQPrsUD9tP-QIyh5cLqpM7I0qOFf0HbwoW2CWeKIK6lD_p4Lw2rCIHk4tfnbPuYt-sfgchbJvvBZ8maS7Qqfocvhuh44rGTHqJ37tMtR7JPIYfDVQBWYoexID-8pVSbxgJ31joR-xwzCj8gKiocbgL41EROjALXZtmKgAnlShD_XqeNDtN1Mp5gUitKxQ3vVZEsbsL1Bs4iiriOL34JtBQNpKyQ-BS8vaf7QVL79oPI0MQqljWbU8XjfY4oZ73M1l2H_YMYhE7yXSoMKrY-R3ZKIQgts1aQ6xcFsmHrVyw.1Sb7S_BMlJLzDPzYsLSOYw"
        mockkObject(ApiCryptoUtils)
        every { ApiCryptoUtils.getEncryptCert() } returns generateCert(true, false)
        val decryptedResult = ApiCryptoUtils.decryptResponse(encryptedData)
        assert(decryptedResult == encryptedData)
    }

    @Test
    fun testDecryptResponse_encryptKey_null() {
        val encryptedData = "eyJ0eXAiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiUlNBLU9BRVAtMjU2In0.LDPHzntUsBJwxBCoLxfzEP5phziKeRawmpIr55oC9GtRqBQHSO43TEwEF13vz93guBO5gPV5V5kh8rW46pHtSSQLMyhtIXzMG3Soi41qxpbNGyEEvIZyBPFXMaC-kHuJARPqvMzsRF8UKJAjV49WLEH8toGJy-1HpeCjIteNHJfBQcPfSE5c9mHeLM8LurLqVwlIDirYc9kY6JvkaJ8AKZgAqMiShFgCrmHR_3cNiUHR6LukYeyXWiXNubvTkmlQQcx5d3On9xZNDHHyLysJv-z9QtP5BdMK32Z3zDP7Y43lijZ0L75yR-Bnm6tX_Xlts0Upy37HWizX0tDQj2L9DQ.8_3obUDo5t5jSwpPmuIsEQ.0HzoBvuYbWfqYHGskv0RWVCyCycZ_zkLObjO6GhK5A0O4RofVDrRbMKtFSWLdxufZjFJIm_4EGFBC8h4r2W30jPWgZCrfIDAT9Lh4waiAy3ONjLKzKYeW_piHTNyDq4BnK7fL2Zw4mkAU1U9VZkqknmJRgnIQiZ1ceqZkqdw5f2KmV2AHiWBM8kK-03WY3LqXxxaS6wzpYtyFaFdTJEjDKTidL-0DCut_lQfy06USdO7TNVttUZt0fv9LrLr4L-AjEzApiowB5M1yKChzChqsNCBG-opPjBsY3A3AU0lCDVib2qc15BOnrmUliLxZYDYu9BSJ-_n4o_wSn0loab81q-a1e4uYT7eax3-8FdNX5YM_p678fBCqeKzurUL8hAdypL8FvT6PdCMZAUEthereLlGM55ib33pQ19RwgSZDMPFpYKxtzW9kxerx6HJ_waevUVEeA0oDa7zugzmDZHtxsLCKV6epgqDgdZbXyca9FJRhNJjBGU5ptIr4kA3tUAy3yIn0gp0yA2nKRRI4sAMr1DFrjs0mR9A8ttYKxDFDVaYDTJeEuR4HdP0rjK9LUXIGT_U3mbKl63nloACvkG3vw74SnjZKk70wNnSYfZunkMK1tuH5NTuq7rH_ltWSyINZsWftV1VQamgcBmROFd0qcuL67vQNRQbNE6AVeRwK4mrOFjdGH3URcIcdO4x0GvIeOrJFsw9gEyu0AY8kqePww-YtJOzmcK2GtFpvDLVWKirnU8kvC6RO1W8BKCF-yl3-ercfGjhskIDebmCk-q5tVTMxAM_QyfOF5_DShIslFC2REiEPXJRVq-2S7n5P0b2cFyh6I87et-u-KL8-AE3mQsSqC5ZfRUHfuiLNkNWMyObnHAjj4lmgwKB8zCfdYRGoGtKcFc8tAh5wjsG1kiMdfGGDE5aepRpI8v6cb7nHTbcz_HdRFj-zc4CCxV-G-pNoBMZTBHoTuHzwyZY5-T_XpyhK3SEzzfUeqlffbuEh03iXBfRd1eAnsbpt_5c9zMOwnLtVSVWkEd86ORQLo2HHMFYeZc0RtTDLyAoPM0CeoSh4iSgggnyN-Rgn78ByCxNpML-Zlvjg8S2ux-tOOrs2fyR9uT5M9KdMA8LrvjSbyly-L4uEPhkgDKO9wzhxkQWJTyHLtQh75Mh_M5F57nyQFX3ej1Pp0hK_wu293ZwxQwrD3tWgWwKB_Z1VIzCVMKJQJo0nrYKhy4Zdjtszb6aL_J8T6NMltZDxtGTUMWLgC9RZAJFEqBP4HqHb4ThOoSau2jU7lXtGczwAVlogIfeFDqLgqZ06-r7hckIiLUvFM5fYgkNomZK4nugKK2bP-t9SIPIFVzlwLSXaiD8Uq2Pba6hX__B1Xd9qXwb1snIIF4oFG7vkCwl9nUY4zPn-WiUjaC7vyeRIGCk_LDAdqxXPmcx-XKy4DNgMexf3dslCz4fvPh6PDlQxFs8fymkFXe_X8sht6_kVoSRldeSm-IbnouXVCFaS061Z6u5BTp2JeqghCC45SQKyMtFi8OnAf88PeqY_klzVpNJhaoH_upqt6qgwEXCuPJC60TYTgz0BNJInbuGT6X222xlO_Oy0OrSO0mX2CfMNnz0eHvy8RbAj_4QE3JgU4ALGG5_ovgFw00oXQEUtbLMyxvov7mkb4xEUIakrtpp1JRUvKhTpYiqmZFw59RPdY2MYu7lz_HuBmyXN9TJikcVJjQi3C2g4T5ImDxHrw-mKmJSElf_bFoHe4PdIt92fH3GjWIwfMRhtKEQLZqVk8_2TC7m9XSoNhscegNd4Y-g68ZVW_Z4Oi5Y702S8bspRrZRT7vC61Fd2wtx6j3S0IOENI016MFhyr53E5ctyDK6znxIl7l8QL4Wy8owH7GcwHcRUQNIzFiLYAFuZQPrsUD9tP-QIyh5cLqpM7I0qOFf0HbwoW2CWeKIK6lD_p4Lw2rCIHk4tfnbPuYt-sfgchbJvvBZ8maS7Qqfocvhuh44rGTHqJ37tMtR7JPIYfDVQBWYoexID-8pVSbxgJ31joR-xwzCj8gKiocbgL41EROjALXZtmKgAnlShD_XqeNDtN1Mp5gUitKxQ3vVZEsbsL1Bs4iiriOL34JtBQNpKyQ-BS8vaf7QVL79oPI0MQqljWbU8XjfY4oZ73M1l2H_YMYhE7yXSoMKrY-R3ZKIQgts1aQ6xcFsmHrVyw.1Sb7S_BMlJLzDPzYsLSOYw"
        mockkObject(ApiCryptoUtils)
        every { ApiCryptoUtils.getEncryptCert() } returns generateCert(false, true)
        val decryptedResult = ApiCryptoUtils.decryptResponse(encryptedData)
        assert(decryptedResult == encryptedData)
    }

    @Test
    fun testDecryptResponse_emptyPayload() {
        val encryptedData = ""
        mockkObject(ApiCryptoUtils)
        every { ApiCryptoUtils.getEncryptCert() } returns generateCert()
        val decryptedResult = ApiCryptoUtils.decryptResponse(encryptedData)
        assert(decryptedResult == encryptedData)
    }

    @After
    fun close() {
        mockedSettings?.close()
    }
}