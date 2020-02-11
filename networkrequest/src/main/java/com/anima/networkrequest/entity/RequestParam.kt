package com.anima.networkrequest.entity

import com.anima.networkrequest.data.okhttp.dataConvert.ResponseParser

/**
 * Created by jianjianhong on 19-11-1
 */
class RequestParam {

    enum class Method {
        GET, POST
    }

    enum class DataFormat {
        OBJECT, LIST, PAGELIST
    }

    //请求URL
    var url: String = ""
    //请求方式
    var method: Method = Method.GET
    //请求参数
    var params: MutableMap<String, String>? = null
    //请求数据格式
    var dataFormat: DataFormat = DataFormat.OBJECT
    //请求数据类型
    var dataClass: Class<*>? = Map::class.java
    //请求数据解析器, 与dataClass冲突，优先dataParser
    var dataParser: ResponseParser? = null
    //请求提示
    var loadingMessage: String? = null

    var downloadFileName: String? = null

    var downloadFilePath: String? = null
}