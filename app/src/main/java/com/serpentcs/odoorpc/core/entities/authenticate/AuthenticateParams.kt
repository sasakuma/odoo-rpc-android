package com.serpentcs.odoorpc.core.entities.authenticate

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AuthenticateParams(

        @field:Expose
        @field:SerializedName("base_location")
        val baseLocation: String = String(),

        @field:Expose
        @field:SerializedName("login")
        val login: String = String(),

        @field:Expose
        @field:SerializedName("password")
        val password: String = String(),

        @field:Expose
        @field:SerializedName("db")
        val db: String = String()
)