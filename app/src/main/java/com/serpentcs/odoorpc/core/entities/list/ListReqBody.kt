package com.serpentcs.odoorpc.core.entities.list

import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ListReqBody(

        @field:Expose
        @field:SerializedName("id")
        val id: String = "0",

        @field:Expose
        @field:SerializedName("jsonrpc")
        val jsonRPC: String = "2.0",

        @field:Expose
        @field:SerializedName("method")
        val method: String = "call",

        @field:Expose
        @field:SerializedName("params")
        val params: JsonObject = JsonObject()
)