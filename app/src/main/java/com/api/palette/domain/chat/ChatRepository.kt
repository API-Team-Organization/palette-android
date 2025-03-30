package com.api.palette.domain.chat

import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import com.api.palette.domain.chat.model.ChatAnswer
import com.api.palette.data.chat.data.ImageListResponse
import com.api.palette.domain.chat.model.PromptData
import com.api.palette.domain.socket.model.MessageResponse
import retrofit2.Response

interface ChatRepository {
    suspend fun createChat(token: String, chat: ChatAnswer, roomId: String): Response<VoidResponse>
    suspend fun getChatList(token: String, roomId: String, before: String?, size: Int): Response<DataResponse<MutableList<MessageResponse>>>
    suspend fun getQnAList(token: String, roomId: String): Response<DataResponse<List<PromptData>>>
    suspend fun getImageList(token: String, page: Int, size: Int): Response<DataResponse<ImageListResponse>>
}
