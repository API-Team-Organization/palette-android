package com.api.palette.data.info

import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.error.ErrorHandler
import com.api.palette.data.info.request.ChangeInfoRequest
import com.api.palette.domain.info.model.ProfileData
import com.api.palette.domain.info.InfoRepository
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InfoRepositoryImpl @Inject constructor(
    private val infoService: InfoService
) : InfoRepository {

    override suspend fun getProfileInfo(token: String): Response<DataResponse<ProfileData>> {
        val response = infoService.profileInfo(token)
        ErrorHandler.handleError(response)
        return response
    }

    override suspend fun changeName(token: String, request: ChangeInfoRequest): Response<VoidResponse> {
        val response = infoService.changeInfo(token, request)
        ErrorHandler.handleError(response)
        return response
    }

    override suspend fun changeBirthDate(token: String, request: ChangeInfoRequest): Response<VoidResponse> {
        val response = infoService.changeInfo(token, request)
        ErrorHandler.handleError(response)
        return response
    }
}
