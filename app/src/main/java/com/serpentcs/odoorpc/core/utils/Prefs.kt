package com.serpentcs.odoorpc.core.utils

import android.content.Context
import android.content.SharedPreferences

abstract class Prefs(name: String, context: Context) {

    private val preferences: SharedPreferences
            = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    fun getBoolean(key: String, defValue: Boolean = false): Boolean
            = preferences.getBoolean(key, defValue)

    fun putBoolean(key: String, defValue: Boolean = false)
            = preferences.edit().putBoolean(key, defValue).apply()

    fun getInt(key: String, defValue: Int = 0): Int
            = preferences.getInt(key, defValue)

    fun putInt(key: String, defValue: Int = 0)
            = preferences.edit().putInt(key, defValue).apply()

    fun getString(key: String, defValue: String = String()): String
            = preferences.getString(key, defValue)

    fun putString(key: String, defValue: String = String())
            = preferences.edit().putString(key, defValue).apply()

    fun clear() = preferences.edit().clear().apply()
}