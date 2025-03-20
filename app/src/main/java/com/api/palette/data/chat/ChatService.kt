package com.api.palette.data.chat

import com.api.palette.domain.model.VoidResponse
import com.api.palette.domain.model.MessageResponse
import com.api.palette.domain.model.DataResponse
import com.api.palette.domain.model.PromptData
import com.api.palette.data.model.QnABody
import com.api.palette.domain.model.ImageListResponse
import retrofit2.Response
import retrofit2.http.*

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
        @Query("before") before: Int?,
        @Query("size") size: String
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
        @Query("page") page: String,
        @Query("size") size: String
    ): Response<DataResponse<ImageListResponse>>
}
