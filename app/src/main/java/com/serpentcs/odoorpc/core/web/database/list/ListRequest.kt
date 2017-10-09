package com.serpentcs.odoorpc.core.web.database.list

import com.serpentcs.odoorpc.core.entities.list.List
import com.serpentcs.odoorpc.core.entities.list.ListReqBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ListRequest {

    @POST("/web/database/list")
    fun list(
            @Body listReqBody: ListReqBody
    ): Call<List>
}