package com.serpentcs.odoorpc.core.web.session.authenticate

import com.serpentcs.odoorpc.core.entities.authenticate.Authenticate
import com.serpentcs.odoorpc.core.entities.authenticate.AuthenticateReqBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticateRequest {

    @POST("/web/session/authenticate")
    fun authenticate(
            @Body authenticateReqBody: AuthenticateReqBody
    ): Call<Authenticate>
}
