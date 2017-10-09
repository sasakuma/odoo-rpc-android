package com.serpentcs.odoorpc

import android.app.Application

class App : Application() {

    companion object {
        @JvmField
        val TAG: String = "App"

        @JvmField
        val KEY_ACCOUNT_TYPE = "${BuildConfig.APPLICATION_ID}.auth"
    }
}
