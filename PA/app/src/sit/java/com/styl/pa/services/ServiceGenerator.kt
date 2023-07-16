package com.styl.pa.services

import com.styl.pa.BuildConfig
import com.styl.pa.services.api_encryption.DecryptInterceptor
import com.styl.pa.services.api_encryption.EncryptInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by trangpham on 8/29/2018
 */
class ServiceGenerator {

    companion object {

        fun <T> createService(serviceClass: Class<T>): T {
            val httpClient = OkHttpClient.Builder()
                    .connectTimeout(Config.CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(Config.READ_TIMEOUT, TimeUnit.MILLISECONDS)

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(EKioskInterceptor())
            httpClient.addInterceptor(logging)

            httpClient.addInterceptor(EncryptInterceptor())
            httpClient.addInterceptor(DecryptInterceptor())

            val retrofit = Retrofit.Builder()
                    .baseUrl(Config.BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            return retrofit.create(serviceClass)
        }

        fun <T> createOtaService(serviceClass: Class<T>): T {
            val httpClient = OkHttpClient.Builder()
                    .connectTimeout(Config.CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(Config.OTA_READ_TIMEOUT, TimeUnit.MILLISECONDS)

            val logging = HttpLoggingInterceptor()

            //download not work with Level.BODY
            logging.level = HttpLoggingInterceptor.Level.BASIC

            httpClient.addInterceptor(logging)

//            SSLManager.handleSSLCertification(httpClient)

            val retrofit = Retrofit.Builder()
                    .baseUrl(Config.OTA_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            return retrofit.create(serviceClass)
        }
    }
}