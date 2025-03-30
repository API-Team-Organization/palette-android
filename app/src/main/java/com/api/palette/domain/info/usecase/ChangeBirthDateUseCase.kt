package com.api.palette.domain.info.usecase

import com.api.palette.data.info.request.ChangeInfoRequest
import com.api.palette.domain.info.InfoRepository
import javax.inject.Inject

class ChangeBirthDateUseCase @Inject constructor(
    private val infoRepository: InfoRepository
) {
    suspend operator fun invoke(token: String, request: ChangeInfoRequest) =
        infoRepository.changeBirthDate(token, request = request)
}
