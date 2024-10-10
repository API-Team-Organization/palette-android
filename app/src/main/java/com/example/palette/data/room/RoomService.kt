package com.example.palette.data.room

import com.example.palette.data.base.BaseResponse
import com.example.palette.data.base.DataResponse
import com.example.palette.data.base.VoidResponse
import com.example.palette.data.room.data.RoomData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface RoomService {
    @POST("room")
    suspend fun createRoom(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
    ): Response<DataResponse<RoomData>>

    @GET("room/list")
    suspend fun getRoomList(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*"
    ): DataResponse<List<RoomData>>

    @DELETE("room/{roomId}")
    suspend fun deleteRoom(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Path("roomId") roomId: Int
    ): Response<VoidResponse>

    @PATCH("room/title")
    suspend fun setRoomTitle(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Body roomData: RoomData
    ): Response<VoidResponse>
}