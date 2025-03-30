package com.api.palette.domain.auth.usecase

import com.api.palette.data.auth.request.LoginRequest
import com.api.palette.domain.auth.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(loginRequest: LoginRequest) = authRepository.login(loginRequest)
}
