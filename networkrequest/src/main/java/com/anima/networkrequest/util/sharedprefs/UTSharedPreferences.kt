package com.anima.networkrequest.util.sharedprefs

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by jianjianhong on 19-11-11
 */
abstract class UTSharedPreferences(context: Context) {
    private var preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences(getName(), Context.MODE_PRIVATE)
    }

    protected abstract fun getName(): String

    fun putLongValue(key: String, value: Long) {
        preferences.edit().putLong(key, value).apply()
    }

    fun putIntValue(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun putStringValue(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun putBooleanValue(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun getLongValue(key: String, defValue: Long): Long {
        return preferences.getLong(key, defValue)
    }

    fun getIntValue(key: String, defValue: Int): Int {
        return preferences.getInt(key, defValue)
    }

    fun getBooleanValue(key: String, defValue: Boolean): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    fun getStringValue(key: String, defValue: String): String? {
        return preferences.getString(key, defValue)
    }

    fun clear() {
        preferences.edit().clear().commit()
    }

    fun remove(key: String) {
        preferences.edit().remove(key).commit()
    }
}