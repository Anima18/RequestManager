package com.anima.networkrequest.data.okhttp

import android.content.Context
import com.anima.networkrequest.util.sharedprefs.UserInfoSharedPreferences
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by jianjianhong on 19-11-11
 */
const val USER_SEEION_ID: String = "user_session_id"
class CookieInterceptor(private val context: Context): Interceptor {
    private var preferences: UserInfoSharedPreferences? = null
    init {
        preferences = UserInfoSharedPreferences.getInstance(context)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val cookie = preferences!!.getStringValue(USER_SEEION_ID, "")!!
        val request = chain.request()
            .newBuilder()
            .addHeader("Cookie", "JSESSIONID=$cookie")
            .addHeader("token", cookie!!)
            .addHeader("user-agent", "android")
            .addHeader("user_agent", "android")
            .build()
        return chain.proceed(request)
    }
}