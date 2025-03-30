package com.api.palette.domain.room

import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import com.api.palette.domain.room.model.RoomData
import com.api.palette.data.room.data.TitleData
import retrofit2.Response

interface RoomRepository {
    suspend fun createRoom(token: String): Response<DataResponse<RoomData>>
    suspend fun getRoomList(token: String): DataResponse<List<RoomData>>
    suspend fun deleteRoom(token: String, roomId: String): Response<VoidResponse>
    suspend fun setRoomTitle(token: String, title: TitleData, roomId: String): Response<VoidResponse>
    suspend fun regenRoom(token: String, roomId: String): Response<VoidResponse>
}
