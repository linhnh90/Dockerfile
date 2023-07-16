package com.styl.pa.services.api_encryption

import com.styl.pa.services.ApiEncryptionConfig

class ApiIgnoreEncrypt private constructor() {

    private var listApiNoNeedToEncrypted = ArrayList<String>()

    init {
        val listApiIgnoreEncrypt = ApiEncryptionConfig.ApiWithoutEncrypt.values()
        for (api in listApiIgnoreEncrypt) {
            listApiNoNeedToEncrypted.add(api.apiPath)
        }
    }

    companion object {

        private var instance: ApiIgnoreEncrypt? = null

        fun getInstance(): ApiIgnoreEncrypt {
            if (instance == null) {
                instance = ApiIgnoreEncrypt()
            }
            return instance!!
        }
    }

    fun isNoNeedToEncrypt(path: String): Boolean {
        return listApiNoNeedToEncrypted.contains(path)
    }
}