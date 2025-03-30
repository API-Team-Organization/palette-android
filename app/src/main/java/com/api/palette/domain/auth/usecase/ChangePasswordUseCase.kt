package com.api.palette.domain.auth.usecase

import com.api.palette.data.auth.request.ChangePasswordRequest
import com.api.palette.domain.auth.AuthRepository
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(token: String, request: ChangePasswordRequest) =
        authRepository.changePassword(token, request)
}
