package com.serpentcs.odoorpc.authenticator

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AuthenticatorService : Service() {

    companion object {
        @JvmField
        val TAG = "AuthenticatorService"
    }

    override fun onBind(intent: Intent): IBinder
            = AccountAuthenticator(this).iBinder
}
