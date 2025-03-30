package com.api.palette.domain.chat.usecase

import com.api.palette.domain.chat.model.ChatAnswer
import com.api.palette.domain.chat.ChatRepository
import javax.inject.Inject

class CreateChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(token: String, chat: ChatAnswer, roomId: String) =
        chatRepository.createChat(token, chat, roomId)
}
