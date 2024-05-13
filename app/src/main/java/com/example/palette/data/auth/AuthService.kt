package com.example.palette.data.auth

import com.example.palette.data.base.BaseVoidResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<BaseVoidResponse>
}