package com.api.palette.domain.auth

import com.api.palette.data.auth.request.*
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.error.CustomException
import retrofit2.Response

interface AuthRepository {

    @Throws(CustomException::class)
    suspend fun login(loginRequest: LoginRequest): Response<VoidResponse>

    @Throws(CustomException::class)
    suspend fun logout(token: String): Response<VoidResponse>

    @Throws(CustomException::class)
    suspend fun session(token: String): Response<VoidResponse>

    @Throws(CustomException::class)
    suspend fun register(registerRequest: RegisterRequest): Response<VoidResponse>

    @Throws(CustomException::class)
    suspend fun verify(token: String, verifyRequest: VerifyRequest): Response<VoidResponse>

    @Throws(CustomException::class)
    suspend fun resend(token: String): Response<VoidResponse>

    @Throws(CustomException::class)
    suspend fun resign(token: String): Response<VoidResponse>

    @Throws(CustomException::class)
    suspend fun changePassword(token: String, request: ChangePasswordRequest): Response<VoidResponse>
}
