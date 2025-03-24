package com.api.palette.data.auth

import com.api.palette.data.base.ErrorHandler
import com.api.palette.data.base.VoidResponse
import com.api.palette.application.PaletteApplication
import retrofit2.HttpException
import retrofit2.Response

object AuthRequestManager {
    private val authRepository = PaletteApplication.appRepository.authRepository

    suspend fun loginRequest(loginData: LoginRequest): Response<VoidResponse> {
        val response = authRepository.login(loginData)
        ErrorHandler.handleError(response)
        return response
    }

    suspend fun logoutRequest(token: String): Response<VoidResponse> {
        val response = authRepository.logout(token)
        ErrorHandler.handleError(response)
        return response
    }

    suspend fun sessionRequest(token: String): Response<VoidResponse> {
        return authRepository.session(token)
    }

    suspend fun registerRequest(registerData: RegisterRequest): Response<VoidResponse> {
        val response = authRepository.register(registerData)
        ErrorHandler.handleError(response)
        return response
    }

    suspend fun verifyRequest(token: String, verifyData: VerifyRequest): Response<VoidResponse> {
        val response = authRepository.verify(token, verifyData)
        ErrorHandler.handleError(response)
        return response
    }

    suspend fun resendRequest(token: String): Response<VoidResponse> {
        return authRepository.resend(token)
    }

    suspend fun resignRequest(token: String): Response<VoidResponse> {
        return authRepository.resign(token)
    }

    suspend fun changePasswordRequest(token: String, beforePassword: String, afterPassword: String): Response<VoidResponse> {
        val response = authRepository.changePassword(token, beforePassword, afterPassword)
        if (response.code() >= 500)
            throw HttpException(response)
        return response
    }
}
