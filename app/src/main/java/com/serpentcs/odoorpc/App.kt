package com.serpentcs.odoorpc

import android.app.Application
import com.serpentcs.odoorpc.core.utils.CookiePrefs
import com.serpentcs.odoorpc.core.utils.Retrofit2Helper

class App : Application() {

    companion object {
        @JvmField
        val TAG: String = "App"

        @JvmField
        val KEY_ACCOUNT_TYPE = "${BuildConfig.APPLICATION_ID}.auth"
    }

    lateinit var cookiePrefs: CookiePrefs

    override fun onCreate() {
        super.onCreate()
        Retrofit2Helper.app = this
        cookiePrefs = CookiePrefs(this)
    }
}
