package com.anima.networkrequest.entity

/**
 * Created by jianjianhong on 20-5-9
 */
data class ViewModelStatus<T>(var success: Boolean, var data: T?, var message: String?, var totalCount: Int? = 0)