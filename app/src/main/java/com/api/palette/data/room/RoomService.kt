package com.api.palette.data.room

import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.room.data.RoomData
import com.api.palette.data.room.data.TitleData
import retrofit2.Response
import retrofit2.http.*

interface RoomService {

    @POST("room")
    suspend fun createRoom(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*"
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
        @Path("roomId") roomId: String
    ): Response<VoidResponse>

    @PATCH("room/{roomId}/title")
    suspend fun setRoomTitle(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Path("roomId") roomId: String,
        @Body title: TitleData
    ): Response<VoidResponse>

    @POST("room/{roomId}/regen")
    suspend fun regenRoom(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Path("roomId") roomId: String
    ): Response<VoidResponse>
}
