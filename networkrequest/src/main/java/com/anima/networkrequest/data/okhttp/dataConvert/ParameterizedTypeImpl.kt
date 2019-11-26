package com.anima.networkrequest.data.okhttp.dataConvert

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Created by jianjianhong on 19-11-4
 */
class ParameterizedTypeImpl(val row: Class<*>, val types: Array<Type>): ParameterizedType {
    override fun getRawType(): Type {
        return row
    }

    override fun getOwnerType(): Type? {
        return null
    }

    override fun getActualTypeArguments(): Array<Type> {
        return types
    }
}