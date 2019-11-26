package com.anima.networkrequestTest

import com.anima.networkrequest.data.okhttp.dataConvert.ParameterizedTypeImpl
import com.anima.networkrequest.data.okhttp.dataConvert.ResponseParser
import com.google.gson.Gson
import java.io.StringReader

/**
 * Created by jianjianhong on 19-11-20
 */
class GitHubUserParser<T>: ResponseParser {

    private var resultData: List<T>? = null

    override fun parser(body: String, clazz: Class<*>): ResponseParser {
        val listType = ParameterizedTypeImpl(List::class.java, arrayOf(clazz))
        resultData = Gson().fromJson<List<T>>(StringReader(body), listType)
        return this
    }

    override fun getResult(): List<T>? {
        return resultData
    }

    override fun getTotal(): Int {
        return resultData?.size?:0
    }

    override fun isSuccess(): Boolean {
        return true
    }

    override fun errorMessage(): String {
        return ""
    }
}