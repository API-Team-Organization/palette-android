package com.api.palette.data.repository

import com.api.palette.data.info.ChangeInfoRequest
import com.api.palette.data.info.InfoService
import com.api.palette.data.info.ProfileData
import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import retrofit2.Response

class InfoRepository(private val infoService: InfoService) {
    suspend fun getProfileInfo(token: String): Response<DataResponse<ProfileData>> =
        infoService.profileInfo(token)

    suspend fun changeInfo(token: String, request: ChangeInfoRequest): Response<VoidResponse> =
        infoService.changeInfo(token, request)
}
