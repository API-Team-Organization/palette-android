package com.api.palette.data.room

import com.api.palette.data.base.DataResponse
import com.api.palette.data.base.VoidResponse
import com.api.palette.data.room.data.RoomData
import com.api.palette.data.room.data.TitleData
import com.api.palette.application.PaletteApplication
import retrofit2.HttpException
import retrofit2.Response

object RoomRequestManager {
    private val roomRepository = PaletteApplication.appRepository.roomRepository

    suspend fun roomRequest(token: String): Response<DataResponse<RoomData>> {
        return roomRepository.createRoom(token)
    }

    suspend fun roomList(token: String): DataResponse<List<RoomData>> {
        return roomRepository.getRoomList(token)
    }

    suspend fun deleteRoom(token: String, id: Int): Response<VoidResponse> {
        val response = roomRepository.deleteRoom(token, id)
        if (!response.isSuccessful) throw HttpException(response)
        return response
    }

    suspend fun setRoomTitle(token: String, title: TitleData, roomId: Int): Response<VoidResponse> {
        return roomRepository.setRoomTitle(token, roomId, title)
    }

    suspend fun regenRoom(token: String, roomId: Int): Response<VoidResponse> {
        return roomRepository.regenRoom(token, roomId)
    }
}
