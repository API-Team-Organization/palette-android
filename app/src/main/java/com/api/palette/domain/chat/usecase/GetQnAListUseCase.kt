package com.api.palette.domain.chat.usecase

import com.api.palette.domain.chat.ChatRepository
import javax.inject.Inject

class GetQnAListUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        token: String,
        roomId: String
    ) = chatRepository.getQnAList(token, roomId)
}
