package com.example.palette.data.chat

import com.example.palette.data.base.DataResponse
import com.example.palette.data.chat.qna.PromptData
import com.example.palette.data.socket.MessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatService {
    @POST("chat")
    suspend fun chat(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Query("roomId") roomId: Int,
        @Body postChat: ChatData
    ): Response<DataResponse<PaletteChat>>

    @GET("chat/{roomId}")
    suspend fun getChatList(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Path("roomId") roomId: Int,
        @Query("before") before: String,
        @Query("size") size: Int
    ): Response<DataResponse<MutableList<MessageResponse>>>

    // 아무튼 Chat 임. ^^7
    @GET("room/{roomId}/qna")
    suspend fun getQnAForRoom(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Path("roomId") roomId: Int,
    ): Response<DataResponse<List<PromptData>>>

    @GET("chat/my-image")
    suspend fun getImageList(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<DataResponse<ImageListResponse>>
}