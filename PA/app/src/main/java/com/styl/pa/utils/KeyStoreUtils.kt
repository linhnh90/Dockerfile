package com.styl.pa.utils

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import java.security.Key
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec


@ExcludeFromJacocoGeneratedReport
class KeyStoreUtils(var context: Context?) {

    companion object {

        const val ANDROID_KEY_STORE = "AndroidKeyStore"
        const val AES_MODE = "AES/GCM/NoPadding"

        const val KEY_ALIAS = "KEY_ENCRYPTED"
    }

    private var keyStore: KeyStore? = null

    private fun generateKey() {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore?.load(null)

        keyStore?.let { keyStore ->

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
                val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setKeySize(128)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(false)
                    .build()
                keyGenerator.init(keyGenParameterSpec)
                keyGenerator.generateKey()
            }
        }
    }

    private fun getSecretKey(): Key? {
        return keyStore?.getKey(KEY_ALIAS, null)
    }

    private fun deleteKey() {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore?.load(null)
        keyStore?.deleteEntry(KEY_ALIAS)
    }

    fun encrypt(rawData: String?): String {
        if (rawData.isNullOrEmpty()) {
            return ""
        }

        var shouldUpdateIv = false

        if (!MySharedPref(context).iv.isNullOrEmpty()) {
            val iv = Base64.decode(MySharedPref(context).iv, Base64.NO_PADDING)

            // old iv is 16 bytes and should be updated to 12 bytes as GCM mode require
            // also generate new key
            if (iv.size == 16) {
                shouldUpdateIv = true

                // delete old key
                deleteKey()
            }
        }

        generateKey()

        // init iv
        if (MySharedPref(context).iv.isNullOrEmpty() || shouldUpdateIv) {
            // GCM mode require 12bytes iv
            val iv = ByteArray(12)
            SecureRandom().nextBytes(iv)
            MySharedPref(context).iv = Base64.encodeToString(iv, Base64.NO_PADDING)
        }
        val iv = Base64.decode(MySharedPref(context).iv, Base64.NO_PADDING)

        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), GCMParameterSpec(128, iv))
        val encodedBytes = cipher.doFinal(rawData.toByteArray())
        return Base64.encodeToString(encodedBytes, Base64.NO_PADDING)
    }

    fun decrypt(encryptedData: String?): String {
        if (encryptedData.isNullOrEmpty()) {
            return ""
        }

        generateKey()

        val encrypted = Base64.decode(encryptedData, Base64.NO_PADDING)
        val cipher = Cipher.getInstance(AES_MODE)
        val iv = Base64.decode(MySharedPref(context).iv, Base64.NO_PADDING)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), GCMParameterSpec(128, iv))
        return String(cipher.doFinal(encrypted))
    }
}