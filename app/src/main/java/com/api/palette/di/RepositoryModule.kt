package com.api.palette.di

import com.api.palette.data.auth.AuthRepositoryImpl
import com.api.palette.data.chat.ChatRepositoryImpl
import com.api.palette.data.info.InfoRepositoryImpl
import com.api.palette.data.room.RoomRepositoryImpl
import com.api.palette.domain.auth.AuthRepository
import com.api.palette.domain.chat.ChatRepository
import com.api.palette.domain.info.InfoRepository
import com.api.palette.domain.room.RoomRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindInfoRepository(impl: InfoRepositoryImpl): InfoRepository

    @Binds
    @Singleton
    abstract fun bindRoomRepository(impl: RoomRepositoryImpl): RoomRepository
}
