package com.api.palette.domain.room.usecase

import com.api.palette.domain.room.RoomRepository
import javax.inject.Inject

class GetRoomListUseCase @Inject constructor(
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(token: String) = roomRepository.getRoomList(token = token)
}
