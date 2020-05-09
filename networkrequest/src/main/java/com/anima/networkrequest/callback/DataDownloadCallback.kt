package com.anima.networkrequest.callback



/**
 * Created by jianjianhong on 19-11-7
 */
interface DataDownloadCallback {
    fun onProgress(fileName: String, progress: Int)
}