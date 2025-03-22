package com.api.palette.data.info

import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import com.api.palette.application.PaletteApplication
import retrofit2.Response

object InfoRequestManager {
    private val infoRepository = PaletteApplication.appRepository.infoRepository

    suspend fun profileInfoRequest(token: String): DataResponse<ProfileData>? {
        val response = infoRepository.getProfileInfo(token)
        return response.body()
    }

    suspend fun changeNameRequest(token: String, username: String?): Response<VoidResponse> {
        val request = ChangeInfoRequest(username, null)
        return infoRepository.changeInfo(token, request)
    }

    suspend fun changeBirthDateRequest(token: String, birthDate: String): Response<VoidResponse> {
        val request = ChangeInfoRequest(null, birthDate)
        return infoRepository.changeInfo(token, request)
    }
}
