package com.styl.pa.services.api_encryption

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.RSADecrypter
import com.nimbusds.jose.crypto.RSAEncrypter
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.EncryptedJWT
import com.nimbusds.jwt.JWTClaimsSet
import com.styl.pa.MyApplication
import com.styl.pa.R
import com.styl.pa.entities.CertData
import com.styl.pa.entities.common.CommonResponse
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.LogManager
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.security.*
import java.security.cert.X509Certificate
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*
import kotlin.collections.HashMap
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.jose.JWEAlgorithm

import com.nimbusds.jose.JWEHeader
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.services.ApiEncryptionConfig

object ApiCryptoUtils {

    private const val KEY_PRIVATE_KEY   = "PrivateKey"
    private const val KEY_PUBLIC_KEY    = "PublicKey"
    private const val KEY_SIGN_CERT     = "SignCert"
    private const val KEY_ENCRYPT_CERT  = "EncryptCert"

    private const val KEY_REQUEST       = "request"

    @ExcludeFromJacocoGeneratedReport
    private fun getCertFromResource(resourceId: Int): CertData? {
        val certAsInputStream =
            MyApplication.getAppInstance()?.resources?.openRawResource(resourceId)
        certAsInputStream?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                val content = reader.readText()
                return GeneralUtils.convertStringToObject<CertData>(content)
            }
        }
        return null
    }

    @VisibleForTesting
    fun getKeyPair(cerFileStream: InputStream?, key: String): HashMap<String, Any>? {
        try {
            val keyPair = HashMap<String, Any>()
            val keyStore: KeyStore = KeyStore.getInstance("PKCS12")
            keyStore.load(cerFileStream, key.toCharArray())
            val keyStoreAliasEnum: Enumeration<String> = keyStore.aliases()
            var alias: String? = null
            while (keyStoreAliasEnum.hasMoreElements()) {
                alias = keyStoreAliasEnum.nextElement()
                val privateKey: PrivateKey =
                    keyStore.getKey(alias, key.toCharArray()) as PrivateKey? ?: continue
                val x509Certificate: X509Certificate =
                    keyStore.getCertificate(alias) as X509Certificate
                val publicKey: PublicKey = x509Certificate.publicKey
                keyPair[KEY_PUBLIC_KEY] = publicKey
                keyPair[KEY_PRIVATE_KEY] = privateKey
                break
            }
            return keyPair
        } catch (e: Exception) {
            LogManager.i("Unable to load cert/key for encryption/decryption")
        }
        return null
    }

    @VisibleForTesting
    @RequiresApi(Build.VERSION_CODES.O)
    fun getKeyPairFromCertData(certData: CertData): HashMap<String, Any>? {
        val certAsByteArray = Base64.getDecoder().decode(certData.cert)
        val certAsInputStream = ByteArrayInputStream(certAsByteArray)
        return getKeyPair(certAsInputStream, certData.key!!)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEncryptCert(): HashMap<String, HashMap<String, Any>>? {
        val encryptCertData = getCertFromResource(R.raw.encrypt_cert)
        val signCertData = getCertFromResource(R.raw.sign_cert)
        if (encryptCertData == null || signCertData == null
            || encryptCertData.key == null || signCertData.key == null
        ) {
            return null
        }
        val encryptKeys = getKeyPairFromCertData(encryptCertData)
        val signKeys = getKeyPairFromCertData(signCertData)
        if (encryptKeys == null || signKeys == null) {
            return null
        }
        val result = HashMap<String, HashMap<String, Any>>()
        result[KEY_SIGN_CERT] = signKeys
        result[KEY_ENCRYPT_CERT] = encryptKeys
        return result
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun encryptRequest(payload: String): String {
        val cert = getEncryptCert() ?: return payload
        val signKeys = cert[KEY_SIGN_CERT]
        val encryptKeys = cert[KEY_ENCRYPT_CERT]
        if (signKeys == null || encryptKeys == null) {
            return payload
        }
        return encrypt(
            signKeys,
            encryptKeys,
            payload
        )
    }

    private fun encrypt(
        signKey: HashMap<String, Any>,
        encryptKey: HashMap<String, Any>,
        payload: String
    ): String {
        val jwtClaimsSet = JWTClaimsSet.Builder()
        jwtClaimsSet.issuer(ApiEncryptionConfig.API_ENCRYPTION_ISS)
        jwtClaimsSet.audience(ApiEncryptionConfig.API_ENCRYPTION_AUD)
        jwtClaimsSet.expirationTime(Date(Date().time + 30 * 60 * 1000))
        jwtClaimsSet.notBeforeTime(Date())
        jwtClaimsSet.claim(KEY_REQUEST, payload)

        val signPublicKey = signKey[KEY_PUBLIC_KEY] as RSAPublicKey
        val signPrivateKey = signKey[KEY_PRIVATE_KEY] as RSAPrivateKey
        val senderJwk = RSAKey.Builder(signPublicKey)
            .privateKey(signPrivateKey)
            .build()
        val signedJwt = SignedJWT(
            JWSHeader.Builder(JWSAlgorithm.RS256).build(),
            jwtClaimsSet.build()
        )
        signedJwt.sign(RSASSASigner(senderJwk))
        val jweObject = JWEObject(
            JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128CBC_HS256)
                .type(JOSEObjectType.JWT)
                .build(),
            Payload(signedJwt)
        )
        val encryptPublicKey = encryptKey[KEY_PUBLIC_KEY] as RSAPublicKey
        val encryptPrivateKey = encryptKey[KEY_PRIVATE_KEY] as RSAPrivateKey
        val recipientJWK = RSAKey.Builder(encryptPublicKey).privateKey(encryptPrivateKey).build()
        jweObject.encrypt(RSAEncrypter(recipientJWK.toRSAKey()))
        return jweObject.serialize()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decryptResponse(payload: String): String {
        val cert = getEncryptCert() ?: return payload
        val signKeys = cert[KEY_SIGN_CERT]
        val encryptKeys = cert[KEY_ENCRYPT_CERT]
        if (signKeys == null || encryptKeys == null || payload.isEmpty()) {
            return payload
        }
        return decrypt(
            signKeys[KEY_PUBLIC_KEY] as RSAPublicKey,
            encryptKeys[KEY_PRIVATE_KEY] as RSAPrivateKey,
            payload
        )
    }

    private fun decrypt(signKey: RSAPublicKey, decryptKey: RSAPrivateKey, payload: String): String {
        val jwt = EncryptedJWT.parse(payload)

        val rsaDecrypter: JWEDecrypter = RSADecrypter(decryptKey)
        jwt.decrypt(rsaDecrypter)

        val signedJWT: SignedJWT? = jwt.payload.toSignedJWT()

        val jwsVerifier = RSASSAVerifier(signKey)

        val decryptedResponse: String

        if (signedJWT?.verify(jwsVerifier) == true && verifyToken(signedJWT.jwtClaimsSet)) {
            decryptedResponse = signedJWT.jwtClaimsSet.toString()
        } else {
            throw ApiException("Verify payload failed!")
        }

        val data = Gson().fromJson(decryptedResponse, CommonResponse::class.java)

        return data?.response ?: ""
    }

    private fun verifyToken(claimsSet: JWTClaimsSet): Boolean {
        return claimsSet.audience.contains(ApiEncryptionConfig.API_ENCRYPTION_AUD)
                && claimsSet.issuer == ApiEncryptionConfig.API_ENCRYPTION_ISS
    }
}