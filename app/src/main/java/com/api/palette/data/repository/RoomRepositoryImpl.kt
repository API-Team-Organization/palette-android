package com.api.palette.data.repository

import com.api.palette.domain.model.RoomData
import com.api.palette.domain.model.TitleData
import com.api.palette.domain.model.VoidResponse
import com.api.palette.domain.repository.RoomRepository
import com.api.palette.data.ApiClient
import com.api.palette.data.room.RoomService
import retrofit2.HttpException

class RoomRepositoryImpl : RoomRepository {
    private val roomService: RoomService = ApiClient.retrofit.create(RoomService::class.java)

    override suspend fun createRoom(token: String): RoomData {
        val response = roomService.createRoom(token = token)
        if (!response.isSuccessful) throw HttpException(response)
        return response.body()?.data ?: throw HttpException(response)
    }

    override suspend fun getRoomList(token: String): List<RoomData> {
        val response = roomService.getRoomList(token = token)
        if (!response.isSuccessful) throw HttpException(response)
        return response.body()?.data ?: throw HttpException(response)
    }

    override suspend fun deleteRoom(token: String, roomId: Int): VoidResponse {
        val response = roomService.deleteRoom(
            token = token,
            accept = "*/*",
            roomId = roomId
        )
        if (!response.isSuccessful) throw HttpException(response)
        return response.body() ?: throw HttpException(response)
    }

    override suspend fun setRoomTitle(token: String, title: TitleData, roomId: Int): VoidResponse {
        val response = roomService.setRoomTitle(
            token = token,
            accept = "*/*",
            roomId = roomId,
            title = title
        )
        if (!response.isSuccessful) throw HttpException(response)
        return response.body() ?: throw HttpException(response)
    }

    override suspend fun regenRoom(token: String, roomId: Int): VoidResponse {
        val response = roomService.regenRoom(
            token = token,
            accept = "*/*",
            roomId = roomId
        )
        if (!response.isSuccessful) throw HttpException(response)
        return response.body() ?: throw HttpException(response)
    }
}
