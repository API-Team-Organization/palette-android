package com.example.palette.data.room

import com.example.palette.data.base.BaseVoidResponse
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST

interface RoomService {
    @POST("/room")
    suspend fun createRoom(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*"
    ): Response<BaseVoidResponse>
}