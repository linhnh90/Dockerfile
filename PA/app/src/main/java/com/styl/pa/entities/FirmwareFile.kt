package com.styl.pa.entities

import android.content.Context
import com.styl.pa.modules.main.presenter.MainPresenter
import com.styl.pa.utils.GeneralUtils
import java.io.File
import java.security.PrivateKey
import javax.crypto.spec.SecretKeySpec

/**
 * Created by NguyenHang on 10/27/2020.
 */
class FirmwareFile(val context: Context?) {

    companion object {
        const val AES_KEY_LENGTH = 256
        const val IV_LENGTH = 12
        const val TAG_LENGTH = 16
    }

    var file: File?
    var aesKey: ByteArray? = null
    var iv: ByteArray? = null
    var tag: ByteArray? = null

    init {
        val filePath = (context?.getExternalFilesDir(null)?.absolutePath ?: "") +
                "/${MainPresenter.FIRMWARE_FOLDER}/${MainPresenter.FIRMWARE_FILE_NAME}"
        file = File(filePath)
    }

    fun parse(firmware: File, rsaPrivateKey: PrivateKey) {
        val signInfoSize = AES_KEY_LENGTH + IV_LENGTH + TAG_LENGTH
        if (firmware.length() > signInfoSize) {
            val posStartSignedInfo = firmware.length() - signInfoSize
            val signInfo = GeneralUtils.readOTADecryptInfo(
                    firmware.absolutePath,
                    posStartSignedInfo
            )
            if (signInfo != null) {
                this.aesKey = signInfo.copyOfRange(0, AES_KEY_LENGTH)
                this.iv = signInfo.copyOfRange(AES_KEY_LENGTH, AES_KEY_LENGTH + IV_LENGTH)
                this.tag = signInfo.copyOfRange(AES_KEY_LENGTH + IV_LENGTH, signInfo.size)
                val keyDecrypted = GeneralUtils.decryptRsa(this.aesKey, rsaPrivateKey)
                val secretKeySpec = SecretKeySpec(keyDecrypted, "AES")
                this.file = GeneralUtils.readAndDecryptOTAPackage(
                        firmware,
                        file!!.absolutePath,
                        signInfoSize.toLong(),
                        iv,
                        tag,
                        secretKeySpec
                )
            }
        }
    }
}

