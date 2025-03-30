package com.api.palette.domain.info.usecase

import com.api.palette.domain.info.InfoRepository
import javax.inject.Inject

class GetProfileInfoUseCase @Inject constructor(
    private val infoRepository: InfoRepository
) {
    suspend operator fun invoke(token: String) = infoRepository.getProfileInfo(token)
}
