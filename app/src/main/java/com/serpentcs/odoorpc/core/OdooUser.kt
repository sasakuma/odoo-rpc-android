package com.serpentcs.odoorpc.core

import android.accounts.Account
import com.google.gson.JsonObject
import com.serpentcs.odoorpc.App
import com.serpentcs.odoorpc.core.utils.Retrofit2Helper

data class OdooUser(
        val protocol: Retrofit2Helper.Companion.Protocol = Retrofit2Helper.Companion.Protocol.HTTP,
        val host: String = String(),
        val login: String = String(),
        val password: String = String(),
        val database: String = String(),
        val serverVersion: String = String(),
        val isAdmin: Boolean = false,
        val id: Int = 0,
        val name: String = String(),
        val imageSmall: String = String(),
        val context: JsonObject = JsonObject(),
        val isActive: Boolean = false,
        val account: Account = Account("false", App.KEY_ACCOUNT_TYPE)
) {
    val androidName: String
        get() = "$login[$database]"

    val timezone: String
        get() = context["tz"].asString
}
