package com.api.palette.domain.usecase

import com.api.palette.domain.model.ProfileData
import com.api.palette.domain.model.VoidResponse
import com.api.palette.domain.repository.InfoRepository
import javax.inject.Inject

class InfoUseCases @Inject constructor(
    private val infoRepository: InfoRepository
) {
    suspend fun profileInfo(token: String): ProfileData = infoRepository.profileInfo(token)
    suspend fun changeName(token: String, username: String?): VoidResponse = infoRepository.changeName(token, username)
    suspend fun changeBirthDate(token: String, birthDate: String): VoidResponse = infoRepository.changeBirthDate(token, birthDate)
}
