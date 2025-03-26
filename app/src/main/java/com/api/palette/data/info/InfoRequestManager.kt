package com.api.palette.data.info

import android.util.Log
import com.api.palette.common.Constant
import com.api.palette.data.ApiClient
import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.info.data.ChangeInfoRequest
import com.api.palette.data.info.data.ProfileData
import retrofit2.Response

object InfoRequestManager {

    private val infoService: InfoService = ApiClient.retrofit.create(InfoService::class.java)

    suspend fun profileInfoRequest(token: String): DataResponse<ProfileData>? {
        val response = infoService.profileInfo(token, "*/*")
        Log.d(Constant.TAG, "response is $response")
        return response.body()
    }

    suspend fun changeNameRequest(token: String, username: String?): Response<VoidResponse> {
        val request = ChangeInfoRequest(username = username, birthDate = null)
        return infoService.changeInfo(token, request)
    }

    suspend fun changeBirthDateRequest(token: String, birthDate: String): Response<VoidResponse> {
        val request = ChangeInfoRequest(username = null, birthDate = birthDate)
        return infoService.changeInfo(token, request)
    }
}
