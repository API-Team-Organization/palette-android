package com.example.palette.data.auth

import android.util.Log
import com.example.palette.common.Constant
import com.example.palette.data.ApiClient
import com.example.palette.data.base.BaseVoidResponse
import retrofit2.HttpException
import retrofit2.Response

object AuthRequestManager {
    private val authService: AuthService = ApiClient.retrofit.create(AuthService::class.java)

    suspend fun registerRequest(registerData: RegisterRequest): Response<BaseVoidResponse> {
        val response = authService.register(registerData)
        if (!response.isSuccessful)
            throw HttpException(response)

        return response
    }

    suspend fun loginRequest(loginData: LoginRequest): Response<BaseVoidResponse> {
        val response = authService.login(loginData)
        if (!response.isSuccessful)
            throw HttpException(response)

        return response
    }

    suspend fun logoutRequest(token: String): Response<BaseVoidResponse> {
        val response = authService.logout(token)
        if (!response.isSuccessful)
            throw HttpException(response)

        return response
    }

    suspend fun sessionRequest(token: String): Response<BaseVoidResponse> {
        val response = authService.session(token)

        return response
    }

    suspend fun verifyRequest(token: String, verifyData: VerifyRequest): Response<BaseVoidResponse> {
        val response = authService.verify(token, verifyData)
        Log.d(Constant.TAG, "verifyRequest: ${response.headers()}")
        if (response.code() >= 500)
            throw HttpException(response)

        return response
    }

    suspend fun resendRequest(token: String): Response<BaseVoidResponse> {
        val response = authService.resend(token)

        return response
    }

    suspend fun resignRequest(token: String): Response<BaseVoidResponse> {
        return authService.resign(token)
    }

    suspend fun changePasswordRequest(token: String, beforePassword: String, afterPassword: String): Response<BaseVoidResponse> {
        val request = ChangePasswordRequest(beforePassword, afterPassword)
        val response = authService.changePassword(
            token = token,
            request = request
        )

        if (response.code() >= 500)
            throw HttpException(response)

        return response
    }
}