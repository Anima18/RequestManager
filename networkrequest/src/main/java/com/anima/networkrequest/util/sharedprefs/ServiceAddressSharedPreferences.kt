package com.anima.networkrequest.util.sharedprefs

import android.content.Context

/**
 * Created by jianjianhong on 19-11-11
 */
class ServiceAddressSharedPreferences private constructor(context: Context): UTSharedPreferences(context) {
    companion object {
        @Volatile
        var instance: ServiceAddressSharedPreferences? = null

        fun getInstance(context: Context): ServiceAddressSharedPreferences {
            if (instance == null) {
                synchronized(ServiceAddressSharedPreferences::class) {
                    if (instance == null) {
                        instance = ServiceAddressSharedPreferences(context)
                    }
                }
            }
            return instance!!
        }
    }

    override fun getName(): String {
        return "ServiceAddress"
    }
}