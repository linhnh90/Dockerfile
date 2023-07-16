package com.styl.pa.interfaces

import java.io.File

/**
 * Created by NguyenHang on 9/4/2020.
 */
interface DownloadFilesListener {
    fun onDownloadSuccess(file: File, isConfigFile: Boolean)
    fun onDownloadFailed(isConfigFile: Boolean)
    fun onUnzipSuccess(path: String)
    fun onUnzipFailed()
    fun onProgressDownload(progress: Int)
    fun notFoundConfigFile()
}