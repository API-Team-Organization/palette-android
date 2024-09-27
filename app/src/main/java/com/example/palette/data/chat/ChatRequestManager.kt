package com.example.palette.data.chat

import android.util.Log
import com.example.palette.common.Constant
import com.example.palette.data.ApiClient
import com.example.palette.data.base.BaseResponse
import com.example.palette.data.socket.MessageResponse
import com.example.palette.ui.util.log
import retrofit2.Response
import java.time.OffsetDateTime

object ChatRequestManager {
    private val chatService: ChatService = ApiClient.retrofit.create(ChatService::class.java)

    suspend fun createChat(token: String, chat: ChatData, roomId: Int): Response<BaseResponse<PaletteChat>> {
        val response = chatService.chat(token = token, roomId = roomId, postChat = chat)
        Log.d(Constant.TAG, "ChatRequestManager createChat response is $response")
        return response
    }

    suspend fun getChatList(token: String, roomId: Int, before: String = OffsetDateTime.now().toString()): BaseResponse<MutableList<MessageResponse>>? {
        val response = chatService.getChatList(token = token, roomId = roomId, before = before, size = 10)
        Log.d(Constant.TAG, "getChatList response: $response")

        return if (response.isSuccessful) {
            response.body()
        } else {
            Log.e(Constant.TAG, "Failed to get chat list: ${response.message()} (code: ${response.code()})")
            BaseResponse(
                code = response.code(), // 실패 시의 상태 코드를 설정합니다
                message = response.message(), // 실패 시의 메시지를 설정합니다
                data = mutableListOf() // 빈 리스트를 반환합니다
            )
        }
    }

    suspend fun getImageList(token: String, page: Int, size: Int, sort: List<String>): BaseResponse<List<String>>? {
        val response = chatService.getImageList(token = token, page = page, size = size, sort = sort)

        return if (response.isSuccessful) {
            response.body()
        } else {
            log("코드: ${response.code()}, 메시지: ${response.message()}")
            BaseResponse(
                code = response.code(),
                message = response.message(),
                data = mutableListOf()
            )
        }
    }
}