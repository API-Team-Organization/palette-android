package com.example.palette.data.chat

import android.util.Log
import com.example.palette.common.Constant
import com.example.palette.common.json
import com.example.palette.data.ApiClient
import com.example.palette.data.base.BaseResponse
import com.example.palette.data.base.DataResponse
import com.example.palette.data.base.ErrorHandler
import com.example.palette.data.base.ErrorResponse
import com.example.palette.data.chat.qna.PromptData
import com.example.palette.data.socket.MessageResponse
import com.example.palette.ui.util.log
import com.example.palette.ui.util.logE
import kotlinx.datetime.Clock
import retrofit2.HttpException
import retrofit2.Response
import java.time.OffsetDateTime

object ChatRequestManager {
    private val chatService: ChatService = ApiClient.retrofit.create(ChatService::class.java)

    suspend fun createChat(token: String, chat: ChatData, roomId: Int): Response<DataResponse<PaletteChat>> {
        val response = chatService.chat(token = token, roomId = roomId, postChat = chat)
        Log.d(Constant.TAG, "ChatRequestManager createChat response is $response")
        return response
    }

    suspend fun getChatList(token: String, roomId: Int, before: String = Clock.System.now().toString()): DataResponse<MutableList<MessageResponse>>? {
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