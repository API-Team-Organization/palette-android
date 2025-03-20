package com.api.palette.domain.usecase

import com.api.palette.domain.model.RoomData
import com.api.palette.domain.model.TitleData
import com.api.palette.domain.model.VoidResponse
import com.api.palette.domain.repository.RoomRepository
import javax.inject.Inject

class RoomUseCases @Inject constructor(
    private val roomRepository: RoomRepository
) {
    suspend fun createRoom(token: String): RoomData = roomRepository.createRoom(token)
    suspend fun getRoomList(token: String): List<RoomData> = roomRepository.getRoomList(token)
    suspend fun deleteRoom(token: String, roomId: Int): VoidResponse = roomRepository.deleteRoom(token, roomId)
    suspend fun setRoomTitle(token: String, title: TitleData, roomId: Int): VoidResponse = roomRepository.setRoomTitle(token, title, roomId)
    suspend fun regenRoom(token: String, roomId: Int): VoidResponse = roomRepository.regenRoom(token, roomId)
}
