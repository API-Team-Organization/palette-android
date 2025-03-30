package com.api.palette.di

import com.api.palette.data.auth.AuthService
import com.api.palette.data.chat.ChatService
import com.api.palette.data.info.InfoService
import com.api.palette.data.room.RoomService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {
    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideChatService(retrofit: Retrofit): ChatService =
        retrofit.create(ChatService::class.java)

    @Provides
    @Singleton
    fun provideInfoService(retrofit: Retrofit): InfoService =
        retrofit.create(InfoService::class.java)

    @Provides
    @Singleton
    fun provideRoomService(retrofit: Retrofit): RoomService =
        retrofit.create(RoomService::class.java)
}
