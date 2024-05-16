package com.example.palette.data.info

import com.example.palette.data.base.BaseResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object InfoRequestManager {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://standard.alcl.cloud:24136")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val infoService: InfoService = retrofit.create(InfoService::class.java)

    suspend fun requestProfileInfo(token: String): BaseResponse<ProfileData>? {
        val response = infoService.profileInfo(token)
        return response.body()
    }
}