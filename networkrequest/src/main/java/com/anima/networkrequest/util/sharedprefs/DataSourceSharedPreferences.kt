package com.anima.networkrequest.util.sharedprefs

import android.content.Context

/**
 * Created by jianjianhong on 19-11-11
 */
class DataSourceSharedPreferences private constructor(context: Context): UTSharedPreferences(context) {
    companion object {
        @Volatile
        var instance: DataSourceSharedPreferences? = null

        fun getInstance(context: Context): DataSourceSharedPreferences {
            if (instance == null) {
                synchronized(DataSourceSharedPreferences::class) {
                    if (instance == null) {
                        instance = DataSourceSharedPreferences(context)
                    }
                }
            }
            return instance!!
        }
    }

    override fun getName(): String {
        return "DataSource"
    }
}