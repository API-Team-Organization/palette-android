package com.api.palette.data.room

import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.room.data.RoomData
import com.api.palette.data.room.data.TitleData
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepository @Inject constructor(
    private val roomService: RoomService
) {

    suspend fun createRoom(token: String): Response<DataResponse<RoomData>> =
        roomService.createRoom(token = token)

    suspend fun getRoomList(token: String): DataResponse<List<RoomData>> =
        roomService.getRoomList(token)

    suspend fun deleteRoom(token: String, roomId: String): Response<VoidResponse> =
        roomService.deleteRoom(token = token, roomId = roomId)

    suspend fun setRoomTitle(token: String, title: TitleData, roomId: String): Response<VoidResponse> =
        roomService.setRoomTitle(token = token, title = title, roomId = roomId)

    suspend fun regenRoom(token: String, roomId: String): Response<VoidResponse> =
        roomService.regenRoom(token = token, roomId = roomId)
}
