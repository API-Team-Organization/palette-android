package com.api.palette.data.auth

import com.api.palette.data.base.VoidResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<VoidResponse>

    @POST("auth/logout")
    suspend fun logout(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*"
    ): Response<VoidResponse>

    @GET("auth/session")
    suspend fun session(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*"
    ): Response<VoidResponse>

    @POST("auth/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): Response<VoidResponse>

    @POST("auth/verify")
    suspend fun verify(
        @Header("X-AUTH-Token") token: String,
        @Body verifyRequest: VerifyRequest
    ): Response<VoidResponse>

    @POST("auth/resend")
    suspend fun resend(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*"
    ): Response<VoidResponse>

    @DELETE("auth/resign")
    suspend fun resign(
        @Header("X-AUTH-Token") token: String
    ): Response<VoidResponse>

    @PATCH("auth/password")
    suspend fun changePassword(
        @Header("X-AUTH-Token") token: String,
        @Body request: ChangePasswordRequest
    ): Response<VoidResponse>
}