package com.serpentcs.odoorpc.core.web.webclient.versionInfo

import com.serpentcs.odoorpc.core.entities.versionInfo.VersionInfo
import com.serpentcs.odoorpc.core.entities.versionInfo.VersionInfoReqBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface VersionInfoRequest {

    @POST("/web/webclient/version_info")
    fun versionInfo(
            @Body versionInfoReqBody: VersionInfoReqBody
    ): Call<VersionInfo>
}