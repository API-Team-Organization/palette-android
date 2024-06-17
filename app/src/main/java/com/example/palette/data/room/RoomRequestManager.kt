package com.example.palette.data.room

import android.util.Log
import com.example.palette.common.Constant
import com.example.palette.data.base.BaseResponse
import com.example.palette.data.base.BaseVoidResponse
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log

object RoomRequestManager {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://standard.alcl.cloud:24136")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val roomService: RoomService = retrofit.create(RoomService::class.java)

    suspend fun roomRequest(token: String, title: TitleData): Response<BaseVoidResponse> {
        val response = roomService.createRoom(token = token, title = title)
        if (!response.isSuccessful){
            Log.d(Constant.TAG,"RoomRequestManager roomRequest response is ${response}")
            throw HttpException(response)
        }


        return response
    }

    suspend fun roomList(token: String): BaseResponse<List<RoomData>> {

        return roomService.getRoomList(token)
    }
}