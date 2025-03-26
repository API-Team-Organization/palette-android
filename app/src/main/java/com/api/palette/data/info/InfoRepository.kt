package com.api.palette.data.info

import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.info.data.ChangeInfoRequest
import com.api.palette.data.info.data.ProfileData
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InfoRepository @Inject constructor(
    private val infoService: InfoService
) {

    suspend fun getProfileInfo(token: String): Response<DataResponse<ProfileData>> =
        infoService.profileInfo(token, "*/*")

    suspend fun changeName(token: String, username: String?): Response<VoidResponse> {
        val request = ChangeInfoRequest(username = username, birthDate = null)
        return infoService.changeInfo(token, request)
    }

    suspend fun changeBirthDate(token: String, birthDate: String): Response<VoidResponse> {
        val request = ChangeInfoRequest(username = null, birthDate = birthDate)
        return infoService.changeInfo(token, request)
    }
}
