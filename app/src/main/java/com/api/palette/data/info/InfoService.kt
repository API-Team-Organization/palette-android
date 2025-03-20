package com.api.palette.data.info

import com.api.palette.domain.model.DataResponse
import com.api.palette.domain.model.ProfileData
import com.api.palette.domain.model.VoidResponse
import retrofit2.Response
import retrofit2.http.*

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
