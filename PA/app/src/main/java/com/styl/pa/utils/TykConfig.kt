package com.styl.pa.utils

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.services.Config
import java.io.InputStream
import java.lang.Exception

@ExcludeFromJacocoGeneratedReport
class TykConfig {

    @ExcludeFromJacocoGeneratedReport
    companion object {

        const val INTRANET_KEY_FILE = "tyk/internal_intranet"
        const val EXTERNAL_KEY_FILE = "tyk/external"

        private const val SUB_BASE = "eKiosk-Android"

        var internalJwtHeader = ""
        var externalJwtHeader = ""

        private fun generateHeader(): JWSHeader {
            return JWSHeader.Builder(JWSAlgorithm.RS256)
                    .type(JOSEObjectType.JWT)
                    .build()
        }

        fun generateIntToken(time: Long, internalKeyInputStream: InputStream): String {
            var retValue = ""
            try {
                val header = generateHeader()
                val payload = JWTClaimsSet.Builder()
                    .claim("sub", "$SUB_BASE-$time")
                    .claim("name", Config.NAME)
                    .claim("iat", Config.IAT)
                    .claim("intranet", Config.INTRANET_POLICY_ID)
                    .claim("internal", Config.INTERNAL_POLICY_ID)
                    .build()

                val signedJwt = SignedJWT(header, payload)
                val privateKey = GeneralUtils.getKeyFromAsset(internalKeyInputStream)
                signedJwt.sign(RSASSASigner(privateKey))

                retValue = signedJwt.serialize()
            } catch (e: Exception) {
                LogManager.i("Unable to generate internal token")
            }
            return retValue
        }

        fun generateExtToken(time: Long, externalKeyInputStream: InputStream): String {
            var retValue = ""
            try {
                val header = generateHeader()
                val payload = JWTClaimsSet.Builder()
                    .claim("sub", "$SUB_BASE-$time")
                    .claim("name", Config.NAME)
                    .claim("iat", Config.IAT)
                    .claim("external", Config.EXTERNAL_POLICY_ID)
                    .build()

                val signedJwt = SignedJWT(header, payload)
                val privateKey = GeneralUtils.getKeyFromAsset(externalKeyInputStream)
                signedJwt.sign(RSASSASigner(privateKey))

                retValue = signedJwt.serialize()
            } catch (e: Exception) {
                LogManager.i("Unable to generate external token")
            }
            return retValue
        }

        fun generateTykToken(internalKeyInputStream: InputStream, externalKeyInputStream: InputStream) {
            if (Config.INTERNAL_POLICY_ID.isEmpty()) {
                return
            }
            val currentUnixTime = System.currentTimeMillis() / 1000
            val internalToken = generateIntToken(currentUnixTime, internalKeyInputStream)
            val externalToken = generateExtToken(currentUnixTime, externalKeyInputStream)
            if (internalToken.isNotEmpty()) internalJwtHeader = internalToken
            if (externalToken.isNotEmpty()) externalJwtHeader = externalToken
        }
    }
}