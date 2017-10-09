package com.serpentcs.odoorpc.core.entities.odooError

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Data(
        @field:Expose
        @field:SerializedName("debug")
        val debug: String = "false",

        @field:Expose
        @field:SerializedName("exception_type")
        val exceptionType: String = "false",

        @field:Expose
        @field:SerializedName("message")
        val message: String = "false",

        @field:Expose
        @field:SerializedName("name")
        val name: String = "false",

        @field:Expose
        @field:SerializedName("arguments")
        val arguments: List<String> = listOf()
)
