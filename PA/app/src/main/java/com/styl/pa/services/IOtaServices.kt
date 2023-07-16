package com.styl.pa.services

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url

interface IOtaServices {

    @Streaming
    @GET
    fun downloadOtaFile(
            @Header("Range") byteRange: String,
            @Url urlPath: String
    ): Observable<Response<ResponseBody>>
}