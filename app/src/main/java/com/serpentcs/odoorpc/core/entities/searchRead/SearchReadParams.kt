package com.serpentcs.odoorpc.core.entities.searchRead

import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SearchReadParams(
        @field:Expose
        @field:SerializedName("model")
        val model: String = String(),

        @field:Expose
        @field:SerializedName("fields")
        val fields: List<String> = listOf("id", "name"),

        @field:Expose
        @field:SerializedName("domain")
        val domain: List<List<Any>> = listOf(),

        @field:Expose
        @field:SerializedName("offset")
        val offset: Int = 0,

        @field:Expose
        @field:SerializedName("limit")
        val limit: Int = 0,

        @field:Expose
        @field:SerializedName("sort")
        val sort: String = String(),

        @field:Expose
        @field:SerializedName("context")
        val context: JsonObject = JsonObject()
)