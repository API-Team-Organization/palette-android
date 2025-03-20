package com.api.palette.domain.usecase

import com.api.palette.domain.model.*
import com.api.palette.domain.repository.AuthRepository
import javax.inject.Inject

class AuthUseCases @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun register(request: RegisterRequest): VoidResponse = authRepository.register(request)
    suspend fun login(request: LoginRequest): Pair<VoidResponse, String?> = authRepository.login(request)
    suspend fun logout(token: String): VoidResponse = authRepository.logout(token)
    suspend fun session(token: String): VoidResponse = authRepository.session(token)
    suspend fun verify(token: String, request: VerifyRequest): VoidResponse = authRepository.verify(token, request)
    suspend fun resend(token: String): VoidResponse = authRepository.resend(token)
    suspend fun resign(token: String): VoidResponse = authRepository.resign(token)
    suspend fun changePassword(token: String, beforePassword: String, afterPassword: String): VoidResponse =
        authRepository.changePassword(token, beforePassword, afterPassword)
}
