package com.example.palette.data.info

import com.example.palette.data.base.BaseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface InfoService {
    @GET("/info/me")
    suspend fun profileInfo(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*"
    ): Response<BaseResponse<ProfileData>>
}
