package com.serpentcs.odoorpc.core.entities.odooError

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Data(
        @field:Expose
        @field:SerializedName("debug")
        val debug: String = String(),

        @field:Expose
        @field:SerializedName("exception_type")
        val exceptionType: String = String(),

        @field:Expose
        @field:SerializedName("message")
        val message: String = String(),

        @field:Expose
        @field:SerializedName("name")
        val name: String = String(),

        @field:Expose
        @field:SerializedName("arguments")
        val arguments: List<String> = listOf()
)
