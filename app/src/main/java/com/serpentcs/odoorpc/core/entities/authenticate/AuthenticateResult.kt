package com.serpentcs.odoorpc.core.entities.authenticate

import android.os.Bundle
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.serpentcs.odoorpc.core.Odoo

data class AuthenticateResult(

        @field:Expose
        @field:SerializedName("is_superuser")
        val superuser: Boolean = false,

        @field:Expose
        @field:SerializedName("company_id")
        val companyId: Int = 0,

        @field:Expose
        @field:SerializedName("web.base.url")
        val webBaseUrl: String = String(),

        @field:Expose
        @field:SerializedName("session_id")
        val sessionId: String = String(),

        @field:Expose
        @field:SerializedName("server_version")
        val serverVersion: String = String(),

        @field:Expose
        @field:SerializedName("is_admin")
        val admin: Boolean = false,

        @field:Expose
        @field:SerializedName("uid")
        val uid: Int = 0,

        @field:Expose
        @field:SerializedName("partner_id")
        val partnerId: Int = 0,

        @field:Expose
        @field:SerializedName("user_companies")
        val userCompanies: Boolean = false,

        @field:Expose
        @field:SerializedName("name")
        val name: String = String(),

        @field:Expose
        @field:SerializedName("server_version_info")
        val serverVersionInfo: JsonArray = JsonArray(),

        @field:Expose
        @field:SerializedName("user_context")
        val userContext: JsonObject = JsonObject(),

        @field:Expose
        @field:SerializedName("db")
        val db: String = String(),

        @field:Expose
        @field:SerializedName("username")
        val username: String = String(),

        @field:Expose
        @field:SerializedName("currencies")
        val currencies: JsonObject = JsonObject(),

        @field:Expose
        @field:SerializedName("web_tours")
        val webTours: JsonArray = JsonArray(),

        var imageSmall: String = String(),
        var password: String = String()
) {
    val androidName: String
        get() = "$username[$db]"

    val toBundle: Bundle
        get() = Bundle().apply {
            putString("protocol", Odoo.protocol.name)
            putString("host", Odoo.host)
            putString("login", username)
            putString("password", password)
            putString("database", db)
            putString("serverVersion", serverVersion)
            putString("isAdmin", admin.toString())
            putString("id", uid.toString())
            putString("name", name)
            putString("imageSmall", imageSmall)
            putString("context", userContext.toString())
            putString("active", false.toString())
        }
}
