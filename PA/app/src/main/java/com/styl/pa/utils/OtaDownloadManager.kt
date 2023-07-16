package com.styl.pa.utils

import android.annotation.SuppressLint
import android.content.Context
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.interfaces.DownloadFilesListener
import com.styl.pa.services.IOtaServices
import com.styl.pa.services.ServiceGenerator
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.util.regex.Pattern

/**
 * Created by NguyenHang on 12/20/2020.
 */
@ExcludeFromJacocoGeneratedReport
class OtaDownloadManager(private var context: Context?, private var folder: File?) {

    companion object {
        const val TAG: String = "OTA"

        const val CONFIG_FILE = "ota_config.xml"
        const val FIRMWARE_FILE = "ota.zip"

        const val FIRMWARE_PATH = "otapackages/ota_patch.zip"
        const val CONFIG_PATH = "otapackages/config.xml"

        const val NO_RESPONSE_BODY = "No Response Body"
    }

    var listener: DownloadFilesListener? = null

    private val downloadService: IOtaServices =
            ServiceGenerator.createOtaService(IOtaServices::class.java)

    @SuppressLint("CheckResult")
    private fun download(
            urlPath: String,
            fileName: String,
            dlProgressConsumer: Consumer<Int>,
            errConsumer: Consumer<Throwable>,
            isConfigFile: Boolean
    ): Observable<File> {
        return Observable.create(ObservableOnSubscribe<File> @ExcludeFromJacocoGeneratedReport {

            val temporaryFile = File(folder, fileName)

            downloadService.downloadOtaFile("bytes=", urlPath)
                    .flatMap(
                            FileDownloader(temporaryFile, it)
                    )
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(dlProgressConsumer, errConsumer)

        }).observeOn(AndroidSchedulers.mainThread())
    }


    @SuppressLint("CheckResult")
    private fun downLoadOtaFile(urlPath: String, fileName: String, isConfigFile: Boolean) {
        download(
                urlPath,
                fileName,
                @ExcludeFromJacocoGeneratedReport { progress ->
                    listener?.onProgressDownload(progress)
                },
                @ExcludeFromJacocoGeneratedReport {
                    LogManager.i(TAG, "An error occurred when download OTA file")
                    if (isConfigFile) {
                        listener?.notFoundConfigFile()
                    } else {
                        listener?.onDownloadFailed(isConfigFile)
                    }
                },
                isConfigFile
        ).subscribe(
                @ExcludeFromJacocoGeneratedReport {
                    listener?.onDownloadSuccess(File(folder, fileName), isConfigFile)
                },
                @ExcludeFromJacocoGeneratedReport {
                    if (isConfigFile || it.message == NO_RESPONSE_BODY) {
                        listener?.notFoundConfigFile()
                    } else {
                        listener?.onDownloadFailed(isConfigFile)
                    }
                }
        )
    }

    fun downloadOtaConfig() {
        if (folder?.exists() == false) {
            folder?.mkdirs()
        }
        downLoadOtaFile(CONFIG_PATH, CONFIG_FILE, true)
    }

    fun downloadOtaPackage() {
        if (folder?.exists() == false) {
            folder?.mkdirs()
        }
        downLoadOtaFile(FIRMWARE_PATH, FIRMWARE_FILE, false)
    }

    @ExcludeFromJacocoGeneratedReport
    private inner class FileDownloader(
            val file: File,
            val fileEmitter: ObservableEmitter<File>
    ) : Function<Response<ResponseBody>, Observable<Int>> {

        var startingByte    = 0L
        var endingByte      = 0L
        var totalBytes      = 0L
        var contentLength   = 0L
        var totalRead       = 0L

        var contentRangePattern = "bytes ([0-9]*)-([0-9]*)/([0-9]*)"
        fun parseContentRange(contentRange: String?) {
            if (contentRange == null) return
            val matcher = Pattern.compile(contentRangePattern).matcher(contentRange)
            if (matcher.find()) {
                startingByte = matcher.group(1).toLong()
                endingByte = matcher.group(2).toLong()
                totalBytes = matcher.group(3).toLong()
            }
        }

        override fun apply(response: Response<ResponseBody>): Observable<Int> {
            return Observable.create(
                    @ExcludeFromJacocoGeneratedReport
                    fun(subscriber: ObservableEmitter<Int>) {
                        try {
                            if (!response.isSuccessful) {
                                val err = IllegalStateException("Code: ${response.code()}, ${response.message()}; Response $response")
                                fileEmitter.onError(err)
                                return
                            }

                            if (response.body() == null || response.body()!!.contentLength() == 0L) {
                                fileEmitter.onError(IllegalStateException(NO_RESPONSE_BODY))
                                return
                            }

                            contentLength = response.body()!!.contentLength()

                            MySharedPref(context).latestOtaPackageSize = contentLength

                            if (response.code() == HttpURLConnection.HTTP_PARTIAL) {
                                // continue download
                                parseContentRange(response.headers().get("Content-Range"))
                                LogManager.i(TAG, "Getting range from $startingByte to $endingByte of $totalBytes bytes")
                            } else {
                                endingByte = contentLength
                                totalBytes = contentLength
                                if (file.exists() && !file.delete()) {
                                    LogManager.i(TAG, "Cannot delete old file before new download")
                                }
                            }

                            totalRead = startingByte

                            val inputStream = response.body()!!.byteStream()
                            val fileOutputStream = FileOutputStream(file, true)

                            var len = 0
                            var progress = 0
                            val buffer = ByteArray(8192)
                            while (inputStream.read(buffer).also { len = it } != -1) {
                                totalRead += len
                                fileOutputStream.write(buffer, 0, len)
                                val currentProgress = (totalRead * 100 / totalBytes).toInt()
                                if (currentProgress != progress) {
                                    progress = currentProgress
                                    subscriber.onNext(progress)
                                }
                            }

                            inputStream.close()
                            fileOutputStream.close()

                            subscriber.onComplete()
                            fileEmitter.onNext(file)
                            fileEmitter.onComplete()
                        } catch (e: Exception) {
                            fileEmitter.onError(e)
                            subscriber.onError(e)
                        }
                    }
            )
        }
    }
}