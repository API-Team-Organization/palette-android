package com.api.palette.domain.info.usecase

import com.api.palette.data.info.data.ChangeInfoRequest
import com.api.palette.domain.info.InfoRepository
import javax.inject.Inject

class ChangeNameUseCase @Inject constructor(
    private val infoRepository: InfoRepository
) {
    suspend operator fun invoke(token: String, request: ChangeInfoRequest) =
        infoRepository.changeName(token, request)
}
