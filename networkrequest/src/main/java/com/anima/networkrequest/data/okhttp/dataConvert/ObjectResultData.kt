package com.anima.networkrequest.data.okhttp.dataConvert

import com.google.gson.Gson
import java.io.StringReader

/**
 * Created by jianjianhong on 19-11-1
 */
class ObjectResultData<T>: ResultData() {
    var data: T? = null

    override fun toResult(body: String, clazz: Class<*>) {
        val type = ParameterizedTypeImpl(ObjectResultData::class.java, arrayOf(clazz))
        val resultData = Gson().fromJson<ObjectResultData<T>>(StringReader(body), type)
        this.data = resultData.data
        this.result = RESULT_SUCCESS
        this.info = ""
        this.dataSize = 1
    }

    override fun getResult(): T? {
        return data
    }
}

