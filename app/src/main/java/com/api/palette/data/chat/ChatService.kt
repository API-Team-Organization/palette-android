package com.api.palette.data.chat

import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.chat.data.ImageListResponse
import com.api.palette.data.chat.data.PromptData
import com.api.palette.data.chat.data.QnABody
import com.api.palette.data.socket.data.MessageResponse
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
        @Query("roomId") roomId: String,
        @Body data: QnABody
    ): Response<VoidResponse>

    @GET("chat/{roomId}")
    suspend fun getChatList(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Path("roomId") roomId: String,
        @Query("before") before: String?,
        @Query("size") size: Int
    ): Response<DataResponse<MutableList<MessageResponse>>>

    @GET("room/{roomId}/qna")
    suspend fun getQnAForRoom(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Path("roomId") roomId: String
    ): Response<DataResponse<List<PromptData>>>

    @GET("chat/my-image")
    suspend fun getImageList(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<DataResponse<ImageListResponse>>
}
