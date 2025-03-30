package com.api.palette.domain.auth.usecase

import com.api.palette.domain.auth.AuthRepository
import javax.inject.Inject

class SessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(token: String) = authRepository.session(token)
}
