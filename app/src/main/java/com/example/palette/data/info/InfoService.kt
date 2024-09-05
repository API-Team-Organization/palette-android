package com.example.palette.data.info

import com.example.palette.data.base.BaseResponse
import com.example.palette.data.base.BaseVoidResponse
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
    ): Response<BaseResponse<ProfileData>>

    @PATCH("info/me")
    suspend fun changeInfo(
        @Header("X-AUTH-Token") token: String,
        @Body changeInfoRequest: ChangeInfoRequest
    ): Response<BaseVoidResponse>
}
