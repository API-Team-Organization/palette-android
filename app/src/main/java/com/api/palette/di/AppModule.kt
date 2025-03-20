package com.api.palette.di

import com.api.palette.data.repository.AuthRepositoryImpl
import com.api.palette.data.repository.ChatRepositoryImpl
import com.api.palette.data.repository.InfoRepositoryImpl
import com.api.palette.data.repository.RoomRepositoryImpl
import com.api.palette.domain.repository.AuthRepository
import com.api.palette.domain.repository.ChatRepository
import com.api.palette.domain.repository.InfoRepository
import com.api.palette.domain.repository.RoomRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideAuthRepository(): AuthRepository = AuthRepositoryImpl()

    @Singleton
    @Provides
    fun provideChatRepository(): ChatRepository = ChatRepositoryImpl()

    @Singleton
    @Provides
    fun provideInfoRepository(): InfoRepository = InfoRepositoryImpl()

    @Singleton
    @Provides
    fun provideRoomRepository(): RoomRepository = RoomRepositoryImpl()
}
