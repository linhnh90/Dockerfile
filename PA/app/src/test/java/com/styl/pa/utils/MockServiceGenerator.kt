package com.styl.pa.utils

import com.styl.pa.BuildConfig
import com.styl.pa.services.Config
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object MockServiceGenerator {
    fun <T> createTestService(serviceClass: Class<T>, mockServer: MockWebServer? = null): T {
        val httpClient = OkHttpClient.Builder()
                .connectTimeout(Config.CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(Config.READ_TIMEOUT, TimeUnit.MILLISECONDS)

        val logging = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logging.level = HttpLoggingInterceptor.Level.BASIC
        }

        httpClient.addInterceptor(logging)

        val retrofit = Retrofit.Builder()
                .baseUrl(mockServer?.url("/") ?: HttpUrl.get(Config.BASE_URL))
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        return retrofit.create(serviceClass)
    }
}