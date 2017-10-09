package com.serpentcs.odoorpc.core.entities.authenticate

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Params(

        @field:Expose
        @field:SerializedName("base_location")
        val baseLocation: String = "false",

        @field:Expose
        @field:SerializedName("login")
        val login: String = "false",

        @field:Expose
        @field:SerializedName("password")
        val password: String = "false",

        @field:Expose
        @field:SerializedName("db")
        val db: String = "false"
)