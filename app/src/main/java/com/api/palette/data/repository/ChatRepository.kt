package com.api.palette.data.repository

import com.api.palette.data.chat.ChatService
import com.api.palette.data.chat.qna.QnABody
import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.chat.ImageListResponse
import com.api.palette.data.socket.MessageResponse
import com.api.palette.data.chat.qna.PromptData
import retrofit2.Response

class ChatRepository(private val chatService: ChatService) {

    suspend fun sendChat(token: String, roomId: Int, body: QnABody): Response<VoidResponse> =
        chatService.chat(token, roomId = roomId, data = body)

    suspend fun getChatList(token: String, roomId: Int, before: String?, size: Int): Response<DataResponse<MutableList<MessageResponse>>> =
        chatService.getChatList(token, roomId = roomId, before = before, size = size)

    suspend fun getQnAList(token: String, roomId: Int): Response<DataResponse<List<PromptData>>> =
        chatService.getQnAForRoom(token, roomId = roomId)

    suspend fun getImageList(token: String, page: Int, size: Int): Response<DataResponse<ImageListResponse>> =
        chatService.getImageList(token, page = page, size = size)
}
