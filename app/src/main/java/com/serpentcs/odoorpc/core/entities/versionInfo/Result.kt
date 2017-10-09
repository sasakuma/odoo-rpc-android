package com.serpentcs.odoorpc.core.entities.versionInfo

import com.google.gson.JsonArray
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Result(

        @field:Expose
        @field:SerializedName("server_serie")
        val serverSerie: String = "false",

        @Suppress("MemberVisibilityCanPrivate")
        @field:Expose
        @field:SerializedName("server_version_info")
        val serverVersionInfo: JsonArray = JsonArray(),

        @field:Expose
        @field:SerializedName("server_version")
        val serverVersion: String = "false",

        @field:Expose
        @field:SerializedName("protocol_version")
        val protocolVersion: Int = 0
) {
    val serverVersionType: String
        get() = if (serverVersionInfo.size() > 0) serverVersionInfo[3].asString else "false"

    val isServerVersionEnterprise: Boolean
        get() = if (serverVersionInfo.size() > 0) serverVersionInfo[5].asString.contains("e", true) else false
}