package com.api.palette.domain.repository

import com.api.palette.domain.model.*

interface RoomRepository {
    suspend fun createRoom(token: String): RoomData
    suspend fun getRoomList(token: String): List<RoomData>
    suspend fun deleteRoom(token: String, roomId: Int): VoidResponse
    suspend fun setRoomTitle(token: String, title: TitleData, roomId: Int): VoidResponse
    suspend fun regenRoom(token: String, roomId: Int): VoidResponse
}
