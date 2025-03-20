package com.api.palette.data.repository

import com.api.palette.domain.model.*
import com.api.palette.domain.repository.ChatRepository
import com.api.palette.data.ApiClient
import com.api.palette.data.chat.ChatService
import com.api.palette.data.base.ErrorHandler
import com.api.palette.data.model.QnABody
import retrofit2.HttpException

class ChatRepositoryImpl : ChatRepository {
    private val chatService: ChatService = ApiClient.retrofit.create(ChatService::class.java)

    override suspend fun createChat(token: String, chat: ChatAnswer, roomId: Int): VoidResponse {
        val response = chatService.chat(
            token = token,
            roomId = roomId.toString(),
            data = QnABody(roomId = roomId.toString(), data = chat)
        )
        ErrorHandler.handleError(response)
        return response.body() ?: throw HttpException(response)
    }

    override suspend fun getChatList(token: String, roomId: Int, before: String?): List<MessageResponse> {
        val beforeInt: Int? = before?.toIntOrNull()
        val response = chatService.getChatList(
            token = token,
            roomId = roomId.toString(),
            before = beforeInt,
            size = "10"
        )
        ErrorHandler.handleError(response)
        return response.body()?.data ?: throw HttpException(response)
    }

    override suspend fun getQnAList(token: String, roomId: Int): List<PromptData> {
        val response = chatService.getQnAForRoom(
            token = token,
            roomId = roomId.toString()
        )
        ErrorHandler.handleError(response)
        return response.body()?.data ?: throw HttpException(response)
    }

    override suspend fun getImageList(token: String, page: Int, size: Int): List<String> {
        val response = chatService.getImageList(
            token = token,
            page = page.toString(),
            size = size.toString()
        )
        ErrorHandler.handleError(response)
        return response.body()?.data?.images ?: throw HttpException(response)
    }
}
