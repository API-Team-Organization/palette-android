package com.api.palette.data.auth

import android.util.Log
import com.api.palette.common.Constant
import com.api.palette.data.ApiClient
import com.api.palette.data.auth.request.ChangePasswordRequest
import com.api.palette.data.auth.request.LoginRequest
import com.api.palette.data.auth.request.RegisterRequest
import com.api.palette.data.auth.request.VerifyRequest
import com.api.palette.data.error.ErrorHandler
import com.api.palette.data.base.VoidResponse
import retrofit2.HttpException
import retrofit2.Response

object AuthRequestManager {
    private val authService: AuthService =
        ApiClient.retrofit.create(AuthService::class.java)

    suspend fun loginRequest(loginData: LoginRequest): Response<VoidResponse> {
        val response = authService.login(loginData)
        ErrorHandler.handleError(response)
        return response
    }

    suspend fun logoutRequest(token: String): Response<VoidResponse> {
        val response = authService.logout(token)
        ErrorHandler.handleError(response)
        return response
    }

    suspend fun sessionRequest(token: String): Response<VoidResponse> {
        return authService.session(token)
    }

    suspend fun registerRequest(registerData: RegisterRequest): Response<VoidResponse> {
        val response = authService.register(registerData)
        ErrorHandler.handleError(response)
        return response
    }

    suspend fun verifyRequest(token: String, verifyData: VerifyRequest): Response<VoidResponse> {
        val response = authService.verify(token, verifyData)
        Log.d(Constant.TAG, "verifyRequest: ${response.headers()}")
        ErrorHandler.handleError(response)
        return response
    }

    suspend fun resendRequest(token: String): Response<VoidResponse> {
        return authService.resend(token)
    }

    suspend fun resignRequest(token: String): Response<VoidResponse> {
        return authService.resign(token)
    }

    suspend fun changePasswordRequest(
        token: String,
        beforePassword: String,
        afterPassword: String
    ): Response<VoidResponse> {
        val request = ChangePasswordRequest(beforePassword, afterPassword)
        val response = authService.changePassword(token, request)
        if (response.code() >= 500) throw HttpException(response)
        return response
    }
}
