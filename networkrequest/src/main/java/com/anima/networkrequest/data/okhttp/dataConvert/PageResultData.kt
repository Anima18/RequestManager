package com.anima.networkrequest.data.okhttp.dataConvert

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.StringReader

/**
 * Created by jianjianhong on 19-11-16
 */
class PageResultData<T>: ResultData() {

    var data: PageInfo<T>? = null

    data class PageInfo<T>(val pageSize: Int, val pages: Int, val total: Int, @SerializedName(
        value = "list",
        alternate = ["rows"]
    ) val list: List<T>)

    override fun getResult(): List<T>? {
        return data!!.list
    }

    override fun toResult(body: String, clazz: Class<*>) {
        val type = ParameterizedTypeImpl(PageResultData::class.java, arrayOf(clazz))
        val resultData = Gson().fromJson<PageResultData<T>>(StringReader(body), type)
        this.data = resultData.data
        this.result = RESULT_SUCCESS
        this.info = ""
        this.dataSize = data!!.total
    }
}