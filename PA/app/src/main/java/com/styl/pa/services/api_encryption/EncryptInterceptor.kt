package com.styl.pa.services.api_encryption

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.styl.pa.entities.common.Payload
import com.styl.pa.entities.proxy.ProxyRequestHeader
import com.styl.pa.utils.LogManager
import okhttp3.*
import okio.Buffer
import java.lang.Exception

class EncryptInterceptor: Interceptor {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val uri = request.url().uri()
        if (ApiIgnoreEncrypt.getInstance().isNoNeedToEncrypt(uri.path)) {
            return chain.proceed(request)
        }
        val buffer = Buffer()
        val body: RequestBody? = request.body()
        if (body != null) {
            try {
                body.writeTo(buffer)
                val bodyAsString = buffer.readUtf8()
                val encryptedRequestBody = ApiCryptoUtils.encryptRequest(bodyAsString)
                val mediaType = MediaType.parse(ProxyRequestHeader.JSON_TYPE)
                val commonRequest = Payload(encryptedRequestBody)
                val commonRequestToString = Gson().toJson(commonRequest)
                LogManager.d("EncryptedRequest: $commonRequestToString")
                val newRequest = request.newBuilder()
                    .method(
                        request.method(),
                        RequestBody.create(mediaType, commonRequestToString)
                    ).build()
                return chain.proceed(newRequest)
            } catch (e: Exception) {
                LogManager.i("Request Failed!")
            }
        }
        return chain.proceed(request)
    }
}