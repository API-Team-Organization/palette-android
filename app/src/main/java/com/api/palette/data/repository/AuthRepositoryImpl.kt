package com.api.palette.data.repository

import com.api.palette.domain.model.*
import com.api.palette.domain.repository.AuthRepository
import com.api.palette.data.ApiClient
import com.api.palette.data.auth.AuthService
import com.api.palette.data.base.ErrorHandler
import retrofit2.HttpException

class AuthRepositoryImpl : AuthRepository {
    private val authService: AuthService = ApiClient.retrofit.create(AuthService::class.java)

    override suspend fun register(request: RegisterRequest): VoidResponse {
        val response = authService.register(request)
        ErrorHandler.handleError(response)
        return response.body() ?: throw HttpException(response)
    }

    override suspend fun login(request: LoginRequest): Pair<VoidResponse, String?> {
        val response = authService.login(request)
        ErrorHandler.handleError(response)
        val token = response.headers()[com.api.palette.common.HeaderUtil.X_AUTH_TOKEN]
        return Pair(response.body() ?: throw HttpException(response), token)
    }

    override suspend fun logout(token: String): VoidResponse {
        val response = authService.logout(token)
        ErrorHandler.handleError(response)
        return response.body() ?: throw HttpException(response)
    }

    override suspend fun session(token: String): VoidResponse {
        val response = authService.session(token)
        ErrorHandler.handleError(response)
        return response.body() ?: throw HttpException(response)
    }

    override suspend fun verify(token: String, request: VerifyRequest): VoidResponse {
        val response = authService.verify(token, request)
        ErrorHandler.handleError(response)
        return response.body() ?: throw HttpException(response)
    }

    override suspend fun resend(token: String): VoidResponse {
        val response = authService.resend(token)
        ErrorHandler.handleError(response)
        return response.body() ?: throw HttpException(response)
    }

    override suspend fun resign(token: String): VoidResponse {
        val response = authService.resign(token)
        ErrorHandler.handleError(response)
        return response.body() ?: throw HttpException(response)
    }

    override suspend fun changePassword(token: String, beforePassword: String, afterPassword: String): VoidResponse {
        val request = ChangePasswordRequest(beforePassword, afterPassword)
        val response = authService.changePassword(token, request)
        if (response.code() >= 500) throw HttpException(response)
        return response.body() ?: throw HttpException(response)
    }
}
