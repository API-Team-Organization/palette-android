package com.api.palette.domain.auth.usecase

import com.api.palette.data.auth.request.RegisterRequest
import com.api.palette.domain.auth.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: RegisterRequest) = authRepository.register(request)
}
