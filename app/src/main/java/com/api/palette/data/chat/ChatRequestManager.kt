package com.api.palette.data.chat

import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.chat.data.ChatAnswer
import com.api.palette.data.chat.data.ImageListResponse
import com.api.palette.data.chat.data.PromptData
import com.api.palette.data.chat.data.QnABody
import com.api.palette.data.socket.data.MessageResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRequestManager @Inject constructor(
    private val chatService: ChatService
) {
    suspend fun createChat(
        token: String,
        chat: ChatAnswer,
        roomId: String
    ): Response<VoidResponse> =
        chatService.chat(token = token, roomId = roomId, data = QnABody(chat))

    suspend fun getChatList(
        token: String,
        roomId: String,
        before: String?,
        size: Int = 10
    ): Response<DataResponse<MutableList<MessageResponse>>> =
        chatService.getChatList(token = token, roomId = roomId, before = before, size = size)

    suspend fun getQnAList(
        token: String,
        roomId: String
    ): Response<DataResponse<List<PromptData>>> =
        chatService.getQnAForRoom(token = token, roomId = roomId)

    suspend fun getImageList(
        token: String,
        page: Int,
        size: Int
    ): Response<DataResponse<ImageListResponse>> =
        chatService.getImageList(token = token, page = page, size = size)
}
