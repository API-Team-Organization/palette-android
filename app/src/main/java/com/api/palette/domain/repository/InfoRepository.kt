package com.api.palette.domain.repository

import com.api.palette.domain.model.*

interface InfoRepository {
    suspend fun profileInfo(token: String): ProfileData
    suspend fun changeName(token: String, username: String?): VoidResponse
    suspend fun changeBirthDate(token: String, birthDate: String): VoidResponse
}
