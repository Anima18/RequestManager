package com.anima.networkrequest.data.okhttp.dataConvert

import com.google.gson.Gson
import java.io.StringReader

/**
 * Created by jianjianhong on 19-11-16
 */
class ListResultData<T>: ResultData() {

    var data: List<T>? = null

    override fun toResult(body: String, clazz: Class<*>) {
        val listType = ParameterizedTypeImpl(List::class.java, arrayOf(clazz))
        val type = ParameterizedTypeImpl(ObjectResultData::class.java, arrayOf(listType))
        val resultData = Gson().fromJson<ObjectResultData<List<T>>>(StringReader(body), type)
        this.data = resultData.data
        this.result = RESULT_SUCCESS
        this.info = ""
        this.dataSize = 1
    }

    override fun getResult(): List<T>? {
        return data
    }
}