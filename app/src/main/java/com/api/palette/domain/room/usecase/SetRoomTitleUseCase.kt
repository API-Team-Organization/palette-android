package com.api.palette.domain.room.usecase

import com.api.palette.data.room.data.TitleData
import com.api.palette.domain.room.RoomRepository
import javax.inject.Inject

class SetRoomTitleUseCase @Inject constructor(
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(token: String, title: TitleData, roomId: String) =
        roomRepository.setRoomTitle(token, title, roomId)
}
