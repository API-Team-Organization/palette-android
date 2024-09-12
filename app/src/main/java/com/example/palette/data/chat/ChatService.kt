package com.example.palette.data.chat

import com.example.palette.data.base.BaseResponse
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
    ): Response<BaseResponse<PaletteChat>>

    @GET("chat/{roomId}")
    suspend fun getChatList(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Path("roomId") roomId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<BaseResponse<MutableList<Received>>>

    @GET("chat/my-image")
    suspend fun getImageList(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>
    ): Response<GetImageListResponse>
}