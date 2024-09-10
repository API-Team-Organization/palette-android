package com.example.palette.data.chat

import android.util.Log
import com.example.palette.common.Constant
import com.example.palette.data.ApiClient
import com.example.palette.data.base.BaseResponse
import retrofit2.Response

object ChatRequestManager {
    private val chatService: ChatService = ApiClient.retrofit.create(ChatService::class.java)

    suspend fun createChat(token: String, chat: ChatData, roomId: Int): Response<BaseResponse<PaletteChat>> {
        val response = chatService.chat(token = token, roomId = roomId, postChat = chat)
        Log.d(Constant.TAG, "ChatRequestManager createChat response is $response")
        return response
    }

    suspend fun getChatList(token: String, roomId: Int, page: Int): BaseResponse<MutableList<Received>>? {
        val response = chatService.getChatList(token = token, roomId = roomId, page = page, size = 10)
        Log.d(Constant.TAG, "getChatList is $response")
        if (!response.isSuccessful) {
            return BaseResponse(
                code = response.code(), // 실패 시의 상태 코드를 설정합니다
                message = response.message(), // 실패 시의 메시지를 설정합니다
                data = mutableListOf() // 빈 리스트를 반환합니다
            )
        }
        return response.body()
    }
}