package com.styl.pa.services

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.security.SecureRandom
import java.util.*

class EKioskInterceptor: Interceptor {

    companion object {
        private const val TAG = "eKiosk.log"
        private const val X_CORRELATION_ID_HEADER = "x-correlation-id"
        private const val NONCE_HEADER = "ekiosk-Nonce"
        private const val TIMESTAMP_HEADER = "ekiosk-Timestamp"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val nonce = SecureRandom().nextInt() and Int.MAX_VALUE
        val timestamp = System.currentTimeMillis() / 1000
        val newRequest = request.newBuilder()
            .addHeader(NONCE_HEADER, nonce.toString())
            .addHeader(TIMESTAMP_HEADER, timestamp.toString())
            .build()

        val response = chain.proceed(newRequest)
        val responseHeaders = response.headers()
        for (header in responseHeaders.names()) {
            if (header.equals(X_CORRELATION_ID_HEADER, true)) {
                Log.i(TAG, "$header: ${responseHeaders.get(header)}")
            }
        }
        return response
    }
}