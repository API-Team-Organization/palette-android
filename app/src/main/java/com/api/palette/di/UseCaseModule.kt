package com.api.palette.di

import com.api.palette.domain.repository.AuthRepository
import com.api.palette.domain.repository.ChatRepository
import com.api.palette.domain.repository.InfoRepository
import com.api.palette.domain.repository.RoomRepository
import com.api.palette.domain.usecase.AuthUseCases
import com.api.palette.domain.usecase.ChatUseCases
import com.api.palette.domain.usecase.InfoUseCases
import com.api.palette.domain.usecase.RoomUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Singleton
    @Provides
    fun provideAuthUseCases(authRepository: AuthRepository): AuthUseCases =
        AuthUseCases(authRepository)

    @Singleton
    @Provides
    fun provideChatUseCases(chatRepository: ChatRepository): ChatUseCases =
        ChatUseCases(chatRepository)

    @Singleton
    @Provides
    fun provideInfoUseCases(infoRepository: InfoRepository): InfoUseCases =
        InfoUseCases(infoRepository)

    @Singleton
    @Provides
    fun provideRoomUseCases(roomRepository: RoomRepository): RoomUseCases =
        RoomUseCases(roomRepository)
}
