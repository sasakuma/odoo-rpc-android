package com.serpentcs.odoorpc.core.entities.odooError

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OdooError(
        @field:Expose
        @field:SerializedName("message")
        val message: String = String(),

        @field:Expose
        @field:SerializedName("code")
        val code: Int = 0,

        @field:Expose
        @field:SerializedName("data")
        val data: Data = Data()
)
