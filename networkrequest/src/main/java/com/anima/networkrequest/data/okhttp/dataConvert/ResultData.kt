package com.anima.networkrequest.data.okhttp.dataConvert

import com.google.gson.Gson

/**
 * Created by jianjianhong on 19-11-1
 */
abstract class ResultData: ResponseParser {
    companion object {
        val RESULT_SUCCESS = "1"
        val RESULT_FAIL = "0"
        val RESULT_USER_SESSION_TIMEOUT = "-1"
    }


    protected var result: String? = null
    protected var info: String? = null
    protected var dataSize: Int = 0

    abstract fun toResult(body: String, clazz: Class<*>)

    override fun parser(body: String, clazz: Class<*>): ResponseParser {
        val bodyVerificationCode = ResponseBodyBarricade.verifyBody(body)
        when (bodyVerificationCode) {
            ResponseBodyBarricade.VERIFY_NOT_DATA -> {
                this.result = RESULT_SUCCESS
                this.info = "没有数据"
            }
            ResponseBodyBarricade.VERIFY_DATA_STRUCTURE_ERROR -> {
                this.result = RESULT_FAIL
                this.info = "请求返回的数据结构不正确,请联系负责人!"
            }
            ResponseBodyBarricade.VERIFY_RESULT_FAIL -> {
                val mapType = ResponseBodyBarricade.genericType<Map<String, Any>>()
                val resultData = Gson().fromJson<Map<String, Any>>(body, mapType)
                this.result = resultData["result"]?.toString()?:"0"
                this.info = resultData["info"]?.toString()?: "请求出错"
            }
            ResponseBodyBarricade.VERIFY_RESULT_SUCCESS -> {
                toResult(body, clazz)
            }
            else -> {
                this.result = RESULT_SUCCESS
                this.info = "没有数据"
            }
        }
        return this
    }

    override fun getTotal(): Int {
        return dataSize
    }

    override fun isSuccess(): Boolean {
        return RESULT_SUCCESS.equals(result)
    }

    override fun errorMessage(): String {
        return info?:""
    }
}