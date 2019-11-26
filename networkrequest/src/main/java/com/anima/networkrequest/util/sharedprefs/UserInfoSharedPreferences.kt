package com.anima.networkrequest.util.sharedprefs

import android.content.Context

/**
 * Created by jianjianhong on 19-11-11
 */
class UserInfoSharedPreferences private constructor(context: Context): UTSharedPreferences(context) {

    companion object {
        @Volatile
        var instance: UserInfoSharedPreferences? = null

        fun getInstance(context: Context): UserInfoSharedPreferences {
            if (instance == null) {
                synchronized(UserInfoSharedPreferences::class) {
                    if (instance == null) {
                        instance = UserInfoSharedPreferences(context)
                    }
                }
            }
            return instance!!
        }
    }
    override fun getName(): String {
        return "UserInfo"
    }
}