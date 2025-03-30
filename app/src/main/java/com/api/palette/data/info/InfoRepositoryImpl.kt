package com.api.palette.data.info

import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.error.ErrorHandler
import com.api.palette.data.info.data.ChangeInfoRequest
import com.api.palette.data.info.data.ProfileData
import com.api.palette.domain.info.InfoRepository
import com.api.palette.presentation.util.log
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InfoRepositoryImpl @Inject constructor(
    private val infoService: InfoService
) : InfoRepository {
    override suspend fun getProfileInfo(token: String): Response<DataResponse<ProfileData>> {
        val response = infoService.profileInfo(token)
        if (!response.isSuccessful) ErrorHandler.handleError(response)
        return response
    }
    override suspend fun changeName(token: String, request: ChangeInfoRequest): Response<VoidResponse> {
        val response = infoService.changeInfo(token, request)
        if (!response.isSuccessful) ErrorHandler.handleError(response)
        return response
    }
    override suspend fun changeBirthDate(token: String, request: ChangeInfoRequest): Response<VoidResponse> {
        val response = infoService.changeInfo(token, request)
        if (!response.isSuccessful) ErrorHandler.handleError(response)
        return response
    }
}
