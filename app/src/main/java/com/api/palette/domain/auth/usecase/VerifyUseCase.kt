package com.api.palette.domain.auth.usecase

import com.api.palette.data.auth.request.VerifyRequest
import com.api.palette.domain.auth.AuthRepository
import javax.inject.Inject

class VerifyUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(token: String, verifyRequest: VerifyRequest) =
        authRepository.verify(token, verifyRequest = verifyRequest)
}
