package com.serpentcs.odoorpc.core.entities.versionInfo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.serpentcs.odoorpc.core.entities.HttpError
import com.serpentcs.odoorpc.core.entities.odooError.OdooError

data class VersionInfo(

        @Suppress("MemberVisibilityCanPrivate")
        @field:Expose
        @field:SerializedName("result")
        val result: Result = Result(),

        @Suppress("MemberVisibilityCanPrivate")
        @field:Expose
        @field:SerializedName("error")
        val odooError: OdooError = OdooError(),

        @Suppress("MemberVisibilityCanPrivate")
        val httpError: HttpError = HttpError()
) {
    val isSuccessful get() = httpError.code == 0 && odooError.message.isEmpty()
    val isHttpError get() = httpError.code != 0
    val isOdooError get() = odooError.code != 0
    val errorCode get() = if (httpError.code != 0) httpError.code else odooError.code
    val errorMessage get() = if (httpError.code != 0) httpError.message else odooError.data.message
}