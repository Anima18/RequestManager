package com.anima.networkrequest.entity

import com.anima.networkrequest.data.okhttp.dataConvert.ResponseParser
import java.io.File

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
    var asJson: Boolean = false
    //请求数据格式
    var dataFormat: DataFormat = DataFormat.OBJECT
    //请求数据类型
    var dataClass: Class<*>? = Map::class.java
    //请求数据解析器, 与dataClass冲突，优先dataParser
    var dataParser: ResponseParser? = null
    //请求提示
    var loadingMessage: String? = null
    //下载文件名称
    var downloadFileName: String? = null
    //下载文件存储路径
    var downloadFilePath: String? = null
    //上传文件列表
    var uploadFiles: List<File>? = null
}