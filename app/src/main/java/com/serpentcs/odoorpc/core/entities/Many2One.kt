package com.serpentcs.odoorpc.core.entities

import com.google.gson.JsonArray

data class Many2One(
        val id: Int = 0,
        val name: String = "false"
) {
    constructor(array: JsonArray) : this(array[0].asInt, array[1].asString)
}
