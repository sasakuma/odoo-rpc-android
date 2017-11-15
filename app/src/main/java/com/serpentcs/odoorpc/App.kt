package com.serpentcs.odoorpc

import android.support.multidex.MultiDexApplication
import com.serpentcs.odoorpc.core.utils.CookiePrefs
import com.serpentcs.odoorpc.core.utils.LetterTileProvider
import com.serpentcs.odoorpc.core.utils.Retrofit2Helper

class App : MultiDexApplication() {

    companion object {
        @JvmField
        val TAG: String = "App"

        @JvmField
        val KEY_ACCOUNT_TYPE = "${BuildConfig.APPLICATION_ID}.auth"
    }

    lateinit var cookiePrefs: CookiePrefs
    private lateinit var letterTileProvider: LetterTileProvider

    override fun onCreate() {
        super.onCreate()
        Retrofit2Helper.app = this
        cookiePrefs = CookiePrefs(this)
        letterTileProvider = LetterTileProvider(this)
    }

    fun getLetterTile(displayName: String): ByteArray =
            letterTileProvider.getLetterTile(displayName)
}
