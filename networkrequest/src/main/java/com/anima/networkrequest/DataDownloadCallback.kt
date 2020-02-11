package com.anima.networkrequest



/**
 * Created by jianjianhong on 19-11-7
 */
interface DataDownloadCallback {
    fun onProgress(fileName: String, progress: Int)
}