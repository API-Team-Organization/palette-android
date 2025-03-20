package com.api.palette.domain.repository

import com.api.palette.domain.model.*

interface ChatRepository {
    suspend fun createChat(token: String, chat: ChatAnswer, roomId: Int): VoidResponse
    suspend fun getChatList(token: String, roomId: Int, before: String? = null): List<MessageResponse>
    suspend fun getQnAList(token: String, roomId: Int): List<PromptData>
    suspend fun getImageList(token: String, page: Int, size: Int): List<String>
}
