package com.api.palette.domain.chat.usecase

import com.api.palette.domain.chat.ChatRepository
import javax.inject.Inject

class GetImageListUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        token: String,
        page: Int,
        size: Int
    ) = chatRepository.getImageList(token, page = page, size = size)
}
