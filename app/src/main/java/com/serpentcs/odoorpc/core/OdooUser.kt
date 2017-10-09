package com.serpentcs.odoorpc.core

import android.accounts.Account

data class OdooUser(
        val protocol: Int,
        val host: String,
        val login: String,
        val password: String,
        val database: String,
        val serverVersion: String,
        val timeZone: String,
        var isActive: Boolean,
        val account: Account
) {
    val androidName: String
        get() = "$login[$database]"
}
