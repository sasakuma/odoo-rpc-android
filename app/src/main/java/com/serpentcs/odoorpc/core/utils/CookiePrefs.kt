package com.serpentcs.odoorpc.core.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Cookie

class CookiePrefs(context: Context) : Prefs(TAG, context) {

    companion object {
        @JvmField
        val TAG = "CookiePrefs"
    }

    private val gson = Gson()
    private val type = object : TypeToken<ArrayList<ClonedCookie>>() {}.type

    fun getCookies(): MutableList<Cookie> {
        val activeUser = context.getActiveOdooUser()
        if (activeUser != null) {
            val cookiesStr = getString(activeUser.androidName)
            if (cookiesStr.isNotEmpty()) {
                val clonedCookies: ArrayList<ClonedCookie> = gson.fromJson(cookiesStr, type)
                val cookies = arrayListOf<Cookie>()
                for (clonedCookie in clonedCookies) {
                    cookies += clonedCookie.toCookie()
                }
                return cookies
            }
        }
        return arrayListOf()
    }

    fun setCookies(cookies: MutableList<Cookie>?) {
        val clonedCookies = arrayListOf<ClonedCookie>()
        if (cookies != null) {
            for (cookie in cookies) {
                clonedCookies += ClonedCookie.fromCookie(cookie)
            }
        }
        val cookiesStr = gson.toJson(clonedCookies, type)
        val activeUser = context.getActiveOdooUser()
        if (activeUser != null) {
            putString(activeUser.androidName, cookiesStr)
        }
    }
}