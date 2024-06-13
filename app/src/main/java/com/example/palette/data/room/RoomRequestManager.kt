package com.example.palette.data.room

import com.example.palette.data.base.BaseVoidResponse
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RoomRequestManager {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://standard.alcl.cloud:24136")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val roomService: RoomService = retrofit.create(RoomService::class.java)

    suspend fun roomRequest(token: String): Response<BaseVoidResponse> {
        val response = roomService.createRoom(token)
        if (!response.isSuccessful)
            throw HttpException(response)

        return response
    }
}