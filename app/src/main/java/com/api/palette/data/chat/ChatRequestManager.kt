package com.api.palette.data.chat

import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.chat.qna.ChatAnswer
import com.api.palette.data.chat.qna.QnABody
import com.api.palette.data.socket.MessageResponse
import com.api.palette.application.PaletteApplication
import com.api.palette.data.chat.qna.PromptData
import retrofit2.Response

object ChatRequestManager {
    private val chatRepository = PaletteApplication.appRepository.chatRepository

    suspend fun createChat(token: String, chat: ChatAnswer, roomId: Int): Response<VoidResponse> {
        return chatRepository.sendChat(token, roomId, QnABody(chat))
    }

    suspend fun getChatList(token: String, roomId: Int, before: String? = null): DataResponse<MutableList<MessageResponse>>? {
        val response = chatRepository.getChatList(token, roomId, before, 10)
        return response.body()
    }

    suspend fun getQnAList(token: String, roomId: Int): DataResponse<List<PromptData>>? {
        val response = chatRepository.getQnAList(token, roomId)
        return response.body()
    }

    suspend fun getImageList(token: String, page: Int, size: Int): DataResponse<ImageListResponse>? {
        val response = chatRepository.getImageList(token, page, size)
        return response.body()
    }
}
