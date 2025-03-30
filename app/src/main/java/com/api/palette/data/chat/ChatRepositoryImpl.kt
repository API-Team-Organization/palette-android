package com.api.palette.data.chat

import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.chat.data.ChatAnswer
import com.api.palette.data.chat.data.ImageListResponse
import com.api.palette.data.chat.data.PromptData
import com.api.palette.data.chat.data.QnABody
import com.api.palette.data.error.ErrorHandler
import com.api.palette.data.socket.data.MessageResponse
import com.api.palette.domain.chat.ChatRepository
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
        val response = chatService.chat(token, "*/*", roomId, QnABody(chat))
        if (!response.isSuccessful) ErrorHandler.handleError(response)
        return response
    }
    override suspend fun getChatList(
        token: String,
        roomId: String,
        before: String?,
        size: Int
    ): Response<DataResponse<MutableList<MessageResponse>>> {
        val response = chatService.getChatList(token, "*/*", roomId, before, size)
        if (!response.isSuccessful) ErrorHandler.handleError(response)
        return response
    }
    override suspend fun getQnAList(token: String, roomId: String): Response<DataResponse<List<PromptData>>> {
        val response = chatService.getQnAForRoom(token, "*/*", roomId)
        if (!response.isSuccessful) ErrorHandler.handleError(response)
        return response
    }
    override suspend fun getImageList(token: String, page: Int, size: Int): Response<DataResponse<ImageListResponse>> {
        val response = chatService.getImageList(token, "*/*", page, size)
        if (!response.isSuccessful) ErrorHandler.handleError(response)
        return response
    }
}
