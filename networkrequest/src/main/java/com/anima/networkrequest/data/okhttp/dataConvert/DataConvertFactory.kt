package com.anima.networkrequest.data.okhttp.dataConvert

import com.anima.networkrequest.entity.RequestParam

/**
 * Created by jianjianhong on 19-11-1
 */
object DataConvertFactory {

    fun <T> convert(body: String, param: RequestParam): ResponseParser? {

        return if(param.dataParser !== null) {
            param.dataParser!!.parser(body, param.dataClass!!)
        }else {
            when(param.dataFormat) {
                RequestParam.DataFormat.OBJECT -> return ObjectResultData<T>().parser(body, param.dataClass!!)

                RequestParam.DataFormat.LIST -> return ListResultData<List<T>>().parser(body, param.dataClass!!)

                RequestParam.DataFormat.PAGELIST -> return PageResultData<List<T>>().parser(body, param.dataClass!!)

                else ->  return null
            }
        }
    }
}