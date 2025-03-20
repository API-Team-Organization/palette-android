package com.api.palette.data.room

import com.api.palette.domain.model.DataResponse
import com.api.palette.domain.model.VoidResponse
import com.api.palette.domain.model.RoomData
import com.api.palette.domain.model.TitleData
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
    ): Response<DataResponse<List<RoomData>>>

    @DELETE("room/{roomId}")
    suspend fun deleteRoom(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Path("roomId") roomId: Int
    ): Response<VoidResponse>

    @PATCH("room/{roomId}/title")
    suspend fun setRoomTitle(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Path("roomId") roomId: Int,
        @Body title: TitleData
    ): Response<VoidResponse>

    @POST("room/{roomId}/regen")
    suspend fun regenRoom(
        @Header("X-AUTH-Token") token: String,
        @Header("Accept") accept: String = "*/*",
        @Path("roomId") roomId: Int
    ): Response<VoidResponse>
}
