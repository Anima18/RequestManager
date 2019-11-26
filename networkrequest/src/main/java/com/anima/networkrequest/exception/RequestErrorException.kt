package com.anima.networkrequest.exception

/**
 * Created by jianjianhong on 19-11-7
 */
class RequestErrorException(val code: Int, override val message: String): Exception() {
}