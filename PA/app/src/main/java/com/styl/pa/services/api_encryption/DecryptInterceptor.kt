package com.styl.pa.services.api_encryption

import android.os.Build
import okhttp3.Interceptor
import okhttp3.Response
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.styl.pa.entities.common.Payload
import com.styl.pa.entities.proxy.ProxyRequestHeader
import com.styl.pa.utils.LogManager
import okhttp3.MediaType
import okhttp3.ResponseBody
import java.lang.Exception


class DecryptInterceptor: Interceptor {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (ApiIgnoreEncrypt.getInstance().isNoNeedToEncrypt(chain.request().url().uri().path)) {
            return response
        }
        if (response.isSuccessful && response.body() != null) {
            try {
                val newResponse = response.newBuilder()
                val rawResponse = response.body()!!.string()
                LogManager.d("RawResponse: $rawResponse")
                val encryptedDataResponse = Gson().fromJson(rawResponse, Payload::class.java)
                val responseString = ApiCryptoUtils.decryptResponse(encryptedDataResponse.payload ?: "")
                newResponse.body(ResponseBody.create(
                    MediaType.parse(ProxyRequestHeader.JSON_TYPE),
                    responseString
                ))
                return newResponse.build()
            } catch (e: Exception) {
                LogManager.i("Response Failed!")
            }
        }
        return response
    }
}