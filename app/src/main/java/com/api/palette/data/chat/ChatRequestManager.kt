package com.api.palette.data.chat

import android.util.Log
import com.api.palette.common.Constant
import com.api.palette.data.ApiClient
import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.ErrorHandler
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.chat.qna.ChatAnswer
import com.api.palette.data.chat.qna.PromptData
import com.api.palette.data.chat.qna.QnABody
import com.api.palette.data.socket.MessageResponse
import kotlinx.datetime.Clock
import retrofit2.Response

object ChatRequestManager {
    private val chatService: ChatService = ApiClient.retrofit.create(ChatService::class.java)

    suspend fun createChat(token: String, chat: ChatAnswer, roomId: Int): Response<VoidResponse> {
        val response = chatService.chat(token = token, roomId = roomId, data = QnABody(chat))
        Log.d(Constant.TAG, "ChatRequestManager createChat response is $response")
        return response
    }

    suspend fun getChatList(token: String, roomId: Int, before: String? = Clock.System.now().toString()): DataResponse<MutableList<MessageResponse>>? {
        val response = chatService.getChatList(token = token, roomId = roomId, before = before, size = 10)
        Log.d(Constant.TAG, "getChatList response: $response")

        ErrorHandler.handleError(response)
        return response.body()
    }

    suspend fun getQnAList(token: String, roomId: Int): DataResponse<List<PromptData>>? {
        val response = chatService.getQnAForRoom(token = token, roomId = roomId)
        Log.d(Constant.TAG, "getChatList response: $response")

        ErrorHandler.handleError(response)
        return response.body()
    }

    suspend fun getImageList(token: String, page: Int, size: Int): DataResponse<ImageListResponse>? {
        val response = chatService.getImageList(token = token, page = page, size = size)
        ErrorHandler.handleError(response)
        return response.body()
    }
}