package com.api.palette.data.chat

import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.chat.data.*
import com.api.palette.data.error.ErrorHandler
import com.api.palette.domain.socket.model.MessageResponse
import com.api.palette.domain.chat.ChatRepository
import com.api.palette.domain.chat.model.ChatAnswer
import com.api.palette.domain.chat.model.PromptData
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatService: ChatService
) : ChatRepository {

    override suspend fun createChat(
        token: String,
        chat: ChatAnswer,
        roomId: String
    ): Response<VoidResponse> {
        val response = chatService.chat(token, roomId = roomId, data = QnABody(chat))
        ErrorHandler.handleError(response)
        return response
    }

    override suspend fun getChatList(
        token: String,
        roomId: String,
        before: String?,
        size: Int
    ): Response<DataResponse<MutableList<MessageResponse>>> {
        val response = chatService.getChatList(token, roomId = roomId, before = before, size = size)
        ErrorHandler.handleError(response)
        return response
    }

    override suspend fun getQnAList(
        token: String,
        roomId: String
    ): Response<DataResponse<List<PromptData>>> {
        val response = chatService.getQnAForRoom(token, roomId = roomId)
        ErrorHandler.handleError(response)
        return response
    }

    override suspend fun getImageList(
        token: String,
        page: Int,
        size: Int
    ): Response<DataResponse<ImageListResponse>> {
        val response = chatService.getImageList(token, page = page, size = size)
        ErrorHandler.handleError(response)
        return response
    }
}
