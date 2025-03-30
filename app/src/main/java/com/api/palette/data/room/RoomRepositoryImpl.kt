package com.api.palette.data.room

import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.error.ErrorHandler
import com.api.palette.data.error.handleErrorReturn
import com.api.palette.domain.room.model.RoomData
import com.api.palette.data.room.data.TitleData
import com.api.palette.domain.room.RoomRepository
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepositoryImpl @Inject constructor(
    private val roomService: RoomService
) : RoomRepository {

    override suspend fun createRoom(token: String): Response<DataResponse<RoomData>> {
        val response = roomService.createRoom(token)
        ErrorHandler.handleError(response)
        return response
    }

    override suspend fun getRoomList(token: String): DataResponse<List<RoomData>> {
        val response = roomService.getRoomList(token)
        if (response.code >= 400) ErrorHandler.handleErrorReturn(response)
        return response
    }

    override suspend fun deleteRoom(token: String, roomId: String): Response<VoidResponse> {
        val response = roomService.deleteRoom(token, roomId = roomId)
        ErrorHandler.handleError(response)
        return response
    }

    override suspend fun setRoomTitle(token: String, title: TitleData, roomId: String): Response<VoidResponse> {
        val response = roomService.setRoomTitle(token, roomId = roomId, title = title)
        ErrorHandler.handleError(response)
        return response
    }

    override suspend fun regenRoom(token: String, roomId: String): Response<VoidResponse> {
        val response = roomService.regenRoom(token, roomId = roomId)
        ErrorHandler.handleError(response)
        return response
    }
}
