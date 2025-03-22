package com.api.palette.data.repository

import retrofit2.Retrofit
import com.api.palette.data.auth.AuthService
import com.api.palette.data.info.InfoService
import com.api.palette.data.chat.ChatService
import com.api.palette.data.room.RoomService

class AppRepository(
    val authRepository: AuthRepository,
    val infoRepository: InfoRepository,
    val chatRepository: ChatRepository,
    val roomRepository: RoomRepository
)

object RepositoryProvider {
    fun provideAppRepository(retrofit: Retrofit): AppRepository {
        val authService = retrofit.create(AuthService::class.java)
        val infoService = retrofit.create(InfoService::class.java)
        val chatService = retrofit.create(ChatService::class.java)
        val roomService = retrofit.create(RoomService::class.java)
        return AppRepository(
            authRepository = AuthRepository(authService),
            infoRepository = InfoRepository(infoService),
            chatRepository = ChatRepository(chatService),
            roomRepository = RoomRepository(roomService)
        )
    }
}
