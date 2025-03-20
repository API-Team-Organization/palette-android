package com.api.palette.data.repository

import com.api.palette.domain.model.ProfileData
import com.api.palette.domain.model.VoidResponse
import com.api.palette.domain.repository.InfoRepository
import com.api.palette.data.ApiClient
import com.api.palette.data.info.InfoService
import retrofit2.HttpException

class InfoRepositoryImpl : InfoRepository {
    private val infoService: InfoService = ApiClient.retrofit.create(InfoService::class.java)

    override suspend fun profileInfo(token: String): ProfileData {
        val response = infoService.profileInfo(token)
        if (!response.isSuccessful) throw HttpException(response)
        return response.body()?.data ?: throw HttpException(response)
    }

    override suspend fun changeName(token: String, username: String?): VoidResponse {
        val request = com.api.palette.data.info.ChangeInfoRequest(username, null)
        val response = infoService.changeInfo(token, request)
        if (!response.isSuccessful) throw HttpException(response)
        return response.body() ?: throw HttpException(response)
    }

    override suspend fun changeBirthDate(token: String, birthDate: String): VoidResponse {
        val request = com.api.palette.data.info.ChangeInfoRequest(null, birthDate)
        val response = infoService.changeInfo(token, request)
        if (!response.isSuccessful) throw HttpException(response)
        return response.body() ?: throw HttpException(response)
    }
}
