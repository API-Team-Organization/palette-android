package com.example.palette.data.chat

import com.example.palette.data.base.BaseResponse
import com.example.palette.data.base.BaseVoidResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatService {
    @POST("/chat")
    suspend fun chat(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Body postChat: ChatData
    ): Response<BaseVoidResponse>

    @GET("/chat/{roomId}")
    suspend fun getChatList(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Path("roomId") roomId: Int
    ): Response<BaseResponse<MutableList<ChatModel>>>
}