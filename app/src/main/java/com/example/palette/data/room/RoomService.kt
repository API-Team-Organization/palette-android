package com.example.palette.data.room

import com.example.palette.data.base.BaseResponse
import com.example.palette.data.base.BaseVoidResponse
import com.example.palette.data.room.data.IdData
import com.example.palette.data.room.data.RoomData
import com.example.palette.data.room.data.TitleData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface RoomService {
    @POST("/room")
    suspend fun createRoom(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Body title: TitleData
    ): Response<BaseVoidResponse>

    // TODO: getRoomList token때문에 bottomNav 움직이면 예전 token써서 버그 발생. 해결ㄱㄱ
    @GET("/room/list")
    suspend fun getRoomList(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*"
    ): BaseResponse<List<RoomData>>

    @DELETE("/room/{roomId}")
    suspend fun deleteRoom(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Path("roomId") roomId: Int
    ): Response<BaseVoidResponse>
}