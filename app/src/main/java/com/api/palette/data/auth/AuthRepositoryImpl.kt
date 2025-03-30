package com.api.palette.data.auth

import com.api.palette.data.auth.request.ChangePasswordRequest
import com.api.palette.data.auth.request.LoginRequest
import com.api.palette.data.auth.request.RegisterRequest
import com.api.palette.data.auth.request.VerifyRequest
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.error.ErrorHandler
import com.api.palette.domain.auth.AuthRepository
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService
) : AuthRepository {
    override suspend fun login(loginRequest: LoginRequest): Response<VoidResponse> {
        val response = authService.login(loginRequest)
        ErrorHandler.handleError(response)
        return response
    }
    override suspend fun logout(token: String): Response<VoidResponse> {
        val response = authService.logout(token)
        ErrorHandler.handleError(response)
        return response
    }
    override suspend fun session(token: String): Response<VoidResponse> {
        val response = authService.session(token)
        if (!response.isSuccessful) ErrorHandler.handleError(response)
        return response
    }
    override suspend fun register(registerRequest: RegisterRequest): Response<VoidResponse> {
        val response = authService.register(registerRequest)
        ErrorHandler.handleError(response)
        return response
    }
    override suspend fun verify(token: String, verifyRequest: VerifyRequest): Response<VoidResponse> {
        val response = authService.verify(token, verifyRequest)
        ErrorHandler.handleError(response)
        return response
    }
    override suspend fun resend(token: String): Response<VoidResponse> {
        val response = authService.resend(token)
        if (!response.isSuccessful) ErrorHandler.handleError(response)
        return response
    }
    override suspend fun resign(token: String): Response<VoidResponse> {
        val response = authService.resign(token)
        if (!response.isSuccessful) ErrorHandler.handleError(response)
        return response
    }
    override suspend fun changePassword(token: String, request: ChangePasswordRequest): Response<VoidResponse> {
        val response = authService.changePassword(token, request)
        if (response.code() >= 500) ErrorHandler.handleError(response)
        return response
    }
}
