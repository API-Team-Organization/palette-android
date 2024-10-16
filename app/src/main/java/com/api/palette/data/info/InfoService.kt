package com.api.palette.data.info

import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH

interface InfoService {
    @GET("info/me")
    suspend fun profileInfo(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*"
    ): Response<DataResponse<ProfileData>>

    @PATCH("info/me")
    suspend fun changeInfo(
        @Header("X-AUTH-Token") token: String,
        @Body changeInfoRequest: ChangeInfoRequest
    ): Response<VoidResponse>
}
