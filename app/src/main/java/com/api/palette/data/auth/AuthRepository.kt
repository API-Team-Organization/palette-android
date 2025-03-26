package com.api.palette.data.auth

import com.api.palette.data.auth.request.ChangePasswordRequest
import com.api.palette.data.auth.request.LoginRequest
import com.api.palette.data.auth.request.RegisterRequest
import com.api.palette.data.auth.request.VerifyRequest
import com.api.palette.data.base.VoidResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authService: AuthService
) {
    suspend fun login(loginRequest: LoginRequest): Response<VoidResponse> =
        authService.login(loginRequest)

    suspend fun logout(token: String): Response<VoidResponse> =
        authService.logout(token)

    suspend fun session(token: String): Response<VoidResponse> =
        authService.session(token)

    suspend fun register(registerRequest: RegisterRequest): Response<VoidResponse> =
        authService.register(registerRequest)

    suspend fun verify(token: String, verifyRequest: VerifyRequest): Response<VoidResponse> =
        authService.verify(token, verifyRequest)

    suspend fun resend(token: String): Response<VoidResponse> =
        authService.resend(token)

    suspend fun resign(token: String): Response<VoidResponse> =
        authService.resign(token)

    suspend fun changePassword(
        token: String,
        beforePassword: String,
        afterPassword: String
    ): Response<VoidResponse> {
        val request = ChangePasswordRequest(beforePassword, afterPassword)
        return authService.changePassword(token, request)
    }
}
