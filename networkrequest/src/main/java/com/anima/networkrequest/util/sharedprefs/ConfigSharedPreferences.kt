package com.anima.networkrequest.util.sharedprefs

import android.content.Context

/**
 * Created by jianjianhong on 19-11-11
 */
class ConfigSharedPreferences private constructor(context: Context): UTSharedPreferences(context) {
    companion object {
        @Volatile
        var instance: ConfigSharedPreferences? = null

        fun getInstance(context: Context): ConfigSharedPreferences {
            if (instance == null) {
                synchronized(ConfigSharedPreferences::class) {
                    if (instance == null) {
                        instance = ConfigSharedPreferences(context)
                    }
                }
            }
            return instance!!
        }
    }

    override fun getName(): String {
        return "config"
    }
}