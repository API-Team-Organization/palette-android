package com.example.palette.data.auth

import com.example.palette.data.ApiClient
import com.example.palette.data.base.BaseVoidResponse
import retrofit2.HttpException
import retrofit2.Response

object RegisterRequestManager {
    private val authService: AuthService = ApiClient.retrofit.create(AuthService::class.java)

    suspend fun registerRequest(registerData: RegisterRequest): Response<BaseVoidResponse> {
        val response = authService.register(registerData)
        if (!response.isSuccessful)
            throw HttpException(response)

        return response
    }
}