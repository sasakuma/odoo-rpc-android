package com.serpentcs.odoorpc.core.web.dataset.searchRead

import com.serpentcs.odoorpc.core.entities.searchRead.SearchRead
import com.serpentcs.odoorpc.core.entities.searchRead.SearchReadReqBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SearchReadRequest {

    @POST("/web/dataset/search_read")
    fun searchRead(
            @Body searchReadReqBody: SearchReadReqBody
    ): Call<SearchRead>
}