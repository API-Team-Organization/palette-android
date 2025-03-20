package com.api.palette.domain.repository

import com.api.palette.domain.model.*

interface AuthRepository {
    suspend fun register(request: RegisterRequest): VoidResponse
    suspend fun login(request: LoginRequest): Pair<VoidResponse, String?>  // 토큰 반환 포함
    suspend fun logout(token: String): VoidResponse
    suspend fun session(token: String): VoidResponse
    suspend fun verify(token: String, request: VerifyRequest): VoidResponse
    suspend fun resend(token: String): VoidResponse
    suspend fun resign(token: String): VoidResponse
    suspend fun changePassword(token: String, beforePassword: String, afterPassword: String): VoidResponse
}
