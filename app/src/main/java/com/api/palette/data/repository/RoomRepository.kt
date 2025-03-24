package com.api.palette.data.repository

import com.api.palette.data.room.RoomService
import com.api.palette.data.room.RoomData
import com.api.palette.data.room.TitleData
import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import retrofit2.Response

class RoomRepository(private val roomService: RoomService) {

    suspend fun createRoom(token: String): Response<DataResponse<RoomData>> =
        roomService.createRoom(token)

    suspend fun getRoomList(token: String): DataResponse<List<RoomData>> =
        roomService.getRoomList(token)

    suspend fun deleteRoom(token: String, roomId: Int): Response<VoidResponse> =
        roomService.deleteRoom(token, roomId = roomId)

    suspend fun setRoomTitle(token: String, roomId: Int, title: TitleData): Response<VoidResponse> =
        roomService.setRoomTitle(token, title = title, roomId = roomId)

    suspend fun regenRoom(token: String, roomId: Int): Response<VoidResponse> =
        roomService.regenRoom(token, roomId = roomId)
}
