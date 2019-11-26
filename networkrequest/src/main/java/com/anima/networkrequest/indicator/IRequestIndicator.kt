package com.anima.networkrequest.indicator

/**
 * Created by jianjianhong on 19-11-7
 */
interface IRequestIndicator {
    fun showProgress()

    fun hideProgress()

    fun updateProgress(message: String, progress: Int)
}