package com.anima.networkrequest.data.okhttp.dataConvert

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

/**
 * Created by jianjianhong on 19-11-1
 */
object ResponseBodyBarricade {
    const val VERIFY_RESULT_SUCCESS = 1

    const val VERIFY_RESULT_FAIL = 0
    //没数据
    const val VERIFY_NOT_DATA = 2
    //数据结构不正确
    const val VERIFY_DATA_STRUCTURE_ERROR = 3

    @Throws(JsonSyntaxException::class)
    fun verifyBody(body: String): Int {
        if (TextUtils.isEmpty(body)) {
            return VERIFY_NOT_DATA
        }

        try {
            val mapType = genericType<Map<String, Any>>()
            val resultData = Gson().fromJson<Map<String, Any>>(body, mapType)
            if (!checkResultFiled(resultData)) {
                return VERIFY_DATA_STRUCTURE_ERROR
            } else {
                val result = resultData.get("result").toString()
                val data = resultData.get("data")

                return if (ResultData.RESULT_SUCCESS.equals(result)) {
                    if (data == null || TextUtils.isEmpty(data!!.toString())) {
                        VERIFY_NOT_DATA
                    } else {
                        VERIFY_RESULT_SUCCESS
                    }
                } else {
                    VERIFY_RESULT_FAIL
                }
            }
        } catch (e: JsonSyntaxException) {
            return VERIFY_DATA_STRUCTURE_ERROR
        }

    }

    inline fun <reified T> genericType() = object : TypeToken<T>() {}.type

    private fun checkResultFiled(resultData: Map<String, Any>?): Boolean {
        return !(resultData == null
                || !resultData.containsKey("result")
                || !resultData.containsKey("info")
                || !resultData.containsKey("data"))
    }
}