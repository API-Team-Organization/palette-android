package com.example.palette.data.info

import android.util.Log
import com.example.palette.common.Constant
import com.example.palette.data.ApiClient
import com.example.palette.data.base.BaseResponse
import com.example.palette.data.base.DataResponse
import com.example.palette.data.base.VoidResponse
import retrofit2.Response

object InfoRequestManager {
    private val infoService: InfoService = ApiClient.retrofit.create(InfoService::class.java)

    suspend fun profileInfoRequest(token: String): DataResponse<ProfileData>? {
        val response = infoService.profileInfo(token, "*/*")
        Log.d(Constant.TAG, "response is $response")
        return response.body()
    }

    suspend fun changeNameRequest(token: String, username: String?, birthDate: String?): Response<VoidResponse> {
        val request = ChangeInfoRequest(username, null)
        val response = infoService.changeInfo(token, request)

        return response
    }

    suspend fun changeBirthDateRequest(token: String, username: String?, birthDate: String): Response<VoidResponse> {
        val request = ChangeInfoRequest(null, birthDate)
        val response = infoService.changeInfo(token, request)

        return response
    }
}