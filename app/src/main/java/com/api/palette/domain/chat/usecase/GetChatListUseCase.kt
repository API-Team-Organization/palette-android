package com.api.palette.domain.chat.usecase

import com.api.palette.domain.chat.ChatRepository
import javax.inject.Inject

class GetChatListUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        token: String,
        roomId: String,
        before: String?,
        size: Int
    ) = chatRepository.getChatList(token, roomId = roomId, before = before, size = size)
}
