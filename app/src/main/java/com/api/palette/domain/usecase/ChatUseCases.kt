package com.api.palette.domain.usecase

import com.api.palette.domain.model.*
import com.api.palette.domain.repository.ChatRepository
import javax.inject.Inject

class ChatUseCases @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend fun createChat(token: String, chat: ChatAnswer, roomId: Int): VoidResponse = chatRepository.createChat(token, chat, roomId)
    suspend fun getChatList(token: String, roomId: Int, before: String? = null): List<MessageResponse> =
        chatRepository.getChatList(token, roomId, before)
    suspend fun getQnAList(token: String, roomId: Int): List<PromptData> = chatRepository.getQnAList(token, roomId)
    suspend fun getImageList(token: String, page: Int, size: Int): List<String> = chatRepository.getImageList(token, page, size)
}
