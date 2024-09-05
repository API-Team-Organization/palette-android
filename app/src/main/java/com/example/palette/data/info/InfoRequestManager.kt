package com.example.palette.data.info

import android.util.Log
import com.example.palette.common.Constant
import com.example.palette.data.base.BaseResponse
import com.example.palette.data.base.BaseVoidResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object InfoRequestManager {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://paletteapp.xyz/backend")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val infoService: InfoService = retrofit.create(InfoService::class.java)

    suspend fun profileInfoRequest(token: String): BaseResponse<ProfileData>? {
        val response = infoService.profileInfo(token, "*/*")
        Log.d(Constant.TAG, "response is $response")
        return response.body()
    }

    suspend fun changeNameRequest(token: String, username: String?, birthDate: String?): Response<BaseVoidResponse> {
        val request = ChangeInfoRequest(username, null)
        val response = infoService.changeInfo(token, request)

        return response
    }

    suspend fun changeBirthDateRequest(token: String, username: String?, birthDate: String): Response<BaseVoidResponse> {
        val request = ChangeInfoRequest(null, birthDate)
        val response = infoService.changeInfo(token, request)

        return response
    }
}