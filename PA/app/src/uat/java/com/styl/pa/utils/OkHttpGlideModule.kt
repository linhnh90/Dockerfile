package com.styl.pa.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.styl.pa.services.Config
import okhttp3.OkHttpClient
import java.io.InputStream
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Created by trangpham on 1/29/2019
 */
@GlideModule
class OkHttpGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {

        val httpClient = OkHttpClient.Builder()
            .connectTimeout(Config.CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(Config.READ_TIMEOUT, TimeUnit.MILLISECONDS)


        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(httpClient.build())
        )
    }
}